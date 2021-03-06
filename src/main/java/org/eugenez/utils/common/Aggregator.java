package org.eugenez.utils.common;

import java.util.ArrayList;
import java.util.Collection;
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

    public void addAll(Collection<T> collection){
        values.addAll(collection);
    }
}
