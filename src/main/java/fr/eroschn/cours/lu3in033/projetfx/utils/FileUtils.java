package fr.eroschn.cours.lu3in033.projetfx.utils;

import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddressTuple;
import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetFrame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IPv4Frame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddress;
import fr.eroschn.cours.lu3in033.projetfx.tcp.TcpSegment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static void exportFile(List<EthernetFrame> frames, File fileToSave) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
            bw.write("\n********************************************* Analyse des flux **********************************************\n");
            System.out.println("Fichier exporté");


            //on commence par récupérer la liste des paires d'IP de la trace
            List<IpAddressTuple> listIps = new ArrayList<>();
            IpAddress ip1 = null;
            IpAddress ip2 = null;

            for (EthernetFrame frame : frames) {
                ip1 = new IPv4Frame(frame.getData().getBytes()).getHeader().getSourceAddress();
                ip2 = new IPv4Frame(frame.getData().getBytes()).getHeader().getDestinationAddress();
                IpAddressTuple tuple = new IpAddressTuple(ip1, ip2);
                listIps.add(tuple);
            }

            //on crée une nouvelle liste sans doublon
            List<IpAddressTuple> listIpsSD = new ArrayList<>();

            for (IpAddressTuple tuple : listIps) {
                if (IpAddressTuple.containsSimilar(listIpsSD, tuple) == false) {
                    listIpsSD.add(tuple);
                }
            }

            //pour chaque paire d'IP on écrit le flux de trames correspondant
            for (IpAddressTuple tuple : listIpsSD) {
                bw.write("\n\n\n\n" + tuple.getFirstAddress().toString()
                        + "\t\t\t\t\t\t\t\t\t\t\t"
                        + tuple.getSecondAddress().toString() + "\n\n");

                for (EthernetFrame frame : frames) {

                    IPv4Frame fr = new IPv4Frame(frame.getData().getBytes());

                    if (tuple.belongsTo(frame) == true) {
                        TcpSegment seg = new TcpSegment(fr.getData().getBytes());
                        int srcPort = seg.getHeader().getSourcePort();
                        int dstPort = seg.getHeader().getDestinationPort();
                        IpAddress srcIp = fr.getHeader().getSourceAddress();


                        bw.write("             ");
                        if (srcPort == 80 ) {
                            for (byte b : seg.getData().getBytes()) {
                                if (Byte.toUnsignedInt(b) == 10) {break;}
                                bw.write((char)b);
                            }
                        } else {
                            writeFlags(bw, seg);
                        }

                        if (tuple.getFirstAddress().equals(srcIp)) {
                            bw.write("\n   " + srcPort);
                            bw.write(" ------------------------------------------------------------------------------------------>");
                            bw.write(" " + dstPort);
                        }
                        else if (tuple.getSecondAddress().equals(srcIp)) {
                            bw.write("\n   " + dstPort);
                            bw.write(" <------------------------------------------------------------------------------------------");
                            bw.write(" " + srcPort);
                        }

                        if (srcPort == 80) {
                            bw.write("       HTTP: ");
                            for (byte b : seg.getData().getBytes()) {
                                if (Byte.toUnsignedInt(b) == 10) {break;}
                                bw.write((char)b);
                            }
                        }
                        else {
                            bw.write("       TCP: ");
                            bw.write((seg.getHeader().getSourcePort()) + "->" + (seg.getHeader().getDestinationPort()) + " ");
                            writeFlags(bw, seg);
                        }

                        bw.write("\n\n");

                    } //fin if belongs

                } // fin for frame

            } //fin for tuple

            bw.write("\n\n************************************************** Fin des flux **************************************************\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeFlags(BufferedWriter bw, TcpSegment seg) throws IOException {
        if (seg.getHeader().isSyn() == 1) {bw.write("[SYN]");}
        if (seg.getHeader().isAck() == 1) {bw.write("[ACK]");}
        if (seg.getHeader().isFin() == 1) {bw.write("[FIN]");}
        if (seg.getHeader().isRst() == 1) {bw.write("[RST]");}
        if (seg.getHeader().isPsh() == 1) {bw.write("[PSH]");}
        if (seg.getHeader().isUrg() == 1) {bw.write("[URG]");}

        bw.write(" Seq=" + (seg.getHeader().getSequenceNumber()));
        if (seg.getHeader().isAck() == 1) {
            bw.write(" Ack=" + (seg.getHeader().getAcknowledgementNumber()));
        }
        bw.write(" Win=" + (seg.getHeader().getWindow()));
        bw.write(" Len=" + (seg.getData().getBytes().length));
    }
}
