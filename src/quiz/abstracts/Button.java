package quiz.abstracts;

import quiz.CustomColors;
import quiz.states.Quiz;
import quiz.enums.Actions;
import quiz.enums.States;

import java.awt.*;

public abstract class Button extends QuizObject {

    public Actions action;
    public States state;
    public Color bgColor = CustomColors.NAVY;

    protected Dimension dimension;

    public void setDimension(Dimension dimension) {
        if (dimension.width > Quiz.getInstance().gridWidth || dimension.height > Quiz.getInstance().gridHeight) return;
        this.dimension = dimension;
        rectangle.setSize((int) (dimension.width * Quiz.getInstance().gridWidth + 1), (int) (dimension.height * Quiz.getInstance().gridHeight + 1));
    }

    public Dimension getDimension() {
        return dimension;
    }
    public Rectangle getRectangle() {
        return rectangle;
    }

    @Override
    public void render(Graphics g) {
        if (getLocation() == null) return;
        if (getDimension() == null) return;

        g.setColor(bgColor);
        g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

        g.setColor(fontColor);
        g.setFont(font);
        if (!content.isEmpty()) g.drawString(toString(),
                rectangle.x + rectangle.width/2 - g.getFontMetrics().stringWidth(toString())/2,
                rectangle.y + rectangle.height/2 + g.getFontMetrics().getAscent()/3);
    }
}
