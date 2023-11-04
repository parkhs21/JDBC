package Frame;

import Data.*;
import Dialog.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// 메인 화면
public class JDBCFrame extends JFrame implements ActionListener, ItemListener, MouseListener {

    private JComboBox searchConditionComboBox;
    private JComboBox searchConditionOrderComboBox;
    private String[] searchConditions = {"All", "Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
    private JPanel[] searchConditionItemPanels = new JPanel[searchConditions.length];
    private JTextField[] serachConditionTextFields = new JTextField[searchConditions.length];

    private String[] defaultItems = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
    private JCheckBox[] searchCheckBoxes = new JCheckBox[defaultItems.length];
    private TableColumn[] tableColumn = new TableColumn[defaultItems.length];
    private DefaultTableModel tableModel;
    private TableColumnModel tableColumnModel;
    private List<String> selectedEmployeeSsn = new ArrayList<>();

    private DAO dao = DAO.sharedInstance();

    private JPanel searchConditionPanel;
    private JLabel selectedEmployeeNames;
    private JLabel totalPeopleCounts;

    private JCheckBox[] searchConditionSexBoxes;
    private JCheckBox[] searchConditionDepartmentBoxes;

    // UI상 가로(행) 형태를 기준으로 묶어서 패널을 만들어 넣음
    public JDBCFrame() {
        setTitle("JDBC GUI");
        setSize(1200, 600);
        setLocation(200, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // 검색 조건 패널 -----------------------------------------
        // ComboBox를 이용하여 조건으로 활용할 항목을 선택
        // 선택된 항목에 따라서 조건을 입력할 수 있는 패널을 보여줌
        // (추가 기능 - 정렬) - 조건으로 활용할 항목이 선택되면 오름차순, 내림차순 정렬을 선택할 수 있음
        // TextField를 이용하여 조건을 입력할 수 있음
        // 급여의 경우, 두개의 TextField를 이용하여 0~Max를 입력할 수 있음
        // 성별의 경우 두개의 CheckBox, 부서의 경우 부서 목록을 조회해 CheckBox로 만들어서 조건을 선택할 수 있음
        searchConditionPanel = new JPanel();
        searchConditionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel searchConditionLabel = new JLabel("검색 범위");
        searchConditionPanel.add(searchConditionLabel);

        searchConditionComboBox = new JComboBox(searchConditions);
        searchConditionComboBox.addItemListener(this);

        searchConditionOrderComboBox = new JComboBox(new String[]{"무관", "오름차순", "내림차순"});

        for (int i = 0; i < searchConditions.length; i++) {
            JPanel tempPanel = new JPanel();
            JTextField textField;
            tempPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            if (searchConditions[i].equals("Name") || searchConditions[i].equals("Ssn") || searchConditions[i].equals("Address") || searchConditions[i].equals("Supervisor")) {
                serachConditionTextFields[i] = new JTextField(12);
                serachConditionTextFields[i].setHorizontalAlignment(SwingConstants.RIGHT);
                tempPanel.add(serachConditionTextFields[i]);
            }
            else if (searchConditions[i].equals("Bdate")) {
                serachConditionTextFields[i] = new JTextField(8);
                serachConditionTextFields[i].setHorizontalAlignment(SwingConstants.RIGHT);
                tempPanel.add(serachConditionTextFields[i]);
                tempPanel.add(new JLabel(" (YYYY-MM-DD)"));
            }
            else if (searchConditions[i].equals("Salary")) {
                serachConditionTextFields[i] = new JTextField(6);
                serachConditionTextFields[i+2] = new JTextField(6);
                serachConditionTextFields[i].setHorizontalAlignment(SwingConstants.RIGHT);
                serachConditionTextFields[i+2].setHorizontalAlignment(SwingConstants.RIGHT);
                tempPanel.add(serachConditionTextFields[i]);
                tempPanel.add(new JLabel(" (0) ~ "));
                tempPanel.add(serachConditionTextFields[i+2]);
                tempPanel.add(new JLabel(" (MAX)"));
            }
            else if (searchConditions[i].equals("Sex")) {
                searchConditionSexBoxes = new JCheckBox[2];
                searchConditionSexBoxes[0] = new JCheckBox("M", true);
                searchConditionSexBoxes[1] = new JCheckBox("F", true);
                tempPanel.add(searchConditionSexBoxes[0]);
                tempPanel.add(searchConditionSexBoxes[1]);
            }
            else if (searchConditions[i].equals("Department")) {
                List<String> departmentList = dao.selectDepartment();
                searchConditionDepartmentBoxes = new JCheckBox[departmentList.size()];
                for (int j = 0; j < departmentList.size(); j++) {
                    searchConditionDepartmentBoxes[j] = new JCheckBox(departmentList.get(j), true);
                    tempPanel.add(searchConditionDepartmentBoxes[j]);
                }
            }
            searchConditionItemPanels[i] = tempPanel;
        }

        searchConditionPanel.add(searchConditionComboBox);
        mainPanel.add(searchConditionPanel);
        // -----------------------------------------------------


        // 검색 항목 패널 -----------------------------------------
        // CheckBox를 이용하여 어떤 항목(열)을 보여줄지 선택할 수 있음
        JPanel searchItemPanel = new JPanel();
        searchItemPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel searchItemLabel = new JLabel("검색 항목");
        searchItemPanel.add(searchItemLabel);

        for (int i = 0; i < defaultItems.length; i++) {
            searchCheckBoxes[i] = new JCheckBox(defaultItems[i], true);
            searchItemPanel.add(searchCheckBoxes[i]);
        }

        JButton searchBtn = new JButton("검색");
        searchBtn.addActionListener(this);
        searchItemPanel.add(searchBtn);

        mainPanel.add(searchItemPanel);
        // -----------------------------------------------------


        // 검색 결과 패널 -----------------------------------------
        // Table을 활용해 검색 결과를 보여줌
        // ScrollPane을 활용해 값이 많아지면 스크롤을 이용해 볼 수 있음
        // TableCoulmnModel을 활용해 선택된 열을 조작할 수 있음
        JPanel searchResultPanel = new JPanel();
        searchResultPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        tableModel = new DefaultTableModel(defaultItems, 0);
        JTable table = new JTable(tableModel);
        table.addMouseListener(this);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 400));

        tableColumnModel = table.getColumnModel();
        for (int i = 0; i < defaultItems.length; i++)
            tableColumn[i] = tableColumnModel.getColumn(i);

        searchResultPanel.add(scrollPane);
        mainPanel.add(searchResultPanel);
        // -----------------------------------------------------


        // 선택한 직원 패널 ----------------------------------------
        // 선택된 직원의 이름을 보여줌
        // 단순 Label을 통해 값을 조작함
        JPanel selectedEmployeePanel = new JPanel();
        selectedEmployeePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel selectedEmployeeLabel = new JLabel("선택한 직원 : ");
        selectedEmployeeNames = new JLabel("");

        selectedEmployeePanel.add(selectedEmployeeLabel);
        selectedEmployeePanel.add(selectedEmployeeNames);
        mainPanel.add(selectedEmployeePanel);
        // -----------------------------------------------------


        // 하단 부가 기능 패널 --------------------------------------
        // 직원의 수를 보여줌, 직원을 추가, 수정, 삭제할 수 있음
        // 단순 Label 및 Button을 통해 값을 조작함
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        JPanel totalPeoplePanel = new JPanel();
        totalPeoplePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel totalPeopleLabel = new JLabel("인원 수 : ");
        totalPeopleCounts = new JLabel("");
        totalPeoplePanel.add(totalPeopleLabel);
        totalPeoplePanel.add(totalPeopleCounts);

        JPanel etcPanel = new JPanel();
        etcPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton updateBtn = new JButton("직원 수정");
        updateBtn.addActionListener(this);
        etcPanel.add(updateBtn);

        JButton insertBtn = new JButton("직원 추가");
        insertBtn.addActionListener(this);
        etcPanel.add(insertBtn);

        JButton deleteBtn = new JButton("직원 삭제");
        deleteBtn.addActionListener(this);
        etcPanel.add(deleteBtn);

        bottomPanel.add(totalPeoplePanel);
        bottomPanel.add(etcPanel);
        mainPanel.add(bottomPanel);
        // -----------------------------------------------------


        add(mainPanel);
        setVisible(true);
    }

