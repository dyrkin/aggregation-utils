package org.eugenez.utils.avg;

import org.eugenez.utils.sum.IntegerSum;

/**
 * @author eugene zadyra
 */
public class IntegerAvg extends IntegerSum {

    @Override
    public Integer getResult() {
        Integer sum = super.getResult();
        if (values.size() > 0) {
            return sum / values.size();
        }
        return zero();
    }
}
