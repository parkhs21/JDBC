import java.sql.*;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        DAO dao = DAO.sharedInstance();

        List<EMPLOYEE> list = dao.getList();
        for (EMPLOYEE e : list) {
            System.out.println(e.toString());
        }
    }
}