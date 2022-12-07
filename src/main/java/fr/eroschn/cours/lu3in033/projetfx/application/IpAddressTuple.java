package fr.eroschn.cours.lu3in033.projetfx.application;

import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetFrame;
import fr.eroschn.cours.lu3in033.projetfx.ethernet.EthernetType;
import fr.eroschn.cours.lu3in033.projetfx.ipv4.IpAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IpAddressTuple {

    private final IpAddress firstAddress;
    private final IpAddress secondAddress;
    private final List<EthernetFrame> frames = new ArrayList<>();

    public IpAddressTuple(IpAddress firstAddress, IpAddress secondAddress) {
        this.firstAddress = firstAddress;
        this.secondAddress = secondAddress;
    }

    public IpAddress getFirstAddress() {
        return firstAddress;
    }

    public IpAddress getSecondAddress() {
        return secondAddress;
    }

    public List<EthernetFrame> getFrames() {
        return frames;
    }

    @Override
    public String toString() {
        return "[ " + firstAddress.toString() + ", " + secondAddress.toString() + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof IpAddressTuple)) return false;
        IpAddressTuple a = (IpAddressTuple) obj;
        return a.getFirstAddress().equals(firstAddress) && a.getSecondAddress().equals(secondAddress);
    }
}
