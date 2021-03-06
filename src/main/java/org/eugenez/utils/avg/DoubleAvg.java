package org.eugenez.utils.avg;

import org.eugenez.utils.sum.DoubleSum;

/**
 * @author eugene zadyra
 */
public class DoubleAvg extends DoubleSum {

    @Override
    public Double getResult() {
        Double sum = super.getResult();
        if (values.size() > 0) {
            return sum / values.size();
        }
        return zero();
    }
}
