package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class ConnectionManager {

    private final ConcurrentHashMap<Integer, ArrayList<Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session) {
        ArrayList<Connection> gameConnections = connections.getOrDefault(gameID, new ArrayList<>());
        gameConnections.removeIf(c -> c.username.equals(username));
        gameConnections.add(new Connection(username, session));
        connections.put(gameID, gameConnections);
    }

    public void remove(int gameID, String username) {
        ArrayList<Connection> gameConnections = connections.get(gameID);
        if (gameConnections != null) {
            gameConnections.removeIf(c -> c.username.equals(username));
        }
    }

    public void sendToUser(int gameID, String username, ServerMessage message) throws IOException {
        ArrayList<Connection> gameConnections = connections.get(gameID);
        if (gameConnections == null) return;
        String json = new Gson().toJson(message);
        for (Connection c : gameConnections) {
            if (c.username.equals(username) && c.session.isOpen()) {
                c.session.getRemote().sendString(json);
                return;
            }
        }
    }

    public void broadcast(int gameID, String excludeUsername, ServerMessage message) throws IOException {
        ArrayList<Connection> gameConnections = connections.get(gameID);
        if (gameConnections == null) return;
        String json = new Gson().toJson(message);
        for (Connection c : new ArrayList<>(gameConnections)) {
            if (!c.username.equals(excludeUsername) && c.session.isOpen()) {
                c.session.getRemote().sendString(json);
            }
        }
    }

    public void broadcastAll(int gameID, ServerMessage message) throws IOException {
        broadcast(gameID, "", message);
    }

    public static class Connection {
        public String username;
        public Session session;

        public Connection(String username, Session session) {
            this.username = username;
            this.session = session;
        }
    }
}