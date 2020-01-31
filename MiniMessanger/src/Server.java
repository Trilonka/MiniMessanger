import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Objects;

class Server implements ConnectionEvents {
    private Connection serverConnection;
    private ArrayList<Connection> listOfConnections = new ArrayList<>();

    private Server() {
        try(ServerSocket serverSocket = new ServerSocket(6565)) {
            while (true) {
                serverConnection = new Connection(serverSocket.accept(), this);
            }
        } catch (IOException e) {
            onException(Objects.requireNonNull(serverConnection), e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }

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