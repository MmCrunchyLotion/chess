package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketFacade {

    private Session session;
    private final Gson gson = new Gson();
    private final String serverUrl;
    private MessageHandler messageHandler;

    public interface MessageHandler {
        void onLoadGame(ChessGame game);
        void onNotification(String message);
        void onError(String errorMessage);
    }

    public WebSocketFacade(String serverUrl, MessageHandler messageHandler) throws ResponseException {
        this.serverUrl = serverUrl;
        this.messageHandler = messageHandler;
        try {
            URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.setDefaultMaxSessionIdleTimeout(5 * 60 * 1000);
            this.session = container.connectToServer(this, uri);
        } catch (Exception e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> messageHandler.onLoadGame(serverMessage.getGame());
            case NOTIFICATION -> messageHandler.onNotification(serverMessage.getMessage());
            case ERROR -> messageHandler.onError(serverMessage.getErrorMessage());
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        if (session == null || !session.isOpen()) {
            throw new ResponseException(
                    ResponseException.Code.ServerError,
                    "WebSocket is not connected."
            );
        }
        try {
            session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void connect(String authToken, int gameID) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move));
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }

    public void close() throws ResponseException {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }
}