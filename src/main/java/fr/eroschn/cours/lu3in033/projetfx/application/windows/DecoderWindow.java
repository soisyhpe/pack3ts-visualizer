package fr.eroschn.cours.lu3in033.projetfx.application.windows;

import fr.eroschn.cours.lu3in033.projetfx.utils.Line;
import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetFrame;
import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetType;
import fr.eroschn.cours.lu3in033.projetfx.http.HttpData;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IPv4Frame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddress;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpProtocol;
import fr.eroschn.cours.lu3in033.projetfx.tcp.TcpHeader;
import fr.eroschn.cours.lu3in033.projetfx.tcp.TcpSegment;
import fr.eroschn.cours.lu3in033.projetfx.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DecoderWindow {

    private final List<Line> lines = new ArrayList<>();

    public DecoderWindow(Stage stage, File file) {

        VBox totalContent = new VBox();
        totalContent.setPadding(new Insets(20, 20, 20, 20));


        try {
            ObservableList<Line> frameData = FXCollections.observableArrayList();
            List<EthernetFrame> frames = decodeFile(file);
            long initialeTime = System.nanoTime();

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

                        String tcpFlags = getTcpFlags(tcpSegment.getHeader());
                        String httpComment = inlineText(httpData.toString());

                        String otherBuilder = "SeqNum : " + tcpSegment.getHeader().getSequenceNumber() + ", " +
                                "AckNum : " + tcpSegment.getHeader().getAcknowledgementNumber();

                        StringBuilder comment = new StringBuilder();
                        if (sourcePort == 80 || destinationPort == 80) {
                            comment.append(httpComment);
                        } else if (sourcePort == 443 || destinationPort == 443) {
                            comment.append("[HTTPS] Les données étant chiffrés, [affichage non supporté]");
                        } else {
                            comment.append("[UNKNOWN] Protocole inconnu, [affichage non supporté]");
                        }

                        Line newLine = new Line(System.nanoTime() - initialeTime,
                                sourceAddr,
                                sourcePort,
                                destinationAddr,
                                destinationPort,
                                comment.toString(),
                                tcpFlags,
                                otherBuilder,
                                listProtocols(f));
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
            time.setCellValueFactory(new PropertyValueFactory<>("timeString"));
            table.getColumns().addAll(time);

            Map<String, List<TableColumn>> map = new HashMap<>();

            int i = 0;
            for (Line l : lines) {

                TableColumn<Line, String> addrA = new TableColumn<>(l.getSourceAddress().toString());
                addrA.setMinWidth(300);
                addrA.setEditable(false);
                addrA.setResizable(false);
                addrA.setSortable(false);
                addrA.setReorderable(false);
                addrA.setStyle("-fx-alignment: CENTER-RIGHT;");
                addrA.setCellValueFactory(new PropertyValueFactory<>("sourcePortString"));
                table.getColumns().add(addrA);

                TableColumn<Line, String> addrB = new TableColumn<>(l.getDestinationAddress().toString());
                addrB.setMinWidth(300);
                addrB.setEditable(false);
                addrB.setResizable(false);
                addrB.setSortable(false);
                addrB.setReorderable(false);
                addrB.setStyle("-fx-alignment: CENTER-LEFT;");
                addrB.setCellValueFactory(new PropertyValueFactory<>("destinationPortString"));
                table.getColumns().add(addrB);


                List<TableColumn> list = new ArrayList<>();
                list.add(addrA);
                list.add(addrB);

                map.put(l.getSourceAddress().toString() + ":" + l.getDestinationAddress().toString(), list);
                i++;
            }

            TableColumn<Line, String> protocols = new TableColumn<>("Protocoles");
            protocols.setPrefWidth(100);
            protocols.setEditable(false);
            protocols.setResizable(true);
            protocols.setSortable(false);
            protocols.setReorderable(false);
            protocols.setStyle("-fx-alignment: CENTER-LEFT;");
            protocols.setCellValueFactory(new PropertyValueFactory<>("protocols"));
            table.getColumns().addAll(protocols);

            TableColumn<Line, String> comment = new TableColumn<>("Commentaire");
            comment.setPrefWidth(500);
            comment.setEditable(false);
            comment.setResizable(true);
            comment.setSortable(false);
            comment.setReorderable(false);
            comment.setStyle("-fx-alignment: CENTER-LEFT;");
            comment.setCellValueFactory(new PropertyValueFactory<>("comment"));
            table.getColumns().addAll(comment);


            // on précise les properties
            table.getItems().addAll(frameData);

            /*for (Line l : lines) {
                if (l.)
            }*/


            tableBox.getChildren().addAll(table);


            table.setItems(filteredList);


            HBox buttonsBox = new HBox();
            buttonsBox.setAlignment(Pos.BOTTOM_CENTER);
            buttonsBox.setPadding(new Insets(10, 100, 25, 100));
            buttonsBox.setSpacing(5);


            HBox searchBox = new HBox();
            searchBox.setAlignment(Pos.BOTTOM_LEFT);
            searchBox.setSpacing(5);

            ComboBox<String> choiceBox = new ComboBox<>();
            choiceBox.getItems().addAll("port source", "port destination", "protocole");
            choiceBox.setValue("Aucun filtre...");

            TextField searchBar = new TextField();
            searchBar.setPromptText("Précisez la valeur correspondant à votre filtre ici !");
            searchBar.setFocusTraversable(false);
            searchBar.setPrefWidth(300);

            searchBar.textProperty().addListener((obs, oldValue, newValue) -> {
                switch (choiceBox.getValue()) {
                    case "port source":
                        filteredList.setPredicate(p -> (p.getSourcePort() + "").toLowerCase().equalsIgnoreCase(newValue.toLowerCase().trim()));
                        break;
                    case "port destination":
                        filteredList.setPredicate(p -> (p.getDestinationPort() + "").toLowerCase().equalsIgnoreCase(newValue.toLowerCase().trim()));
                        break;
                    case "protocole":
                        filteredList.setPredicate(p -> (p.getProtocols() + "").toLowerCase().contains(newValue.toLowerCase().trim()));
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
                FileChooser saveFile = new FileChooser();

                saveFile.setTitle("Exporter vers un fichier");
                saveFile.setInitialFileName("promethee_spathis.txt");

                File fileToSave = saveFile.showSaveDialog(new Stage());

                if (fileToSave != null) {
                    FileUtils.exportFile(frames, fileToSave);
                }
            });

            Button closeButton = new Button();
            closeButton.setText("Fermer");
            closeButton.setCursor(Cursor.HAND);
            closeButton.setFocusTraversable(false);
            closeButton.setOnAction((e) -> {
                stage.close();
            });

            buttonsBox.getChildren().addAll(searchBox, exportButton, closeButton);

            VBox informationsBox = new VBox();

            Label warningTitle = new Label();
            warningTitle.setText("Informations");
            warningTitle.setFont(new Font(warningTitle.getFont().getName(), 20));

            Separator informationsSeparator = new Separator();

            Label warningContent = new Label();
            warningContent.setText("Le champ \"Temps\" correspond au temps émis par l'ordinateur à traiter la trame.\n" +
                    "Le champ \"Protocole\" correspond au protocole de la couche la plus haute. Protocoles supportés : http, tcp.\n" +
                    "Le champ \"Commentaire\" correspond aux informations de la couche la plus haute récupérées par le programme. \n" +
                    "Si la taille des informations de cette colonne dépasse la taille initiale, il faut redimensionner manuellement.");

            informationsBox.getChildren().addAll(warningTitle, informationsSeparator, warningContent);

            totalContent.getChildren().addAll(tableBox, buttonsBox, informationsBox);

            stage.setTitle("Visualisateur de trafic - " + file.getName());
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(false);
            stage.setScene(new Scene(totalContent));
            stage.show();


        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Une erreur s'est produite... :(");
            alert.setHeaderText("" + e.getMessage());
            alert.setContentText("Raison : " + e.getCause());

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

    private String listProtocols(EthernetFrame f) {
        List<String> protocols = new ArrayList<>();

        if (f.getHeader().getType().equals(EthernetType.IPV4)) {
            protocols.add(EthernetType.IPV4.getName());

            IPv4Frame ip = new IPv4Frame(f.getData().getBytes());
            if (ip.getHeader().getProtocol().equals(IpProtocol.TCP)) {
                protocols.add(IpProtocol.TCP.getName());

                TcpSegment tcp = new TcpSegment(ip.getData().getBytes());

                HttpData http = new HttpData(ip.getData().getBytes());
                String inlineHttp = inlineText(http.toString());
                if (http.getBytes().length > 0) {

                    // System.out.println(inlineHttp);

                    if (tcp.getHeader().getSourcePort() == 80 || tcp.getHeader().getDestinationPort() == 80) {
                        protocols.add("HTTP");
                    } else if (tcp.getHeader().getSourcePort() == 443 || tcp.getHeader().getDestinationPort() == 443) {
                        protocols.add("HTTPS");
                    } else {
                        protocols.add("??");
                    }
                }
            }
        }

        return listToSB(protocols, "️ >", false).toString();
    }

    private String getTcpFlags(TcpHeader tcpHeader) {
        List<String> flagsTcp = new ArrayList<>();

        if (tcpHeader.isUrg() == 1) flagsTcp.add("URG");
        if (tcpHeader.isAck() == 1) flagsTcp.add("ACK");
        if (tcpHeader.isPsh() == 1) flagsTcp.add("PSH");
        if (tcpHeader.isRst() == 1) flagsTcp.add("RST");
        if (tcpHeader.isSyn() == 1) flagsTcp.add("SYN");
        if (tcpHeader.isFin() == 1) flagsTcp.add("FIN");

        StringBuilder flagsBuilder = listToSB(flagsTcp, ",", true);
        return flagsBuilder.toString().equalsIgnoreCase("[]") ? "" : flagsBuilder.toString();
    }

    private StringBuilder listToSB(List<String> list, String separator, boolean brackets) {
        StringBuilder sb = new StringBuilder();
        if (brackets) sb.append("[");
        int i = 0;
        for (String flag : list) {
            sb.append(flag);
            if (i < list.size() - 1) sb.append(separator).append(" ");
            i++;
        }
        if (brackets) sb.append("]");

        return sb;
    }

    private String inlineText(String initialText) {
        StringBuilder sb = new StringBuilder();
        String[] t = initialText.split("\r\n");
        for (String s : t) {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }

    private List<EthernetFrame> decodeFile(File file) throws IOException {
        try (BufferedReader bufferedReader = normalize(new BufferedReader(new FileReader(file)))) {

            BufferedReader br2 = normalize(new BufferedReader(new FileReader(file)));
            List<EthernetFrame> frames = new ArrayList<>();
            List<Byte> currentFrame = new ArrayList<>();
            String currentOffset;
            String[] currentLine;

            long totalLines = br2.lines().count();
            int lineCounter = 0;

            String line = bufferedReader.readLine();

            // pas un fichier correct
            if (!line.startsWith("0000")) {
                throw new IOException("Impossible de décoder le fichier car ce dernier ne contient pas d'offset !");
            }

            while (line != null) {

                currentLine = line.contains("   ") ? line.split("   ") : line.split("  ");
                currentOffset = currentLine[0];

                // nouvelle trame, on sauvegarde la précédente
                if (currentOffset.equalsIgnoreCase("0000") || lineCounter == (totalLines - 1)) {

                    byte[] oe = toByteTab(currentFrame);

                    if (oe.length > 0) {

                        EthernetFrame ethernetFrame = new EthernetFrame(toByteTab(currentFrame));
                        // System.out.println("\n\n\nDEBUG : \n" + ethernetFrame);

                        if (ethernetFrame.getHeader().getType() == EthernetType.IPV4) {

                            IPv4Frame ipFrame = new IPv4Frame(ethernetFrame.getData().getBytes());
                            // System.out.println("\nDEBUG : \n" + ipFrame);

                            if (ipFrame.getHeader().getProtocol() == IpProtocol.TCP) {

                                TcpSegment tcpSeg = new TcpSegment(ipFrame.getData().getBytes());
                                // System.out.println("\nDEBUG : \n" + tcpSeg);

                                HttpData appData = new HttpData(tcpSeg.getData().getBytes());
                                // System.out.println("\nDEBUG : \nDonees applicatives : " + appData);
                            }

                        }
                        frames.add(ethernetFrame);
                    }
                    currentFrame = new ArrayList<>();
                }

                // on ajoute le contenu de la ligne
                String bytesLine = currentLine[1];
                String[] buffer = bytesLine.split(" ");

                for (String b : buffer) {
                    currentFrame.add((byte) Integer.parseInt(b, 16));
                }

                line = bufferedReader.readLine();
                lineCounter++;
            }

            return frames;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Impossible de trouver le fichier car ce dernier n'a pas été trouvé !");
        } catch (IOException e) {
            throw new IOException("Impossible de lire le fichier !");
        }
    }

    private BufferedReader normalize(BufferedReader reader) {
        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(
                                reader.lines()
                                        .filter(line -> !line.trim().isEmpty())
                                        .collect(Collectors.joining("\n"))
                                        .getBytes(StandardCharsets.UTF_8))));
    }

    private byte[] toByteTab(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

}
