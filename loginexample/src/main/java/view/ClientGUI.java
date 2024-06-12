package view;

import model.Student;
import model.StudentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientGUI extends JFrame {
    private JTextField nameField;
    private JTextField ageField;
    private JTextField genderField;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ClientGUI() {
        studentDAO = new StudentDAO();
        try {
            Socket socket = new Socket("localhost", 12345);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Client");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nameField = new JTextField(15);
        ageField = new JTextField(15);
        genderField = new JTextField(15);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Gender:"));
        inputPanel.add(genderField);

        JButton addButton = new JButton("Add Information");
        JButton getAllButton = new JButton("Look for information");
        JButton seeBillsButton = new JButton("See Bills");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        getAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });

        seeBillsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seeBills();
            }
        });

        inputPanel.add(addButton);
        inputPanel.add(getAllButton);
        inputPanel.add(seeBillsButton);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Room");
        tableModel.addColumn("Age");
        tableModel.addColumn("Gender");


        studentTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(studentTable);

        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void addStudent() {
        try {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = genderField.getText();
            Student student = new Student(name, "", age, gender, 0, 0, "2000", 0);
            studentDAO.saveStudent(student);
            updateTable();
            JOptionPane.showMessageDialog(this, "Student added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        try {
            List<Student> students = studentDAO.getAllStudents();
            for (Student student : students) {
                tableModel.addRow(new Object[]{
                        student.getId(),
                        student.getName(),
                        student.getRoom(),
                        student.getAge(),
                        student.getGender(),
                        student.getElectricityBill(),
                        student.getWaterBill(),
                        student.getMoveInDate(),
                        student.getRoomRent()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve students.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seeBills() {
        try {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(ClientGUI.this, "Please select a student.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int studentID = (int) studentTable.getValueAt(selectedRow, 0);

            Student student = studentDAO.getStudentById(studentID);
            if (student == null) {
                JOptionPane.showMessageDialog(ClientGUI.this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double totalBill = student.getElectricityBill() + student.getWaterBill() + student.getRoomRent();
            String billInfo = String.format("Electricity Bill: %.2f\nWater Bill: %.2f\nRoom Rent: %.2f\nTotal Bill: %.2f",
                    student.getElectricityBill(), student.getWaterBill(), student.getRoomRent(), totalBill);

            JButton payBillsButton = new JButton("Pay Bills");
            JButton okButton = new JButton("OK");

            payBillsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    payBills(student, totalBill);
                }
            });

            Object[] options = {payBillsButton, okButton};
            JOptionPane.showOptionDialog(this, billInfo, "Bills Information",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(ClientGUI.this, "Failed to retrieve bills.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    private void payBills(Student student, double totalBill) {
//        String amountStr = JOptionPane.showInputDialog(this, "Enter the amount to pay:", "Pay Bills", JOptionPane.PLAIN_MESSAGE);
//
//        if (amountStr != null && !amountStr.isEmpty()) {
//            try {
//                double amount = Double.parseDouble(amountStr);
//
//                if (amount >= totalBill) {
//                    
//                    JOptionPane.showMessageDialog(this, "Bills paid in full.", "Success", JOptionPane.INFORMATION_MESSAGE);
//                } else {
//                    JOptionPane.showMessageDialog(this, "Insufficient amount to pay all bills.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//
//                studentDAO.updateStudent(student);
//                updateTable();
//            } catch (NumberFormatException e) {
//                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
//}
    private void payBills(Student student, double totalBill) {
        String amountStr = JOptionPane.showInputDialog(this, "Enter the amount to pay:", "Pay Bills", JOptionPane.PLAIN_MESSAGE);

        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);

                if (amount >= totalBill) {
                    student.setElectricityBill(0);
                    student.setWaterBill(0);

                    // Notify the server that bills are paid in full
                    try {
                        oos.writeObject("Bills paid in full for student with ID: " + student.getId());
                        oos.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(this, "Bills paid in full.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient amount to pay all bills.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                studentDAO.updateStudent(student);
                updateTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientGUI().setVisible(true);
            }
        });
    }
}
