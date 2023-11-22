//package Manager
import java.io.*;
import java.rmi.ConnectIOException;
import java.sql.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class Manager {
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


    public static void Manager(String[] args) throws IOException{
        Connection conn = connectToMySQL();
        System.out.println("-----Operations for manager menu-----");
        System.out.println("What kinds of opearion would you like to perform?");
        System.out.println("1. List all salesperson");
        System.out.println("2. Count the no. of sales record of each salesperson under a specific range on years of experience");
        System.out.println("3. Show the total sales value of each manufacturer");
        System.out.println("4. Show the N most popular part");
        System.out.println("5. Return to the main menu");
        System.out.print("Enter Your Choice: ");
        BufferedReader nochoice = new BufferedReader(new InputStreamReader(System.in));
        int manchoice = Integer.parseInt(nochoice.readLine());
        switch (manchoice) {
            case 1:{
                Listsalesperson(conn);
                break;
            }
            case 2:{
                Countno(conn);
                break;
            }
            case 3:{
                showtotalsales(conn);
                break;
            }
            case 4:{
                Nmostpopular(conn);
                break;
            }
            case 5:{
                System.out.println("Enter 6");
                //call fcn return main menu
                break;
            }
            default:{
                System.err.println("[Error]: Input Choice invaild!\nPlease Enter again");
            }
        }
        }

//fucntion
public static void Listsalesperson(Connection conn) throws IOException{
    int ordering;
    int listingCount = 0;
    Scanner scan = new Scanner(System.in);
    System.out.print("Choose ordering:");
    System.out.println("1. By ascending order");
    System.out.println("2. By descending order");
    do{
        System.out.print("Choose the list ordering: ");
        ordering = scan.nextInt();;
        } while (ordering < 1 || ordering > 2);
    Connection con =null;
    try{
        String order;
        if(ordering == 1){
            order = "ASC";
        }else{
            order = "DESC";
        }
        Statement stmt = con.createStatement();
        String tablecontent = "SELECT * FROM Salesperson ORDER BY sExperience" + order;
        ResultSet resultSet = stmt.executeQuery(tablecontent);
        //stuck
        //stuck
        //stuck
        
    }
    
    catch (SQLException e) 
        {
            System.err.println("Fail! Cannot connect to Database!\n");
            e.printStackTrace();
        }
            finally {
            System.out.println("Closing the connection.");
            if (con != null) try { con.close(); } catch (SQLException ignore) {}
        }
        scan.close();
}

public static void Countno(Connection conn) throws IOException{
    int lowerbound;
    int upperbound;    
    int column = 0;
    int returnCount = 0;
    try{
      String sqlStatement = "SELECT COUNT(*) FROM salesperson";
      PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
      ResultSet rs = pstmt.executeQuery();
      // Move cursor to data
      rs.next();
      column = rs.getInt("count(*)");
    }
    catch (Exception ex){

    }
    
    String[][] resultsInRange = new String[column][4];
    System.out.print("Type in the lower bound for years of experience: ");
    Scanner scan = new Scanner(System.in);
    lowerbound = scan.nextInt();
    System.out.print("Type in the upper bound for years of experience: ");
    upperbound = scan.nextInt();

    Connection con = connectToMySQL() ;
    /*Get the data in range */
    try{
        Statement stmt = con.createStatement();
        String countno = "SELECT sID, sName, sExperience, COUNT(tID) AS NumberofTran FROM Salesperson S, Transaction T "
                + "WHERE S.sID = T.sID GROUP BY S.sID, sName, sExperience ORDER BY S.sID DESC";
        ResultSet resultset = stmt.executeQuery(countno);

        /*Print the result to console */
        System.out.println("| ID | Name | Year of Experience | Number of Tranaction |");
        Boolean hasResult = false;
        while(resultset.next()){
            int yeartocheck = resultset.getInt("sExperience");
            Boolean checkDate = yeartocheck <= upperbound;
            checkDate = checkDate && (yeartocheck >= lowerbound);
            if(checkDate){
                hasResult = true;
                resultsInRange[returnCount][0] = resultset.getString("sID");
                resultsInRange[returnCount][1] = resultset.getString("sName");
                resultsInRange[returnCount][2] = resultset.getString("sExperience");
                resultsInRange[returnCount][3] = resultset.getString("NumberofTran");  
                returnCount += 1;
            }
        }
        for(int i =0; i< returnCount; i++){
            System.out.println("| " + resultsInRange[i][0] + " | " + resultsInRange[i][1] + " | " + resultsInRange[i][2] + " | " + resultsInRange[i][3] + " | ");
        }
    }
    catch (SQLException e) 
        {
            System.err.println("Cannot connect ! ");
            e.printStackTrace();
        }
    finally {
            System.out.println("Closing the connection.");
            if (con != null) try { con.close(); } catch (SQLException ignore) {}
        }
        scan.close();
    }
    

public static void showtotalsales(Connection conn) throws IOException{

}


public static void Nmostpopular(Connection conn) throws IOException{
    
}


public static void main(String[] args) throws IOException{
    manager_operations();
}


}

