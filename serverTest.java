import javax.swing.JFrame;

public class serverTest {
    public static void main(String[] args){
        server so = new server();
        so.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        so.startRunning();
    }
}
