package org.locutusque.CL.Exceptions;

import org.jetbrains.annotations.Nullable;

public class SyntaxError extends CLException {
    private final String desc;
    private final String pattern;
    private final int index;

    public SyntaxError(String desc, @Nullable String regex, int index) {
        super(true, "SyntaxError", buildErrorMessage(desc, regex, index));
        this.desc = desc;
        this.pattern = regex;
        this.index = index;
    }

    private static String buildErrorMessage(String desc, String regex, int index) {
        StringBuilder sb = new StringBuilder();
        if (index >= 0) {
            sb.append(" near index ");
            sb.append(index);
        }
        sb.append(System.lineSeparator());
        if (regex != null) {
            sb.append(regex);
        }
        if (index >= 0 && regex != null && index < regex.length()) {
            sb.append(System.lineSeparator());
            for (int i = 0; i < index; i++) {
                sb.append((regex.charAt(i) == '\t') ? '\t' : ' ');
            }
            sb.append('^');
        }
        sb.append(System.lineSeparator());
        sb.append("If your using the \"main\" keyword make sure your leaving a space between \"main\" and the parameters like this: ");
        sb.append(System.lineSeparator());
        sb.append("main (str[] args) {} instead of main(str[] args) {} \n This is a common mistake so make sure you have a space! ");
        return sb.toString();
    }
    public String getDesc() {
        return this.desc;
    }
    public String getPattern() {
        return this.pattern;
    }
    public Integer getIndex() {
        return this.index;
    }

}
