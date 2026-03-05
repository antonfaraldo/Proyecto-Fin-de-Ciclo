module MineManagerFX {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    // 1. Abrimos los controladores para que JavaFX pueda inyectar los @FXML
    // Esto es vital para que funcionen tus archivos en /fxml/
    opens dam.proyectofinal.afm.controller to javafx.fxml;

    // 2. Abrimos los modelos para que las TableView (Ranking) puedan leer los datos
    opens dam.proyectofinal.afm.model to javafx.base;

    // NOTA: He borrado "opens dam.proyectofinal.view" porque al estar
    // los FXML en resources/fxml, no se consideran un paquete de código.

    exports dam.proyectofinal.afm;
    exports dam.proyectofinal.afm.controller;
    exports dam.proyectofinal.afm.model;
    exports dam.proyectofinal.afm.util;
    exports dam.proyectofinal.afm.dao;
}