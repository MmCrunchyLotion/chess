package client;

import com.google.gson.Gson;
import exception.ResponseException;
import models.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(UserData user) throws ResponseException {
        return makeRequest("POST", "/user", user, AuthData.class);
    }

    public AuthData login(UserData user) throws ResponseException {
        return makeRequest("POST", "/session", user, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", null, null, authToken);
    }

    public GameData createGame(String gameName, String authToken) throws ResponseException {
        return makeRequest("POST", "/game", new GameData(gameName), GameData.class, authToken);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        record ListGamesResponse(GameData[] games) {}
        return makeRequest("GET", "/game", null, ListGamesResponse.class, authToken).games();
    }

    public void joinGame(String playerColor, int gameID, String authToken) throws ResponseException {
        makeRequest("PUT", "/game", new JoinBody(playerColor, gameID), null, authToken);
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        return makeRequest(method, path, request, responseClass, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setRequestProperty("Content-Type", "application/json");
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }
            if (request != null) {
                http.setDoOutput(true);
                try (OutputStream os = http.getOutputStream();
                     Writer writer = new OutputStreamWriter(os)) {
                    gson.toJson(request, writer);
                }
            }
            http.connect();
            if (http.getResponseCode() / 100 == 2) {
                if (responseClass == null) {
                    return null;
                }
                try (InputStream is = http.getInputStream();
                     Reader reader = new InputStreamReader(is)) {
                    return gson.fromJson(reader, responseClass);
                }
            } else {
            try (InputStream is = http.getErrorStream();
                 Reader reader = new InputStreamReader(is)) {
                var map = gson.fromJson(reader, java.util.Map.class);
                String message = map.get("message").toString();
                String status = map.get("status").toString();
                ResponseException.Code code = ResponseException.Code.valueOf(status);
                throw new ResponseException(code, message);
            }
        }
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
