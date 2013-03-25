package com.eugenez.utils.sum;

import com.eugenez.utils.common.Aggregator;

/**
 * @author eugene zadyra
 */
public abstract class Sum<T> extends Aggregator<T> {

    protected abstract T inc(T oldVal, T incVal);

    @Override
    public T getResult() {
        T result = zero();
        for (T value : values) {
            result = inc(result, value);
        }
        return result;
    }
}
