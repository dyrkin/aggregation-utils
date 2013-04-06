package org.eugenez.utils.clazz;

import java.util.List;

/**
* @author eugene zadyra
*/
public class Building {
    private List<Apartment> apartments;
    private int apartmentsCount;
    private Dimensions dimensions;
    private Generified<Integer, Double> generified;

    public Building() {
    }

    public List<Apartment> getApartments() {
        return apartments;
    }

    public void setApartments(List<Apartment> apartments) {
        this.apartments = apartments;
    }

    public void setApartmentsCount(int apartmentsCount) {
        this.apartmentsCount = apartmentsCount;
    }

    public int getApartmentsCount() {
        return apartmentsCount;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    public Generified<Integer, Double> getGenerified() {
        return generified;
    }

    public void setGenerified(Generified<Integer, Double> generified) {
        this.generified = generified;
    }
}
