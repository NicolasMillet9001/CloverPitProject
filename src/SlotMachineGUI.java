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

    // --- NOUVEAUX CHAMPS POUR L'ANIMATION ---
    private Timer animationTimer; // Le chrono qui fait changer les images
    private boolean isSpinning = false; // Pour savoir si ça tourne
    private long spinStartTime; // Pour mesurer les 2 secondes
    private Symbol[][] tempGrid; // Grille temporaire pour l'effet visuel

    public SlotMachineGUI() {
        // Initialise la machine à sous et le gestionnaire de symboles
        slotMachine = new SlotMachine();
        symbolManager = new SymbolManager();

        // On initialise une grille temporaire vide pour commencer
        tempGrid = new Symbol[3][5];

        // Charge l'image de fond
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/SlotScreen.png")));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Impossible de charger l'image de fond.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Configuration de la fenêtre
        setTitle("Slot Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        // setResizable(false); // Souvent mieux pour les jeux à fond fixe

        // --- CONFIGURATION DU TIMER D'ANIMATION ---
        // 60ms = Vitesse de défilement (effet Mario Kart rapide)
        animationTimer = new Timer(60, e -> updateAnimation());

        // Bouton pour faire tourner
        JButton spinButton = new JButton("SPIN !");
        spinButton.setFont(new Font("Arial", Font.BOLD, 20));
        spinButton.setBackground(new Color(50, 205, 50));
        spinButton.setForeground(Color.WHITE);
        spinButton.setFocusPainted(false);
        spinButton.addActionListener(e -> startSpin());

        // --- GESTION DE LA TOUCHE ESPACE ---
        // On utilise InputMap/ActionMap qui est plus robuste que KeyListener pour Swing
        JPanel mainPanel = new SlotMachinePanel();
        InputMap im = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = mainPanel.getActionMap();

        im.put(KeyStroke.getKeyStroke("SPACE"), "spinAction");
        am.put("spinAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSpin();
            }
        });

        // Ajout des composants
        add(mainPanel, BorderLayout.CENTER);
        add(spinButton, BorderLayout.SOUTH);
    }

    // --- LOGIQUE D'ANIMATION ---

    private void startSpin() {
        if (isSpinning) return; // On empêche de relancer si ça tourne déjà

        isSpinning = true;
        spinStartTime = System.currentTimeMillis();
        animationTimer.start(); // Lance le défilement visuel
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();

        // Si moins de 0.75s (750ms) se sont écoulées
        if (currentTime - spinStartTime < 750) {
            // On remplit la grille temporaire avec du "bruit" aléatoire
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    // On crée un nouveau symbole juste pour l'affichage (visuel uniquement)
                    tempGrid[i][j] = new Symbol();
                }
            }
            repaint(); // On redessine l'interface avec ces symboles temporaires
        }
        else {
            // Les 2 secondes sont finies : ON ARRÊTE TOUT ET ON DONNE LE VRAI RÉSULTAT
            stopSpin();
        }
    }

    private void stopSpin() {
        animationTimer.stop();
        isSpinning = false;

        // C'est ICI que le vrai calcul mathématique se fait (crédits, gains, etc.)
        slotMachine.spin();

        repaint(); // On redessine une dernière fois avec la vraie grille fixe
    }

    // --- Panneau personnalisé pour dessiner la machine à sous ---
    private class SlotMachinePanel extends JPanel {

        // --- TES CONSTANTES DE CALIBRAGE ---
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

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // 1. Fond
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, this);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, panelWidth, panelHeight);
            }

            // 2. Facteurs d'échelle
            double scaleX = (double) panelWidth / REF_SCREEN_W;
            double scaleY = (double) panelHeight / REF_SCREEN_H;
            int actualSymbolW = (int) (REF_SYMBOL_W * scaleX);
            int actualSymbolH = (int) (REF_SYMBOL_H * scaleY);

            // 3. Choix de la grille à afficher
            Symbol[][] gridToDraw;

            if (isSpinning) {
                // Si ça tourne, on dessine la grille temporaire (effet Mario Kart)
                gridToDraw = tempGrid;
            } else {
                // Si c'est arrêté, on dessine la vraie grille de la machine (résultat final)
                gridToDraw = slotMachine.getSymbols();
            }

            // Si la grille est vide (au tout premier lancement), on évite le crash
            if (gridToDraw[0][0] == null && !isSpinning) {
                // Optionnel : on pourrait forcer un spin initial ou laisser vide
                // Ici on laisse vide ou on dessine rien
                return;
            }

            // 4. Boucle d'affichage
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {

                    // Sécurité supplémentaire si la grille n'est pas init
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