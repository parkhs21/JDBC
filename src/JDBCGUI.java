import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JDBCGUI extends JFrame implements ActionListener {

    private String[] searchItems = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
    private JCheckBox[] searchCheckBoxes = new JCheckBox[searchItems.length];

    private String[][] temp = {};

    private JTextField inputMsg;

    public JDBCGUI() {
        setTitle("JDBC GUI");
        setSize(1000, 800);
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        inputMsg = new JTextField(10);

        JButton sendBtn = new JButton("전송");
        sendBtn.setActionCommand("send");
        sendBtn.addActionListener(this);

        JButton deleteBtn = new JButton("삭제");
        deleteBtn.setActionCommand("delete");
        deleteBtn.addActionListener(this);

        JPanel panel2 = new JPanel();

        JLabel searchItemLabel = new JLabel("검색 항목");
        panel2.add(searchItemLabel);

        for (int i = 0; i < searchItems.length; i++) {
            searchCheckBoxes[i] = new JCheckBox(searchItems[i], true);
            panel2.add(searchCheckBoxes[i]);
        }

        JButton searchBtn = new JButton("검색");
        panel2.add(searchBtn);

//        JTable table = new JTable(, searchItems);
//        JScrollPane scrollPane = new JScrollPane(table);


        add(panel2);
//        add(scrollPane);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("send")) {
            String msg = inputMsg.getText();
            JOptionPane.showMessageDialog(this, msg);
        } else if (cmd.equals("delete")) {
            inputMsg.setText("");
        }
    }

    public static void main(String args[]) {
        JDBCGUI gui = new JDBCGUI();
    }
}