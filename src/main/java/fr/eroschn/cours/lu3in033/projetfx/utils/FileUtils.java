package fr.eroschn.cours.lu3in033.projetfx.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.eroschn.cours.lu3in033.projetfx.application.IpAddressTuple;
import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetFrame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IPv4Frame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddress;
import fr.eroschn.cours.lu3in033.projetfx.tcp.TcpSegment;

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
                        + "                                                                                   "
                        + tuple.getSecondAddress().toString() + "\n\n");
                
                for (EthernetFrame frame : frames) {
                	
                	IPv4Frame fr = new IPv4Frame(frame.getData().getBytes());
                    TcpSegment seg = new TcpSegment(fr.getData().getBytes());
                    
                    if (tuple.belongsTo(frame) == true) {
                    	
                    	bw.write("           ");
                        if (seg.getHeader().isSyn() == 1) {
                            bw.write("[SYN]");
                        }
                        if (seg.getHeader().isAck() == 1) {
                           bw.write("[ACK]");
                        }
                        if (seg.getHeader().isFin() == 1) {
                            bw.write("[FIN]");
                        }
                        if (seg.getHeader().isRst() == 1) {
                            bw.write("[RST]");
                        }
                        if (seg.getHeader().isPsh() == 1) {
                            bw.write("[PSH]");
                        }
                        if (seg.getHeader().isUrg() == 1) {
                            bw.write("[URG]");
                        }

                        bw.write(" Seq=" + (seg.getHeader().getSequenceNumber()));
                        
                        if (seg.getHeader().isAck() == 1) {
                            bw.write(" Ack=" + (seg.getHeader().getAcknowledgementNumber()));
                        }
                        
                        bw.write(" Win=" + (seg.getHeader().getWindow()));
                        bw.write(" Len=" + (seg.getData().getBytes().length));
                        
                        if (tuple.getFirstAddress().equals(fr.getHeader().getSourceAddress())) {
                            bw.write("\n   " + (seg.getHeader().getSourcePort()));
                            bw.write(" ------------------------------------------------------------------------------------------>");
                            bw.write(" " + (seg.getHeader().getDestinationPort()));
                            bw.write("\n\n");
                        } else if (tuple.getSecondAddress().equals(fr.getHeader().getSourceAddress())) {
                            bw.write("\n   " + (seg.getHeader().getDestinationPort()));
                            bw.write(" <------------------------------------------------------------------------------------------");
                            bw.write(" " + (seg.getHeader().getSourcePort()) + " ");
                            bw.write("\n\n");
                        }
                        
                    } //fin if belongs
                    
                } // fin for frame
                
            } //fin for tuple
            
            bw.write("\n\n************************************************** Fin des flux **************************************************\n");
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
