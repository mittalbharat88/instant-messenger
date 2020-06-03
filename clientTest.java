import javax.swing.JFrame;

public class clientTest {
    public static void main(String[] args){
        client co = new client("127.0.0.1");
        co.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        co.startRunning();
    }
}
