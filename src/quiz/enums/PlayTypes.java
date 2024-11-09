package quiz.enums;

public enum PlayTypes {
    TIME_ATTACK,
    ENDLESS,
    LIMITED_LIVES;

    @Override
    public String toString() {
        return switch (this) {
            case TIME_ATTACK -> "Time Attack";
            case ENDLESS -> "Endless";
            case LIMITED_LIVES -> "Limited Lives";
        };
    }
}
