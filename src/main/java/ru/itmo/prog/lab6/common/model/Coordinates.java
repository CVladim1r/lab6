package ru.itmo.prog.lab6.common.model;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable, Comparable<Coordinates> {
    private static final long serialVersionUID = 1L;

    private Float x;
    private Double y;

    public Coordinates(Float x, Double y) {
        setX(x);
        setY(y);
    }

    public Float getX() { return x; }

    public void setX(Float x) { this.x = Objects.requireNonNull(x, "x"); }

    public Double getY() { return y; }

    public void setY(Double y) { this.y = Objects.requireNonNull(y, "y"); }

    @Override
    public int compareTo(Coordinates o) {
        int c = Float.compare(x, o.x);
        if (c != 0) return c;
        return Double.compare(y, o.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Float.compare(x, that.x) == 0 && Double.compare(y, that.y) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y); }

    @Override
    public String toString() { return "Coordinates{x=" + x + ", y=" + y + '}'; }
}
