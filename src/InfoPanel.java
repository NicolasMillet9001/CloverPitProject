import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class InfoPanel extends JPanel {

    private SymbolManager symbolManager;
    private JPanel symbolsContainer;
    private JPanel patternsContainer;

    // Listes pour la mise à l'échelle du texte
    private List<JLabel> textLabels = new ArrayList<>();
    private List<JLabel> titleLabels = new ArrayList<>();

    public InfoPanel(SymbolManager manager) {
        this.symbolManager = manager;

        setLayout(new GridLayout(1, 2, 20, 0)); // 2 colonnes
        setBackground(new Color(40, 40, 40));

        // Initialisation des conteneurs
        symbolsContainer = new JPanel();
        symbolsContainer.setLayout(new BoxLayout(symbolsContainer, BoxLayout.Y_AXIS));
        symbolsContainer.setBackground(new Color(40, 40, 40));

        patternsContainer = new JPanel();
        patternsContainer.setLayout(new BoxLayout(patternsContainer, BoxLayout.Y_AXIS));
        patternsContainer.setBackground(new Color(40, 40, 40));

        // Ajout (avec ScrollPane invisible)
        add(createScrollPane(symbolsContainer));
        add(createScrollPane(patternsContainer));

        // Listener Responsive
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayoutScale();
            }
        });

        // Premier chargement
        updateInfo();
    }

    public void updateInfo() {
        // 1. Reset
        symbolsContainer.removeAll();
        patternsContainer.removeAll();
        textLabels.clear();
        titleLabels.clear();

        // 2. Colonne GAUCHE (Symboles)
        addTitle(symbolsContainer, "--- SYMBOLES ---");
        addHeader(symbolsContainer, "ICÔNE", "VALEUR", "CHANCE");

        for (Symbol.EnumSymbolType type : Symbol.EnumSymbolType.values()) {
            addSymbolRow(type);
        }

        // 3. Colonne DROITE (Patterns)
        addTitle(patternsContainer, "--- PATTERNS ---");
        addHeader(patternsContainer, "FORME", "NOM", "MULT");

        // --- DEFINITION DES PATTERNS (ORDRE MODIFIÉ) ---

        // 1. Ligne de 3 (Centrée)
        boolean[][] p3 = new boolean[3][5];
        p3[1][1] = true;
        p3[1][2] = true;
        p3[1][3] = true;
        double m3 = Pattern.GetMultiplier(Pattern.PatternType.horizontal3);
        addPatternRow(p3, "Ligne de 3", "x " + m3);

        // 2. Colonne (Centrée)
        boolean[][] pCol = new boolean[3][5];
        pCol[0][2] = true;
        pCol[1][2] = true;
        pCol[2][2] = true;
        double mVert = Pattern.GetMultiplier(Pattern.PatternType.vertical3);
        addPatternRow(pCol, "Colonne", "x " + mVert);

        // 3. Diagonale (Centrée)
        boolean[][] pDiag = new boolean[3][5];
        pDiag[0][1] = true;
        pDiag[1][2] = true;
        pDiag[2][3] = true;
        double mDiag = Pattern.GetMultiplier(Pattern.PatternType.diagonal);
        addPatternRow(pDiag, "Diagonale", "x " + mDiag);

        // 4. Ligne de 4 (Centrée)
        boolean[][] p4 = new boolean[3][5];
        p4[1][0] = true;
        p4[1][1] = true;
        p4[1][2] = true;
        p4[1][3] = true;
        double m4 = Pattern.GetMultiplier(Pattern.PatternType.horizontal4);
        addPatternRow(p4, "Ligne de 4", "x " + m4);

        // 5. Ligne de 5
        boolean[][] p5 = new boolean[3][5];
        for (int k = 0; k < 5; k++)
            p5[1][k] = true;
        double m5 = Pattern.GetMultiplier(Pattern.PatternType.horizontal5);
        addPatternRow(p5, "Ligne de 5", "x " + m5);

        // 6. V Classique (Pointe en bas)
        boolean[][] pV = new boolean[3][5];
        pV[0][0] = true;
        pV[1][1] = true;
        pV[2][2] = true;
        pV[1][3] = true;
        pV[0][4] = true;
        double mZig = Pattern.GetMultiplier(Pattern.PatternType.zigzag);
        addPatternRow(pV, "Zig", "x " + mZig);

        // 7. V Inversé (Pointe en haut - "Chapeau")
        boolean[][] pVInv = new boolean[3][5];
        pVInv[2][0] = true;
        pVInv[1][1] = true;
        pVInv[0][2] = true;
        pVInv[1][3] = true;
        pVInv[2][4] = true;
        addPatternRow(pVInv, "Zag", "x " + mZig);

        // 8. Triangle Classique (Pointe en haut, Base pleine en bas)
        boolean[][] pTri = new boolean[3][5];
        pTri[0][2] = true; // Pointe haut
        pTri[1][1] = true;
        pTri[1][3] = true; // Côtés
        for (int k = 0; k < 5; k++)
            pTri[2][k] = true; // Toute la ligne du bas
        double mTri = Pattern.GetMultiplier(Pattern.PatternType.triangle);
        addPatternRow(pTri, "Triangle", "x " + mTri);

        // 9. Triangle Inversé (Pointe en bas, Base pleine en haut)
        boolean[][] pTriInv = new boolean[3][5];
        pTriInv[2][2] = true; // Pointe bas
        pTriInv[1][1] = true;
        pTriInv[1][3] = true; // Côtés
        for (int k = 0; k < 5; k++)
            pTriInv[0][k] = true; // Toute la ligne du haut
        addPatternRow(pTriInv, "Tri. Inversé", "x " + mTri);

        // 10. Jackpot
        boolean[][] pFull = new boolean[3][5];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 5; j++)
                pFull[i][j] = true;
        double mJack = Pattern.GetMultiplier(Pattern.PatternType.jackpot);
        addPatternRow(pFull, "JACKPOT", "x " + mJack);

        // Rafraîchissement
        updateLayoutScale();
        revalidate();
        repaint();
    }

    private void updateLayoutScale() {
        int h = getHeight();
        if (h == 0)
            h = 600;

        int titleSize = Math.max(20, h / 25);
        int textSize = Math.max(12, h / 45);

        for (JLabel l : titleLabels)
            l.setFont(new Font("Arial", Font.BOLD, titleSize));
        for (JLabel l : textLabels)
            l.setFont(new Font("Arial", Font.BOLD, textSize));
    }

    // --- Helpers ---

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(40, 40, 40));
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return sp;
    }

    private void addTitle(JPanel p, String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setForeground(Color.LIGHT_GRAY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabels.add(l);
        p.add(l);
        p.add(Box.createVerticalStrut(20));
    }

    private void addHeader(JPanel p, String... texts) {
        JPanel h = new JPanel(new GridLayout(1, texts.length));
        h.setBackground(new Color(60, 60, 60));
        h.setMaximumSize(new Dimension(2000, 40));
        for (String s : texts) {
            JLabel l = new JLabel(s, SwingConstants.CENTER);
            l.setForeground(Color.GRAY);
            textLabels.add(l);
            h.add(l);
        }
        p.add(h);
        p.add(Box.createVerticalStrut(10));
    }

    private void addSymbolRow(Symbol.EnumSymbolType type) {
        JPanel row = new JPanel(new GridLayout(1, 3));
        row.setBackground(new Color(40, 40, 40));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(80, 80, 80)));
        row.setMaximumSize(new Dimension(2000, 60));

        JLabel iconLabel = new JLabel();
        BufferedImage img = symbolManager.getSymbolImage(type.toString());
        if (img != null) {
            Image scaledImg = img.getScaledInstance(45, 45, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(scaledImg));
        }
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Value + Coin Icon
        JPanel valPanel = new JPanel(new GridBagLayout());
        valPanel.setBackground(new Color(40, 40, 40));

        JLabel val = new JLabel(String.valueOf((int) Symbol.getValue(type)), SwingConstants.RIGHT);
        val.setForeground(new Color(255, 215, 0));

        JLabel coinIcon = new JLabel();
        BufferedImage coinImg = symbolManager.getSymbolImage("Coin");
        if (coinImg != null) {
            Image scaledCoin = coinImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            coinIcon.setIcon(new ImageIcon(scaledCoin));
        } else {
            val.setText(val.getText() + " $");
        }

        valPanel.add(coinIcon); // Icone avant ou après ? User dit "affiche le Coin.png... et à côté le nombre"
                                // -> Icone puis Nombre ou Nombre puis Icone ?
        // "affiche le Coin.png ... et à côté le nombre" -> Sounds like Icon LEFT,
        // Number RIGHT.
        // But for price usually it's Number then Icon/Currency.
        // User said: "instead of displaying the value in dollar, display also the
        // Coin.png ...".
        // Let's put Coin then Number as requested for the main slot ("Coin.png ... et à
        // côté le nombre").
        // For panel: "affiche aussi le Coin.png ...".
        // I will do: [Coin Icon] [Value]

        // Re-reading user request: "Coin.png ... et à côté le nombre" (Slot)
        // "au lieu d'afficher la valeur en dollar, affiche aussi le Coin.png" (Panel)

        valPanel.removeAll();
        // Add components with minimal gap
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 5); // Right padding
        valPanel.add(coinIcon, gbc);

        gbc.insets = new Insets(0, 0, 0, 0); // Reset
        valPanel.add(val, gbc);

        // Note: textLabels logic might break if I don't add 'val' to it?
        // The original code added 'val' to 'textLabels' for scaling.
        textLabels.add(val);

        JLabel chance = new JLabel(String.format("%.1f%%", Symbol.GetChance(type)), SwingConstants.CENTER);
        chance.setForeground(Color.WHITE);
        textLabels.add(chance);

        row.add(iconLabel);
        row.add(valPanel);
        row.add(chance);
        symbolsContainer.add(row);
        symbolsContainer.add(Box.createVerticalStrut(5));
    }

    private void addPatternRow(boolean[][] pattern, String name, String mult) {
        JPanel row = new JPanel(new GridLayout(1, 3));
        row.setBackground(new Color(40, 40, 40));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(80, 80, 80)));
        row.setMaximumSize(new Dimension(2000, 60));

        JPanel previewContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        previewContainer.setBackground(new Color(40, 40, 40));
        previewContainer.add(new PatternPreview(pattern));

        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
        lblName.setForeground(Color.WHITE);
        textLabels.add(lblName);

        JLabel lblMult = new JLabel(mult, SwingConstants.CENTER);
        lblMult.setForeground(new Color(255, 215, 0));
        textLabels.add(lblMult);

        row.add(previewContainer);
        row.add(lblName);
        row.add(lblMult);

        patternsContainer.add(row);
        patternsContainer.add(Box.createVerticalStrut(5));
    }
}