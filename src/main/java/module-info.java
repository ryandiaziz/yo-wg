module com.ryan.yowg {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires transitive javafx.web;
    requires java.sql;
    requires atlantafx.base;
    requires com.kodedu.terminalfx;
    requires pty4j;


    opens com.ryan.yowg to javafx.fxml;
    exports com.ryan.yowg;
    exports com.ryan.yowg.models;
    opens com.ryan.yowg.models to javafx.fxml;
    exports com.ryan.yowg.controllers;
    opens com.ryan.yowg.controllers to javafx.fxml;
    exports com.ryan.yowg.controllers.access;
    opens com.ryan.yowg.controllers.access to javafx.fxml;
    exports com.ryan.yowg.controllers.resource;
    opens com.ryan.yowg.controllers.resource to javafx.fxml;
    exports com.ryan.yowg.controllers.wireguard;
    opens com.ryan.yowg.controllers.wireguard to javafx.fxml;
    exports com.ryan.yowg.services;
    exports com.ryan.yowg.components;
}