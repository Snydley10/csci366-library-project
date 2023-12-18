package csci366.library.project;

//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Properties;
import java.util.Scanner;

public class LibraryClient {

    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);
            LibraryUI libraryUI = new LibraryUI();

            System.out.println("Welcome to the Library!");

            while (true) {
                System.out.print("\nEnter User Type (1 for Regular User, 2 for Admin, 3 to Exit): ");
                String userType = scan.next();

                if (userType.equals("1")) {
                    // Regular User Mode
                    while (libraryUI.runLibraryInteraction(scan)) {
                        // Continue interacting with the library
                    }
                } else if (userType.equals("2")) {
                    // Admin Mode
                    System.out.print("Enter Admin Password: ");
                    String adminPassword = scan.next();
                    libraryUI.enterAdminMode(adminPassword, scan);
                        
                } else if (userType.equals("3")) {
                    System.out.println("Exiting...");
                    libraryUI.close();
                    break;
                } else {
                    System.out.println("Invalid User Type. Try again.");
                }
            }

            scan.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class LibraryUI {

    private Data data;
    Properties properties = loadProperties();
    public String adminPassword = properties.getProperty("db.adminPassword");
    private boolean adminMode;

    public LibraryUI() throws Exception {
        this.data = new Data();
    }

    public boolean runLibraryInteraction(Scanner scan) {
        try {
            System.out.print("\nChoose an Option by entering the corresponding number:\n\n"
                    + "1  View All Books\n2  View Available Books\n3  View Checked Out Books\n4  Search for Books by title\n"
                    + "5  Search for Books by genre\n6  Search for Books by Author\n7  Become a Member\n8  Exit\n\nEnter Option: ");

            String option = scan.next();

            if (isInteger(option)) {
                int optionNum = Integer.parseInt(option);
                switch (optionNum) {
                    case 1:
                        System.out.println("All Books:\n" + ASCIITable(data.getBooks()));
                        break;
                    case 2:
                        handleViewAvailable(scan);
                        break;
                    case 3:
                        System.out.println("Checked Out Books:\n" + ASCIITable(data.getUnavailable()));
                        break;
                    case 4:
                        handleSearchByTitle(scan);
                        break;
                    case 5:
                        handleSearchByGenre(scan);
                        break;
                    case 6:
                        handleSearchByAuthor(scan);
                        break;
                    case 7:
                        handleBecomeMember(scan);
                        break;
                    case 8:
                        System.out.println("Exiting...");
                        data.close();
                        return false;
                    default:
                        System.out.println("Number must be in range 1-8");
                        break;
                }
            } else {
                System.out.println("Input must be an integer");
            }

            return true;
        } catch (SQLException e) {
            System.out.println("\nError: " + e.getMessage() + "\n");
            return true; // Continue the loop in case of an error
        }
    }

    private void handleViewAvailable(Scanner scan) throws SQLException {
        System.out.println("Available Books:\n" + ASCIITable(data.getAvailable()));
        System.out.print("Would you like to check out a book? (y/n): ");
        String YorN = scan.next();
        if (YorN.equalsIgnoreCase("y")) {
            handleCheckOut(scan);
        }
    }

    private void handleCheckOut(Scanner scan) throws SQLException {
        boolean goodInput = false;

        while (!goodInput) {
            System.out.print("Enter memberID: ");
            String memberIDStr = scan.next();
            System.out.print("Enter bookID: ");
            String bookIDStr = scan.next();

            if (isInteger(memberIDStr) && isInteger(bookIDStr)) {
                int memberID = Integer.parseInt(memberIDStr);
                int bookID = Integer.parseInt(bookIDStr);
                goodInput = data.checkOut(memberID, bookID);

                if (!goodInput) {
                    System.out.println("memberID or bookID not valid, try again");
                }
            } else {
                System.out.println("ID's must be integers, try again");
            }
        }
    }

    private void handleSearchByTitle(Scanner scan) throws SQLException {
        System.out.print("Enter Book title: ");
        String searchString = scan.next();
        System.out.println("Search by title: " + searchString + "\n" + ASCIITable(data.getByTitle(searchString)));
    }

    private void handleSearchByGenre(Scanner scan) throws SQLException {
        System.out.print("Enter Book genre: ");
        String searchString = scan.next();
        System.out.println("Search by genre: " + searchString + "\n" + ASCIITable(data.getByGenre(searchString)));
    }

    private void handleSearchByAuthor(Scanner scan) throws SQLException {
        System.out.print("Enter Author name: ");
        String searchString = scan.next();
        System.out.println("Search by author: " + searchString + "\n" + ASCIITable(data.getByAuthor(searchString)));
    }

    private void handleBecomeMember(Scanner scan) throws SQLException {
        String first, last, phone, address;
        System.out.print("Enter first name: ");
        first = scan.next();
        System.out.print("Enter last name: ");
        last = scan.next();
        System.out.print("Enter phone number: ");
        phone = scan.next();
        System.out.print("Enter address: ");
        address = scan.next();
        long newID = data.newMember(first, last, phone, address);
        System.out.println("Member ID: " + newID);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("database.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it, throw a runtime exception)
        }
        return properties;
    }
    
    public boolean isAdmin(String enteredPassword) {
        return adminPassword.equals(enteredPassword);
    }

    public void enterAdminMode(String adminPassword, Scanner scan) {
        if (isAdmin(adminPassword)) {
            adminMode = true;
            // Reinitialize Data for admin mode
            try {
                this.data = new Data();
                handleAdminOptions(scan);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("\nError: " + e.getMessage() + "\n");
            }
        } else {
            System.out.println("Invalid admin password");
        }
    }
    
    public void handleAdminOptions(Scanner scan) throws SQLException {
        
        while (adminMode) {
            System.out.print("\nAdmin Options:\n"
                    + "1  View All Books\n"
                    + "2  View Checked Out Books\n"
                    + "3  View Available Books\n"
                    + "4  Add Checked Out Book\n"
                    + "5  Delete Checked Out Book\n"
                    + "6  View Members\n"
                    + "7  Add Member\n"
                    + "8  Delete Member\n"
                    + "9  Exit Admin Mode\n"
                    + "\nEnter Option: ");

            String option = scan.next();

            if (isInteger(option)) {
                int optionNum = Integer.parseInt(option);
                switch (optionNum) {
                    case 1:
                       System.out.println("All Books:\n" + ASCIITable(data.getBooks())); 
                       break;
                    case 2:
                        System.out.println("Checked Out Books:\n" + ASCIITable(data.getUnavailable()));
                        break;
                    case 3:
                        System.out.println("Available Books:\n" + ASCIITable(data.getAvailable()));
                        break;
                    case 4:
                        handleCheckOut(scan);
                        break;
                    case 5:
                        handleDeleteCheckedOutBook(scan);
                        break;
                    case 6:
                        System.out.println("Members:\n" + ASCIITable(data.getMembers()));
                        break;
                    case 7:
                        handleAddMember(scan);
                        break;
                    case 8:
                        handleDeleteMember(scan);
                        break;
                    case 9:
                        System.out.println("Exiting Admin Mode...");
                        adminMode = false;
                        break;
                    default:
                        System.out.println("Number must be in range 1-9");
                        break;
                }
            } else {
                System.out.println("Input must be an integer");
            }
        }
    }

    private void handleDeleteCheckedOutBook(Scanner scan) throws SQLException {
        System.out.print("Enter Book ID to delete from checked-out books: ");
        String bookIDStr = scan.next();

        if (isInteger(bookIDStr)) {
            int bookID = Integer.parseInt(bookIDStr);
            data.deleteCheckedOutBook(bookID);
            System.out.println("Book with ID " + bookID + " has been deleted from checked-out books.");
        } else {
            System.out.println("Invalid Book ID. Must be an integer.");
        }
    }

    private void handleAddMember(Scanner scan) throws SQLException {
        System.out.print("Enter first name: ");
        String firstName = scan.next();
        System.out.print("Enter last name: ");
        String lastName = scan.next();
        System.out.print("Enter phone number: ");
        String phoneNum = scan.next();
        System.out.print("Enter address: ");
        String address = scan.next();

        data.addMember(firstName, lastName, phoneNum, address);
        System.out.println("New member added.");
    }

    private void handleDeleteMember(Scanner scan) throws SQLException {
        System.out.print("Enter Member ID to delete: ");
        String memberIDStr = scan.next();

        if (isInteger(memberIDStr)) {
            int memberID = Integer.parseInt(memberIDStr);
            data.deleteMember(memberID);
            System.out.println("Member with ID " + memberID + " has been deleted.");
        } else {
            System.out.println("Invalid Member ID. Must be an integer.");
        }
    }
    
    private static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String ASCIITable(String[][] data) {
        // Your existing ASCIITable method
        String returnString = "";
        int[] biggest = findLongest(data);
        String rowSeperator = "+";
        for (int i = 0; i < biggest.length; i++) {           //builds row seperator
            rowSeperator += "-".repeat(biggest[i] + 4);
            rowSeperator += "+";
        }
        for (int i = 0; i < data[0].length; i++) {
            returnString += rowSeperator + "\n|  ";         //builds each row of data
            for (int j = 0; j < data.length; j++) {
                try {
                    long temp = Long.parseLong(data[j][i]);
                    returnString += String.format("%," + biggest[j] + "d", temp) + "  |  ";
                } catch (NumberFormatException nfe) {
                    returnString += String.format("%-" + biggest[j] + "s", data[j][i]) + "  |  ";
                }
            }
            returnString += "\n";
        }
        returnString += rowSeperator;
        return returnString;            //returns the table as a String
    }

    public static int[] findLongest(String[][] data) {
        int[] biggest = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            String tempBiggest = "";
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j].length() > tempBiggest.length()) {
                    tempBiggest = data[i][j];
                }
            }
            try {
                long num = Long.parseLong(tempBiggest);
                String tempStr = String.format("%,d", num);
                biggest[i] = tempStr.length();
            } catch (NumberFormatException nfe) {
                biggest[i] = tempBiggest.length();
            }
        }
        return biggest;
    }
    
    public void close() {
        if(data != null) {
            data.close();
        }
    }
}
