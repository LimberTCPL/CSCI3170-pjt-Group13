//package CSCI3170_tutor.java;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Scanner;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class Salesperson {
 //Choice: Main Menu move
 //A_Choice_1:Administrator move 1
 //S_Choice_1:Salesperdon move 1   
    
    public static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db13?autoReconnect=true&useSSL=false";
        public static String dbUsername = "Group13";
        public static String dbPassword = "CSCI3170";
        
        

        public static Connection connectToMySQL() throws IOException{
                Connection con = null;
                try{
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
                        
                } catch (ClassNotFoundException e){
                        System.out.println("[Error]: Java MySQL DB Driver not found!!");
                        System.exit(0);
                } catch (SQLException e){
                        System.out.println(e);
                }
                return con;
        }
            
        public static void main(String[] args) {
            try {
                Connection conn = connectToMySQL();
                salesperson_operation(conn);
                conn.close();
            } catch (IOException e) {
                System.out.println("An error occurred while connecting to the database: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("An SQL error occurred: " + e.getMessage());
            }
        }

    public static void salesperson_operation(Connection conn) throws SQLException  {
        int input;
        System.out.println("\n-----Operations for salesperson menu-----");
        System.out.println("What kind of operation would you like to perform? ");
        System.out.println("1. Search for parts");
        System.out.println("2. Sell a part");
        System.out.println("3. Return to the main menu:");
        Scanner scan = new Scanner(System.in);
        do {
            System.out.print("Enter Your Choice: ");
            input = scan.nextInt();
        } while (input < 1 || input > 3);
        if (input == 1)
            partsearching(conn);
        else if (input == 2)
            partselling(conn);
        else if (input == 3)
            salesperson_operation(conn);
        
        scan.close();    
    }

    public static void partsearching(Connection conn)   {
        int p_input;
        int order;
        String ordering;
        String keyword;
        System.out.println("Choose the Search criterion:");
        System.out.println("1. Part Name");
        System.out.println("2. Manufacturer Name");
        Scanner scan = new Scanner(System.in);
        do {
            System.out.print("Enter Your Choice: ");
            p_input = scan.nextInt();
        } while (p_input < 1 || p_input > 2);
        scan.nextLine(); // Consume the newline character
        System.out.println("Type in the Search Keyword:");
        keyword = scan.nextLine();
        System.out.println("Choose ordering:");
        System.out.println("1. By price, ascending order");
        System.out.println("2. By price, descending order");
        do {
            System.out.print("Enter Your Choice: ");
            order = scan.nextInt();
        } while (order < 1 || order > 2);
        
        try {
            if(order == 1){
                ordering = "ASC";
            }else{
                ordering = "DESC";
            }
            String sqlStatement;
            PreparedStatement pstmt;
            if (p_input == 1) {
                sqlStatement = "SELECT * " + 
                "FROM part " +
                "JOIN category " + 
                "ON part.cID = category.cID " +
                "JOIN manufacturer " +
                "ON manufacturer.mID = part.mID " +
                "WHERE part.pName LIKE ? " +
                "ORDER BY part.pPrice " + ordering ;
                pstmt = conn.prepareStatement(sqlStatement);
                pstmt.setString(1, "%" + keyword + "%");
                ResultSet resultSet = pstmt.executeQuery();
                boolean hasRecords = false;
                while (resultSet.next()){
                    hasRecords = true;
                    int pID = resultSet.getInt("pID");
                    String pName = resultSet.getString("pName");
                    int pPrice = resultSet.getInt("pPrice");
                    int cID = resultSet.getInt("cID");
                    int mID = resultSet.getInt("mID");
                    String mName = resultSet.getString("mName");
                    String cName = resultSet.getString("cName");
                    int pWarrantyPeriod = resultSet.getInt("pWarrantyPeriod");
                    int pAvailableQuantity = resultSet.getInt("pAvailableQuantity");
                    System.out.println("| " + pID + " | " + pName + " | " + mName + " | " + cName + " | " + pAvailableQuantity + " | " + pWarrantyPeriod + " | " + pPrice + " |");
                }
                if (!hasRecords) {
                        System.out.println("No matching records found.");
                    }

            } else {
                sqlStatement = "SELECT * " + 
                "FROM part " +
                "JOIN category " + 
                "ON part.cID = category.cID " +
                "JOIN manufacturer " +
                "ON manufacturer.mID = part.mID " +
                "WHERE manufacturer.mName LIKE ? " +
                "ORDER BY part.pPrice " + ordering ;
                pstmt = conn.prepareStatement(sqlStatement);
                pstmt.setString(1, "%" + keyword + "%");
                ResultSet resultSet = pstmt.executeQuery();
                boolean hasRecords = false;
                    while (resultSet.next()) {
                        hasRecords = true;
                        int pID = resultSet.getInt("pID");
                        String pName = resultSet.getString("pName");
                        int pPrice = resultSet.getInt("pPrice");
                        int cID = resultSet.getInt("cID");
                        int mID = resultSet.getInt("mID");
                        String mName = resultSet.getString("mName");
                        String cName = resultSet.getString("cName");
                        int pWarrantyPeriod = resultSet.getInt("pWarrantyPeriod");
                        int pAvailableQuantity = resultSet.getInt("pAvailableQuantity");
                        System.out.println("| " + pID + " | " + pName + " | " + mName + " | " + cName + " | " + pAvailableQuantity + " | " + pWarrantyPeriod + " | " + pPrice + " |");
                    }
                    if (!hasRecords) {
                        System.out.println("No matching records found.");
                    }
            }
            
        } catch (Exception exp) { 
            System.out.println("[Error]: A matching search record is not found. The input does not exist in the database.");
            exp.printStackTrace();
        }
        scan.close();
    }


      public static void partselling(Connection conn) {
        int part_id;
        int sales_id;

        System.out.println("Enter the part ID:");
        Scanner scan = new Scanner(System.in);
        part_id = scan.nextInt();
        System.out.println("Enter the Salesperson ID:");
        sales_id = scan.nextInt();

        try {
            String sqlStatement_check = "SELECT  * "+
            "FROM part " +
            "JOIN category "+
            "ON part.cID= category.cID "+
            "JOIN manufacturer " +
            "ON manufacturer.mID = part.mID " +
            "WHERE part.pID=?";

            PreparedStatement pstmt_check = conn.prepareStatement(sqlStatement_check);
            pstmt_check.setInt(1, part_id);

            ResultSet resultSet = pstmt_check.executeQuery();

            if (resultSet.next()) {
                int pID = resultSet.getInt("pID");
                String pName = resultSet.getString("pName");
                int pAvailableQuantity = resultSet.getInt("pAvailableQuantity");
                if (pAvailableQuantity > 0) {
                    String sqlStatement_sell = "UPDATE part SET " +
                    "part.pAvailableQuantity = part.pAvailableQuantity - 1 " +
                    "WHERE part.pID = ? ";
                    PreparedStatement pstmt_sell = conn.prepareStatement(sqlStatement_sell);
                    pstmt_sell.setInt(1, part_id);
                    pstmt_sell.executeUpdate();

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    LocalDate localDate = LocalDate.now();


                    /* Insert record */
                    String tDate='2016-10-21';
                    String sqlStatement_trans = "INSERT INTO transaction (tID, pID, sID, tDate) " + 
                                                " VALUES (?, ?, ?, ? ) ";
                    PreparedStatement pstmt_trans = conn.prepareStatement(sqlStatement_trans);
                    //@Column (name = "tDate");
                    pstmt_trans.setInt(1, lastID);
                    pstmt_trans.setInt(2, part_id);
                    pstmt_trans.setInt(3, sales_id);
                    pstmt_trans.setString(4, "'" + localDate + "'" );
                    pstmt_trans.executeUpdate();

                    System.out.println("Product:" + pName + "(id:" + pID + ") Remaining Quality:" + pAvailableQuantity );
                } else {
                    System.out.println("[Error]: The Part (part_id: " + part_id +") has been sold out!");
                }
            } else {
                System.out.println("[Error]: The Part (part_id: " + part_id +") does not exist!");
            }
        } catch (Exception exp) {
            System.out.println("Part sell failed to perform!!");
            System.out.println("Error: " + exp);
            
        }
        scan.close();
    }
}
