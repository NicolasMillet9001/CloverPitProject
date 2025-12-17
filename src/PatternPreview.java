import javax.swing.*;
import java.awt.*;

public class PatternPreview extends JPanel {
    private boolean[][] patternMap;

    public PatternPreview(boolean[][] pattern) {
        this.patternMap = pattern;
        // On augmente un peu la taille par défaut pour le grand écran
        this.setPreferredSize(new Dimension(80, 50));
        this.setBackground(new Color(45, 45, 45));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rows = 3;
        int cols = 5;

        int w = getWidth();
        int h = getHeight();

        // Calcul pour remplir l'espace tout en gardant des carrés
        int size = Math.min(w / cols, h / rows) - 2;

        // Centrage
        int offsetX = (w - (size + 2) * cols) / 2;
        int offsetY = (h - (size + 2) * rows) / 2;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = offsetX + j * (size + 2);
                int y = offsetY + i * (size + 2);

                if (patternMap[i][j]) {
                    g2d.setColor(new Color(255, 215, 0));
                    g2d.fillRect(x, y, size, size);
                } else {
                    g2d.setColor(new Color(80, 80, 80));
                    g2d.drawRect(x, y, size, size);
                }
            }
        }
    }
}