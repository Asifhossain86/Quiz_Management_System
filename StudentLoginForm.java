import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.EventListener;
import java.util.List;

class LoginSuccessEvent extends EventObject {
    public LoginSuccessEvent(Object source) {
        super(source);
    }
}

interface LoginSuccessListener extends EventListener {
    void onLoginSuccess(LoginSuccessEvent event);
}

public class StudentLoginForm extends JFrame {

    private Container c;
    private JLabel labelTitle, labelUsername, labelInitial, labelPassword;
    private JTextField textFieldUsername, textFieldInitial;
    private JPasswordField passwordField;
    private JButton buttonSubmit;
    private final List<LoginSuccessListener> loginSuccessListeners = new ArrayList<>();

    public StudentLoginForm() {
        initComponents();
    }

    private void initComponents() {
        c = this.getContentPane();
        c.setLayout(null);

        labelTitle = new JLabel("Student Login");
        labelTitle.setFont(new Font("Arial", Font.BOLD, 20));
        labelTitle.setBounds(200, 50, 300, 100);
        c.add(labelTitle);

        labelUsername = new JLabel("Username:");
        labelUsername.setBounds(150, 150, 100, 30);
        c.add(labelUsername);

        textFieldUsername = new JTextField();
        textFieldUsername.setBounds(250, 150, 100, 30);
        c.add(textFieldUsername);

        labelInitial = new JLabel("ID:");
        labelInitial.setBounds(150, 190, 100, 30);
        c.add(labelInitial);

        textFieldInitial = new JTextField();
        textFieldInitial.setBounds(250, 190, 100, 30);
        c.add(textFieldInitial);

        labelPassword = new JLabel("Password:");
        labelPassword.setBounds(150, 230, 100, 30);
        c.add(labelPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(250, 230, 100, 30);
        c.add(passwordField);

        buttonSubmit = new JButton("SUBMIT");
        buttonSubmit.setBounds(200, 270, 100, 20);
        buttonSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check login logic, and if login is successful, create StudentDashboard
                if (isLoginSuccessful()) {
                    // Create StudentDashboard and make it visible
                    //List<Quiz> quizzes = loadQuizzesFromFile("C:\\Users\\Admin\\Desktop\\quizname.txt");
                    StudentDashboard studentDashboard = new StudentDashboard();
                    studentDashboard.setVisible(true);

                    // Notify the listeners about login success
                    notifyLoginSuccessListeners();

                    // Close the login form
                    dispose();
                }
            }
        });
        c.add(buttonSubmit);
    }

    // Dummy login logic (replace with your actual login logic)
    private boolean isLoginSuccessful() {
        String username = textFieldUsername.getText();
        String id = textFieldInitial.getText();
        // Add your logic here to check if login is successful
        // For now, just return true
        return true;
    }

    // Add a listener for login success
    public void addLoginSuccessListener(LoginSuccessListener listener) {
        loginSuccessListeners.add(listener);
    }

    // Remove a listener for login success
    public void removeLoginSuccessListener(LoginSuccessListener listener) {
        loginSuccessListeners.remove(listener);
    }

    // Notify listeners when login is successful
    private void notifyLoginSuccessListeners() {
        LoginSuccessEvent event = new LoginSuccessEvent(this);
        for (LoginSuccessListener listener : loginSuccessListeners) {
            listener.onLoginSuccess(event);
        }
    }

    // Load quizzes from a text file
    private List<Quiz> loadQuizzesFromFile(String filePath) {
        List<Quiz> quizzes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into quiz data (modify as needed)
                String[] quizData = line.split(",");
                if (quizData.length >= 4) {
                    String quizName = quizData[0];
                    int timeLimit = Integer.parseInt(quizData[1]);
                    int passingScore = Integer.parseInt(quizData[2]);
                    // Create a Quiz object and add it to the list
                    Quiz quiz = new Quiz(quizName);
                    quizzes.add(quiz);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentLoginForm frame = new StudentLoginForm();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBounds(100, 50, 600, 400);
        });
    }
}
