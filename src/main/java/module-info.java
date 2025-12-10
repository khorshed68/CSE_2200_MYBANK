//module com.khorshed.mybank.mybankapp {
//    requires javafx.controls;
//    requires javafx.fxml;
//   requires javafx.web;
////
////    requires org.controlsfx.controls;
////    requires com.dlsc.formsfx;
////    requires net.synedra.validatorfx;
////    requires org.kordamp.ikonli.javafx;
////    requires org.kordamp.bootstrapfx.core;
////    requires eu.hansolo.tilesfx;
////    requires com.almasb.fxgl.all;
//    requires java.sql;
//
//    opens com.khorshed.mybank.mybankapp to javafx.fxml;
//    exports com.khorshed.mybank.mybankapp;
//}

//module com.khorshed.mybank.mybankapp {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires java.sql;
//
//    // SQLite JDBC
//    requires org.xerial.sqlitejdbc;
//
//    // Allow JavaFX to access packages via reflection (FXML loading)
//    opens com.khorshed.mybank.mybankapp to javafx.fxml, javafx.graphics;
//    opens com.khorshed.mybank.mybankapp.controllers to javafx.fxml;
//
//    // Export main package
//    exports com.khorshed.mybank.mybankapp;
//}
module com.khorshed.mybank.mybankapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java. sql;

    // SQLite JDBC
    requires org.xerial.sqlitejdbc;

    // Allow JavaFX to access packages via reflection (FXML loading)
    opens com.khorshed.mybank. mybankapp to javafx.fxml, javafx.graphics;
    opens com. khorshed.mybank.mybankapp.controllers to javafx.fxml;

    // Allow JavaFX TableView to access model properties via reflection
    opens com.khorshed.mybank.mybankapp.models to javafx.base;

    // Export main package
    exports com.khorshed.mybank.mybankapp;
}