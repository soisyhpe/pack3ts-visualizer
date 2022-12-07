package fr.eroschn.cours.lu3in033.projetfx;

import java.io.BufferedWriter;
import java.io.IOException;

/* 
Classe de représentation d'une trame, avec des attributs contenant seulement les entêtes et données utiles pour notre application
*/

public class Frame {

    //entêtes IP
    private char[] srcIP;
    private char[] dstIP;

    //entêtes TCP
    private int srcPort = 0;
    private int dstPort = 0;
    private int sequenceNumber;
    private int ACKNumber;
    private char ACK;
    private char SYN;
    private char FIN;

    //données applicatives
    private char[] applicationData;


    //construit une 'trame' Frame à partir de la 'trame brute' bytes
    public Frame(char[] bytes) {
        if (bytes == null) {
            //lancer une exception ?
            return;
        }

        //indice de l'octet courant
        //on commence directement par pointer sur le 1er octet du paquet IP
        int i = 14;

        //récupération de la longueur de l'entête IP (en mots de 32 bits) et de celle du paquet IP (en octets)
        char headerLengthIP = (char) (((int) bytes[i]) % 16);
        i += 2;
        int totalLengthIP = byteArrayToInt(fieldCopy(bytes, i, i + 1));

        //récupération des adresses IP
        i += 10;
        this.srcIP = fieldCopy(bytes, i, i + 3);
        i += 4;
        this.dstIP = fieldCopy(bytes, i, i + 3);
        i = i + 4 + (((int) headerLengthIP * 4) - 20); //on pointe sur le segment TCP

        //récupération des numéros de port
        this.srcPort = byteArrayToInt(fieldCopy(bytes, i, i + 1));
        i += 2;
        this.dstPort = byteArrayToInt(fieldCopy(bytes, i, i + 1));
        i += 2;

        //récupération du numéro de séquence + ACK number
        this.sequenceNumber = byteArrayToInt(fieldCopy(bytes, i, i + 3));
        i += 4;
        this.ACKNumber = byteArrayToInt(fieldCopy(bytes, i, i + 3));
        i += 4;

        //récupération de la longueur de l'entête TCP (en mots de 32 bits)
        char headerLengthTCP = (char) (((int) bytes[i]) / 16);

        //récupération des drapeaux ACK, SYN et FIN
        i++;
        char octet = bytes[i];
        this.FIN = (char) (((int) octet) % 2);
        octet = (char) ((int) octet / 2);
        this.SYN = (char) (((int) octet) % 2);
        octet = (char) ((int) octet / 8);
        this.ACK = (char) (((int) octet) % 2);
        i = i + 7 + (((int) headerLengthTCP * 4) - 20); // on pointe sur les données applicatives

        //récupération des données applicatives
        this.applicationData = fieldCopy(bytes, i, i - 1 + (totalLengthIP - (headerLengthTCP * 4) - (headerLengthIP * 4)));
    }


    //retourne l'entier correspondant à la suite d'octets passée en argument
    public static int byteArrayToInt(char[] bytes) {
        int poids = 1;
        int res = 0;

        int i;
        for (i = bytes.length - 1; i >= 0; i--) {
            res += ((int) bytes[i]) * poids;
            poids = poids * 256;
        }
        return res;
    }


    //retourne un sous-tableau du tableau d'octets passé en paramètre, commençant à l'indice début et finissant à l'indice fin
    public static char[] fieldCopy(char[] frame, int debut, int fin) {
        if (fin >= frame.length || debut < 0 || fin < 0) {
            System.out.print("fieldCopy() : pas d'octets");
            //lancer une exception ?
            return null;
        }

        char[] res = new char[fin - debut + 1];
        int i;
        for (i = 0; i < res.length; i++) {
            res[i] = frame[debut + i];
        }
        return res;
    }


    //écrit une trame dans un fichier dans un format "lisible"
    public void writeFrame(BufferedWriter bw) {
        try {
            bw.write(IPAdressToString(this.srcIP));
            bw.write("    ");

            bw.write("" + this.srcPort);
            bw.write(" -> ");
            bw.write("" + this.dstPort);

            bw.write("    ");
            bw.write(IPAdressToString(this.dstIP));

            bw.write("    ");
            if (this.SYN == 1) {
                bw.write("[SYN]");
            }
            if (this.ACK == 1) {
                bw.write("[ACK]");
            }
            if (this.FIN == 1) {
                bw.write("[FIN]");
            }

            bw.write(" Seq=" + sequenceNumber);
            if (this.ACK == 1) {
                bw.write(" Ack=" + ACKNumber);
            }

            bw.write("\n");
            bw.write(Analyseur.bytesToStringAscii(this.applicationData));
        } catch (IOException e) {
            System.err.println(e);
        }
    }


    //retourne un String représentant une adresse IP au format standard
    public static String IPAdressToString(char[] bytes) {
        if (bytes == null) {
            System.out.print("IPAdressToString() : pas d'IP");
            //lancer une exception ?
            return null;
        }

        String res = "";
        int i;
        for (i = 0; i < bytes.length - 1; i++) {
            res += (int) bytes[i] + ".";
        }
        res += (int) bytes[i];
        return res;
    }


    //retourne l'adresse IP source
    public char[] getSourceIP() {
        return this.srcIP;
    }




    /*
    //affiche une adresse MAC au format standard
    public static void printMACAdress(char[] bytes) {
        if (bytes == null) {
            //ici, lancer une exception
            return;
        }
        int i;
        for (i=0; i<bytes.length-1; i++) {
            System.out.print(String.format("%02X", (int)bytes[i]) + ":");
        }
        System.out.print(String.format("%02X", (int)bytes[i]));
    }
    */

    /*
    //affiche une trame dans un format "lisible"
    public void printFrame() {
        System.out.print(IPAdressToString(this.srcIP));
        System.out.print("    ");

        System.out.print(this.srcPort);
        System.out.print(" -> ");
        System.out.print(this.dstPort);

        System.out.print("    ");
        System.out.print(IPAdressToString(this.dstIP));

        System.out.print("    ");
        if (this.SYN == 1) {
            System.out.print("[SYN]");
        }
        if (this.ACK == 1) {
            System.out.print("[ACK]");
        }
        if (this.FIN == 1) {
            System.out.print("[FIN]");
        }

        System.out.print(" Seq=" + sequenceNumber);
        if (this.ACK == 1) {
            System.out.print(" Ack=" + ACKNumber);
        }

        System.out.print("\n");
        System.out.print(bytesToStringAscii(this.applicationData));
    }
    */
    

    /*
    if (this.SYN == 1) {
            if (this.ACK == 1) {
                System.out.print("[SYN, ACK]");
            } else {
                System.out.print("[SYN]");
            }
        } else if (this.ACK == 1) {
            if (this.FIN == 1) {
                System.out.print("[ACK, FIN]");
            } else {
                System.out.print("[ACK]");
            }
        } else if (this.FIN == 1) {
            System.out.print("[FIN]");
        }
    */

}