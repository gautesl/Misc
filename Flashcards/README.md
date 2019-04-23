# Custom quiz app
This program inputs a folder of named images, and asks the user to name each one in a flashcard-like way.

Usage: `java Main [image-folder]`

### Dependencies and restrictions
- The program uses [javafx](https://docs.oracle.com/javase/8/javafx/api/toc.htm) for the GUI.
- The current version is in Norwegian. Compile with -encoding utf-8 (`javac -encoding utf-8 *.java`).
- Images are shown using the [Image](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/image/Image.html) class, see the documentation for allowed extensions.
- The folder containing the images must contain subfolders representing the respective categories. These category-folders may optionally contain other folders representing subcategories. See the example below:

    Organisms/
        LandAnimals/
            Birds/
                eagle.jpg
                crow.jpg
                duck.jpg
            Mammals/
                elk.jpg
                elephant.jpg
                hamster.jpg
        Plants/
            Fruits/
                apple.jpg
                pear.jpg
        Fish/
            tuna.jpg
            swordfish.jpg
            pike.jpg