    // repaint()를 통해 화면을 갱신할 수 있음
    // 기존의 repaint 실행 후, 테이블을 갱신할 수 있는 코드를 통해 repaint할 경우, 값이 갱신됨
    // 도메인(열) 선택 조건을 먼저 확인하여 selectItems에 반영함
    // 정렬 조건을 확인하여 conditionOrder에 반영함
    // 도메인(열) 값 조건을 확인하여 conditionLabel, conditionValue에 반영함
    // 반영된 각 값들을 통해 dao.selectEmployee()를 실행함
    // 실행된 결과를 통해 테이블을 갱신함
    @Override
    public void repaint() {
        super.repaint();
        List<EMPLOYEE> list;

        // 도메인(열) 선택 조건 ----------------------------------
        String[] selectItems = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
        for (int i = 0; i < defaultItems.length; i++)
            selectItems[i] = (searchCheckBoxes[i].isSelected()) ? defaultItems[i] : null;

        // 정렬 -----------------------------------------------
        String conditionOrder = null;
        if (searchConditionOrderComboBox.getSelectedIndex() == 1)
            conditionOrder = "ASC";
        else if (searchConditionOrderComboBox.getSelectedIndex() == 2)
            conditionOrder = "DESC";


        // 도메인(열) 값 조건 -----------------------------------------
        if (searchConditionComboBox.getSelectedIndex() == 0) {
            list = dao.selectEmployee(selectItems);
        }
        else if (searchConditionComboBox.getSelectedIndex() == 5) {
            String[] checks = new String[searchConditionSexBoxes.length];
            for (int i = 0; i < searchConditionSexBoxes.length; i++)
                if (searchConditionSexBoxes[i].isSelected()) checks[i] = searchConditionSexBoxes[i].getText();
                else checks[i] = null;
            list = dao.selectEmployee(selectItems, "Sex", checks, conditionOrder);
        }
        else if (searchConditionComboBox.getSelectedIndex() == 6) {
            String[] texts = {serachConditionTextFields[6].getText(), serachConditionTextFields[8].getText()};
            if (texts[0].isEmpty()) texts[0] = null;
            if (texts[1].isEmpty()) texts[1] = null;

            list = dao.selectEmployee(selectItems, "Salary", texts, conditionOrder);
        }
        else if (searchConditionComboBox.getSelectedIndex() == 8) {
            String[] checks = new String[searchConditionDepartmentBoxes.length];
            for (int i = 0; i < searchConditionDepartmentBoxes.length; i++)
                if (searchConditionDepartmentBoxes[i].isSelected())
                    checks[i] = searchConditionDepartmentBoxes[i].getText();
                else checks[i] = null;
            list = dao.selectEmployee(selectItems, "Department", checks, conditionOrder);
        }
        else {
            String conditionLabel = searchConditions[searchConditionComboBox.getSelectedIndex()];
            String conditionValue = serachConditionTextFields[searchConditionComboBox.getSelectedIndex()].getText();
            if (conditionValue.isEmpty()) conditionValue = null;
            list = dao.selectEmployee(selectItems, conditionLabel, conditionValue, conditionOrder);
        }



        for (TableColumn tc : tableColumn)
            tableColumnModel.removeColumn(tc);
        for (int i = 0; i < defaultItems.length; i++)
            if (searchCheckBoxes[i].isSelected())
                tableColumnModel.addColumn(tableColumn[i]);

        tableModel.setRowCount(0);
        for (EMPLOYEE e : list)
            tableModel.addRow(e.toArray());

        selectedEmployeeNames.setText("");
        totalPeopleCounts.setText(String.valueOf(list.size()));
    }

