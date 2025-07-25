import java.util.*;

public class StudentManager {
    private List<Student> students = new ArrayList<>();
    private int nextId = 1;

    public void addStudent(String name, int english, int hindi, int marathi, int maths, int science, int social, int geography) {
        Student s = new Student(nextId++, name, english, hindi, marathi, maths, science, social, geography);
        students.add(s);
    }

    public boolean deleteStudentById(int id) {
        return students.removeIf(s -> s.getId() == id);
    }

    public Student searchStudent(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    public List<Student> getStudents() {
        return students;
    }

    public double calculateAverage() {
        return students.stream().mapToDouble(Student::getAverageScore).average().orElse(0.0);
    }

    public double getHighestScore() {
        return students.stream().mapToDouble(Student::getAverageScore).max().orElse(0.0);
    }

    public double getLowestScore() {
        return students.stream().mapToDouble(Student::getAverageScore).min().orElse(0.0);
    }

    public void sortByName() {
        students.sort(Comparator.comparing(Student::getName));
    }

    public void sortByScoreDescending() {
        students.sort((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()));
    }
}
