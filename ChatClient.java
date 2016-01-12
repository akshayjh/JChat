package chatting;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.net.Socket;
import javax.swing.text.*;

public class ChatClient extends JFrame implements ActionListener {

    private String screenName;

    // GUI stuff
    private JTextArea  enteredText = new JTextArea(10, 32);
    private JTextField typedText   = new JTextField(32);

    // socket for connection to chat server
    private Socket socket;

    // for writing to and reading from the server
    private Out out;
    private In in;

    public ChatClient(String screenName, String hostName) {

        // connect to server
        try {
            socket = new Socket(hostName, 4444);
            out    = new Out(socket);
            in     = new In(socket);
        }
        catch (Exception ex) { ex.printStackTrace(); }
        this.screenName = screenName;

        // close output stream  - this will cause listen() to stop and exit
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    out.close();
                }
            }
        );


        // create GUI stuff
        enteredText.setEditable(false);
        Color colorback=new Color(159, 255, 159);
        enteredText.setBackground(colorback);
        typedText.addActionListener(this);

        Container content = getContentPane();
        content.add(new JScrollPane(enteredText), BorderLayout.CENTER);
        content.add(typedText, BorderLayout.SOUTH);


        // display the window, with focus on typing box
        setTitle("JChatv1.0: [" + screenName + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        typedText.requestFocusInWindow();
        setVisible(true);

        enteredText .setFont(new Font("Comic Sans MS",Font.PLAIN,20));
        Color colortext=new Color(0, 128, 0);
        enteredText .setForeground(colortext);

    }

    // process TextField after user hits Enter
    public void actionPerformed(ActionEvent e) {
        out.println("[" + screenName + "]: " + typedText.getText());
        typedText.setText("");
        typedText.requestFocusInWindow();
    }

    // listen to socket and print everything that server broadcasts
    public void listen() {
        String s;
        while ((s = in.readLine()) != null) {
            enteredText.insert(s + "\n", enteredText.getText().length());
            enteredText.setCaretPosition(enteredText.getText().length());
        }
        out.close();
        in.close();
        try                 { socket.close();      }
        catch (Exception e) { e.printStackTrace(); }
        System.err.println("Closed client socket");
    }

    public static void main(String[] args)  {
        ChatClient client = new ChatClient(args[0], args[1]);
        client.listen();
    }
}