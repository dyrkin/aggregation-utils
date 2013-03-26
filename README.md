**Aggregation utils**

Aggregation utils used for collections and doesn't requires anonymous classes to specify method which will be used for aggregation.
This is means that you don't need to do something like this:

```java
List<SomeClass> list = new ArrayList<SomeClass>();
//add some data here
AggregationUtils.sum(list, new ValueGetter<SomeClass, Integer>() {
    public Integer getValue(SomeClass object) {
        return object.getIntegerValue();
    }
});
````

or

```java
List<SomeClass> list = new ArrayList<SomeClass>();
//add some data here
Integer sum = 0;
for(SomeClass object: list){
    sum+=object.getIntegerValue();
}
````
Just add static import: `import static com.eugenez.utils.MethodMagic.m;`

And do instead:

```java
List<SomeClass> list = new ArrayList<SomeClass>();
//add some data here
AggregationUtils.sum(list, m(SomeClass.class).getIntegerValue());
````

You can also do hierarchy calls:

```java
List<SomeClass> list = new ArrayList<SomeClass>();
//add some data here
AggregationUtils.sum(list, m(SomeClass.class).getSomeOtherClass().getIntegerValue());
````

For every element in the list call `getCollectionElements()` method which returns the collection of `CollectionElement.class` objects. 
Get first element from collection and call the method `getIntegerValue()` and summarize result:

```java
List<ClassWithCollection> list = new ArrayList<ClassWithCollection>();
//add some data here
AggregationUtils.sum(list, m(ClassWithCollection.class).getCollectionElements().get(0).getIntegerValue());
````

**Nested collection magic**

Instead of:
```java
List<ClassWithCollection> list = new ArrayList<ClassWithCollection>();
//add some data here
int sum=0;
for(ClassWithCollection classWithCollection: list){
    if(classWithCollection.getCollectionElements()!=null){
        for(ClassWithCollection.CollectionElement collectionElement: classWithCollection.getCollectionElements()){
            sum+=collectionElement.getIntegerValue();
        }
    }
}
````

You can just write(specifying element index = `-1`):
```java
List<ClassWithCollection> list = new ArrayList<ClassWithCollection>();
//add some data here
AggregationUtils.sum(list, m(ClassWithCollection.class).getCollectionElements().get(-1).getIntegerValue())
````

adn aggregation utility will go through all elements in `getCollectionElements()` collection.

The aggregation funtions supported:

- `sum` - the sum of the return results from the specified method of the collection elements(supported datatypes: `Integer`, `Double`, `Float`, `String`)
- `avg` - the avrage value of the return results from the specified method of the collection elements(supported datatypes: `Integer`, `Double`, `Float`)
- `max` - the max value of the return results from the specified method of the collection elements(supported datatypes: `Integer`, `Double`, `Float`, `String`) **Not implemented yet**
- `min` - the min value of the return results from the specified method of the collection elements(supported datatypes: `Integer`, `Double`, `Float`, `String`) **Not implemented yet**
- `first` - the first value of the return results from the specified method of the collection elements(supported any datatype) **Not implemented yet**
- `last` - the last value of the return results from the specified method of the collection elements(supported any datatype) **Not implemented yet**

There is additional function supported by Aggregation utils and it is `set`. There is example of how to use it:

```java
List<ClassWithCollection> list = new ArrayList<ClassWithCollection>();
//add some data here
AggregationUtils.sum(list, m(SomeClass.class).setIntegerValue(30));
````

This is means that for every element in collection will be called the method `setIntegerValue()` whith the value `30`. You can use it to initialize some state in collection element or reset some values.
