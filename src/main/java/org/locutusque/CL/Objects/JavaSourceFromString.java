package org.locutusque.CL.Objects;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;

public class JavaSourceFromString extends SimpleJavaFileObject {
    private final String code;

    public JavaSourceFromString(String name, String code, Kind kind) {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
