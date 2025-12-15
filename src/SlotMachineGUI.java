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
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/SlotScreen.png")));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Impossible de charger l'image de fond.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }

        // Configuration de la fenêtre
        setTitle("Slot Machine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Bouton pour faire tourner les symboles
        JButton spinButton = new JButton("Spin");
        spinButton.addActionListener(e -> {
            slotMachine.spin();
            repaint(); // Rafraîchit l'affichage
        });

        // Ajoute le bouton et le panneau de la machine à sous
        add(spinButton, BorderLayout.SOUTH);
        add(new SlotMachinePanel(), BorderLayout.CENTER);
    }

    // Panneau personnalisé pour dessiner la machine à sous
    private class SlotMachinePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Dessine l'image de fond
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }

            // Dessine les symboles
            Symbol[][] grid = slotMachine.getSymbols();
            int symbolWidth = 100;
            int symbolHeight = 100;
            int startX = 150;
            int startY = 100;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    int x = startX + j * symbolWidth;
                    int y = startY + i * symbolHeight;
                    Symbol symbol = grid[i][j];
                    BufferedImage symbolImage = symbolManager.getSymbolImage(symbol.GetNom().toString());
                    if (symbolImage != null) {
                        g.drawImage(symbolImage, x, y, symbolWidth, symbolHeight, this);
                    } else {
                        // Dessine un rectangle coloré si l'image n'est pas chargée
                        g.setColor(Color.LIGHT_GRAY);
                        g.fillRect(x, y, symbolWidth, symbolHeight);
                        g.setColor(Color.BLACK);
                        g.drawString(symbol.GetNom().toString(), x + 10, y + 20);
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
