package fr.eroschn.cours.lu3in033.projetfx;

import fr.eroschn.cours.lu3in033.projetfx.application.DecoderWindow;

import java.io.File;
import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        try {
            DecoderWindow.decodeFile(new File("data/frame.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
