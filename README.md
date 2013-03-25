**Aggregation utils**

Aggregation used used for collections and doesn't requires anonymous classes to specify method which will be used for aggregation.
This is means that you don't need to do something like this:

```java
AggregationUtils.sum(list, new ValueGetter<SomeClass, Integer>() {
    public Integer getValue(SomeClass object) {
        return object.getIntV();
    }
});
````

Just do instead:

```java
AggregationUtils.sum(list, m(SomeClass.class).getIntV());
````