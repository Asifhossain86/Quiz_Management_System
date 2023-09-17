import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class StudentDashboard extends JFrame {//quizzes

    private JButton viewQuizzesButton;
    private JButton takeQuizButton;
    private JButton viewScoresButton;
    private JButton reviewCompletedQuizzesButton;
    private JButton loadTextButton;

    private List<Quiz> availableQuizzes = new ArrayList<>();
    private Map<String, Double> quizScores = new HashMap<>();
    private Map<String, List<String>> quizResponses = new HashMap<>();

    private String selectedQuizName;
    private int currentQuestionIndex = 0;
    private List<String> currentQuizQuestions;
    private Map<String, List<String>> currentMCQOptions;
    private List<String> studentResponses = new ArrayList<>();

    private JTextArea questionTextArea;
    private ButtonGroup optionButtonGroup;
    private JRadioButton[] optionRadioButtons;
    private JButton nextButton;

    public StudentDashboard() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Student Dashboard");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        viewQuizzesButton = new JButton("View Available Quizzes");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(viewQuizzesButton, constraints);

        takeQuizButton = new JButton("Take Quiz");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(takeQuizButton, constraints);

        viewScoresButton = new JButton("View Scores");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(viewScoresButton, constraints);

        reviewCompletedQuizzesButton = new JButton("Review Completed Quizzes");
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(reviewCompletedQuizzesButton, constraints);

        loadTextButton = new JButton("Load Text");
        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(loadTextButton, constraints);

        viewQuizzesButton.addActionListener(this::viewQuizzesButtonClicked);
        takeQuizButton.addActionListener(this::takeQuizButtonClicked);
        viewScoresButton.addActionListener(this::viewScoresButtonClicked);
        reviewCompletedQuizzesButton.addActionListener(this::reviewCompletedQuizzesButtonClicked);
        loadTextButton.addActionListener(this::loadTextButtonClicked);

        add(panel);
    }

    private void viewQuizzesButtonClicked(ActionEvent e) {
        // Read available quizzes from a text file
        availableQuizzes.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Admin\\Desktop\\Question.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String quizName = parts[0];
                    String quizDescription = parts[1];
                    String dueDateStr = parts[2];
                    Date dueDate = parseDate(dueDateStr);

                    availableQuizzes.add(new Quiz(quizName, quizDescription, dueDate));
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading the quiz data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!availableQuizzes.isEmpty()) {
            StringBuilder quizzesList = new StringBuilder();
            for (Quiz quiz : availableQuizzes) {
                quizzesList.append("Quiz Name: ").append(quiz.getName())
                        .append("\nDescription: ").append(quiz.getDescription())
                        .append("\nDue Date: ").append(formatDate(quiz.getDueDate()))
                        .append("\n\n");
            }
            JOptionPane.showMessageDialog(this, "Available Quizzes:\n" + quizzesList.toString());
        } else {
            JOptionPane.showMessageDialog(this, "No available quizzes found.");
        }
    }

    private void takeQuizButtonClicked(ActionEvent e) {
        // Read available quiz names from the file
        List<String> quizNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Admin\\Desktop\\quizname.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    String quizName = parts[0];
                    quizNames.add(quizName);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading the quiz data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!quizNames.isEmpty()) {
            selectedQuizName = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a quiz to take:",
                    "Take Quiz",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    quizNames.toArray(),
                    null
            );

            if (selectedQuizName != null) {
                // Read quiz questions and options for the selected quiz from the "Mcq.txt" file
                currentQuizQuestions = new ArrayList<>();
                currentMCQOptions = new HashMap<>();

                try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Admin\\Desktop\\Mcq.txt"))) {
                    String line;
                    String currentQuiz = "";
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 5) {
                            String quizName = parts[0];
                            String quizQuestion = parts[1];
                            String[] options = Arrays.copyOfRange(parts, 2, 6);

                            if (quizName.equals(selectedQuizName)) {
                                // Add the question to the current quiz
                                currentQuizQuestions.add(quizQuestion);

                                // Add the options to the current question
                                currentMCQOptions.put(quizQuestion, Arrays.asList(options));
                            }
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading quiz questions and options: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                // Start taking the quiz
                takeQuiz();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No available quizzes found.");
        }
    }

 // Inside the takeQuiz() method
