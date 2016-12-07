package com.cmax.bodysheild.http.rxexception;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class NetworkConnectionException extends Exception {
    public NetworkConnectionException( ) {
        super();
    }

    public NetworkConnectionException(final String message) {
        super(message);
    }

    public NetworkConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NetworkConnectionException(final Throwable cause) {
        super(cause);
    }
}
