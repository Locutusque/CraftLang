package org.locutusque.CL.Exceptions;

public class IllegalParameterError extends CLException {
    int parameter;
    String message;
    public IllegalParameterError(String message, int parameter) {
        super(true, "IllegalParameterError", message, parameter);
        this.parameter = parameter;
        this.message = message;
    }
    public int getParameter() {
        return this.parameter;
    }
}
