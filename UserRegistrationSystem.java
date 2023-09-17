import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class UserRegistrationSystem extends JFrame {

    private List<User> users = new ArrayList<>();
    private JTextField usernameField;
    private JPasswordField passwordField;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JFrame loginFrame;
    private List<Quiz> sampleQuizzes; // List of sample quizzes for students

    public UserRegistrationSystem() {
        initializeUI();
        // Create some sample quizzes for students
        sampleQuizzes = new ArrayList<>();
        sampleQuizzes.add(new Quiz("Math Quiz", 30, 70));
        sampleQuizzes.add(new Quiz("Science Quiz", 45, 80));
    }

    private void initializeUI() {
        setTitle("User Registration and Login System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel registrationPanel = createRegistrationPanel();

        cardPanel.add(registrationPanel, "Registration");
        add(cardPanel);

        setVisible(true);
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton registerButton = new JButton("Register");
        panel.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }

                users.add(new User(username, password));

                // JOptionPane.showMessageDialog(null, "Registration successful!");
                dispose();
                openLoginFrame();
            }
        });

        return panel;
    }

    private void openLoginFrame() {
        loginFrame = new JFrame("Quiz Login Page");
        loginFrame.setBounds(100, 100, 650, 350);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel lv1;
        JButton b1, b2, b3;

        Container c;
        c = loginFrame.getContentPane();
        c.setLayout(null);

        lv1 = new JLabel("Select Your Designation ");
        lv1.setBounds(180, 20, 300, 40);
        lv1.setFont(new Font("Arial", Font.BOLD, 25));
        c.add(lv1);

        b1 = new JButton("Admin");
        b1.setBounds(200, 70, 200, 40);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (verifyCredentials(username, password)) {
                    // Code to open the AdminLoginForm class.
                    dispose();
                    AdminLoginForm adminLoginForm = new AdminLoginForm();
                    adminLoginForm.setVisible(true);
                    adminLoginForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close to go back
                    adminLoginForm.setBounds(100, 50, 600, 400);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            }
        });
        c.add(b1);

        b2 = new JButton("Instructor ");
        b2.setBounds(200, 140, 200, 40);
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (verifyCredentials(username, password)) {
                    // Code to open the TeacherLoginForm class.
                    dispose();
                    TeacherLoginForm teacherLoginForm = new TeacherLoginForm();
                    teacherLoginForm.setVisible(true);
                    teacherLoginForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close to go back
                    teacherLoginForm.setBounds(100, 50, 600, 400);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            }
        });
        c.add(b2);

        b3 = new JButton("Student");
        b3.setBounds(200, 210, 200, 40);
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (verifyCredentials(username, password)) {
                    // Code to open the StudentLoginForm class.
                    dispose();
                    StudentLoginForm studentLoginForm = new StudentLoginForm(); // Pass sample quizzes
                    studentLoginForm.addLoginSuccessListener(new LoginSuccessListener() {
                        public void onLoginSuccess(LoginSuccessEvent event) {
                            // Start the StudentDashboard upon successful student login
                           // openStudentDashboard();
                        }
                    });
                    studentLoginForm.setVisible(true);
                    studentLoginForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close to go back
                    studentLoginForm.setBounds(100, 50, 600, 400);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            }
        });
        c.add(b3);

        loginFrame.setVisible(true);
    }

    private boolean verifyCredentials(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private class User {
        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    private class Quiz {
        private String name;
        private int timeLimit;
        private int passingScore;

        public Quiz(String name, int timeLimit, int passingScore) {
            this.name = name;
            this.timeLimit = timeLimit;
            this.passingScore = passingScore;
        }

        public String getName() {
            return name;
        }

        public int getTimeLimit() {
            return timeLimit;
        }

        public int getPassingScore() {
            return passingScore;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserRegistrationSystem system = new UserRegistrationSystem();
        });
    }
}
