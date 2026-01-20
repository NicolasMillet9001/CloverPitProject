
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ItemLoader {

    public static List<Item> loadItems() {
        List<Item> items = new ArrayList<>();
        try {
            InputStream is = ItemLoader.class.getResourceAsStream("/Items/items.json");
            if (is == null) {
                System.err.println("Could not find /Items/items.json");
                return items;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
            String json = jsonBuilder.toString();

            // Simple manual parsing for list of objects
            // Remove outer [ and ]
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1);
            }

            // Split by "}," to get objects (imperfect but likely sufficient for this
            // formatted JSON)
            // Better: state machine or finding boundaries

            // Quick and dirty parser for this specific file structure
            // We can iterate through the string and find objects

            List<String> objectStrings = new ArrayList<>();
            int depth = 0;
            StringBuilder currentObj = new StringBuilder();
            for (char c : json.toCharArray()) {
                if (c == '{') {
                    if (depth == 0)
                        currentObj = new StringBuilder();
                    depth++;
                }
                if (depth > 0) {
                    currentObj.append(c);
                }
                if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        objectStrings.add(currentObj.toString());
                    }
                }
            }

            for (String objStr : objectStrings) {
                Item item = parseItem(objStr);
                if (item != null) {
                    items.add(item);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    private static Item parseItem(String json) {
        try {
            String name = extractString(json, "nom");
            String desc = extractString(json, "description");
            int price = extractInt(json, "basePrice");
            String type = extractString(json, "itemType");
            boolean unique = extractBool(json, "unique");

            Item item = new Item(name, desc, price, type, unique);

            // Lists
            List<String> targets = extractStringList(json, "targetStats");
            for (String t : targets)
                item.addTargetStat(t);

            List<Double> mods = extractDoubleList(json, "StatsModifiers");
            for (Double d : mods)
                item.addStatModifier(d);

            // Assign Image Path logic
            assignImage(item);

            return item;
        } catch (Exception e) {
            System.err.println("Error parsing item: " + json);
            e.printStackTrace();
            return null;
        }
    }

    private static void assignImage(Item item) {
        // user rule: Golden -> Value, Picture -> Multiplier
        // item types: SymbolValueModifier, SymbolChanceModifier (maybe?),
        // PatternMultiplierModifier

        String path = ""; // Default

        // Heuristic based on target stats or type
        String type = item.getType();
        List<String> targets = item.getTargetStats();
        String mainTarget = targets.isEmpty() ? "" : targets.get(0);

        if (type.contains("Value")) {
            // Golden
            if (mainTarget.contains("Cerise"))
                path = "/medias/symbols_shop/SymbolGoldenCherry.png";
            else if (mainTarget.contains("Cloche"))
                path = "/medias/symbols_shop/SymbolGoldenBell.png";
            else if (mainTarget.contains("Trefle"))
                path = "/medias/symbols_shop/SymbolGoldenClover.png";
            else if (mainTarget.contains("Coffre"))
                path = "/medias/symbols_shop/SymbolGoldenChest.png";
            else if (mainTarget.contains("Diamant"))
                path = "/medias/symbols_shop/SymbolGoldenDiamond.png";
            else if (mainTarget.contains("Sept"))
                path = "/medias/symbols_shop/SymbolGoldenSeven.png";
            else if (mainTarget.contains("Global"))
                path = "/medias/symbols_shop/x2.png";

        } else if (type.contains("Chance")) {
            if (mainTarget.contains("Cerise"))
                path = "/medias/symbols_shop/SymbolPictureCherry.png";
            else if (mainTarget.contains("Cloche"))
                path = "/medias/symbols_shop/SymbolPictureBell.png";
            else if (mainTarget.contains("Trefle"))
                path = "/medias/symbols_shop/SymbolPictureClover.png";
            else if (mainTarget.contains("Coffre"))
                path = "/medias/symbols_shop/SymbolPictureChest.png";
            else if (mainTarget.contains("Diamant"))
                path = "/medias/symbols_shop/SymbolPictureDiamond.png";
            else if (mainTarget.contains("Sept"))
                path = "/medias/symbols_shop/SymbolPictureSeven.png";
        }
        else if (type.contains("Pattern")) {
            if (mainTarget.contains("Horizontal3Mult"))
                path = "/medias/symbols_shop/PatternHorizontal3.png";
            else if (mainTarget.contains("Vertical3Mult"))
                path = "/medias/symbols_shop/PatternVertical3.png";
            else if (mainTarget.contains("Horizontal4Mult"))
                path = "/medias/symbols_shop/PatternHorizontal4.png";
            else if (mainTarget.contains("Horizontal5Mult"))
                path = "/medias/symbols_shop/PatternHorizontal5.png";
            else if (mainTarget.contains("DiagonalMult"))
                path = "/medias/symbols_shop/PatternDiagonal.png";
            else if (mainTarget.contains("ZigzagMult"))
                path = "/medias/symbols_shop/PatternZigzag.png";
            else if (mainTarget.contains("TriangleMult"))
                path = "/medias/symbols_shop/PatternTriangle.png";
            else if (mainTarget.contains("JackpotMult"))
                path = "/medias/symbols_shop/PatternJackpot.png";
            else
                path = "/medias/symbols_shop/PatternEye.png";
        }

        item.setImagePath(path);
    }

    // Helper parsers (very basic regex-ish)
    private static String extractString(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1)
            return "";
        start += search.length();
        int firstQuote = json.indexOf("\"", start);
        int secondQuote = json.indexOf("\"", firstQuote + 1);
        return json.substring(firstQuote + 1, secondQuote);
    }

    private static int extractInt(String json, String key) {
        try {
            String search = "\"" + key + "\":";
            int start = json.indexOf(search);
            if (start == -1)
                return 0;
            start += search.length();
            // find numbers
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) { // crude
                end++; // wait, spaces?
            }
            // Better: read until comma or }
            int comma = json.indexOf(",", start);
            int brace = json.indexOf("}", start);
            end = comma == -1 ? brace : (brace == -1 ? comma : Math.min(comma, brace));

            String val = json.substring(start, end).trim();
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private static double extractDouble(String json, String key) {
        try {
            String search = "\"" + key + "\":";
            int start = json.indexOf(search);
            if (start == -1)
                return 0;
            start += search.length();
            int comma = json.indexOf(",", start);
            int brace = json.indexOf("}", start);
            int end = comma == -1 ? brace : (brace == -1 ? comma : Math.min(comma, brace));
            String val = json.substring(start, end).trim();
            return Double.parseDouble(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean extractBool(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1)
            return false;
        start += search.length();
        // check for "true" or "false" (might be quoted or not in valid JSON, but here
        // "false" in file has quotes)
        // File has "unique": "false"
        int quote = json.indexOf("\"", start);
        if (quote != -1 && quote - start < 5) {
            int secondQuote = json.indexOf("\"", quote + 1);
            String val = json.substring(quote + 1, secondQuote);
            return Boolean.parseBoolean(val);
        }
        return false;
    }

    private static List<String> extractStringList(String json, String key) {
        List<String> list = new ArrayList<>();
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1)
            return list;
        int arrStart = json.indexOf("[", start);
        int arrEnd = json.indexOf("]", arrStart);
        if (arrStart == -1 || arrEnd == -1)
            return list;

        String content = json.substring(arrStart + 1, arrEnd);
        String[] parts = content.split(",");
        for (String p : parts) {
            String clean = p.trim().replace("\"", "");
            if (!clean.isEmpty())
                list.add(clean);
        }
        return list;
    }

    private static List<Double> extractDoubleList(String json, String key) {
        List<Double> list = new ArrayList<>();
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1)
            return list;
        int arrStart = json.indexOf("[", start);
        int arrEnd = json.indexOf("]", arrStart);

        String content = json.substring(arrStart + 1, arrEnd);
        String[] parts = content.split(",");
        for (String p : parts) {
            String clean = p.trim();
            if (!clean.isEmpty()) {
                try {
                    list.add(Double.parseDouble(clean));
                } catch (Exception e) {
                }
            }
        }
        return list;
    }
}
