package org.eugenez.utils;


import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.*;
import static org.eugenez.utils.Enhancer.e;


/**
 * @author eugene zadyra
 */
public class EnhancerTest {

    @Test
    public void testOneLevelCall() {
        e(Building.class).getApartmentsCount();
        MethodEntry methodEntry = Enhancer.invokedMethodHierarchy.get();
        assertNotNull(methodEntry);
        assertNotNull(methodEntry.getMethod());
        assertEquals("getApartmentsCount", methodEntry.getMethod().getName());
        assertEquals(0, methodEntry.getArgs().length);
        assertEquals(Integer.TYPE, methodEntry.getReturnType());
        assertNull(methodEntry.getNextMethodEntry());
    }

    @Test
    public void testTwoLevelsCall() {
        e(Building.class).getDimensions().getArea();
        MethodEntry methodEntry = getEntryFromTheBottomOfHierarchy(Enhancer.invokedMethodHierarchy.get());
        assertNotNull(methodEntry.getNextMethodEntry());
        MethodEntry secondLevelMethodEntry = methodEntry.getNextMethodEntry();
        assertNotNull(secondLevelMethodEntry);
        assertNotNull(secondLevelMethodEntry.getMethod());
        assertEquals("getArea", secondLevelMethodEntry.getMethod().getName());
        assertEquals(0, secondLevelMethodEntry.getArgs().length);
        assertEquals(Integer.TYPE, secondLevelMethodEntry.getReturnType());
        assertNull(secondLevelMethodEntry.getNextMethodEntry());
    }

    @Test
    public void testTwoLevelsCollectionCall() {
        e(Building.class).getApartments().size();
        MethodEntry methodEntry = getEntryFromTheBottomOfHierarchy(Enhancer.invokedMethodHierarchy.get());
        assertNotNull(methodEntry);
        assertNotNull(methodEntry.getMethod());
        assertEquals("getApartments", methodEntry.getMethod().getName());
        assertEquals(0, methodEntry.getArgs().length);
        assertEquals(List.class, methodEntry.getReturnType());
        assertNotNull(methodEntry.getNextMethodEntry());
        MethodEntry secondLevelMethodEntry = methodEntry.getNextMethodEntry();
        assertNotNull(secondLevelMethodEntry);
        assertNotNull(secondLevelMethodEntry.getMethod());
        assertEquals("size", secondLevelMethodEntry.getMethod().getName());
        assertEquals(0, secondLevelMethodEntry.getArgs().length);
        assertEquals(Integer.TYPE, secondLevelMethodEntry.getReturnType());
        assertNull(secondLevelMethodEntry.getNextMethodEntry());
    }


    @Test
    public void testTwoLevelsCollectionSizeCall() {
        e(Building.class).getApartments().size();
        MethodEntry methodEntry = getEntryFromTheBottomOfHierarchy(Enhancer.invokedMethodHierarchy.get());
        assertNotNull(methodEntry);
        assertNotNull(methodEntry.getMethod());
        assertEquals("getApartments", methodEntry.getMethod().getName());
        assertEquals(0, methodEntry.getArgs().length);
        assertEquals(List.class, methodEntry.getReturnType());
        assertNotNull(methodEntry.getNextMethodEntry());
        MethodEntry secondLevelMethodEntry = methodEntry.getNextMethodEntry();
        assertNotNull(secondLevelMethodEntry);
        assertNotNull(secondLevelMethodEntry.getMethod());
        assertEquals("size", secondLevelMethodEntry.getMethod().getName());
        assertEquals(0, secondLevelMethodEntry.getArgs().length);
        assertEquals(Integer.TYPE, secondLevelMethodEntry.getReturnType());
        assertNull(secondLevelMethodEntry.getNextMethodEntry());
    }

