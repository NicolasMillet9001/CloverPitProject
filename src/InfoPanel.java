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

        // --- DEFINITION DES PATTERNS CORRIGÉS ---

        // Ligne de 3 (Centrée)
        boolean[][] p3 = new boolean[3][5]; p3[1][1]=true; p3[1][2]=true; p3[1][3]=true;
        addPatternRow(p3, "Ligne de 3", "x 1.0");

        // Ligne de 4 (Centrée)
        boolean[][] p4 = new boolean[3][5]; p4[1][0]=true; p4[1][1]=true; p4[1][2]=true; p4[1][3]=true;
        addPatternRow(p4, "Ligne de 4", "x 2.0");

        // Ligne de 5
        boolean[][] p5 = new boolean[3][5]; for(int k=0;k<5;k++) p5[1][k]=true;
        addPatternRow(p5, "Ligne de 5", "x 5.0");

        // Colonne
        boolean[][] pCol = new boolean[3][5]; pCol[0][2]=true; pCol[1][2]=true; pCol[2][2]=true;
        addPatternRow(pCol, "Colonne", "x 1.0");

        // Diagonale (Centrée)
        boolean[][] pDiag = new boolean[3][5]; pDiag[0][1]=true; pDiag[1][2]=true; pDiag[2][3]=true;
        addPatternRow(pDiag, "Diagonale", "x 3.0");

        // --- FORMES SPÉCIALES CORRIGÉES ---

        // V Classique (Pointe en bas)
        // (0,0), (1,1) -> (2,2) <- (1,3), (0,4)
        boolean[][] pV = new boolean[3][5];
        pV[0][0]=true; pV[1][1]=true; pV[2][2]=true; pV[1][3]=true; pV[0][4]=true;
        addPatternRow(pV, "Forme V", "x 6.0");

        // V Inversé (Pointe en haut - "Chapeau")
        // (2,0), (1,1) -> (0,2) <- (1,3), (2,4)
        boolean[][] pVInv = new boolean[3][5];
        pVInv[2][0]=true; pVInv[1][1]=true; pVInv[0][2]=true; pVInv[1][3]=true; pVInv[2][4]=true;
        addPatternRow(pVInv, "V Inversé", "x 6.0");

        // Triangle Classique (Pointe en haut, Base pleine en bas)
        boolean[][] pTri = new boolean[3][5];
        pTri[0][2]=true; // Pointe haut
        pTri[1][1]=true; pTri[1][3]=true; // Côtés
        for(int k=0; k<5; k++) pTri[2][k]=true; // Toute la ligne du bas
        addPatternRow(pTri, "Triangle", "x 8.0");

        // Triangle Inversé (Pointe en bas, Base pleine en haut)
        boolean[][] pTriInv = new boolean[3][5];
        pTriInv[2][2]=true; // Pointe bas
        pTriInv[1][1]=true; pTriInv[1][3]=true; // Côtés
        for(int k=0; k<5; k++) pTriInv[0][k]=true; // Toute la ligne du haut
        addPatternRow(pTriInv, "Tri. Inversé", "x 8.0");

        // Jackpot
        boolean[][] pFull = new boolean[3][5]; for(int i=0;i<3;i++) for(int j=0;j<5;j++) pFull[i][j]=true;
        addPatternRow(pFull, "JACKPOT", "x 100");

        // Rafraîchissement
        updateLayoutScale();
        revalidate();
        repaint();
    }

    private void updateLayoutScale() {
        int h = getHeight();
        if (h == 0) h = 600;

        int titleSize = Math.max(20, h / 25);
        int textSize = Math.max(12, h / 45);

        for (JLabel l : titleLabels) l.setFont(new Font("Arial", Font.BOLD, titleSize));
        for (JLabel l : textLabels) l.setFont(new Font("Arial", Font.BOLD, textSize));
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

        JLabel val = new JLabel(String.valueOf((int)Symbol.getValue(type)) + " $", SwingConstants.CENTER);
        val.setForeground(new Color(255, 215, 0));
        textLabels.add(val);

        JLabel chance = new JLabel(String.format("%.1f%%", Symbol.getChance(type)), SwingConstants.CENTER);
        chance.setForeground(Color.WHITE);
        textLabels.add(chance);

        row.add(iconLabel);
        row.add(val);
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