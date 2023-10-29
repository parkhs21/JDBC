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

}