package csci366.library.project;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * This class acts as a client class for the Data class and represents a
 * simple Library program. It interacts with the user through the console
 * giving them various options that then call upon methods from Data
 * @author Brandon Snyder, Caleb Myhra
 */
public class LibraryClientOld {

    public static void main(String[] args) {

        //Error handling for PostgreSQL Exception
        try {
            
            //Establishes connection to PostgreSQL database
            Data data = new Data();

            //various variables used in running the Library console interaction
            boolean run = true;
            String option;
            int optionNum;
            String searchString;
            
            //reads user input
            Scanner scan = new Scanner(System.in);
            
            System.out.println("Welcome to the Library!");
            
            //while loop to continue prompting user for input
            while (run) {
                
                //Lists the options the user can choose from
                System.out.print("\nChoose an Option by entering the corresponding number:\n\n"
                        + "1  View All Books\n2  View Available Books\n3  View Checked Out Books\n4  Search for Books by title\n"
                        + "5  Search for Books by genre\n6  Search for Books by Author\n7  Become a Member\n8  Exit\n\nEnter Option: ");

                option = scan.next();   //gets user input
                if (isInteger(option)) {    //checks if input is int
                    optionNum = Integer.parseInt(option);
                    
                    //checks int is valid option
                    if (optionNum >= 1 && optionNum <= 8) {

                        //switch statement to run logic for each option number
                        switch (optionNum) {
                            case 1: //displays all books
                                System.out.println("All Books:\n" + ASCIITable(data.getBooks()));
                                break;
                            //displays available books then prompts user to
                            //check out a book and then asks for memberID and bookID
                            case 2:
                                try {
                                System.out.println("Available Books:\n" + ASCIITable(data.getAvailable()));
                                System.out.print("Would you like to check out a book? (y/n): ");
                                String YorN = scan.next();
                                if (YorN.equalsIgnoreCase("y")) {
                                    boolean goodInput = false;
                                    //loops until goodInput is true
                                    while (!goodInput) {
                                        System.out.print("Enter memberID: ");
                                        String memberIDStr = scan.next();
                                        System.out.print("Enter bookID: ");
                                        String bookIDStr = scan.next();
                                        if (isInteger(memberIDStr) && isInteger(memberIDStr)) {
                                            int memberID = Integer.parseInt(memberIDStr);
                                            int bookID = Integer.parseInt(bookIDStr);
                                            //attempts to check out and receives boolean for if succesful or not
                                            goodInput = data.checkOut(memberID, bookID);
                                            if (!goodInput) {
                                                System.out.println("memberID or bookID not valid, try again");
                                            }
                                        } else {
                                            System.out.println("ID's must be integers, try again");
                                        }
                                    }
                                } else {
                                    break; 
                                }
                                } catch(SQLException S) {
                                        System.out.println("\nNo Available Books at this time\n");
                                        break;
                                    }
                             
                            case 3: //displays checked out books
                                System.out.println("Checked Out Books:\n" + ASCIITable(data.getUnavailable()));
                                break;
                            case 4: //Lets user search for books by entering the title (can be partial titles)
                                System.out.print("Enter Book title: ");
                                searchString = scan.next();
                                System.out.println("");
                                System.out.println("Search by title: " + searchString + "\n" + ASCIITable(data.getByTitle(searchString)));
                                break;
                            case 5: //Lets user search for books by entering the genre (can be partial genres)
                                System.out.print("Enter Book genre: ");
                                searchString = scan.next();
                                System.out.println("");
                                System.out.println("Search by genre: " + searchString + "\n" + ASCIITable(data.getByGenre(searchString)));
                                break;
                            case 6: //Lets user search for books by entering the Author's name (can be partial author names)
                                System.out.print("Enter Author name: ");
                                searchString = scan.next();
                                System.out.println("");
                                System.out.println("Search by author: " + searchString + "\n" + ASCIITable(data.getByAuthor(searchString)));
                                break;
                            case 7: //lets user become a member by entering their info
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
                                break;
                            case 8: //this option exits the while loop and program stops
                                System.out.println("Exiting...");
                                data.close();
                                run = false;
                                break;
                        }
                    }
                    else
                        System.out.println("Number must be in range 1-8");
                }
                else
                    System.out.println("Input must be an integer");
            }
            scan.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    /**
     * Utility method that returns Boolean value for if a String input
     * is a valid int or not
     * @param input
     * @return Boolean for if valid integer
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This method creates a dynamic ASCII table from a 2D array of Strings
     * @param data
     * @return String representing the dynamic ASCII table
     */
    public static String ASCIITable(String[][] data) {
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

    /**
     * Utility method to create array representing the longest amount of
     * letters/digits in each array in a 2D array. Used in ASCII table method
     * @param data
     * @return int[] representing longest digits in each array of 2D array
     */
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
}
