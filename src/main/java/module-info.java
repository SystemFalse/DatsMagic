module dats_magic {
    requires com.google.gson;
    requires javafx.graphics;
    requires javafx.controls;
    requires java.logging;
    requires java.desktop;

    exports org.system_false.dats_magic;
    exports org.system_false.dats_magic.json;

    opens org.system_false.dats_magic.json to com.google.gson;
}