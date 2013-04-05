package org.eugenez.utils;

import org.eugenez.utils.exception.AggregationException;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.eugenez.utils.MethodMagic.m;
import static org.junit.Assert.assertEquals;

/**
 * @author eugene zadyra
 */
public class AggregationUtilsTest {
    @Test
    public void testSinglethredSum() throws AggregationException {

        List<SomeClass> list = new ArrayList<SomeClass>() {{
            add(new SomeClass(5, 10, "Hello", 1));
            add(new SomeClass(5, 12, " World! ", 1));
            add(new SomeClass(5, 11, "Wassup!", 1));
        }};

        Assert.assertEquals(Integer.valueOf(15), AggregationUtils.sum(list, m(SomeClass.class).getIntV()));
        assertEquals(Double.valueOf(33), AggregationUtils.sum(list, m(SomeClass.class).getDouV()));
        assertEquals("Hello World! Wassup!", AggregationUtils.sum(list, m(SomeClass.class).getStrV()));

    }

    @Test
    public void testSinglethredCallHierarchySum() throws AggregationException {

        List<SomeClass> list = new ArrayList<SomeClass>() {{
            add(new SomeClass().setSomeOtherClass(new SomeClass.SomeOtherClass(12)));
            add(new SomeClass().setSomeOtherClass(new SomeClass.SomeOtherClass(13)));
            add(new SomeClass().setSomeOtherClass(new SomeClass.SomeOtherClass(14)));
        }};

        assertEquals(Integer.valueOf(39), AggregationUtils.sum(list, m(SomeClass.class).getSomeOtherClass().getIntValue()));
    }

    @Test
    public void testSinglethredCallHierarchyCollectionSum() throws AggregationException {

        List<SomeClass> list = new ArrayList<SomeClass>() {{
            add(new SomeClass().addValsToIntCollection(12));
            add(new SomeClass().addValsToIntCollection(13));
            add(new SomeClass().addValsToIntCollection(14));
        }};

        assertEquals(Integer.valueOf(39), AggregationUtils.sum(list, m(SomeClass.class).getCollection().get(0)));
    }

    @Test
    public void testMutithreadSum() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Callable<ExpectedValue>> todo = new ArrayList<Callable<ExpectedValue>>();
        for (int i = 0; i < 100; i++) {
            todo.add(new MyCallable());
        }
        List<Future<ExpectedValue>> answers = executorService.invokeAll(todo);

