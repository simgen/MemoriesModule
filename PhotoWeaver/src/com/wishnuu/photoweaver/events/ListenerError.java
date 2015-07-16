package com.wishnuu.photoweaver.events;

import android.util.Log;

/**
 * Wrapper class for handling errors via the listener
 */
public class ListenerError extends Throwable {

    private static final String CLASS_NAME = "ListenerError";

    private static final long serialVersionUID = 1L;
    private final Exception innerException;

    /**
     * Constructor
     *
     * @param message
     *            User readable message for the error
     * @param e
     *            Inner exception that may be used for further debugging
     */
    public ListenerError(String message, Exception e) {
        super(message);
        this.innerException = e;
        Log.d(CLASS_NAME, e.toString());
    }

    /**
     * Returns the inner exception
     *
     * @return Inner exception
     */
    public Exception getInnerException() {
        return innerException;
    }

}
