import java.util.ArrayList;
import java.util.List;

public class Item {
    private String name;
    private String description;
    private int price;
    private String type; // Type de l'objet (ex: "SymbolChanceModifier", "SymbolValueModifier")
    private List<String> targetStats; // Statistiques ciblées (ex: "CeriseLuck", "ClocheValue")
    private List<Double> statsModifiers; // Multiplicateurs à appliquer
    private String imagePath; // Chemin vers l'image de l'objet
    private boolean isUnique; // Si l'objet est unique
    private int quality; // 1 = Common, 2 = Rare, 3 = Epic, 4 = Legendary

    public Item(String name, String description, int price, String type, boolean isUnique, int quality) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.isUnique = isUnique;
        this.quality = quality;
        this.targetStats = new ArrayList<>();
        this.statsModifiers = new ArrayList<>();
    }

    // Constructor override for backward compatibility if needed, defaulting to
    // quality 1
    public Item(String name, String description, int price, String type, boolean isUnique) {
        this(name, description, price, type, isUnique, 1);
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    // Ajoute une statistique cible
    public void addTargetStat(String stat) {
        targetStats.add(stat);
    }

    // Ajoute un modificateur de statistique
    public void addStatModifier(double modifier) {
        statsModifiers.add(modifier);
    }

    // Getters
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
    }

    public boolean isUnique() {
        return isUnique;
    }

    // Setter pour le chemin de l'image
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Méthode pour appliquer l'effet de l'objet
    public void applyEffect() {
        for (int i = 0; i < targetStats.size(); i++) {
            String stat = targetStats.get(i);
            double modifier = statsModifiers.get(i);

            switch (type) {
                case "SymbolChanceModifier":
                    modifySymbolChance(stat, modifier);
                    break;
                case "SymbolValueModifier":
                    modifySymbolValue(stat, modifier);
                    break;
                case "SymbolGlobalValueModifier":
                    Symbol.AddSymbolGlobalValueMultiplier(modifier);
                    break;
                case "PatternMultiplierModifier":
                    modifyPatternMultiplier(stat, modifier);
                    break;
                case "PatternGlobalMultiplierModifier":
                    Pattern.AddGlobalMultiplier(modifier);
                    break;
            }
        }
    }

    // Méthodes utilitaires pour modifier les statistiques
    private void modifySymbolChance(String stat, double modifier) {
        Symbol.EnumSymbolType symbolType = getSymbolTypeFromStat(stat);
        double currentChance = Symbol.GetChance(symbolType);
        Symbol.SetChance(symbolType, currentChance * modifier);
    }

    private void modifySymbolValue(String stat, double modifier) {
        Symbol.EnumSymbolType symbolType = getSymbolTypeFromStat(stat);
        double currentValue = Symbol.GetValue(symbolType);
        Symbol.SetValue(symbolType, currentValue * modifier);
    }

    private void modifyPatternMultiplier(String stat, double modifier) {
        Pattern.PatternType patternType = getPatternTypeFromStat(stat);
        double currentMultiplier = Pattern.GetMultiplier(patternType);
        Pattern.SetMultiplier(patternType, currentMultiplier * modifier);
    }

    // Convertit une chaîne de statistique en type de symbole
    private Symbol.EnumSymbolType getSymbolTypeFromStat(String stat) {
        switch (stat) {
            case "CitronLuck":
            case "CitronValue":
                return Symbol.EnumSymbolType.Citron;
            case "CeriseLuck":
            case "CeriseValue":
                return Symbol.EnumSymbolType.Cerise;
            case "ClocheLuck":
            case "ClocheValue":
                return Symbol.EnumSymbolType.Cloche;
            case "TrefleLuck":
            case "TrefleValue":
                return Symbol.EnumSymbolType.Trefle;
            case "CoffreLuck":
            case "CoffreValue":
                return Symbol.EnumSymbolType.Coffre;
            case "DiamantLuck":
            case "DiamantValue":
                return Symbol.EnumSymbolType.Diamant;
            case "SeptLuck":
            case "SeptValue":
                return Symbol.EnumSymbolType.Sept;
            default:
                return null;
        }
    }

    // Convertit une chaîne de statistique en type de pattern
    private Pattern.PatternType getPatternTypeFromStat(String stat) {
        switch (stat) {
            case "Horizontal3Mult":
                return Pattern.PatternType.horizontal3;
            case "Horizontal4Mult":
                return Pattern.PatternType.horizontal4;
            case "Horizontal5Mult":
                return Pattern.PatternType.horizontal5;
            case "Vertical3Mult":
                return Pattern.PatternType.vertical3;
            case "DiagonalMult":
                return Pattern.PatternType.diagonal;
            case "ZigzagMult":
                return Pattern.PatternType.zigzag;
            case "TriangleMult":
                return Pattern.PatternType.triangle;
            case "JackpotMult":
                return Pattern.PatternType.jackpot;
            default:
                return null;
        }
    }

    // Modifie le prix (pour les promos par ex)
    public void setPrice(int price) {
        this.price = price;
    }

    // Crée une copie de l'objet (pour ne pas modifier l'original dans la liste
    // globale)
    public Item copy() {
        Item newItem = new Item(this.name, this.description, this.price, this.type, this.isUnique, this.quality);
        newItem.setImagePath(this.imagePath);
        // Copy lists
        for (String stat : this.targetStats)
            newItem.addTargetStat(stat);
        for (Double mod : this.statsModifiers)
            newItem.addStatModifier(mod);
        return newItem;
    }

    @Override
    public String toString() {
        return name + " (" + price + " coins) [Q" + quality + "]";
    }
}
