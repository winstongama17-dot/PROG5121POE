/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.loginsystem;

/**
 *
 * @author Student
 */
import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// ==================== MAIN APPLICATION ====================
public class LoginSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, User> userDatabase = new TreeMap<>();
    private static final List<Message> sentMessages = new ArrayList<>();
    private static User loggedInUser = null;
    private static final JFrame dialogOwner = buildInvisibleFrame();

    public static void main(String[] args) {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> registerUser();
                case "2" -> { if (loginUser()) launchChat(); }
                case "3" -> {
                    running = false;
                    System.out.println("\nGoodbye! Thank you for using LoginSystem.");
                }
                default -> System.out.println("\n[ERROR] Invalid option. Please enter 1, 2, or 3.");
            }
        }
        dialogOwner.dispose();
    }

    private static JFrame buildInvisibleFrame() {
        JFrame frame = new JFrame();
        frame.setSize(0, 0);
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(false);
        return frame;
    }

    private static void printBanner() {
        System.out.println("*************************************************");
        System.out.println("*              LOGIN SYSTEM APP                 *");
        System.out.println("*************************************************");
    }

    private static void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("\nChoice (1-3): ");
    }

    private static void registerUser() {
        System.out.println("\n--- REGISTER ---");

        String username = prompt("Username: ");
        String password = prompt("Password: ");
        String phone    = prompt("Phone (e.g. +2717563370): ");
        String firstName = prompt("First name: ");
        String lastName  = prompt("Last name: ");

        Validator v = new Validator();

        if (!v.isValidUsername(username)) {
            System.out.println("[ERROR] Username must contain '_' and be 5 or fewer characters.");
            return;
        }
        if (!v.isValidPassword(password)) {
            System.out.println("[ERROR] Password must be 8+ characters with uppercase, digit, and special character.");
            return;
        }
        if (!v.isValidPhone(phone)) {
            System.out.println("[ERROR] Phone must start with '+' and contain max 10 digits.");
            return;
        }
        if (userDatabase.containsKey(username)) {
            System.out.println("[ERROR] Username already taken.");
            return;
        }

        userDatabase.put(username, new User(username, password, phone, firstName, lastName));
        System.out.println("\n[SUCCESS] Account created!");
    }

    private static boolean loginUser() {
        System.out.println("\n--- LOGIN ---");
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        User user = userDatabase.get(username);
        if (user != null && user.authenticate(username, password)) {
            loggedInUser = user;
            System.out.println("\n[SUCCESS] Welcome, " + user.getFirstName() + "!");
            return true;
        }
        System.out.println("[ERROR] Incorrect username or password.");
        return false;
    }

    private static void launchChat() {
        System.out.println("\n=== QuickChat ===");
        int limit = readPositiveInt("How many messages would you like to send? ");
        if (limit <= 0) return;

        Message.resetCount();

        boolean active = true;
        while (active) {
            System.out.println("\n1. Send Message\n2. View Messages\n3. Quit");
            System.out.print("Choice: ");

            switch (scanner.nextLine()) {
                case "1" -> composeMessage(limit);
                case "2" -> showMessages();
                case "3" -> active = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void composeMessage(int limit) {
        if (Message.getSentCount() >= limit) {
            System.out.println("Limit reached.");
            return;
        }

        String recipient = prompt("Recipient: ");
        String body = prompt("Message: ");

        Message msg = new Message(recipient, body);

        if (body.length() > 250) {
            System.out.println("Message too long.");
            return;
        }

        sentMessages.add(msg);
        Message.incrementCount();
        System.out.println("Message sent!");
    }

    private static void showMessages() {
        for (Message m : sentMessages) {
            System.out.println(m.getId() + " -> " + m.getBody());
        }
    }

    private static String prompt(String text) {
        System.out.print(text);
        return scanner.nextLine();
    }

    private static int readPositiveInt(String label) {
        System.out.print(label);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}

// ==================== USER ====================
class User {
    private final String username, password, phone, firstName, lastName;

    public User(String u, String p, String ph, String f, String l) {
        username = u; password = p; phone = ph; firstName = f; lastName = l;
    }

    public boolean authenticate(String u, String p) {
        return username.equals(u) && password.equals(p);
    }

    public String getFirstName() { return firstName; }
}

// ==================== VALIDATOR ====================
class Validator {
    public boolean isValidUsername(String u) {
        return u.contains("_") && u.length() <= 5;
    }

    public boolean isValidPassword(String p) {
        return p.length() >= 8;
    }

    public boolean isValidPhone(String ph) {
        return ph.startsWith("+");
    }
}

// ==================== MESSAGE ====================
class Message {
    private final String id, recipient, body;
    private static int count = 0;

    public Message(String r, String b) {
        id = UUID.randomUUID().toString().substring(0, 8);
        recipient = r;
        body = b;
    }

    public static void incrementCount() { count++; }
    public static int getSentCount() { return count; }
    public static void resetCount() { count = 0; }

    public String getId() { return id; }
    public String getBody() { return body; }
}