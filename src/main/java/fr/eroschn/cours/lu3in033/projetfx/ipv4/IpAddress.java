package fr.eroschn.cours.lu3in033.projetfx.ipv4;//package fr.eroschn.cours.lu3in033.projetfx.ipv4;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpAddress {

    private final byte[] bytes;
    private final String ipAddress;

    public IpAddress(byte[] bytes) {
        if (bytes.length != 4) throw new RuntimeException("Impossible de parser cette ip " + bytes);
        this.bytes = bytes;

        StringBuilder sb = new StringBuilder();
        int i;
        for (i=0; i<bytes.length-1; i++) {
            sb.append(Byte.toUnsignedInt(bytes[i]) + ".");
        }
        sb.append(Byte.toUnsignedInt(bytes[i]));
        ipAddress = sb.toString();
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String toString() {
        return ipAddress;
    }

}