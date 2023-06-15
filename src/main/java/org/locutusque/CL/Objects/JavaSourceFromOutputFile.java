package org.locutusque.CL.Objects;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

class JavaSourceFromOutputFile extends SimpleJavaFileObject {
    private final URI uri;

    protected JavaSourceFromOutputFile(URI uri, Kind kind) {
        super(uri, kind);
        this.uri = uri;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return Files.newOutputStream(Paths.get(uri));
    }
}
