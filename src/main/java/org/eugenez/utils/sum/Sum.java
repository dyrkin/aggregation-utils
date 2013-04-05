package org.eugenez.utils.sum;

import org.eugenez.utils.common.ItemAggregator;

/**
 * @author eugene zadyra
 */
public abstract class Sum<T> extends ItemAggregator<T> {

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
