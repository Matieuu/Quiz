package quiz.states;

import quiz.CustomColors;
import quiz.abstracts.*;
import quiz.abstracts.Button;
import quiz.abstracts.TextField;
import quiz.enums.Actions;
import quiz.enums.PlayTypes;
import quiz.enums.States;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Play {

    private RandomOrgAPI randomOrgAPI;
    private Thread thread;

    private final PlayTypes playType;
    private final double amount;
    private long nanoTimeStarted;

    private int score, maxScore, timePassed;
    private boolean newQuestion;
    private Question question;

    public Play(PlayTypes playType, double amount) {
        this.playType = playType;
        this.amount = amount;
        this.maxScore = 0;
        this.score = 0;
        this.nanoTimeStarted = System.nanoTime();
        this.timePassed = Integer.MIN_VALUE;
        this.newQuestion = true;

        randomOrgAPI = new RandomOrgAPI();
        thread = new Thread(() -> {
            while (!thread.isInterrupted()) {
                update();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.fillInStackTrace();
                    thread.interrupt();
                }
            }
        });
        thread.start();
    }

    public void update() {
        long currentTime = System.nanoTime();
        if (currentTime - nanoTimeStarted > 1_000_000_000) {
            timePassed++;
            nanoTimeStarted = currentTime;
        }
        if (switch (playType) {
            case TIME_ATTACK -> amount*60 < (timePassed-Integer.MIN_VALUE + .25);
            case LIMITED_LIVES -> maxScore - score >= amount;
            case ENDLESS -> false;
        }) Quiz.getInstance().changeState(States.END_PLAY);
        if (timePassed == Integer.MAX_VALUE) {
            Quiz.getInstance().changeState(States.END_PLAY);
        }
    }

    public void updateQuestion(Map<String, QuizObject> objects) {
        if (Quiz.getInstance().getStateManager().getState() == States.END_PLAY) {
            thread.interrupt();
            objects.put("nickname", new StringObject() {{
                setLocation(new Point(1, 1));
                content.append(Quiz.getInstance().getStateManager().getNickname()).append(" got:");
            }});

            objects.put("score", new StringObject() {{
                setLocation(new Point(1, 2));
                content.append("score ").append(score).append(" out of ").append(maxScore);
            }});

            objects.put("timePassed", new StringObject() {{
                setLocation(new Point(1, 3));
                content.append("in time ").append((long) timePassed-(long) Integer.MIN_VALUE);
            }});

            objects.put("game type", new StringObject() {{
                setLocation(new Point(1, 4));
                content.append("playing ").append(playType);
                if (playType == PlayTypes.TIME_ATTACK) content.append(" time limit on ").append(amount).append(" minutes");
                if (playType == PlayTypes.LIMITED_LIVES) content.append(" with lives limited to ").append((byte)amount);
            }});

            objects.put("exit", new Button() {{
                setDimension(new Dimension(1, 1));
                setLocation(new Point(11, 0));

                action = Actions.CHANGE_STATE;
                state = States.MENU;

                content.append("X");
                bgColor = CustomColors.INDIAN_RED;
            }});

            Quiz.getInstance().repaint();
            Quiz.getInstance().setPlayState(null);
            return;
        }
        if (newQuestion) {
            question = Quiz.getInstance().getQuestions().get(randomOrgAPI.getNext());
            objects.clear();

            objects.put("exit", new Button() {{
                setDimension(new Dimension(1, 1));
                setLocation(new Point(11, 0));

                action = Actions.CHANGE_STATE;
                state = States.END_PLAY;

                content.append("X");
                bgColor = CustomColors.INDIAN_RED;
            }});

            objects.put("question", new StringObject() {{
                content.append(question.getQuestion());
                setLocation(new Point(0, 0));
                font = new Font("TimesRoman", Font.PLAIN, 32);
            }});

            objects.put("score", new QuizObject() {{
                content.append(score).append("/").append(maxScore);
                setLocation(new Point(6, 8));
            }});

            switch (question.getQuestionType()) {
                case WRITE -> {
                    objects.put("answer", new TextField() {{
                        setDimension(new Dimension(8, 1));
                        setLocation(new Point(2, 4));

                        borderColor = CustomColors.GREEN;
                        focus = true;
                    }});

                    objects.put("submit", new Button() {{
                        setDimension(new Dimension(4, 1));
                        setLocation(new Point(4, 6));

                        action = Actions.SUBMIT;

                        content.append("SUBMIT");
                        bgColor = CustomColors.YELLOW_GREEN;
                    }});
                }
                case CHOOSE_FOUR -> {
                    ArrayList<Integer> order = new ArrayList<>() {{
                        Random rand = new Random();
                        int number;

                        for (int i = 0; i < 4; i++) {
                            do { number = rand.nextInt(4); } while (contains(number));
                            add(number);
                        }
                    }};

                    for (int i = 0; i < 4; i++) {
                        int finalI = i;
                        objects.put("answer"+ order.get(i), new Button() {{
                            setDimension(new Dimension(4, 3));
                            setLocation(new Point(order.get(finalI)%2==0 ? 1 : 7, order.get(finalI) < 2 ? 1 : 5));

                            action = Actions.SUBMIT;

                            content.append(question.getAnswers().get(order.get(finalI)));
                            bgColor = order.get(finalI)%2 == 0
                                    ? order.get(finalI) < 2 ? CustomColors.GREEN : CustomColors.SANDY_BROWN
                                    : order.get(finalI) < 2 ? CustomColors.DEEP_SKY_BLUE : CustomColors.PLUM;
                        }});
                    }
                }
                case TRUE_FALSE -> {
                    objects.put("true", new Button() {{
                        setDimension(new Dimension(3, 6));
                        setLocation(new Point(2, 2));

                        action = Actions.SUBMIT;
                        attributes.put("answer", true);

                        content.append("TRUE");
                        bgColor = CustomColors.MEDIUM_SEA_GREEN;
                    }});

                    objects.put("false", new Button() {{
                        setDimension(new Dimension(3, 6));
                        setLocation(new Point(7, 2));

                        action = Actions.SUBMIT;
                        attributes.put("answer", false);

                        content.append("FALSE");
                        bgColor = CustomColors.CRIMSON;
                    }});
                }
            }
        }
        update();
    }

    public void generateNewQuestion() {
        newQuestion = true;
        Quiz.getInstance().changeState(States.PLAY);
    }

    public void incrementScore(boolean correct) {
        maxScore++;
        if (correct) score++;
    }

    public Question getCurrentQuestion() {
        return question;
    }
}
