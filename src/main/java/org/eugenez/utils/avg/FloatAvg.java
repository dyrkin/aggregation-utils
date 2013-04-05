package org.eugenez.utils.avg;

import org.eugenez.utils.sum.FloatSum;

/**
 * @author eugene zadyra
 */
public class FloatAvg extends FloatSum {


    @Override
    public Float getResult() {
        Float sum = super.getResult();
        if (values.size() > 0) {
            return sum / values.size();
        }
        return zero();
    }
}
