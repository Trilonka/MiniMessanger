package MiniMessanger.src;

import java.io.*;
import java.net.Socket;

/**
 * This is the connection class that the client uses to communicate with the server
 * Written 31.01.2020
 * @author Trilonka
 * @version 0.1.0
 */
public class Connection {

    private final Socket socket;
    private final ConnectionEvents events;
    private final BufferedReader socketIn;
    private final BufferedWriter socketOut;
    private Thread readerThread;

    /**
     * initialization all params and start readerThread
     * @param socket
     * @param events
     * @throws IOException
     */
    public Connection(Socket socket, ConnectionEvents events) throws IOException {
        this.socket = socket;
        this.events = events;
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        readerThread = new Thread(()-> {
            events.onConnect(Connection.this);
            try {
                while(!readerThread.isInterrupted()) {
                    events.onReceivedMessage(socketIn.readLine());
                }
            } catch (IOException e) {
                events.onException(Connection.this, e);
            } finally {
                events.onDisconnect(Connection.this);
            }
        });
        readerThread.start();
    }

    /**
     * Record messages in SocketBuffer
     * @param message
     */
    public synchronized void sendMessage(String message) {
        try {
            socketOut.write(message + "\n");
            socketOut.flush();
        } catch (IOException e) {
            events.onException(this, e);
        }
    }

    /**
     * Disconnecting from the connection
     */
    public synchronized void disconnect() {
        readerThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            events.onException(this, e);
        }
    }

    @Override
    public String toString() {
        return socket.getInetAddress() + ": " + socket.getPort();
    }
}