package fr.eroschn.cours.lu3in033.projetfx.application;

import fr.eroschn.cours.lu3in033.projetfx.application.windows.HomeWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class DecoderApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        new HomeWindow(stage, getHostServices());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
