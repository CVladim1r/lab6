package ru.itmo.prog.lab6.common.network;

import java.io.Serializable;

public enum CommandType implements Serializable {
    HELP,
    INFO,
    SHOW,
    CLEAR,
    INSERT,
    UPDATE,
    REMOVE_KEY,
    REMOVE_LOWER,
    REMOVE_GREATER_KEY,
    REMOVE_LOWER_KEY,
    COUNT_BY_WEAPON_TYPE,
    GROUP_COUNTING_BY_NAME,
    PRINT_FIELD_DESCENDING_MOOD,
    GET_BY_ID
}
