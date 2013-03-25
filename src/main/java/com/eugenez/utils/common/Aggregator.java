package com.eugenez.utils.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eugene zadyra
 */
public abstract class Aggregator<T> {

    protected List<T> values = new ArrayList<T>();

    public void add(T value) {
        if (value != null) {
            values.add(value);
        }
    }

    public abstract T getResult();

    protected abstract T zero();
}
