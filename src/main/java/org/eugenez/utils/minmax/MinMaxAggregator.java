package org.eugenez.utils.minmax;

import org.eugenez.utils.common.ItemAggregator;

import java.util.Collections;

/**
 * @author eugene zadyra
 */
public abstract class MinMaxAggregator<T extends Comparable> extends ItemAggregator<T> {

    protected abstract T getElement();

    @Override
    public T getResult() {
        Collections.sort(values);
        return getElement();
    }

    @Override
    protected T zero() {
        return null;
    }
}
