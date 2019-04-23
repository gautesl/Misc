import javafx.scene.image.Image;
import java.util.ArrayList;

class Species {
    private Image image;
    private ArrayList<String> names;
    private String category, subcategory;

    public Species(Image image, ArrayList<String> names, String category) {
        this(image, names, category, null);
    }

    public Species(Image image, ArrayList<String> names, String category, String subcategory) {
        this.image = image;
        this.names = names;
        this.category = category;
        this.subcategory = subcategory;
    }

    public boolean hasName(String name) {
        for (String n : names) {
            if (name.equalsIgnoreCase(n)) return true;
            if (n.endsWith("er") && name.equalsIgnoreCase(n.substring(0, n.length() - 1))) return true;
        }
        return false;
    }

    public Image getImage() {
        return image;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    @Override
    public String toString() {
        String s = names.get(0);
        for (int i = 1; i < names.size(); i++) {
            s += " - " + names.get(i);
        }
        return s;
    }
}
