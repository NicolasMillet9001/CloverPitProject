import java.util.Random;

public class SlotMachine {

    private Symbol[][] Symbols = new Symbol[3][5];
    // Ce tableau va stocker 'true' pour les cases qui font partie d'une combinaison gagnante
    private boolean[][] winningCells = new boolean[3][5];

    public SlotMachine() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                Symbols[i][j] = new Symbol();
            }
        }
    }

    public void spin() {
        // 1. Réinitialisation
        // On génère les nouveaux symboles et on remet les gagnants à zéro
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                Symbols[i][j] = new Symbol();
                winningCells[i][j] = false; // Reset des gagnants
            }
        }

        try {
            // --- Vérification des lignes ---
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (Symbols[i][j].GetNom() == Symbols[i][j+1].GetNom() && Symbols[i][j].GetNom() == Symbols[i][j+2].GetNom()) {
                        // Vérifie que la ligne n'a pas déjà été comptée (pour éviter de compter une ligne de 4 comme une ligne de 3 + une autre)
                        if (j == 0 || Symbols[i][j].GetNom() != Symbols[i][j-1].GetNom()) {

                            // Ligne de 5 ?
                            if (j == 0 && Symbols[i][j].GetNom() == Symbols[i][j+3].GetNom() && Symbols[i][j].GetNom() == Symbols[i][j+4].GetNom()) {
                                System.out.println("Une ligne de 5 !");
                                markWinRow(i, j, 5);
                            }
                            // Ligne de 4 ?
                            else if (j < 2 && Symbols[i][j].GetNom() == Symbols[i][j+3].GetNom()) {
                                System.out.println("Une ligne de 4 !");
                                markWinRow(i, j, 4);
                            }
                            // Ligne de 3 standard
                            else {
                                System.out.println("Une ligne de 3 !");
                                markWinRow(i, j, 3);
                            }
                        }
                    }
                }
            }

            // --- Vérification des colonnes ---
            for (int j = 0; j < 5; j++) {
                if (Symbols[0][j].GetNom() == Symbols[1][j].GetNom() && Symbols[0][j].GetNom() == Symbols[2][j].GetNom()) {
                    System.out.println("Une colonne de 3 !");
                    winningCells[0][j] = true;
                    winningCells[1][j] = true;
                    winningCells[2][j] = true;
                }
            }

            // --- Vérification des diagonales et formes spéciales ---
            for (int j = 0; j < 3; j++) {
                // Diagonale Droite (Haut-Gauche vers Bas-Droite)
                if (Symbols[0][j].GetNom() == Symbols[1][j+1].GetNom() && Symbols[0][j].GetNom() == Symbols[2][j+2].GetNom()) {

                    // Vérification "Triangle Inversé" ou "V"
                    if (j == 0 && Symbols[0][j].GetNom() == Symbols[1][3].GetNom() && Symbols[0][j].GetNom() == Symbols[0][4].GetNom()) {
                        if (Symbols[0][0].GetNom() == Symbols[0][1].GetNom() && Symbols[0][0].GetNom() == Symbols[0][2].GetNom() && Symbols[0][0].GetNom() == Symbols[0][3].GetNom() && Symbols[0][0].GetNom() == Symbols[0][4].GetNom()) {
                            System.out.println("Triangle Inverse !");
                            // Tout le triangle
                            markWinDiagonalRight(j);
                            winningCells[1][3] = true;
                            winningCells[0][4] = true;
                            // + la ligne du haut
                            markWinRow(0, 0, 5);
                        } else {
                            System.out.println("Un V !");
                            markWinDiagonalRight(j); // 0,0 - 1,1 - 2,2
                            winningCells[1][3] = true;
                            winningCells[0][4] = true;
                        }
                    } else {
                        System.out.println("Une diagonale droite !");
                        markWinDiagonalRight(j);
                    }
                }

                // Diagonale Gauche (Bas-Gauche vers Haut-Droite ou inversement selon l'implémentation)
                // Note: Ton code original utilise `4-j`, donc on part de la droite vers la gauche
                if (Symbols[0][4-j].GetNom() == Symbols[1][3-j].GetNom() && Symbols[0][4-j].GetNom() == Symbols[2][2-j].GetNom()) {

                    // Vérification "Triangle" ou "^"
                    if (j == 2 && Symbols[0][2].GetNom() == Symbols[1][3].GetNom() && Symbols[0][2].GetNom() == Symbols[2][4].GetNom()) {
                        // Note: la logique originale ici semble complexe, je simplifie pour marquer les cases impliquées
                        if (Symbols[2][0].GetNom() == Symbols[2][1].GetNom() && Symbols[2][0].GetNom() == Symbols[2][2].GetNom() && Symbols[2][0].GetNom() == Symbols[2][3].GetNom() && Symbols[2][0].GetNom() == Symbols[2][4].GetNom()) {
                            System.out.println("Triangle !");
                            markWinDiagonalLeft(j);
                            winningCells[1][3] = true;
                            winningCells[2][4] = true; // Ajusté selon logique probable
                            markWinRow(2, 0, 5); // Base du triangle
                        } else {
                            System.out.println("Un ^ !");
                            markWinDiagonalLeft(j);
                            // On marque les extensions du chapeau
                            winningCells[1][3] = true;
                            winningCells[0][2] = true; // Ajuster selon ta logique de forme exacte
                        }
                    } else {
                        System.out.println("Une diagonale gauche !");
                        markWinDiagonalLeft(j);
                    }
                }
            }

            // --- Jackpot (Tout l'écran pareil) ---
            boolean jackpot = true;
            for (int i = 0; i < 3 && jackpot; i++) {
                for (int j = 0; j < 5; j++) {
                    if (Symbols[0][0].GetNom() != Symbols[i][j].GetNom()) {
                        jackpot = false;
                        break;
                    }
                }
            }
            if (jackpot) {
                System.out.println("Jackpot !");
                for(int i=0; i<3; i++) for(int k=0; k<5; k++) winningCells[i][k] = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Méthodes utilitaires pour marquer les gagnants ---

    private void markWinRow(int row, int startCol, int length) {
        for (int k = 0; k < length; k++) {
            if (startCol + k < 5) winningCells[row][startCol + k] = true;
        }
    }

    private void markWinDiagonalRight(int startCol) {
        // Marque (0, start), (1, start+1), (2, start+2)
        winningCells[0][startCol] = true;
        winningCells[1][startCol + 1] = true;
        winningCells[2][startCol + 2] = true;
    }

    private void markWinDiagonalLeft(int startJ) {
        // Correspond à ta logique: Symbols[0][4-j], Symbols[1][3-j], Symbols[2][2-j]
        winningCells[0][4 - startJ] = true;
        winningCells[1][3 - startJ] = true;
        winningCells[2][2 - startJ] = true;
    }

    public void Affiche() {
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

    // Nouvelle méthode pour que l'interface graphique sache qui a gagné
    public boolean[][] getWinningCells() {
        return winningCells;
    }
}