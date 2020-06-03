import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class client extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String serverIP ;
    private Socket connection;
    private String message="";

    public client(String host){
        super("client application");
        serverIP =host;
        userText= new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent event){
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText ,BorderLayout.NORTH);

        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow) , BorderLayout.CENTER);

        setSize(450 ,350);
        setVisible(true);
    }

    public void startRunning(){

            try {
                connectServer();
                setupStreams();
                whileChatting();
            } catch (EOFException eof) {
                showMessage("\n Connection ended! ");
            } catch (IOException io) {
                io.printStackTrace();
            } finally {
                closeApplication();
            }
    }

    private void connectServer() throws IOException{
        showMessage("Attempting to set up connection");
        connection = new Socket(InetAddress.getByName(serverIP), 1234);
        showMessage("\nConnected to: "+ connection.getInetAddress().getHostName());
    }
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Stream setup Complete");
    }

    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try {
                message = (String) input.readObject();
                showMessage("\n"+ message);
            }catch (ClassNotFoundException clnf){
                showMessage("Scanned invalid text");
            }
        }while(!message.equals("SERVER - END"));
    }
    private void closeApplication(){
        showMessage("Closing the connection! ");
        ableToType(false);
       try {
           output.close();
           input.close();
           connection.close();
       }catch(IOException ioe){
           ioe.printStackTrace();
       }
    }
    private void sendMessage(String text) {
        try {
            output.writeObject("CLIENT - " + text);
            output.flush();
            showMessage("\nCLIENT - " + text);
        } catch (IOException io) {
           chatWindow.append("\nsomething went wrong while sending the message! ");
        }
    }

    private void showMessage(String text){
        SwingUtilities.invokeLater(
                new Runnable(){
                    @Override
                    public void run() {
                        chatWindow.append(text);
                    }
                }

        );
    }

    private void ableToType(boolean tof){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        userText.setEditable(tof);
                    }
                }
        );
    }



}