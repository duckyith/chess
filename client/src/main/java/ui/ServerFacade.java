package ui;

import com.google.gson.Gson;
import exception.ResponseException;
import models.AuthData;
import models.GameData;
import models.UserData;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws ResponseException {
        var request = buildRequest("POST", "/user", userData, null);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
        return handleResponse(response, AuthData.class);
    }

    public AuthData login(UserData userData) throws ResponseException {
        var request = buildRequest("POST", "/session", userData, null);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
        return handleResponse(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
    }

    public Games list(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
        return handleResponse(response, Games.class);
    }

    public void create(String gameName, String authToken) throws ResponseException {
        GameData gameData = new GameData(1000,null,null,gameName,null);
        var request = buildRequest("POST", "/game", gameData, authToken);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
    }

    public void join(UserData userData, String authToken) throws ResponseException {
        var request = buildRequest("PUT", "/game", userData, authToken);
        var response = sendRequest(request);
        if (response.statusCode() != 200) {
            throw new ResponseException(ResponseException.Code.ClientError, response.body());
        }
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.setHeader("Authorization", authToken);
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
