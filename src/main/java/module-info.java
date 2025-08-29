module org.demo.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.bootstrapicons;
    requires java.sql;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires tess4j;
    requires org.apache.commons.io;
    requires jbcrypt;


    opens org.demo.demo to javafx.fxml;
    opens org.demo.demo.entities to javafx.base;
    exports org.demo.demo;
    exports org.demo.demo.controller;
    opens org.demo.demo.controller to javafx.fxml;
    exports org.demo.demo.config;
    opens org.demo.demo.config to javafx.fxml;
    exports org.demo.demo.session;
    opens org.demo.demo.session to javafx.fxml;
    exports org.demo.demo.services;
    opens org.demo.demo.services to javafx.fxml;
}