package fr.eroschn.cours.lu3in033.projetfx.ipv4;//package fr.eroschn.cours.lu3in033.projetfx.ipv4;

import java.util.Arrays;

public class IPv4Frame {

    private final IPv4Header header;
    private final IPv4Data data;

    public IPv4Frame(byte[] bytes) {

        // en-tête ip
        header = new IPv4Header(bytes);

        // données ip
        data = new IPv4Data(Arrays.copyOfRange(bytes, header.getInternetHeaderLength() * 4, bytes.length));
    }

    public IPv4Header getHeader() {
        return header;
    }

    public IPv4Data getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("En-tetes [ ");
        sb.append(header);
        sb.append(" ]\n");
        sb.append("Donnees [ ");
        sb.append(data);
        sb.append(" ]");
        return sb.toString();
    }
}
