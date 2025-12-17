public class SlotMachine {

    private Symbol[][] Symbols =  new Symbol[3][5];
    private double score = 0;
    
    public double GetScore(){
        return score;
    }
    
    public void SetScore(double score){
        this.score = score;
    }
    
    public void AddScore(double score){
        this.score += score;
    }
    
    public void SubstractScore(double score){
        this.score -= score;
    }

    public SlotMachine()
    {
        spin();
    }

    public void spin() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                Symbols[i][j] = new Symbol();
            }
        }
        try{
            // Vérification de lignes
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Symbols[i][j].GetSymbolType() == Symbols[i][j+1].GetSymbolType() &&  Symbols[i][j].GetSymbolType() == Symbols[i][j+2].GetSymbolType()) {
                        if (j==0 || Symbols[i][j].GetSymbolType() != Symbols[i][j-1].GetSymbolType()){ // Vérifie que la ligne n'a pas déjà été comptée
                            if (j<2 && Symbols[i][j].GetSymbolType() == Symbols[i][j+3].GetSymbolType()) {
                                if (j==0 && Symbols[i][j].GetSymbolType() == Symbols[i][j+4].GetSymbolType()) {
                                    System.out.println("Une ligne de 5 !");
                                    this.AddScore(5*Symbol.getValue(Symbols[i][j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.horizontal5));

                                }else{
                                    System.out.println("Une ligne de 4 !");
                                    this.AddScore(4*Symbol.getValue(Symbols[i][j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.horizontal4));

                                }
                            }else{
                                System.out.println("Une ligne de 3 !");
                                this.AddScore(3*Symbol.getValue(Symbols[i][j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.horizontal3));

                            }

                        }
                    }
                }
            }
            // Vérification de colonnes
            for (int j = 0; j < 5; j++) {
                    if (Symbols[0][j].GetSymbolType() == Symbols[1][j].GetSymbolType() && Symbols[0][j].GetSymbolType() == Symbols[2][j].GetSymbolType()) {
                        System.out.println("Une colonne de 3 !");
                        this.AddScore(3*Symbol.getValue(Symbols[0][j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.vertical3));
                    }
            }
            //Vérification des diagonales et zig zag
            for (int j = 0; j < 3; j++) {
                if (Symbols[0][j].GetSymbolType() == Symbols[1][j+1].GetSymbolType() && Symbols[0][j].GetSymbolType() == Symbols[2][j+2].GetSymbolType()) {
                    if (j==0 && Symbols[0][j].GetSymbolType() == Symbols[1][3].GetSymbolType() && Symbols[0][j].GetSymbolType() == Symbols[0][4].GetSymbolType()) {
                        if (Symbols[0][0].GetSymbolType() == Symbols[0][1].GetSymbolType() &&  Symbols[0][0].GetSymbolType() == Symbols[0][2].GetSymbolType() && Symbols[0][0].GetSymbolType() == Symbols[0][3].GetSymbolType() &&  Symbols[0][0].GetSymbolType() == Symbols[0][4].GetSymbolType()) {
                            System.out.println("Triangle Inverse !");
                            this.AddScore(8*Symbol.getValue(Symbols[0][j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.triangle));

                        }
                        else{
                            System.out.println("Un V !");
                            this.AddScore(5*Symbol.getValue(Symbols[0][j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.zigzag));

                        }
                    }else{
                        System.out.println("Une diagonale droite !");
                        this.AddScore(3*Symbol.getValue(Symbols[0][j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.diagonal));

                    }
                }
                if (Symbols[0][4-j].GetSymbolType() == Symbols[1][3-j].GetSymbolType() && Symbols[0][4-j].GetSymbolType() == Symbols[2][2-j].GetSymbolType()) {
                    if (j==2 && Symbols[0][2].GetSymbolType() == Symbols[1][3].GetSymbolType() && Symbols[0][2].GetSymbolType() == Symbols[2][4].GetSymbolType()) {
                        if (Symbols[2][0].GetSymbolType() == Symbols[2][1].GetSymbolType() &&  Symbols[2][0].GetSymbolType() == Symbols[2][2].GetSymbolType() &&  Symbols[2][0].GetSymbolType() == Symbols[2][3].GetSymbolType() && Symbols[2][0].GetSymbolType() == Symbols[2][4].GetSymbolType()) {
                            System.out.println("Triangle !");
                            this.AddScore(8*Symbol.getValue(Symbols[0][j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.triangle));

                        }
                        else{
                            System.out.println("Un ^ !");
                            this.AddScore(5*Symbol.getValue(Symbols[0][4-j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.zigzag));

                        }
                    }else{
                        System.out.println("Une diagonale gauche !");
                        this.AddScore(3*Symbol.getValue(Symbols[0][4-j].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.diagonal));

                    }
                }

            }
            boolean jackpot = true;
            for (int i = 0; i < 3 && jackpot; i++) {
                for (int j = 0; j < 5; j++) {
                    if (Symbols[0][0].GetSymbolType() != Symbols[i][j].GetSymbolType()){
                        jackpot = false;
                        break;
                    }
                }
            }
            if (jackpot) {
                System.out.println("Jackpot !");
                this.AddScore(15*Symbol.getValue(Symbols[0][0].GetSymbolType())*Pattern.GetMultiplier(Pattern.PatternType.jackpot));

            }

            System.out.println("Score : "+GetScore());
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void Affiche(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                Symbols[i][j].Affiche();
            }
            System.out.println();
        }
    }

    public Symbol[][] getSymbols() {
        return Symbols;
    }
}

