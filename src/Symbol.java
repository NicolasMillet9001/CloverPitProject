import java.util.Random;


public class Symbol {
    public static double CitronChance = 19.4;
    public static double CeriseChance = 19.4;
    public static double TrefleChance = 14.9;
    public static double ClocheChance = 14.9;
    public static double DiamantChance = 11.9;
    public static double CoffreChance = 11.9;
    public static double SeptChance = 7.5;

    public static double CitronValue = 2;
    public static double CeriseValue = 2;
    public static double TrefleValue = 3;
    public static double ClocheValue = 3;
    public static double DiamantValue = 5;
    public static double CoffreValue = 5;
    public static double SeptValue = 7;

    public static double SymbolGlobalValueMultiplier = 1;


    public enum EnumSymbolType {
        Citron,
        Cerise,
        Trefle,
        Cloche,
        Diamant,
        Coffre,
        Sept
    }

    private EnumSymbolType nom;

    public Symbol() {
        this.nom = getRandomSymbol();
    }

    private EnumSymbolType getRandomSymbol() {
        Random random = new Random();
        double randomValue = random.nextDouble() * 100;

        double cumulativeChance = 0;
        cumulativeChance += CitronChance;
        if (randomValue < cumulativeChance) return EnumSymbolType.Citron;

        cumulativeChance += CeriseChance;
        if (randomValue < cumulativeChance) return EnumSymbolType.Cerise;

        cumulativeChance += TrefleChance;
        if (randomValue < cumulativeChance) return EnumSymbolType.Trefle;

        cumulativeChance += ClocheChance;
        if (randomValue < cumulativeChance) return EnumSymbolType.Cloche;

        cumulativeChance += DiamantChance;
        if (randomValue < cumulativeChance) return EnumSymbolType.Diamant;

        cumulativeChance += CoffreChance;
        if (randomValue < cumulativeChance) return EnumSymbolType.Coffre;

        return EnumSymbolType.Sept; // Retourne 7 si aucune condition n'est remplie
    }

    public EnumSymbolType GetSymbolType() {
        return this.nom;
    }

    public void Affiche(){
        System.out.printf(GetSymbolType()+" ");
    }

    public static double GetChance(EnumSymbolType symbol) {
        switch (symbol) {
            case Citron: return CitronChance;
            case Cerise: return CeriseChance;
            case Trefle: return TrefleChance;
            case Cloche: return ClocheChance;
            case Diamant: return DiamantChance;
            case Coffre: return CoffreChance;
            case Sept: return SeptChance;
            default: return 0;
        }
    }

    public static double GetValue(EnumSymbolType symbol) {
        switch (symbol) {
            case Citron: return CitronValue;
            case Cerise: return CeriseValue;
            case Trefle: return TrefleValue;
            case Cloche: return ClocheValue;
            case Diamant: return DiamantValue;
            case Coffre: return CoffreValue;
            case Sept: return SeptValue;
            default: return 0;
        }
    }

    public static void SetChance(EnumSymbolType symbol, double chance) {
        switch (symbol) {
            case Citron: CitronChance = chance; break;
            case Cerise: CeriseChance = chance; break;
            case Trefle: TrefleChance = chance; break;
            case Cloche: ClocheChance = chance; break;
            case Diamant: DiamantChance = chance; break;
            case Coffre: CoffreChance = chance; break;
            case Sept: SeptChance = chance; break;
        }
    }

    public static void SetValue(EnumSymbolType symbol, double value) {
        switch (symbol) {
            case Citron: CitronValue = value; break;
            case Cerise: CeriseValue = value; break;
            case Trefle: TrefleValue = value; break;
            case Cloche: ClocheValue = value; break;
            case Diamant: DiamantValue = value; break;
            case Coffre: CoffreValue = value; break;
            case Sept: SeptValue = value; break;
        }
    }

    public static double GetSymbolGlobalValueMultiplier() {
        return SymbolGlobalValueMultiplier;
    }
    public static void SetSymbolGlobalValueMultiplier(double multiplier) {
        SymbolGlobalValueMultiplier = multiplier;
    }
    public static void AddSymbolGlobalValueMultiplier(double multiplier) {
        SymbolGlobalValueMultiplier += multiplier;
    }
}

