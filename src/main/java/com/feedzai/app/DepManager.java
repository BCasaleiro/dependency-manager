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

    /**
    * Basic Dependency Manager Constructor
    *
    */
    public DepManager() { }

    /**
    * Instantiate Available Services
    * @return      available services list
    */
    private ArrayList<Service> instantiateServices () {
        ArrayList<Service> services = new ArrayList<Service>();
        services.add(new ServiceA());
        services.add(new ServiceB());
        services.add(new ServiceC());
        services.add(new ServiceD());

        return services;
    }

    /**
    * Load dependencies into
    * @param  services  receive list of available services
    * @return           if there are problems in the file
    */
    private Boolean loadDependencies(ArrayList<Service> services) {
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
    private void start( ArrayList<Service> services, Integer index ) {
        Service service = services.get(index);
        Iterator<Integer> dependencies;
        Integer dependencyIndex;

        dependencies = service.getDependencies();
        while (dependencies.hasNext()) {
            dependencyIndex = dependencies.next();

            if ( DEBUG ) System.out.println("[DEBUG] " + index + " depends on " + dependencyIndex + ".");
            if ( !services.get( dependencyIndex ).isRunning() ) {
                start(services, dependencyIndex);
            }
        }

        if ( DEBUG ) System.out.println("[DEBUG] Starting service " + index + ".");
        service.setRunning(true);
        (new Thread(service)).start();
    }

    /**
    * Start all Services
    * @param  services  receive list of available services
    */
    private void startAll( ArrayList<Service> services ) {
        int servicesSize = services.size();

        if ( DEBUG ) System.out.println("[DEBUG] Starting all Services.");

        for (int i = 0; i < servicesSize; i++) {
            if ( !services.get(i).isRunning() ) {
                start(services, i);
            }
        }
    }

    private void stop( Service service ) {

    }

    private void stopAll() {

    }

    public static void main( String[] args )
    {
        DepManager dm = new DepManager();

        ArrayList<Service> services = dm.instantiateServices();
        if ( DEBUG ) System.out.println("[DEBUG] Instantiated Services.");

        if (dm.loadDependencies(services)) {
            if ( DEBUG ) System.out.println("[DEBUG] Dependencies Loaded.");
            
        } else {
            System.out.println("[ERROR] Invalid dependencies file.");
        }
    }
}
