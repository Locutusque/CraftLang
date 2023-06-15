package org.locutusque.CL.Exceptions;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * The base class for all CraftLang exceptions.
 */
public class CLException extends Throwable {
    private final Object[] parameters;
    private Throwable cause;
    private String exception;
    private final boolean printObject;

    /**
     * Constructs a new CLException with the specified parameters.
     *
     * @param printObject Indicates whether to print the object on throw.
     * @param additionalInfo  The parameters associated with the exception.
     */
    public CLException(boolean printObject, String exception, @Nullable Object... additionalInfo) {
        this.parameters = additionalInfo;
        this.printObject = printObject;
        this.exception = exception;
    }

    /**
     * Constructs a new CLException with the specified parameters and cause.
     *
     * @param printObject Indicates whether to print the object on throw.
     * @param cause       The cause of the exception.
     * @param additionalInfo  The parameters associated with the exception.
     */
    public CLException(boolean printObject, Throwable cause, String exception, @Nullable Object... additionalInfo) {
        this.printObject = printObject;
        this.parameters = additionalInfo;
        this.cause = cause;
        this.exception =  exception;
    }

    /**
     * Returns the parameters associated with the exception.
     *
     * @return The parameters associated with the exception.
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Returns the cause of the exception.
     *
     * @return The cause of the exception.
     */
    public Throwable getCause() {
        return cause;
    }

    /**
     *
     * @return The class name of the exception
     */
    public String getException() {
        return exception;
    }

    /**
     * Returns the detail message string of this exception.
     * If the "printObject" flag is set, it includes the string representation of the parameters.
     *
     * @return The detail message string of this exception.
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (Object param : parameters) {
            if (param instanceof String) {
                sb.append(param).append("CraftLang " + exception + " >> " + param);
            }
            else if (param instanceof Integer || param instanceof Float || param instanceof Long) {
                sb.append("\n index ").append(param);
            }
            else if (printObject) {
                sb.append(Arrays.toString(parameters));
            }
        }
        return sb.toString().trim();
    }
}
