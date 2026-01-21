import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ShopPanel extends JPanel {
    private SlotMachine slotMachine;
    private JPanel centerContainer;
    private BufferedImage coinIcon;
    private Runnable onRerollCallback;
    private Runnable onCloseCallback;
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

        initHeader();

        // Center Container - Grid Layout to force items into one row
        centerContainer = new JPanel(new GridLayout(1, 0, 10, 10)); // 1 row, any cols, gaps
        centerContainer.setOpaque(false);
        // Add padding around the items
        centerContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(centerContainer, BorderLayout.CENTER);

        // Bottom - Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton rerollButton = new JButton("RELANCER (10 Pièces)");
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
                JOptionPane.showMessageDialog(this, "Pas assez de pièces !");
            }
        });

        JButton closeButton = new JButton("ROUND SUIVANT");
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

    private void initHeader() {
        // Header Panel using JLayeredPane for true centering
        JLayeredPane headerLayered = new JLayeredPane();
        headerLayered.setPreferredSize(new Dimension(800, 100)); // Base size

        // 1. Title (Centered in the whole pane)
        JLabel titleLabel = new JLabel("LUCKY CHARMS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Stencil", Font.BOLD, 48));
        titleLabel.setForeground(new Color(210, 180, 140));
        titleLabel.setBounds(0, 0, 800, 100);

        // 2. Money (Right aligned)
        moneyLabel = new JLabel("0");
        moneyLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        if (coinIcon != null) {
            moneyLabel.setIcon(new ImageIcon(coinIcon.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
            moneyLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            moneyLabel.setIconTextGap(10);
        }

        JPanel moneyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        moneyPanel.setOpaque(false);
        moneyPanel.add(moneyLabel);
        moneyPanel.setBounds(0, 0, 800, 100);

        headerLayered.add(titleLabel, JLayeredPane.DEFAULT_LAYER);
        headerLayered.add(moneyPanel, JLayeredPane.PALETTE_LAYER);

        // Resize listener to keep components aligned
        headerLayered.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int w = headerLayered.getWidth();
                int h = headerLayered.getHeight();
                titleLabel.setBounds(0, 0, w, h);
                moneyPanel.setBounds(0, 25, w, h); // Offset slightly down
            }
        });

        add(headerLayered, BorderLayout.NORTH);
    }

    public void refreshItems() {
        centerContainer.removeAll();
        updateMoneyDisplay();

        List<Item> items = slotMachine.getCurrentShopItems();

        for (int i = 0; i < items.size(); i++) {
            ItemPanel itemPanel = new ItemPanel(items.get(i), i);
            centerContainer.add(itemPanel);
        }

        centerContainer.revalidate();
        centerContainer.repaint();
    }

    private void updateMoneyDisplay() {
        int score = (int) slotMachine.GetScore();
        moneyLabel.setText(String.valueOf(score));
    }

    // --- Inner Class for Scalable Item Panel ---
    private class ItemPanel extends JPanel {
        private JLabel nameLabel;
        private JLabel imageLabel;
        private JLabel priceLabel;
        private JLabel priceIconLabel;
        private JButton buyBtn;

        // Base sizes for scaling reference
        private final int BASE_W = 160;
        private final int BASE_H = 240;
        private final int BASE_IMG_SIZE = 80;
        private final float BASE_NAME_FONT = 10f;
        private final float BASE_PRICE_FONT = 14f;
        private final float BASE_BTN_FONT = 12f;

        private BufferedImage originalImage;

        public ItemPanel(Item item, int index) {
            setLayout(new GridBagLayout()); // More control than BorderLayout for centering
            setBackground(new Color(60, 60, 60));
            setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

            String tooltip = "<html><p width=\"150\">" + item.getDescription() + "</p></html>";
            setToolTipText(tooltip);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // 1. Name
            nameLabel = new JLabel("<html><div style='text-align: center;'>" + item.getName() + "</div></html>");
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            gbc.gridy = 0;
            gbc.weighty = 0.1;
            gbc.anchor = GridBagConstraints.NORTH;
            add(nameLabel, gbc);

            // 2. Image
            imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                if (item.getImagePath() != null) {
                    originalImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(item.getImagePath())));
                    // Initial scale
                    imageLabel.setIcon(new ImageIcon(
                            originalImage.getScaledInstance(BASE_IMG_SIZE, BASE_IMG_SIZE, Image.SCALE_SMOOTH)));
                }
            } catch (Exception e) {
                imageLabel.setText("IMG");
            }

            gbc.gridy = 1;
            gbc.weighty = 0.6;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.BOTH;
            add(imageLabel, gbc);

            // 3. Price Panel
            JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            pricePanel.setOpaque(false);

            boolean isFree = !slotMachine.isFirstPurchaseMade();
            String priceText = isFree ? "0" : String.valueOf(item.getPrice());

            priceLabel = new JLabel(priceText);
            priceLabel.setForeground(new Color(255, 215, 0));
            pricePanel.add(priceLabel);

            priceIconLabel = new JLabel();
            if (coinIcon != null) {
                priceIconLabel.setIcon(new ImageIcon(coinIcon.getScaledInstance(15, 15, Image.SCALE_SMOOTH)));
                pricePanel.add(priceIconLabel);
            }

            gbc.gridy = 2;
            gbc.weighty = 0.1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(pricePanel, gbc);

            // 4. Buy Button
            buyBtn = new JButton(isFree ? "GRATUIT" : "ACHETER");

            boolean isSold = slotMachine.isItemSold(index);
            if (isSold) {
                buyBtn.setText("VENDU");
                buyBtn.setEnabled(false);
                buyBtn.setBackground(Color.GRAY);
            }

            buyBtn.addActionListener(e -> {
                if (slotMachine.buyItem(index)) {
                    // Check again after refresh if needed, but simplest is full refresh
                    ShopPanel.this.refreshItems();
                    if (onRerollCallback != null)
                        onRerollCallback.run();
                } else {
                    JOptionPane.showMessageDialog(ShopPanel.this, "Pas assez de pièces !");
                }
            });

            gbc.gridy = 3;
            gbc.weighty = 0.2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add(buyBtn, gbc);

            // Add resize listener
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateScaling();
                }
            });
        }

        private void updateScaling() {
            int w = getWidth();
            int h = getHeight();

            // Calculate scale ratio relative to base size
            // Use the smaller dimension to constrain scaling so it fits
            double scaleW = (double) w / BASE_W;
            double scaleH = (double) h / BASE_H;
            double scale = Math.min(scaleW, scaleH);

            // Limit scale to realistic values to prevent microscopic or huge items
            scale = Math.max(0.3, Math.min(scale, 2.0));

            // Update Fonts
            Font baseFont = new Font("Arial", Font.PLAIN, 10);
            nameLabel.setFont(baseFont.deriveFont((float) (BASE_NAME_FONT * scale)));

            Font priceFont = new Font("Monospaced", Font.BOLD, 14);
            priceLabel.setFont(priceFont.deriveFont((float) (BASE_PRICE_FONT * scale)));

            Font btnFont = new Font("Arial", Font.BOLD, 12);
            buyBtn.setFont(btnFont.deriveFont((float) (BASE_BTN_FONT * scale)));

            // Update Image
            if (originalImage != null) {
                int imgSize = (int) (BASE_IMG_SIZE * scale);
                if (imgSize > 0) {
                    imageLabel.setIcon(
                            new ImageIcon(originalImage.getScaledInstance(imgSize, imgSize, Image.SCALE_SMOOTH)));
                }
            }

            // Update Coin Icon
            if (coinIcon != null) {
                int coinSize = (int) (15 * scale);
                if (coinSize > 0)
                    priceIconLabel
                            .setIcon(new ImageIcon(coinIcon.getScaledInstance(coinSize, coinSize, Image.SCALE_SMOOTH)));
            }

            revalidate();
            // Don't call repaint loop here usually, but if needed
        }
    }
}
