package com.feedzai.app;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Hashtable;

/**
 * Dependency Console
 *
 */
public class DepManagerConsole
{
    private static final boolean DEBUG = false;
    private static final boolean TESTA = false;
    private static final String DELIMITER = " ";
    private static final String MONITOR = "MASTER";

    /**
     * Print Console Menu
     *
     */
    private static void printMenu()
    {
        System.out.println("MENU:");
        System.out.println("start [service]");
        System.out.println("startall");
        System.out.println("stop [service]");
        System.out.println("stopall");
        System.out.println("kill [service]");
        System.out.println("info");
        System.out.println("exit");
        System.out.println("|----------------------------------|");
    }

    /**
     * List all the currently running Services
     * @param runningServices list of running services
     */
    private static void printRunningServices( ArrayList<Service> runningServices )
    {
        int len = runningServices.size();

        System.out.println("|-----------LIST-----------|");
        for (int i = 0; i < len; i++) {
            System.out.println("ID: " + runningServices.get(i).getId());
        }
        System.out.println("|--------------------------|");
    }

    /**
     * Main method
     * @param args call arguments
     */
    public static void main( String[] args )
    {
        DepManager dm;
        Scanner scanner;
        String aux;
        String[] cmd;
        ArrayList<Service> services;
        Hashtable<String, Integer> availableServices;

        if (TESTA) {
            dm = new DepManager(MONITOR, "dependencies.csv");

            availableServices = new Hashtable<String, Integer>() {{
                put("a", 0);
                put("b", 1);
                put("c", 2);
                put("d", 3);
            }};

            services = new ArrayList<Service>();
            for (int i = 0; i < 4; i++) {
                services.add(new ServiceDummy(i, MONITOR));
            }
        } else {
            dm = new DepManager(MONITOR, "dependencies1.csv");

            availableServices = new Hashtable<String, Integer>() {{
                put("a", 0);
                put("b", 1);
                put("c", 2);
                put("d", 3);
                put("e", 4);
                put("f", 5);
                put("k", 6);
                put("i", 7);
            }};

            services = new ArrayList<Service>();
            for (int i = 0; i < 8; i++) {
                services.add(new ServiceDummy(i, MONITOR));
            }
        }


        dm.instantiateServices(services, availableServices);
        if ( DEBUG ) System.out.println("[DEBUG] Instantiated Services.");

        if (dm.loadDependencies()) {
            if ( DEBUG ) System.out.println("[DEBUG] Dependencies Loaded.");

            printMenu();

            scanner = new Scanner(System.in);
            while (!(aux = scanner.nextLine()).equals("exit")) {
                cmd = aux.split(DELIMITER);
                if( cmd[0].equals("start") ) {
                    if ( availableServices.containsKey(cmd[1]) ) {
                        dm.start( availableServices.get(cmd[1]), true );
                    } else {
                        System.out.println("Not an available Service.");
                    }
                } else if ( cmd[0].equals("startall") ) {
                    dm.startAll();
                } else if ( cmd[0].equals("stop") ) {
                    if ( availableServices.containsKey(cmd[1]) ) {
                        dm.stop( availableServices.get(cmd[1]), true );
                    } else {
                        System.out.println("Not an available Service.");
                    }
                } else if ( cmd[0].equals("stopall") ) {
                    dm.stopAll();
                } else if ( cmd[0].equals("kill") ) {
                    if ( availableServices.containsKey(cmd[1]) ) {
                        dm.kill( availableServices.get(cmd[1]) );
                    } else {
                        System.out.println("Not an available Service.");
                    }
                } else if ( cmd[0].equals("info") ) {
                    printMenu();
                } else if ( cmd[0].equals("list") ) {
                    printRunningServices( dm.getRunningServices() );
                } else {
                    System.out.println("Not a valid command.");
                }
            }

            if ( DEBUG ) System.out.println("[DEBUG] Cleaning Up.");
            dm.stopAll();
        } else {
            System.out.println("[ERROR]");
        }
    }
}
