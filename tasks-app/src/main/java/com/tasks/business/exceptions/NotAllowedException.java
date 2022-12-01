package com.tasks.business.exceptions;

public class NotAllowedException extends Exception {

    private static final long serialVersionUID = -4208426212843868045L;

    private final Long id;
    private final String type;

    public NotAllowedException(Long id, String type, String message) {
        super(message);
        this.id = id;
        this.type = type;
    }

    public Object getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
