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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DecoderWindow {

    private final boolean isMacSupported = false;
    ArrayList<File> recents = new ArrayList<>();

    public DecoderWindow(Stage stage, File file) {

        VBox totalContent = new VBox();
        totalContent.setPadding(new Insets(20, 20, 20, 20));


        try {
            ObservableList<Line> frameData = FXCollections.observableArrayList();

            List<EthernetFrame> frames = decodeFile(file);
            List<IpAddress> addresses = new ArrayList<>();

            int i = 0;
            for (EthernetFrame f : frames) {

                if (f.getHeader().getType() == EthernetType.IPV4) {
                    IPv4Frame ip = new IPv4Frame(f.getData().getBytes());
                    IpAddress sourceAddr = ip.getHeader().getSourceAddress();
                    IpAddress destinationAddr = ip.getHeader().getDestinationAddress();

                    // pour éviter la duplication d'IP
                    if (!addresses.contains(sourceAddr)) addresses.add(sourceAddr);
                    if (!addresses.contains(destinationAddr)) addresses.add(destinationAddr);

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
                                httpData.toString());
                        frameData.add(newLine);
                    }
                }
            }

            VBox tableBox = new VBox();

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

            List<TableColumn> columns = new ArrayList<>();
            for (IpAddress ip : addresses) {

                TableColumn<Line, String> addrA = new TableColumn<>(ip.toString());
                addrA.setMinWidth(300);
                addrA.setEditable(false);
                addrA.setResizable(false);
                addrA.setSortable(false);
                addrA.setReorderable(false);

                TableColumn<Line, String> addrA1 = new TableColumn<>("");
                addrA1.setPrefWidth(150);

                TableColumn<Line, String> addrA2 = new TableColumn<>("");
                addrA2.setPrefWidth(150);

                addrA.getColumns().addAll(addrA1, addrA2);

                columns.add(addrA);
            }

            TableColumn<Line, String> comment = new TableColumn<>("Commentaire");
            comment.setPrefWidth(200);
            comment.setEditable(false);
            comment.setResizable(false);
            comment.setSortable(false);
            comment.setReorderable(false);


            time.setCellValueFactory(new PropertyValueFactory<>("time"));

            for (TableColumn e : columns) {
                e.setCellValueFactory(new PropertyValueFactory<>("sourceAddress"));
            }

            comment.setCellValueFactory(new PropertyValueFactory<>("comment"));


            table.getColumns().addAll(time);
            for (TableColumn t : columns) {
                table.getColumns().add(t);
            }
            table.getColumns().addAll(comment);


            // on précise les properties
            table.getItems().addAll(frameData);


            tableBox.getChildren().addAll(table);


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

    /**
     * Permet de "normaliser" le BufferedReader pour ne prendre
     * en compte que les lignes non-vides
     *
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
