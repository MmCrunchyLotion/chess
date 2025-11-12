package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.*;
import exception.ResponseException;
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
                .post("/user", this::registerUser)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
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

    private void registerUser(Context ctx) {
        UserData givenUser = new Gson().fromJson(ctx.body(), UserData.class);
        UserDAO mockUserDAO = new UserDAO();
        AuthDAO mockAuthDAO = new AuthDAO();
        RegisterUserService registerRequest = new RegisterUserService(givenUser, mockUserDAO, mockAuthDAO);
        try {
            registerRequest.register();
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
        String message = registerRequest.toString();
        ctx.result(message);
    }

    private void login(Context ctx) throws ResponseException {
        UserData givenUser = new Gson().fromJson(ctx.body(), UserData.class);
        UserDAO mockUserDAO = new UserDAO();
        AuthDAO mockAuthDAO = new AuthDAO();
        LoginService loginRequest = new LoginService(givenUser, mockUserDAO, mockAuthDAO);
        loginRequest.login();
        String message = loginRequest.toString();
        ctx.result(message);
    }

    private void logout(Context ctx) throws ResponseException {
        AuthData auth = new Gson().fromJson(ctx.header("Authorization"), AuthData.class);
        AuthDAO mockAuthDAO = new AuthDAO();
        LogoutService logoutRequest = new LogoutService(auth, mockAuthDAO);
        logoutRequest.logout();
//        String message = null;
//        ctx.result(message);
    }

    private void listGames(Context ctx) throws ResponseException {
        AuthData auth = new Gson().fromJson(ctx.header("Authorization"), AuthData.class);
        AuthDAO mockAuthDAO = new AuthDAO();
        GameDAO mockGameDAO = new GameDAO();
        ListGamesService listGamesRequest = new ListGamesService(auth, mockAuthDAO, mockGameDAO);
        Collection<GameData> games = listGamesRequest.list();
        ctx.result(games.toString());
    }

    private void createGame(Context ctx) throws ResponseException {
        AuthData auth = new Gson().fromJson(ctx.header("Authorization"), AuthData.class);
        GameData game = new Gson().fromJson(ctx.body(), GameData.class);
        AuthDAO mockAuthDAO = new AuthDAO();
        GameDAO mockGameDAO = new GameDAO();
        CreateGameService createGameRequest = new CreateGameService(auth, game, mockAuthDAO, mockGameDAO);
        createGameRequest.addGame();
        ctx.result(game.toString()); // This may need to be changed to return only the gameID
    }

    private void joinGame(Context ctx) throws ResponseException, DataAccessException {
        AuthData auth = new Gson().fromJson(ctx.header("Authorization"), AuthData.class);
        JoinBody join = new Gson().fromJson(ctx.body(), JoinBody.class);
        AuthDAO mockAuthDAO = new AuthDAO();
        GameDAO mockGameDAO = new GameDAO();
        JoinGameService joinGameRequest = new JoinGameService(auth, join, mockAuthDAO, mockGameDAO);
        joinGameRequest.addPlayer(auth.getUsername(), mockGameDAO);
    }

    private void clear(Context ctx) throws ResponseException, DataAccessException {
        UserDAO mockUserDAO = new UserDAO();
        AuthDAO mockAuthDAO = new AuthDAO();
        GameDAO mockGameDAO = new GameDAO();
        ClearDBService clearDBRequest = new ClearDBService(mockAuthDAO, mockUserDAO, mockGameDAO);
    }
}
