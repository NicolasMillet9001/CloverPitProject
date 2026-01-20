import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class InventoryPanel extends JPanel {

    private SlotMachine slotMachine;
    private JPanel centerContainer;
    private Runnable onCloseCallback;

    public InventoryPanel(SlotMachine slotMachine, Runnable onCloseCallback) {
        this.slotMachine = slotMachine;
        this.onCloseCallback = onCloseCallback;

        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 35)); // Slightly different dark bg

        // Header
        JLabel title = new JLabel("INVENTAIRE", SwingConstants.CENTER);
        title.setFont(new Font("Stencil", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Center - WrapLayout or GridBag for items
        // We'll use a simple GridLayout wrapper with scroll
        centerContainer = new JPanel();
        centerContainer.setLayout(new GridLayout(0, 4, 15, 15)); // 4 cols, auto rows
        centerContainer.setBackground(new Color(25, 25, 35));
        centerContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(centerContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(25, 25, 35));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom
        JButton closeBtn = new JButton("RETOUR");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        closeBtn.setBackground(new Color(100, 100, 100));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> {
            if (this.onCloseCallback != null)
                this.onCloseCallback.run();
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);

        refreshInventory();
    }

    public void refreshInventory() {
        centerContainer.removeAll();

        List<Item> inventory = slotMachine.getInventory();

        // Group by Name
        Map<String, Item> uniqueItems = new HashMap<>(); // To keep one instance for details
        Map<String, Integer> counts = new HashMap<>();

        for (Item item : inventory) {
            String name = item.getName();
            uniqueItems.putIfAbsent(name, item);
            counts.put(name, counts.getOrDefault(name, 0) + 1);
        }

        for (String name : uniqueItems.keySet()) {
            Item item = uniqueItems.get(name);
            int count = counts.get(name);
            centerContainer.add(createItemCard(item, count));
        }

        centerContainer.revalidate();
        centerContainer.repaint();
    }

    private JPanel createItemCard(Item item, int count) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(50, 50, 60));
        card.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 2));
        card.setPreferredSize(new Dimension(140, 180));

        // Name
        JLabel nameLbl = new JLabel("<html><div style='text-align: center;'>" + item.getName() + "</div></html>",
                SwingConstants.CENTER);
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        nameLbl.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        card.add(nameLbl, BorderLayout.NORTH);

        // Image
        JLabel imgLbl = new JLabel();
        imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            if (item.getImagePath() != null) {
                BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getResource(item.getImagePath())));
                imgLbl.setIcon(new ImageIcon(img.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
            }
        } catch (Exception e) {
            imgLbl.setText("IMG");
        }
        card.add(imgLbl, BorderLayout.CENTER);

        // Count
        JLabel countLbl = new JLabel("x" + count, SwingConstants.CENTER);
        countLbl.setFont(new Font("Arial", Font.BOLD, 20));
        countLbl.setForeground(new Color(255, 215, 0));
        countLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(countLbl, BorderLayout.SOUTH);

        card.setToolTipText(item.getDescription());

        return card;
    }
}
