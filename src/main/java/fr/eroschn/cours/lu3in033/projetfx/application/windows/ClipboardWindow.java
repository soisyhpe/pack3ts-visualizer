package fr.eroschn.cours.lu3in033.projetfx.application.windows;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClipboardWindow {

    public ClipboardWindow(Stage stage, String clipboardContent) {

        stage.setTitle("Importer depuis le presse-papier");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setResizable(false);


        VBox rootBox = new VBox();
        rootBox.setPadding(new Insets(20, 20, 20, 20));


        VBox textBox = new VBox();

        TextArea textArea = new TextArea();
        textArea.setPrefHeight(500);
        textArea.setText(clipboardContent);
        textArea.setFocusTraversable(false);

        textBox.getChildren().add(textArea);


        HBox buttonsBox = new HBox();
        buttonsBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonsBox.setPadding(new Insets(20, 0, 20, 0));
        buttonsBox.setSpacing(5);

        Button confirmationButton = new Button();
        confirmationButton.setText("Confirmer");
        confirmationButton.setCursor(Cursor.HAND);
        confirmationButton.setFocusTraversable(false);
        confirmationButton.setOnAction((e) -> {
            stage.close();

            try {
                System.out.println("" + clipboardContent);
                new DecoderWindow(new Stage(), clipboardToFile("clipboard-" + (int) (Math.random() * 1000) + ".txt", clipboardContent));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button closeButton = new Button();
        closeButton.setText("Fermer");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setFocusTraversable(false);
        closeButton.setOnAction((e) -> {
            stage.close();
        });

        buttonsBox.getChildren().addAll(confirmationButton, closeButton);


        rootBox.getChildren().addAll(textBox, buttonsBox);

        stage.setScene(new Scene(rootBox));
        stage.show();
    }

    private File clipboardToFile(String fileName, String content) throws IOException {
        String tmpPath = System.getProperty("java.io.tmpdir");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tmpPath + "/" + fileName));
        bufferedWriter.write(content);
        bufferedWriter.flush();
        return new File(tmpPath + "/" + fileName);
    }

}
