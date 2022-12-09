package fr.eroschn.cours.lu3in033.projetfx;

import java.io.*;
import java.util.regex.PatternSyntaxException;

public class Analyseur {
    static final int FRAME_SIZE = 1550; //taille max d'une trame Ethernet (en octets)

    //retourne un String représentant les octets passés en paramètre comme des caractères ASCII
    public static String bytesToStringAscii(char[] bytes) {
        if (bytes == null) {
            System.out.println("bytesToStringAscii() : pas d'octets");
            //lancer une exception ?
            return null;
        }

        String res = "";
        int i;
        for (i = 0; i < bytes.length; i++) {
            res += bytes[i];
        }
        return res;
    }

    //vérifie l'égalité de deux tableaux d'octets
    public static boolean compareBytes(char[] bytes1, char[] bytes2) {
        if (bytes1.length != bytes2.length) {
            System.out.println("compareBytes() : pas la meme longueur");
            return false;
        }

        int i;
        for (i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        String fichier = args[0];
        String filtre = null;

        if (args.length == 2) {
            filtre = args[1];
        }

        Analyseur a = new Analyseur();
        a.visualize(fichier, filtre);

    }

    /*
    Lit les trames écrites dans le fichier passé en argument
    Ecrit les trames de manière lisible, filtrées selon le filtre passé en ligne de commande (s'il y a un filtre)
    Le résultat se trouve dans le fichier "resultat.txt"
    */
    public void visualize(String fichier, String filtre) {
        System.out.println("\n---------- Lancement du programme ----------\n\n");
        System.out.println("Decodage en cours...\n");

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("resultat.txt"))) {

                bw.write("\n--------------- Décodage des trames ---------------\n");
                bw.write("\n----- Filtre : ");
                if (filtre == null) {
                    bw.write("pas de filtre\n\n");
                } else {
                    bw.write(filtre + "\n\n");
                }

                String lineString = br.readLine(); //ligne courante
                String[] lineTab; //ligne courante sous forme de tableau séparant les octets
                char[] frameBytes = new char[FRAME_SIZE]; //trame courante sous forme d'octets

                int i = 0, j = 0;
                while (lineString != null) {

                    //la 1ère case du tableau est l'offset, les 2 suivantes sont vides, toutes les suivantes sont les octets
                    lineTab = lineString.split(" ");

                    //on recopie les octets dans la trame courante
                    for (i = 3; i < lineTab.length; i++) {
                        frameBytes[j] = (char) Integer.parseInt(lineTab[i], 16);
                        j++;
                    }

                    //lecture de la ligne suivante
                    lineString = br.readLine();

                    //on regarde si la ligne suivante appartient à une nouvelle trame ou non
                    //si oui, on filtre la trame courante puis on l'écrit si nécessaire, puis on passe à la lecture de la trame suivante
                    //si non, on continue de lire la trame courante sur la ligne suivante
                    if (lineString == null || lineString.startsWith("0000")) {
                        Frame curFrame = new Frame(frameBytes);

                        if (gardeIP(filtre, curFrame.getSourceIP())) {
                            curFrame.writeFrame(bw);
                            bw.write("\n");
                        }

                        frameBytes = new char[FRAME_SIZE];
                        j = 0;
                    }
                }

                bw.write("\n\n\n--------------- Fin du décodage ---------------\n\n");
                System.out.println("Ouvrir le fichier 'resultat.txt' pour voir les trames decodees.\n");
                System.out.println("\n---------- Fin du programme ----------\n\n");
            } catch (IOException e) {
                System.err.println(e);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (PatternSyntaxException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

    }

    //renvoie true si ip est dans le filtre boolExp, false sinon
    //renvoie true s'il n'y a pas de filtre (sans filtre il faut garder toutes les trames)
    public boolean gardeIP(String boolExp, char[] ip) {
        //cas où il n'y a pas de filtre
        if (boolExp == null) {
            return true;
        }

        //on commence par parser le filtre
        String[] filtreSepare = boolExp.split("OU");
        char[][] adressesIP = new char[filtreSepare.length][4]; //tableau des adresses IP passées en ligne de commande

        int i = 0;
        for (String exp : filtreSepare) {
            String[] expSeparee = exp.split("=");
            String[] tempOct = expSeparee[1].split("\\.");

            int j = 0;
            for (String oct : tempOct) {
                adressesIP[i][j] = (char) Integer.parseInt(oct, 10);
                j++;
            }
            i++;
        }

        //on regarde si l'ip passée en paramètre est dans le filtre
        for (char[] ipF : adressesIP) {
            if (compareBytes(ipF, ip)) {
                return true;
            }
        }
        return false;
    }




    /*
    //affiche au format décimal une suite d'octets
    public static void printBytesDec(char[] bytes) {
        if (bytes == null) {
            System.out.println("printBytesDec() : pas d'octets");
            //ici, lancer une exception
            return;
        }

        int i;
        for (i=0; i<bytes.length-1; i++) {
            System.out.print((int)bytes[i] + " ");
        }
        System.out.print((int)bytes[i]);
    }
    */

    /*
    //affiche au format hexadécimal une suite d'octets
    public static void printBytesHex(char[] bytes) {
        if (bytes == null) {
            System.out.println("printBytesHex() : pas d'octets");
            //ici, lancer une exception
            return;
        }

        int i;
        for (i=0; i<bytes.length-1; i++) {
            System.out.print(String.format("%02X", (int)bytes[i]) + " ");
        }
        System.out.print(String.format("%02X", (int)bytes[i]));
    }
    */



    /*
    //s'il n'y a pas de filtre, écriture de la trame dans le fichier
    if (filtre == null) {
        curFrame.writeFrame(bw);
        bw.write("\n");
    }
    //s'il y a un filtre, filtrage de la trame puis écriture de la trame dans le fichier si besoin
    else {
        if (gardeIP(filtre, curFrame.getSourceIP()) == true) {
            curFrame.writeFrame(bw);
            bw.write("\n");
        }
    }
    */

    /*
    //Ecriture de la dernière trame
    Frame curFrame = new Frame(frameBytes);
    if (gardeIP(filtre, curFrame.getSourceIP()) == true) {
        curFrame.writeFrame(bw);
        bw.write("\n");
    }
    */

}

    /*
    String test1 = "ip=21.21.21.21||ip=41.41.41.41";
    String[] res1 = test1.split("\\|\\|");
    for(String s : res1) {
        System.out.println(s);
    }
    */


    /*
    String test1 = "41.41.41.41";
    String[] res1 = test1.split("\\.");
    for(String s : res1) {
        System.out.println(s);
    }
    */


    /*
    String testSplit = "0000   a0 78 17 65 22 87 d2 c8 b2 cc 14 fc 08 00 45 00";
    String[] resSplit = testSplit.split(" ");
    for(String s : resSplit) {
        System.out.println(s + ".");
    }
    */


    /*
    for (char[] fr : framesTest) {
        v.printBytesHex(fr);
        System.out.print("\n");
    }
    */


    /*
    String test = "0F";
    int res = Integer.parseInt(test, 16);
    System.out.println(res);
    */


    /*
    char[] copyTest = v.fieldCopy(framesTest.get(0), 0, 5);
    v.printBytesDec(copyTest);
    */