package fr.eroschn.cours.lu3in033.projetfx.ethernet;//package fr.eroschn.cours.lu3in033.projetfx.ethernet;

import fr.eroschn.cours.lu3in033.projetfx.utils.ByteUtils;

import java.util.Arrays;

public class EthernetHeader {

    private final MacAddress destination;
    private final MacAddress source;
    private final EthernetType type;

    public EthernetHeader(byte[] bytes) {
        // on récupère l'adress mac destination
        destination = new MacAddress(Arrays.copyOfRange(bytes, 0, 6));

        // on récupère l'adresse mac source
        source = new MacAddress(Arrays.copyOfRange(bytes, 6, 12));

        // on récupère le ethertype
        type = EthernetType.fromValue(ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 12, 14)));
    }

    /**
     * Récupérer l'adresse mac de destination
     *
     * @return adresse mac de destination
     */
    public MacAddress getDestination() {
        return destination;
    }

    /**
     * Récupérer l'adresse mac source
     *
     * @return adresse mac source de destination
     */
    public MacAddress getSource() {
        return source;
    }

    /**
     * Récupérer le type ethernet
     *
     * @return l'ethertype correspondant
     */
    public EthernetType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MAC destination : " + destination + ", MAC source : " + source + ", Type : " + type;
    }

}
