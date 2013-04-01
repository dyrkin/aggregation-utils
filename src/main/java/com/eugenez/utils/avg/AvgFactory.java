package com.eugenez.utils.avg;

import com.eugenez.utils.common.Aggregator;
import com.eugenez.utils.common.ItemAggregator;
import com.eugenez.utils.exception.AggregationException;
import com.eugenez.utils.sum.*;

/**
 * @author eugene zadyra
 */
public class AvgFactory {
    public static <Z> ItemAggregator<Z> createAvgAggregator(Class<?> returnType) throws AggregationException {
        if (returnType.equals(Integer.TYPE) || returnType.equals(Integer.class)) {
            return (Sum<Z>) new IntegerAvg();
        } else if (returnType.equals(Double.TYPE) || returnType.equals(Double.class)) {
            return (Sum<Z>) new DoubleAvg();
        } else if (returnType.equals(Float.TYPE) || returnType.equals(Float.class)) {
            return (Sum<Z>) new FloatAvg();
        }
        throw new AggregationException("Unsupported type: " + returnType.getSimpleName());
    }
}
