module POO {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    exports br.edu.ufersa.aplicativo;
    opens br.edu.ufersa.aplicativo.controlles to javafx.fxml;
}