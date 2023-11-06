package Data;

import java.io.Serializable;
import java.util.Date;

public class EMPLOYEE implements Serializable {
    private String Name;
    private String Ssn;
    private Date Bdate;
    private String Address;
    private String Sex;
    private double Salary;
    private String Supervisor;
    private String Department;

    public EMPLOYEE() {
        super();
    }

    public EMPLOYEE(String Name, String Ssn, Date Bdate, String Address, String Sex, double Salary, String Supervisor, String Department) {
        this.Name = Name;
        this.Ssn = Ssn;
        this.Bdate = Bdate;
        this.Address = Address;
        this.Sex = Sex;
        this.Salary = Salary;
        this.Supervisor = Supervisor;
        this.Department = Department;
    }

    public void setName(String Name) { this.Name = Name; }
    public void setSsn(String Ssn) { this.Ssn = Ssn; }
    public void setBdate(Date Bdate) { this.Bdate = Bdate; }
    public void setAddress(String Address) { this.Address = Address; }
    public void setSex(String Sex) { this.Sex = Sex; }
    public void setSalary(double Salary) { this.Salary = Salary; }
    public void setSupervisor(String Supervisor) { this.Supervisor = Supervisor; }
    public void setDepartment(String Department) { this.Department = Department; }

    @Override
    public String toString() {
        String result = "EMPLOYEE [";

        result += "Name=" + Name + ", ";
        result += "Ssn=" + Ssn + ", ";
        result += "Bdate=" + Bdate + ", ";
        result += "Address=" + Address + ", ";
        result += "Sex=" + Sex + ", ";
        result += "Salary=" + Salary + ", ";
        result += "Supervisor=" + Supervisor + ", ";
        result += "Department=" + Department + "]";

        return result;
    }

    // Array로 반환하는 함수
    // Array로 반환하여 JTable에 바로 넣을 수 있도록 함.
    // String으로 보여지느 JTable에 맞춰, 다른 형태인 Date, double은 전처리 해줌.
    public String[] toArray() {
        String[] result = new String[8];

        result[0] = Name;
        result[1] = Ssn;
        result[2] = (Bdate == null) ? null : Bdate.toString();
        result[3] = Address;
        result[4] = Sex;
        result[5] = (Salary == 0.0) ? null : String.valueOf(Salary);
        result[6] = Supervisor;
        result[7] = Department;

        return result;
    }
}