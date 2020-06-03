import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class server extends JFrame{

        private JTextField userText;
        private JTextArea chatWindow;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private Socket connection;
        private ServerSocket server;

        public server(){

            super("Instant Messenger");

            userText = new JTextField("Enter the message! ");
            userText.setEditable(false);
            userText.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent event){
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
            );
            add(userText, BorderLayout.NORTH);

            chatWindow = new JTextArea();
            add(new JScrollPane(chatWindow) , BorderLayout.CENTER);

            setSize(500,370);
            setVisible(true);
        }

        public void startRunning(){
           try{
               server = new ServerSocket(1234 ,10);
               while(true){
                   try{
                       waitingForConnection();
                       setupStream();
                       whileChatting();
                   }catch (EOFException eof){
                       System.out.println("\n connection ended! ");
                   }finally{
                       closeApp();
                   }

            }
           }catch (IOException e){
               e.printStackTrace();
           }
        }

        private void waitingForConnection() throws IOException{
            showMessage("Waiting for connection! ");
            connection = server.accept();
            showMessage("\n connected to "+ connection.getInetAddress().getHostName());
        }

        private void setupStream() throws IOException{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            showMessage("\n streams setup successful \n");
        }

        private void whileChatting()throws IOException{
            String message ="u r connected now! ";
            showMessage(message);
            ableToType(true);

            do{
                try{
                    message = (String)input.readObject();
                    showMessage("\n" + message);

                }catch (ClassNotFoundException cne){
                    showMessage("\nunidentified char revieved ");
                }

            }while(message != "CLIENT - END");
        }

        private void closeApp(){
            showMessage("\n closing the connections ");
            ableToType(false);
            try{
                output.close();
                input.close();
                connection.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

        private void sendMessage(String message){
            try{
                output.writeObject("SERVER - " + message);
                output.flush();
                showMessage("\nSERVER - " + message);
            }catch(IOException e){
                showMessage("\n cant send the message");
            }
        }

        private void showMessage(String text){
            SwingUtilities.invokeLater(
                    new Runnable() {
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
