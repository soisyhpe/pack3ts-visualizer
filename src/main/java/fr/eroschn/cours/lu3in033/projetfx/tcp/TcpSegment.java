package fr.eroschn.cours.lu3in033.projetfx.tcp;//package fr.eroschn.cours.lu3in033.projetfx.tcp;

import java.util.Arrays;

public class TcpSegment {

    private final TcpHeader header;
    private final TcpData data;

    public TcpSegment(byte[] bytes) {

        // en-tête ip
        header = new TcpHeader(bytes);

        // données ip
        data = new TcpData(Arrays.copyOfRange(bytes, header.getDataOffset() * 4, bytes.length));
    }

    public TcpHeader getHeader() {
        return header;
    }

    public TcpData getData() {
        return data;
    }

    @Override
    public String toString() {
        String sb = "En-tetes [ " +
                header +
                " ]\n" +
                "Donnees [ " +
                data +
                " ]";
        return sb;
    }
}