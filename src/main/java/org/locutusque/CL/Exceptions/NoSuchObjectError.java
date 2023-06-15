package org.locutusque.CL.Exceptions;

import java.util.Arrays;

/**
 * This exception is thrown when an Object is either null, undefined, or doesn't exist.
 */
public class NoSuchObjectError extends CLException {
    private final String objectName;

    /**
     * Constructs a new NoSuchObjectError with the given object name.
     *
     * @param objectName the name of the object that doesn't exist
     */
    public NoSuchObjectError(String objectName) {
        super(false, "NoSuchObjectError", objectName);
        this.objectName = objectName;
    }

    /**
     * Constructs a new NoSuchObjectError with the given object name and custom parameters.
     *
     * @param objectName        the name of the object that doesn't exist
     * @param customParameters  additional custom parameters describing the error
     */
    public NoSuchObjectError(String objectName, String... customParameters) {
        super(true, "NoSuchObjectError", objectName, Arrays.toString(customParameters));
        this.objectName = objectName;
    }
}
