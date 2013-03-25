package com.eugenez.utils;

import com.eugenez.utils.avg.AvgFactory;
import com.eugenez.utils.common.Aggregator;
import com.eugenez.utils.exception.AggregationException;
import com.eugenez.utils.sum.Sum;
import com.eugenez.utils.sum.SumFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author eugene zadyra
 */
public class AggregationUtils {

    /**
     * Calculate SUM of the values returned by bean method specified in dummy param
     *
     * @param collection - collection with POJO objects
     * @param dummy      - just for dummy call
     * @param <?>        - collection with beans of type T
     * @param <Z>        - result type
     * @return - returns result of aggregation.
     * @throws AggregationException
     */
    public static <Z> Z sum(Collection<?> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregate(collection, (Sum<Z>) SumFactory.createSumAggregator(getMethodReturnType(methodEntry)), methodEntry);
    }

    public static <Z> Z min(Collection<?> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <Z> Z max(Collection<?> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <Z> Z avg(Collection<?> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregate(collection, (Sum<Z>) AvgFactory.createAvgAggregator(getMethodReturnType(methodEntry)), methodEntry);
    }

    public static <Z> Z first(Collection<?> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <Z> Z last(Collection<?> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <Z> Z set(Collection<?> collection, Z dummy) throws AggregationException {
        return null;
    }

    private static <Z> Z aggregate(Collection<?> collection, Aggregator<Z> aggregator, MethodEntry methodEntry) throws AggregationException {
        for (Object element : collection) {
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

        Object potentialResult = element;
        if (methodEntry.getPreviousMethod() != null) {
            potentialResult = hierarchyCall(element, methodEntry.getPreviousMethod());
        }
        if (potentialResult instanceof Iterable && methodEntry.getMethod().getName().equals("get")
                && methodEntry.getArgs() != null && methodEntry.getArgs().length == 1
                && (Integer) methodEntry.getArgs()[0] == -1) {
            Iterator iterator = ((Iterable)potentialResult).iterator();
            int i=0;
            while (iterator.hasNext()){

                i++;
            }
        }
        return methodEntry.getMethod().invoke(potentialResult, methodEntry.getArgs());
    }

    private static Class<?> getMethodReturnType(MethodEntry methodEntry) {
        return methodEntry.getReturnType();
    }

    private static MethodEntry getAndResetInvokedMethod() {
        MethodEntry method = MethodMagic.invokedMethodHierarchy.get();
        MethodMagic.invokedMethodHierarchy.set(null);
        return method;
    }
}
