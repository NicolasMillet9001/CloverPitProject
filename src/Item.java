
import java.util.ArrayList;
import java.util.List;

public class Item {
    private String name;
    private String description;
    private int price;
    private String type; // itemType from JSON
    private List<String> targetStats;
    private List<Double> statsModifiers;
    private String imagePath;
    private boolean isUnique;

    public Item(String name, String description, int price, String type, boolean isUnique) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.isUnique = isUnique;
        this.targetStats = new ArrayList<>();
        this.statsModifiers = new ArrayList<>();
    }

    public void addTargetStat(String stat) {
        targetStats.add(stat);
    }

    public void addStatModifier(double modifier) {
        statsModifiers.add(modifier);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public List<String> getTargetStats() {
        return targetStats;
    }

    public List<Double> getStatsModifiers() {
        return statsModifiers;
    }

    public String getImagePath() {
        return imagePath;
    } // Will be set after loading

    public boolean isUnique() {
        return isUnique;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return name + " (" + price + ")";
    }
}
