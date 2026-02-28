package server;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import java.util.Collection;
import java.util.Map;
import models.*;
import services.*;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public Server() {
        try {
            this.userDAO = new UserDAO();
            this.authDAO = new AuthDAO();
            this.gameDAO = new GameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize DAOs: " + e.getMessage());
        }

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
            RegisterUserService registerRequest = new RegisterUserService(givenUser, userDAO, authDAO);
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
            LoginService loginRequest = new LoginService(givenUser, userDAO, authDAO);
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
            LogoutService logoutRequest = new LogoutService(auth, authDAO);
            logoutRequest.logout();
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void listGames(Context ctx) {
        AuthData auth = new AuthData("", ctx.header("Authorization"));
        try {
            ListGamesService listGamesRequest = new ListGamesService(auth, authDAO, gameDAO);
            Collection<GameData> games = listGamesRequest.list();
            String message = new Gson().toJson(Map.of("games", games));
            ctx.result(message);
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void createGame(Context ctx) {
        AuthData auth = new AuthData("", ctx.header("Authorization"));
        GameData gameBody = new Gson().fromJson(ctx.body(), GameData.class);
        try {
            CreateGameService createGameRequest = new CreateGameService(auth, gameBody, authDAO, gameDAO);
            GameData newGame = createGameRequest.addGame();
            String message = new Gson().toJson(Map.of("gameID", newGame.getGameID()));
            ctx.result(message);
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void joinGame(Context ctx) {
        AuthData auth = new AuthData("", ctx.header("Authorization"));
        JoinBody join = new Gson().fromJson(ctx.body(), JoinBody.class);
        try {
            JoinGameService joinGameRequest = new JoinGameService(auth, join, authDAO, gameDAO);
            joinGameRequest.addPlayer(gameDAO);
        } catch (ResponseException ex) {
            exceptionHandler(ex, ctx);
        }
    }

    private void clear(Context ctx) {
        new ClearDBService(authDAO, userDAO, gameDAO);
    }
}
