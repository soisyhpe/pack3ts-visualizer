package fr.eroschn.cours.lu3in033.projetfx.application;

import fr.eroschn.cours.lu3in033.projetfx.Test;
import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetFrame;
import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetType;
import fr.eroschn.cours.lu3in033.projetfx.http.HttpData;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IPv4Frame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpProtocol;
import fr.eroschn.cours.lu3in033.projetfx.tcp.TcpHeader;
import fr.eroschn.cours.lu3in033.projetfx.tcp.TcpSegment;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DecoderWindow {

    private boolean isMacSupported = false;
    ArrayList<File> recents = new ArrayList<>();

    public DecoderWindow(Stage stage, File file) {

        VBox totalContent = new VBox();
        totalContent.setPadding(new Insets(20, 20, 20, 20));



        try {
            List<EthernetFrame> frames = decodeFile(file);

            for (EthernetFrame f : frames) {
                System.out.println(f);
            }

            VBox tableBox = new VBox();
            TableView table = new TableView();
            table.setFocusTraversable(false);
            table.setEditable(false);

            TableColumn<EthernetFrame, String> time = new TableColumn<>("Temps");
            time.setMinWidth(100);
            time.setEditable(false);
            time.setResizable(false);
            time.setSortable(false);
            time.setReorderable(false);

            TableColumn<EthernetFrame, String> addrA = new TableColumn<>("IP A");
            addrA.setMinWidth(300);
            addrA.setEditable(false);
            addrA.setResizable(false);
            addrA.setSortable(false);
            addrA.setReorderable(false);
            TableColumn<EthernetFrame, String> addrA1 = new TableColumn<>("");
            addrA1.setPrefWidth(150);
            TableColumn<EthernetFrame, String> addrA2 = new TableColumn<>("");
            addrA2.setPrefWidth(150);

            addrA.getColumns().addAll(addrA1, addrA2);

            TableColumn<EthernetFrame, String> addrB = new TableColumn<>("IP B");
            addrB.setMinWidth(300);
            addrB.setEditable(false);
            addrB.setResizable(false);
            addrB.setSortable(false);
            addrB.setReorderable(false);
            TableColumn<EthernetFrame, String> addrB1 = new TableColumn<>("");
            addrB1.setPrefWidth(150);
            TableColumn<EthernetFrame, String> addrB2 = new TableColumn<>("");
            addrB2.setPrefWidth(150);

            addrB.getColumns().addAll(addrB1, addrB2);

            TableColumn<EthernetFrame, String> comment = new TableColumn<>("Commentaire");
            comment.setPrefWidth(200);
            comment.setEditable(false);
            comment.setResizable(false);
            comment.setSortable(false);
            comment.setReorderable(false);

            table.getColumns().addAll(time, addrA, addrB, comment);


            ScrollPane tablePane = new ScrollPane();
            tablePane.setFitToWidth(true);
            tablePane.setFitToHeight(true);
            tablePane.setFocusTraversable(false);
            tablePane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            tablePane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            tablePane.setContent(table);

            table.getItems().addAll(frames);



            tableBox.getChildren().addAll(tablePane);


            HBox buttonsBox = new HBox();
            buttonsBox.setAlignment(Pos.BOTTOM_CENTER);
            buttonsBox.setPadding(new Insets(10, 100, 25, 100));
            buttonsBox.setSpacing(5);

            Tooltip exportButtonTooltip = new Tooltip();
            exportButtonTooltip.setText("Permet d'exporter la trace (fichier .txt)");

            Button exportButton = new Button();
            exportButton.setText("Exporter");
            exportButton.setCursor(Cursor.HAND);
            exportButton.setFocusTraversable(false);
            exportButton.setTooltip(exportButtonTooltip);

            Button closeButton = new Button();
            closeButton.setText("Fermer");
            closeButton.setCursor(Cursor.HAND);
            closeButton.setFocusTraversable(false);
            closeButton.setOnAction((e) -> {
                stage.close();
            });

            buttonsBox.getChildren().addAll(exportButton, closeButton);

            totalContent.getChildren().addAll(tableBox, buttonsBox);

            stage.setTitle("Visualisateur de trafic - " + file);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(false);
            stage.setScene(new Scene(totalContent));
            stage.show();


        } catch (IOException e) {
            DialogPane error = new DialogPane();
            error.setHeaderText("Erreur");
        }
    }

    private void openFile(Stage stage, File file) {
        try {
            List<EthernetFrame> frames = decodeFile(file);

            // todo : show every frames

            StackPane root = new StackPane();



            /*
            TextField searchText = new TextField();
            //searchText.setLayoutX(24.0);
            //searchText.setLayoutY(24.0);
            searchText.setPrefHeight(35);
            searchText.setPrefWidth(800);
            searchText.setPromptText("Entrez votre filtre ici ! Tips : commencez à écrire pour avoir l'auto-completion");

            Button searchButton = new Button();
            //searchButton.setLayoutX(24.0);
            //searchButton.setLayoutY(24.0);
            searchButton.setPrefHeight(35);
            searchButton.setPrefWidth(100);
            searchButton.setText("Rechercher");
            */

            TableView<EthernetFrame> table = new TableView<>();

            table.prefWidth(1080);
            table.prefHeight(800);

            TableColumn<EthernetFrame, Integer> timeCol = new TableColumn<>("Temps");
            timeCol.setPrefWidth(100);

            // todo : boucle pour afficher X IP

            TableColumn<EthernetFrame, String> adrrACol = new TableColumn<>("192.168.0.85");
            adrrACol.setPrefWidth(300);

            TableColumn<EthernetFrame, String> adrrBCol = new TableColumn<>("45.155.169.162");
            adrrBCol.setPrefWidth(300);

            TableColumn<EthernetFrame, String> commentCol = new TableColumn<>("Commentaire");
            commentCol.setPrefWidth(200);

            ScrollPane sp = new ScrollPane(table);
            sp.setFitToHeight(true);
            sp.setFitToWidth(true);

            table.getColumns().addAll(timeCol, adrrACol, adrrBCol, commentCol);
            root.getChildren().addAll(table);

            stage.setScene(new Scene(root, 1080, 800));

            for (EthernetFrame f : frames) {

            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur s'est produite lors de l'ouverture du fichier !");
            alert.setContentText(e.getMessage());

            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            TextArea textArea = new TextArea(exceptionText);
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
        }
    }

    public static List<EthernetFrame> decodeFile(File file) throws IOException {
        try (BufferedReader bufferedReader = normalize(new BufferedReader(new FileReader(file)))) {

            List<EthernetFrame> frames = new ArrayList<>();
            List<Byte> currentFrame = new ArrayList<>();
            String currentOffset;
            String[] currentLine;

            String line = bufferedReader.readLine();

            while (line != null) {
                // System.out.println(line);
                currentLine = line.split("   ");
                currentOffset = currentLine[0];

                // nouvelle trame, on sauvegarde la précédente
                if (currentOffset.equalsIgnoreCase("0000") || line.isEmpty()) {

                    byte[] oe = toByteTab(currentFrame);
                    if (oe.length > 0) {
                        EthernetFrame ethernetFrame = new EthernetFrame(toByteTab(currentFrame));
                        System.out.println("\n\n\nDEBUG : \n" + ethernetFrame);

                        if (ethernetFrame.getHeader().getType() == EthernetType.IPV4) {
                            IPv4Frame ipFrame = new IPv4Frame(ethernetFrame.getData().getBytes());
                            System.out.println("\nDEBUG : \n" + ipFrame);

                            if (ipFrame.getHeader().getProtocol() == IpProtocol.TCP) {
                                TcpSegment tcpSeg = new TcpSegment(ipFrame.getData().getBytes());
                                System.out.println("\nDEBUG : \n" + tcpSeg);

                                HttpData appData = new HttpData(tcpSeg.getData().getBytes());
                                System.out.println("\nDEBUG : \nDonees applicatives : " + appData);
                            }

                        }
                        frames.add(ethernetFrame);
                    }
                    currentFrame = new ArrayList<>();
                }

                // on ajoute le contenu de la ligne
                for (String i : currentLine[1].split(" ")) {
                    currentFrame.add((byte)Integer.parseInt(i, 16));
                }

                line = bufferedReader.readLine();
            }

            return frames;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Erreur : impossible de trouver le fichier !");
        } catch (IOException e) {
            throw new IOException("Erreur : impossible de lire le fichier !");
        }
    }

    /**
     * Permet de "normaliser" le BufferedReader pour ne prendre
     * en compte que les lignes non-vides
     * @param reader ancien BufferedReader
     * @return nouveau BufferedReader
     */
    private static BufferedReader normalize(BufferedReader reader) {
        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(
                                reader.lines()
                                        .filter(line -> !line.trim().isEmpty())
                                        .collect(Collectors.joining("\n"))
                                        .getBytes(StandardCharsets.UTF_8))));
    }

    private static byte[] toByteTab(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

}
