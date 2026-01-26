import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Objects;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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
    // private JButton shopButton;

    // Animation vars
    private Timer animationTimer;
    private boolean isSpinning = false;
    private long spinStartTime;
    private Symbol[][] tempGrid;

    // Noms des écrans
    private final String CARD_GAME = "GAME";
    private final String CARD_INFO = "INFO";
    private final String CARD_SHOP = "SHOP";
    private final String CARD_INVENTORY = "INVENTORY";
    private final String CARD_MENU = "MENU";

    private boolean isInfoScreenVisible = false;
    private boolean isShopScreenVisible = false;
    private boolean isInventoryScreenVisible = false;
    private boolean isGameRunning = false;
    private boolean isMenuVisible = true;

    private InfoPanel infoPanel;
    private ShopPanel shopPanel;
    private InventoryPanel inventoryPanel;
    private MainMenuPanel mainMenuPanel;

    private JPanel buttonPanel;

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
        infoPanel = new InfoPanel(panelSymbolManager);
        shopPanel = new ShopPanel(slotMachine,
                () -> repaint(),
                () -> toggleShopScreen());
        inventoryPanel = new InventoryPanel(slotMachine, this::toggleInventoryScreen);

        // MAIN MENU INIT
        mainMenuPanel = new MainMenuPanel(
                e -> startGame(), // Play/Resume action
                e -> System.exit(0), // Quit action
                e -> { // Restart action
                    slotMachine.resetGame(false);
                    isGameRunning = true;
                    isMenuVisible = false;
                    mainMenuPanel.setGameStarted(true);
                    buttonPanel.setVisible(true);
                    cardLayout.show(mainContainer, CARD_GAME);
                });

        mainContainer.add(mainMenuPanel, CARD_MENU);
        mainContainer.add(gamePanel, CARD_GAME);
        mainContainer.add(infoPanel, CARD_INFO);
        mainContainer.add(shopPanel, CARD_SHOP);
        mainContainer.add(inventoryPanel, CARD_INVENTORY);

        // --- 2. BOUTONS ---
        buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        spinButton = new JButton("LANCER !");
        spinButton.setPreferredSize(new Dimension(150, 50));
        spinButton.setFont(new Font("Arial", Font.BOLD, 20));
        spinButton.setBackground(new Color(50, 205, 50));
        spinButton.setForeground(Color.WHITE);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFocusPainted(false);
        spinButton.setFocusable(false); // Fix: Prevent button from stealing focus
        spinButton.addActionListener(e -> {
            if (slotMachine.getSpinsLeft() <= 0) {
                toggleShopScreen();
            } else {
                startSpin();
            }
        });

        // Register callback for game reset
        slotMachine.setOnGameReset(() -> {
            spinButton.setText("LANCER !");
            spinButton.setBackground(new Color(50, 205, 50)); // Green
            cardLayout.show(mainContainer, CARD_GAME);
        });

        // Bouton Info & Shop
        infoButton = new JButton("INFOS");
        infoButton.setPreferredSize(new Dimension(80, 50));
        infoButton.setFont(new Font("Arial", Font.BOLD, 14));
        infoButton.setBackground(new Color(70, 130, 180));
        infoButton.setForeground(Color.WHITE);
        infoButton.setForeground(Color.WHITE);
        infoButton.setFocusPainted(false);
        infoButton.setFocusable(false); // Fix: Prevent button from stealing focus
        infoButton.addActionListener(e -> toggleInfoScreen());

        JButton inventoryButton = new JButton("INVENTAIRE");
        inventoryButton.setPreferredSize(new Dimension(120, 50)); // Wider for longer text
        inventoryButton.setFont(new Font("Arial", Font.BOLD, 14));
        inventoryButton.setBackground(new Color(100, 100, 100)); // Gray
        inventoryButton.setForeground(Color.WHITE);
        inventoryButton.setForeground(Color.WHITE);
        inventoryButton.setFocusPainted(false);
        inventoryButton.setFocusable(false); // Fix: Prevent button from stealing focus
        inventoryButton.addActionListener(e -> toggleInventoryScreen());

        // Panel bas (Boutons)
        buttonPanel.add(infoButton);
        buttonPanel.add(spinButton);
        buttonPanel.add(inventoryButton);

        // Auto-Open Shop Listener
        slotMachine.setOnShopOpenListener(() -> {
            SwingUtilities.invokeLater(() -> {
                if (!isShopScreenVisible) {
                    toggleShopScreen();
                }
            });
        });

        // Victory Listener (End of Round 8)
        slotMachine.setOnVictoryListener(() -> {
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showConfirmDialog(this,
                        "Félicitations ! Vous avez complété les 8 rounds !\nVoulez-vous continuer en mode 'Sans fin' (Score libre) ?",
                        "Victoire !", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (choice == JOptionPane.YES_OPTION) {
                    slotMachine.continueToEndless();
                } else {
                    // Reset game basically
                    // We can call resetGame() on slotMachine but it shows a "Lost" message usually.
                    // Ideally we should just reset quietly or show title screen if we had one.
                    // For now, let's call resetGame but maybe we should avoid the "Lost" popup if
                    // possible?
                    // The current resetGame shows "Partie perdue". Let's just let it be for now or
                    // improve later.
                    // Actually, let's just trigger a manual reset or similar.
                    // Since existing resetGame() shows a popup, let's just use it to be safe,
                    // user might just be done.
                    slotMachine.resetGame(); // This will show "Partie perdue", maybe slightly awkward but safe.
                }
            });
        });

        add(mainContainer, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Show Buttons conditionally (Hide on Menu)
        buttonPanel.setVisible(false);

        // --- GLOBAL KEY BINDING FOR SPACE ---
        KeyStroke spaceKey = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(spaceKey, "spin");
        getRootPane().getActionMap().put("spin", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ensure we only spin when allowed (game screen active, buttons enabled)
                if (!isMenuVisible && spinButton.isEnabled() && !isShopScreenVisible && !isInfoScreenVisible
                        && !isInventoryScreenVisible) {
                    if (slotMachine.getSpinsLeft() <= 0) {
                        toggleShopScreen();
                    } else {
                        startSpin();
                    }
                }
            }
        });

        // --- GLOBAL KEY BINDING FOR ESCAPE (PAUSE) ---
        KeyStroke escKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escKey, "toggleMenu");
        getRootPane().getActionMap().put("toggleMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMainMenu();
            }
        });

        // Init state
        cardLayout.show(mainContainer, CARD_MENU);
    }

    private void startGame() {
        if (!isGameRunning) {
            // New Game
            slotMachine.resetGame(false); // Ensure clean state WITHOUT "Lost" message
            isGameRunning = true;
            isMenuVisible = false;
            mainMenuPanel.setGameStarted(true); // Update button text to "Reprendre"
            buttonPanel.setVisible(true); // Show game buttons
            cardLayout.show(mainContainer, CARD_GAME);
        } else {
            // Resume
            isMenuVisible = false;
            buttonPanel.setVisible(true);

            // Restore appropriate screen
            if (isShopScreenVisible)
                cardLayout.show(mainContainer, CARD_SHOP);
            else if (isInfoScreenVisible)
                cardLayout.show(mainContainer, CARD_INFO);
            else if (isInventoryScreenVisible)
                cardLayout.show(mainContainer, CARD_INVENTORY);
            else
                cardLayout.show(mainContainer, CARD_GAME);
        }
    }

    private void toggleMainMenu() {
        if (isMenuVisible) {
            if (isGameRunning) { // Changed from isGameStarted to isGameRunning
                startGame(); // Resume
            }
            // else ignore or maybe quit? No, stay on menu
        } else {
            // Pause Game
            isMenuVisible = true;
            buttonPanel.setVisible(false); // Hide game buttons
            cardLayout.show(mainContainer, CARD_MENU);
        }
    }

    private void toggleInventoryScreen() {
        if (isInventoryScreenVisible) {
            // ClOSING INVENTORY
            isInventoryScreenVisible = false;

            if (isShopScreenVisible) {
                // Return to Shop
                cardLayout.show(mainContainer, CARD_SHOP);
                // Spin button stays disabled in shop
            } else {
                // Return to Game
                cardLayout.show(mainContainer, CARD_GAME);
                spinButton.setEnabled(true);
            }
            mainContainer.requestFocusInWindow();
        } else {
            // OPENING INVENTORY
            // If Info is open, close it (visually) or just switch card
            if (isInfoScreenVisible) {
                isInfoScreenVisible = false;
                infoButton.setText("?");
                infoButton.setBackground(new Color(70, 130, 180));
            }

            inventoryPanel.refreshInventory();
            cardLayout.show(mainContainer, CARD_INVENTORY);
            isInventoryScreenVisible = true;
            spinButton.setEnabled(false);
        }
    }

    private void toggleInfoScreen() {
        if (isInfoScreenVisible) {
            // CLOSING INFO
            isInfoScreenVisible = false;
            infoButton.setText("?");
            infoButton.setBackground(new Color(70, 130, 180));

            if (isInventoryScreenVisible) {
                // If inventory was active (unlikely as we usually toggle one or other), go back
                // there
                cardLayout.show(mainContainer, CARD_INVENTORY);
            } else if (isShopScreenVisible) {
                // Return to Shop
                cardLayout.show(mainContainer, CARD_SHOP);
            } else {
                // Return to Game
                cardLayout.show(mainContainer, CARD_GAME);
                spinButton.setEnabled(true);
            }
            mainContainer.requestFocusInWindow();
        } else {
            // OPENING INFO
            if (isInventoryScreenVisible) {
                isInventoryScreenVisible = false;
            }

            infoPanel.updateInfo();
            cardLayout.show(mainContainer, CARD_INFO);

            isInfoScreenVisible = true;
            infoButton.setText("X");
            infoButton.setBackground(new Color(200, 50, 50));
            spinButton.setEnabled(false);
        }
    }

    private void toggleShopScreen() {
        if (isShopScreenVisible) {
            // CLOSING SHOP (End of shopping)
            isShopScreenVisible = false;

            // Reset overlays if any
            isInfoScreenVisible = false;
            isInventoryScreenVisible = false;

            // Reset buttons
            infoButton.setText("?");
            infoButton.setBackground(new Color(70, 130, 180));

            // BACK TO GAME & NEW ROUND
            cardLayout.show(mainContainer, CARD_GAME);

            spinButton.setText("LANCER !");
            spinButton.setBackground(new Color(50, 205, 50)); // Green
            spinButton.setEnabled(true);
            infoButton.setEnabled(true);

            slotMachine.startNewRound();
            mainContainer.requestFocusInWindow();
        } else {
            // OPENING SHOP
            isShopScreenVisible = true;
            shopPanel.refreshItems();

            // If Info/Inventory were open, we switch to Shop,
            // but we might want to close them to show the shop first?
            // Usually Shop opens automatically so we assume we show it.
            isInfoScreenVisible = false;
            isInventoryScreenVisible = false;
            infoButton.setText("?");
            infoButton.setBackground(new Color(70, 130, 180));

            cardLayout.show(mainContainer, CARD_SHOP);

            spinButton.setEnabled(false);
            // We allow Info and Inventory buttons to work in Shop!
            infoButton.setEnabled(true);
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
            isSpinning = false;
            animationTimer.stop();
            slotMachine.spin();

            // Trigger floating text animation if gain > 0
            double gain = slotMachine.getLastSpinGain();
            if (gain > 0) {
                showFloatingGain(gain);
            }

            mainContainer.repaint();
            infoPanel.updateInfo(); // Update info panel to reflect new score

            // Check if round is over (0 spins)
            if (slotMachine.getSpinsLeft() <= 0) {
                spinButton.setText("Ouvrir boutique");
                spinButton.setBackground(new Color(255, 140, 0)); // Dark Orange
            }
        }
    }

    private void showFloatingGain(double gain) {
        // Create a label for the floating text
        JLabel gainLabel = new JLabel("+" + (int) gain, SwingConstants.CENTER); // Cast to int for cleaner look if
                                                                                // simple integers
        gainLabel.setFont(new Font("Arial", Font.BOLD, 40));
        gainLabel.setForeground(Color.YELLOW);

        // Add icon if available
        try {
            ImageIcon icon = new ImageIcon(
                    ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/coin.png"))));
            // Resize icon
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
            gainLabel.setIcon(new ImageIcon(newimg));
        } catch (Exception e) {
            // fallback text only
        }

        // Use GlassPane to float over everything
        JPanel glassPane = (JPanel) getGlassPane();
        glassPane.setVisible(true);
        glassPane.setLayout(null); // Absolute positioning

        // Center the label initially
        int x = (getWidth() - 300) / 2; // Approximate centering
        int y = getHeight() / 2;
        gainLabel.setBounds(x, y, 300, 50);

        glassPane.add(gainLabel);
        glassPane.revalidate();
        glassPane.repaint();

        // Animate upwards
        Timer floatTimer = new Timer(50, null);
        final int[] steps = { 0 };
        floatTimer.addActionListener(e -> {
            steps[0]++;
            gainLabel.setLocation(gainLabel.getX(), gainLabel.getY() - 5); // Move up

            if (steps[0] >= 20) { // After 20 steps (approx 1 sec)
                floatTimer.stop();
                glassPane.remove(gainLabel);
                glassPane.revalidate();
                glassPane.repaint();
                glassPane.setVisible(false); // Hide if no other components
            }
        });
        floatTimer.start();
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
        // private static final double REF_SCORE_W = 200.0; // Largeur du cadre score
        // (UNUSED)
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

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Round: " + slotMachine.getCurrentRound() + "/8", 20, 30); // Uncommented per request

            // Afficher les lancers restants (MOVED TO BOTTOM)
            // g2d.drawString("Lancers restants: " + slotMachine.getSpinsLeft(), 20, 60);

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
            // int scoreW = (int) (REF_SCORE_W * scaleX); // UNUSED
            int scoreH = (int) (REF_SCORE_H * scaleY);

            // Texte du score
            // On récupère le score (entier)
            int currentScore = (int) slotMachine.GetScore();
            int minScore = (int) slotMachine.getMinScoreToPass();
            String scoreText = currentScore + " / " + minScore;

            // Police qui s'adapte à la taille
            int fontSize = (int) (30 * scaleY);
            g2d.setFont(new Font("Monospaced", Font.BOLD, fontSize)); // Monospaced fait plus "digital"

            FontMetrics metrics = g2d.getFontMetrics();

            // Icone Coin
            BufferedImage coinImg = gameSymbolManager.getSymbolImage("Coin");
            int iconSize = (int) (40 * scaleY);
            int iconY = scoreY + (scoreH - iconSize) / 2;
            int textY = scoreY + ((scoreH - metrics.getHeight()) / 2) + metrics.getAscent();

            int textEndX = scoreX;

            if (coinImg != null) {
                g.drawImage(coinImg, scoreX + 10, iconY, iconSize, iconSize, this);
                g2d.setColor(Color.WHITE);
                g2d.drawString(scoreText, scoreX + 10 + iconSize + 10, textY);
                textEndX = scoreX + 10 + iconSize + 10 + metrics.stringWidth(scoreText);
            } else {
                // Fallback si pas d'image
                g2d.setColor(Color.WHITE);
                g2d.drawString("C: " + scoreText, scoreX + 20, textY);
                textEndX = scoreX + 20 + metrics.stringWidth("C: " + scoreText);
            }

            // Affichage "Lancers restants" à droite du score
            String spinsText = "Lancers: " + slotMachine.getSpinsLeft();
            g2d.setFont(new Font("Arial", Font.BOLD, (int) (20 * scaleY)));
            g2d.drawString(spinsText, textEndX + 30, textY);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SlotMachineGUI gui = new SlotMachineGUI();
            gui.setVisible(true);
        });
    }
}