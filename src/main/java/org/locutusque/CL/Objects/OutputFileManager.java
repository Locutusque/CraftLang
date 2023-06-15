package org.locutusque.CL.Objects;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.File;
import java.io.IOException;

public class OutputFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final File outputDir;

    public OutputFileManager(StandardJavaFileManager fileManager, File outputDir) {
        super(fileManager);
        this.outputDir = outputDir;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        File outputFile = new File(outputDir, className + kind.extension);
        return new JavaSourceFromOutputFile(outputFile.toURI(), kind);
    }
}
