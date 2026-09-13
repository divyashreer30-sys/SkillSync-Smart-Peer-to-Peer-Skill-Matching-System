import java.util.ArrayList;
import java.util.Scanner;

public class SkillSync {

    // Student class
    static class Student {
        int id;
        String name;
        int age;
        String department;
        String skillKnown;
        String skillWanted;
        double experience;
        String learningMode;

        Student(int id, String name, int age, String department,
                String skillKnown, String skillWanted,
                double experience, String learningMode) {

            this.id = id;
            this.name = name;
            this.age = age;
            this.department = department;
            this.skillKnown = skillKnown;
            this.skillWanted = skillWanted;
            this.experience = experience;
            this.learningMode = learningMode;
        }
    }

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Student> students = new ArrayList<>();
    static int nextId = 101;

    public static void main(String[] args) {

        // Sample students for demonstration/testing.
        // You can remove this method call if you want an empty system.
        addSampleStudents();

        int choice;

        do {
            displayMenu();
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    registerStudent();
                    break;

                case 2:
                    viewStudentProfile();
                    break;

                case 3:
                    findSkillMatch();
                    break;

                case 4:
                    viewAllStudents();
                    break;

                case 5:
                    System.out.println("\n========================================");
                    System.out.println("Thank you for using SkillSync!");
                    System.out.println("Keep Learning. Keep Sharing.!");
                    System.out.println("========================================");
                    break;

                default:
                    System.out.println("\nInvalid choice! Please enter 1 to 5.");
            }

        } while (choice != 5);

