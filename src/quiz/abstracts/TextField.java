package quiz.abstracts;

import quiz.CustomColors;
import quiz.states.Quiz;

import java.awt.*;
import java.awt.event.KeyEvent;

public abstract class TextField extends QuizObject {

    public Color borderColor = CustomColors.YELLOW_GREEN;
    public Color bgColor;
    public boolean focus = false;
    public int activeChar = 0;
    public boolean textChar = System.currentTimeMillis() % 1000 < 500;
    public Color textCharColor = CustomColors.ALICE_BLUE;

    protected Dimension dimension;

    public void removeContent(KeyEvent e) {
        if (e.isControlDown()) switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE -> {
                if (activeChar <= 0) break;
                char deleted = content.charAt(activeChar - 1);
                content.deleteCharAt(activeChar - 1);
                activeChar--;
                if (deleted != ' ') removeContent(e);
            }
            case KeyEvent.VK_DELETE -> {
                if (activeChar >= content.length()) break;
                char deleted = content.charAt(activeChar);
                content.deleteCharAt(activeChar);
                if (deleted != ' ') removeContent(e);
            }
        } else switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE -> {
                if (activeChar <= 0) break;
                content.deleteCharAt(activeChar - 1);
                activeChar--;
            }
            case KeyEvent.VK_DELETE -> {
                if (activeChar >= content.length()) break;
                content.deleteCharAt(activeChar);
            }
        }
    }

    public void moveCursorTo(KeyEvent e) {
        if (e.isControlDown()) switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> {
                if (activeChar < content.length()-1 && content.charAt(activeChar+1) != ' ') {
                    activeChar++;
                    moveCursorTo(e);
                } else if (activeChar < content.length()) activeChar++;
            }
            case KeyEvent.VK_LEFT -> {
                if (activeChar > 1 && content.charAt(activeChar-2) != ' ') {
                    activeChar--;
                    moveCursorTo(e);
                } else if (activeChar == 1 || content.charAt(activeChar-1) != ' ') activeChar--;
            }
        }
        else switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> {
                if (activeChar < content.length()) activeChar++;
            }
            case KeyEvent.VK_LEFT -> {
                if (activeChar > 0) activeChar--;
            }
            case KeyEvent.VK_BEGIN, KeyEvent.VK_HOME, KeyEvent.VK_PAGE_DOWN -> activeChar = 0;
            case KeyEvent.VK_END, KeyEvent.VK_PAGE_UP -> activeChar = content.length();
        }
    }

    public void addContent(KeyEvent e) {
        if (activeChar == content.length()) content.append(e.getKeyChar());
        else content.insert(activeChar, e.getKeyChar());
        activeChar++;
    }

    public void setDimension(Dimension dimension) {
        if (dimension.width > Quiz.getInstance().gridWidth || dimension.height > Quiz.getInstance().gridHeight) return;
        this.dimension = dimension;
        rectangle.setSize((int) (dimension.width * Quiz.getInstance().gridWidth + 1), (int) (dimension.height * Quiz.getInstance().gridHeight + 1));
    }
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public void render(Graphics g) {
        if (getLocation() == null) return;
        if (getDimension() == null) return;

        int textOffset = 10;
        textChar = System.currentTimeMillis() % 1000 < 500;

        if (bgColor != null) {
            g.setColor(bgColor);
            g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }

        g.setColor(borderColor);
        g.drawRoundRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height, (int) (Quiz.getInstance().gridHeight/4), (int) (Quiz.getInstance().gridHeight/4));

        g.setColor(fontColor);
        g.setFont(font);
        if (!content.isEmpty()) g.drawString(toString(),
                rectangle.x + textOffset,
                rectangle.y + rectangle.height/2 + g.getFontMetrics().getAscent()/3);

        g.setColor(textCharColor);
        if (focus && textChar)
            g.drawLine(rectangle.x + textOffset + g.getFontMetrics().stringWidth(content.isEmpty() || activeChar == 0 ? "" : content.substring(0, activeChar)), rectangle.y + 5,
                    rectangle.x + textOffset + g.getFontMetrics().stringWidth(content.isEmpty() || activeChar == 0 ? "" : content.substring(0, activeChar)), rectangle.y + rectangle.height - 5);
    }
}
