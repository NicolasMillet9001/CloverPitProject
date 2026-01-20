import javax.swing.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class SlotMachine {

    private Symbol[][] Symbols = new Symbol[3][5];
    private boolean[][] winningCells = new boolean[3][5];
    private double score = 0;
    private int maxSpinsPerRound = 10;
    private int spinsLeft;
    private int currentRound = 1;
    private double minScoreToPass;

    // SHOP LOGIC
    private List<Item> allItems;
    private List<Item> currentShopItems;
    private java.util.Set<Integer> soldIndices = new java.util.HashSet<>();

    public double GetScore() {
        return score;
    }

    public void SetScore(double score) {
        this.score = score;
    }

    public void AddScore(double score) {
        this.score += score;
    }

    public void AddScore(int nbOfSymbols, Symbol symbol, Pattern.PatternType pattern) {
        this.score += nbOfSymbols * (Symbol.GetValue(symbol.GetSymbolType()) * Symbol.GetSymbolGlobalValueMultiplier())
                * (Pattern.GetMultiplier(pattern) * Pattern.GetGlobalMultiplier());
    }

    public void SubstractScore(double score) {
        this.score -= score;
    }

    public SlotMachine() {
        this.spinsLeft = maxSpinsPerRound;
        this.minScoreToPass = calculateMinScoreForRound(currentRound - 1);

        // Load Items
        allItems = ItemLoader.loadItems();
        currentShopItems = new ArrayList<>();

        // spin(); // Removed to prevent auto-start
    }

    public void startNewRound() {

        this.currentRound++;
        this.spinsLeft = maxSpinsPerRound;
        this.minScoreToPass = calculateMinScoreForRound(currentRound - 1);
        // Reset grid for manual start
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                Symbols[i][j] = null;
                winningCells[i][j] = false;
            }
        }
        // spin(); // Removed to require manual start
    }

    private double calculateMinScoreForRound(int round) {
        return 50 * round;
    }

    public void spin() {
        if (spinsLeft <= 0) {
            // Fin du round, vérifier résultat
            checkRoundResult();
            // openShop(); // FIX: Removed duplicate/incorrect call. checkRoundResult
            // handles it.
            return;
        }
        spinsLeft--;
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
                    if (Symbols[i][j].GetSymbolType() == Symbols[i][j + 1].GetSymbolType()
                            && Symbols[i][j].GetSymbolType() == Symbols[i][j + 2].GetSymbolType()) {
                        if (j == 0 || Symbols[i][j].GetSymbolType() != Symbols[i][j - 1].GetSymbolType()) { // Vérifie
                                                                                                            // que la
                                                                                                            // ligne n'a
                                                                                                            // pas déjà
                                                                                                            // été
                                                                                                            // comptée
                            if (j < 2 && Symbols[i][j].GetSymbolType() == Symbols[i][j + 3].GetSymbolType()) {
                                if (j == 0 && Symbols[i][j].GetSymbolType() == Symbols[i][j + 4].GetSymbolType()) {
                                    System.out.println("Une ligne de 5 !");
                                    this.AddScore(5, Symbols[i][j], Pattern.PatternType.horizontal5);
                                    markWinRow(i, j, 5);

                                } else {
                                    System.out.println("Une ligne de 4 !");
                                    this.AddScore(4, Symbols[i][j], Pattern.PatternType.horizontal4);
                                    markWinRow(i, j, 4);

                                }
                            } else {
                                System.out.println("Une ligne de 3 !");
                                this.AddScore(3, Symbols[i][j], Pattern.PatternType.horizontal3);
                                markWinRow(i, j, 3);
                            }
                        }
                    }
                }
            }

            // --- Vérification des colonnes ---
            for (int j = 0; j < 5; j++) {
                if (Symbols[0][j].GetSymbolType() == Symbols[1][j].GetSymbolType()
                        && Symbols[0][j].GetSymbolType() == Symbols[2][j].GetSymbolType()) {
                    System.out.println("Une colonne de 3 !");
                    this.AddScore(3, Symbols[0][j], Pattern.PatternType.vertical3);
                    winningCells[0][j] = true;
                    winningCells[1][j] = true;
                    winningCells[2][j] = true;
                }
            }

            // --- Vérification des diagonales et formes spéciales ---
            for (int j = 0; j < 3; j++) {
                // Diagonale Droite (Haut-Gauche vers Bas-Droite)
                if (Symbols[0][j].GetSymbolType() == Symbols[1][j + 1].GetSymbolType()
                        && Symbols[0][j].GetSymbolType() == Symbols[2][j + 2].GetSymbolType()) {

                    // Vérification "Triangle Inversé" ou "V"
                    if (j == 0 && Symbols[0][j].GetSymbolType() == Symbols[1][3].GetSymbolType()
                            && Symbols[0][j].GetSymbolType() == Symbols[0][4].GetSymbolType()) {
                        if (Symbols[0][0].GetSymbolType() == Symbols[0][1].GetSymbolType()
                                && Symbols[0][0].GetSymbolType() == Symbols[0][2].GetSymbolType()
                                && Symbols[0][0].GetSymbolType() == Symbols[0][3].GetSymbolType()
                                && Symbols[0][0].GetSymbolType() == Symbols[0][4].GetSymbolType()) {
                            System.out.println("Triangle Inverse !");
                            this.AddScore(8, Symbols[0][j], Pattern.PatternType.triangle);
                            // Tout le triangle
                            markWinDiagonalRight(j);
                            winningCells[1][3] = true;
                            winningCells[0][4] = true;
                            // + la ligne du haut
                            markWinRow(0, 0, 5);
                        } else {
                            System.out.println("Un V !");
                            this.AddScore(5, Symbols[0][0], Pattern.PatternType.zigzag);
                            markWinDiagonalRight(j); // 0,0 - 1,1 - 2,2
                            winningCells[1][3] = true;
                            winningCells[0][4] = true;
                        }
                    } else {
                        if (j != 2 || (Symbols[0][2].GetSymbolType() != Symbols[1][1].GetSymbolType()
                                && Symbols[0][2].GetSymbolType() != Symbols[2][0].GetSymbolType())) {
                            System.out.println("Une diagonale droite !");
                            this.AddScore(3, Symbols[0][2], Pattern.PatternType.diagonal);
                            markWinDiagonalRight(j);
                        }
                    }
                }

                // Diagonale Gauche (Bas-Gauche vers Haut-Droite ou inversement selon
                // l'implémentation)
                // Note: Ton code original utilise `4-j`, donc on part de la droite vers la
                // gauche
                if (Symbols[0][4 - j].GetSymbolType() == Symbols[1][3 - j].GetSymbolType()
                        && Symbols[0][4 - j].GetSymbolType() == Symbols[2][2 - j].GetSymbolType()) {

                    // Vérification "Triangle" ou "^"
                    if (j == 2 && Symbols[0][2].GetSymbolType() == Symbols[1][3].GetSymbolType()
                            && Symbols[0][2].GetSymbolType() == Symbols[2][4].GetSymbolType()) {
                        // Note: la logique originale ici semble complexe, je simplifie pour marquer les
                        // cases impliquées
                        if (Symbols[2][0].GetSymbolType() == Symbols[2][1].GetSymbolType()
                                && Symbols[2][0].GetSymbolType() == Symbols[2][2].GetSymbolType()
                                && Symbols[2][0].GetSymbolType() == Symbols[2][3].GetSymbolType()
                                && Symbols[2][0].GetSymbolType() == Symbols[2][4].GetSymbolType()) {
                            System.out.println("Triangle !");
                            this.AddScore(8, Symbols[0][2], Pattern.PatternType.triangle);
                            markWinDiagonalLeft(j);
                            winningCells[1][3] = true;
                            winningCells[2][4] = true; // Ajusté selon logique probable
                            markWinRow(2, 0, 5); // Base du triangle
                        } else {
                            System.out.println("Un ^ !");
                            this.AddScore(5, Symbols[0][2], Pattern.PatternType.zigzag);
                            markWinDiagonalLeft(j);
                            // On marque les extensions du chapeau
                            winningCells[1][3] = true;
                            winningCells[0][2] = true; // Ajuster selon ta logique de forme exacte
                        }
                    } else {
                        System.out.println("Une diagonale gauche !");
                        this.AddScore(3, Symbols[0][2], Pattern.PatternType.diagonal);
                        markWinDiagonalLeft(j);
                    }
                }
            }

            // --- Jackpot (Tout l'écran pareil) ---
            boolean jackpot = true;
            for (int i = 0; i < 3 && jackpot; i++) {
                for (int j = 0; j < 5; j++) {
                    if (Symbols[0][0].GetSymbolType() != Symbols[i][j].GetSymbolType()) {
                        jackpot = false;
                        break;
                    }
                }
            }
            if (jackpot) {
                System.out.println("Jackpot !");
                this.AddScore(15, Symbols[0][0], Pattern.PatternType.jackpot);
                for (int i = 0; i < 3; i++)
                    for (int k = 0; k < 5; k++)
                        winningCells[i][k] = true;
            }

            System.out.println("Score : " + GetScore());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Runnable onShopOpenListener;

    public void setOnShopOpenListener(Runnable listener) {
        this.onShopOpenListener = listener;
    }

    private void openShop() {
        System.out.println("Fin du round " + currentRound + " ! Ouverture du shop...");
        generateShopItems();
        if (onShopOpenListener != null) {
            onShopOpenListener.run();
        }
    }

    private void checkRoundResult() {
        if (score >= minScoreToPass) {
            System.out.println("Round " + currentRound + " réussi ! Ouverture du shop...");
            openShop();
        } else {
            System.out.println("Round " + currentRound + " échoué ! La partie est perdue.");
            resetGame();
        }
    }

    private void resetGame() {
        this.currentRound = 1;
        this.spinsLeft = maxSpinsPerRound;
        this.score = 0;
        this.minScoreToPass = calculateMinScoreForRound(currentRound - 1); // Fix: Recalculate correctly
        JOptionPane.showMessageDialog(null, "Partie perdue ! Recommencez depuis le début.", "Fin de partie",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Méthodes utilitaires pour marquer les gagnants ---

    private void markWinRow(int row, int startCol, int length) {
        for (int k = 0; k < length; k++) {
            if (startCol + k < 5)
                winningCells[row][startCol + k] = true;
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

    // --- SHOP LOGIC ---

    public void generateShopItems() {
        if (allItems.isEmpty())
            return;

        currentShopItems.clear();
        soldIndices.clear();

        // Ensure uniqueness using a temporary Set to track added items (by Name or Ref)
        List<Item> pool = new ArrayList<>(allItems);
        Collections.shuffle(pool);

        List<String> addedNames = new ArrayList<>();

        for (Item item : pool) {
            if (currentShopItems.size() >= 5)
                break;

            // Check if we already added an item with this name (to avoid JSON dupes if any)
            if (!addedNames.contains(item.getName())) {
                currentShopItems.add(item);
                addedNames.add(item.getName());
            }
        }

        // If we ran out of unique items but have less than 5, we just show what we
        // have.
    }

    public List<Item> getCurrentShopItems() {
        return currentShopItems;
    }

    public boolean isItemSold(int index) {
        return soldIndices.contains(index);
    }

    public boolean rerollShop() {
        if (score >= 10) {
            SubstractScore(10);
            generateShopItems();
            return true;
        }
        return false;
    }

    public boolean buyItem(int index) {
        if (index < 0 || index >= currentShopItems.size())
            return false;

        if (soldIndices.contains(index))
            return false;

        Item item = currentShopItems.get(index);
        if (score >= item.getPrice()) {
            SubstractScore(item.getPrice());
            applyItemEffect(item);
            soldIndices.add(index);
            // currentShopItems.remove(index); // Removed to keep item in list
            return true;
        }
        return false;
    }

    private void applyItemEffect(Item item) {
        System.out.println("Applying item effect: " + item.getName());
        // Placeholder for logic link
    }

    public int getSpinsLeft() {
        return spinsLeft;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public double getMinScoreToPass() {
        return minScoreToPass;
    }
}