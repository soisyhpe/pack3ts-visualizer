package fr.eroschn.cours.lu3in033.projetfx.utils;

public class ByteUtils {

    //retourne l'entier correspondant à la suite d'octets passée en argument
    public static int byteArrayToInt(byte[] bytes) {
        int poids = 1;
        int res = 0;

        int i;
        for (i = bytes.length - 1; i >= 0; i--) {
            res += (Byte.toUnsignedInt(bytes[i])) * poids;
            poids = poids * 256;
        }
        return res;
    }
}
