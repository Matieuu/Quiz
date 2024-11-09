package quiz.abstracts;

import quiz.states.Quiz;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class StringObject extends QuizObject {
    @Override
    public void render(Graphics g) {
        double gridWidth = Quiz.getInstance().gridWidth,
                gridHeight = Quiz.getInstance().gridHeight;

        g.setColor(fontColor);
        g.setFont(font);

        if (!content.isEmpty()) {
            ArrayList<String> lines = new ArrayList<>() {{
                add(content.toString());
            }};

//            boolean correct = false;
//            while (!correct) {
//                correct = true;
//                for (int i = 0; i < lines.size(); i++) {
//                    String line = lines.get(i);
//                    if (g.getFontMetrics().stringWidth(line) > gridWidth*11) {
//                        correct = false;
//                        for (int j = line.split(" ").length-1; j > 0; j--) {
//                            if (g.getFontMetrics().stringWidth(line.substring(0, j)) < gridWidth*11) {
//                                lines.clear();
//                                lines.add(String.join(" ", Arrays.copyOfRange(line.split(" "), 0, j)));
//                                lines.add(String.join(" ", Arrays.copyOfRange(line.split(" "), j, line.length())));
//                            }
//                        }
//                    }
//                }
//            }

            FontMetrics fm = g.getFontMetrics();
            String[] words = content.toString().split(" ");
            StringBuilder line = new StringBuilder();
            int lineHeight = fm.getHeight(), maxWidth = (int) (gridWidth*11), yOffset = lineHeight;

            for (String word : words) {
                if (fm.stringWidth(line+word) > maxWidth) {
                    g.drawString(line.toString(), (int) (getLocation().x * gridWidth), (int) (getLocation().y * gridHeight) + yOffset);
                    line = new StringBuilder(word +" ");
                    yOffset += lineHeight;
                } else line.append(word).append(" ");
            }
            g.drawString(line.toString(), (int) (getLocation().x * gridWidth), (int) (getLocation().y * gridHeight) + yOffset);

//            for (int i = 0; i < lines.size(); i++) {
//                System.out.println(lines.get(i));
//                g.drawString(lines.get(i), (int) (getLocation().x * gridWidth),
//                        (i + 1) * (int) (getLocation().y * gridHeight + .5 * gridHeight + (double) fm.getAscent() / (3 * lines.size())));
//            }
        }
    }
}
