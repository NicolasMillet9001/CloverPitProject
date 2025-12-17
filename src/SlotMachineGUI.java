import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Objects;

public class SlotMachineGUI extends JFrame {
    private SlotMachine slotMachine;
    private SymbolManager symbolManager;
    private BufferedImage backgroundImage;

    // Composants GUI
    private JPanel mainContainer; // Le panel qui change (CardLayout)
    private CardLayout cardLayout;
    private JButton spinButton; // On le garde en attribut pour pouvoir le désactiver
    private JButton infoButton;

    // Animation vars
    private Timer animationTimer;
    private boolean isSpinning = false;
    private long spinStartTime;
    private Symbol[][] tempGrid;

    // Noms des écrans pour le CardLayout
    private final String CARD_GAME = "GAME";
    private final String CARD_INFO = "INFO";
    private boolean isInfoScreenVisible = false;

    public SlotMachineGUI() {
        slotMachine = new SlotMachine();
        symbolManager = new SymbolManager();
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

        // --- 1. CONFIGURATION DU CARD LAYOUT (Le cœur du changement) ---
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Création des deux écrans
        JPanel gamePanel = new SlotMachinePanel();
        JPanel infoPanel = new InfoPanel(symbolManager); // Notre nouvelle classe InfoPanel

        // Ajout au container
        mainContainer.add(gamePanel, CARD_GAME);
        mainContainer.add(infoPanel, CARD_INFO);

        // --- 2. BARRE DE BOUTONS ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Bouton SPIN
        spinButton = new JButton("SPIN !");
        spinButton.setPreferredSize(new Dimension(150, 50));
        spinButton.setFont(new Font("Arial", Font.BOLD, 20));
        spinButton.setBackground(new Color(50, 205, 50));
        spinButton.setForeground(Color.WHITE);
        spinButton.setFocusPainted(false);
        spinButton.addActionListener(e -> startSpin());

        // Bouton INFO (Toggle)
        infoButton = new JButton("?");
        infoButton.setPreferredSize(new Dimension(50, 50));
        infoButton.setFont(new Font("Arial", Font.BOLD, 20));
        infoButton.setBackground(new Color(70, 130, 180));
        infoButton.setForeground(Color.WHITE);
        infoButton.setFocusPainted(false);

        infoButton.addActionListener(e -> toggleInfoScreen());

        buttonPanel.add(spinButton);
        buttonPanel.add(infoButton);

        // --- 3. INPUT MAP (ESPACE) ---
        InputMap im = mainContainer.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainContainer.getActionMap();
        im.put(KeyStroke.getKeyStroke("SPACE"), "spinAction");
        am.put("spinAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // On ne spin que si on est sur l'écran de jeu
                if (!isInfoScreenVisible) {
                    startSpin();
                }
            }
        });

        // Assemblage final
        add(mainContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // --- Gestion du changement d'écran ---
    // Il faut que ton attribut infoPanel soit accessible ici.
    // Dans le constructeur, tu as fait "JPanel infoPanel = ...".
    // Transforme-le en attribut de classe "private InfoPanel infoPanel;" tout en haut.

    private void toggleInfoScreen() {
        if (isInfoScreenVisible) {
            // Retour au jeu
            cardLayout.show(mainContainer, CARD_GAME);
            infoButton.setText("?");
            infoButton.setBackground(new Color(70, 130, 180));
            spinButton.setEnabled(true);
            isInfoScreenVisible = false;
            mainContainer.requestFocusInWindow();
        } else {
            // ALLER AUX INFOS

            // C'EST ICI LA CLÉ : On demande au panel de se mettre à jour
            // Il va relire les variables statiques de Symbol (chances, valeurs)
            // et redessiner le tableau.
            ((InfoPanel) mainContainer.getComponent(1)).updateInfo();
            // Note: le cast (InfoPanel) est nécessaire si tu l'as stocké comme JPanel

            cardLayout.show(mainContainer, CARD_INFO);
            infoButton.setText("X");
            infoButton.setBackground(new Color(200, 50, 50));
            spinButton.setEnabled(false);
            isInfoScreenVisible = true;
        }
    }

    // --- LOGIQUE ANIMATION (Reste identique) ---
    private void startSpin() {
        if (isSpinning) return;
        isSpinning = true;
        spinStartTime = System.currentTimeMillis();
        animationTimer.start();
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - spinStartTime < 10) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    tempGrid[i][j] = new Symbol();
                }
            }
            mainContainer.repaint(); // Important : repaint le container principal
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

    // --- CLASSE INTERNE SLOTMACHINEPANEL (Reste identique) ---
    private class SlotMachinePanel extends JPanel {
        private static final double REF_SCREEN_W = 800.0;
        private static final double REF_SCREEN_H = 600.0;
        private static final double REF_START_X = 133.0;
        private static final double REF_START_Y = 130.0;
        private static final double REF_SYMBOL_W = 85.0;
        private static final double REF_SYMBOL_H = 85.0;
        private static final double REF_STEP_X = 109.0;
        private static final double REF_STEP_Y = 123.0;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

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

            Symbol[][] gridToDraw;
            boolean[][] winners = null;

            if (isSpinning) {
                gridToDraw = tempGrid;
            } else {
                gridToDraw = slotMachine.getSymbols();
                winners = slotMachine.getWinningCells();
            }

            if (gridToDraw[0][0] == null && !isSpinning) return;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    if (gridToDraw[i][j] == null) continue;

                    double refX = REF_START_X + (j * REF_STEP_X);
                    double refY = REF_START_Y + (i * REF_STEP_Y);

                    int finalX = (int) (refX * scaleX);
                    int finalY = (int) (refY * scaleY);

                    Symbol symbol = gridToDraw[i][j];
                    String nomSymbole = symbol.GetSymbolType().toString();
                    BufferedImage symbolImage = symbolManager.getSymbolImage(nomSymbole);

                    if (symbolImage != null) {
                        g.drawImage(symbolImage, finalX, finalY, actualSymbolW, actualSymbolH, this);
                    } else {
                        g.setColor(Color.RED);
                        g.drawRect(finalX, finalY, actualSymbolW, actualSymbolH);
                    }

                    if (!isSpinning && winners != null && winners[i][j]) {
                        g2d.setColor(new Color(255, 215, 0));
                        g2d.setStroke(new BasicStroke((float)(5 * scaleX)));
                        int margin = (int)(5 * scaleX);
                        g2d.drawRect(finalX - margin, finalY - margin, actualSymbolW + (margin*2), actualSymbolH + (margin*2));
                    }
                }
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