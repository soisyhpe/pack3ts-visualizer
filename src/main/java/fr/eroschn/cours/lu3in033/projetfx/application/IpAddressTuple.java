package fr.eroschn.cours.lu3in033.projetfx.application;

import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetFrame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IPv4Frame;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddress;

import java.util.List;

public class IpAddressTuple {

    private final IpAddress firstAddress;
    private final IpAddress secondAddress;

    public IpAddressTuple(IpAddress firstAddress, IpAddress secondAddress) {
        this.firstAddress = firstAddress;
        this.secondAddress = secondAddress;
    }

    //retourne true si la liste de paires d'ip contient déjà un tuple similaire à tuple
    public static boolean containsSimilar(List<IpAddressTuple> list, IpAddressTuple tuple) {
        for (IpAddressTuple t : list) {
            if (t.isSimilarTo(tuple)) {
                return true;
            }
        }
        return false;
    }

    public IpAddress getFirstAddress() {
        return firstAddress;
    }

    public IpAddress getSecondAddress() {
        return secondAddress;
    }

    @Override
    public String toString() {
        return "[ " + firstAddress.toString() + ", " + secondAddress.toString() + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof IpAddressTuple a)) return false;
        return a.getFirstAddress().equals(firstAddress) && a.getSecondAddress().equals(secondAddress);
    }

    //retourne true si les deux tuples sont similaires au sens de l'égalité ensembliste
    public boolean isSimilarTo(IpAddressTuple tuple) {
        return this.equals(tuple) || this.equals(new IpAddressTuple(tuple.getSecondAddress(), tuple.getFirstAddress()));
    }

    //retourne true si la trame a des adresses ip qui correspondent à celle de this (dans un sens ou dans l'autre)
    public boolean belongsTo(EthernetFrame frame) {
        IPv4Frame fr = new IPv4Frame(frame.getData().getBytes());
        IpAddressTuple tuple = new IpAddressTuple(fr.getHeader().getSourceAddress(), fr.getHeader().getDestinationAddress());

        return this.isSimilarTo(tuple);
    }
}
