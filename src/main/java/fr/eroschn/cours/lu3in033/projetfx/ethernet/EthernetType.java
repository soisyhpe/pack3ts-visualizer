package fr.eroschn.cours.lu3in033.projetfx.ethernet;//package fr.eroschn.cours.lu3in033.projetfx.ethernet;

public enum EthernetType {

    IPV4(0x0800, "IPv4"),
    IPV6(0x86dd, "IPv6"),
    ARP(0x0806, "ARP");

    private final long value;
    private final String name;

    EthernetType(long value, String name) {
        this.value = value;
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * Permet de convertir un short (2 octets) en un type ethernet
     *
     * @param value valeur en short (2 octets)
     * @return le type ethernet correspondant
     */
    public static EthernetType fromValue(long value) {
        for (EthernetType ethernetType : values()) {
            if (ethernetType.value == value) return ethernetType;
        }
        throw new IllegalArgumentException("Le type ethernet fourni n'existe pas : " + value);
    }

    @Override
    public String toString() {
        return name;
    }
}