private void takeQuiz() {
    if (currentQuestionIndex < currentQuizQuestions.size()) {
      JFrame quizFrame = new JFrame("Quiz: " + selectedQuizName);
            quizFrame.setSize(400, 400);
            quizFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            quizFrame.setLocationRelativeTo(this);

            JPanel quizPanel = new JPanel(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(10, 10, 10, 10);

            String currentQuestion = currentQuizQuestions.get(currentQuestionIndex);

            questionTextArea = new JTextArea(currentQuestion);
            questionTextArea.setEditable(false);
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            quizPanel.add(questionTextArea, constraints);

            optionButtonGroup = new ButtonGroup();
            List<String> options = currentMCQOptions.get(currentQuestion);
            optionRadioButtons = new JRadioButton[options.size()];

            for (int i = 0; i < options.size(); i++) {
                optionRadioButtons[i] = new JRadioButton(options.get(i));
                optionRadioButtons[i].setActionCommand(options.get(i));
                optionButtonGroup.add(optionRadioButtons[i]);
                constraints.gridx = 0;
                constraints.gridy = i + 1;
                constraints.gridwidth = 2; // Adjust the grid width for options
                quizPanel.add(optionRadioButtons[i], constraints);
            }

            nextButton = new JButton("Next");
            constraints.gridx = 0;
            constraints.gridy = options.size() + 1;
            constraints.gridwidth = 2;
            quizPanel.add(nextButton, constraints);

            nextButton.addActionListener(this::nextButtonClicked);

            quizFrame.add(quizPanel);
            quizFrame.setVisible(true);
        }
     else {
        JOptionPane.showMessageDialog(this, "Quiz Completed!");

        // Calculate the score for the completed quiz
        double quizScore = calculateQuizScore(selectedQuizName);

        // Show the score summary dialog
        showScoreSummary(quizScore);
    }
}

// Calculate the score for the completed quiz
private double calculateQuizScore(String quizName) {
    List<String> correctAnswers = loadCorrectAnswers(quizName);
    double score = 0.0;

    // Compare student responses with correct answers
    for (int i = 0; i < studentResponses.size(); i++) {
        if (i < correctAnswers.size() && studentResponses.get(i).equals(correctAnswers.get(i))) {
            score += 1.0; // Increase the score for each correct answer
        }
    }

    // Update the overall quiz score
    quizScores.put(quizName, score);

    return score;
}

// Load correct answers for a quiz from the answer file
private List<String> loadCorrectAnswers(String quizName) {
    List<String> correctAnswers = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Admin\\Desktop\\Answer.txt"))) {
        String line;
        boolean isReading = false;

        while ((line = reader.readLine()) != null) {
            if (line.equals(quizName)) {
                isReading = true;
                continue;
            }

            if (isReading && !line.isEmpty()) {
                correctAnswers.add(line);
            } else {
                break; // Stop reading when an empty line is encountered
            }
        }
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error reading the answer file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    return correctAnswers;
}

// Show a score summary dialog
private void showScoreSummary(double quizScore) {
    JOptionPane.showMessageDialog(this, "Your Score for Quiz " + selectedQuizName + ": " + quizScore);
}


    private void nextButtonClicked(ActionEvent e) {
        // Get the selected option
        String selectedOption = optionButtonGroup.getSelection().getActionCommand();
        studentResponses.add(selectedOption); // Store the student's response

        // Get the correct answer from the answer file
        String correctAnswer = getCorrectAnswerForQuestion(currentQuizQuestions.get(currentQuestionIndex));

        // Compare the student's answer with the correct answer
        boolean isCorrect = selectedOption.equals(correctAnswer);

        // Update the score for the current quiz
        double currentScore = quizScores.getOrDefault(selectedQuizName, 0.0);
        if (isCorrect) {
            currentScore += 1.0; // You can adjust the scoring logic as needed
        }
        quizScores.put(selectedQuizName, currentScore);

        // Move to the next question
        currentQuestionIndex++;

        // Close the current quiz frame
        ((Window) SwingUtilities.getRoot((Component) e.getSource())).dispose();

        // Start the next question or finish the quiz
        takeQuiz();
    }

    // Helper method to get the correct answer for a question
    private String getCorrectAnswerForQuestion(String question) {
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Admin\\Desktop\\Answer.txt"))) {
            String line;
            String currentQuiz = null;
            while ((line = reader.readLine()) != null) {
                if (line.equals(question)) {
                    currentQuiz = line;
                    break;
                }
            }
            if (currentQuiz != null) {
                line = reader.readLine(); // Read the correct answer
                return line;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading the answer file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null; // Return null if the correct answer is not found
    }

    private void viewScoresButtonClicked(ActionEvent e) {
        StringBuilder scoresList = new StringBuilder();
        double totalScore = 0.0;

        for (Quiz quiz : availableQuizzes) {
            Double score = quizScores.get(quiz.getName());
            if (score != null) {
                scoresList.append("Quiz: ").append(quiz.getName())
                        .append("\nScore: ").append(score).append("\n\n");
                totalScore += score;
            }
        }

        double averageScore = totalScore / availableQuizzes.size();
        scoresList.append("Overall Average Score: ").append(averageScore);

        JOptionPane.showMessageDialog(this, "Your Scores:\n" + scoresList.toString());
    }

    private void reviewCompletedQuizzesButtonClicked(ActionEvent e) {
        StringBuilder reviewList = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : quizResponses.entrySet()) {
            String quizName = entry.getKey();
            List<String> responses = entry.getValue();

            reviewList.append("Quiz: ").append(quizName).append("\n");
            for (int i = 0; i < responses.size(); i++) {
                reviewList.append("Question ").append(i + 1).append(": Your Answer - ").append(responses.get(i)).append("\n");
                // In a real application, you'd also show correct answers here
            }
            reviewList.append("\n");
        }

        if (reviewList.length() > 0) {
            JOptionPane.showMessageDialog(this, "Review Completed Quizzes:\n" + reviewList.toString());
        } else {
            JOptionPane.showMessageDialog(this, "You have not completed any quizzes yet.");
        }
    }

    private void loadTextButtonClicked(ActionEvent e) {//startQuiz
        // Specify the file path here
        String selectedFile = "C:\\Users\\Admin\\Desktop\\nabil.txt";

        try {
            String fileContents = readFile(selectedFile);

            // Create a JTextArea to display the text
            JTextArea textArea = new JTextArea(10, 40);
            textArea.setText(fileContents);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setCaretPosition(0);
            textArea.setEditable(false);

            // Create a JScrollPane to add scroll bars if necessary
            JScrollPane scrollPane = new JScrollPane(textArea);

            // Create a dialog to display the text
            JDialog textDialog = new JDialog(this, "Text File Viewer", true);
            textDialog.add(scrollPane);
            textDialog.pack();
            textDialog.setLocationRelativeTo(this);
            textDialog.setVisible(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading the file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to read a text file and return its contents as a string
    private String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private Date parseDate(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (Exception e) {
            return new Date();
        }
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private double simulateQuizScoring(java.util.List<String> responses) { // Use java.util.List here
        // Simulate quiz scoring based on responses
        // In a real application, you'd evaluate answers and calculate a score
        return Math.random() * 100; // Random score between 0 and 100
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentDashboard studentDashboard = new StudentDashboard();
            studentDashboard.setVisible(true);
        });
    }

    private class Quiz {
        private String name;
        private String description;
        private Date dueDate;

        public Quiz(String name, String description, Date dueDate) {
            this.name = name;
            this.description = description;
            this.dueDate = dueDate;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Date getDueDate() {
            return dueDate;
        }
    }
}
//takeQuiz
