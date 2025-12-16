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
            // Vérification de lignes
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Symbols[i][j].GetNom() == Symbols[i][j+1].GetNom() &&  Symbols[i][j].GetNom() == Symbols[i][j+2].GetNom()) {
                        if (j==0 || Symbols[i][j].GetNom() != Symbols[i][j-1].GetNom()){ // Vérifie que la ligne n'a pas déjà été comptée
                            if (j<2 && Symbols[i][j].GetNom() == Symbols[i][j+3].GetNom()) {
                                if (j==0 && Symbols[i][j].GetNom() == Symbols[i][j+4].GetNom()) {
                                    System.out.println("Une ligne de 5 !");
                                }else{
                                    System.out.println("Une ligne de 4 !");
                                }
                            }else{
                                System.out.println("Une ligne de 3 !");
                            }

                        }
                    }
                }
            }
            // Vérification de colonnes
            for (int j = 0; j < 5; j++) {
                    if (Symbols[0][j].GetNom() == Symbols[1][j].GetNom() && Symbols[0][j].GetNom() == Symbols[2][j].GetNom()) {
                        System.out.println("Une colonne de 3 !");
                    }
            }
            //Vérification des diagonales et zig zag
            for (int j = 0; j < 3; j++) {
                if (Symbols[0][j].GetNom() == Symbols[1][j+1].GetNom() && Symbols[0][j].GetNom() == Symbols[2][j+2].GetNom()) {
                    if (j==0 && Symbols[0][j].GetNom() == Symbols[1][3].GetNom() && Symbols[0][j].GetNom() == Symbols[0][4].GetNom()) {
                        if (Symbols[0][0].GetNom() == Symbols[0][1].GetNom() &&  Symbols[0][0].GetNom() == Symbols[0][2].GetNom() && Symbols[0][0].GetNom() == Symbols[0][3].GetNom() &&  Symbols[0][0].GetNom() == Symbols[0][4].GetNom()) {
                            System.out.println("Triangle Inverse !");
                        }
                        else{
                            System.out.println("Un V !");
                        }
                    }else{
                        System.out.println("Une diagonale droite !");
                    }
                }
                if (Symbols[0][4-j].GetNom() == Symbols[1][3-j].GetNom() && Symbols[0][4-j].GetNom() == Symbols[2][2-j].GetNom()) {
                    if (j==2 && Symbols[0][2].GetNom() == Symbols[1][3].GetNom() && Symbols[0][2].GetNom() == Symbols[2][4].GetNom()) {
                        if (Symbols[2][0].GetNom() == Symbols[2][1].GetNom() &&  Symbols[2][0].GetNom() == Symbols[2][2].GetNom() &&  Symbols[2][0].GetNom() == Symbols[2][3].GetNom() && Symbols[2][0].GetNom() == Symbols[2][4].GetNom()) {
                            System.out.println("Triangle !");
                        }
                        else{
                            System.out.println("Un ^ !");
                        }
                    }else{
                        System.out.println("Une diagonale gauche !");
                    }
                }

            }
            boolean jackpot = true;
            for (int i = 0; i < 3 && jackpot; i++) {
                for (int j = 0; j < 5; j++) {
                    if (Symbols[0][0].GetNom() != Symbols[i][j].GetNom()){
                        jackpot = false;
                        break;
                    }
                }
            }
            if (jackpot) {
                System.out.println("Jackpot !");
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

