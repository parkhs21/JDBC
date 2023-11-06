package Data;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;


public class DAO {
    private static final String url = "jdbc:mysql://localhost:3306/COMPANY";
    private static final String user = "root";
    private static final String password = "mypassword";

    // 드라이버 로딩
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        }
    }

    private DAO() {}

    // Connection 객체를 공유해서 사용하기 위해 싱글톤 패턴을 사용
    private static DAO obj;
    public static DAO sharedInstance() {
        if (obj == null) obj = new DAO();
        return obj;
    }

    public Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    // DB 연결
    private boolean connect() {
        boolean result = false;
        try {
            conn = DriverManager.getConnection(url, user, password);
            result = true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "데이터베이스에 연결할 수 없습니다.");
            System.out.println("데이터베이스에 연결할 수 없습니다.");
            e.printStackTrace();
        }
        return result;
    }

    // DB 연결 종료
    private void close() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            System.out.print("연결을 종료할 수 없습니다.");
            e.printStackTrace();
        }
    }


    // 각 이름의 속성을 하나로 합치고, 상급자의 이름을 가져오고, 부서 이름을 가져온 상태에서 크게 변하지 않음.
    // 직원 하나를 대상으로 가져오는 값들이므로, 기존 직원 테이블에서 조작하면 자동으로 뷰에 반영됨.
    // 따라서 뷰를 생성하고, 뷰를 통해 직원을 가져오는 것이 더 효율적이라 판단해서 사용하게 됐음.
    // 뷰 생성
    public void createView() {
        this.dropView();
        // 원하는 형태의 뷰 생성 쿼리
        String createViewQuery = "CREATE VIEW emp_view " +
                "as SELECT CONCAT(E1.Fname, ' ', E1.Minit, ' ', E1.Lname) AS Name, E1.Ssn, E1.Bdate, E1.Address, E1.Sex, " +
                            "E1.Salary, CONCAT(E2.Fname, ' ', E2.Minit, ' ', E2.Lname) AS Supervisor, D.Dname AS Department " +
                "FROM EMPLOYEE E1 " +
                "LEFT OUTER JOIN EMPLOYEE E2 ON E1.Super_ssn = E2.Ssn " +
                "JOIN DEPARTMENT D ON E1.Dno = D.Dnumber";

        // DB 연결 실패시, 프로그램 종료
        if (!connect()) System.exit(0);

        // 쿼리를 실행 후, DB 연결 종료
        try {
            pstmt = conn.prepareStatement(createViewQuery);
            pstmt.clearParameters();
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("PrepareStatement 객체를 생성할 수 없습니다.");
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    // 뷰 삭제
    private void dropView() {
        String dropViewQuery = "DROP VIEW emp_view";

        if (!connect()) System.exit(0);

        try {
            pstmt = conn.prepareStatement(dropViewQuery);
            pstmt.clearParameters();
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("PrepareStatement 객체를 생성할 수 없습니다.");
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    // 뷰의 데이터를 가져오는 메소드
    private List<EMPLOYEE> selectEmployee(String[] labels, String Query) {
        List<EMPLOYEE> list = null;

        // DB 연결을 실패시, 프로그램 종료
        if (!connect()) System.exit(0);

        // 파라미터로 들어온 쿼리를 실행함.
        // 반환 값을 각각 EMPLOYEE 객체에 저장하고, 값들을 모아서 리스트로 반환함.
        try {
            pstmt = conn.prepareStatement(Query);
            pstmt.clearParameters();
            rs = pstmt.executeQuery();
            list = new ArrayList<EMPLOYEE>();

            while (rs.next()) {
                EMPLOYEE emp = new EMPLOYEE();
                for (String label : labels) {
                    if (label == "Name") emp.setName(rs.getString("Name"));
                    else if (label == "Ssn") emp.setSsn(rs.getString("Ssn"));
                    else if (label == "Bdate") emp.setBdate(rs.getDate("Bdate"));
                    else if (label == "Address") emp.setAddress(rs.getString("Address"));
                    else if (label == "Sex") emp.setSex(rs.getString("Sex"));
                    else if (label == "Salary") emp.setSalary(rs.getDouble("Salary"));
                    else if (label == "Supervisor") emp.setSupervisor(rs.getString("Supervisor"));
                    else if (label == "Department") emp.setDepartment(rs.getString("Department"));
                }
                list.add(emp);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "PrepareStatement 연결할 수 없습니다.");
            System.out.println("PrepareStatement 객체를 생성할 수 없습니다.");
            e.printStackTrace();
        } finally {
            this.close();
        }

        return list;
    }

    // 원하는 열에만 맞춰서 가져옴.
    // select (labels) from emp_view
    public List<EMPLOYEE> selectEmployee(String[] labels) {
        String selectQuery = "";
        for (String label : labels)
            selectQuery += ", " + label;
        selectQuery = "SELECT Ssn" + selectQuery + " FROM emp_view";

        return this.selectEmployee(labels, selectQuery);
    }

    // 원하는 열, 조건, 정렬에 맞춰서 가져옴.
    // select (labels) from emp_view where (conditionLabel = conditionValue)
    // order by (conditionLabel) (conditionOrder)
    public List<EMPLOYEE> selectEmployee(String[] labels, String conditionLabel, String conditionValue, String conditionOrder) {
        String selectQuery = "";
        for (String label : labels) selectQuery += ", " + label;
        selectQuery = "SELECT Ssn" + selectQuery + " FROM emp_view";
        if (conditionValue != null) selectQuery += " WHERE " + conditionLabel + " = '" + conditionValue + "'";
        if (conditionOrder != null) selectQuery += " ORDER BY " + conditionLabel + " " + conditionOrder;

        return this.selectEmployee(labels, selectQuery);
    }

    // 원하는 열, 조건들, 정렬에 맞춰서 가져옴.
    // 만약 조건이 Salary라면, conditionValues[0]은 최소값, conditionValues[1]은 최대값으로 범위로 가져옴.
    // select (labels) from emp_view where (conditionLabel in conditionValues)
    // order by (conditionLabel) (conditionOrder)
    public List<EMPLOYEE> selectEmployee(String[] labels, String conditionLabel, String[] conditionValues, String conditionOrder) {
        String selectQuery = "";
        for (String label : labels) selectQuery += ", " + label;
        selectQuery = "SELECT Ssn" + selectQuery + " FROM emp_view";

        if (conditionLabel == "Salary") {
            String temp = "";
            if (conditionValues[0] != null) temp += conditionLabel + " >= " + conditionValues[0];
            if (conditionValues[1] != null && !temp.isEmpty()) temp += " AND ";
            if (conditionValues[1] != null) temp += conditionLabel + " <= " + conditionValues[1];
            if (!temp.isEmpty()) selectQuery += " WHERE " + temp;

        } else {
            selectQuery += " WHERE " + conditionLabel + " IN (";
            for (int i = 0; i < conditionValues.length; i++) {
                if (i != 0) selectQuery += ", ";
                selectQuery += "'" + conditionValues[i] + "'";
            }
            selectQuery += ")";
        }

        if (conditionOrder != null) selectQuery += " ORDER BY " + conditionLabel + " " + conditionOrder;

        return this.selectEmployee(labels, selectQuery);
    }


    // select와 다르게 employee에 직접 추가됨.
    // 직원 추가
    public int insertEmployee(String[] info) {
        int insertRowNum = 0;
        String insertQuery = "INSERT INTO EMPLOYEE (FName, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if (!connect()) System.exit(0);
        if (info.length != 10) System.exit(0);

        try {
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.clearParameters();

            for (int i = 0; i < 10; i++)
                pstmt.setString(i + 1, info[i]);

            insertRowNum = pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "PrepareStatement 연결할 수 없습니다.");
            System.out.println("PrepareStatement 객체를 생성할 수 없습니다.");
            e.printStackTrace();
        } finally {
            this.close();
        }

        return insertRowNum;
    }

    // PK인 Ssn 사용하여 직원 삭제
    // select와 다르게 employee에서 직접 삭제됨.
    // 직원 삭제
    public int deleteEmployee(String[] Ssns) {
        int deleteRowNum = 0;
        String deleteQuery = "DELETE FROM EMPLOYEE WHERE Ssn = ?";

        if (!connect()) System.exit(0);

        try {
            pstmt = conn.prepareStatement(deleteQuery);

            for (String ssn : Ssns) {
                pstmt.clearParameters();
                pstmt.setString(1, ssn);
                deleteRowNum += pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "PrepareStatement 연결할 수 없습니다.");
            System.out.println("PrepareStatement 객체를 생성할 수 없습니다.");
            e.printStackTrace();
        } finally {
            this.close();
        }

        return deleteRowNum;
    }

    // 부서 옵션에서 선택할 수 있는 값을 얻기 위함.
    // 부서 조회
    public List<String> selectDepartment() {
        List<String> list = null;
        String selectDepartmentQuery = "SELECT Dname FROM DEPARTMENT ORDER BY Dnumber";

        // DB 연결을 실패시, 프로그램 종료
        if (!connect()) System.exit(0);

        try {
            pstmt = conn.prepareStatement(selectDepartmentQuery);
            pstmt.clearParameters();
            rs = pstmt.executeQuery();
            list = new ArrayList<String>();

            while (rs.next())
                list.add(rs.getString("Dname"));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "PrepareStatement 연결할 수 없습니다.");
            System.out.println("PrepareStatement 객체를 생성할 수 없습니다.");
            e.printStackTrace();
        } finally {
            this.close();
        }

        return list;
    }

    // 부서 옵션에서 선택할 수 있는 값을 얻기 위함.
    // 부서 조회
    public List<Integer> selectDno() {
        List<Integer> list = null;
        String selectDepartmentQuery = "SELECT Dnumber as Dno FROM DEPARTMENT ORDER BY Dnumber";

        // DB 연결을 실패시, 프로그램 종료
        if (!connect()) System.exit(0);

        try {
            pstmt = conn.prepareStatement(selectDepartmentQuery);
            pstmt.clearParameters();
            rs = pstmt.executeQuery();
            list = new ArrayList<Integer>();

            while (rs.next())
                list.add(rs.getInt("Dno"));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "PrepareStatement 연결할 수 없습니다.");
            System.out.println("PrepareStatement 객체를 생성할 수 없습니다.");
            e.printStackTrace();
        } finally {
            this.close();
        }

        return list;
    }
}
