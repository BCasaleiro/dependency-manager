package com.feedzai.app;

import java.io.BufferedReader;
import java.io.FileReader;

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
    private static final Hashtable<String, Integer> AVAILABLE_SERVICES = new Hashtable<String, Integer>() {{
        put("a", 0);
        put("b", 1);
        put("c", 2);
        put("d", 3);
    }};

    private static final boolean DEBUG = true;

    private ArrayList<Service> services;
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
    public void instantiateServices () {
        services = new ArrayList<Service>();
        threads = new ArrayList<Thread>();

        services.add(new ServiceA());
        services.add(new ServiceB());
        services.add(new ServiceC());
        services.add(new ServiceD());

        for (int i = 0; i < AVAILABLE_SERVICES.size(); i++) {
            threads.add(new Thread(services.get(i)));
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

        return true;
    }

    /**
    * Start Service
    * @param  services  receive list of available services
    * @param  index     receive Service index to start
    */
    public void start( int index ) {
        Service service = services.get(index);
        Iterator<Integer> dependencies;
        Integer dependencyIndex;

        dependencies = service.getDependencies();
        while (dependencies.hasNext()) {
            dependencyIndex = dependencies.next();

            if ( DEBUG ) System.out.println("[DEBUG] " + index + " depends on " + dependencyIndex + ".");
            if ( !services.get( dependencyIndex ).isRunning() ) {
                start(dependencyIndex);
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Starting service " + index + ".");
        service.setRunning(true);
        (threads.get(index)).start();
        if ( DEBUG ) System.out.println("[DEBUG] Service " + index + " started.");
    }

    /**
    * Start all Services
    * @param  services  receive list of available services
    */
    public void startAll() {
        int servicesSize = services.size();

        if ( DEBUG ) System.out.println("[DEBUG] Starting all Services.");

        for (int i = 0; i < servicesSize; i++) {
            if ( !services.get(i).isRunning() ) {
                start(i);
            }
        }
    }

    /**
    * Stop Service
    * @param  services  receive list of available services
    * @param  index     receive Service index to stop
    */
    public void stop( int index ) {
        Service service = services.get(index);
        Iterator<Integer> requirements;
        Integer requirementIndex;

        requirements = service.getRequirements();
        while (requirements.hasNext()) {
            requirementIndex = requirements.next();

            if ( DEBUG ) System.out.println("[DEBUG] " + index + " required by " + requirementIndex + ".");
            if ( services.get( requirementIndex ).isRunning() ) {
                stop(requirementIndex);
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Stopping service " + index + ".");
        service.setRunning(false);
        service.stop();
        (threads.get(index)).interrupt();
        if ( DEBUG ) System.out.println("[DEBUG] Service " + index + " stopped.");
    }

    /**
    * Stop all Services
    * @param  services  receive list of available services
    */
    public void stopAll() {
        int servicesSize = services.size();

        if ( DEBUG ) System.out.println("[DEBUG] Stopping all Services.");

        for (int i = 0; i < servicesSize; i++) {
            if ( services.get(i).isRunning() ) {
                stop(i);
            }
        }
    }

    /**
    * Kill a Service
    * @param  index  receive Service index to kill
    */
    public void kill( int index ) {
        (threads.get(index)).interrupt();
    }

}
