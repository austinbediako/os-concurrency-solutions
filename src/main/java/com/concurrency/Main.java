package com.concurrency;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int choice = -1;

            while (choice != 0) {
                System.out.println("\nOS Concurrency Simulation Menu");
                System.out.println("1. Run Producer-Consumer Simulation");
                System.out.println("2. Run Dining Philosophers Simulation");
                System.out.println("3. Run Readers-Writers Simulation");
                System.out.println("0. Exit");
                System.out.println("Enter your choice: ");

                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    choice = -1;
                    System.out.println("Invalid input. Please enter a number.");
                }

                switch (choice) {
                    case 1:
                        System.out.println("\n Producer-Consumer Simulation is not implemented yet.");
                        break;
                    case 2:
                        System.out.println("\n Dining Philosophers Simulation is not implemented yet.");
                        break;
                    case 3:
                        System.out.println("Running Readers-Writers Simulation...");
                        System.out.flush();
                        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        ReadersWriters.run(); // implemented module
                        break;
                    case 0:
                        System.out.println("Exiting program... \nGoodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }
}

