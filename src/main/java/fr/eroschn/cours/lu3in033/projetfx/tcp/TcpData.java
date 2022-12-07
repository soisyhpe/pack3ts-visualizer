package fr.eroschn.cours.lu3in033.projetfx.tcp;//package fr.eroschn.cours.lu3in033.projetfx.tcp;

public class TcpData {

    private final byte[] bytes;

    public TcpData(byte[] bytes) {
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