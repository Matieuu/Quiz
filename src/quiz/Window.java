package quiz;

import quiz.abstracts.Button;
import quiz.abstracts.Question;
import quiz.abstracts.QuizObject;
import quiz.abstracts.TextField;
import quiz.enums.PlayTypes;
import quiz.enums.QuestionType;
import quiz.enums.States;
import quiz.states.Play;
import quiz.states.Quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Window extends JFrame {

    private Panel panel;

    public Window() {
        super("Quiz");
        add(panel = new Panel());
        pack();

        MouseAdapter mouseAdapter = new MouseAdapter() {
            private Point press;

            @Override
            public void mousePressed(MouseEvent e) {
                press = e.getPoint();

                Quiz.getInstance().getAllTextFields().forEach(tf ->  tf.focus = false);
                if (Quiz.getInstance().getObject(press) instanceof TextField tf)
                    tf.focus = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                QuizObject obj = Quiz.getInstance().getObject(press, e.getPoint());

                if (obj instanceof Button btn) {
                    switch (btn.action) {
                        case CHANGE_STATE -> {
                            if (btn.attributes.get("question_type") instanceof QuestionType qt) Quiz.getInstance().setQuestionType(qt);
                            if (btn.state != null) Quiz.getInstance().changeState(btn.state);
                        }
                        case EXIT -> System.exit(0);
                        case PLAY -> {
                            PlayTypes playType = (PlayTypes) btn.attributes.get("play_type");
                            double amount = btn.attributes.get("amount") == null ? 0 : (double) btn.attributes.get("amount");
                            Quiz.getInstance().setPlayState(new Play(playType, amount));
                            Quiz.getInstance().changeState(States.PLAY);
                        }
                        case SUBMIT -> {
                            Quiz quiz = Quiz.getInstance();

                            switch (Quiz.getInstance().getStateManager().getState()) {
                                case CHANGE_NAME -> {
                                    TextField tf = quiz.getAllTextFields().getFirst();

                                    if (!tf.content.isEmpty()) quiz.getStateManager().setNickname(tf.toString());
                                    quiz.changeState(btn.state != null ? btn.state : States.MENU);
                                }
                                case ADD_QUESTION -> {
                                    switch (quiz.getQuestionType()) {
                                        case WRITE -> {
                                            TextField question = quiz.getAllTextFields().getFirst();
                                            TextField answer = quiz.getAllTextFields().getLast();

                                            quiz.createQuestion(new Question(quiz.getQuestionType()) {{
                                                setQuestion(question.toString());
                                                setCorrectAnswer(answer.toString());
                                            }});
                                        }
                                        case TRUE_FALSE -> {
                                            TextField question = quiz.getAllTextFields().getFirst();

                                            quiz.createQuestion(new Question(quiz.getQuestionType()) {{
                                                setQuestion(question.toString());
                                                setCorrectAnswer(btn.attributes.get("answer").toString());
                                            }});
                                        }
                                        case CHOOSE_FOUR -> {
                                            TextField question = quiz.getAllTextFields().getFirst();
                                            ArrayList<TextField> answers = quiz.getAllTextFields().stream()
                                                    .filter(tf -> !Objects.equals(tf.toString(), question.toString()))
                                                    .collect(Collectors.toCollection(ArrayList::new));

                                            quiz.createQuestion(new Question(quiz.getQuestionType()) {{
                                                setQuestion(question.toString());
                                                setCorrectAnswer(answers.stream().filter(tf -> tf.attributes.get("answer").equals("correct"))
                                                        .collect(Collectors.toCollection(ArrayList::new)).getFirst().toString());
                                                for (TextField tf : answers)
                                                    addAnswer(tf.toString());
                                            }});
                                        }
                                    }

                                    Quiz.getInstance().setQuestionType(null);
                                    if (btn.state != null) Quiz.getInstance().changeState(btn.state);
                                }
                                case PLAY -> {
                                    Play play = quiz.getPlayState();
                                    String ans = switch (play.getCurrentQuestion().getQuestionType()) {
                                        case WRITE -> Quiz.getInstance().getAllTextFields().getFirst().toString();
                                        case TRUE_FALSE -> btn.attributes.get("answer").toString();
                                        case CHOOSE_FOUR -> btn.content.toString();
                                    };
                                    play.incrementScore(play.getCurrentQuestion().verify(ans));
                                    play.generateNewQuestion();
                                }
                            }
                        }
                    }
                }

                press = null;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                QuizObject obj = Quiz.getInstance().getObject(e.getPoint());
                if (obj instanceof Button) panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                else if (obj instanceof TextField) panel.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                else panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        };
        KeyAdapter keyAdapter = new KeyAdapter() {
            private final ArrayList<Integer> allowedKeys = new ArrayList<>(List.of(KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_BEGIN, KeyEvent.VK_END, KeyEvent.VK_HOME,
                    KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_PAGE_UP, KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE));

            @Override
            public void keyPressed(KeyEvent e) {
                TextField tf = Quiz.getInstance().getFocusedTextField();
                if (e.isActionKey() && !allowedKeys.contains(e.getKeyCode())) return;

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                         KeyEvent.VK_BEGIN, KeyEvent.VK_END, KeyEvent.VK_HOME,
                         KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_PAGE_UP -> {
                        if (tf == null) return;
                        tf.moveCursorTo(e);
                    }
                    case KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE -> {
                        if (tf == null) return;
                        tf.removeContent(e);
                    }
                    case KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_ALT -> {}
                    case KeyEvent.VK_ESCAPE -> {
                        switch (Quiz.getInstance().getStateManager().getState()) {
                            case CHANGE_NAME, PLAY, CHOOSE_QUESTION -> Quiz.getInstance().changeState(States.MENU);
                            case MENU -> System.exit(0);
                            case ADD_QUESTION -> Quiz.getInstance().changeState(States.CHOOSE_QUESTION);
                        }
                    }
                    case KeyEvent.VK_ENTER -> {
                        Quiz quiz = Quiz.getInstance();
                        if (tf == null) return;
                        Button btn = quiz.getSubmitButton();
                        switch (quiz.getStateManager().getState()) {
                            case CHANGE_NAME -> {
                                if (!tf.content.isEmpty()) quiz.getStateManager().setNickname(tf.toString());
                                quiz.changeState(btn.state != null ? btn.state : States.MENU);
                            }
                            case PLAY -> {
                                Play play = quiz.getPlayState();
                                play.incrementScore(play.getCurrentQuestion().verify(tf.toString()));
                                quiz.changeState(States.PLAY);
                            }
                        }
                    }
                    default -> {
                        if (tf == null) return;
                        tf.addContent(e);
                    }
                }
                if (tf != null) Quiz.getInstance().repaint();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addKeyListener(keyAdapter);

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public Dimension getPanelSize() {
        return panel.getPreferredSize();
    }

    private class Panel extends JPanel {
        public Panel() {
            setPreferredSize(new Dimension(1600, 900));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Quiz.getInstance().render(g);
        }
    }
}
