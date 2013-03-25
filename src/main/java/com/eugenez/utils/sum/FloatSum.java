package com.eugenez.utils.sum;

/**
* @author eugene zadyra
*/
public class FloatSum extends Sum<Float> {

    @Override
    protected Float zero() {
        return (float) 0;
    }

    @Override
    protected Float inc(Float oldVal, Float incVal) {
        return oldVal += incVal;
    }
}
