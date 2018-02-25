/*
 * Copyright (c) 2015. For Intelligent Group.
 */

package com.dmsoft.firefly.sdk.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Application Exception.
 * <p>
 * Created by Peter.Li on 2015/10/9.
 */
public class ApplicationException extends RuntimeException {

    /**
     * Constructor.
     */
    public ApplicationException() {
        this(null, (Throwable) null);
    }

    /**
     * Constructor.
     *
     * @param message String the exception message
     */
    public ApplicationException(String message) {
        this(message, (Throwable) null);
    }

    /**
     * Constructor.
     *
     * @param cause Throwable the exception cause
     */
    public ApplicationException(Throwable cause) {
        this(null, cause);
    }

    /**
     * Constructor.
     *
     * @param message String the exception message
     * @param cause   Throwable the exception cause
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Translate the CheckedException to UncheckedException.
     *
     * @param ex Throwable the exception cause
     * @return RuntimeException
     */
    public static RuntimeException unchecked(Throwable ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        } else {
            return new RuntimeException(ex);
        }
    }

    /**
     * Translate the ErrorStack to String.
     *
     * @param ex Throwable the exception cause
     * @return Throwable
     */
    public static String getStackTraceAsString(Throwable ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * Get the detail message from local exception and struck.
     *
     * @param ex Throwable the exception cause
     * @return String
     */
    public static String getErrorMessageWithNestedException(Throwable ex) {
        Throwable nestedException = ex.getCause();
        return new StringBuilder().append(ex.getMessage()).append(" nested exception is ")
                .append(nestedException.getClass().getName()).append(":").append(nestedException.getMessage())
                .toString();
    }

    /**
     * Get the root cause.
     *
     * @param ex Throwable the exception cause
     * @return Throwable
     */
    public static Throwable getRootCause(Throwable ex) {
        Throwable cause;
        while ((cause = ex.getCause()) != null) {
            ex = cause;
        }
        return ex;
    }

    /**
     * Judge the cause by.
     *
     * @param ex                    Throwable the exception cause
     * @param causeExceptionClasses collection of exception cause classes.
     * @return boolean
     */
    public static boolean isCausedBy(Exception ex, Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex;
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (causeClass.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }
}
