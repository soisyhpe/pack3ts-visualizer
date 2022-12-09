package fr.eroschn.cours.lu3in033.projetfx.application.windows;

import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;

public class HomeWindow {

    private boolean isMacSupported = false;

    public HomeWindow(Stage stage, HostServices hostServices) {


        // on récupère les infos de l'os
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) isMacSupported = true;


        Menu fileMenu = new Menu("Fichier");

        MenuItem openItem = new MenuItem("Ouvrir");

        KeyCombination openCombination = new KeyCodeCombination(
                KeyCode.O,
                isMacSupported ? KeyCombination.META_DOWN : KeyCombination.CONTROL_DOWN);
        openItem.setAccelerator(openCombination);

        openItem.setOnAction((e) -> {
            openFromFile();
        });


        Menu recentsMenu = new Menu("Ouvrir l'élément récent");

        MenuItem item = new MenuItem("(vide)");
        item.setDisable(true);
        recentsMenu.getItems().add(item);

        recentsMenu.getItems().add(new SeparatorMenuItem());

        MenuItem clearHistoric = new MenuItem("Effacer l'historique");
        // clearHistoric.setDisable(true);

        clearHistoric.setOnAction((e) -> {
        });

        recentsMenu.getItems().add(clearHistoric);


        SeparatorMenuItem s1 = new SeparatorMenuItem();


        Menu importMenu = new Menu("Importer...");
        MenuItem clipboardItem = new MenuItem("... depuis le presse-papier");


        KeyCombination clipboardCombination = new KeyCodeCombination(
                KeyCode.L,
                isMacSupported ? KeyCombination.META_DOWN : KeyCombination.CONTROL_DOWN);
        clipboardItem.setAccelerator(clipboardCombination);

        clipboardItem.setOnAction((e) -> {
            openFromClipboard();
        });

        importMenu.getItems().add(clipboardItem);


        MenuItem exportItem = new MenuItem("Exporter");
        exportItem.setDisable(true);


        SeparatorMenuItem s2 = new SeparatorMenuItem();


        MenuItem closeItem = new MenuItem("Fermer");

        KeyCombination closeCombination = new KeyCodeCombination(
                KeyCode.W,
                isMacSupported ? KeyCombination.META_DOWN : KeyCombination.CONTROL_DOWN);
        closeItem.setAccelerator(closeCombination);

        closeItem.setOnAction((e) -> {
            stage.close();
        });


        fileMenu.getItems().addAll(openItem, recentsMenu, s1, importMenu, exportItem, s2, closeItem);

