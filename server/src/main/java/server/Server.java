package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import chess.ChessGame;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import models.*;

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
        UserData givenUser = new Gson().fromJson(ctx.body(), UserData.class);
        NewUserService newUser = new NewUserService(givenUser);
        String message = newUser.toString();
        ctx.result(message);
    }

    private void login(Context ctx) {

    }

    private void logout(Context ctx) {

    }

    private void listGames(Context ctx) {

    }

    private void newGame(Context ctx) {
        ChessGame game = new Gson().fromJson(ctx.body(), ChessGame.class);

    }

    private void clear(Context ctx) {

    }
}
