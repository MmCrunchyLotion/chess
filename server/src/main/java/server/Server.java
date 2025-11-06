package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import chess.ChessGame;
import io.javalin.*;
import io.javalin.http.Context;
import services.*;
import models.*;

import java.util.Collection;

public class Server {

//    private final ChessService service;
    private final Javalin javalin;

//    public Server(ChessService service) {
//        this.service = service;
    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::addUser)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::newGame)
                .put("/game", this::joinGame)
                .delete("/db", this::clear)
                .exception(ResponseException.class, this::exceptionHandler);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.toHttpStatusCode());
        ctx.result(ex.toJson());
    }

    private void addUser(Context ctx) throws ResponseException, DataAccessException {
//        I may need to have the result passed through an exception handler
        UserData givenUser = new Gson().fromJson(ctx.body(), UserData.class);
        RegisterUser registerRequest = new RegisterUser(givenUser);
        String message = registerRequest.toString();
        ctx.result(message);
    }

    private void login(Context ctx) throws ResponseException, DataAccessException {
        UserData givenUser = new Gson().fromJson(ctx.body(), UserData.class);
        Login loginRequest = new Login(givenUser);
        String message = loginRequest.toString();
        ctx.result(message);
    }

    private void logout(Context ctx) throws ResponseException, DataAccessException {
        AuthData auth = new Gson().fromJson(ctx.header("Authorization"), AuthData.class);
        Logout logoutRequest = new Logout(auth);
        logoutRequest.logout();
//        String message = null;
//        ctx.result(message);
    }

    private void listGames(Context ctx) throws ResponseException, DataAccessException {
        AuthData auth = new Gson().fromJson(ctx.header("Authorization"), AuthData.class);
        ListGamesService listGamesService = new ListGamesService(auth);
        Collection<GameData> games = listGamesService.list();
        ctx.result(games.toString());
    }

    private void newGame(Context ctx) throws ResponseException, DataAccessException {
        AuthData auth = new Gson().fromJson(ctx.header("Authorization"), AuthData.class);
        GameData game = new Gson().fromJson(ctx.body(), GameData.class);
        CreateGameService createGameService = new CreateGameService(auth, game);
        createGameService.addGame();
        ctx.result(game.toString()); // This may need to be changed to return only the gameID
    }

    private void joinGame(Context ctx) throws ResponseException, DataAccessException {
        AuthData auth = new Gson().fromJson(ctx.header("Authorization"), AuthData.class);
        GameData game = new Gson().fromJson(ctx.body(), GameData.class); // TODO: Figure out how to get the PlayerColor
        JoinGameService joinGameService = new JoinGameService(auth, game);
        joinGameService.addPlayer(game, auth.getUsername(), playerColor);
    }

    private void clear(Context ctx) throws ResponseException, DataAccessException {
        ClearDBService clearDBService = new ClearDBService();

    }
}
