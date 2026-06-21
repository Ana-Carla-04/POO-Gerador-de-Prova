module POO {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires mysql.connector.j;

    exports br.edu.ufersa.aplicativo.application;

    opens br.edu.ufersa.aplicativo.controlles to javafx.fxml;
}