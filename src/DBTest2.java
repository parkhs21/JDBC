import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class DBTest2 {
    public static void main (String args []) throws SQLException, IOException {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;

        String dbacct, passwrd, dbname, ssn, lname;
        Double salary;

//        System.out.println("Enter database account: ");
//        dbacct = scanner.nextLine();
//        System.out.println("Enter password: ");
//        passwrd = scanner.nextLine();
//        System.out.println("Enter database name: ");
//        dbname = scanner.nextLine();
        dbacct = "root";
        passwrd = "mypassword";
        dbname = "COMPANY";

        String url = "jdbc:mysql://localhost:3306/" + dbname;
        conn = DriverManager.getConnection(url, dbacct, passwrd);

//        String stm1 = "SELECT Lname,Salary FROM EMPLOYEE WHERE Ssn = ?";
        String stm1 = "SELECT Fname,Lname,Salary FROM EMPLOYEE WHERE Sex = ?";
        PreparedStatement p = conn.prepareStatement(stm1);

//        System.out.println("Enter a Social Security Number: ");
//        ssn = scanner.nextLine();
        ssn = "M";

        p.clearParameters();
        p.setString(1, ssn);
        ResultSet r = p.executeQuery();

        while (r.next()) {
            System.out.println(r.getRow());
            System.out.println(r.getMetaData());

            lname = r.getString(2);
            salary = r.getDouble(3);
            System.out.println(lname + " " + salary);
            System.out.println();
        }

        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
        }
    }
}
