package Dialog;

import Data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 직원 추가하는 화면
public class InsertDialog extends JDialog implements ActionListener {

    private JFrame parentFrame = null;
    private final JTextField[] textFields = new JTextField[10];
    private JRadioButton[] sexRadioButtons = new JRadioButton[2];
    private JComboBox departmentComboBox;

    private final DAO dao = DAO.sharedInstance();

    // 부모 Frame을 받아와서 값 변경 후, reload 시켜줌.
    // 각 항목을 textField를 통해 받아옴.
    // 성별은 RadioButton으로, 부서는 데이터베이스에서 목록을 받아서 ComboBox로 값을 받아옴.
    public InsertDialog(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setTitle("Insert GUI");
        setSize(400, 500);
        setLocation(150, 150);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        String[] label = {"Fname", "Minit", "Lname", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};

        for (int i = 0; i < 10; i++) {
            JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel itemLabel = new JLabel(label[i] + " : ");
            itemLabel.setPreferredSize(new Dimension(100, 20));
            temp.add(itemLabel);

            if (i == 6) {
                sexRadioButtons = new JRadioButton[2];
                temp.add(sexRadioButtons[0] = new JRadioButton("M"));
                temp.add(sexRadioButtons[1] = new JRadioButton("F"));

                ButtonGroup sexGroup = new ButtonGroup();
                sexGroup.add(sexRadioButtons[0]);
                sexGroup.add(sexRadioButtons[1]);

                mainPanel.add(temp);
            }
            else if (i == 9) {
                List<String> departmentList = DAO.sharedInstance().selectDepartment();
                List<Integer> dnoList = DAO.sharedInstance().selectDno();

                List<String> dList = new ArrayList<>();
                for (int j = 0; j < departmentList.size(); j++)
                    dList.add(dnoList.get(j) + " (" + departmentList.get(j) + ")");
                departmentComboBox = new JComboBox(dList.toArray());

                temp.add(departmentComboBox);
                mainPanel.add(temp);
            }
            else {
                temp.add(textFields[i] = new JTextField(20));
                mainPanel.add(temp);
            }
        }

        JPanel temp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton insertBtn = new JButton("직원 추가");
        insertBtn.addActionListener(this);
        temp.add(insertBtn);
        mainPanel.add(temp);

        add(mainPanel);

        setVisible(true);
    }

    // 값을 입력 후, 직원 추가 버튼을 누르면
    // DAO의 insertEmployee 함수를 통해 직원이 추가됨.
    // 추가 후, 부모 Frame을 reload 시켜줌.
    // 만약, 유효하지 않는 값이 입력되면 에러를 발생하고, 에러 메시지를 띄워줌.
    @Override
    public void actionPerformed(ActionEvent e) {
        String[] info = new String[10];
        for (int i = 0; i < 10; i++) {
            if (i == 6) {
                if (sexRadioButtons[0].isSelected()) info[i] = "M";
                else if (sexRadioButtons[1].isSelected()) info[i] = "F";
                else info[i] = null;
            } else if (i == 9) {
                String selectedItem = (String) departmentComboBox.getSelectedItem();
                info[i] = selectedItem.substring(0, selectedItem.indexOf(" "));
            } else {
                info[i] = textFields[i].getText();
            }
        }

        try {
            dao.insertEmployee(info);
            parentFrame.repaint();
            dispose();
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage());
        }
    }
}