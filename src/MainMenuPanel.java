import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class MainMenuPanel extends JPanel {

    private BufferedImage backgroundImage;
    private BufferedImage titleImage;
    private JButton playButton;
    private JButton restartButton;
    private JButton quitButton;
    private boolean isGameStarted = false;

    public MainMenuPanel(ActionListener onPlayClick, ActionListener onQuitClick, ActionListener onRestartClick) {
        setLayout(new GridBagLayout()); // Using GridBagLayout for centering

        // Load background
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/mainscreen.png")));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            setBackground(Color.DARK_GRAY); // Fallback color
        }

        // Load title
        try {
            titleImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/title.png")));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        // Constraints for layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(250, 0, 20, 0); // Increase top padding to make room for title image
        gbc.anchor = GridBagConstraints.CENTER;

        // Play/Resume Button
        playButton = createStyledButton("JOUER");
        playButton.addActionListener(onPlayClick);
        add(playButton, gbc);

        // Restart Button
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        restartButton = createStyledButton("RECOMMENCER");
        restartButton.addActionListener(onRestartClick);
        restartButton.setVisible(false); // Hidden by default
        add(restartButton, gbc);

        // Quit Button
        gbc.gridy = 2; // Move to next row
        gbc.insets = new Insets(20, 0, 0, 0);
        quitButton = createStyledButton("QUITTER");
        quitButton.addActionListener(onQuitClick);
        add(quitButton, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient Background
                if (getModel().isPressed()) {
                    g2.setColor(new Color(34, 139, 34).darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(50, 205, 50));
                } else {
                    g2.setColor(new Color(34, 139, 34)); // Forest Green
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Border
                g2.setColor(new Color(255, 215, 0)); // Gold
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);

                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics metrics = g2.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(250, 70));
        button.setFont(new Font("Arial", Font.BOLD, 28));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void setGameStarted(boolean started) {
        this.isGameStarted = started;
        playButton.setText(started ? "REPRENDRE" : "JOUER");
        restartButton.setVisible(started); // Only show restart if game is started
        playButton.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw Title Image
        if (titleImage != null) {
            // Scale it if necessary or draw centered
            // Let's ensure it's not too big.
            // Assume width around 400-600 px is good.

            int targetW = 600;
            int targetH = (int) ((double) titleImage.getHeight() / titleImage.getWidth() * targetW);

            if (targetW > getWidth() - 40) {
                targetW = getWidth() - 40;
                targetH = (int) ((double) titleImage.getHeight() / titleImage.getWidth() * targetW);
            }

            int x = (getWidth() - targetW) / 2;
            int y = 50; // Padding from top

            g.drawImage(titleImage, x, y, targetW, targetH, this);
        }
    }
}
