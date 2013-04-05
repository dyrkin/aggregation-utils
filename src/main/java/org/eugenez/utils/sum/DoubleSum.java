package org.eugenez.utils.sum;

/**
* @author eugene zadyra
*/
public class DoubleSum extends Sum<Double> {

    @Override
    protected Double zero() {
        return 0.;
    }

    @Override
    protected Double inc(Double oldVal, Double incVal) {
        return oldVal += incVal;
    }
}
