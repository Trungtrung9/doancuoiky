package view;

import model.Student;
import model.StudentDAO;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ServerGUI extends JFrame {
    private JTextField idField;
    private JTextField nameField;
    private JTextField roomField;
    private JTextField ageField;
    private JTextField genderField;
    private JTextField electricityBillField;
    private JTextField waterBillField;
    private JTextField moveInDateField;
    private JTextField roomRentField;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private StudentDAO studentDAO;

    public ServerGUI() {
        studentDAO = new StudentDAO();

        setTitle("Server");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        idField = new JTextField(15);
        idField.setEditable(false); // ID field should not be editable
        nameField = new JTextField(15);
        roomField = new JTextField(15);
        ageField = new JTextField(15);
        genderField = new JTextField(15);
        electricityBillField = new JTextField(15);
        waterBillField = new JTextField(15);
        moveInDateField = new JTextField(15);
        roomRentField = new JTextField(15);

        JPanel inputPanel = new JPanel(new GridLayout(12, 2));
        inputPanel.add(new JLabel("ID:")); inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:")); inputPanel.add(nameField);
        inputPanel.add(new JLabel("Room:")); inputPanel.add(roomField);
        inputPanel.add(new JLabel("Age:")); inputPanel.add(ageField);
        inputPanel.add(new JLabel("Gender:")); inputPanel.add(genderField);
        inputPanel.add(new JLabel("Electricity Bill:")); inputPanel.add(electricityBillField);
        inputPanel.add(new JLabel("Water Bill:")); inputPanel.add(waterBillField);
        inputPanel.add(new JLabel("Move In Date:")); inputPanel.add(moveInDateField);
        inputPanel.add(new JLabel("Room Rent:")); inputPanel.add(roomRentField);

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update Student");
        JButton deleteButton = new JButton("Delete Student");
        JButton getAllButton = new JButton("Get All Students");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateStudent();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });

        getAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });

        inputPanel.add(addButton); inputPanel.add(updateButton);
        inputPanel.add(deleteButton); inputPanel.add(getAllButton);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Room");
        tableModel.addColumn("Age");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Electricity Bill");
        tableModel.addColumn("Water Bill");
        tableModel.addColumn("Move In Date");
        tableModel.addColumn("Room Rent");

        studentTable = new JTable(tableModel);
        scrollPane = new JScrollPane(studentTable);

        studentTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    int selectedRow = studentTable.getSelectedRow();
                    if (selectedRow != -1) {
                        idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                        nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                        roomField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                        ageField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                        genderField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                        electricityBillField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                        waterBillField.setText(tableModel.getValueAt(selectedRow, 6).toString());
                        moveInDateField.setText(tableModel.getValueAt(selectedRow, 7).toString());
                        roomRentField.setText(tableModel.getValueAt(selectedRow, 8).toString());
                    }
                }
            }
        });

        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        roomField.setText("");
        ageField.setText("");
        genderField.setText("");
        electricityBillField.setText("");
        waterBillField.setText("");
        moveInDateField.setText("");
        roomRentField.setText("");
    }

    private void addStudent() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String name = nameField.getText();
                    String room = roomField.getText();
                    int age = Integer.parseInt(ageField.getText());
                    String gender = genderField.getText();
                    double electricityBill = Double.parseDouble(electricityBillField.getText());
                    double waterBill = Double.parseDouble(waterBillField.getText());
                    String moveInDate = moveInDateField.getText();
                    double roomRent = Double.parseDouble(roomRentField.getText());
                    Student student = new Student(name, room, age, gender, electricityBill, waterBill, moveInDate, roomRent);
                    studentDAO.saveStudent(student);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new Exception("Failed to add student");
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    updateTable();
                    clearFields();
                    JOptionPane.showMessageDialog(ServerGUI.this, "Student added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ServerGUI.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateStudent() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String name = nameField.getText();
                    String room = roomField.getText();
                    int age = Integer.parseInt(ageField.getText());
                    String gender = genderField.getText();
                    double electricityBill = Double.parseDouble(electricityBillField.getText());
                    double waterBill = Double.parseDouble(waterBillField.getText());
                    String moveInDate = moveInDateField.getText();
                    double roomRent = Double.parseDouble(roomRentField.getText());
                    Student student = new Student(name, room, age, gender, electricityBill, waterBill, moveInDate, roomRent);
                    student.setId(id);
                    studentDAO.updateStudent(student);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new Exception("Failed to update student");
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    updateTable();
                    clearFields();
                    JOptionPane.showMessageDialog(ServerGUI.this, "Student updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ServerGUI.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void deleteStudent() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    int id = Integer.parseInt(idField.getText());
                    studentDAO.deleteStudent(id);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new Exception("Failed to delete student");
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    updateTable();
                    clearFields();
                    JOptionPane.showMessageDialog(ServerGUI.this, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ServerGUI.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateTable() {
        new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                return studentDAO.getAllStudents();
            }

            @Override
            protected void done() {
                try {
                    List<Student> students = get();
                    tableModel.setRowCount(0); // Clear existing rows
                    for (Student student : students) {
                        Object[] rowData = {
                                student.getId(),
                                student.getName(),
                                student.getRoom(),
                                student.getAge(),
                                student.getGender(),
                                student.getElectricityBill(),
                                student.getWaterBill(),
                                student.getMoveInDate(),
                                student.getRoomRent()
                        };
                        tableModel.addRow(rowData);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ServerGUI.this, "Failed to retrieve students.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ServerGUI().setVisible(true);
            }
        });
    }
}
