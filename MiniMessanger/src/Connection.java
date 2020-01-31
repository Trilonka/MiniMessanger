import java.io.*;
import java.net.Socket;

class Connection {
    private Socket socket;
    private ConnectionEvents events;
    private BufferedReader socketIn;
    private BufferedWriter socketOut;
    private Thread readerThread;

    /**
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

    public synchronized void sendMessage(String message) {
        try {
            socketOut.write(message + "\n");
            socketOut.flush();
        } catch (IOException e) {
            events.onException(this, e);
        }
    }

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