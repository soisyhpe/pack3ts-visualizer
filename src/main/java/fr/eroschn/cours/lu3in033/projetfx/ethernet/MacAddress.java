package fr.eroschn.cours.lu3in033.projetfx.ethernet;//package fr.eroschn.cours.lu3in033.projetfx.ethernet;

public class MacAddress {

    private final byte[] macAddress;

    public MacAddress(byte[] macAddress) {
        // erreur : si les octets en entrée n'ont pas la bonne taille
        if (macAddress.length != 6)
            throw new IllegalArgumentException("Impossible de convertir les octets suivants en adresse MAC : " + macAddress);

        this.macAddress = macAddress;
    }

    public byte[] getMacAddress() {
        return macAddress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < macAddress.length - 1; i++) {
            sb.append(Integer.toHexString(Byte.toUnsignedInt(macAddress[i])));
            sb.append(":");
        }
        sb.append(Integer.toHexString(Byte.toUnsignedInt(macAddress[i])));
        return sb.toString();
    }
}
