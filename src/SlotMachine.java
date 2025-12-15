public class SlotMachine {

    private Symbol[][] Symbols =  new Symbol[3][5];

    public SlotMachine()
    {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                Symbols[i][j] = new Symbol();
            }
        }
    }

    public void spin() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                Symbols[i][j] = new Symbol();
            }
        }
        try{
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Symbols[i][j].GetNom() == Symbols[i][j+1].GetNom() &&  Symbols[i][j].GetNom() == Symbols[i][j+2].GetNom()) {
                        System.out.println("Une ligne de 3 !");
                        //TODO: ajouter la détection de ligne de 4 puis 5
                    };
                }
            }
            for (int j = 0; j < 5; j++) {
                    if (Symbols[0][j].GetNom() == Symbols[1][j].GetNom() && Symbols[0][j].GetNom() == Symbols[2][j].GetNom()) {
                        System.out.println("Une colonne de 3 !");
                    }
            }
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

