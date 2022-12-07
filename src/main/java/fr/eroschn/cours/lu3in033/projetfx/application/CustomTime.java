package fr.eroschn.cours.lu3in033.projetfx.application;

public class CustomTime {

    private long time;

    public CustomTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "" + time / 1000 % 60 + "s";
    }
}
