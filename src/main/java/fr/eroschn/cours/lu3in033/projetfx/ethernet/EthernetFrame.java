package fr.eroschn.cours.lu3in033.projetfx.ethernet;//package fr.eroschn.cours.lu3in033.projetfx.ethernet;

public class EthernetFrame {

    private static final int ETHERNET_HEADER_LENGTH = 14; // taille d'une en-tête ethernet

    private final EthernetHeader header;
    private final EthernetData data;

    public EthernetFrame(byte[] bytes) {
        // erreur : la taille de la trame est inférieure à celle de l'en-tête
        if (bytes.length < ETHERNET_HEADER_LENGTH)
            throw new IllegalArgumentException("L'en-tete ethernet n'est pas complete !");

        // on récupère l'en-tête
        header = new EthernetHeader(bytes);

        // on récupère les données
        data = new EthernetData(bytes);
    }

    /**
     * Récupérer l'en-tête de la trame ethernet
     *
     * @return en-tête de la trame ethernet
     */
    public EthernetHeader getHeader() {
        return header;
    }

    /**
     * Récupérer les données de la trame ethernet
     *
     * @return les donnéesEthernetHeader.java de la trame
     */
    public EthernetData getData() {
        return data;
    }

    @Override
    public String toString() {
        String sb = "En-tetes [ " +
                header +
                " ]\n" +
                "Donnees [ " +
                data +
                " ]";
        return sb;
    }
}
