module com.ryan.yowg {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ryan.yowg to javafx.fxml;
    exports com.ryan.yowg;
}