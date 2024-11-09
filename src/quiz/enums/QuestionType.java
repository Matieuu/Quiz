package quiz.enums;

public enum QuestionType {
    WRITE, TRUE_FALSE, CHOOSE_FOUR;

    @Override
    public String toString() {
        return switch (this) {
            case WRITE -> "WRITE";
            case TRUE_FALSE -> "TRUE FALSE";
            case CHOOSE_FOUR -> "CHOOSE FOUR";
        };
    }

    public static QuestionType toQuestionType(String s) {
        return switch (s.toUpperCase()) {
            case "WRITE" -> WRITE;
            case "TRUE FALSE", "TRUE_FALSE" -> TRUE_FALSE;
            case "CHOOSE FOUR", "CHOOSE_FOUR" -> CHOOSE_FOUR;
            default -> null;
        };
    }
}
