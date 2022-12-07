package fr.eroschn.cours.lu3in033.projetfx.ethernet;//package fr.eroschn.cours.lu3in033.projetfx.ethernet;

public class EthernetData {

    private static final int ETHERNET_DATA_MAX_LENGTH = 1500; // taille maximale d'une trame ethernet
    private static final int ETHERNET_HEADER_LENGTH = 14; // taille d'une en-tête ethernet
    private final byte[] bytes;

    public EthernetData(byte[] bytes) {
        int dataLength = Math.min((bytes.length - ETHERNET_HEADER_LENGTH), ETHERNET_DATA_MAX_LENGTH);

        this.bytes = new byte[dataLength];

        int i, j = 0;
        for (i = ETHERNET_HEADER_LENGTH; i < bytes.length; i++) {
            this.bytes[j] = bytes[i];
            j++;
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
