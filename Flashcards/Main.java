import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class Main extends Application {
    private static String imageFolder = "Bilder";
    private Stage stage;
    private BorderPane root;
    private ImageView view;
    private String currentImage;
    private Text message;
    private TextField input;
    private Button nextImageButton;
    private Random random = new Random();
    private HashMap<String, Species> mastermap = new HashMap<>();
    private HashMap<String, Species> images = new HashMap<>();
    private HashMap<String, Species> usedImages = new HashMap<>();
    private HashMap<String, Set<String>> catHierarchy = new HashMap<>();

    public static void main(String[] args) {
        if (args.length > 0) imageFolder = args[0];
        launch(args);
    }

    public void start(Stage stage) {
        if (new File(imageFolder).listFiles() == null) {
            System.out.println("Invalid folder: " + imageFolder);
            System.out.println("usage: java Main [imagefolder]");
            System.exit(-1);
        }
        loadFiles(imageFolder);

        this.stage = stage;

        view = new ImageView();
        message = new Text();
        message.setFill(Color.BLACK);
        message.setFont(new Font("Verdana", 20));
        message.setTextAlignment(TextAlignment.CENTER);
        view.setPreserveRatio(true);
        view.setFitWidth(1100);
        view.setFitHeight(600);

        input = new TextField();
        input.setPromptText("navn");
        Button checkButton = new Button("Check");
        checkButton.setOnAction(e -> checkAnswer());

        nextImageButton = new Button("Neste");
        Button correctAnswerButton = new Button("Fasit");
        correctAnswerButton.setOnAction(e -> {
            message.setText(mastermap.get(currentImage) + "");
            message.setFill(Color.BLUE);
            nextImageButton.requestFocus();
        });

        nextImageButton.setPrefWidth(50);
        nextImageButton.setPrefHeight(600);
        nextImageButton.setOnAction(e -> {
            setNextImage();
            input.setText("");
        });

        root = new BorderPane();
        root.setCenter(view);
        root.setTop(new HBox(input, checkButton, correctAnswerButton, message));
        root.setRight(nextImageButton);
        root.setLeft(makeFilterChoices());

        Scene scene = new Scene(root);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.SPACE || key.getCode() == KeyCode.RIGHT) {
                setNextImage();
            }
            if (key.getCode() == KeyCode.ENTER) {
                if (input.isFocused() || checkButton.isFocused()) {
                    checkAnswer();
                } else if (nextImageButton.isFocused()) {
                    setNextImage();
                } else if (correctAnswerButton.isFocused()) {
                    correctAnswerButton.fire();
                }
            }
        });

        setNextImage();
        stage.setTitle("Flashcards");
        stage.setScene(scene);
        stage.show();
    }

    private HBox makeFilterChoices() {
        VBox boxes = new VBox();
        for (String category : catHierarchy.keySet()) {
            CheckBox mainCatBox = new CheckBox(category);
            mainCatBox.setSelected(true);

            ArrayList<CheckBox> subCatBoxes = new ArrayList<>();
            for (String subcat : catHierarchy.get(category)) {
                CheckBox cb = new CheckBox(subcat);
                cb.setSelected(true);
                cb.setOnAction(e -> toggleCategory(subcat, cb.isSelected(), true));
                subCatBoxes.add(cb);
            }

            mainCatBox.setOnAction(e -> {
                for (CheckBox cb : subCatBoxes) {
                    cb.setSelected(mainCatBox.isSelected());
                }
                toggleCategory(category, mainCatBox.isSelected(), false);
            });
            boxes.getChildren().add(mainCatBox);
            for (CheckBox cb : subCatBoxes) {
                Pane p = new Pane();
                p.setPadding(new Insets(0, 0, 0, 10));
                boxes.getChildren().add(new HBox(p, cb));
            }
        }

        return new HBox(boxes);
    }

    private void toggleCategory(String category, boolean toggled, boolean isSubCat) {
        Set<String> affected = new HashSet<>();

        for (String name : mastermap.keySet()) {
            String subcat = mastermap.get(name).getSubcategory();

            if (isSubCat && subcat != null && subcat.equals(category) ||
                !isSubCat && mastermap.get(name).getCategory().equals(category)) {
                affected.add(name);
            }
        }

        for (String name : affected) {
            if (toggled) images.put(name, mastermap.get(name));
            else {
                images.remove(name);
                usedImages.remove(name);
            }
        }
    }

    private void checkAnswer() {
        if (input.getText().equals("")) return;
        message.setText(input.getText() + " er feil");
        message.setFill(Color.RED);
        input.requestFocus();
        if (mastermap.get(currentImage).hasName(input.getText())) {
            message.setText(mastermap.get(currentImage) + " er riktig!");
            message.setFill(Color.GREEN);
            nextImageButton.requestFocus();
        }
        input.setText("");
    }

    private void setNextImage() {
        if (images.isEmpty()) {
            images = usedImages;
            usedImages = new HashMap<>();
        }
        String key = new ArrayList<>(images.keySet()).get(random.nextInt(images.keySet().size()));
        usedImages.put(key, images.get(key));
        images.remove(key);
        currentImage = key;
        message.setText("Hva er på bildet? ("+usedImages.get(key).getCategory()+")");
        message.setFill(Color.BLACK);
        view.setImage(usedImages.get(key).getImage());
        input.requestFocus();
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    private void loadFiles(String foldername) {
        for (File folder : new File(foldername).listFiles()) {
            String category = folder.getName();
            for (File file : folder.listFiles()) {
                String path = foldername + "/" + category + "/" + file.getName();
                if (file.isDirectory()) {
                    String subcat = file.getName();
                    for (File subfile : file.listFiles()) {
                        addEntry(subfile, path + "/" + subfile.getName(), category, subcat);
                    }
                } else {
                    addEntry(file, path, category, null);
                }
            }
        }
    }

    private void addEntry(File file, String path, String cat, String subcat) {
        String name = file.getName().split("\\.")[0];
        ArrayList<String> names = new ArrayList<>();
        if (name.split(" \\(").length > 1) {
            String[] synonyms = name.split(" \\(")[1].split("\\)")[0].split(", ");
            name = name.split(" \\(")[0];
            for (String synonym : synonyms) {
                names.add(synonym);
            }
        }
        names.add(0, name);
        Image image = new Image(path);
        Species species = new Species(image, names, cat, subcat);
        images.put(name, species);
        mastermap.put(name, species);

        if (catHierarchy.containsKey(cat)) {
            if (subcat != null) catHierarchy.get(cat).add(subcat);
        } else {
            Set<String> set = new HashSet<>();
            if (subcat != null) set.add(subcat);
            catHierarchy.put(cat, set);
        }
    }
}
