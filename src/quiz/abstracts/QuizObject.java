package quiz.abstracts;

import quiz.CustomColors;
import quiz.states.Quiz;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class QuizObject {

    public StringBuilder content = new StringBuilder();
    public Font font = new Font("Arial", Font.BOLD, 64);
    public Color fontColor = CustomColors.ALICE_BLUE;
    public Map<String, Object> attributes = new HashMap<>();

    protected Point location;
    protected Rectangle rectangle = new Rectangle();

    public void setLocation(Point location) {
        if (location.x > Quiz.getInstance().gridWidth || location.y > Quiz.getInstance().gridHeight) return;
        this.location = location;
        rectangle.setLocation((int) (location.x * Quiz.getInstance().gridWidth), (int) (location.y * Quiz.getInstance().gridHeight));
    }
    public Point getLocation() {
        return location;
    }
    public Rectangle getRectangle() {
        return rectangle;
    }

    public void render(Graphics g) {
        double gridWidth = Quiz.getInstance().gridWidth,
                gridHeight = Quiz.getInstance().gridHeight;

        g.setColor(fontColor);
        g.setFont(font);

        if (!content.isEmpty()) g.drawString(toString(),
                (int) (getLocation().x * gridWidth - (double) g.getFontMetrics().stringWidth(toString()) / 2),
                (int) (getLocation().y * gridHeight + .5 * gridHeight + (double) g.getFontMetrics().getAscent() / 3));
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
