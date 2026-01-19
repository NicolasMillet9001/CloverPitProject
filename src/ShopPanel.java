
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class ShopPanel extends JPanel {
    private String[] imagePaths = {
            "/medias/symbols_shop/SymbolGoldenDiamond.png",
            "/medias/symbols_shop/SymbolGoldenBell.png",
            "/medias/symbols_shop/SymbolGoldenClover.png",
            "/medias/symbols_shop/SymbolGoldenChest.png",
            "/medias/symbols_shop/SymbolGoldenSeven.png"
    };

    private int[] prices = { 300, 400, 250, 350, 500 };
    private BufferedImage coinIcon;

    public ShopPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20)); // Dark background

        // Header
        JLabel headerLabel = new JLabel("LUCKY CHARMS", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Stencil", Font.BOLD, 48));
        headerLabel.setForeground(new Color(210, 180, 140)); // Tan/Wood color
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Content Panel (Pyramid Layout attempt)
        JPanel itemsPanel = new JPanel(new GridBagLayout());
        itemsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        try {
            coinIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/symbols_panel/Coin.png")));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        // Top Row (2 items)
        for (int i = 0; i < 2; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            itemsPanel.add(createItemPanel(i), gbc);
        }

        // Bottom Row (3 items)
        for (int i = 0; i < 3; i++) {
            gbc.gridx = i; // This aligns them left-ish, need to offset to center relative to top row?
            // easy hack: make grid width different or just center the panel
            // Actually GridBagLayout centers by default if we add to a Frame, but here we
            // add to itemsPanel.
        }

        // Let's redo loops to be specific for the pyramid look
        // Row 0: 2 items centered. To center 2 items over 3, we can use 6 columns.
        // Row 0 items at x=1 (width 2) and x=3 (width 2)? Or just simpler:
        // Row 0: Item 0, Item 1.
        // Row 1: Item 2, Item 3, Item 4.

        // Clean implementation:
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        row1.setOpaque(false);
        row1.add(createItemPanel(0));
        row1.add(createItemPanel(1));

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        row2.setOpaque(false);
        row2.add(createItemPanel(2));
        row2.add(createItemPanel(3));
        row2.add(createItemPanel(4));

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);
        centerContainer.add(Box.createVerticalGlue());
        centerContainer.add(row1);
        centerContainer.add(Box.createVerticalStrut(30));
        centerContainer.add(row2);
        centerContainer.add(Box.createVerticalGlue());

        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createItemPanel(int index) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(60, 60, 60)); // Dark container background
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        panel.setPreferredSize(new Dimension(140, 180));

        // Image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getResource(imagePaths[index])));
            // Resize if needed, roughly 100x100
            Image scaled = img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            imageLabel.setText("IMG?");
            imageLabel.setForeground(Color.RED);
        }
        panel.add(imageLabel, BorderLayout.CENTER);

        // Price Tag
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pricePanel.setBackground(Color.BLACK);

        JLabel priceLabel = new JLabel(String.valueOf(prices[index]));
        priceLabel.setForeground(new Color(255, 215, 0)); // Gold text
        priceLabel.setFont(new Font("Monospaced", Font.BOLD, 18));

        if (coinIcon != null) {
            Image scaledCoin = coinIcon.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            JLabel coinLabel = new JLabel(new ImageIcon(scaledCoin));
            pricePanel.add(priceLabel);
            pricePanel.add(coinLabel);
        } else {
            pricePanel.add(priceLabel);
        }

        panel.add(pricePanel, BorderLayout.SOUTH);

        return panel;
    }
}
