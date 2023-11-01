import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO {
    private static final String url = "jdbc:mysql://localhost:3306/jdbc?serverTimezone=UTC";
    private static final String user = "root";
    private static final String password = "1234";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        }
    }

    private DAO() {}

    private static DAO obj;

    public static DAO sharedInstance() {
        if (obj == null) obj = new DAO();
        return obj;
    }

    public Connection conn;
    private Statement stmt;
    private ResultSet rs;

    private boolean connect() {
        boolean result = false;
        try {
            conn = DriverManager.getConnection(url, user, password);
            result = true;
        } catch (Exception e) {
            System.out.println("데이터베이스에 연결할 수 없습니다.");
            e.printStackTrace();
        }
        return result;
    }

    private void close() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            System.out.print("연결을 종료할 수 없습니다.");
            e.printStackTrace();
        }
    }

    public List<EMPLOYEE> getList() {
        List<EMPLOYEE> list = null;
        String sql = "SELECT E1.Fname, E1.Ssn, E1.Bdate, E1.Address, E1.Sex, E1.Salary, E2.Fname, D.Dname " +
                "FROM EMPLOYEE E1 " +
                "LEFT OUTER JOIN EMPLOYEE E2 ON E1.Super_ssn = E2.Ssn " +
                "JOIN DEPARTMENT D ON E1.Dno = D.Dnumber";

        // DB 연결을 실패시, 프로그램 종료
        if (!connect()) System.exit(0);

        try {
            stmt = conn.createStatement();
            if (stmt != null) {
                rs = stmt.executeQuery(sql);

                list = new ArrayList<EMPLOYEE>();

                while (rs.next()) {
                    EMPLOYEE emp = new EMPLOYEE();
                    emp.setName(rs.getString("E1.Fname"));
                    emp.setSsn(rs.getString("E1.Ssn"));
                    emp.setBdate(rs.getDate("E1.Bdate"));
                    emp.setAddress(rs.getString("E1.Address"));
                    emp.setSex(rs.getString("E1.Sex"));
                    emp.setSalary(rs.getDouble("E1.Salary"));
                    emp.setSupervisor(rs.getString("E2.Fname"));
                    emp.setDepartment(rs.getString("D.Dname"));
                    list.add(emp);
                }
            }
        } catch (SQLException e) {
            System.out.println("Statement 객체를 생성할 수 없습니다.");
            e.printStackTrace();
        } finally {
            this.close();
        }

        return list;
    }


//    public DAO() {
//        try (Connection conn = getConnection()) {
//            System.out.println("정상적으로 연결되었습니다.");
//            runSQL(conn);
//        } catch (SQLException e) {
//            System.err.println("연결할 수 없습니다.");
//            e.printStackTrace();
//        }
//    }
//
//    public Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(url, user, password);
//    }
//
//    public static void runSQL(Connection conn) throws SQLException {
//        try (Statement stmt = conn.createStatement()) {
//            getResult(stmt);
//        }
//    }
//
//    private static void getResult(Statement stmt) throws SQLException {
//        String sql = "SELECT Fname, Salary FROM EMPLOYEE WHERE sex='M'";
//        String fname;
//        double salary;
//        try (ResultSet rs = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                fname = rs.getString(1);
//                salary = rs.getDouble("Salary");
//                System.out.printf("Fname : %s Salary : %f\n", fname, salary);
//            }
//        }
//    }
}