    // 버튼이 수행되면 각 기능을 실행함
    // 검색 버튼의 경우 repaint를 통해 화면의 테이블을 갱신함. 검색 조건은 repaint에서 각 값들을 확인함
    // 직원 수정의 경우 추후 개발할 내용임
    // 직원 추가의 경우 InsertDialog를 통해 새로운 창을 띄우고 값을 입력받음
    // 직원 삭제의 경우 선택된 직원의 Ssn을 통해 삭제함
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "검색":
                repaint();
                break;
            case "직원 수정":
                break;
            case "직원 추가":
                new InsertDialog(this);
                break;
            case "직원 삭제":
                if (searchCheckBoxes[1].isSelected()) {
                    dao.deleteEmployee(selectedEmployeeSsn.toArray(new String[selectedEmployeeSsn.size()]));
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "직원 정보를 삭제하려면, Ssn 열이 선택되어야 합니다.");
                }
                break;
        }
    }

    // ComboBox를 통해 선택된 항목에 따라서 조건을 입력할 수 있는 패널을 보여줌
    // 선택된 조건에 따라서 입력받을 내용(TextField, CheckBox 등)의 선택지를 바꿔줌
    @Override
    public void itemStateChanged(ItemEvent e) {
        for (int i = 0; i < searchConditions.length; i++) {
            if (searchConditions[i] == e.getItem()) {
                if (searchConditions[i].equals("All"))
                    searchConditionPanel.remove(searchConditionOrderComboBox);
                else
                    searchConditionPanel.add(searchConditionOrderComboBox);

                if (e.getStateChange() == ItemEvent.DESELECTED)
                    searchConditionPanel.remove(searchConditionItemPanels[i]);
                else if (e.getStateChange() == ItemEvent.SELECTED)
                    searchConditionPanel.add(searchConditionItemPanels[i]);

                revalidate();
                break;
            }
        }
    }

    // 마우스 클릭을 통해 테이블에서 선택된 것을 조작해줌
    // 선택된 직원의 이름을 보여줌
    // 다중 선택이 됨에 따라 추가로 선택된 직원의 이름을 보여줌
    @Override
    public void mouseClicked(MouseEvent e) {
        String selectedList = "";
        int[] rows = ((JTable) e.getSource()).getSelectedRows();
        selectedEmployeeSsn.clear();
        for (int row : rows) {
            selectedList += ",  " + ((String) tableModel.getValueAt(row, 0));
            selectedEmployeeSsn.add((String) tableModel.getValueAt(row, 1));
        }
        selectedEmployeeNames.setText(selectedList.substring(3));
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
