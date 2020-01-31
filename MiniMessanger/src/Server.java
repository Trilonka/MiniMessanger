package MiniMessanger.src;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This is server for connecting clients
 * This class implements ConnectionEvents
 * Written 31.01.2020
 * @author Trilonka
 * @version 0.1.0
 */
public class Server implements ConnectionEvents {

    private Connection serverConnection;
    private ArrayList<Connection> listOfConnections = new ArrayList<>();

    /**
     * Initialization Server and start socket-waiter
     */
    private Server() {
        try(ServerSocket serverSocket = new ServerSocket(6565)) {
            while (true) {
                serverConnection = new Connection(serverSocket.accept(), this);
            }
        } catch (IOException e) {
            onException(Objects.requireNonNull(serverConnection), e);
        }
    }

    /**
     * Start Server
     * @param args
     */
    public static void main(String[] args) {
        new Server();
    }

    /**
     * Send received message everyone client
     * @param message
     */
    private void sendEveryone(String message) {
        for (Connection c : listOfConnections) {
            c.sendMessage(message);
        }
    }

    @Override
    public void onConnect(Connection connection) {
        listOfConnections.add(connection);
        sendEveryone(connection + " connected.");
    }

    @Override
    public void onDisconnect(Connection connection) {
        listOfConnections.remove(connection);
        sendEveryone(connection + " disconnected.");
    }

    @Override
    public void onReceivedMessage(String message) {
        sendEveryone(message);
    }

    @Override
    public void onException(Connection connection, Exception e) {
        System.out.println(connection + " " + e.getMessage());
        connection.disconnect();
    }
}