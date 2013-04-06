package org.eugenez.utils;

import org.eugenez.utils.avg.AvgFactory;
import org.eugenez.utils.common.Aggregator;
import org.eugenez.utils.common.ItemAggregator;
import org.eugenez.utils.common.ListAggregator;
import org.eugenez.utils.exception.AggregationException;
import org.eugenez.utils.minmax.MaxAggregator;
import org.eugenez.utils.minmax.MinAggregator;
import org.eugenez.utils.sum.Sum;
import org.eugenez.utils.sum.SumFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author eugene zadyra
 */
public class AggregationUtils {

    /**
     * Calculate SUM of the values returned by bean method specified in dummy param
     *
     * @param collection - collection with bean of any type
     * @param dummy      - just for dummy call
     * @param <Z>        - result type
     * @return - returns result of aggregation.
     * @throws AggregationException
     */
    public static <Z> Z sum(Collection<?> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregateItem(collection, (Sum<Z>) SumFactory.createAggregator(getMethodReturnType(methodEntry)), methodEntry);
    }

    public static <Z> Z sum(Collection<?> collection, Collection<Z> dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregateItem(collection, (Sum<Z>) SumFactory.createAggregator(getMethodReturnType(methodEntry)), methodEntry);
    }

    public static <Z extends Comparable> Z min(Collection<?> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregateItem(collection, new MinAggregator<Z>(), methodEntry);
    }

    public static <Z extends Comparable> Z max(Collection<?> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregateItem(collection, new MaxAggregator<Z>(), methodEntry);
    }

    public static <Z> Z avg(Collection<?> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregateItem(collection, (Sum<Z>) AvgFactory.createAggregator(getMethodReturnType(methodEntry)), methodEntry);
    }

    public static <Z> Z first(Collection<?> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <Z> Z last(Collection<?> collection, Z dummy) throws AggregationException {
        return null;
    }

    public static <Z> void set(Collection<?> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        aggregate(collection, new Aggregator<Z>() {
            @Override
            public void add(Object value) {
                //just do nothins
            }
        }, methodEntry);
    }

    public static <Z> List<Z> extract(Collection<?> collection, Z dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregateList(collection, methodEntry);
    }

    public static <Z> List<Z> extract(Collection<?> collection, Collection<Z> dummy) throws AggregationException {
        MethodEntry methodEntry = getAndResetInvokedMethod();
        return aggregateList(collection, methodEntry);
    }

    private static <Z> Z aggregateItem(Collection<?> collection, ItemAggregator<Z> aggregator, MethodEntry methodEntry) throws AggregationException {
        aggregate(collection, aggregator, methodEntry);
        return aggregator.getResult();
    }

    private static <Z> List<Z> aggregateList(Collection<?> collection, MethodEntry methodEntry) throws AggregationException {
        ListAggregator<Z> aggregator = new ListAggregator<Z>();
        aggregate(collection, aggregator, methodEntry);
        return aggregator.getResult();
    }

    private static <Z> void aggregate(Collection<?> collection, Aggregator<Z> aggregator, MethodEntry methodEntry) throws AggregationException {
        for (Object element : collection) {
            try {
                hierarchyCall(element, methodEntry, aggregator);
            } catch (IllegalAccessException e) {
                throw new AggregationException("Error occurred", e);
            } catch (InvocationTargetException e) {
                throw new AggregationException("Error occurred", e);
            }
        }
    }

    private static <Z> void hierarchyCall(Object element, MethodEntry methodEntry, Aggregator<Z> aggregator) throws IllegalAccessException, InvocationTargetException {
        if (isCollectionRequiresIteration(element, methodEntry)) {
            iterateCollection(element, methodEntry, aggregator);
        } else {
            Object potentialResult = methodEntry.getMethod().invoke(element, methodEntry.getArgs());
            if (methodEntry.getNextMethodEntry() != null) {
                hierarchyCall(potentialResult, methodEntry.getNextMethodEntry(), aggregator);
            } else {
                if (potentialResult instanceof Collection) {
                    aggregator.addAll((Collection<Z>) potentialResult);
                } else {
                    aggregator.add((Z) potentialResult);
                }
            }
        }
    }

    /**
     * If get method of collection has index argument equals -1 then this means the
     * we should do hierarchical call for every element in this collection
     *
     * @param element     - any object
     * @param methodEntry
     * @return true in case if element is collection, method called equals to "get",
     *         method "get" has one argument and it's value = -1
     */
    private static boolean isCollectionRequiresIteration(Object element, MethodEntry methodEntry) {
        return element instanceof Iterable && methodEntry.getMethod().getName().equals("get")
                && methodEntry.getArgs() != null && methodEntry.getArgs().length == 1
                && (Integer) methodEntry.getArgs()[0] == -1;
    }

    private static void iterateCollection(Object element, MethodEntry methodEntry, Aggregator aggregator) throws IllegalAccessException, InvocationTargetException {
        Iterator iterator = ((Iterable) element).iterator();
        int i = 0;
        while (iterator.hasNext()) {
            MethodEntry entry = new MethodEntry(methodEntry.getMethod(), new Object[]{i});
            entry.setReturnType(methodEntry.getReturnType());
            entry.setNextMethodEntry(methodEntry.getNextMethodEntry());
            hierarchyCall(element, entry, aggregator);
            iterator.next();
            i++;
        }
    }

    private static Class<?> getMethodReturnType(MethodEntry methodEntry) {
        if (methodEntry.getNextMethodEntry() != null) {
            return getMethodReturnType(methodEntry.getNextMethodEntry());
        }
        return methodEntry.getReturnType();
    }

    private static MethodEntry getAndResetInvokedMethod() {
        MethodEntry methodEntry = Enhancer.invokedMethodHierarchy.get();
        Enhancer.invokedMethodHierarchy.set(null);

        return getEntryFromTheBottomOfHierarchy(methodEntry);
    }

    private static MethodEntry getEntryFromTheBottomOfHierarchy(MethodEntry methodEntry) {
        if (methodEntry.getPreviousMethod() != null) {
            return getEntryFromTheBottomOfHierarchy(methodEntry.getPreviousMethod());
        }
        return methodEntry;
    }
}
