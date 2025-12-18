import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SymbolManager {
    private Map<String, BufferedImage> symbolImages;
    private String basePath;

    public SymbolManager(String basePath) {
        this.basePath = basePath;
        symbolImages = new HashMap<>();
        loadSymbolImages();
    }

    // Charge les images des symboles
    private void loadSymbolImages() {
        try {
            // Ensure basePath ends with / if not empty and not just /
            String pathPrefix = basePath;
            if (!pathPrefix.endsWith("/")) {
                pathPrefix += "/";
            }
            // Remove leading slash if present for getResource
            if (pathPrefix.startsWith("/")) {
                pathPrefix = pathPrefix.substring(1);
            }

            // Note: We use pathPrefix which is relative to classpath root (src)
            // The originals were "medias/symbols/..."
            // The new ones will be passed in like "/medias/symbols_slot" or
            // "/medias/symbols_panel"

            String[] symbolsFull = { "SymbolLemon.png", "SymbolCherry.png", "SymbolClover.png", "SymbolBell.png",
                    "SymbolDiamond.png", "SymbolChest.png", "SymbolSeven.png", "Coin.png" };
            String[] commonNames = { "Citron", "Cerise", "Trefle", "Cloche", "Diamant", "Coffre", "Sept", "Coin" };

            for (int i = 0; i < symbolsFull.length; i++) {
                String resourcePath = "/" + pathPrefix + symbolsFull[i];
                var resource = getClass().getResource(resourcePath);
                if (resource != null) {
                    symbolImages.put(commonNames[i], ImageIO.read(resource));
                } else {
                    System.err.println("Image introuvable : " + resourcePath);
                }
            }
            // Ajoute les autres symboles
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.err.println("Erreur chargement images depuis: " + basePath);
        }
    }

    // Retourne l'image d'un symbole donné
    public BufferedImage getSymbolImage(String type) {
        return symbolImages.getOrDefault(type, null);
    }
}
