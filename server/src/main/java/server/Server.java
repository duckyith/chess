package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import models.AuthData;
import models.User;
import org.jetbrains.annotations.NotNull;
import service.Service;

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
        javalin.exception(AlreadyTakenException.class, this::takenException);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public void register(Context context) throws DataAccessException, AlreadyTakenException {
        User user = gson.fromJson(context.body(), User.class);
        AuthData data = service.register(user);
        context.result(gson.toJson(data));
        context.status(200);
    }

    public void takenException(AlreadyTakenException error, Context context) {
        context.result(gson.toJson(error.getMessage()));
        context.status(403);
    }
}
