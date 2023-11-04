import Frame.*;
import javax.swing.*;

public class Main {
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            JDBCFrame gui = new JDBCFrame();
            gui.repaint();
        });
    }
}