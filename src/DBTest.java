import java.sql.*;
public class DBTest {
    private static final String url = "jdbc:mysql://localhost:3306/COMPANY";
    private static final String user =  "root";
    private static final String password = "mypassword";

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("정상적으로 연결되었습니다.");
            runSQL(conn);
        } catch (SQLException e) {
            System.err.println("연결할 수 없습니다.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
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
