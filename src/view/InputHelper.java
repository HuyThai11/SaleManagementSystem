package view;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readLine(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public static int readInt(String message) {
        while (true) {
            try {
                return Integer.parseInt(readLine(message));
            } catch (NumberFormatException e) {
                System.out.println("Please enter an integer.");
            }
        }
    }

    public static double readDouble(String message) {
        while (true) {
            try {
                return Double.parseDouble(readLine(message));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
