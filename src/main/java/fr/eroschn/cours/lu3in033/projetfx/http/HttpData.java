package fr.eroschn.cours.lu3in033.projetfx.http;//package fr.eroschn.cours.lu3in033.projetfx.http;

public class HttpData {

    private final byte[] bytes;

    public HttpData(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append((char)b);
        }
        return sb.toString();
    }
}
