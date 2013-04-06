package org.eugenez.utils.minmax;

/**
 * @author eugene zadyra
 */
public class MinAggregator<T extends Comparable> extends MinMaxAggregator<T> {


    @Override
    protected T getElement() {
        return values.get(0);
    }
}
