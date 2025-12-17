public class Pattern {
    public static double horizontal3mult = 1;
    public static double vertical3mult = 1;
    public static double diagonalmult = 1;
    public static double horizontal4mult = 2;
    public static double horizontal5mult = 3;
    public static double zigzagmult = 4;
    public static double trianglemult = 4;
    public static double jackpotmult = 10;


    public enum PatternType{
        horizontal3,
        horizontal4,
        horizontal5,
        vertical3,
        diagonal,
        zigzag,
        triangle,
        jackpot
    }

    public static double GetMultiplier(PatternType patternType){
        switch (patternType){
            case horizontal3:
                return horizontal3mult;
            case horizontal4:
                return horizontal4mult;
            case horizontal5:
                return horizontal5mult;
            case vertical3:
                return vertical3mult;
            case diagonal:
                return diagonalmult;
            case zigzag:
                return zigzagmult;
            case triangle:
                return trianglemult;
            case jackpot:
                return jackpotmult;
            default:
                return -1;
        }
    }

    public static void SetMultiplier(PatternType patternType, double multiplier){
        switch (patternType){
            case horizontal3:
                horizontal3mult = multiplier;
                break;
            case horizontal4:
                horizontal4mult = multiplier;
                break;
            case horizontal5:
                horizontal5mult = multiplier;
                break;
            case vertical3:
                vertical3mult = multiplier;
                break;
            case diagonal:
                diagonalmult = multiplier;
                break;
            case zigzag:
                zigzagmult = multiplier;
                break;
            case triangle:
                trianglemult = multiplier;
                break;
            case jackpot:
                jackpotmult = multiplier;
                break;
            default:
                return;
        }
    }
}
