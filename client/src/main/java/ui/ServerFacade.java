package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import models.AuthData;
import models.GameData;
import models.UserData;

import java.lang.reflect.Array;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws ResponseException {
        var request = buildRequest("POST", "/user", userData);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData userData) throws ResponseException {
        var request = buildRequest("POST", "/session", userData);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
        return handleResponse(response, AuthData.class);
    }

    public void logout(UserData userData) throws ResponseException {
        var request = buildRequest("DELETE", "/session", userData);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
    }

    public Games list(UserData userData) throws ResponseException {
        var request = buildRequest("GET", "/game", userData);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
        return handleResponse(response, Games.class);
    }

    public int create(UserData userData) throws ResponseException {
        var request = buildRequest("POST", "/game", userData);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
        return handleResponse(response, int.class);
    }

    public void join(UserData userData) throws ResponseException {
        var request = buildRequest("PUT", "/game", userData);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
    }

    public void clear(UserData userData) throws ResponseException {
        var request = buildRequest("DELETE", "/db", userData);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
