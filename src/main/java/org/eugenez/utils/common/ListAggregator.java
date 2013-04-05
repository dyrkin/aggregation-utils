package org.eugenez.utils.common;

import java.util.List;

/**
 * User: ezadyra
 * Date: 4/1/13
 */
public class ListAggregator<T> extends Aggregator<T> {

    public List<T> getResult() {
        return values;
    }
}
