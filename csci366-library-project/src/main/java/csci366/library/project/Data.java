package csci366.library.project;

import java.sql.*;
import java.util.Date;
import java.util.Calendar;
import java.util.Properties;
import java.io.*;

/**
 * This class retrieves data from the PostgreSQL Library database and has various
 * methods for processing the tables and views into 2D arrays
 * @author Brandon Snyder, Caleb Myhra
 */
public class Data {
    private Connection con;
    private PreparedStatement preparedStatement;
    private Statement statement;
    Properties properties = loadProperties();
    public String adminPassword = properties.getProperty("db.adminPassword");


    public Data() throws Exception {
        try {
            this.con = connectToDatabase();
            this.statement = con.createStatement();
            
        } catch (Exception e) {
            System.out.println("Error in Connecting to PostgreSQL server\n");
            e.printStackTrace();
        }
    }

    private Connection connectToDatabase() throws SQLException {
        String jdbcURL = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        

        return DriverManager.getConnection(jdbcURL, username, password);
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
    
    // Refactored method to execute a query and return a 2D array
    private String[][] executeQuery(String query, String countQuery) throws SQLException {
        // Get number of rows
        int nRow = rowCount(countQuery);

        // Execute query for table
        try (ResultSet result = statement.executeQuery(query)) {
            ResultSetMetaData rsmd = result.getMetaData();
            // Get number of columns
            int nCol = rsmd.getColumnCount();

            // Create and populate table
            return createTable(nCol, nRow, result, rsmd);
        }
    }
    
    /**
     * Utility method to get the rowCount from a ResultSet
     * @param countQuery
     * @return int Count
     * @throws SQLException 
     */
    // Refactored method to get row count from a query
    private int rowCount(String countQuery) throws SQLException {
        try (ResultSet count = statement.executeQuery(countQuery)) {
            count.next();
            return count.getInt("count");
        }
    }
    
    // Refactored method to create a 2D array from ResultSet
    private String[][] createTable(int nCol, int nRow, ResultSet result, ResultSetMetaData rsmd) throws SQLException {
        // Create array to store DB table
        String[][] table = new String[nCol][nRow + 1];

        // Populate array
        for (int i = 1; i < nCol + 1; i++) {
            table[i - 1][0] = rsmd.getColumnName(i);
        }

        int j = 1;
        while (result.next()) {
            for (int i = 1; i <= nCol; i++) {
                Object obj = result.getObject(i);
                table[i - 1][j] = (obj == null) ? null : obj.toString();
            }
            j++;
        }

        return table;
    }
    
    public String[][] getBooks() throws SQLException {
        String query = "SELECT * FROM book";
        String countQuery = "SELECT COUNT(*) FROM book";
        return executeQuery(query, countQuery);
    }

    public String[][] getAvailable() throws SQLException {
        String query = "SELECT * FROM available_books";
        String countQuery = "SELECT COUNT(*) FROM available_books";
        return executeQuery(query, countQuery);
    }

    public String[][] getUnavailable() throws SQLException {
        String query = "SELECT * FROM unavailable_books";
        String countQuery = "SELECT COUNT(*) FROM unavailable_books";
        return executeQuery(query, countQuery);
    }

    /**
     * This method uses a SQL query to get all from Book table where searchString is like the title
     * and then inserts each value into a 2D array using the createTable method
     * @param searchString
     * @return 2D array of Book where title like searchString
     * @throws SQLException 
     */
    public String[][] getByTitle(String searchString) throws SQLException {

        String query = "select * from book where lower(title) like lower('%" + searchString + "%')";
        String countQuery = "select count(*) from book where lower(title) like lower('%" + searchString + "%')";

        //get number of rows
        int nRow = rowCount(countQuery);

        //execute query for table
        ResultSet result = statement.executeQuery(query);
        ResultSetMetaData rsmd = result.getMetaData();

        //get number of columns
        int nCol = rsmd.getColumnCount();

        //create and populate table
        String[][] Table = createTable(nCol, nRow, result, rsmd);

        result.close();
        return Table;
    }

    /**
     * This method uses a SQL query to get all from Book table where searchString is like the genre
     * and then inserts each value into a 2D array using the createTable method
     * @param searchString
     * @return 2D array of Book where genre like searchString
     * @throws SQLException 
     */
    public String[][] getByGenre(String searchString) throws SQLException {

        String query = "select * from book where lower(genre) like lower('%" + searchString + "%')";
        String countQuery = "select count(*) from book where lower(genre) like lower('%" + searchString + "%')";

        //get number of rows
        int nRow = rowCount(countQuery);

        //execute query for table
        ResultSet result = statement.executeQuery(query);
        ResultSetMetaData rsmd = result.getMetaData();

        //get number of columns
        int nCol = rsmd.getColumnCount();

        //create and populate table
        String[][] Table = createTable(nCol, nRow, result, rsmd);

        result.close();
        return Table;
    }

    /**
     * This method uses a SQL query to get all from Books_By_Author view where searchString
     * is like the author_name and then inserts each value into a 2D array using the createTable method
     * @param searchString
     * @return 2D array of Books_By_Author view where author_name like searchString
     * @throws SQLException 
     */
    public String[][] getByAuthor(String searchString) throws SQLException {

        String query = "select * from books_by_author where lower(author_name) like lower('%" + searchString + "%')";
        String countQuery = "select count(*) from books_by_author where lower(author_name) like lower('%" + searchString + "%')";

        //get number of rows
        int nRow = rowCount(countQuery);

        //execute query for table
        ResultSet result = statement.executeQuery(query);
        ResultSetMetaData rsmd = result.getMetaData();

        //get number of columns
        int nCol = rsmd.getColumnCount();

        //create and populate table
        String[][] Table = createTable(nCol, nRow, result, rsmd);
        
        result.close();
        return Table;
    }

    /**
     * This method uses a SQL query to get all from Checked_Out table
     * and then inserts each value into a 2D array using the createTable method.
     * This method is used in the checkOut method.
     * @return 2D array of Checked_Out table
     * @throws SQLException 
     */
    public String[][] checkedOutTable() throws SQLException {
        String query = "select * from checked_out";
        String countQuery = "select count(*) from checked_out";

        //get number of rows
        int nRow = rowCount(countQuery);

        //execute query for table
        ResultSet result = statement.executeQuery(query);
        ResultSetMetaData rsmd = result.getMetaData();

        //get number of columns
        int nCol = rsmd.getColumnCount();

        //create and populate table
        String[][] Table = createTable(nCol, nRow, result, rsmd);

        result.close();
        return Table;
    }

    /**
     * This method inserts into the Checked_Out table to check out a book by
     * using a PreparedStatement and then executing that query if memberID and
     * bookID are valid
     * @param memberID
     * @param bookID
     * @return Boolean for if checkout method was successfully executed
     * @throws SQLException 
     */
    public boolean checkOut(int memberID, int bookID) throws SQLException {

        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); //today's date
        c.add(Calendar.DATE, 14); //Adds two weeks
        java.sql.Date due_date = new java.sql.Date(c.getTime().getTime());
        String query = "insert into checked_out (memberID, bookID, due_date) "
                + "values (?, ?, ?)";

        // create the mysql insert preparedstatement
        PreparedStatement preparedStmt = con.prepareStatement(query);
        preparedStmt.setInt(1, memberID);
        preparedStmt.setInt(2, bookID);
        preparedStmt.setDate(3, due_date);

        String[][] checkedTable = checkedOutTable();
        String[][] allBooks = getBooks();
        String[][] members = getMembers();

        boolean checked = false;
        for (int i = 1; i < checkedTable[1].length; i++) {
            int current = Integer.valueOf(checkedTable[1][i]);
            if (bookID == current) {
                checked = true;
            }
        }

        boolean bookExists = false;
        for (int i = 1; i < allBooks[0].length; i++) {
            int current = Integer.valueOf(allBooks[0][i]);
            if (bookID == current) {
                bookExists = true;
            }
        }

        boolean memberExists = false;
        for (int i = 1; i < members[0].length; i++) {
            int current = Integer.valueOf(members[0][i]);
            if (memberID == current) {
                memberExists = true;
            }
        }

        if (bookExists && memberExists && !checked) {
            preparedStmt.execute();
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method inserts into the Members table using the given parameters
     * to mimic creating a new member. It returns the generated MemberID so the
     * user can use that to check out a book
     * @param firstname
     * @param lastname
     * @param phoneNum
     * @param address
     * @return long representing the new MemberID generated
     * @throws SQLException 
     */
    public long newMember(String firstname, String lastname, String phoneNum, String address) throws SQLException {

        String query = "insert into member (firstname, lastname, phone_number, address) "
                + "values (?, ?, ?, ?)";

        // create the insert preparedstatement
        String generatedColumns[] = {"memberid"};
        PreparedStatement preparedStmt = con.prepareStatement(query, generatedColumns);
        preparedStmt.setString(1, firstname);
        preparedStmt.setString(2, lastname);
        preparedStmt.setString(3, phoneNum);
        preparedStmt.setString(4, address);

        preparedStmt.execute();

        ResultSet rs = preparedStmt.getGeneratedKeys();
        rs.next();
        long id = rs.getLong(1);
        return id;
    }

    /**
     * This method uses a SQL query to get all from Member table and then inserts
     * each value into a 2D array using the createTable method
     * @return 2D array of Member table
     * @throws SQLException 
     */
    public String[][] getMembers() throws SQLException {
        String query = "select * from member";
        String countQuery = "select count(*) from member";

        //get number of rows
        int nRow = rowCount(countQuery);

        //execute query for table
        ResultSet result = statement.executeQuery(query);
        ResultSetMetaData rsmd = result.getMetaData();

        //get number of columns
        int nCol = rsmd.getColumnCount();

        //create and populate table
        String[][] Table = createTable(nCol, nRow, result, rsmd);

        result.close();
        return Table;
    }
    

    public boolean isAdmin(String enteredPassword) {
        return adminPassword.equals(enteredPassword);
    }

    public void deleteCheckedOutBook(int bookID) throws SQLException {
        String query = "DELETE FROM checked_out WHERE bookID = ?";
        
        // Create the prepared statement
        try (PreparedStatement preparedStmt = con.prepareStatement(query)) {
            preparedStmt.setInt(1, bookID);
            
            // Execute the delete operation
            preparedStmt.executeUpdate();
        }
    }
    
    public void addMember(String firstname, String lastname, String phoneNum, String address) throws SQLException {
        String query = "INSERT INTO member (firstname, lastname, phone_number, address) VALUES (?, ?, ?, ?)";

        
         // Create the prepared statement
         try (PreparedStatement preparedStmt = con.prepareStatement(query)) {
            preparedStmt.setString(1, firstname);
            preparedStmt.setString(2, lastname);
            preparedStmt.setString(3, phoneNum);
            preparedStmt.setString(4, address);

            // Execute the insert operation
            preparedStmt.executeUpdate();
        }
    }

    public void deleteMember(int memberId) throws SQLException {
        String query = "DELETE FROM member WHERE memberID = ?";

        // Create the prepared statement
        try (PreparedStatement preparedStmt = con.prepareStatement(query)) {
            preparedStmt.setInt(1, memberId);

            // Execute the delete operation
            preparedStmt.executeUpdate();
        }
    }
        
    /**
     * Utility method to close the Statement and Connection instance
     */
    public void close() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (e.g., log it)
        }
    }
}
