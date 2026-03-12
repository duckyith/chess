package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import models.AuthData;
import models.GameData;
import models.JoinData;
import models.UserData;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.Service;
import service.UnauthorizedException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private final Service service;
    private final Gson gson = new Gson();
    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MYSQLUserDAO();
        service = new Service(userDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.delete("/db", this::clear);
        javalin.post("/game", this::create);
        javalin.get("/game", this::list);
        javalin.put("/game", this::join);
        javalin.exception(AlreadyTakenException.class, this::takenException);
        javalin.exception(BadRequestException.class, this::badException);
        javalin.exception(UnauthorizedException.class, this::unauthorizedException);

        javalin.exception(Exception.class, (error, ctx) -> {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Internal Server Error: " + error.getMessage())));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public void register(Context context)
            throws DataAccessException, BadRequestException, AlreadyTakenException, SQLException {
        UserData userData = gson.fromJson(context.body(), UserData.class);
        AuthData data = service.register(userData);
        context.result(gson.toJson(data));
        context.status(200);
    }

    public void login(Context context)
            throws DataAccessException, BadRequestException, UnauthorizedException, SQLException {
        UserData userData = gson.fromJson(context.body(), UserData.class);
        AuthData data = service.login(userData);
        context.result(gson.toJson(data));
        context.status(200);
    }

    public void logout(Context context)
            throws DataAccessException, UnauthorizedException, SQLException {
        service.logout(context.header("Authorization"));
        context.status(200);
    }

    public void clear(Context context) throws SQLException, DataAccessException {
        service.clear();
        context.status(200);
    }

    public void create(Context context)
            throws UnauthorizedException, BadRequestException {
        GameData game = gson.fromJson(context.body(), GameData.class);
        GameData gameID = service.create(context.header("Authorization"),game);
        context.result(gson.toJson(gameID));
        context.status(200);
    }

    public void list(Context context)
            throws UnauthorizedException  {
        ArrayList<GameData> games = new ArrayList<>(service.list(context.header("Authorization")));
        Map<String, Object> response = new HashMap<>();
        response.put("games", games);
        context.result(gson.toJson(response));
        context.status(200);
    }

    public void join(Context context)
            throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException, SQLException {
        JoinData request = gson.fromJson(context.body(), JoinData.class);
        service.join(context.header("Authorization"),request);
        context.status(200);
    }

    public void takenException(AlreadyTakenException error, Context context) {
        context.result(gson.toJson(Map.of("message", error.getMessage())));
        context.status(403);
    }

    public void unauthorizedException(UnauthorizedException error, Context context) {
        context.result(gson.toJson(Map.of("message", error.getMessage())));
        context.status(401);
    }

    public void badException(BadRequestException error, Context context) {
        context.result(gson.toJson(Map.of("message", error.getMessage())));
        context.status(400);
    }
}
