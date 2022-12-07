package fr.eroschn.cours.lu3in033.projetfx.tcp;//package fr.eroschn.cours.lu3in033.projetfx.tcp;

import fr.eroschn.cours.lu3in033.projetfx.utils.ByteUtils;

import java.util.Arrays;

public class TcpHeader {

    private final int sourcePort;
    private final int destinationPort;
    private final int sequenceNumber;
    private final int acknowledgementNumber;
    private final int dataOffset;
    private final int reserved;

    // flags
    private final int urg;
    private final int ack;
    private final int psh;
    private final int rst;
    private final int syn;
    private final int fin;

    private final int window;
    private final int checksum;
    private final int urgentPointer;
    private String options_ = "non";
    private final byte[] options; // si dataOffset > 5

    public TcpHeader(byte[] bytes) {

        // source port
        sourcePort = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 0, 2));

        // destination port
        destinationPort = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 2, 4));

        // sequence number
        sequenceNumber = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 4, 8));

        // acknowledgement number
        acknowledgementNumber = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 8, 12));

        // data offset
        dataOffset = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 12, 13)) / 16;

        // reserved
        reserved = (ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 12, 13)) % 16) + (ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 13, 14)) & 0xC0);

        //flags
        int flags = Byte.toUnsignedInt(bytes[13]);
        //ns = false;
        //cwr = false;
        //ece = false;
        fin = flags % 2 == 1 ? 1 : 0;
        flags = flags / 2;
        syn = flags % 2 == 1 ? 1 : 0;
        flags = flags / 2;
        rst = flags % 2 == 1 ? 1 : 0;
        flags = flags / 2;
        psh = flags % 2 == 1 ? 1 : 0;
        flags = flags / 2;
        ack = flags % 2 == 1 ? 1 : 0;
        flags = flags / 2;
        urg = flags % 2 == 1 ? 1 : 0;
        flags = flags / 2;

        //window
        window = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 14, 16));

        //checksum
        checksum = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 16, 18));

        //urgent pointer
        urgentPointer = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 18, 20));

        //options
        options = new byte[0];
        if (dataOffset > 5) {
            options_ = "oui";
        }
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public int getReserved() {
        return reserved;
    }

    public int isUrg() {
        return urg;
    }

    public int isAck() {
        return ack;
    }

    public int isPsh() {
        return psh;
    }

    public int isRst() {
        return rst;
    }

    public int isSyn() {
        return syn;
    }

    public int isFin() {
        return fin;
    }

    public int getWindow() {
        return window;
    }

    public int getChecksum() {
        return checksum;
    }

    public int getUrgentPointer() {
        return urgentPointer;
    }

    public byte[] getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return "Source Port : " + sourcePort
                + ", Destination Port : " + destinationPort
                + ", Sequence Number : " + sequenceNumber
                + ", Acknowledgement Number : " + acknowledgementNumber
                + ", THL : " + dataOffset
                + ", Reserved : " + reserved
                + ", URG : " + urg
                + ", ACK : " + ack
                + ", PSH : " + psh
                + ", RST : " + rst
                + ", SYN : " + syn
                + ", FIN : " + fin
                + ", Window : " + window
                + ", Checksum : 0x" + String.format("%04X", checksum)
                + ", Urgent Pointer : " + urgentPointer
                + ", Options : " + options_;
    }
}
