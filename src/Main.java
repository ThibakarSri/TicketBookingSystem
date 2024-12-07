
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String filename = "configs.json";

        // Declared the ArrayList data structure to store the configurations
        List<Configuration > configurations = new ArrayList<>();

        // Load existing configurations
        System.out.println("Load existing configurations? (Yes/No):");
        String loadConfig = input.nextLine().trim().toLowerCase();
        if (loadConfig.equals("yes")) {
            try {
                configurations = Configuration .loadConfig(filename, gson);
                System.out.println("Available configurations:");
                for (int i = 0; i < configurations.size(); i++) {
                    System.out.println((i + 1) + ": " + configurations.get(i));
                }
                System.out.println("Select a configuration to load (1-" + configurations.size() + "):");
                int choice = Integer.parseInt(input.nextLine().trim());
                if (choice >= 1 && choice <= configurations.size()) {
                    Configuration  selectedConfig = configurations.get(choice - 1);
                    System.out.println("Loaded configuration:\n" + selectedConfig);
                } else {
                    System.out.println("Invalid choice. Starting fresh.");
                }
            } catch (Exception e) {
                System.out.println("Failed to load configurations. Starting fresh.");
            }
        }

        // Create a new configuration
        Configuration  newConfig = new Configuration ();
        System.out.println("Please configure the system:");
        newConfig.setTotalTickets(promptInt(input, "Enter the total number of tickets:", 1,1000));
        newConfig.setTicketReleaseRate(promptInt(input, "Enter the ticket release rate (seconds):", 1,10));
        newConfig.setCustomerRetrievalRate(promptInt(input, "Enter the customer retrieval rate (seconds):", 1,10));
        newConfig.setMaxTicketCapacity(promptInt(input, "Enter the maximum ticket capacity:", 1,1000));

        configurations.add(newConfig);

        // Save configurations
        try {
            Configuration .saveConfig(configurations, filename, gson);
            System.out.println("Configuration saved successfully.");
        } catch (Exception e) {
            System.out.println("Failed to save configuration: " + e.getMessage());
        }

        // Get the number of vendors and customers
        int numVendors = promptInt(input, "Enter the number of vendors:", 1 ,10);
        int numCustomers = promptInt(input, "Enter the number of customers:", 1,10);

        // Get the maximum tickets each customer can buy
        int maxTicketsPerCustomer = promptInt(input, "Enter the maximum number of tickets each customer can buy:", 1, newConfig.getTotalTickets());

        // Initialize TicketPool
        TicketPool ticketPool = new TicketPool(newConfig.getMaxTicketCapacity());

        // Create threads for vendors and customers
        List<Thread> threads = new ArrayList<>();

        // Create and start vendor threads
        for (int i = 1; i <= numVendors; i++) {
            Thread vendorThread = new Thread(new Vendor(
                    newConfig.getTotalTickets()/ numVendors, // Divide total tickets among vendors
                    newConfig.getTicketReleaseRate(),
                    ticketPool), "Vendor-" + i);
            threads.add(vendorThread);
            vendorThread.start();
        }

        // Create and start customer threads

        for (int i = 1; i <= numCustomers; i++) {
            Thread customerThread = new Thread(new Customer(
                    ticketPool,
                    newConfig.getCustomerRetrievalRate(),
                    maxTicketsPerCustomer), // Divide total purchases among customers
                    "Customer-" + i);
            threads.add(customerThread);
            customerThread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread execution interrupted: " + e.getMessage());
            }
        }
        System.out.println("Ticketing system execution completed.");
    }

    private static int promptInt(Scanner input, String message, int min ,int max) {
        int value;
        while (true) {
            System.out.print(message + " ");
            try {
                value = Integer.parseInt(input.nextLine().trim());
                if (value >= 1 && value <= max) break;
                else System.out.println("Value must be between " + 1 + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
        return value;
    }
}


//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//        Scanner input = new Scanner(System.in);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String filename = "config.json";
//
//        // Configuration Initialization
//        List<Configuration > configurations  = new ArrayList<>();
//
//        // Load existing configuration
//        System.out.println("Load existing configuration? (yes/no):");
//        String loadConfig = input.nextLine().trim().toLowerCase();
//
//        if (loadConfig.equals("yes")) {
//            try {
//                configurations = Configuration .loadConfig("config.json", gson);
//                System.out.println("Available configurations:");
//                for(int i = 0; i < configurations.size(); i++) {
//                    System.out.println((i+1)+ ": " + configurations.get(i));
//                }
//                System.out.println("Select a configuration to load (1-" + configurations.size() + "):");
//                int choice = Integer.parseInt(input.nextLine().trim());
//                if (choice >= 1 && choice <= configurations.size()) {
//                    Configuration  selectedConfig = configurations.get(choice - 1);
//                    System.out.println("Loaded configuration:\n" + selectedConfig);
//                } else {
//                    System.out.println("Invalid choice. Starting fresh.");
//                }
//            } catch (Exception e) {
//                System.out.println("Failed to load configuration. Starting fresh.");
//            }
//        }
//
//        // Create a new configurations
//        Configuration  newConfig = new Configuration ();
//        System.out.println("Please configure the system:");
//        newConfig.setTotalTickets(promptInt(input, "Enter total number of tickets:", 1, 1000));
//        newConfig.setTicketReleaseRate(promptInt(input, "Enter ticket release rate (seconds):", 1, 10));
//        newConfig.setCustomerRetrievalRate(promptInt(input, "Enter customer retrieval rate (seconds):", 1, 10));
//        newConfig.setMaxTicketCapacity(promptInt(input, "Enter maximum ticket capacity:", 1, 1000));
//
//        configurations.add(newConfig);
//
//        // Save Configuration
//        try {
//            Configuration .saveConfig(configurations,"config.json", gson);
//            System.out.println("Configuration saved successfully.");
//        } catch (Exception e) {
//            System.out.println("Failed to save configuration: " + e.getMessage());
//        }
//
//        // Initialize TicketPool
////        TicketPool ticketPool = new TicketPool(newConfig.getMaxTicketCapacity());
////
////        // Start Vendors and Customers
////        Thread vendorThread = new Thread(new Vendor(
////                newConfig.getTotalTickets(),
////                newConfig.getTicketReleaseRate(),
////                ticketPool), "Vendor-1");
////
////        Thread customerThread = new Thread(new Customer(
////                ticketPool,
////                newConfig.getCustomerRetrievalRate(),
////                newConfig.getTotalTickets() / 2), "Customer-1");
////
////        // Start Threads
////        vendorThread.start();
////        customerThread.start();
////
////        try {
////            vendorThread.join();
////            customerThread.join();
////        } catch (InterruptedException e) {
////            System.out.println("Thread execution interrupted: " + e.getMessage());
////        }
////
////        System.out.println("Ticketing system execution completed.");
////    }
//
//        // Initialize TicketPool
//        TicketPool ticketPool = new TicketPool(newConfig.getMaxTicketCapacity());
//
//    // Create a list to hold vendor and customer threads
//        List<Thread> threads = new ArrayList<>();
//
//// Create and start multiple vendor threads
//        int numVendors = 3; // Number of vendors
//        for (int i = 1; i <= numVendors; i++) {
//            Thread vendorThread = new Thread(new Vendor(
//                    newConfig.getTotalTickets() / numVendors, // Divide tickets among vendors
//                    newConfig.getTicketReleaseRate(),
//                    ticketPool), "Vendor-" + i);
//            threads.add(vendorThread);
//            vendorThread.start();
//        }
//
//// Create and start multiple customer threads
//        int numCustomers = 5; // Number of customers
//        for (int i = 1; i <= numCustomers; i++) {
//            Thread customerThread = new Thread(new Customer(
//                    ticketPool,
//                    newConfig.getCustomerRetrievalRate(),
//                    newConfig.getTotalTickets() / (2 * numCustomers)), // Divide purchase attempts
//                    "Customer-" + i);
//            threads.add(customerThread);
//            customerThread.start();
//        }
//
//// Wait for all threads to finish
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                System.out.println("Thread execution interrupted: " + e.getMessage());
//            }
//        }
//
//        System.out.println("Ticketing system execution completed.");
//
//
//        private static int promptInt(Scanner input, String message, int min, int max) {
//        int value;
//        while (true) {
//            System.out.print(message + " ");
//            try {
//                value = Integer.parseInt(input.nextLine().trim());
//                if (value >= min && value <= max) break;
//                else System.out.println("Value must be between " + min + " and " + max + ".");
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input. Please enter a valid integer.");
//            }
//        }
//        return value;
//    }
//}
//}

