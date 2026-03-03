package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import models.*;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("DEBUG: received message: " + message);
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            System.out.println("DEBUG: command type: " + command.getCommandType());
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command);
                case MAKE_MOVE -> handleMakeMove(session, command);
                case LEAVE -> handleLeave(session, command);
                case RESIGN -> handleResign(session, command);
            }
        } catch (Exception e) {
            System.err.println("DEBUG: exception in onMessage: " + e.getMessage());
            e.printStackTrace();
            sendError(session, "Error: " + e.getMessage());
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {
        System.out.println("DEBUG: handleConnect called for gameID: " + command.getGameID());
        try {
            AuthData auth = authDAO.getAuthByToken(command.getAuthToken());
            System.out.println("DEBUG: auth result: " + (auth != null ? auth.getUsername() : "null"));

            GameData game = gameDAO.getGame(command.getGameID());
            System.out.println("DEBUG: game result: " + (game != null ? game.getGameID() : "null"));

            if (auth == null) {
                sendError(session, "Error: invalid auth token");
                return;
            }
            if (game == null) {
                sendError(session, "Error: game not found");
                return;
            }

            String username = auth.getUsername();
            connections.add(command.getGameID(), username, session);
            System.out.println("DEBUG: added connection for " + username);

            ServerMessage loadGameMsg = ServerMessage.loadGame(game.getGame());
            System.out.println("DEBUG: sending LOAD_GAME: " + new Gson().toJson(loadGameMsg));
            connections.sendToUser(command.getGameID(), username, loadGameMsg);
            System.out.println("DEBUG: LOAD_GAME sent");

            String role = getRole(username, game);
            connections.broadcast(command.getGameID(), username,
                    ServerMessage.notification(username + " connected as " + role));
            System.out.println("DEBUG: notification broadcast done");

        } catch (DataAccessException e) {
            System.err.println("DEBUG: DataAccessException: " + e.getMessage());
            sendError(session, "Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("DEBUG: unexpected exception: " + e.getMessage());
            e.printStackTrace();
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command) throws IOException {
        try {
            AuthData auth = authDAO.getAuthByToken(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: invalid auth token");
                return;
            }
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: game not found");
                return;
            }
            String username = auth.getUsername();
            ChessGame game = gameData.getGame();

            // verify game not over
            if (game.isGameOver()) {
                sendError(session, "Error: game is already over");
                return;
            }

            // verify user is a player
            boolean isWhite = username.equals(gameData.getWhiteUsername());
            boolean isBlack = username.equals(gameData.getBlackUsername());
            if (!isWhite && !isBlack) {
                sendError(session, "Error: observers cannot make moves");
                return;
            }

            // verify user's turn
            ChessGame.TeamColor userColor = isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            if (game.getTeamTurn() != userColor) {
                sendError(session, "Error: it is not your turn");
                return;
            }

            // attempt move
            ChessMove move = command.getMove();
            game.makeMove(move);

            // check for check/checkmate/stalemate
            ChessGame.TeamColor opponent = userColor == ChessGame.TeamColor.WHITE ?
                    ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            String statusNotification = null;
            if (game.isInCheckmate(opponent)) {
                statusNotification = username + " has put " + getColorName(opponent) + " in checkmate! Game over.";
                game.setGameOver(true);
            } else if (game.isInStalemate(opponent)) {
                statusNotification = "Stalemate! Game over.";
                game.setGameOver(true);
            } else if (game.isInCheck(opponent)) {
                statusNotification = getColorName(opponent) + " is in check!";
            }

            // save game
            gameDAO.updateGame(command.getGameID(), game);

            // send LOAD_GAME to all clients
            connections.broadcastAll(command.getGameID(), ServerMessage.loadGame(game));

            // notify others of the move
            String moveDesc = describeMoveNotation(move);
            connections.broadcast(command.getGameID(), username,
                    ServerMessage.notification(username + " moved " + moveDesc));

            // send check/checkmate/stalemate notification to all
            if (statusNotification != null) {
                connections.broadcastAll(command.getGameID(),
                        ServerMessage.notification(statusNotification));
            }

        } catch (InvalidMoveException e) {
            sendError(session, "Error: invalid move - " + e.getMessage());
        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException {
        try {
            AuthData auth = authDAO.getAuthByToken(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: invalid auth token");
                return;
            }
            String username = auth.getUsername();
            GameData gameData = gameDAO.getGame(command.getGameID());

            if (gameData != null) {
                if (username.equals(gameData.getWhiteUsername())) {
                    gameDAO.clearUser("WHITE", command.getGameID());
                } else if (username.equals(gameData.getBlackUsername())) {
                    gameDAO.clearUser("BLACK", command.getGameID());
                }
            }

            connections.remove(command.getGameID(), username);
            connections.broadcast(command.getGameID(), username,
                    ServerMessage.notification(username + " left the game"));

        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void handleResign(Session session, UserGameCommand command) throws IOException {
        try {
            AuthData auth = authDAO.getAuthByToken(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: invalid auth token");
                return;
            }
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: game not found");
                return;
            }
            String username = auth.getUsername();

            boolean isPlayer = username.equals(gameData.getWhiteUsername()) ||
                    username.equals(gameData.getBlackUsername());
            if (!isPlayer) {
                sendError(session, "Error: observers cannot resign");
                return;
            }

            if (gameData.getGame().isGameOver()) {
                sendError(session, "Error: game is already over");
                return;
            }

            ChessGame game = gameData.getGame();
            game.setGameOver(true);
            gameDAO.updateGame(command.getGameID(), game);

            connections.broadcastAll(command.getGameID(),
                    ServerMessage.notification(username + " resigned. Game over."));

        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private String getRole(String username, GameData game) {
        if (username.equals(game.getWhiteUsername())) return "WHITE";
        if (username.equals(game.getBlackUsername())) return "BLACK";
        return "an observer";
    }

    private String getColorName(ChessGame.TeamColor color) {
        return color == ChessGame.TeamColor.WHITE ? "White" : "Black";
    }

    private String describeMoveNotation(ChessMove move) {
        String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String from = cols[move.getStartPosition().getColumn() - 1] + move.getStartPosition().getRow();
        String to = cols[move.getEndPosition().getColumn() - 1] + move.getEndPosition().getRow();
        return from + " to " + to;
    }

    private void sendError(Session session, String message) throws IOException {
        session.getRemote().sendString(gson.toJson(ServerMessage.error(message)));
    }
}