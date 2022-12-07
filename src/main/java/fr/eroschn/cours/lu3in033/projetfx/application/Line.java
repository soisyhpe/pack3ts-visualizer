package fr.eroschn.cours.lu3in033.projetfx.application;

import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddress;

public class Line {

    private final long time;
    private final IpAddress sourceAddress;
    private final int sourcePort;
    private final IpAddress destinationAddress;
    private final int destinationPort;
    private final String comment;

    public Line(long time, IpAddress sourceAddress, int sourcePort, IpAddress destinationAddress, int destinationPort, String comment) {
        this.time = time;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
        this.comment = comment;
    }

    public long getTime() {
        return time;
    }

    public IpAddress getSourceAddress() {
        return sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public IpAddress getDestinationAddress() {
        return destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public String getComment() {
        return comment;
    }
}