        //validate results
        for (Future<ExpectedValue> future : answers) {
            assertEquals(future.get().getExpectedInt(), future.get().getActualInt());
//            System.out.println("Expected INT: " + future.get().getExpectedInt() + ". Actual INT: " + future.get().getActualInt());

            assertEquals(future.get().getExpectedDouble(), future.get().getActualDouble());
//            System.out.println("Expected DOUBLE: " + future.get().getExpectedDouble() + ". Actual DOUBLE: " + future.get().getActualDouble());

            assertEquals(future.get().getExpectedString(), future.get().getActualString());
//            System.out.println("Expected STRING: " + future.get().getExpectedString() + ". Actual STRING: " + future.get().getActualString());
        }
    }


    private static class MyCallable implements Callable<ExpectedValue> {
        int expectedIntSum = 0;
        double expectedDoubleSum = 0.;
        StringBuilder expectedStringSum = new StringBuilder();
        List<SomeClass> list;

        ExpectedValue expectedValue;

        public MyCallable() {
            prepareData();
        }

        private void prepareData() {
            Random r = new Random();
            list = new ArrayList<SomeClass>();
            for (int z = 0; z < 100; z++) {
                //generate random values
                int randomInt = r.nextInt(100);
                double randomDouble = r.nextDouble() * 100;
                String randomString = UUID.randomUUID().toString().substring(1, 2);

                list.add(new SomeClass(randomInt, randomDouble, randomString, randomInt));

                expectedIntSum += randomInt;
                expectedDoubleSum += randomDouble;
                expectedStringSum.append(randomString);
            }
            expectedValue = new ExpectedValue(expectedIntSum, expectedDoubleSum, expectedStringSum.toString(), expectedIntSum);
        }

        public ExpectedValue call() throws Exception {
            expectedValue.setActualInt(AggregationUtils.sum(list, m(SomeClass.class).getIntV()));
            expectedValue.setActualDouble(AggregationUtils.sum(list, m(SomeClass.class).getDouV()));
            expectedValue.setActualString(AggregationUtils.sum(list, m(SomeClass.class).getStrV()));
            expectedValue.setActualSomeOtherClassInt(AggregationUtils.sum(list, m(SomeClass.class).getSomeOtherClass().getIntValue()));
            return expectedValue;
        }
    }

    private static class ExpectedValue {
        private Integer expectedInt;
        private Double expectedDouble;
        private String expectedString;
        private Integer expectedSomeOtherClassInt;
        private Integer actualInt;
        private Double actualDouble;
        private String actualString;
        private Integer actualSomeOtherClassInt;

        private ExpectedValue(Integer expectedInt, Double expectedDouble, String expectedString, Integer expectedSomeOtherClassInt) {
            this.expectedInt = expectedInt;
            this.expectedDouble = expectedDouble;
            this.expectedString = expectedString;
            this.expectedSomeOtherClassInt = expectedSomeOtherClassInt;
        }

        public Integer getExpectedInt() {
            return expectedInt;
        }

        public Double getExpectedDouble() {
            return expectedDouble;
        }

        public String getExpectedString() {
            return expectedString;
        }

        public Integer getExpectedSomeOtherClassInt() {
            return expectedSomeOtherClassInt;
        }

        public Integer getActualInt() {
            return actualInt;
        }

        public void setActualInt(Integer actualInt) {
            this.actualInt = actualInt;
        }

        public Double getActualDouble() {
            return actualDouble;
        }

        public void setActualDouble(Double actualDouble) {
            this.actualDouble = actualDouble;
        }

        public String getActualString() {
            return actualString;
        }

        public void setActualString(String actualString) {
            this.actualString = actualString;
        }

        public Integer getActualSomeOtherClassInt() {
            return actualSomeOtherClassInt;
        }

        public void setActualSomeOtherClassInt(Integer actualSomeOtherClassInt) {
            this.actualSomeOtherClassInt = actualSomeOtherClassInt;
        }
    }

    public static class SomeClass {

        private int intV;

        private double douV;

        private String strV;

        private SomeOtherClass someOtherClass;

        private List<Integer> collection = new ArrayList<Integer>();

        public SomeClass() {
        }

        public SomeClass(int intV, double douV, String strV, int someOtherClassInt) {
            this.intV = intV;
            this.douV = douV;
            this.strV = strV;
            this.someOtherClass = new SomeOtherClass(someOtherClassInt);
        }

        public int getIntV() {
            return intV;
        }

        public double getDouV() {
            return douV;
        }

        public String getStrV() {
            return strV;
        }

        public SomeOtherClass getSomeOtherClass() {
            return someOtherClass;
        }

        private SomeClass addValsToIntCollection(Integer... val) {
            collection.addAll(Arrays.asList(val));
            return this;
        }

        public List<Integer> getCollection() {
            return collection;
        }

        public SomeClass setSomeOtherClass(SomeOtherClass someOtherClass) {
            this.someOtherClass = someOtherClass;
            return this;
        }

        public static class SomeOtherClass {
            private int intValue;

            public SomeOtherClass() {

            }

            public SomeOtherClass(int intValue) {
                this.intValue = intValue;
            }

            public int getIntValue() {
                return intValue;
            }
        }
    }


    @Test
    public void testSinglethredCallSum2() throws AggregationException {

        List<ClassWithCollection> list = new ArrayList<ClassWithCollection>() {{
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(12)));
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(13)));
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(14)));
        }};

        assertEquals(Integer.valueOf(39), AggregationUtils.sum(list, m(ClassWithCollection.class).getCollectionElements().get(0).getIntegerValue()));
    }

    @Test
    public void testAggregateList() throws AggregationException {

        List<ClassWithCollection> list = new ArrayList<ClassWithCollection>() {{
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(12),
                    new ClassWithCollection.CollectionElement(12),
                    new ClassWithCollection.CollectionElement(15)));
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(13)));
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(14)));
        }};

        List<Integer> resultList = AggregationUtils.extract(list, m(ClassWithCollection.class).getCollectionElements().get(-1).getIntegerValue());

        assertEquals(5, resultList.size());

        Collections.sort(resultList);
        assertEquals(12, resultList.get(0).intValue());
        assertEquals(12, resultList.get(1).intValue());
        assertEquals(13, resultList.get(2).intValue());
        assertEquals(14, resultList.get(3).intValue());
        assertEquals(15, resultList.get(4).intValue());
    }

    @Test
    public void testSinglethredCallSum3() throws AggregationException {

        List<ClassWithCollection> list = new ArrayList<ClassWithCollection>() {{
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(12),
                    new ClassWithCollection.CollectionElement(12),
                    new ClassWithCollection.CollectionElement(15)));
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(13)));
            add(new ClassWithCollection(new ClassWithCollection.CollectionElement(14)));
        }};

        assertEquals(Integer.valueOf(66), AggregationUtils.sum(list, m(ClassWithCollection.class).getCollectionElements().get(-1).getIntegerValue()));
    }


    public static class ClassWithCollection {
        private List<CollectionElement> collectionElements;

        public ClassWithCollection(CollectionElement... collectionElements) {
            this.collectionElements = new ArrayList<CollectionElement>();
            this.collectionElements.addAll(Arrays.asList(collectionElements));
        }

        public ClassWithCollection() {
        }

        public List<CollectionElement> getCollectionElements() {
            return collectionElements;
        }

        public void setCollectionElements(List<CollectionElement> collectionElements) {
            this.collectionElements = collectionElements;
        }

        public static class CollectionElement {
            public Integer integerValue;

            public CollectionElement() {
            }

            public CollectionElement(Integer integerValue) {
                this.integerValue = integerValue;
            }

            public Integer getIntegerValue() {
                return integerValue;
            }
        }
    }
}