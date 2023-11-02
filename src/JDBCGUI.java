import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

public class JDBCGUI extends JFrame {

    private String[] searchItems = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
    private JCheckBox[] searchCheckBoxes = new JCheckBox[searchItems.length];

    private String[][] temp = {};


    public JDBCGUI() {
        setTitle("JDBC GUI");
        setSize(1000, 800);
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel panel2 = new JPanel();

        JLabel searchItemLabel = new JLabel("검색 항목");
        panel2.add(searchItemLabel);

        for (int i = 0; i < searchItems.length; i++) {
            searchCheckBoxes[i] = new JCheckBox(searchItems[i], true);
            panel2.add(searchCheckBoxes[i]);
        }

        JButton searchBtn = new JButton("검색");
        panel2.add(searchBtn);



        JPanel panel3 = new JPanel();
        DefaultTableModel dft = new DefaultTableModel(searchItems, 0);
        JTable table = new JTable(dft);
        JScrollPane scrollPane = new JScrollPane(table);

        DAO dao = DAO.sharedInstance();

        List<EMPLOYEE> list = dao.getList();
        for (EMPLOYEE e : list) {
            dft.addRow(e.toArray());
//            System.out.println(Arrays.toString(e.toArray()));
        }



        panel3.add(scrollPane);


        add(panel2);
        add(panel3);

        setVisible(true);
    }

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        String cmd = e.getActionCommand();
//        if (cmd.equals("send")) {
//            String msg = inputMsg.getText();
//            JOptionPane.showMessageDialog(this, msg);
//        } else if (cmd.equals("delete")) {
//            inputMsg.setText("");
//        }
//    }

    public static void main(String args[]) {
        JDBCGUI gui = new JDBCGUI();
    }
}