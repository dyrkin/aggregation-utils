**Aggregation utils**

Aggregation utils used for collections and doesn't requires anonymous classes to specify method which will be used for aggregation.
This is means that you don't need to do something like this:

```java
AggregationUtils.sum(list, new ValueGetter<SomeClass, Integer>() {
    public Integer getValue(SomeClass object) {
        return object.getIntegerValue();
    }
});
````

or

```java
Integer sum = 0;
for(SomeClass object: list){
    sum+=object.getIntegerValue();
}
````

Just do instead:

```java
AggregationUtils.sum(list, m(SomeClass.class).getIntegerValue());
````

You can also do hierarchy calls:

```java
AggregationUtils.sum(list, m(SomeClass.class).getSomeOtherClass().getIntegerValue());
````

The aggregation funtions supported:

- **sum** - the sum of the return results from the specified method of the collection elements(supported datatypes: *Integer*, *Double*, *Float*, *String*)
- **avg** - the avrage value of the return results from the specified method of the collection elements(supported datatypes: *Integer*, *Double*, *Float*)
- **max** - the max value of the return results from the specified method of the collection elements(supported datatypes: *Integer*, *Double*, *Float*, *String*) **Not implemented yet**
- **min** - the min value of the return results from the specified method of the collection elements(supported datatypes: *Integer*, *Double*, *Float*, *String*) **Not implemented yet**
- **first** - the first value of the return results from the specified method of the collection elements(supported any datatype) **Not implemented yet**
- **last** - the last value of the return results from the specified method of the collection elements(supported any datatype) **Not implemented yet**

There is additional function supported by Aggregation utils and it is **set**. There is example of how to use it:

```java
AggregationUtils.sum(list, m(SomeClass.class).setIntegerValue(30));
````

This is means that for every element in collection will be called the method `setIntegerValue()` whith the value `30`. You can use it to initialize some state in collection element or reset some values.