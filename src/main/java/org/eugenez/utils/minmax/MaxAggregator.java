package org.eugenez.utils.minmax;

/**
 * @author eugene zadyra
 */
public class MaxAggregator<T extends Comparable> extends MinMaxAggregator<T> {


    @Override
    protected T getElement() {
        return values.get(values.size() - 1);
    }
}
