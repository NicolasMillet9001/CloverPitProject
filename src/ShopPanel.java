import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

public class ShopPanel extends JPanel {
    private SlotMachine slotMachine;
    private JPanel centerContainer;
    private BufferedImage coinIcon;
    private Runnable onRerollCallback;
    private Runnable onCloseCallback; // To refresh main GUI if needed
    private JLabel moneyLabel;

    public ShopPanel(SlotMachine slotMachine, Runnable onRerollCallback, Runnable onCloseCallback) {
        this.slotMachine = slotMachine;
        this.onRerollCallback = onRerollCallback;
        this.onCloseCallback = onCloseCallback;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        try {
            coinIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/medias/symbols_panel/Coin.png")));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        // Header Panel (Title + Money)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("LUCKY CHARMS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Stencil", Font.BOLD, 48));
        titleLabel.setForeground(new Color(210, 180, 140));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Money Label
        moneyLabel = new JLabel("0");
        moneyLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        moneyPanel.setOpaque(false);
        moneyPanel.add(moneyLabel);

        // Add coin icon to money label initially if loaded
        if (coinIcon != null) {
            moneyLabel.setIcon(new ImageIcon(coinIcon.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            moneyLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            moneyLabel.setIconTextGap(10);
        }

        headerPanel.add(moneyPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Container for Items
        centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));
        centerContainer.setOpaque(false);
        add(centerContainer, BorderLayout.CENTER);

        // Bottom - Reroll Button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton rerollButton = new JButton("REROLL (10 Coins)");
        rerollButton.setFont(new Font("Arial", Font.BOLD, 20));
        rerollButton.setBackground(new Color(139, 69, 19)); // SaddleBrown
        rerollButton.setForeground(Color.WHITE);
        rerollButton.setFocusPainted(false);
        rerollButton.addActionListener(e -> {
            if (slotMachine.rerollShop()) {
                refreshItems();
                if (this.onRerollCallback != null)
                    this.onRerollCallback.run();
            } else {
                JOptionPane.showMessageDialog(this, "Not enough coins!");
            }
        });

        JButton closeButton = new JButton("NEXT ROUND");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setBackground(new Color(34, 139, 34)); // ForestGreen
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            if (this.onCloseCallback != null)
                this.onCloseCallback.run();
        });

        bottomPanel.add(rerollButton);
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initial load
        refreshItems();
    }

    public void refreshItems() {
        centerContainer.removeAll();
        updateMoneyDisplay();

        List<Item> items = slotMachine.getCurrentShopItems();

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        row1.setOpaque(false);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        row2.setOpaque(false);

        for (int i = 0; i < items.size(); i++) {
            JPanel itemPanel = createItemPanel(items.get(i), i);
            if (i < 2) {
                row1.add(itemPanel);
            } else {
                row2.add(itemPanel);
            }
        }

        centerContainer.add(Box.createVerticalGlue());
        centerContainer.add(row1);
        centerContainer.add(Box.createVerticalStrut(30));
        centerContainer.add(row2);
        centerContainer.add(Box.createVerticalGlue());

        centerContainer.revalidate();
        centerContainer.repaint();
    }

    private void updateMoneyDisplay() {
        int score = (int) slotMachine.GetScore();
        moneyLabel.setText(String.valueOf(score));
    }

    private JPanel createItemPanel(Item item, int index) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(60, 60, 60));
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        panel.setPreferredSize(new Dimension(160, 240)); // Increased size for name

        // Tooltip logic
        String tooltip = "<html><p width=\"150\">" + item.getDescription() + "</p></html>";
        panel.setToolTipText(tooltip);

        // Top: Name
        JLabel nameLabel = new JLabel("<html><div style='text-align: center;'>" + item.getName() + "</div></html>");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        panel.add(nameLabel, BorderLayout.NORTH);

        // Center: Image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setToolTipText(tooltip);
        try {
            if (item.getImagePath() != null) {
                BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getResource(item.getImagePath())));
                Image scaled = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception e) {
            imageLabel.setText("IMG");
        }
        panel.add(imageLabel, BorderLayout.CENTER);

        // Bottom: Price + Button
        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.setOpaque(false);
        bottom.setToolTipText(tooltip);

        // Price
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pricePanel.setBackground(Color.BLACK);
        pricePanel.setToolTipText(tooltip);

        JLabel priceLabel = new JLabel(String.valueOf(item.getPrice()));
        priceLabel.setForeground(new Color(255, 215, 0));
        priceLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        pricePanel.add(priceLabel);
        if (coinIcon != null) {
            pricePanel.add(new JLabel(new ImageIcon(coinIcon.getScaledInstance(15, 15, Image.SCALE_SMOOTH))));
        }
        bottom.add(pricePanel);

        // Buy Button
        JButton buyBtn = new JButton("BUY");
        buyBtn.setFont(new Font("Arial", Font.BOLD, 12));
        buyBtn.addActionListener(e -> {
            if (slotMachine.buyItem(index)) {
                // Item bought
                refreshItems();
                if (onRerollCallback != null)
                    onRerollCallback.run(); // Update credits in main UI
            } else {
                JOptionPane.showMessageDialog(ShopPanel.this, "Not enough coins!");
            }
        });
        bottom.add(buyBtn);

        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }
}
