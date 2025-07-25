import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class StudentGradeGUI extends JFrame {
    private StudentManager manager;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel averageLabel, highLabel, lowLabel;
    private JTextField nameField, englishField, hindiField, marathiField, mathsField, scienceField, socialField, geographyField, searchField;

    public StudentGradeGUI() {
        manager = new StudentManager();
        setTitle("Student Grade Management");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        updateTable();
        updateSummaryLabels();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Name", "English", "Hindi", "Marathi", "Maths", "Science", "Social Science", "Geography", "Total", "Average"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        panel.add(tableScroll, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(5, 5, 10, 5));
        nameField = new JTextField(); englishField = new JTextField();
        hindiField = new JTextField(); marathiField = new JTextField();
        mathsField = new JTextField(); scienceField = new JTextField();
        socialField = new JTextField(); geographyField = new JTextField();
        JButton addButton = new JButton("Add");

        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("English:")); inputPanel.add(englishField);
        inputPanel.add(new JLabel("Hindi:")); inputPanel.add(hindiField);
        inputPanel.add(new JLabel("Marathi:")); inputPanel.add(marathiField);
        inputPanel.add(new JLabel("Maths:")); inputPanel.add(mathsField);
        inputPanel.add(new JLabel("Science:")); inputPanel.add(scienceField);
        inputPanel.add(new JLabel("Social Science:")); inputPanel.add(socialField);
        inputPanel.add(new JLabel("Geography:")); inputPanel.add(geographyField);
        inputPanel.add(addButton);

        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton deleteButton = new JButton("Delete Selected");
        JButton exportExcel = new JButton("Export to Excel");
        JButton exportPdf = new JButton("Export to PDF");
        inputPanel.add(new JLabel("Search Name:")); inputPanel.add(searchField);
        inputPanel.add(searchButton); inputPanel.add(deleteButton);
        inputPanel.add(exportExcel); inputPanel.add(exportPdf);

        panel.add(inputPanel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 3));
        averageLabel = new JLabel("Average: ");
        highLabel = new JLabel("Highest: ");
        lowLabel = new JLabel("Lowest: ");
        summaryPanel.add(averageLabel);
        summaryPanel.add(highLabel);
        summaryPanel.add(lowLabel);
        panel.add(summaryPanel, BorderLayout.SOUTH);

        JPanel sortPanel = new JPanel();
        String[] sortOptions = {"Sort by Name", "Sort by Score Desc"};
        JComboBox<String> sortBox = new JComboBox<>(sortOptions);
        JButton sortButton = new JButton("Sort");
        sortPanel.add(sortBox); sortPanel.add(sortButton);
        panel.add(sortPanel, BorderLayout.WEST);

        addButton.addActionListener(_ -> addStudent());
        searchButton.addActionListener(_ -> searchStudent());
        deleteButton.addActionListener(_ -> deleteStudent());
        sortButton.addActionListener(_ -> {
            if (sortBox.getSelectedIndex() == 0) manager.sortByName();
            else manager.sortByScoreDescending();
            updateTable();
        });
        exportExcel.addActionListener(_ -> exportToExcel());
        exportPdf.addActionListener(_ -> exportToPDF());

        add(panel);
    }

    private void addStudent() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
            int[] scores = new int[] {
                Integer.parseInt(englishField.getText().trim()),
                Integer.parseInt(hindiField.getText().trim()),
                Integer.parseInt(marathiField.getText().trim()),
                Integer.parseInt(mathsField.getText().trim()),
                Integer.parseInt(scienceField.getText().trim()),
                Integer.parseInt(socialField.getText().trim()),
                Integer.parseInt(geographyField.getText().trim())
            };
            for (int score : scores) {
                if (score < 0 || score > 100) throw new IllegalArgumentException("Scores must be 0-100.");
            }
            manager.addStudent(name, scores[0], scores[1], scores[2], scores[3], scores[4], scores[5], scores[6]);
            updateTable();
            updateSummaryLabels();
            clearFields();
        } catch (NumberFormatException e) {
            showMessage("Please enter valid numeric scores.");
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    private void exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");
            Row header = sheet.createRow(0);
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                header.createCell(i).setCellValue(tableModel.getColumnName(i));
            }
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    row.createCell(j).setCellValue(tableModel.getValueAt(i, j).toString());
                }
            }
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            FileOutputStream out = new FileOutputStream("students_" + timestamp + ".xlsx");
            workbook.write(out);
            out.close();
            showMessage("Excel exported successfully.");
        } catch (Exception e) {
            showMessage("Failed to export Excel: " + e.getMessage());
        }
    }

    private void exportToPDF() {
        try {
            Document doc = new Document();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            PdfWriter.getInstance(doc, new FileOutputStream("students_" + timestamp + ".pdf"));
            doc.open();
            PdfPTable pdfTable = new PdfPTable(tableModel.getColumnCount());
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                pdfTable.addCell(new PdfPCell(new Phrase(tableModel.getColumnName(i))));
            }
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    pdfTable.addCell(tableModel.getValueAt(row, col).toString());
                }
            }
            doc.add(pdfTable);
            doc.close();
            showMessage("PDF exported successfully.");
        } catch (Exception e) {
            showMessage("Failed to export PDF: " + e.getMessage());
        }
    }

    private void searchStudent() {
        String name = searchField.getText().trim();
        if (name.isEmpty()) {
            showMessage("Enter name to search.");
            return;
        }
        Student s = manager.searchStudent(name);
        if (s != null) {
            showMessage("Found: " + s.getName() + " Average Score: " + s.getAverageScore());
        } else {
            showMessage("Student not found.");
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Select a student row to delete.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        if (manager.deleteStudentById(id)) {
            updateTable();
            updateSummaryLabels();
            showMessage("Student deleted.");
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Student s : manager.getStudents()) {
            tableModel.addRow(new Object[] {
                s.getId(), s.getName(), s.getEnglish(), s.getHindi(), s.getMarathi(),
                s.getMaths(), s.getScience(), s.getSocialScience(), s.getGeography(),
                s.getTotalScore(), String.format("%.2f", s.getAverageScore())
            });
        }
    }

    private void updateSummaryLabels() {
        averageLabel.setText("Average: " + String.format("%.2f", manager.calculateAverage()));
        highLabel.setText("Highest: " + String.format("%.2f", manager.getHighestScore()));
        lowLabel.setText("Lowest: " + String.format("%.2f", manager.getLowestScore()));
    }

    private void clearFields() {
        nameField.setText(""); englishField.setText(""); hindiField.setText("");
        marathiField.setText(""); mathsField.setText(""); scienceField.setText("");
        socialField.setText(""); geographyField.setText("");
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGradeGUI().setVisible(true));
    }
}