        sc.close();
    }

    // Display main menu
    static void displayMenu() {
        System.out.println("\n========================================");
        System.out.println("              SkillSync");
        System.out.println(" Smart Peer-to-Peer Skill Matching System");
        System.out.println("========================================");
        System.out.println("1. Register Student");
        System.out.println("2. View Student Profile");
        System.out.println("3. Find Skill Match");
        System.out.println("4. View All Students");
        System.out.println("5. Exit");
        System.out.println("========================================");
    }

    // Register a new student
    static void registerStudent() {

        System.out.println("\n--------- STUDENT REGISTRATION ---------");

        String name = readNonEmptyString("Enter Student Name: ");
        int age = readPositiveInt("Enter Age: ");
        String department = readNonEmptyString("Enter Department: ");
        String skillKnown = readNonEmptyString("Enter Skill You Know: ");
        String skillWanted = readNonEmptyString("Enter Skill You Want to Learn: ");
        double experience = readNonNegativeDouble("Enter Experience (in years): ");
        String learningMode = readLearningMode();

        Student student = new Student(
                nextId,
                name,
                age,
                department,
                skillKnown,
                skillWanted,
                experience,
                learningMode
        );

        students.add(student);

        System.out.println("\n========================================");
        System.out.println("Student registered successfully!");
        System.out.println("Student ID: " + nextId);
        System.out.println("========================================");

        nextId++;
    }

    // View one student profile
    static void viewStudentProfile() {

        if (students.isEmpty()) {
            System.out.println("\nNo students registered yet.");
            return;
        }

        System.out.println("\n--------- VIEW STUDENT PROFILE ---------");

        int id = readInt("Enter Student ID: ");
        Student student = findStudentById(id);

        if (student == null) {
            System.out.println("\nStudent ID not found!");
            return;
        }

        displayFullProfile(student);
    }

    // Find the best skill match
    static void findSkillMatch() {

        if (students.size() < 2) {
            System.out.println("\nAt least 2 students are required to find a match.");
            return;
        }

        System.out.println("\n--------- FIND SKILL MATCH ---------");

        int id = readInt("Enter Your Student ID: ");
        Student current = findStudentById(id);

        if (current == null) {
            System.out.println("\nStudent ID not found!");
            return;
        }

        Student bestMatch = null;
        int bestScore = 0;

        for (Student other : students) {

            if (other.id == current.id) {
                continue;
            }

            int score = calculateMatchScore(current, other);

            if (score > bestScore) {
                bestScore = score;
                bestMatch = other;
            }
        }

        if (bestMatch == null || bestScore == 0) {
            System.out.println("\n========================================");
            System.out.println("          NO SUITABLE MATCH");
            System.out.println("========================================");
            System.out.println("No student currently matches your skill");
            System.out.println("requirements.");
            System.out.println("Try again after more students register.");
            return;
        }

        displayMatch(current, bestMatch, bestScore);
    }

    // Calculate matching score
    static int calculateMatchScore(Student current, Student other) {

        int score = 0;

        String currentWanted = normalize(current.skillWanted);
        String currentKnown = normalize(current.skillKnown);

        String otherKnown = normalize(other.skillKnown);
        String otherWanted = normalize(other.skillWanted);

        // Main condition:
        // Other student knows the skill current student wants.
        if (isSkillMatch(currentWanted, otherKnown)) {
            score += 60;
        }

        // Mutual exchange:
        // Current student knows the skill other student wants.
        if (isSkillMatch(otherWanted, currentKnown)) {
            score += 30;
        }

        // Same learning mode
        if (normalize(current.learningMode)
                .equals(normalize(other.learningMode))) {
            score += 5;
        }

        // Same department
        if (normalize(current.department)
                .equals(normalize(other.department))) {
            score += 5;
        }

        return Math.min(score, 100);
    }

    // Display matching result
    static void displayMatch(Student current, Student match, int score) {

        System.out.println("\n========================================");
        System.out.println("          SKILL MATCH FOUND!");
        System.out.println("========================================");

        System.out.println("\nYour Profile:");
        System.out.println("Name          : " + current.name);
        System.out.println("Department    : " + current.department);
        System.out.println("You Know      : " + current.skillKnown);
        System.out.println("You Want      : " + current.skillWanted);

        System.out.println("\nBest Matching Peer:");
        System.out.println("Name          : " + match.name);
        System.out.println("Department    : " + match.department);
        System.out.println("They Know     : " + match.skillKnown);
        System.out.println("They Want     : " + match.skillWanted);
        System.out.println("Experience    : " + match.experience + " years");
        System.out.println("Learning Mode : " + match.learningMode);

        System.out.println("\nMatch Score   : " + score + "%");

        System.out.println("\nWhy this match?");
        boolean firstReason = true;

        if (isSkillMatch(current.skillWanted, match.skillKnown)) {
            System.out.println("✓ " + match.name + " knows "
                    + match.skillKnown + " which you want to learn.");
            firstReason = false;
        }

        if (isSkillMatch(match.skillWanted, current.skillKnown)) {
            System.out.println("✓ You know " + current.skillKnown
                    + " which " + match.name + " wants to learn.");
            firstReason = false;
        }

        if (normalize(current.learningMode)
                .equals(normalize(match.learningMode))) {
            System.out.println("✓ Both prefer " + current.learningMode + " learning.");
            firstReason = false;
        }

        if (normalize(current.department)
                .equals(normalize(match.department))) {
            System.out.println("✓ Both are from the same department.");
            firstReason = false;
        }

        if (firstReason) {
            System.out.println("✓ Skill similarity found.");
        }

        System.out.println("\nRecommendation:");
        if (score >= 90) {
            System.out.println("Excellent match! You can learn from each other.");
        } else if (score >= 60) {
            System.out.println("Good match! This peer can help you learn your required skill.");
        } else {
            System.out.println("Possible match. You can connect and discuss your skills.");
        }

        System.out.println("========================================");
    }

    // View all registered students
    static void viewAllStudents() {

        if (students.isEmpty()) {
            System.out.println("\nNo students registered yet.");
            return;
        }

        System.out.println("\n---------------- ALL STUDENTS ----------------");

        System.out.printf("%-6s %-15s %-15s %-18s %-18s%n",
                "ID", "Name", "Department", "Skill Known", "Skill Wanted");

        System.out.println("--------------------------------------------------------------------------");

        for (Student student : students) {
            System.out.printf("%-6d %-15s %-15s %-18s %-18s%n",
                    student.id,
                    student.name,
                    student.department,
                    student.skillKnown,
                    student.skillWanted);
        }

        System.out.println("--------------------------------------------------------------------------");
        System.out.println("Total Students: " + students.size());
    }

    // Display full profile
    static void displayFullProfile(Student student) {

        System.out.println("\n--------- STUDENT PROFILE ---------");
        System.out.println("Student ID        : " + student.id);
        System.out.println("Name              : " + student.name);
        System.out.println("Age               : " + student.age);
        System.out.println("Department        : " + student.department);
        System.out.println("Skill Known       : " + student.skillKnown);
        System.out.println("Skill Wanted      : " + student.skillWanted);
        System.out.println("Experience        : " + student.experience + " years");
        System.out.println("Learning Mode     : " + student.learningMode);
        System.out.println("-----------------------------------");
    }

    // Find student using ID
    static Student findStudentById(int id) {

        for (Student student : students) {
            if (student.id == id) {
                return student;
            }
        }

        return null;
    }

    // Check whether two skill names match
    static boolean isSkillMatch(String skill1, String skill2) {

        String a = normalize(skill1);
        String b = normalize(skill2);

        return a.equals(b);
    }

    // Normalize input to make matching case-insensitive
    static String normalize(String text) {
        return text.trim().toLowerCase();
    }

    // Read non-empty text
    static String readNonEmptyString(String message) {

        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    // Read integer safely
    static int readInt(String message) {

        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Read positive integer
    static int readPositiveInt(String message) {

        while (true) {
            int value = readInt(message);

            if (value > 0) {
                return value;
            }

            System.out.println("Please enter a value greater than 0.");
        }
    }

    // Read non-negative double
    static double readNonNegativeDouble(String message) {

        while (true) {
            System.out.print(message);
            String input = sc.nextLine().trim();

            try {
                double value = Double.parseDouble(input);

                if (value >= 0) {
                    return value;
                }

                System.out.println("Experience cannot be negative.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Read learning mode
    static String readLearningMode() {

        while (true) {
            System.out.print("Enter Preferred Learning Mode (Online/Offline): ");
            String mode = sc.nextLine().trim();

            if (mode.equalsIgnoreCase("Online")
                    || mode.equalsIgnoreCase("Offline")) {

                return capitalizeFirstLetter(mode);
            }

            System.out.println("Please enter only Online or Offline.");
        }
    }

    // Capitalize first letter
    static String capitalizeFirstLetter(String text) {

        if (text == null || text.isEmpty()) {
            return text;
        }

        return text.substring(0, 1).toUpperCase()
                + text.substring(1).toLowerCase();
    }

    // Sample data
    static void addSampleStudents() {

        students.add(new Student(
                nextId++, "Priya", 21, "CSE",
                "Python", "Java", 2.0, "Online"
        ));

        students.add(new Student(
                nextId++, "Anu", 21, "CSE",
                "Java", "Python", 1.5, "Online"
        ));

        students.add(new Student(
                nextId++, "Kavi", 22, "IT",
                "SQL", "Java", 2.0, "Offline"
        ));
    }
}
