import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SymbolManager {
    private Map<String, BufferedImage> symbolImages;

    public SymbolManager() {
        symbolImages = new HashMap<>();
        loadSymbolImages();
    }

    // Charge les images des symboles
    private void loadSymbolImages() {
        try {
            symbolImages.put("Citron", ImageIO.read(getClass().getResource("medias/symbols/SymbolLemon.png")));
            symbolImages.put("Cerise", ImageIO.read(getClass().getResource("medias/symbols/SymbolCherry.png")));
            symbolImages.put("Trefle", ImageIO.read(getClass().getResource("medias/symbols/SymbolClover.png")));
            symbolImages.put("Cloche", ImageIO.read(getClass().getResource("medias/symbols/SymbolBell.png")));
            symbolImages.put("Diamant", ImageIO.read(getClass().getResource("medias/symbols/SymbolDiamond.png")));
            symbolImages.put("Coffre", ImageIO.read(getClass().getResource("medias/symbols/SymbolChest.png")));
            symbolImages.put("Sept", ImageIO.read(getClass().getResource("medias/symbols/SymbolSeven.png")));
            // Ajoute les autres symboles
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retourne l'image d'un symbole donné
    public BufferedImage getSymbolImage(String type) {
        return symbolImages.getOrDefault(type, null);
    }
}
