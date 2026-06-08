package ru.itmo.prog.lab6.common.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class Car implements Serializable, Comparable<Car> {
    private static final long serialVersionUID = 1L;
    private static final Comparator<Car> NULLS_LAST = Comparator.nullsLast(
            Comparator.comparing(Car::getName, Comparator.nullsLast(String::compareTo))
                      .thenComparing(Car::getCool, Comparator.nullsLast(Boolean::compareTo)));

    private String name;
    private Boolean cool;

    public Car(String name, Boolean cool) {
        setName(name);
        this.cool = cool;
    }

    public String getName() { return name; }

    public void setName(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isEmpty()) throw new IllegalArgumentException("name must not be empty");
        this.name = name;
    }

    public Boolean getCool() { return cool; }

    public void setCool(Boolean cool) { this.cool = cool; }

    @Override
    public int compareTo(Car o) { return NULLS_LAST.compare(this, o); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(name, car.name) && Objects.equals(cool, car.cool);
    }

    @Override
    public int hashCode() { return Objects.hash(name, cool); }

    @Override
    public String toString() { return "Car{name='" + name + '\'' + ", cool=" + cool + '}'; }
}
