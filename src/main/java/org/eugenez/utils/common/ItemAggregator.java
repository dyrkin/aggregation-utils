package org.eugenez.utils.common;

/**
 * User: ezadyra
 * Date: 4/1/13
 */
public abstract class ItemAggregator<T> extends Aggregator<T>{
    public abstract T getResult();
    protected abstract T zero();
}