    @Test
    public void testTwoLevelsCollectionGetCall() {
        e(Building.class).getApartments().get(0);
        MethodEntry methodEntry = getEntryFromTheBottomOfHierarchy(Enhancer.invokedMethodHierarchy.get());
        assertNotNull(methodEntry.getNextMethodEntry());
        MethodEntry secondLevelMethodEntry = methodEntry.getNextMethodEntry();
        assertNotNull(secondLevelMethodEntry);
        assertNotNull(secondLevelMethodEntry.getMethod());
        assertEquals("get", secondLevelMethodEntry.getMethod().getName());
        assertEquals(1, secondLevelMethodEntry.getArgs().length);
        assertEquals(0, secondLevelMethodEntry.getArgs()[0]);
        assertEquals(Apartment.class, secondLevelMethodEntry.getReturnType());
        assertNull(secondLevelMethodEntry.getNextMethodEntry());
    }


    @Test
    public void testMultiLevelsCollectionCall() {
        e(Building.class).getApartments().get(0).getResidents().get(-1).getAge();
        MethodEntry methodEntry = getEntryFromTheBottomOfHierarchy(Enhancer.invokedMethodHierarchy.get());
        assertNotNull(methodEntry.getNextMethodEntry());
        assertNotNull(methodEntry.getNextMethodEntry().getNextMethodEntry());
        MethodEntry thirdLevelMethodEntry = methodEntry.getNextMethodEntry().getNextMethodEntry();
        assertNotNull(thirdLevelMethodEntry);
        assertNotNull(thirdLevelMethodEntry.getMethod());
        assertEquals("getResidents", thirdLevelMethodEntry.getMethod().getName());
        assertEquals(0, thirdLevelMethodEntry.getArgs().length);
        assertEquals(List.class, thirdLevelMethodEntry.getReturnType());

        assertNotNull(thirdLevelMethodEntry.getNextMethodEntry());
        MethodEntry fourthLevelMethodEntry = thirdLevelMethodEntry.getNextMethodEntry();
        assertNotNull(fourthLevelMethodEntry);
        assertNotNull(fourthLevelMethodEntry.getMethod());
        assertEquals("get", fourthLevelMethodEntry.getMethod().getName());
        assertEquals(1, fourthLevelMethodEntry.getArgs().length);
        assertEquals(-1, fourthLevelMethodEntry.getArgs()[0]);
        assertEquals(Resident.class, fourthLevelMethodEntry.getReturnType());

        assertNotNull(fourthLevelMethodEntry.getNextMethodEntry());
        MethodEntry fifthLevelMethodEntry = fourthLevelMethodEntry.getNextMethodEntry();
        assertNotNull(fifthLevelMethodEntry);
        assertNotNull(fifthLevelMethodEntry.getMethod());
        assertEquals("getAge", fifthLevelMethodEntry.getMethod().getName());
        assertEquals(0, fifthLevelMethodEntry.getArgs().length);
        assertEquals(Integer.TYPE, fifthLevelMethodEntry.getReturnType());
        assertNull(fifthLevelMethodEntry.getNextMethodEntry());
    }

    private static MethodEntry getEntryFromTheBottomOfHierarchy(MethodEntry methodEntry) {
        if (methodEntry.getPreviousMethod() != null) {
            return getEntryFromTheBottomOfHierarchy(methodEntry.getPreviousMethod());
        }
        return methodEntry;
    }

    public static class Building {
        private List<Apartment> apartments;
        private int apartmentsCount;
        private Dimensions dimensions;

        public Building() {
        }

        public List<Apartment> getApartments() {
            return apartments;
        }

        public void setApartments(List<Apartment> apartments) {
            this.apartments = apartments;
        }

        public void setApartmentsCount(int apartmentsCount) {
            this.apartmentsCount = apartmentsCount;
        }

        public int getApartmentsCount() {
            return apartmentsCount;
        }

        public Dimensions getDimensions() {
            return dimensions;
        }

        public void setDimensions(Dimensions dimensions) {
            this.dimensions = dimensions;
        }
    }

    public static class Apartment {
        private List<Resident> residents;

        public Apartment() {
        }

        public List<Resident> getResidents() {
            return residents;
        }

        public void setResidents(List<Resident> residents) {
            this.residents = residents;
        }
    }

    public static class Resident {
        private int age;

        public Resident() {
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Dimensions {
        private int area;

        public Dimensions() {
        }

        public int getArea() {
            return area;
        }

        public void setArea(int area) {
            this.area = area;
        }
    }
}
