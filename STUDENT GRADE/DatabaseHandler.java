import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASS = "Aman#152003";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static ArrayList<Student> loadAllStudents() {
        ArrayList<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getInt("english"), rs.getInt("hindi"), rs.getInt("marathi"),
                    rs.getInt("maths"), rs.getInt("science"), rs.getInt("social_science"), rs.getInt("geography")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
        return students;
    }

    public static void addStudent(String name, int english, int hindi, int marathi, int maths,
                                  int science, int socialScience, int geography) {
        String sql = "INSERT INTO students (name, english, hindi, marathi, maths, science, social_science, geography) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, english);
            ps.setInt(3, hindi);
            ps.setInt(4, marathi);
            ps.setInt(5, maths);
            ps.setInt(6, science);
            ps.setInt(7, socialScience);
            ps.setInt(8, geography);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    public static boolean deleteStudentById(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
        return false;
    }

    public static Student searchStudent(String name) {
        String sql = "SELECT * FROM students WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Student(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getInt("english"), rs.getInt("hindi"), rs.getInt("marathi"),
                    rs.getInt("maths"), rs.getInt("science"), rs.getInt("social_science"), rs.getInt("geography")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error searching student: " + e.getMessage());
        }
        return null;
    }
}