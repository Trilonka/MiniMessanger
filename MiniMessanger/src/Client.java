package MiniMessanger.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

/**
 * This is client's window for send message with textArea for show all messages,
 * class is listener for input field and implement ConnectionEvents
 * Written 31.01.2020
 * @author Trilonka
 * @version 0.1.0
 */
public class Client extends JFrame implements ConnectionEvents, ActionListener {

    private Connection clientConnection;
    private final JTextArea area = new JTextArea();
    private final JTextField userNameInput = new JTextField("Sasha");
    private final JTextField userMessageInput = new JTextField();

    /**
     * Create user window and start connection
     */
    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600,500);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        area.setEditable(false);
        area.setLineWrap(true);
        userMessageInput.addActionListener(this);

        add(area, BorderLayout.CENTER);
        add(userNameInput, BorderLayout.NORTH);
        add(userMessageInput, BorderLayout.SOUTH);

        setVisible(true);

        try {
            clientConnection = new Connection(new Socket("localhost", 6565), this);
        } catch (IOException e) {
            onException(clientConnection, e);
        }
    }

    /**
     * Start Client
     * @param args
     */
    public static void main(String[] args) {
        new Client();
    }

    /**
     * Print messages in window textArea
     * return if message equals "null"
     * @param message
     */
    void printMessage(String message) {
        if (message.equals("null")) return;
        area.append(message + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    @Override
    public void onConnect(Connection connection) {
        printMessage("Connection " + connection + " ready.");
    }

    @Override
    public void onDisconnect(Connection connection) {
        printMessage("Connection " + connection + " closed.");
    }

    @Override
    public void onReceivedMessage(String message) {
        printMessage(message);
    }

    @Override
    public void onException(Connection connection, Exception e) {
        printMessage(connection + " " + e.getMessage());
        connection.disconnect();
    }

    /**
     * Listener for userMessageInput
     * return if void
     * @param actionEvent
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String message = userMessageInput.getText();
        if (message.equals("")) return;
        userMessageInput.setText("");
        clientConnection.sendMessage(userNameInput.getText() + ": " + message);
    }
}