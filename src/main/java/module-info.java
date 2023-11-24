module slapp.editor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.gluonhq.richtextarea;
    requires com.gluonhq.emoji;
    requires java.logging;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.lineawesome;



    opens slapp.editor to javafx.fxml;
    exports slapp.editor;
    exports slapp.editor.decorated_rta;
    opens slapp.editor.decorated_rta to javafx.fxml;
    exports slapp.editor.tests;
    opens slapp.editor.tests to javafx.fxml;
}