package fr.eroschn.cours.lu3in033.projetfx.application.windows;

import fr.eroschn.cours.lu3in033.projetfx.application.IpAddressTuple;
import fr.eroschn.cours.lu3in033.projetfx.application.Line;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DecoderWindow {

    private List<Line> lines = new ArrayList<>();

    public DecoderWindow(Stage stage, File file) {

        VBox totalContent = new VBox();
        totalContent.setPadding(new Insets(20, 20, 20, 20));


        try {
            ObservableList<Line> frameData = FXCollections.observableArrayList();

            List<EthernetFrame> frames = decodeFile(file);

            for (EthernetFrame f : frames) {

                if (f.getHeader().getType() == EthernetType.IPV4) {
                    IPv4Frame ip = new IPv4Frame(f.getData().getBytes());
                    IpAddress sourceAddr = ip.getHeader().getSourceAddress();
                    IpAddress destinationAddr = ip.getHeader().getDestinationAddress();

                    //if (isEquivExists(sourceAddr, destinationAddr))
                    //    continue;

                    if (ip.getHeader().getProtocol() == IpProtocol.TCP) {
                        TcpSegment tcpSegment = new TcpSegment(ip.getData().getBytes());
                        int sourcePort = tcpSegment.getHeader().getSourcePort();
                        int destinationPort = tcpSegment.getHeader().getDestinationPort();
                        HttpData httpData = new HttpData(tcpSegment.getData().getBytes());

                        String comment = inlineText(httpData.toString());

                        Line newLine = new Line(System.currentTimeMillis(),
                                sourceAddr,
                                sourcePort,
                                destinationAddr,
                                destinationPort,
                                ((sourcePort == 80 || destinationPort == 80) && comment.contains("HTTP") ? "[HTTP] : " + comment : (httpData.getBytes().length == 0 ? "[TCP] : " + tcpSegment.getHeader().toString() : "[TCP] ?")));
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

            Map<String, List<TableColumn>> map = new HashMap<>();

            for (Line l : lines) {

                TableColumn<Line, String> addrA = new TableColumn<>(l.getSourceAddress().toString());
                addrA.setMinWidth(300);
                addrA.setEditable(false);
                addrA.setResizable(false);
                addrA.setSortable(false);
                addrA.setReorderable(false);
                addrA.setStyle("-fx-alignment: CENTER-RIGHT;");
                addrA.setCellValueFactory(new PropertyValueFactory<>("sourcePortString"));
                table.getColumns().addAll(addrA);

                TableColumn<Line, String> addrB = new TableColumn<>(l.getDestinationAddress().toString());
                addrB.setMinWidth(300);
                addrB.setEditable(false);
                addrB.setResizable(false);
                addrB.setSortable(false);
                addrB.setReorderable(false);
                addrB.setStyle("-fx-alignment: CENTER-LEFT;");
                addrB.setCellValueFactory(new PropertyValueFactory<>("destinationPortString"));
                table.getColumns().addAll(addrB);


                List<TableColumn> list = new ArrayList<>();
                list.add(addrA);
                list.add(addrB);

                map.put(l.getSourceAddress().toString() + ":" + l.getDestinationAddress().toString(), list);

                // todo : draw arrow from source to destination
                /*
                // Create a new Polyline object to draw the arrow
                Polyline arrow = new Polyline();

                // Add the points of the arrow to the Polyline
                arrow.getPoints().addAll(new Double[]{
                        x1, y1,   // Starting point of the arrow
                        x2, y2,   // Ending point of the arrow
                        xA, yA,   // First point of the arrowhead
                        x2, y2,   // Back to the ending point of the arrow
                        xB, yB    // Second point of the arrowhead
                });

                // Set the stroke style of the arrow to be a solid line
                arrow.setStroke(Color.BLACK);
                arrow.setStrokeWidth(1.0);
                */

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
                        filteredList.setPredicate(p -> new String(p.getSourcePort() + "").toLowerCase().contains(newValue.toLowerCase().trim()));
                        break;
                    case "port destination":
                        filteredList.setPredicate(p -> new String(p.getDestinationPort() + "").toLowerCase().contains(newValue.toLowerCase().trim()));
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
                exportFile(frames);
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

    public static void exportFile(List<EthernetFrame> frames) {

        // la liste d'ip
        List<IpAddress> addresses = new ArrayList<>();

        // les paires d'ip
        List<IpAddressTuple> tuples = new ArrayList<>();

        // on récupère la liste d'ip, sans doublons
        for (EthernetFrame frame : frames) {
            IPv4Frame ipFrame = new IPv4Frame(frame.getData().getBytes());
            IpAddress sourceAddress = ipFrame.getHeader().getSourceAddress();
            IpAddress destinationAddress = ipFrame.getHeader().getDestinationAddress();

            IpAddressTuple tuple = new IpAddressTuple(sourceAddress, destinationAddress);
            tuple.getFrames().add(frame);
            tuples.add(tuple);

            if (addresses.contains(sourceAddress)) continue;
            if (addresses.contains(destinationAddress)) continue;

            addresses.add(sourceAddress);
            addresses.add(destinationAddress);
        }

        // check
//        for (IpAddress ip : addresses) {
//            System.out.println(ip);
//        }

        System.out.println("avant");
        for (IpAddressTuple t1 : tuples) {
            System.out.println(t1);
        }

        List<IpAddressTuple> nt1 = new ArrayList<>(tuples);
        List<IpAddressTuple> nt2 = new ArrayList<>(tuples);

        for (IpAddressTuple t1 : nt1) {
            for (IpAddressTuple t2 : nt2) {
                if (t1 != t2) {
                    if (t1.getFirstAddress().equals(t2.getSecondAddress()) && t1.getSecondAddress().equals(t2.getFirstAddress())) {

                        t1.getFrames().addAll(t2.getFrames());

                        tuples.remove(t2);
                    }
                }
            }
        }

        System.out.println("après");
        for (IpAddressTuple t1 : tuples) {
            System.out.println(t1.getFirstAddress() + "                                                               " + t1.getSecondAddress());

            List<EthernetFrame> tupleFrames = t1.getFrames();

            for (EthernetFrame ef : tupleFrames) {
                IPv4Frame ip = new IPv4Frame(ef.getData().getBytes());
                TcpSegment tcp = new TcpSegment(ip.getData().getBytes());

                if (ip.getHeader().getSourceAddress().equals(t1.getFirstAddress())) {
                    System.out.println(tcp.getHeader().getSourcePort() + "----------------------------------------------------------------------------->" + tcp.getHeader().getDestinationPort());
                } else if (ip.getHeader().getSourceAddress().equals(t1.getSecondAddress())) {
                    System.out.println(tcp.getHeader().getDestinationPort() + "<-----------------------------------------------------------------------------" + tcp.getHeader().getSourcePort());
                }

            }
        }



//        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/flux.txt"))) {
//            bw.write("\n---------------------------------- Analyse des flux ----------------------------------\n\n");
//            System.out.println("Fichier exporté");
//
//            List<IpAddress[]> listIps = new ArrayList<>();
//            IpAddress ip1 = null;
//            IpAddress ip2 = null;
//
//            //on commence par récupérer la liste des paires d'IP de la trace
//            int i = 0;
//            for (EthernetFrame frame : frames) {
//                ip1 = new IPv4Frame(frame.getData().getBytes()).getHeader().getSourceAddress();
//                ip2 = new IPv4Frame(frame.getData().getBytes()).getHeader().getDestinationAddress();
//                IpAddress[] duoIps = new IpAddress[2];
//                duoIps[0] = ip1;
//                duoIps[1] = ip2;
//                listIps.add(duoIps);
//            }
//
//            //on retire les doublons de la liste
//            List<IpAddress[]> listIpsCopy = List.copyOf(listIps);
//            //System.out.println(listIps.size());
//
//          /*
//          for (IpAddress[] duoIps1 : listIps) {
//
//          }
//          */
//
//            for (IpAddress[] duoIps1 : listIpsCopy) {
//                for (IpAddress[] duoIps2 : listIpsCopy) {
//                    if (duoIps1 != duoIps2
//                            && ((duoIps1[0].equals(duoIps2[0]) && duoIps1[1].equals(duoIps2[1]))
//                            || (duoIps1[0].equals(duoIps2[0]) && duoIps1[1].equals(duoIps2[1])))) {
//                        System.out.println(" ip1 = " + duoIps1[0] + " ip2 = " + duoIps1[1]);
//                        System.out.println("removing : ip1 = " + duoIps2[0] + " ip2 = " + duoIps2[1]);
//                        listIps.remove(duoIps2);
//                    }
//                }
//            }
//
//            //System.out.println(listIps.size());
//
//            //((duoIps1[0] == duoIps2[0] && duoIps1[1] == duoIps2[1]) || (duoIps1[0] == duoIps2[1] && duoIps1[0] == duoIps2[1])))
//
//            List<IpAddress> tmp = new ArrayList<>();
//
//            //pour chaque paire d'IP on écrit le flux de trames correspondant
//            for (IpAddress[] duoIps : listIps) {
//                bw.write("\n\n\n" + duoIps[0].toString()
//                        + " "
//                        + duoIps[1].toString() + "\n\n");
//
//                for (EthernetFrame frame : frames) {
//
//                    if (belongsTo(frame, duoIps) == true) {
//
//                        //if (tmp.contains(duoIps[0]) && tmp.contains(duoIps[1])) continue;
//
//                        IPv4Frame fr = new IPv4Frame(frame.getData().getBytes());
//                        TcpSegment seg = new TcpSegment(fr.getData().getBytes());
//
//                        bw.write(" ");
//                        if (seg.getHeader().isSyn() == 1) {
//                            bw.write("[SYN]");
//                        }
//                        if (seg.getHeader().isAck() == 1) {
//                            bw.write("[ACK]");
//                        }
//                        if (seg.getHeader().isFin() == 1) {
//                            bw.write("[FIN]");
//                        }
//                        if (seg.getHeader().isRst() == 1) {
//                            bw.write("[RST]");
//                        }
//                        if (seg.getHeader().isPsh() == 1) {
//                            bw.write("[PSH]");
//                        }
//                        if (seg.getHeader().isUrg() == 1) {
//                            bw.write("[URG]");
//                        }
//
//                        bw.write(" Seq=" + (seg.getHeader().getSequenceNumber()));
//                        if (seg.getHeader().isAck() == 1) {
//                            bw.write(" Ack=" + (seg.getHeader().getAcknowledgementNumber()));
//                        }
//                        bw.write(" Win=" + (seg.getHeader().getWindow()));
//                        bw.write(" Len=" + (seg.getData().getBytes().length));
//
//                        tmp.add(duoIps[0]);
//                        tmp.add(duoIps[1]);
//
//                        if (duoIps[0].equals(fr.getHeader().getSourceAddress())) {
//                            bw.write("\n " + (seg.getHeader().getSourcePort()));
//                            bw.write(" ----------------------------------------------------------------------------->");
//                            bw.write(" " + (seg.getHeader().getDestinationPort()));
//                            bw.write("\n\n");
//                        } else if (duoIps[1].equals(fr.getHeader().getSourceAddress())) {
//                            bw.write("\n " + (seg.getHeader().getDestinationPort()));
//                            bw.write(" <-----------------------------------------------------------------------------");
//                            bw.write(" " + (seg.getHeader().getSourcePort()) + " ");
//                            bw.write("\n\n");
//                        }
//                        //}
//                    }
//                }
//
//                bw.write("\n\n\n----------------------------------- Fin des flux -----------------------------------\n\n");
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static boolean belongsTo(EthernetFrame frame, IpAddress[] duoIps) {
        IPv4Frame fr = new IPv4Frame(frame.getData().getBytes());

        if ((fr.getHeader().getSourceAddress().equals(duoIps[0]) && fr.getHeader().getDestinationAddress().equals(duoIps[1]))
                || (fr.getHeader().getSourceAddress().equals(duoIps[1]) && fr.getHeader().getDestinationAddress().equals(duoIps[0]))) {
            return true;
        }
        return false;
    }

    private boolean isEquivExists(IpAddress sourceAddress, IpAddress destinationAddress) {
        for (Line l : lines) {

            if (l.getSourceAddress().equals(sourceAddress) && l.getDestinationAddress().equals(destinationAddress))
                return true;

            if (l.getSourceAddress().equals(destinationAddress) && l.getDestinationAddress().equals(sourceAddress))
                return true;

        }

        return false;
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

                currentLine = line.split("   ");
                currentOffset = currentLine[0];

                // nouvelle trame, on sauvegarde la précédente
                if (currentOffset.equalsIgnoreCase("0000") || lineCounter == (totalLines - 1)) {

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
