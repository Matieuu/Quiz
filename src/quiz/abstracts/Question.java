package quiz.abstracts;

import quiz.enums.QuestionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Question {
    private String question;
    private QuestionType questionType;
    private ArrayList<String> answers;
    private String correctAnswer;

    public Question(QuestionType questionType) {
        this.questionType = questionType;
        if (questionType == QuestionType.CHOOSE_FOUR || questionType == QuestionType.TRUE_FALSE) answers = new ArrayList<>();
    }

    public boolean verify(String answer) {
        return correctAnswer.equalsIgnoreCase(answer);
    }

    public void addAnswer(String answer) {
        if (answers == null) return;
        if ((questionType == QuestionType.TRUE_FALSE && answers.size() >= 2) || (questionType == QuestionType.CHOOSE_FOUR && answers.size() >= 4)) return;
        answers.add(answer);
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }
    public String getQuestion() {
        return question;
    }
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    public QuestionType getQuestionType() {
        return questionType;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
