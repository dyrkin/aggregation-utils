**Aggregation utils**


**SUM**

Aggregation utils uses for collections and don't requires anonymous classes to specify method which will be used for aggregation.
This is means that to count all apartments from all buildings on your street you don't need to do something like this:

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
Integer apartmentsCount = AggregationUtils.sum(buildings, new ValueGetter<Building, Integer>() {
    public Integer getValue(Building object) {
         return object.getApartmentsCount();
    }
});
````

or

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
Integer apartmentsCount = 0;
for(Building building: buildings){
    apartmentsCount+=building.getApartmentsCount();
}
````
Just add static import: `import static org.eugenez.utils.Enhancer.e;`

And do instead:

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
Integer apartmentsCount = AggregationUtils.sum(buildings, e(Building.class).getApartmentsCount());
````

**Hierarchical calls**

You also can do hierarchical calls. So, to count all bricks from all buildings on your street do:

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
Integer bicksCount = AggregationUtils.sum(buildings, e(Building.class).getBuildingFrame().getBricks().size()));
````

You don't need to do any NullPointerException checks.

Ok, to count all residents who live in the first apartment in every building on your street.

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
Integer residentsCount = AggregationUtils.sum(buildings, e(Building.class).getApartments().get(0).getResidents().size());
````

**Nested collection magic**

So, to count all residents who live in every apartment in every building on your street, instead of writing:

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
int residentsCount=0;
for(Building building: buildings){
    if(building.getApartments()!=null){
        for(Apartment apartment: building.getApartments()){
            if(apartment.getResidents()!=null){
                residentsCount+=apartment.getResidents().size();
            }
        }
    }
}
````

You can just write(specifying element index = `-1`):

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
Integer residentsCount = AggregationUtils.sum(buildings, e(Building.class).getApartments().get(-1).getResidents().size());
````

and aggregation utility will go through all elements in `getApartments()` collection.


**EXTRACT**

Extract aggreagation function used to extract collection of the values returned by the specified method.

To get a list of ages of every resident who live in every apartment in every building on your street:

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
List<Integer> ages = AggregationUtils.extract(buildings, e(Building.class).getApartments().get(-1).getResidents().get(-1).getAge());
````


**SET**

There is additional function supported by Aggregation utils and it is `set`. There is example of how to use it:

```java
List<Building> buildings = new ArrayList<Building>();
//add some data here
AggregationUtils.set(buildings, e(Building.class).getApartments().get(-1).getKithcen().setArea(30));
````

You set area of the kitchen is equals to 30 for every aparment in every building on your street.

The aggregation utils supported these functions:

- `sum` - the sum of the return results from the specified method of the collection elements(supported datatypes: `Integer`, `Double`, `Float`, `String`)
- `avg` - the avrage value of the return results from the specified method of the collection elements(supported datatypes: `Integer`, `Double`, `Float`)
- `max` - the max value of the return results from the specified method of the collection elements(supported datatypes: **Any comparable**)
- `min` - the min value of the return results from the specified method of the collection elements(supported datatypes: **Any comparable**)
- `first` - the first value of the return results from the specified method of the collection elements(supported any datatype) **Not implemented yet**
- `last` - the last value of the return results from the specified method of the collection elements(supported any datatype) **Not implemented yet**

- `extract` - collection of specified method value (supported any datatype)

- `set` - set the specified value for every element in collection
