package server;

import model.Student;
import model.StudentDAO;
import model.User;
import model.UserDAO;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private StudentDAO studentDAO;
    private UserDAO userDAO;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.studentDAO = new StudentDAO();
        this.userDAO = new UserDAO();
    }

    public void run() {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

            String command = (String) ois.readObject();

            if ("register".equals(command)) {
                User user = (User) ois.readObject();
                userDAO.saveUser(user);
                oos.writeObject("User registered successfully");
            } else if ("login".equals(command)) {
                String username = (String) ois.readObject();
                String password = (String) ois.readObject();
                User user = userDAO.getUserByUsername(username);
                if (user != null && user.getPassword().equals(password)) {
                    oos.writeObject("Login successful");
                } else {
                    oos.writeObject("Invalid username or password");
                }
            } else if ("getAllStudents".equals(command)) {
                List<Student> students = studentDAO.getAllStudents();
                oos.writeObject(students);
            } else if ("addStudent".equals(command)) {
                Student student = (Student) ois.readObject();
                studentDAO.saveStudent(student);
                oos.writeObject("Student added successfully");
            } else if ("updateStudent".equals(command)) {
                Student student = (Student) ois.readObject();
                studentDAO.updateStudent(student);
                oos.writeObject("Student updated successfully");
            } else if ("deleteStudent".equals(command)) {
                int studentId = (Integer) ois.readObject();
                studentDAO.deleteStudent(studentId);
                oos.writeObject("Student deleted successfully");
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}

