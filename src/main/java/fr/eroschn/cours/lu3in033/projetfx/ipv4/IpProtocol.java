package fr.eroschn.cours.lu3in033.projetfx.ipv4;//package fr.eroschn.cours.lu3in033.projetfx.ipv4;

public enum IpProtocol {

    ICMP(0x01, "ICMP"),
    IGMP(0x02, "IGMP"),
    TCP(0x06, "TCP"),
    UDP(0x11, "UDP");

    private final int value;
    private final String name;

    IpProtocol(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static IpProtocol fromValue(int value) {
        for (IpProtocol p : values()) {
            if (p.value == value) return p;
        }
        throw new IllegalArgumentException("Impossible de retrouver ce protocole ip : " + value);
    }

    @Override
    public String toString() {
        return name;
    }
}
