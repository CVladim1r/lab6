package ru.itmo.prog.lab6.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class HumanBeing implements Serializable, Comparable<HumanBeing> {
    private static final long serialVersionUID = 1L;
    private static final Comparator<Boolean> NULL_BOOL_LAST = Comparator.nullsLast(Boolean::compareTo);
    private static final Comparator<WeaponType> NULL_WEAPON_LAST = Comparator.nullsLast(Enum::compareTo);
    private static final Comparator<Mood> NULL_MOOD_LAST = Comparator.nullsLast(Enum::compareTo);
    private static final Comparator<Car> NULL_CAR_LAST = Comparator.nullsLast(Car::compareTo);

    private int id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private boolean realHero;
    private Boolean hasToothpick;
    private Integer impactSpeed;
    private WeaponType weaponType;
    private Mood mood;
    private Car car;

    public HumanBeing() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) {
        Objects.requireNonNull(name, "name");
        if (name.isEmpty()) throw new IllegalArgumentException("name must not be empty");
        this.name = name;
    }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = Objects.requireNonNull(coordinates, "coordinates");
    }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = Objects.requireNonNull(creationDate, "creationDate");
    }

    public boolean isRealHero() { return realHero; }
    public void setRealHero(boolean realHero) { this.realHero = realHero; }

    public Optional<Boolean> getHasToothpick() { return Optional.ofNullable(hasToothpick); }
    public void setHasToothpick(Boolean hasToothpick) { this.hasToothpick = hasToothpick; }

    public Integer getImpactSpeed() { return impactSpeed; }
    public void setImpactSpeed(Integer impactSpeed) {
        this.impactSpeed = Objects.requireNonNull(impactSpeed, "impactSpeed");
    }

    public Optional<WeaponType> getWeaponType() { return Optional.ofNullable(weaponType); }
    public void setWeaponType(WeaponType weaponType) { this.weaponType = weaponType; }

    public Optional<Mood> getMood() { return Optional.ofNullable(mood); }
    public void setMood(Mood mood) { this.mood = mood; }

    public Optional<Car> getCar() { return Optional.ofNullable(car); }
    public void setCar(Car car) { this.car = car; }

    @Override
    public int compareTo(HumanBeing o) {
        int c = name.compareTo(o.name);
        if (c != 0) return c;
        c = coordinates.compareTo(o.coordinates);
        if (c != 0) return c;
        c = Boolean.compare(realHero, o.realHero);
        if (c != 0) return c;
        c = NULL_BOOL_LAST.compare(hasToothpick, o.hasToothpick);
        if (c != 0) return c;
        c = Integer.compare(impactSpeed, o.impactSpeed);
        if (c != 0) return c;
        c = NULL_WEAPON_LAST.compare(weaponType, o.weaponType);
        if (c != 0) return c;
        c = NULL_MOOD_LAST.compare(mood, o.mood);
        if (c != 0) return c;
        return NULL_CAR_LAST.compare(car, o.car);
    }

    @Override
    public String toString() {
        return "HumanBeing{id=" + id + ", name='" + name + '\'' + ", coordinates=" + coordinates
                + ", creationDate=" + creationDate + ", realHero=" + realHero
                + ", hasToothpick=" + hasToothpick + ", impactSpeed=" + impactSpeed
                + ", weaponType=" + weaponType + ", mood=" + mood + ", car=" + car + '}';
    }
}
