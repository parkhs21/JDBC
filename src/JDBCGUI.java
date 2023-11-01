import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JDBCGUI extends JFrame {

    private String[] searchItems = {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary", "Supervisor", "Department"};
    private JCheckBox[] searchCheckBoxes = new JCheckBox[searchItems.length];

    private String[][] temp = {};

    class EventHandlerSearch implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            refreshSearch();
        }
    }

    private void refreshSearch() {
        System.out.println("search");
        //선택한 항목들을 담을 배열 생성
        ArrayList<String> selectedColumns = new ArrayList<String>();
        Object[] ResultList;

        //이전 검색이 누적되면 안되므로, 초기화
//        pIdList.clear();

        //체크박스 검사
        if(searchCheckBoxes[0].isSelected()) {
            selectedColumns.add("E.Fname");
            selectedColumns.add("E.Minit");
            selectedColumns.add("E.Lname");

//            model.addColumn("Fname");
//            model.addColumn("Minit");
//            model.addColumn("Lname");
        }

        if(searchCheckBoxes[0].isSelected()) {
            selectedColumns.add("E.Ssn");
//            model.addColumn("SSN");
        }
        if(searchCheckBoxes[1].isSelected()) {
            selectedColumns.add("E.Bdate");
//            model.addColumn("Birth date");
        }
        if(searchCheckBoxes[2].isSelected()) {
            selectedColumns.add("E.Address");
//            model.addColumn("Address");
        }
        if(searchCheckBoxes[3].isSelected()) {
            selectedColumns.add("E.Sex");
//            model.addColumn("Sex");
        }
        if(searchCheckBoxes[4].isSelected()) {
            selectedColumns.add("E.Salary");
//            model.addColumn("Salary");
        }
        if(searchCheckBoxes[5].isSelected()) {
            //한 셀에 이름을 붙여주기 위함
            selectedColumns.add("concat(S.Fname,' ',S.Minit,' ',S.Lname)");
//            model.addColumn("Supervisor Name");
        }
        if(searchCheckBoxes[6].isSelected()) {
            selectedColumns.add("Dname");
//            model.addColumn("Department Name");
        }

        //쿼리문 생성
        String searchingStatement = "SELECT ";

        //Column 선택에 따른 쿼리문 추가
        for(int i=0; i<selectedColumns.size(); i++) {
            if(i+1 == selectedColumns.size()) {
                searchingStatement = searchingStatement+selectedColumns.get(i)+" ";
            }else {
                searchingStatement = searchingStatement+selectedColumns.get(i)+", ";
            }
        }

        searchingStatement = searchingStatement+" FROM (EMPLOYEE AS E LEFT OUTER JOIN EMPLOYEE AS S ON S.Ssn = E.Super_ssn), DEPARTMENT WHERE E.Dno = Dnumber";

        //어떤 질의를 해도 기본적으로 받아올 정보 (내부적으로 처리)
        String preEditStatement = "SELECT Fname, Minit, Lname, Ssn, Salary FROM EMPLOYEE, DEPARTMENT WHERE Dno = Dnumber";

        //Department 별로 검색시에 추가할 쿼리
//        if(isDfilterNeed) {
//            searchingStatement = searchingStatement+" AND Dname = '"+department+"';" ;
//            preEditStatement = preEditStatement +" AND Dname = '"+department+"';" ;
//        }else {
            searchingStatement = searchingStatement+";" ;
            preEditStatement = preEditStatement +";" ;
//        }

        //DAO를 private 멤버변수로 설정하셨으면 지워도 되는 LINE
        DAO dao = DAO.sharedInstance();

        Connection dbConn = dao.conn;

        try {

            PreparedStatement statement = dbConn.prepareStatement(searchingStatement);
            statement.clearParameters();
            ResultSet result = statement.executeQuery();

            System.out.println(searchingStatement);

            while(result.next()){
                //체크박스 초기화
                ResultList = new Object[selectedColumns.size()];
                ResultList[0] = false;

                for(int j=1; j<selectedColumns.size(); j++) {
                    ResultList[j]=(result.getString(j));
                }

//                model.addRow(ResultList);

                DefaultTableCellRenderer rendererForNormal = new DefaultTableCellRenderer();

                //셀 내용 가운데 정렬0
                rendererForNormal.setHorizontalAlignment(SwingConstants.CENTER);

//                tableResult.getColumn("Check").setCellRenderer(renderer);
//                tableResult.getColumn("Check").setPreferredWidth(15);

//                for(int k=1; k<tableResult.getColumnCount(); k++) {
//                    String name = tableResult.getColumnName(k);
//                    tableResult.getColumn(name).setCellRenderer(rendererForNormal);
//                }

                    // 용혁님! 이 부분 하다가 임포트를 해도 먼가 타입선언을 제대로 안한것같기도 하고 이코드때문에 오류가 발생하기도 해서 일단은 주석처리 해두겠습니다! 오류는 java: cannot find symbol
                //  symbol:   variable personalInformation
                //  location: class JDBCGUI 이런 내용이 떠요 !
//                System.out.println(personalInformation);
            }



            //인원수 표시
//            labelNumPeople.setText("Number of people  :  "+model.getRowCount());
        } catch (SQLException e2) {
            e2.printStackTrace();
        }

        try {
            PreparedStatement statement =dbConn.prepareStatement(preEditStatement);
            statement.clearParameters();
            ResultSet resultForBack = statement.executeQuery();

            while(resultForBack.next()){
                ArrayList<String> personalInformation = new ArrayList<String>();
                for(int j=0; j<5; j++) {
                    personalInformation.add(resultForBack.getString(j+1));
                }
//                pIdList.add(personalInformation);
            }



        } catch (SQLException e2) {
            e2.printStackTrace();
        }

    }


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
        searchBtn.addActionListener(new EventHandlerSearch());

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

        //TODO
        /*
        1. 검색버튼 이벤트 클릭 리스너 만들기 - done
        2. 클릭 리스너에서 검색 실행 함수 만들기 - done
        3. 함수 안에서 체크박스 필터링 & sql 생성 -done
        4. 가져와서 화면에 뿌리기 (선택)
        5. 콤보 박스 2개 만들기
        6. 검색 질의에 sql 업데이트
        * */
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