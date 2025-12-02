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

    private UserDAO mockUserDAO;
    private AuthDAO mockAuthDAO;
    private GameDAO mockGameDAO;

//    public Server(ChessService service) {
//        this.service = service;
    public Server() {

        this.mockUserDAO = new UserDAO();
        this.mockAuthDAO = new AuthDAO();
        this.mockGameDAO = new GameDAO();

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
        try {
            RegisterUserService registerRequest = new RegisterUserService(givenUser, mockUserDAO, mockAuthDAO);
            registerRequest.register();
            String message = registerRequest.getAuth().toString();
            ctx.result(message);
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void login(Context ctx) {
        UserData givenUser = new Gson().fromJson(ctx.body(), UserData.class);
        try {
            LoginService loginRequest = new LoginService(givenUser, mockUserDAO, mockAuthDAO);
            loginRequest.login(); // TODO: May need to make it so that a browser only allows for one user to be logged in at a time, but server allows for multiple users to be logged in simultaneously
            String message = loginRequest.getAuth().toString();
            ctx.result(message);
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void logout(Context ctx) {
        AuthData auth = new AuthData("", ctx.header("Authorization"));
        try {
            LogoutService logoutRequest = new LogoutService(auth, mockAuthDAO);
            logoutRequest.logout();
            String message = "";
            ctx.result(message);
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void listGames(Context ctx) {
        AuthData auth = new AuthData("", ctx.header("Authorization"));
        try {
            ListGamesService listGamesRequest = new ListGamesService(auth, mockAuthDAO, mockGameDAO);
            Collection<GameData> games = listGamesRequest.list();
            ctx.result(games.toString());
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void createGame(Context ctx) {
        AuthData auth = new AuthData("", ctx.header("Authorization"));
        GameData game = new Gson().fromJson(ctx.body(), GameData.class);
        try {
            CreateGameService createGameRequest = new CreateGameService(auth, game, mockAuthDAO, mockGameDAO);
            createGameRequest.addGame();
            ctx.result(game.toString());
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void joinGame(Context ctx) {
        AuthData auth = new AuthData("", ctx.header("Authorization"));
        JoinBody join = new Gson().fromJson(ctx.body(), JoinBody.class);
        try {
            JoinGameService joinGameRequest = new JoinGameService(auth, join, mockAuthDAO, mockGameDAO);
            joinGameRequest.addPlayer(auth.getUsername(), mockGameDAO);
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void clear(Context ctx) {
        ClearDBService clearDBRequest = new ClearDBService(mockAuthDAO, mockUserDAO, mockGameDAO);
    }
}
