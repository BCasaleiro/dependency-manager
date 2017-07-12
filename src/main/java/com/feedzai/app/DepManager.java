package com.feedzai.app;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.Hashtable;

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
        put("a", 1);
        put("b", 2);
        put("c", 3);
        put("d", 4);
    }};

    /**
    * Basic Dependency Manager Constructor
    */
    public DepManager() { }

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
    * @return      if there are problems in the file
    */
    private Boolean loadDependencies() {
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

    private void start( Service service ) {

    }

    private void startAll() {

    }

    private void stop( Service service ) {

    }

    private void stopAll() {

    }

    public static void main( String[] args )
    {
        DepManager dm = new DepManager();

        if (dm.loadDependencies()) {
            System.out.println("[DEBUG] Dependencies Loaded.");
            ArrayList services = instantiateServices();
            System.out.println("[DEBUG] Instantiated Services.");
            
        } else {
            System.out.println("[ERROR] Invalid dependencies file.");
        }
    }
}
