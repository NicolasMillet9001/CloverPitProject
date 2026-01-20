import java.io.PrintStream;
import java.io.OutputStream;

public class GameBalanceAnalyzer {

    public static void main(String[] args) {
        int roundsToSimulate = 1000;
        double totalScore = 0;
        int spinsPerRound = 10;

        // Redirect stdout to suppress game logs during simulation
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // DO NOTHING
            }
        }));

        try {
            for (int i = 0; i < roundsToSimulate; i++) {
                SlotMachine sm = new SlotMachine();
                // Constructor calls spin() once, so we need 9 more calls to reach 10 total
                for (int spin = 0; spin < spinsPerRound - 1; spin++) {
                    sm.spin();
                }
                totalScore += sm.GetScore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Restore stdout
            System.setOut(originalOut);
        }

        double average = totalScore / roundsToSimulate;

        System.out.println("----- RESULTATS DE LA SIMULATION -----");
        System.out.println("Nombre de rounds simulés : " + roundsToSimulate);
        System.out.println("Score Total cumulé : " + totalScore);
        System.out.println("Score MOYEN par round (10 lancers) : " + String.format("%.2f", average));
        System.out.println("--------------------------------------");
    }
}
