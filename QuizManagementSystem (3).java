import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class QuizManagementSystem extends JFrame {
    private List<Quiz> quizzes = new ArrayList<>();
    private JTextArea questionTextArea;
    private DefaultListModel<String> quizListModel;
    private Map<Quiz, Question> lastModifiedQuestions = new HashMap<>();

    public QuizManagementSystem() {
        initializeUI();
    }

    public void initializeUI() {
        setTitle("Quiz Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        quizListModel = new DefaultListModel<>();
        JList<String> quizList = new JList<>(quizListModel);
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                int selectedIndex = quizList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    Quiz selectedQuiz = quizzes.get(selectedIndex);
                    updateQuestionTextArea(selectedQuiz);
                }
            }
        });

        mainPanel.add(new JScrollPane(quizList), BorderLayout.WEST);

        questionTextArea = new JTextArea();
        questionTextArea.setEditable(false);
        mainPanel.add(new JScrollPane(questionTextArea), BorderLayout.CENTER);

        JButton createQuizButton = new JButton("Create Quiz");
        createQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String quizName = JOptionPane.showInputDialog("Enter quiz name:");
                if (quizName != null && !quizName.trim().isEmpty()) {
                    Quiz newQuiz = new Quiz(quizName);
                    quizzes.add(newQuiz);
                    quizListModel.addElement(quizName);
                }
            }
        });

        JButton addQuestionButton = new JButton("Add Question");
        addQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = quizList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    Quiz selectedQuiz = quizzes.get(selectedIndex);
                    String questionText = JOptionPane.showInputDialog("Enter a question:");
                    if (questionText != null && !questionText.trim().isEmpty()) {
                        String[] options = new String[4];
                        for (int i = 0; i < 4; i++) {
                            options[i] = JOptionPane.showInputDialog("Enter option " + (i + 1) + ":");
                        }
                        String correctAnswer = (String) JOptionPane.showInputDialog(
                                null,
                                "Select the correct answer:",
                                "Correct Answer",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]
                        );
                        if (correctAnswer != null) {
                            int timeLimit = Integer.parseInt(JOptionPane.showInputDialog("Enter time limit (in seconds):"));
                            int passingScore = Integer.parseInt(JOptionPane.showInputDialog("Enter passing score:"));
                            selectedQuiz.addQuestion(new MultipleChoiceQuestion(questionText, options, correctAnswer, timeLimit, passingScore));
                            updateQuestionTextArea(selectedQuiz);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a quiz.");
                }
            }
        });

        JButton editQuestionButton = new JButton("Edit Question");
        editQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = quizList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    Quiz selectedQuiz = quizzes.get(selectedIndex);
                    String[] options = selectedQuiz.getQuestions().toArray(new String[0]);
                    String selectedQuestion = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a question to edit:",
                            "Edit Question",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                    if (selectedQuestion != null) {
                        Question existingQuestion = selectedQuiz.getQuestion(selectedQuestion);
                        if (existingQuestion instanceof MultipleChoiceQuestion) {
                            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) existingQuestion;
                            String questionText = JOptionPane.showInputDialog("Edit the question:", selectedQuestion);
                            String[] updatedOptions = new String[4];
                            for (int i = 0; i < 4; i++) {
                                updatedOptions[i] = JOptionPane.showInputDialog("Edit option " + (i + 1) + ":", mcq.getOptions()[i]);
                            }
                            String updatedCorrectAnswer = (String) JOptionPane.showInputDialog(
                                null,
                                "Select the correct answer:",
                                "Correct Answer",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                updatedOptions,
                                mcq.getCorrectAnswer()
                            );
                            if (updatedCorrectAnswer != null) {
                                int updatedTimeLimit = Integer.parseInt(JOptionPane.showInputDialog("Edit time limit (in seconds):", mcq.getTimeLimit()));
                                int updatedPassingScore = Integer.parseInt(JOptionPane.showInputDialog("Edit passing score:", mcq.getPassingScore()));
                                Question updatedQuestion = new MultipleChoiceQuestion(questionText, updatedOptions, updatedCorrectAnswer, updatedTimeLimit, updatedPassingScore);
                                selectedQuiz.editQuestion(selectedQuestion, updatedQuestion);
                                updateQuestionTextArea(selectedQuiz);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a quiz.");
                }
            }
        });

        JButton deleteQuestionButton = new JButton("Delete Question");
        deleteQuestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = quizList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    Quiz selectedQuiz = quizzes.get(selectedIndex);
                    String[] options = selectedQuiz.getQuestions().toArray(new String[0]);
                    String selectedQuestion = (String) JOptionPane.showInputDialog(
                            null,
                            "Select a question to delete:",
                            "Delete Question",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                    if (selectedQuestion != null) {
                        selectedQuiz.deleteQuestion(selectedQuestion);
                        updateQuestionTextArea(selectedQuiz);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a quiz.");
                }
            }
        });

        JButton showLastModifiedQuestionsButton = new JButton("Show Last Modified Questions");
        showLastModifiedQuestionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLastModifiedQuestions();
            }
        });

        JButton saveQuizDataButton = new JButton("Save Quiz Data");
        saveQuizDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveQuizDataToFile();
            }
        });

        JButton loadQuizDataButton = new JButton("Load Quiz Data");
        loadQuizDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadQuizDataFromFile();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1));
        buttonPanel.add(createQuizButton);
        buttonPanel.add(addQuestionButton);
        buttonPanel.add(editQuestionButton);
        buttonPanel.add(deleteQuestionButton);
        buttonPanel.add(showLastModifiedQuestionsButton);
        buttonPanel.add(saveQuizDataButton);
        buttonPanel.add(loadQuizDataButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }

    private void updateQuestionTextArea(Quiz quiz) {
        if (quiz != null && !quiz.getQuestions().isEmpty()) {
            StringBuilder questionText = new StringBuilder();
            for (Question question : quiz.getQuestions()) {
                questionText.append("Question: ").append(question.getQuestionText()).append("\n");
                if (question instanceof MultipleChoiceQuestion) {
                    MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
                    questionText.append("Options: ").append(String.join(", ", mcq.getOptions())).append("\n");
                    questionText.append("Correct Answer: ").append(mcq.getCorrectAnswer()).append("\n");
                }
                questionText.append("Time Limit: ").append(question.getTimeLimit()).append(" seconds\n");
                questionText.append("Passing Score: ").append(question.getPassingScore()).append("\n");
                questionText.append("\n");
            }
            questionTextArea.setText(questionText.toString());
        } else {
            questionTextArea.setText("");
        }
    }

    private void showLastModifiedQuestions() {
        if (!lastModifiedQuestions.isEmpty()) {
            StringBuilder modifiedText = new StringBuilder("Last Modified Questions:\n\n");
            for (Map.Entry<Quiz, Question> entry : lastModifiedQuestions.entrySet()) {
                Quiz quiz = entry.getKey();
                Question question = entry.getValue();
                modifiedText.append("Quiz: ").append(quiz.getName()).append("\n");
                modifiedText.append("Question: ").append(question.getQuestionText()).append("\n\n");
            }
            JOptionPane.showMessageDialog(this, modifiedText.toString());
        } else {
            JOptionPane.showMessageDialog(this, "No questions have been modified recently.");
        }
    }

 private void saveQuizDataToFile() {
    try (PrintWriter quizNameWriter = new PrintWriter("C:\\Users\\Admin\\Desktop\\quizname.txt");
         PrintWriter questionWriter = new PrintWriter("C:\\Users\\Admin\\Desktop\\Question.txt");
         PrintWriter mcqWriter = new PrintWriter("C:\\Users\\Admin\\Desktop\\Mcq.txt");
         PrintWriter answerWriter = new PrintWriter("C:\\Users\\Admin\\Desktop\\Answer.txt");
         PrintWriter timeWriter = new PrintWriter("C:\\Users\\Admin\\Desktop\\time.txt");
         PrintWriter scoreWriter = new PrintWriter("C:\\Users\\Admin\\Desktop\\score.txt")) {

        for (Quiz quiz : quizzes) {
            // Save quiz name to quizname.txt
            quizNameWriter.println(quiz.getName());

            for (Question question : quiz.getQuestions()) {
                // Save question text to Question.txt
                questionWriter.print( quiz.getName()+",");
                questionWriter.println(question.getQuestionText());

                // Check if the question is of type MultipleChoiceQuestion
                if (question instanceof MultipleChoiceQuestion) {
                    MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
                   
                    mcqWriter.print(quiz.getName()+",");
                    mcqWriter.print(question.getQuestionText()+",");
                    mcqWriter.println( String.join(",", mcq.getOptions()));

                    // Save correct answer to Answer.txt
                       answerWriter.println(quiz.getName());
                    //answerWriter.println("Question:" + question.getQuestionText());
                    answerWriter.println( mcq.getCorrectAnswer());
                }

                // Save time limit to time.txt
                //timeWriter.println("Quiz:" + quiz.getName());
               // timeWriter.println("Question:" + question.getQuestionText());
                timeWriter.println( question.getTimeLimit());

                // Save passing score to General.txt
               // timeWriter.println("PassingScore:" + question.getPassingScore());

                // Save score information to score.txt
                double score = quiz.getScore(question.getQuestionText());
                if (score != -1) { // Check if a score exists for the question
                  //  scoreWriter.println("Quiz:" + quiz.getName());
                   // scoreWriter.println("Question:" + question.getQuestionText());
                    scoreWriter.println( score);
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Quiz data saved to files.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error saving quiz data to files.");
    }
}


    private void loadQuizDataFromFile() {
    try (Scanner scanner = new Scanner(new File("C:\\Users\\Admin\\Desktop\\nabil.txt"));
         Scanner scoreScanner = new Scanner(new File("C:\\Users\\Admin\\Desktop\\score.txt"))) {
        quizzes.clear();
        quizListModel.clear();
        Quiz currentQuiz = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("QuizName:")) {
                String quizName = line.substring("QuizName:".length());
                currentQuiz = new Quiz(quizName);
                quizzes.add(currentQuiz);
                quizListModel.addElement(quizName);
            } else if (line.startsWith("Question:") && currentQuiz != null) {
                String questionText = line.substring("Question:".length());
                String optionsLine = scanner.nextLine();
                String[] options = optionsLine.substring("Options:".length()).split(",");
                String correctAnswer = scanner.nextLine().substring("CorrectAnswer:".length());
                int timeLimit = Integer.parseInt(scanner.nextLine().substring("TimeLimit:".length()));
                int passingScore = Integer.parseInt(scanner.nextLine().substring("PassingScore:".length()));
                currentQuiz.addQuestion(new MultipleChoiceQuestion(questionText, options, correctAnswer, timeLimit, passingScore));
                updateQuestionTextArea(currentQuiz);
            }
        }

        // Load scores from score.txt
        while (scoreScanner.hasNextLine()) {
            String quizName = scoreScanner.nextLine().substring("Quiz:".length());
            String questionText = scoreScanner.nextLine().substring("Question:".length());
            double score = Double.parseDouble(scoreScanner.nextLine().substring("Score:".length()));

            // Find the quiz by name
            Quiz quiz = quizzes.stream().filter(q -> q.getName().equals(quizName)).findFirst().orElse(null);
            if (quiz != null) {
                // Set the score for the question in the quiz
                quiz.setScore(questionText, score);
            }
        }

        JOptionPane.showMessageDialog(this, "Quiz data loaded from C:\\Users\\Admin\\Desktop\\nabil.txt");
    } catch (FileNotFoundException e) {
        JOptionPane.showMessageDialog(this, "Error loading quiz data from file.");
    }
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QuizManagementSystem();
        });
    }

    public abstract class Question {
        public String questionText;
        public int timeLimit;
        public int passingScore;

        public Question(String questionText, int timeLimit, int passingScore) {
            this.questionText = questionText;
            this.timeLimit = timeLimit;
            this.passingScore = passingScore;
        }

        public String getQuestionText() {
            return questionText;
        }

        public int getTimeLimit() {
            return timeLimit;
        }

        public int getPassingScore() {
            return passingScore;
        }
    }

    public class MultipleChoiceQuestion extends Question {
        public String[] options;
        public String correctAnswer;

        public MultipleChoiceQuestion(String questionText, String[] options, String correctAnswer, int timeLimit, int passingScore) {
            super(questionText, timeLimit, passingScore);
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        public String[] getOptions() {
            return options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }
    }

   public class Quiz {
    public String name;
    public List<Question> questions;
    public Map<String, Double> scores; // Map to store scores for each question

    public Quiz(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
        this.scores = new HashMap<>(); // Initialize the scores map
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        if (questions.size() < 5) { // Limit to 5 questions
            questions.add(question);
        } else {
            JOptionPane.showMessageDialog(null, "You can add up to 5 questions to a quiz.");
        }
    }

    public Question getQuestion(String questionText) {
        for (Question question : questions) {
            if (question.getQuestionText().equals(questionText)) {
                return question;
            }
        }
        return null;
    }

    public void editQuestion(String oldQuestionText, Question newQuestion) {
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getQuestionText().equals(oldQuestionText)) {
                questions.set(i, newQuestion);
                return;
            }
        }
    }

    public void deleteQuestion(String questionText) {
        questions.removeIf(question -> question.getQuestionText().equals(questionText));
        // Remove the score for the deleted question
        scores.remove(questionText);
    }

    // Set the score for a question
    public void setScore(String questionText, double score) {
        scores.put(questionText, score);
    }

    // Get the score for a question
    public double getScore(String questionText) {
        return scores.getOrDefault(questionText, 0.0); // Default to 0 if no score is found
    }
}

}
