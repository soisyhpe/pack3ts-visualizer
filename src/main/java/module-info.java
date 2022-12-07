module fr.eroschn.cours.lu3in033.projetfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires net.sourceforge.plantuml;


    exports fr.eroschn.cours.lu3in033.projetfx;
    opens fr.eroschn.cours.lu3in033.projetfx to javafx.fxml;
    exports fr.eroschn.cours.lu3in033.projetfx.ethernet;
    opens fr.eroschn.cours.lu3in033.projetfx.ethernet to javafx.fxml;
    exports fr.eroschn.cours.lu3in033.projetfx.application;
    opens fr.eroschn.cours.lu3in033.projetfx.application to javafx.fxml;
    exports fr.eroschn.cours.lu3in033.projetfx.application.windows;
    opens fr.eroschn.cours.lu3in033.projetfx.application.windows to javafx.fxml;
}