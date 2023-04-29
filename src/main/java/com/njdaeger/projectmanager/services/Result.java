package com.njdaeger.projectmanager.services;

public interface Result<T> {

    static <C> Result<C> of(C result, String message, boolean wasSuccessful) {
        return new Result<>() {
            @Override
            public C getResult() {
                return result;
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public boolean wasSuccess() {
                return wasSuccessful;
            }
        };
    }

    /**
     * Creates a bad result
     * @param result The failed result
     * @param message The resulting message
     * @param <C> The type of result being returned
     * @return The result
     */
    static <C> Result<C> bad(C result, String message) {
        return of(result, message, false);
    }

    /**
     * Creates a bad result
     * @param result The failed result
     * @param <C> The type of result being returned
     * @return The result
     */
    static <C> Result<C> bad(C result) {
        return of(result, null, false);
    }

    /**
     * Creates a bad result
     * @param <C> The type of result being returned
     * @return The result
     */
    static <C> Result<C> bad() {
        return of(null, null, false);
    }

    /**
     * Creates a good result
     * @param result The result
     * @param message The resulting message
     * @param <C> The type of result being returned
     * @return The result
     */
    static <C> Result<C> good(C result, String message) {
        return of(result, message, true);
    }

    /**
     * Creates a good result
     * @param result The result
     * @param <C> The type of result being returned
     * @return The result
     */
    static <C> Result<C> good(C result) {
        return of(result, null, true);
    }

    /**
     * Creates a good result
     * @param <C> The type of result being returned
     * @return The result
     */
    static <C> Result<C> good() {
        return of(null, null, true);
    }

    /**
     * Get the result from this operation
     * @return The result. Can be null
     */
    T getResult();

    /**
     * Get the response message from ths operation
     * @return The operation response message. Can be null
     */
    String getMessage();

    /**
     * If this was a successful operation
     * @return True if successful, false otherwise
     */
    boolean wasSuccess();

}
