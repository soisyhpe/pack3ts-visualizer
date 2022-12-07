package fr.eroschn.cours.lu3in033.projetfx.ipv4;//package fr.eroschn.cours.lu3in033.projetfx.ipv4;

public class IPv4Data {

    private final byte[] bytes;

    public IPv4Data(byte[] bytes) {
        this.bytes = bytes;
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
