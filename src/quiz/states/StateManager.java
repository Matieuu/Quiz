package quiz.states;

import quiz.CustomColors;
import quiz.abstracts.Button;
import quiz.abstracts.QuizObject;
import quiz.enums.Actions;
import quiz.enums.PlayTypes;
import quiz.enums.QuestionType;
import quiz.enums.States;

import java.awt.*;
import java.util.Map;

public class StateManager {

    private States state;

    private String nickname;

    public StateManager() {
        nickname = "Guest";
    }

    public void setState(Map<String, QuizObject> objects, States state) {
        this.state = state;

        switch (state) {
            case MENU -> {
                objects.put("nickname", new QuizObject() {{
                    content.append("User: ").append(nickname == null ? "Guest" : nickname);
                    setLocation(new Point(6, 0));
                }});

                objects.put("start", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(4, 3));
                    setLocation(new Point(1, 1));

                    action = Actions.CHANGE_STATE;
                    state = States.CHOOSE_PLAY;

                    content.append("Start quiz");
                    bgColor = CustomColors.FOREST_GREEN;
                }});

                objects.put("rename", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(4, 3));
                    setLocation(new Point(7, 1));

                    action = Actions.CHANGE_STATE;
                    state = States.CHANGE_NAME;

                    content.append("Change name");
                    bgColor = CustomColors.CHOCOLATE;
                }});

                objects.put("addquestion", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(4, 3));
                    setLocation(new Point(1, 5));

                    action = Actions.CHANGE_STATE;
                    state = States.CHOOSE_QUESTION;

                    content.append("Add question");
                    bgColor = CustomColors.DARK_CYAN;
                }});

                objects.put("exit", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(4, 3));
                    setLocation(new Point(7, 5));

                    action = Actions.EXIT;

                    content = new StringBuilder("Exit quiz");
                    bgColor = CustomColors.INDIAN_RED;
                }});
            }
            case CHANGE_NAME -> {
                objects.put("title", new QuizObject() {{
                    content.append("Last name: ").append(nickname == null ? "Guest" : nickname);
                    setLocation(new Point(6, 0));
                }});

                objects.put("newname", new quiz.abstracts.TextField() {{
                    setDimension(new Dimension(8, 1));
                    setLocation(new Point(2, 2));

                    borderColor = CustomColors.DARK_ORANGE;
                    focus = true;
                }});

                objects.put("submit", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(4, 2));
                    setLocation(new Point(4, 4));

                    action = Actions.SUBMIT;
                    state = States.MENU;

                    content.append("Submit");
                    bgColor = CustomColors.YELLOW_GREEN;
                }});
            }
            case CHOOSE_QUESTION -> {
                objects.put("title", new QuizObject() {{
                    content = new StringBuilder("Choose question type");
                    setLocation(new Point(6, 0));
                }});

                objects.put("write", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(4, 3));
                    setLocation(new Point(1, 1));

                    action = Actions.CHANGE_STATE;
                    state = States.ADD_QUESTION;
                    attributes.put("question_type", QuestionType.WRITE);

                    content = new StringBuilder("String answer");
                    bgColor = CustomColors.MEDIUM_AQUAMARINE;
                }});

                objects.put("truefalse", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(4, 3));
                    setLocation(new Point(7, 1));

                    action = Actions.CHANGE_STATE;
                    state = States.ADD_QUESTION;
                    attributes.put("question_type", QuestionType.TRUE_FALSE);

                    content = new StringBuilder("True or false");
                    bgColor = CustomColors.DARK_CYAN;
                }});

                objects.put("choosefour", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(4, 3));
                    setLocation(new Point(4, 5));

                    action = Actions.CHANGE_STATE;
                    state = States.ADD_QUESTION;
                    attributes.put("question_type", QuestionType.CHOOSE_FOUR);

                    content = new StringBuilder("Choose from four");
                    bgColor = CustomColors.DODGER_BLUE;
                }});

                objects.put("exit", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(1, 1));
                    setLocation(new Point(11, 0));

                    action = Actions.CHANGE_STATE;
                    state = States.MENU;

                    content.append("X");
                    bgColor = CustomColors.INDIAN_RED;
                }});
            }
            case ADD_QUESTION -> {
                objects.put("title", new QuizObject() {{
                    content.append(switch (Quiz.getInstance().getQuestionType()) {
                        case QuestionType.WRITE -> "wrote answer";
                        case QuestionType.TRUE_FALSE -> "true or false";
                        case QuestionType.CHOOSE_FOUR -> "one correct from four answers";
                        case null, default -> "Error occurred";
                    });
                    setLocation(new Point(5, 0));
                }});

                objects.put("question", new quiz.abstracts.TextField() {{
                    setDimension(new Dimension(8, 1));
                    setLocation(new Point(2, 1));

                    borderColor = CustomColors.DARK_ORANGE;
                    focus = true;
                }});

                objects.put("exit", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(1, 1));
                    setLocation(new Point(11, 0));

                    action = Actions.CHANGE_STATE;
                    state = States.CHOOSE_QUESTION;
                    attributes.put("question_type", null);

                    content.append("X");
                    bgColor = CustomColors.INDIAN_RED;
                }});

                switch (Quiz.getInstance().getQuestionType()) {
                    case WRITE -> {
                        objects.put("answer", new quiz.abstracts.TextField() {{
                            setDimension(new Dimension(8, 1));
                            setLocation(new Point(2, 4));

                            borderColor = CustomColors.LIME_GREEN;
                        }});

                        objects.put("submit", new quiz.abstracts.Button() {{
                            setDimension(new Dimension(4, 1));
                            setLocation(new Point(4, 7));

                            action = Actions.SUBMIT;
                            state = States.CHOOSE_QUESTION;
                            attributes.put("question_type", null);

                            content.append("SUBMIT");
                            bgColor = CustomColors.YELLOW_GREEN;
                        }});
                    }
                    case TRUE_FALSE -> {
                        objects.put("true", new quiz.abstracts.Button() {{
                            setDimension(new Dimension(3, 4));
                            setLocation(new Point(2, 3));

                            action = Actions.SUBMIT;
                            state = States.CHOOSE_QUESTION;
                            attributes.put("question_type", null);
                            attributes.put("answer", true);

                            content.append("TRUE");
                            bgColor = CustomColors.MEDIUM_SEA_GREEN;
                        }});

                        objects.put("false", new quiz.abstracts.Button() {{
                            setDimension(new Dimension(3, 4));
                            setLocation(new Point(7, 3));

                            action = Actions.SUBMIT;
                            state = States.CHOOSE_QUESTION;
                            attributes.put("question_type", null);
                            attributes.put("answer", false);

                            content.append("FALSE");
                            bgColor = CustomColors.CRIMSON;
                        }});
                    }
                    case CHOOSE_FOUR -> {
                        objects.put("answer1", new quiz.abstracts.TextField() {{
                            setDimension(new Dimension(8, 1));
                            setLocation(new Point(2, 3));

                            attributes.put("answer", "correct");

                            borderColor = CustomColors.LIME_GREEN;
                        }});

                        objects.put("answer2", new quiz.abstracts.TextField() {{
                            setDimension(new Dimension(8, 1));
                            setLocation(new Point(2, 4));

                            attributes.put("answer", "incorrect");

                            borderColor = CustomColors.BROWN;
                        }});

                        objects.put("answer3", new quiz.abstracts.TextField() {{
                            setDimension(new Dimension(8, 1));
                            setLocation(new Point(2, 5));

                            attributes.put("answer", "incorrect");

                            borderColor = CustomColors.BROWN;
                        }});

                        objects.put("answer4", new quiz.abstracts.TextField() {{
                            setDimension(new Dimension(8, 1));
                            setLocation(new Point(2, 6));

                            attributes.put("answer", "incorrect");

                            borderColor = CustomColors.BROWN;
                        }});

                        objects.put("submit", new quiz.abstracts.Button() {{
                            setDimension(new Dimension(4, 1));
                            setLocation(new Point(4, 8));

                            action = Actions.SUBMIT;
                            state = States.CHOOSE_QUESTION;
                            attributes.put("question_type", null);

                            content.append("SUBMIT");
                            bgColor = CustomColors.YELLOW_GREEN;
                        }});
                    }
                }
            }
            case CHOOSE_PLAY -> {
                objects.put("exit", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(1, 1));
                    setLocation(new Point(11, 0));

                    action = Actions.CHANGE_STATE;
                    state = States.MENU;

                    content.append("X");
                    bgColor = CustomColors.INDIAN_RED;
                }});

                objects.put("timeattack30s", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(2, 1));
                    setLocation(new Point(2, 2));

                    action = Actions.PLAY;
                    attributes.put("play_type", PlayTypes.TIME_ATTACK);
                    attributes.put("amount", .5);

                    content.append("30 s");
                    bgColor = CustomColors.DARK_CYAN;
                }});

                objects.put("timeattack1m", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(2, 1));
                    setLocation(new Point(5, 2));

                    action = Actions.PLAY;
                    attributes.put("play_type", PlayTypes.TIME_ATTACK);
                    attributes.put("amount", 1.);

                    content.append("1 min");
                    bgColor = CustomColors.LIGHT_SEA_GREEN;
                }});

                objects.put("timeattack2m", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(2, 1));
                    setLocation(new Point(8, 2));

                    action = Actions.PLAY;
                    attributes.put("play_type", PlayTypes.TIME_ATTACK);
                    attributes.put("amount", 2.);

                    content.append("2 min");
                    bgColor = CustomColors.MEDIUM_AQUAMARINE;
                }});

                objects.put("timeattack5m", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(2, 1));
                    setLocation(new Point(3, 4));

                    action = Actions.PLAY;
                    attributes.put("play_type", PlayTypes.TIME_ATTACK);
                    attributes.put("amount", 5.);

                    content.append("5 min");
                    bgColor = CustomColors.DEEP_SKY_BLUE;
                }});

                objects.put("endless", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(2, 1));
                    setLocation(new Point(7, 4));

                    action = Actions.PLAY;
                    attributes.put("play_type", PlayTypes.ENDLESS);

                    content.append("endless");
                    bgColor = CustomColors.YELLOW_GREEN;
                }});

                objects.put("wrong1", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(2, 1));
                    setLocation(new Point(2, 6));

                    action = Actions.PLAY;
                    attributes.put("play_type", PlayTypes.LIMITED_LIVES);
                    attributes.put("amount", 1.);

                    content.append("1 life");
                    bgColor = CustomColors.MEDIUM_SEA_GREEN;
                }});

                objects.put("wrong3", new quiz.abstracts.Button() {{
                    setDimension(new Dimension(2, 1));
                    setLocation(new Point(5, 6));

                    action = Actions.PLAY;
                    attributes.put("play_type", PlayTypes.LIMITED_LIVES);
                    attributes.put("amount", 3.);

                    content.append("3 lives");
                    bgColor = CustomColors.FOREST_GREEN;
                }});

                objects.put("wrong5", new Button() {{
                    setDimension(new Dimension(2, 1));
                    setLocation(new Point(8, 6));

                    action = Actions.PLAY;
                    attributes.put("play_type", PlayTypes.LIMITED_LIVES);
                    attributes.put("amount", 5.);

                    content.append("5 lives");
                    bgColor = CustomColors.DARK_GREEN;
                }});
            }
        }
    }

    public void setState(States state) {
        this.state = state;
    }

    public States getState() {
        return state;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
