import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO {
    private static final String url = "jdbc:mysql://localhost:3306/COMPANY";
    private static final String user = "root";
    private static final String password = "mypassword";

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

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    private boolean connect() {
        boolean result = false;
        try {
            conn = DriverManager.getConnection(url, user, password);
            result = true;
        } catch (Exception e) {
            System.out.println("연결할 수 없습니다.");
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
        String sql = "SELECT * FROM EMPLOYEE";

        if (connect()) {
            try {
                stmt = conn.createStatement();
                if (stmt != null) {
                    rs = stmt.executeQuery(sql);

                    list = new ArrayList<EMPLOYEE>();

                    while (rs.next()) {
                        EMPLOYEE emp = new EMPLOYEE();
                        emp.setName(rs.getString("Fname"));
                        emp.setSsn(rs.getString("Ssn"));
                        emp.setBdate(rs.getDate("Bdate"));
                        emp.setAddress(rs.getString("Address"));
                        emp.setSex(rs.getString("Sex"));
                        emp.setSalary(rs.getDouble("Salary"));
                        emp.setSupervisor(rs.getString("Super_ssn"));
                        emp.setDepartment(rs.getString("Dno"));
                        list.add(emp);
                    }

                    this.close();
                }
            } catch (SQLException e) {
                System.out.println("Statement 객체를 생성할 수 없습니다.");
                e.printStackTrace();
            }
        } else {
            System.out.println("데이터베이스에 연결할 수 없습니다.");
            System.exit(0);
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

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static void runSQL(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            getResult(stmt);
        }
    }

    private static void getResult(Statement stmt) throws SQLException {
        String sql = "SELECT Fname, Salary FROM EMPLOYEE WHERE sex='M'";
        String fname;
        double salary;
        try (ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fname = rs.getString(1);
                salary = rs.getDouble("Salary");
                System.out.printf("Fname : %s Salary : %f\n", fname, salary);
            }
        }
    }
}