        MenuBar menuBar = new MenuBar(fileMenu);
        menuBar.useSystemMenuBarProperty().set(isMacSupported);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);


        VBox box = new VBox(menuBar);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 200, 0, 200));


        VBox welcomeBox = new VBox();
        welcomeBox.setAlignment(Pos.CENTER);
        welcomeBox.setPadding(new Insets(10, 0, 25, 0));

        try {
            ImageView welcomeLogo = new ImageView();

            ClassLoader classLoader = getClass().getClassLoader();
            String imageUrl = classLoader.getResource("images/logo.png").toExternalForm();
            Image logo = new Image(imageUrl);

            welcomeLogo.setImage(logo);
            welcomeLogo.setFitHeight(100);
            welcomeLogo.setPreserveRatio(true);
            welcomeLogo.setCursor(Cursor.DEFAULT);

            welcomeBox.getChildren().addAll(welcomeLogo);
        } catch (Exception e) {
            Label welcomeTitle = new Label();
            welcomeTitle.setText("Pack3t Visualizer");
            welcomeTitle.setCursor(Cursor.DEFAULT);
            welcomeTitle.setFont(new Font(welcomeTitle.getFont().getName(), 35));

            Label welcomeText = new Label();
            welcomeText.setText("Visualisateur de trafic réseau");
            welcomeText.setCursor(Cursor.DEFAULT);
            welcomeText.setFont(new Font(welcomeText.getFont().getName(), 15));

            welcomeBox.getChildren().addAll(welcomeTitle, welcomeText);
        }


        VBox startingBox = new VBox();
        startingBox.setAlignment(Pos.CENTER);
        startingBox.setPadding(new Insets(10, 0, 25, 0));

        Label explanationTitle = new Label();
        explanationTitle.setText("Démarrage");
        explanationTitle.setCursor(Cursor.DEFAULT);
        explanationTitle.setFont(new Font(explanationTitle.getFont().getName(), 20));

        Separator explanationSeparator = new Separator();

        VBox explanationBox = new VBox();
        explanationBox.setAlignment(Pos.CENTER);
        explanationBox.setPadding(new Insets(50, 100, 0, 100));
        explanationBox.setSpacing(5);

        HBox openFromFileBox = new HBox();
        openFromFileBox.setAlignment(Pos.CENTER);
        openFromFileBox.setSpacing(5);

        Label openFromFileLabel = new Label();
        openFromFileLabel.setText("Pour ouvrir un fichier");

        Button openFromFileButton = new Button();
        openFromFileButton.setText((isMacSupported ? "⌘" : "CTRL+") + "O");
        openFromFileButton.setCursor(Cursor.HAND);
        openFromFileButton.setFocusTraversable(false);

        openFromFileButton.setOnAction((e) -> {
            openFromFile();
        });

        openFromFileBox.getChildren().addAll(openFromFileLabel, openFromFileButton);

        HBox openFromClipboardBox = new HBox();
        openFromClipboardBox.setAlignment(Pos.CENTER);
        openFromClipboardBox.setSpacing(5);

        Label openFromClipboardLabel = new Label();
        openFromClipboardLabel.setText("Pour ouvrir depuis le presse-papier");

        Button openFromClipboardButton = new Button();
        openFromClipboardButton.setText((isMacSupported ? "⌘" : "CTRL+") + "L");
        openFromClipboardButton.setCursor(Cursor.HAND);
        openFromClipboardButton.setFocusTraversable(false);

        openFromClipboardButton.setOnAction((e) -> {
            openFromClipboard();
        });

        openFromClipboardBox.getChildren().addAll(openFromClipboardLabel, openFromClipboardButton);

        explanationBox.getChildren().addAll(openFromFileBox, openFromClipboardBox);

        startingBox.getChildren().addAll(explanationTitle, explanationSeparator, explanationBox);


        VBox aboutBox = new VBox();
        aboutBox.setAlignment(Pos.CENTER);
        aboutBox.setPadding(new Insets(10, 0, 25, 0));

        Label aboutTitle = new Label();
        aboutTitle.setText("En savoir plus");
        aboutTitle.setCursor(Cursor.DEFAULT);
        aboutTitle.setFont(new Font(aboutTitle.getFont().getName(), 20));

        Separator aboutSeparator = new Separator();

        HBox aboutLinksBox = new HBox();
        aboutLinksBox.setAlignment(Pos.CENTER);

        Hyperlink manual = new Hyperlink();
        manual.setText("Manuel d'utilisation (PDF)");
        manual.setUnderline(false);
        manual.setFocusTraversable(false);
        manual.setOnAction((e) -> {
            hostServices.showDocument("https://go.eroschn.fr/lu3in033-projet-manual");
        });

        Hyperlink presentation = new Hyperlink();
        presentation.setText("Vidéo de présentation (Youtube)");
        presentation.setUnderline(false);
        presentation.setFocusTraversable(false);
        presentation.setOnAction((e) -> {
            hostServices.showDocument("https://go.eroschn.fr/lu3in033-projet-presentation");
        });

        Hyperlink sourceCode = new Hyperlink();
        sourceCode.setText("Code source (GitHub)");
        presentation.setUnderline(false);
        sourceCode.setFocusTraversable(false);
        sourceCode.setOnAction((e) -> {
            hostServices.showDocument("https://go.eroschn.fr/lu3in033-projet-source");
        });

        aboutLinksBox.getChildren().addAll(manual, presentation, sourceCode);

        aboutBox.getChildren().addAll(aboutTitle, aboutSeparator, aboutLinksBox);


        box.getChildren().addAll(welcomeBox, startingBox, aboutBox);

        stage.setTitle("Pack3t Visualizer");
        stage.setMinWidth(1080);
        stage.setMinHeight(800);
        stage.setResizable(false);
        stage.setScene(new Scene(box));
        stage.show();
    }

    private void openFromFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            new DecoderWindow(new Stage(), file);
        }
    }

    private void openFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (!clipboard.hasString()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Une erreur s'est produite... :(");
            alert.setHeaderText("Votre presse-papier est vide !");
            alert.setContentText("Raison : presse-papier vide");

            TextArea textArea = new TextArea("");
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);

            alert.showAndWait();

        } else {
            new ClipboardWindow(new Stage(), clipboard.getString());
        }
    }
}
