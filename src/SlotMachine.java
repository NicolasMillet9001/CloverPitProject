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

