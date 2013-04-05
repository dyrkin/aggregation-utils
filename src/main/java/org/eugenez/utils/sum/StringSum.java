package org.eugenez.utils.sum;

/**
* @author eugene zadyra
*/
public class StringSum extends Sum<String> {

    @Override
    protected String zero() {
        return "";
    }

    @Override
    protected String inc(String oldVal, String incVal) {
        return oldVal += incVal;
    }
}
