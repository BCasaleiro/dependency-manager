package com.feedzai.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Unit test for simple App.
 */
public class DepManagerTest
    extends TestCase
{
    private static final String MONITOR = "MASTER";
    private static final Hashtable<String, Integer> AVAILABLE_SERVICES = new Hashtable<String, Integer>() {{
        put("a", 0);
        put("b", 1);
        put("c", 2);
        put("d", 3);
    }};
    private ArrayList<Service> services;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DepManagerTest( String testName )
    {
        super( testName );

        services = new ArrayList<Service>();
        for (int i = 0; i < AVAILABLE_SERVICES.size(); i++) {
            services.add(new ServiceDummy(i, MONITOR));
        }
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DepManagerTest.class );
    }

    /**
     * Test Dependency loading
     */
    public void testLoadDependenciesTrue()
    {
        DepManager dm = new DepManager(MONITOR);
        dm.instantiateServices(services, AVAILABLE_SERVICES);
        assertTrue( dm.loadDependencies("dependencies.csv") );
    }

    /**
     * Test Dependency loading error catching
     */
    public void testLoadDependenciesFalse()
    {
        DepManager dm = new DepManager(MONITOR);
        dm.instantiateServices(services, AVAILABLE_SERVICES);
        assertFalse( dm.loadDependencies("dependencies1.csv") );
    }

    /**
     * Test start Service method
     */
    public void testStart() {
        DepManager dm = new DepManager(MONITOR);
        dm.instantiateServices(services, AVAILABLE_SERVICES);
        dm.loadDependencies("dependencies.csv");
        dm.start(1, false);
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue( dm.getRunningServices().size() == 2 );
    }

    /**
     * Test start all method
     */
    public void testStartAll() {
        DepManager dm = new DepManager(MONITOR);
        dm.instantiateServices(services, AVAILABLE_SERVICES);
        dm.loadDependencies("dependencies.csv");
        dm.startAll();
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue( dm.getRunningServices().size() == 4 );
    }

    /**
     * Test stop Service method
     */
    public void testStop() {
        DepManager dm = new DepManager(MONITOR);
        dm.instantiateServices(services, AVAILABLE_SERVICES);
        dm.loadDependencies("dependencies.csv");
        dm.start(1, false);
        dm.stop(1, false);
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue( dm.getRunningServices().size() == 1 );
    }

    /**
     * Test stop all method
     */
    public void testStopAll() {
        DepManager dm = new DepManager(MONITOR);
        dm.instantiateServices(services, AVAILABLE_SERVICES);
        dm.loadDependencies("dependencies.csv");
        dm.startAll();
        dm.stopAll();
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue( dm.getRunningServices().size() == 0 );
    }

    /**
     * Test kill Service method
     */
    public void testKill() {
        DepManager dm = new DepManager(MONITOR);
        dm.instantiateServices(services, AVAILABLE_SERVICES);
        dm.loadDependencies("dependencies.csv");
        dm.start(3, false);
        dm.kill(1);
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue( dm.getRunningServices().size() == 1 );
    }

    /**
     * Test dependency count
     */
    public void testDependencyCount() {
        DepManager dm = new DepManager(MONITOR);
        dm.instantiateServices(services, AVAILABLE_SERVICES);
        dm.loadDependencies("dependencies.csv");
        dm.startAll();
        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue( dm.getRunningServices().get(2).getNumberOfDependencies() == 1 );
    }

    /**
     * Test all methods
     */
    public void testSystem() {
        DepManager dm = new DepManager(MONITOR);
        try {
            dm.instantiateServices(services, AVAILABLE_SERVICES);
            dm.loadDependencies("dependencies.csv");
            dm.startAll();
            Thread.sleep(100);
            assertTrue( dm.getRunningServices().size() == AVAILABLE_SERVICES.size() );

            dm.stop(1, false);
            Thread.sleep(100);
            assertTrue( dm.getRunningServices().size() == 2 );

            dm.start(3, false);
            Thread.sleep(100);
            assertTrue( dm.getRunningServices().size() == AVAILABLE_SERVICES.size() );

            dm.stopAll();
            Thread.sleep(100);
            assertTrue( dm.getRunningServices().size() == 0 );

            dm.start(1, false);
            dm.kill(0);
            Thread.sleep(100);
            assertTrue( dm.getRunningServices().size() == 0 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
