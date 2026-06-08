package ru.itmo.prog.lab6.common.network;

import ru.itmo.prog.lab6.common.model.HumanBeing;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final HumanBeing humanBeing;

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.humanBeing = null;
    }

    public Response(boolean success, String message, HumanBeing humanBeing) {
        this.success = success;
        this.message = message;
        this.humanBeing = humanBeing;
    }

    public boolean isSuccess() { return success; }

    public String getMessage() { return message; }

    public HumanBeing getHumanBeing() { return humanBeing; }
}
