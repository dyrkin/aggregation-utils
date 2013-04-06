package org.eugenez.utils.clazz;

/**
 * @author eugene zadyra
 */
public class Generified<T, Z> {
    private T tValue;
    private Z zValue;

    public Generified() {
    }

    public T gettValue() {
        return tValue;
    }

    public void settValue(T tValue) {
        this.tValue = tValue;
    }

    public Z getzValue() {
        return zValue;
    }

    public void setzValue(Z zValue) {
        this.zValue = zValue;
    }
}
