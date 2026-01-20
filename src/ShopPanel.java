import javax.swing.*;
import java.awt.*;
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

        // Center Container uses WrapLayout
        centerContainer = new JPanel(new WrapLayout(FlowLayout.CENTER, 30, 30));
        centerContainer.setOpaque(false);
        // Important: preferred size helps constraint panel know target size
        centerContainer.setPreferredSize(new Dimension(600, 600));

        // Constraint Panel to enforce max width for 3 items on wide screens
        JPanel constraintPanel = new JPanel(new GridBagLayout());
        constraintPanel.setOpaque(false);
        constraintPanel.add(centerContainer);

        JScrollPane scrollPane = new JScrollPane(constraintPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Hide scrollbar visual (size 0) but keep functionality
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // Bottom - Buttons
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
            JPanel itemPanel = createItemPanel(items.get(i), i);
            centerContainer.add(itemPanel);
        }

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
        panel.setPreferredSize(new Dimension(160, 240));

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

        JButton buyBtn = new JButton("BUY");
        buyBtn.setFont(new Font("Arial", Font.BOLD, 12));
        buyBtn.addActionListener(e -> {
            if (slotMachine.buyItem(index)) {
                refreshItems();
                if (onRerollCallback != null)
                    onRerollCallback.run();
            } else {
                JOptionPane.showMessageDialog(ShopPanel.this, "Not enough coins!");
            }
        });
        bottom.add(buyBtn);

        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * FlowLayout subclass that fully supports wrapping of components.
     */
    public class WrapLayout extends FlowLayout {
        private Dimension preferredLayoutSize;

        public WrapLayout() {
            super();
        }

        public WrapLayout(int align) {
            super(align);
        }

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;

                if (targetWidth == 0)
                    targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);

                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }

                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }

                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                addRow(dim, rowWidth, rowHeight);

                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;

                Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
                if (scrollPane != null && target.isValid()) {
                    dim.width -= (hgap + 1);
                }

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);
            if (dim.height > 0) {
                dim.height += getVgap();
            }
            dim.height += rowHeight;
        }
    }
}
