import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

class Client extends JFrame implements ConnectionEvents, ActionListener {
    private Connection clientConnection;
    private JTextArea area = new JTextArea();
    private JTextField userNameInput = new JTextField("Sasha");
    private JTextField userMessageInput = new JTextField();

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

    public static void main(String[] args) {
        new Client();
    }

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

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String message = userMessageInput.getText();
        if (message.equals("")) return;
        userMessageInput.setText("");
        clientConnection.sendMessage(userNameInput.getText() + ": " + message);
    }
}