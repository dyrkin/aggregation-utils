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
                Z result = (Z) hierarchyCall(element, methodEntry, aggregator);
                if (result != null) {
                    aggregator.add(result);
                }
            } catch (IllegalAccessException e) {
                throw new AggregationException("Error occurred", e);
            } catch (InvocationTargetException e) {
                throw new AggregationException("Error occurred", e);
            }
        }
        return aggregator.getResult();
    }

    private static Object hierarchyCall(Object element, MethodEntry methodEntry, Aggregator aggregator) throws IllegalAccessException, InvocationTargetException {
        if (element instanceof Iterable && methodEntry.getMethod().getName().equals("get")
                && methodEntry.getArgs() != null && methodEntry.getArgs().length == 1
                && (Integer) methodEntry.getArgs()[0] == -1) {
            Iterator iterator = ((Iterable) element).iterator();
            int i = 0;
            while (iterator.hasNext()) {
                MethodEntry entry = new MethodEntry(methodEntry.getMethod(), new Object[]{i});
                entry.setReturnType(methodEntry.getReturnType());
                entry.setNextMethodEntry(methodEntry.getNextMethodEntry());
                aggregator.add(hierarchyCall(element, entry, aggregator));
                iterator.next();
                i++;
            }
            return null;
        } else {
            Object potentialResult = methodEntry.getMethod().invoke(element, methodEntry.getArgs());
            if (methodEntry.getNextMethodEntry() != null) {
                return hierarchyCall(potentialResult, methodEntry.getNextMethodEntry(), aggregator);
            }
            return potentialResult;
        }
    }

    private static Class<?> getMethodReturnType(MethodEntry methodEntry) {
        if (methodEntry.getNextMethodEntry() != null) {
            return getMethodReturnType(methodEntry.getNextMethodEntry());
        }
        return methodEntry.getReturnType();
    }

    private static MethodEntry getAndResetInvokedMethod() {
        MethodEntry methodEntry = MethodMagic.invokedMethodHierarchy.get();
        MethodMagic.invokedMethodHierarchy.set(null);

        return getBootomEntry(methodEntry);
    }

    private static MethodEntry getBootomEntry(MethodEntry methodEntry) {
        if (methodEntry.getPreviousMethod() != null) {
            return getBootomEntry(methodEntry.getPreviousMethod());
        }
        return methodEntry;
    }
}
