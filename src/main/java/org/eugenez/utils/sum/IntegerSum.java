package org.eugenez.utils.sum;

/**
* @author eugene zadyra
*/
public class IntegerSum extends Sum<Integer> {

    @Override
    protected Integer zero() {
        return 0;
    }

    @Override
    protected Integer inc(Integer oldVal, Integer incVal) {
        return oldVal += incVal;
    }
}
