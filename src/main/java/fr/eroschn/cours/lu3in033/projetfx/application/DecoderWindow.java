package fr.eroschn.cours.lu3in033.projetfx.application;

import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetFrame;
import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetType;
import fr.eroschn.cours.lu3in033.projetfx.http.HttpData;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IPv4Frame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddress;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpProtocol;
import fr.eroschn.cours.lu3in033.projetfx.tcp.TcpSegment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DecoderWindow {

    public DecoderWindow(Stage stage, File file) {

        VBox totalContent = new VBox();
        totalContent.setPadding(new Insets(20, 20, 20, 20));


        try {
            ObservableList<Line> frameData = FXCollections.observableArrayList();

            List<EthernetFrame> frames = decodeFile(file);
            List<Line> lines = new ArrayList<>();

            int i = 0;
            for (EthernetFrame f : frames) {

                if (f.getHeader().getType() == EthernetType.IPV4) {
                    IPv4Frame ip = new IPv4Frame(f.getData().getBytes());
                    IpAddress sourceAddr = ip.getHeader().getSourceAddress();
                    IpAddress destinationAddr = ip.getHeader().getDestinationAddress();

                    if (ip.getHeader().getProtocol() == IpProtocol.TCP) {
                        TcpSegment tcpSegment = new TcpSegment(ip.getData().getBytes());
                        int sourcePort = tcpSegment.getHeader().getSourcePort();
                        int destinationPort = tcpSegment.getHeader().getDestinationPort();
                        HttpData httpData = new HttpData(tcpSegment.getData().getBytes());

                        Line newLine = new Line(i++,
                                sourceAddr,
                                sourcePort,
                                destinationAddr,
                                destinationPort,
                                inlineText(httpData.toString()));
                        frameData.add(newLine);
                        lines.add(newLine);
                    }
                }
            }

            VBox tableBox = new VBox();


            FilteredList<Line> filteredList = new FilteredList(frameData, p -> true);


            TableView table = new TableView();
            table.setMaxWidth(800);
            table.setFocusTraversable(false);
            table.setEditable(false);

            TableColumn<Line, String> time = new TableColumn<>("Temps");
            time.setMaxWidth(100);
            time.setEditable(false);
            time.setResizable(false);
            time.setSortable(false);
            time.setReorderable(false);
            time.setStyle("-fx-alignment: CENTER-LEFT;");
            time.setCellValueFactory(new PropertyValueFactory<>("time"));
            table.getColumns().addAll(time);

            List<TableColumn> columns = new ArrayList<>();
            for (Line l : lines) {

                TableColumn<Line, String> addrA = new TableColumn<>(l.getSourceAddress().toString());
                addrA.setMinWidth(300);
                addrA.setEditable(false);
                addrA.setResizable(false);
                addrA.setSortable(false);
                addrA.setReorderable(false);
                addrA.setStyle("-fx-alignment: CENTER-LEFT;");
                addrA.setCellValueFactory(new PropertyValueFactory<>("sourcePort"));
                table.getColumns().addAll(addrA);

                TableColumn<Line, String> addrB = new TableColumn<>(l.getSourceAddress().toString());
                addrB.setMinWidth(300);
                addrB.setEditable(false);
                addrB.setResizable(false);
                addrB.setSortable(false);
                addrB.setReorderable(false);
                addrB.setStyle("-fx-alignment: CENTER-RIGHT;");
                addrB.setCellValueFactory(new PropertyValueFactory<>("destinationPort"));
                table.getColumns().addAll(addrB);

                columns.add(addrA);
                columns.add(addrB);

                // todo : draw arrow from source to destination
            }

            TableColumn<Line, String> comment = new TableColumn<>("Commentaire");
            comment.setPrefWidth(500);
            comment.setEditable(false);
            comment.setResizable(false);
            comment.setSortable(false);
            comment.setReorderable(false);
            comment.setStyle("-fx-alignment: CENTER-LEFT;");
            comment.setCellValueFactory(new PropertyValueFactory<>("comment"));
            table.getColumns().addAll(comment);


            // on précise les properties
            table.getItems().addAll(frameData);


            tableBox.getChildren().addAll(table);


            table.setItems(filteredList);



            HBox buttonsBox = new HBox();
            buttonsBox.setAlignment(Pos.BOTTOM_CENTER);
            buttonsBox.setPadding(new Insets(10, 100, 25, 100));
            buttonsBox.setSpacing(5);



            HBox searchBox = new HBox();
            searchBox.setAlignment(Pos.BOTTOM_LEFT);
            searchBox.setSpacing(5);

            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            choiceBox.getItems().addAll("port source", "port destination");
            choiceBox.setValue("Aucun filtre");

            TextField searchBar = new TextField();
            searchBar.setPromptText("Précisez votre filtre ici. Tips : écrivez pour avoir l'auto-complétion");
            searchBar.setFocusTraversable(false);
            searchBar.setPrefWidth(300);

            searchBar.textProperty().addListener((obs, oldValue, newValue) -> {
                switch (choiceBox.getValue()) {
                    case "port source":
                        filteredList.setPredicate(p -> new String(p.getSourcePort()+"").toLowerCase().contains(newValue.toLowerCase().trim()));
                        break;
                    case "port destination":
                        filteredList.setPredicate(p -> new String(p.getDestinationPort()+"").toLowerCase().contains(newValue.toLowerCase().trim()));
                        break;
                }
            });

            choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)
                    -> {//reset table and textfield when new choice is selected
                if (newVal != null) {
                    searchBar.setText("");
                }
            });

            searchBox.getChildren().addAll(choiceBox, searchBar);



            Button exportButton = new Button();
            exportButton.setText("Exporter");
            exportButton.setCursor(Cursor.HAND);
            exportButton.setFocusTraversable(false);
            exportButton.setOnAction((e) -> {

            });

            Button closeButton = new Button();
            closeButton.setText("Fermer");
            closeButton.setCursor(Cursor.HAND);
            closeButton.setFocusTraversable(false);
            closeButton.setOnAction((e) -> {
                stage.close();
            });

            buttonsBox.getChildren().addAll(searchBox, exportButton, closeButton);

            totalContent.getChildren().addAll(tableBox, buttonsBox);

            stage.setTitle("Visualisateur de trafic - " + file.getName());
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

    public String inlineText(String initialText) {
        StringBuilder sb = new StringBuilder();
        String[] t = initialText.split("\r\n");
        for (String s : t) {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
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
                    currentFrame.add((byte) Integer.parseInt(i, 16));
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
