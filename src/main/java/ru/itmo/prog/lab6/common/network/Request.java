package ru.itmo.prog.lab6.common.network;

import ru.itmo.prog.lab6.common.model.HumanBeing;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CommandType commandType;
    private final String[] args;
    private final HumanBeing humanBeing;

    public Request(CommandType commandType, String[] args, HumanBeing humanBeing) {
        this.commandType = commandType;
        this.args = args;
        this.humanBeing = humanBeing;
    }

    public Request(CommandType commandType, String[] args) {
        this(commandType, args, null);
    }

    public Request(CommandType commandType) {
        this(commandType, new String[0], null);
    }

    public CommandType getCommandType() { return commandType; }

    public String[] getArgs() { return args; }

    public HumanBeing getHumanBeing() { return humanBeing; }
}
