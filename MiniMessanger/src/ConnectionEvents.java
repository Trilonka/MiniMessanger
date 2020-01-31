public interface ConnectionEvents {
    void onConnect(Connection connection);
    void onDisconnect(Connection connection);
    void onReceivedMessage(String message);
    void onException(Connection connection, Exception e);
}