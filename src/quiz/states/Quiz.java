package quiz.states;

import quiz.CustomColors;
import quiz.QuestionYaml;
import quiz.Window;
import quiz.abstracts.Button;
import quiz.abstracts.Question;
import quiz.abstracts.QuizObject;
import quiz.abstracts.TextField;
import quiz.enums.QuestionType;
import quiz.enums.States;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Quiz implements Runnable {

    private static Quiz instance;

    public Point grid;
    public double gridWidth, gridHeight;
    private boolean render;
    private QuestionType questionType;

    private ArrayList<Question> questions;
    private HashMap<String, QuizObject> objects;

    private quiz.Window window;
    private Thread thread;
    private ScheduledThreadPoolExecutor exec;
    private StateManager stateManager;
    private Play playState;
    private QuestionYaml questionYaml;

    public Quiz() {
        if (instance == null) instance = this;
        window = new Window();

        grid = new Point(12, 9);
        gridWidth = window.getPanelSize().getWidth()/grid.x;
        gridHeight = window.getPanelSize().getHeight()/grid.y;

        questions = new ArrayList<>();
        objects = new HashMap<>();
        try {
            questionYaml = new QuestionYaml();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        render = true;
        exec = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

        questionType = null;
        stateManager = new StateManager();
        changeState(States.MENU);

        thread = new Thread(this);
        thread.start();
    }

    public void render(Graphics g) {
        g.setColor(CustomColors.LIGHT_BLACK);
        g.fillRect(0, 0, window.getWidth(), window.getHeight());

//        g.setColor(CustomColors.LIGHTER_BLACK);
//        for (double i = 1; i <= grid.x; i++)
//            g.drawLine((int) (i * gridWidth), 0, (int) (i * gridWidth), window.getHeight());
//        for (int i = 1; i <= grid.y; i++)
//            g.drawLine(0, (int) (i * gridHeight), window.getWidth(), (int) (i * gridHeight));

        for (Map.Entry<String, QuizObject> entry : objects.entrySet())
            entry.getValue().render(g);
    }

    @Override
    public void run() {
        while (!thread.isInterrupted()) {
            if (render) {
                window.repaint();
                render = false;
            }
        }
    }

    public void changeState(States state) {
        objects.clear();

        if (playState != null) {
            stateManager.setState(state);
            playState.updateQuestion(objects);
        } else stateManager.setState(objects, state);

        render = true;
        setExecInterval();
    }

    public QuizObject getObject(Point p) {
        for (QuizObject obj : objects.values())
            if (obj.getRectangle().contains(p)) return obj;
        return null;
    }

    public QuizObject getObject(Point press, Point release) {
        QuizObject pressObj = null, releaseObj = null;

        for (QuizObject obj : objects.values()) {
            if (!(obj instanceof Button btn)) continue;
            if (btn.getLocation() == null) continue;
            if (btn.getRectangle().contains(press)) pressObj = obj;
            if (btn.getRectangle().contains(release)) releaseObj = obj;
        }

        if (pressObj == null || releaseObj == null) return null;
        if (pressObj.equals(releaseObj)) return pressObj;
        return null;
    }

    public Button getSubmitButton() {
        for (Map.Entry<String, QuizObject> entry : objects.entrySet()) {
            if (!(entry.getValue() instanceof Button btn)) continue;
            if (entry.getKey().equalsIgnoreCase("submit")) return btn;
        }
        return null;
    }

    public ArrayList<TextField> getAllTextFields() {
        ArrayList<TextField> textFields = new ArrayList<>();
        for (QuizObject obj : objects.values())
            if (obj instanceof TextField) textFields.add((TextField) obj);
        return textFields;
    }

    public TextField getFocusedTextField() {
        for (QuizObject obj : objects.values())
            if (obj instanceof TextField && ((TextField) obj).focus) return (TextField) obj;
        return null;
    }

    public void setExecInterval() {
        if (getFocusedTextField() == null) exec.getQueue().clear();
        if (getFocusedTextField() != null && exec.getQueue().isEmpty())
            exec.scheduleAtFixedRate(() -> render = true, 500 - (System.currentTimeMillis() % 1000), 500, TimeUnit.MILLISECONDS);
    }

    public void repaint() {
        render = true;
    }

    public static Quiz getInstance() {
        return instance;
    }
    public StateManager getStateManager() {
        return stateManager;
    }
    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
    public QuestionType getQuestionType() {
        return questionType;
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }
    public void createQuestion(Question question) {
        questions.add(question);
        try {
            questionYaml.addOrUpdateQuestion("question"+ questions.size(), question);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void addQuestion(Map<String, Object> questionMap) {
        Question question = new Question(QuestionType.toQuestionType((String) questionMap.get("questionType"))) {{
            setQuestion((String) questionMap.get("question"));
            setCorrectAnswer(questionMap.get("correctAnswer") instanceof String ? (String) questionMap.get("correctAnswer") : String.valueOf((boolean) questionMap.get("correctAnswer")));
            if (getQuestionType() == QuestionType.CHOOSE_FOUR && questionMap.get("answers") instanceof ArrayList)
                for (Object ans : (ArrayList<?>) questionMap.get("answers"))
                    addAnswer((String) ans);
        }};
        questions.add(question);
    }
    public Play getPlayState() {
        return playState;
    }
    public void setPlayState(Play playState) {
        this.playState = playState;
    }
}
