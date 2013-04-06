package org.eugenez.utils.clazz;

import java.util.List;

/**
* @author eugene zadyra
*/
public class Apartment {
    private List<Resident> residents;

    public Apartment() {
    }

    public List<Resident> getResidents() {
        return residents;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }
}
