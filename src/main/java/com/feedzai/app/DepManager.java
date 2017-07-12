package com.feedzai.app;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


import java.io.IOException;

/**
 * Dependency Manager
 *
 */
public class DepManager
{
    private final static String FILE = "dependencies.csv";
    private final static String DELIMITER = " ";
    private static final Set<String> AVAILABLE_SERVICES = new HashSet<String>(Arrays.asList( new String[] {"a","b","c","d"} ));

    /**
    * Basic Dependency Manager Constructor
    */
    public DepManager() { }

    /**
    * Load dependencies into
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

                if ( AVAILABLE_SERVICES.contains(dependency[0]) && AVAILABLE_SERVICES.contains(dependency[1]) ) {

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

        } else {
            System.out.println("[ERROR] Invalid dependencies file.");
        }
    }
}
