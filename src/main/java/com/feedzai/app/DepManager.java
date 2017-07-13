package com.feedzai.app;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import java.io.IOException;

/**
 * Dependency Manager
 *
 */
public class DepManager
{
    private static final String FILE = "dependencies.csv";
    private static final String DELIMITER = " ";
    private static final String MONITOR = "MASTER";
    private static final Hashtable<String, Integer> AVAILABLE_SERVICES = new Hashtable<String, Integer>() {{
        put("a", 0);
        put("b", 1);
        put("c", 2);
        put("d", 3);
    }};

    private static final boolean DEBUG = true;

    private ArrayList<Service> services;
    private ArrayList<Service> allServicesOrder;
    private ArrayList<Thread> threads;

    /**
    * Basic Dependency Manager Constructor
    *
    */
    public DepManager() { }

    /**
    * Instantiate Available Services
    * @return      available services list
    */
    public void instantiateServices() {
        services = new ArrayList<Service>();
        allServicesOrder = new ArrayList<Service>();
        services.add(new ServiceA(0, MONITOR));
        services.add(new ServiceB(1, MONITOR));
        services.add(new ServiceC(2, MONITOR));
        services.add(new ServiceD(3, MONITOR));

        threads = new ArrayList<Thread>();
        for (int i = 0; i < AVAILABLE_SERVICES.size(); i++) {
            threads.add(new Thread());
        }
    }

    /**
    * Load dependencies
    * @return           if there are problems in the file
    */
    public Boolean loadDependencies() {
        FileReader fr = null;
        BufferedReader br = null;
        String line;
        String[] dependency;

        try {
            fr = new FileReader(FILE);
            br = new BufferedReader(fr);

            while ((line = br.readLine()) != null) {
                dependency = line.split(DELIMITER);

                if ( AVAILABLE_SERVICES.containsKey(dependency[0]) && AVAILABLE_SERVICES.containsKey(dependency[1]) ) {
                    services.get( AVAILABLE_SERVICES.get(dependency[0]) ).addDependency( AVAILABLE_SERVICES.get(dependency[1]) );
                    services.get( AVAILABLE_SERVICES.get(dependency[1]) ).addRequirement( AVAILABLE_SERVICES.get(dependency[0]) );
                } else {
                    return false;
                }
			}

        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
        		if (br != null) {
                    br.close();
                }

        		if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
		}

        this.updateAllRankings();

        return true;
    }

    /**
    * Update Ranking of a Service and its dependencies
    *
    */
    private void updateRanking(int index) {
        Iterator<Integer> it = services.get(index).getDependencies();
        int serviceIndex;

        while ( it.hasNext() ) {
            serviceIndex = it.next();
            if ( services.get(serviceIndex).getRanking() == 0 ) {
                updateRanking( serviceIndex );
            }
            services.get(index).incrementRanking( services.get(serviceIndex).getRanking() );
            services.get(index).incrementRanking(1);
        }
    }

    /**
    * Update Rankings
    * Sort the Services in an Ascending order of ranking
    */
    private void updateAllRankings() {

        for (int i = 0; i < AVAILABLE_SERVICES.size(); i++) {
            if ( services.get(i).getRanking() == 0 ) {
                updateRanking(i);
            }
        }

        allServicesOrder = new ArrayList<Service>(services);
        Collections.sort(allServicesOrder, new Comparator<Service>() {
            @Override
            public int compare(Service a, Service b)
            {
                return a.getRanking().compareTo(b.getRanking());
            }
        });
    }

