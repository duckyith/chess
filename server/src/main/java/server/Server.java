package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import models.AuthData;
import models.User;
import service.Service;

import java.util.Map;

public class Server {
    private final Service service;
    private final Gson gson = new Gson();
    private final Javalin javalin;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        service = new Service(userDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.delete("/db", this::clear);
        javalin.exception(AlreadyTakenException.class, this::takenException);
        javalin.exception(BadRequestException.class, this::badException);
        javalin.exception(UnauthorizedException.class, this::unauthorizedException);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public void register(Context context) throws DataAccessException, BadRequestException, AlreadyTakenException {
        User user = gson.fromJson(context.body(), User.class);
        AuthData data = service.register(user);
        context.result(gson.toJson(data));
        context.status(200);
    }

    public void login(Context context) throws DataAccessException, BadRequestException, UnauthorizedException {
        User user = gson.fromJson(context.body(), User.class);
        AuthData data = service.login(user);
        context.result(gson.toJson(data));
        context.status(200);
    }

    public void logout(Context context) throws DataAccessException, UnauthorizedException {
        service.logout(context.header("Authorization"));
        context.status(200);
    }

    public void clear(Context context) {
        service.clear();
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
