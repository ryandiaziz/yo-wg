module com.ryan.yowg {
    requires javafx.controls;
    requires javafx.fxml;
    requires typesafe.config;


    opens com.ryan.yowg to javafx.fxml;
    exports com.ryan.yowg;
    exports com.ryan.yowg.controllers;
    opens com.ryan.yowg.controllers to javafx.fxml;
}