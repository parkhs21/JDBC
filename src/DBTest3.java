import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class DBTest3 {
    public static void main (String args []) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;

        String dbacct, passwrd, dbname, dname, dnumber, mgr_ssn, mgr_sdate;

        System.out.println("Enter database account: ");
        dbacct = scanner.nextLine();
        System.out.println("Enter password: ");
        passwrd = scanner.nextLine();
        System.out.println("Enter database name: ");
        dbname = scanner.nextLine();

        String url = "jdbc:mysql://localhost:3306/" + dbname;
        conn = DriverManager.getConnection(url, dbacct, passwrd);

        String stm1 = "INSERT INTO DEPARTMENT VALUES (?, ?, ?, ?)";
        PreparedStatement p = conn.prepareStatement(stm1);

        System.out.println("Enter a department name: ");
        dname = scanner.nextLine();
        System.out.println("Enter a department number: ");
        dnumber = scanner.nextLine();
        System.out.println("Enter a manager's ssn: ");
        mgr_ssn = scanner.nextLine();
        System.out.println("Enter a manager's start date: ");
        mgr_sdate = scanner.nextLine();

        p.clearParameters();
        p.setString(1, dname);
        p.setString(2, dnumber);
        p.setString(3, mgr_ssn);
        p.setString(4, mgr_sdate);

        p.executeUpdate();

        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
        }
    }
}
