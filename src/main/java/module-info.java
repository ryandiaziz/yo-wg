module com.ryan.yowg {
    requires javafx.controls;
    requires javafx.fxml;
    requires typesafe.config;
    requires java.sql;


    opens com.ryan.yowg to javafx.fxml;
    exports com.ryan.yowg;
    exports com.ryan.yowg.controllers;
    opens com.ryan.yowg.controllers to javafx.fxml;
    exports com.ryan.yowg.controllers.access;
    opens com.ryan.yowg.controllers.access to javafx.fxml;
    exports com.ryan.yowg.controllers.resource;
    opens com.ryan.yowg.controllers.resource to javafx.fxml;
    exports com.ryan.yowg.controllers.wireguard;
    opens com.ryan.yowg.controllers.wireguard to javafx.fxml;
}