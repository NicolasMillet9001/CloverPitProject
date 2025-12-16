import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Objects;

public class SlotMachineGUI extends JFrame {
    private SlotMachine slotMachine;
    private SymbolManager symbolManager;
    private BufferedImage backgroundImage;

    public SlotMachineGUI() {
        // Initialise la machine à sous et le gestionnaire de symboles
        slotMachine = new SlotMachine();
        symbolManager = new SymbolManager();

        // Charge l'image de fond
        try {
            // Assurez-vous que le chemin est correct dans votre dossier resources
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/SlotScreen.png")));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Impossible de charger l'image de fond (/medias/SlotScreen.png).", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Configuration de la fenêtre
        setTitle("Slot Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); // Taille initiale recommandée
        setLocationRelativeTo(null); // Centrer à l'écran

        // Bouton pour faire tourner les symboles
        JButton spinButton = new JButton("SPIN !");
        spinButton.setFont(new Font("Arial", Font.BOLD, 20));
        spinButton.setBackground(new Color(50, 205, 50)); // Un vert "casino"
        spinButton.setForeground(Color.WHITE);
        spinButton.setFocusPainted(false);

        spinButton.addActionListener(e -> {
            slotMachine.spin();
            repaint(); // Rafraîchit l'affichage pour montrer les nouveaux symboles
        });

        // Ajoute le bouton et le panneau de la machine à sous
        // Le Panel est au centre pour prendre toute la place disponible
        add(new SlotMachinePanel(), BorderLayout.CENTER);
        // Le bouton est au sud
        add(spinButton, BorderLayout.SOUTH);
    }

    // --- Panneau personnalisé pour dessiner la machine à sous ---
    // --- Panneau personnalisé pour dessiner la machine à sous ---
    private class SlotMachinePanel extends JPanel {

        // --- CONSTANTES DE CALIBRAGE (Ajustées pour tes captures d'écran) ---

        // Taille de référence de l'image de fond
        private static final double REF_SCREEN_W = 800.0;
        private static final double REF_SCREEN_H = 600.0;

        // --- CORRECTION DU POINT DE DÉPART ---
        // J'ai augmenté ces valeurs pour décaler les symboles vers la droite et le bas.
        // Avant: 106.0 -> Maintenant: 125.0 (Décalage vers la droite)
        // Avant: 128.0 -> Maintenant: 155.0 (Décalage vers le bas)
        private static final double REF_START_X = 128.0;
        private static final double REF_START_Y = 132.0;

        // Taille des symboles (légèrement réduits pour qu'ils "respirent" dans la case)
        private static final double REF_SYMBOL_W = 85.0;
        private static final double REF_SYMBOL_H = 85.0;

        // ESPACEMENT (STEP)
        // Distance entre le début d'une case et le début de la suivante.
        // J'ai très légèrement ajusté pour bien tomber dans les cadres.
        private static final double REF_STEP_X = 111.0;
        private static final double REF_STEP_Y = 123.0;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // 1. Dessine l'image de fond
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, this);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0,0, panelWidth, panelHeight);
            }

            // 2. Calcul des facteurs d'échelle
            double scaleX = (double) panelWidth / REF_SCREEN_W;
            double scaleY = (double) panelHeight / REF_SCREEN_H;

            // 3. Taille réelle des symboles
            int actualSymbolW = (int) (REF_SYMBOL_W * scaleX);
            int actualSymbolH = (int) (REF_SYMBOL_H * scaleY);

            Symbol[][] grid = slotMachine.getSymbols();

            for (int i = 0; i < 3; i++) {       // Lignes (Y)
                for (int j = 0; j < 5; j++) {   // Colonnes (X)

                    // --- Calcul de la position ---
                    double refX = REF_START_X + (j * REF_STEP_X);
                    double refY = REF_START_Y + (i * REF_STEP_Y);

                    int finalX = (int) (refX * scaleX);
                    int finalY = (int) (refY * scaleY);

                    // --- Dessin ---
                    Symbol symbol = grid[i][j];
                    String nomSymbole = symbol.GetNom().toString();
                    BufferedImage symbolImage = symbolManager.getSymbolImage(nomSymbole);

                    if (symbolImage != null) {
                        g.drawImage(symbolImage, finalX, finalY, actualSymbolW, actualSymbolH, this);
                    } else {
                        // Debug visuel si image manquante
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