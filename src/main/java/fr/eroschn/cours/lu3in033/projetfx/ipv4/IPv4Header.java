package fr.eroschn.cours.lu3in033.projetfx.ipv4;//package fr.eroschn.cours.lu3in033.projetfx.ipv4;

import fr.eroschn.cours.lu3in033.projetfx.utils.ByteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IPv4Header {

    private final int version;
    private final int internetHeaderLength;
    private final int typeOfService;
    private final int totalLength;
    private final int identifier;
    private final boolean reserved;
    private final boolean dontFragment;
    private final boolean moreFragment;
    private final int fragmentOffset;
    private final int timeToLive;
    private final IpProtocol protocol;
    private final int headerChecksum;
    private final IpAddress sourceAddress;
    private final IpAddress destinationAddress;
    private String options_ = "non";
    private final List<IpOption> options; // si IHL > 5

    public IPv4Header(byte[] bytes) {

        if (bytes.length < 20) {
            throw new IllegalArgumentException("L'en-tête IPv4 est incomplète (< 20 octets)");
        }

        // version
        version = ((int) bytes[0]) / 16;

        // internet header length
        internetHeaderLength = ((int) bytes[0]) % 16;

        // type of service
        typeOfService = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 1, 2));

        // total length
        totalLength = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 2, 4));

        // identifier
        identifier = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 4, 6));

        // flags
        int flags = Byte.toUnsignedInt(bytes[6]);
        reserved = flags % 64 == 1;
        flags = flags / 64;
        dontFragment = flags % 2 == 1;
        flags = flags / 2;
        moreFragment = flags % 2 == 1;

        //fragment offset
        fragmentOffset = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 6, 8)) & 0x1FFF;

        // time to live
        timeToLive = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 8, 9));

        // protocol
        protocol = IpProtocol.fromValue(ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 9, 10)));

        // header checksum
        headerChecksum = ByteUtils.byteArrayToInt(Arrays.copyOfRange(bytes, 10, 12));

        // ip source
        sourceAddress = new IpAddress(Arrays.copyOfRange(bytes, 12, 16));

        // ip destination
        destinationAddress = new IpAddress(Arrays.copyOfRange(bytes, 16, 20));

        // todo : options
        options = new ArrayList<>();

        // on a des options
        if (internetHeaderLength > 5) {
            options_ = "oui";
        }
    }

    public int getVersion() {
        return version;
    }

    public int getInternetHeaderLength() {
        return internetHeaderLength;
    }

    public int getTypeOfService() {
        return typeOfService;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public int getIdentifier() {
        return identifier;
    }

    public boolean isReserved() {
        return reserved;
    }

    public boolean isDontFragment() {
        return dontFragment;
    }

    public boolean isMoreFragment() {
        return moreFragment;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public IpProtocol getProtocol() {
        return protocol;
    }

    public int getHeaderChecksum() {
        return headerChecksum;
    }

    public IpAddress getSourceAddress() {
        return sourceAddress;
    }

    public IpAddress getDestinationAddress() {
        return destinationAddress;
    }

    public List<IpOption> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return "Version : " + version
                + ", IHL : " + internetHeaderLength
                + ", ToS : " + typeOfService
                + ", Total Length : " + totalLength
                + ", Identifier : 0x" + String.format("%04X", identifier)
                + ", Reserved : " + reserved
                + ", DF : " + dontFragment
                + ", MF : " + moreFragment
                + ", Fragment offset : " + fragmentOffset
                + ", Time To Live : " + timeToLive
                + ", Protocol : " + protocol.getName() + " (" + protocol.getValue() + ")"
                + ", Header Checksum : 0x" + String.format("%04X", headerChecksum)
                + ", Source Address : " + sourceAddress
                + ", Destination Address : " + destinationAddress
                + ", Options : " + options_;
    }
}
