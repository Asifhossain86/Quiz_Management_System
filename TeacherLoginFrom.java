import javax.swing.*;
import java.awt.*;

public class TeacherLoginForm extends JFrame {

    private Container c;
    private JLabel labelTitle, labelUsername, labelInitial, labelPassword;
    private JTextField textFieldUsername, textFieldInitial;
    private JPasswordField passwordField;
    private JButton buttonSubmit;

    public TeacherLoginForm() {
        initComponents();
    }

    private void initComponents() {
        c = this.getContentPane();
        c.setLayout(null);

        labelTitle = new JLabel("Teacher Login");
        labelTitle.setFont(new Font("Arial", Font.BOLD, 20));
        labelTitle.setBounds(200, 50, 300, 100);
        c.add(labelTitle);

        labelUsername = new JLabel("Username:");
        labelUsername.setBounds(150, 150, 100, 30);
        c.add(labelUsername);

        textFieldUsername = new JTextField();
        textFieldUsername.setBounds(250, 150, 100, 30);
        c.add(textFieldUsername);

        labelInitial = new JLabel("Initial:");
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
        c.add(buttonSubmit);
    }

    public static void main(String[] args) {
        TeacherLoginForm frame = new TeacherLoginForm();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 50, 600, 400);
    }
}
