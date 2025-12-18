import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Objects;

public class SlotMachineGUI extends JFrame {
    private SlotMachine slotMachine;
    private SymbolManager gameSymbolManager;
    private SymbolManager panelSymbolManager;
    private BufferedImage backgroundImage;

    // Composants GUI
    private JPanel mainContainer;
    private CardLayout cardLayout;
    private JButton spinButton;
    private JButton infoButton;

    // Animation vars
    private Timer animationTimer;
    private boolean isSpinning = false;
    private long spinStartTime;
    private Symbol[][] tempGrid;

    // Noms des écrans
    private final String CARD_GAME = "GAME";
    private final String CARD_INFO = "INFO";
    private boolean isInfoScreenVisible = false;

    // --- Panel Infos (Déclaré ici pour pouvoir l'appeler) ---
    private InfoPanel infoPanel;

    public SlotMachineGUI() {
        slotMachine = new SlotMachine();
        // Modification chemins images
        gameSymbolManager = new SymbolManager("medias/symbols_slot");
        panelSymbolManager = new SymbolManager("medias/symbols_panel");

        tempGrid = new Symbol[3][5];

        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/SlotScreen.png")));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur chargement fond.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Slot Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        animationTimer = new Timer(60, e -> updateAnimation());

        // --- 1. CONFIGURATION CARD LAYOUT ---
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Création des écrans
        JPanel gamePanel = new SlotMachinePanel();
        infoPanel = new InfoPanel(panelSymbolManager); // On utilise le manager dédié au panel

        mainContainer.add(gamePanel, CARD_GAME);
        mainContainer.add(infoPanel, CARD_INFO);

        // --- 2. BOUTONS ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        spinButton = new JButton("SPIN !");
        spinButton.setPreferredSize(new Dimension(150, 50));
        spinButton.setFont(new Font("Arial", Font.BOLD, 20));
        spinButton.setBackground(new Color(50, 205, 50));
        spinButton.setForeground(Color.WHITE);
        spinButton.setFocusPainted(false);
        spinButton.addActionListener(e -> startSpin());

        infoButton = new JButton("?");
        infoButton.setPreferredSize(new Dimension(50, 50));
        infoButton.setFont(new Font("Arial", Font.BOLD, 20));
        infoButton.setBackground(new Color(70, 130, 180));
        infoButton.setForeground(Color.WHITE);
        infoButton.setFocusPainted(false);
        infoButton.addActionListener(e -> toggleInfoScreen());

        buttonPanel.add(spinButton);
        buttonPanel.add(infoButton);

        // --- 3. INPUT MAP ---
        InputMap im = mainContainer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainContainer.getActionMap();
        im.put(KeyStroke.getKeyStroke("SPACE"), "spinAction");
        am.put("spinAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isInfoScreenVisible) {
                    startSpin();
                }
            }
        });

        add(mainContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void toggleInfoScreen() {
        if (isInfoScreenVisible) {
            cardLayout.show(mainContainer, CARD_GAME);
            infoButton.setText("?");
            infoButton.setBackground(new Color(70, 130, 180));
            spinButton.setEnabled(true);
            isInfoScreenVisible = false;
            mainContainer.requestFocusInWindow();
        } else {
            // Mise à jour des infos avant d'afficher
            infoPanel.updateInfo();

            cardLayout.show(mainContainer, CARD_INFO);
            infoButton.setText("X");
            infoButton.setBackground(new Color(200, 50, 50));
            spinButton.setEnabled(false);
            isInfoScreenVisible = true;
        }
    }

    private void startSpin() {
        if (isSpinning)
            return;
        isSpinning = true;
        spinStartTime = System.currentTimeMillis();
        animationTimer.start();
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - spinStartTime < 750) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    tempGrid[i][j] = new Symbol();
                }
            }
            mainContainer.repaint();
        } else {
            stopSpin();
        }
    }

    private void stopSpin() {
        animationTimer.stop();
        isSpinning = false;
        slotMachine.spin();
        mainContainer.repaint();
    }

    // --- PANNEAU DE JEU AVEC SCORE GRAPHIQUE ---
    private class SlotMachinePanel extends JPanel {
        // Constantes de grille
        private static final double REF_SCREEN_W = 800.0;
        private static final double REF_SCREEN_H = 600.0;
        private static final double REF_START_X = 138.0;
        private static final double REF_START_Y = 130.0;
        private static final double REF_SYMBOL_W = 85.0;
        private static final double REF_SYMBOL_H = 85.0;
        private static final double REF_STEP_X = 107.0;
        private static final double REF_STEP_Y = 123.0;

        // Constantes pour l'affichage du SCORE (Bas-Gauche)
        private static final double REF_SCORE_X = 30.0; // Position X (Gauche)
        private static final double REF_SCORE_Y = 530.0; // Position Y (Bas)
        private static final double REF_SCORE_W = 200.0; // Largeur du cadre score
        private static final double REF_SCORE_H = 60.0; // Hauteur du cadre score

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // 1. Image de fond
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, this);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, panelWidth, panelHeight);
            }

            double scaleX = (double) panelWidth / REF_SCREEN_W;
            double scaleY = (double) panelHeight / REF_SCREEN_H;
            int actualSymbolW = (int) (REF_SYMBOL_W * scaleX);
            int actualSymbolH = (int) (REF_SYMBOL_H * scaleY);

            // --- 2. AFFICHAGE DE LA GRILLE ---
            Symbol[][] gridToDraw;
            boolean[][] winners = null;

            if (isSpinning) {
                gridToDraw = tempGrid;
            } else {
                gridToDraw = slotMachine.getSymbols();
                winners = slotMachine.getWinningCells();
            }

            if (gridToDraw[0][0] != null) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 5; j++) {
                        if (gridToDraw[i][j] == null)
                            continue;

                        double refX = REF_START_X + (j * REF_STEP_X);
                        double refY = REF_START_Y + (i * REF_STEP_Y);

                        int finalX = (int) (refX * scaleX);
                        int finalY = (int) (refY * scaleY);

                        Symbol symbol = gridToDraw[i][j];
                        String nomSymbole = symbol.GetSymbolType().toString();
                        BufferedImage symbolImage = gameSymbolManager.getSymbolImage(nomSymbole);

                        if (symbolImage != null) {
                            // Réduction de 20% (0.8)
                            int reducedW = (int) (actualSymbolW * 0.8);
                            int reducedH = (int) (actualSymbolH * 0.8);

                            // Centrage avec la nouvelle taille
                            int offsetX = (actualSymbolW - reducedW) / 2;
                            int offsetY = (actualSymbolH - reducedH) / 2;

                            g.drawImage(symbolImage, finalX + offsetX, finalY + offsetY, reducedW, reducedH, this);
                        } else {
                            g.setColor(Color.RED);
                            g.drawRect(finalX, finalY, actualSymbolW, actualSymbolH);
                        }

                        // Cadre gagnant
                        if (!isSpinning && winners != null && winners[i][j]) {
                            g2d.setColor(new Color(255, 215, 0));
                            g2d.setStroke(new BasicStroke((float) (5 * scaleX)));
                            int margin = (int) (5 * scaleX);
                            g2d.drawRect(finalX - margin, finalY - margin, actualSymbolW + (margin * 2),
                                    actualSymbolH + (margin * 2));
                        }
                    }
                }
            }

            // --- 3. AFFICHAGE DU SCORE ---

            // Calcul position du cadre score
            int scoreX = (int) (REF_SCORE_X * scaleX);
            int scoreY = (int) (REF_SCORE_Y * scaleY);
            int scoreW = (int) (REF_SCORE_W * scaleX);
            int scoreH = (int) (REF_SCORE_H * scaleY);

            // Fond du score (Noir semi-transparent) -> RETIRÉ
            // g2d.setColor(new Color(0, 0, 0, 200));
            // g2d.fillRoundRect(scoreX, scoreY, scoreW, scoreH, 20, 20);

            // Bordure du score (Or) -> RETIRÉ
            // g2d.setColor(new Color(255, 215, 0));
            // g2d.setStroke(new BasicStroke((float) (3 * scaleX)));
            // g2d.drawRoundRect(scoreX, scoreY, scoreW, scoreH, 20, 20);

            // Texte du score
            // On récupère le score (entier)
            int currentScore = (int) slotMachine.GetScore();
            String scoreText = String.valueOf(currentScore);

            // Police qui s'adapte à la taille
            int fontSize = (int) (30 * scaleY);
            g2d.setFont(new Font("Monospaced", Font.BOLD, fontSize)); // Monospaced fait plus "digital"

            FontMetrics metrics = g2d.getFontMetrics();

            // Icone Coin
            BufferedImage coinImg = gameSymbolManager.getSymbolImage("Coin");
            int iconSize = (int) (40 * scaleY);
            int iconY = scoreY + (scoreH - iconSize) / 2;
            int textY = scoreY + ((scoreH - metrics.getHeight()) / 2) + metrics.getAscent();

            if (coinImg != null) {
                g.drawImage(coinImg, scoreX + 10, iconY, iconSize, iconSize, this);
                g2d.setColor(Color.WHITE);
                g2d.drawString(scoreText, scoreX + 10 + iconSize + 10, textY);
            } else {
                // Fallback si pas d'image
                g2d.setColor(Color.WHITE);
                g2d.drawString("C: " + scoreText, scoreX + 20, textY);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SlotMachineGUI gui = new SlotMachineGUI();
            gui.setVisible(true);
        });
    }
}