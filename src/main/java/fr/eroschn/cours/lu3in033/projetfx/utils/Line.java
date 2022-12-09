package fr.eroschn.cours.lu3in033.projetfx.utils;

import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddress;

public class Line {

    private static int cpt = 0;
    private final long time;
    private final IpAddress sourceAddress;
    private final int sourcePort;
    private final String sourcePortString;
    private final IpAddress destinationAddress;
    private final int destinationPort;
    private final String destinationPortString;
    private final String comment;
    private final String protocols;
    private final int id;

    public Line(long time, IpAddress sourceAddress, int sourcePort, IpAddress destinationAddress, int destinationPort, String comment, String tcpFlags, String other, String protocols) {
        id = cpt++;
        this.time = time;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        sourcePortString = tcpFlags + "\n" + sourcePort + " --------------------";
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
        destinationPortString = other + "\n" + "--------------------> " + destinationPort;
        this.comment = comment;
        this.protocols = protocols;
    }

    public long getTime() {
        return time;
    }

    public String getTimeString() {
        return String.format("%d", (time / 100000)) + "ms";
    }

    public IpAddress getSourceAddress() {
        return sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public String getSourcePortString() {
        return sourcePortString;
    }

    public IpAddress getDestinationAddress() {
        return destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public String getDestinationPortString() {
        return destinationPortString;
    }

    public String getProtocols() {
        return protocols;
    }

    public String getComment() {
        return comment;
    }
}
