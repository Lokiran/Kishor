import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


interface Payable {
    void processSalary();
}

abstract class Employee implements Payable {
    private int id;
    private String name;
    private double salary;           // For Full-Time: monthly base salary; For Part-Time: earnings (hours * rate)
    private String department;       // NEW
    private int yearsExperience;     // NEW
    private int annualLeaves;        // NEW
    private double annualIncrement;  // NEW (percentage)

    public Employee(int id, String name, double salary,
                    String department, int yearsExperience, int annualLeaves, double annualIncrement) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.department = department;
        this.yearsExperience = yearsExperience;
        this.annualLeaves = annualLeaves;
        this.annualIncrement = annualIncrement;
    }

    // Getters
    public int getId()                { return id; }
    public String getName()           { return name; }
    public double getSalary()         { return salary; }
    public String getDepartment()     { return department; }
    public int getYearsExperience()   { return yearsExperience; }
    public int getAnnualLeaves()      { return annualLeaves; }
    public double getAnnualIncrement(){ return annualIncrement; }

    /** Annual Salary = current monthly base/earnings × 12 */
    public double getAnnualSalary() {
        return salary * 12.0;
    }

    /** Next year annual salary after increment % is applied to current annual salary. */
    public double getNextYearSalary() {
        double incFactor = (annualIncrement / 100.0);
        return getAnnualSalary() * (1.0 + incFactor);
    }

    /** Common details; subclasses may append more. */
    public void displayDetails() {
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Base Salary/Earnings (Monthly): " + salary);
        System.out.println("Department: " + department);
        System.out.println("Years of Experience: " + yearsExperience);
        System.out.println("Annual Leaves: " + annualLeaves);
        System.out.println("Annual Increment %: " + annualIncrement);
        System.out.printf("Annual Salary (Current): %.2f%n", getAnnualSalary());
        System.out.printf("Annual Salary Next Year (After Increment): %.2f%n", getNextYearSalary());
    }

    /** Force subclasses to provide their own bonus calculations. */
    public abstract double calculateBonus();
}

/** Full-time employee with percentage bonus on base monthly salary. */
class FullTimeEmployee extends Employee {
    private double bonusPercentage;

    public FullTimeEmployee(int id, String name, double salary, double bonusPercentage,
                            String dept, int exp, int leaves, double increment) {
        super(id, name, salary, dept, exp, leaves, increment);
        this.bonusPercentage = bonusPercentage;
    }

    @Override
    public double calculateBonus() {
        return getSalary() * (bonusPercentage / 100.0);
    }

    @Override
    public void processSalary() {
        double totalMonthly = getSalary() + calculateBonus();
        System.out.printf("%s (Full-Time) Monthly Payout: %.2f%n", getName(), totalMonthly);
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Bonus % (Monthly): " + bonusPercentage);
    }
}

/** Part-time employee; salary stored as earnings (hours × rate) for the record. */
class PartTimeEmployee extends Employee {
    private int hoursWorked;
    private double hourlyRate;
    private double bonusPercentage; // percentage of earnings

    public PartTimeEmployee(int id, String name, int hoursWorked, double hourlyRate, double bonusPercentage,
                            String dept, int exp, int leaves, double increment) {
        // Store earnings for this entry as the "salary" in the base class for uniform display
        super(id, name, hoursWorked * hourlyRate, dept, exp, leaves, increment);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
        this.bonusPercentage = bonusPercentage;
    }

    @Override
    public double calculateBonus() {
        double earnings = hoursWorked * hourlyRate;
        return earnings * (bonusPercentage / 100.0);
    }

    @Override
    public void processSalary() {
        double monthlyTotal = getSalary() + calculateBonus(); // here, getSalary() is earnings for the period
        System.out.printf("%s (Part-Time) Payout: %.2f%n", getName(), monthlyTotal);
    }

    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Hours Worked: " + hoursWorked);
        System.out.println("Hourly Rate: " + hourlyRate);
        System.out.println("Bonus % (of earnings): " + bonusPercentage);
    }
}

public class Main {

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (e.g., 50000 or 200.5).");
            }
        }
    }

    private static String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("Input cannot be empty.");
        }
    }

    private static String readChoice(Scanner sc, String prompt, String... allowed) {
        while (true) {
            System.out.print(prompt);
            String val = sc.nextLine().trim().toUpperCase();
            for (String a : allowed) {
                if (val.equals(a.toUpperCase())) return val;
            }
            System.out.println("Please enter one of: " + String.join("/", allowed));
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Employee> employees = new ArrayList<>();

        System.out.println("=== Employee Management System (Interactive) ===");

        // Loop to add multiple employees
        while (true) {
            String type = readChoice(sc, "Enter employee type (F = Full-Time, P = Part-Time): ", "F", "P");

            int id = readInt(sc, "Enter ID (integer): ");
            String name = readNonEmpty(sc, "Enter Name: ");

            // Common fields now collected for both types
            String dept = readNonEmpty(sc, "Enter Department: ");
            int exp = readInt(sc, "Enter Years of Experience: ");
            int leaves = readInt(sc, "Enter Annual Leaves: ");
            double increment = readDouble(sc, "Enter Annual Increment (%): ");

            if (type.equals("F")) {
                // Full-Time input
                double baseSalary = readDouble(sc, "Enter Base Monthly Salary: ");
                double bonusPct = readDouble(sc, "Enter Bonus Percentage (%): ");
                employees.add(new FullTimeEmployee(id, name, baseSalary, bonusPct, dept, exp, leaves, increment));
            } else {
                // Part-Time input
                int hours = readInt(sc, "Enter Hours Worked: ");
                double rate = readDouble(sc, "Enter Hourly Rate: ");
                double bonusPct = readDouble(sc, "Enter Bonus Percentage (% of earnings): ");
                employees.add(new PartTimeEmployee(id, name, hours, rate, bonusPct, dept, exp, leaves, increment));
            }

            String more = readChoice(sc, "Add another employee? (Y/N): ", "Y", "N");
            if (more.equals("N")) break;
            System.out.println();
        }

        System.out.println("\n=== Payroll Preview ===");
        for (Employee emp : employees) {
            System.out.println("----------------------------------");
            emp.displayDetails();
            System.out.printf("Calculated Bonus (for this period): %.2f%n", emp.calculateBonus());
            emp.processSalary(); // works because Employee implements Payable
        }
        System.out.println("----------------------------------");
        System.out.println("Done.");

        sc.close();
    }
}