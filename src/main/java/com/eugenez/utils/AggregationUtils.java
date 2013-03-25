package com.eugenez.utils;

import com.eugenez.utils.avg.AvgFactory;
import com.eugenez.utils.common.Aggregator;
import com.eugenez.utils.exception.AggregationException;
import com.eugenez.utils.sum.Sum;
import com.eugenez.utils.sum.SumFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * @author eugene zadyra
 */
public class AggregationUtils {

    /**
     * Calculate SUM of the values returned by bean method specified in dummy param
     *
     * @param collection - collection with POJO objects
     * @param dummy      - just for dummy call
     * @param <T>        - collection with beans of type T
     * @param <Z>        - result type
     * @return - returns result of aggregation.
     * @throws AggregationException
     */
    public static <T, Z> Z sum(Collection<T> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregate(collection, (Sum<Z>) SumFactory.createSumAggregator(getMethodReturnType(methodEntry)), methodEntry);
    }

    public static <T, Z> Z min(Collection<T> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <T, Z> Z max(Collection<T> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <T, Z> Z avg(Collection<T> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregate(collection, (Sum<Z>) AvgFactory.createAvgAggregator(getMethodReturnType(methodEntry)), methodEntry);
    }

    public static <T, Z> Z first(Collection<T> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <T, Z> Z last(Collection<T> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <T, Z> Z set(Collection<T> collection, Z dummy) throws AggregationException {
        return null;
    }

    private static <T, Z> Z aggregate(Collection<T> collection, Aggregator<Z> aggregator, MethodEntry methodEntry) throws AggregationException {
        for (T element : collection) {
            try {
                aggregator.add((Z) hierarchyCall(element, methodEntry));
            } catch (IllegalAccessException e) {
                throw new AggregationException("Error occurred", e);
            } catch (InvocationTargetException e) {
                throw new AggregationException("Error occurred", e);
            }
        }
        return aggregator.getResult();
    }

    private static Object hierarchyCall(Object element, MethodEntry methodEntry) throws IllegalAccessException, InvocationTargetException {
        Object potentialResult = methodEntry.getMethod().invoke(element, methodEntry.getArgs());
        if (methodEntry.getValue() != null) {
            return hierarchyCall(potentialResult, methodEntry.getValue());
        }
        return potentialResult;
    }

    private static Class<?> getMethodReturnType(MethodEntry methodEntry) {
        if (methodEntry.getValue() != null) {
            return getMethodReturnType(methodEntry.getValue());
        }
        return methodEntry.getReturnType();
    }

    private static MethodEntry getAndResetInvokedMethod() {
        MethodEntry method = MethodMagic.invokedMethodHierarchy.get();
        MethodMagic.invokedMethodHierarchy.set(null);
        return method;
    }
}
