package org.system_false.dats_magic;

import java.io.IOException;

public class ErrorCodeException extends IOException {
    private final int code;

    public ErrorCodeException(int code) {
        super("server sent code " + code);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
