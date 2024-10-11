module dats_magic {
    requires com.google.gson;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires jdk.localedata;

    exports org.system_false.dats_magic;
    exports org.system_false.dats_magic.json;

    opens org.system_false.dats_magic.json to com.google.gson;
    opens org.system_false.dats_magic to javafx.fxml;
}