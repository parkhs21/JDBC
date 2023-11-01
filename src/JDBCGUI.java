import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JDBCGUI extends JFrame {

    private String[] searchItems = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
    private JCheckBox[] searchCheckBoxes = new JCheckBox[searchItems.length];

    private DefaultTableModel tableModel;
    private JTextField FnameField;
    private JTextField MnameField;
    private JTextField LnameField;
    private JTextField ssnField;
    private JTextField bdateField;
    private JTextField addressField;
    private JTextField sexField;
    private JTextField salaryField;
    private JTextField supervisorField;
    private JTextField departmentField;

    public JDBCGUI() {
        setTitle("JDBC GUI");
        setSize(1000, 800);
        setLocation(100, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel searchCheckBoxPanel = new JPanel();
        searchCheckBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel searchItemLabel = new JLabel("검색 항목");
        searchCheckBoxPanel.add(searchItemLabel);

        for (int i = 0; i < searchItems.length; i++) {
            searchCheckBoxes[i] = new JCheckBox(searchItems[i], true);
            searchCheckBoxPanel.add(searchCheckBoxes[i]);
        }

        JButton searchBtn = new JButton("검색");
        searchCheckBoxPanel.add(searchBtn);

        add(searchCheckBoxPanel, BorderLayout.NORTH);

        JPanel panel3 = new JPanel();
        tableModel = new DefaultTableModel(searchItems, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        DAO dao = DAO.sharedInstance();

        List<EMPLOYEE> list = dao.getList();
        for (EMPLOYEE e : list) {
            tableModel.addRow(e.toArray());
        }

        panel3.add(scrollPane);

        JPanel insertPanel = new JPanel();
        insertPanel.setLayout(new BoxLayout(insertPanel, BoxLayout.Y_AXIS));

        insertPanel.add(new JLabel("새로운 직원 정보 추가"));

        FnameField = new JTextField(20);
        MnameField = new JTextField(20);
        LnameField = new JTextField(20);
        ssnField = new JTextField(20);
        bdateField = new JTextField(20);
        addressField = new JTextField(20);
        sexField = new JTextField(20);
        salaryField = new JTextField(20);
        supervisorField = new JTextField(20);
        departmentField = new JTextField(20);

        insertPanel.add(new JLabel("First Name:"));
        insertPanel.add(FnameField);

        insertPanel.add(new JLabel("Middle Name:"));
        insertPanel.add(MnameField);

        insertPanel.add(new JLabel("Last Name:"));
        insertPanel.add(LnameField);

        insertPanel.add(new JLabel("Ssn:"));
        insertPanel.add(ssnField);

        insertPanel.add(new JLabel("Birthdate (YYYY-MM-DD):"));
        insertPanel.add(bdateField);

        insertPanel.add(new JLabel("Address:"));
        insertPanel.add(addressField);

        insertPanel.add(new JLabel("Sex:"));
        insertPanel.add(sexField);

        insertPanel.add(new JLabel("Salary:"));
        insertPanel.add(salaryField);

        insertPanel.add(new JLabel("Super_ssn:"));
        insertPanel.add(supervisorField);

        insertPanel.add(new JLabel("Dno:"));
        insertPanel.add(departmentField);

        JButton insertButton = new JButton("직원 정보 추가하기");
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertEmployee();
            }
        });
        insertPanel.add(insertButton);

        add(panel3, BorderLayout.CENTER);
        add(insertPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void insertEmployee() {
        String Fname = FnameField.getText();
        String Mname = MnameField.getText();
        String Lname = LnameField.getText();
        String ssn = ssnField.getText();
        String bdate = bdateField.getText();
        String address = addressField.getText();
        String sex = sexField.getText();
        String salary = salaryField.getText();
        String supervisor = supervisorField.getText();
        String department = departmentField.getText();

        String jdbcUrl = "jdbc:mysql://localhost:3306/jdbc?serverTimezone=UTC";
        String username = "root";
        String password = "1234";

        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 데이터베이스 연결
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // INSERT 쿼리 작성
            String insertQuery = "INSERT INTO employee (FName, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            // PreparedStatement 생성
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, Fname);
            preparedStatement.setString(2, Mname);
            preparedStatement.setString(3, Lname);
            preparedStatement.setString(4, ssn);
            preparedStatement.setString(5, bdate);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, sex);
            preparedStatement.setString(8, salary);
            preparedStatement.setString(9, supervisor);
            preparedStatement.setString(10, department);

            // 쿼리 실행
            preparedStatement.executeUpdate();

            // 리소스 정리
            preparedStatement.close();
            connection.close();

            // 성공 메시지 또는 리프레시
            // 예를 들어, 테이블 모델을 업데이트하거나 화면을 리프레시할 수 있습니다.
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // 오류 처리
        }

        // 입력 필드 비우기
        FnameField.setText("");
        MnameField.setText("");
        LnameField.setText("");
        ssnField.setText("");
        bdateField.setText("");
        addressField.setText("");
        sexField.setText("");
        salaryField.setText("");
        supervisorField.setText("");
        departmentField.setText("");

        // Insert the employee data into the database using JDBC

        // You need to write the JDBC code to insert the data into your database here
        // You should establish a database connection, create a PreparedStatement, and execute the insertion query.

        // After insertion, you can clear the text fields
        FnameField.setText("");
        MnameField.setText("");
        LnameField.setText("");
        ssnField.setText("");
        bdateField.setText("");
        addressField.setText("");
        sexField.setText("");
        salaryField.setText("");
        supervisorField.setText("");
        departmentField.setText("");
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            JDBCGUI gui = new JDBCGUI();
        });
    }
}
