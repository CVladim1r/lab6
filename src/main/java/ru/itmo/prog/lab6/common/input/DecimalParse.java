package ru.itmo.prog.lab6.common.input;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class DecimalParse {
    private static final MathContext TO_FLOAT = new MathContext(7, RoundingMode.DOWN);
    private static final MathContext TO_DOUBLE = new MathContext(16, RoundingMode.DOWN);

    private DecimalParse() {}

    public static float parseFloat(String raw) {
        return new BigDecimal(DecimalString.forFloatParsing(raw)).round(TO_FLOAT).floatValue();
    }

    public static double parseDouble(String raw) {
        return new BigDecimal(DecimalString.forFloatParsing(raw)).round(TO_DOUBLE).doubleValue();
    }
}