    /**
    * Start Service
    * @param  index     receive Service index to start
    * @param  noWait    boolean indicating if the wait is needed
    */
    public void start( int index, boolean noWait ) {
        Service service = services.get(index);
        Iterator<Integer> dependencies;
        Integer dependencyIndex;

        if (service.isRunning()) {
            return;
        }

        dependencies = service.getDependencies();
        while (dependencies.hasNext()) {
            dependencyIndex = dependencies.next();

            if ( DEBUG ) System.out.println("[DEBUG] " + index + " depends on " + dependencyIndex + ".");
            if ( !services.get( dependencyIndex ).isRunning() ) {
                start(dependencyIndex, false);
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Starting service " + index + ".");
        threads.set(index,new Thread(service));
        if ( noWait ) {
            threads.get(index).start();
        } else {
            try {
                synchronized (MONITOR) {
                    threads.get(index).start();
                    MONITOR.wait();
                }
            } catch (InterruptedException e) {
                System.out.println("Gotta go, can't wait!");
                Thread.currentThread().interrupt();
            }
        }


        if ( DEBUG ) System.out.println("[DEBUG] Service " + index + " started.");
    }

    /**
    * Start all Services
    *
    */
    public void startAll() {
        int servicesSize = allServicesOrder.size();

        if ( DEBUG ) System.out.println("[DEBUG] Starting all Services.");

        for (int i = 0; i < servicesSize; i++) {
            if ( !allServicesOrder.get(i).isRunning() ) {
                start(allServicesOrder.get(i).getId(), ( i > 0 && allServicesOrder.get(i - 1).getRanking() == allServicesOrder.get(i).getRanking() ) );
            }
        }
    }

    /**
    * Stop Service
    * @param  index     receive Service index to stop
    * @param  noWait    boolean indicating if the wait is needed
    */
    public void stop( int index, boolean noWait ) {
        Service service = services.get(index);
        Iterator<Integer> requirements;
        Integer requirementIndex;

        requirements = service.getRequirements();
        while (requirements.hasNext()) {
            requirementIndex = requirements.next();

            if ( DEBUG ) System.out.println("[DEBUG] " + index + " required by " + requirementIndex + ".");
            if ( services.get( requirementIndex ).isRunning() ) {
                stop(requirementIndex, false);
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Stopping service " + index + ".");
        if (noWait) {
            service.stop();
        } else {
            try {
                synchronized (MONITOR) {
                    service.stop();
                    MONITOR.wait();
                }
            } catch (InterruptedException e) {
                System.out.println("Gotta go, can't wait!");
                Thread.currentThread().interrupt();
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Service " + index + " stopped.");
    }

    /**
    * Stop all Services
    *
    */
    public void stopAll() {
        int servicesSize = allServicesOrder.size();

        if ( DEBUG ) System.out.println("[DEBUG] Stopping all Services.");

        for (int i = servicesSize - 1; i >= 0; i--) {
            if ( services.get(i).isRunning() ) {
                stop(allServicesOrder.get(i).getId(), ( i < (servicesSize - 1) && allServicesOrder.get(i + 1).getRanking() == allServicesOrder.get(i).getRanking() ) );
            }
        }
    }

    /**
    * Kill a Service
    * @param  index  receive Service index to kill
    */
    public void kill( int index ) {
        Service service = services.get(index);
        Iterator<Integer> requirements;
        Integer requirementIndex;

        if ( !services.get(index).isRunning() ) {
            return;
        }

        requirements = service.getRequirements();
        while (requirements.hasNext()) {
            requirementIndex = requirements.next();

            if ( DEBUG ) System.out.println("[DEBUG] " + index + " required by " + requirementIndex + ".");
            if ( services.get( requirementIndex ).isRunning() ) {
                kill(requirementIndex);
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Killing service " + index + ".");
        services.get(index).setRunning(false);
        threads.get(index).interrupt();
        if ( DEBUG ) System.out.println("[DEBUG] Service " + index + " killed.");
    }

    /**
    * List all the running Services
    * @return  list of running services
    */
    public ArrayList<Service> getRunningServices() {
        ArrayList<Service> output = new ArrayList<Service>();
        Service service;
        int len = services.size();

        for (int i = 0; i < len; i++) {
            service = services.get(i);
            if ( service.isRunning() ) {
                output.add(service);
            }
        }

        return output;
    }

}
