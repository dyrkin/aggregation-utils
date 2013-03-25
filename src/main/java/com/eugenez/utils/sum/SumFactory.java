package com.eugenez.utils.sum;

import com.eugenez.utils.common.Aggregator;
import com.eugenez.utils.exception.AggregationException;

/**
 * @author eugene zadyra
 */
public class SumFactory {
    public static <Z> Aggregator<Z> createSumAggregator(Class<?> returnType) throws AggregationException {
        if (returnType.equals(Integer.TYPE) || returnType.equals(Integer.class)) {
            return (Sum<Z>) new IntegerSum();
        } else if (returnType.equals(Double.TYPE) || returnType.equals(Double.class)) {
            return (Sum<Z>) new DoubleSum();
        } else if (returnType.equals(Float.TYPE) || returnType.equals(Float.class)) {
            return (Sum<Z>) new FloatSum();
        } else if (returnType.equals(String.class)) {
            return (Sum<Z>) new StringSum();
        }
        throw new AggregationException("Unsupported type: " + returnType.getSimpleName());
    }
}
