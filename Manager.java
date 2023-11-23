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
    
    public static Connection connectToMySQL(){
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

//Manager menu
    public static void manager(String[] args) throws IOException{
        Connection conn = connectToMySQL();
        int man=1;
        while(man==1){
        System.out.println("----Operations for manager menu----");
        System.out.println("What kinds of opearion would you like to perform?");
        System.out.println("1. List all salesperson");
        System.out.println("2. Count the no. of sales record of each salesperson under a specific range on years of experience");
        System.out.println("3. Show the total sales value of each manufacturer");
        System.out.println("4. Show the N most popular part");
        System.out.println("5. Return to the main menu");
        System.out.printf("Enter Your Choice: ");
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
                Showtotalsales(conn);
                break;
            }
            case 4:{
                Nmostpopular(conn);
                break;
            }
            case 5:{
                System.out.println("Enter 6");
                mainmenu.main(args);
                break;
            }
            default:{
                System.err.println("[Error]: Input Choice invaild!\nPlease Enter again");
                
            }
        }}
}

//fucntion
public static void Listsalesperson(Connection conn) throws IOException{
    int ordering;
    int listingCount = 0;
    String order;
    Scanner scan = new Scanner(System.in);
    System.out.println("Choose ordering:");
    System.out.println("1. By ascending order");
    System.out.println("2. By descending order");
    do{
        System.out.printf("Choose the list ordering: ");
        ordering = scan.nextInt();;
        } while (ordering < 1 || ordering > 2);
    Connection con = connectToMySQL();
    try{
        if(ordering == 1){
            order = "ASC";
        }else{
            order = "DESC";
        }
        Statement stmt = con.createStatement();
        String tablecontent = "SELECT * FROM salesperson ORDER BY sExperience " + order;
        ResultSet resultSet = stmt.executeQuery(tablecontent);
        System.out.println("| ID  | Name | Mobile Phone | Years of Experience |");
        while (resultSet.next()) {
                    int sID = resultSet.getInt("sID");
                    String sName = resultSet.getString("sName");
                    int sPhoneNumber = resultSet.getInt("sPhoneNumber");
                    int sExperience = resultSet.getInt("sExperience");
                    System.out.println("| " + sID + " | " + sName + " |" + sPhoneNumber + " | " + sExperience + " |");
                }        
    }
    catch (SQLException e) 
        {
            System.err.println("Fail! Cannot connect to Database!\n");
            e.printStackTrace();
        }
        System.out.println("Return to manager menu.\n\n\n");
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
        String countno = "SELECT S.sID, S.sName, S.sExperience, COUNT(T.tID) AS NumberofTran FROM salesperson S, transaction T "
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
    System.out.println("End of Query.\n\n\n");
    System.out.println("Return to manager menu.\n\n\n");
    }
    

public static void Showtotalsales(Connection conn) throws IOException{
    int columns = 10000;
    try{
        String sqlStatement = "SELECT COUNT(*) FROM manufacturer";
        PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
        ResultSet rs = pstmt.executeQuery();
        // Move cursor to data
        rs.next();
        columns = rs.getInt("count(*)");
        }
        catch (Exception ex){

        }
        String[][] results = new String[columns][4];
        int returnCount = 0;

        try{
            String sqlStatement_totalsales = "SELECT m.mID, m.mName, sum(p.pPrice) as TotalSalesValue " +
                "FROM manufacturer m " +
                    "JOIN part p ON m.mID = p.mID " +
                        "GROUP BY m.mID " +
                            "ORDER BY TotalSalesValue DESC"; 
            PreparedStatement pstmt_totalsales = conn.prepareStatement(sqlStatement_totalsales);
            ResultSet rs_totalsales = pstmt_totalsales.executeQuery();

            System.out.println("| manufacturer ID | manufacturer Name | Total Sales Value |");
            Boolean hasResult = false;
            while(rs_totalsales.next()){
                results[returnCount][0] = rs_totalsales.getString("mID");
                results[returnCount][1] = rs_totalsales.getString("mName");
                results[returnCount][2] = rs_totalsales.getString("TotalSalesValue");
                returnCount +=1;
            }
            for(int i=0; i<returnCount; i++)
                System.out.println("| " + results[i][0] + " | " + results[i][1] + " | " + results[i][2] );
            }
            catch (Exception exp) {
                System.out.println("Error: " + exp);
            }
    System.out.println("End of Query.\n\n\n");
    System.out.println("Return to manager menu.\n\n\n");

}

public static void Nmostpopular(Connection conn) throws IOException{
    int N = 0;
    int rowCount = 0;
    Scanner scan = new Scanner(System.in);
    System.out.print("Type in the number of parts: ");
    N = scan.nextInt();    
    try{
        String sqlStatement = "SELECT COUNT(*) FROM manufacturer";
        PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
        ResultSet rs = pstmt.executeQuery();
        // Move cursor to data
        rs.next();
        rowCount = rs.getInt("count(*)");
    }
        catch (Exception ex){
           
    }
    String sqlStatement_Nmost;
    PreparedStatement pstmt_Nmost;
    String[][] results = new String[rowCount][4];
    int returnCount = 0;
    try{
        sqlStatement_Nmost = "SELECT p.pID, p.pName, COUNT(t.tID) AS Totaltransactions " +
            "FROM part p " +
                "JOIN transaction t ON p.pID = t.pID " +
                    "GROUP BY p.pID, p.pName " +
                        "ORDER BY Totaltransactions DESC, p.pID DESC " +
                            "LIMIT ?";
    pstmt_Nmost = conn.prepareStatement(sqlStatement_Nmost);
    pstmt_Nmost.setInt(1, N);
    ResultSet rs_Nmost = pstmt_Nmost.executeQuery();    
    System.out.println("| Part ID | Part Name | No. of Tranaction |");
    while(rs_Nmost.next()){
        results[returnCount][0]= rs_Nmost.getString("pID");
        results[returnCount][1]= rs_Nmost.getString("pName");
        results[returnCount][2]= rs_Nmost.getString("Totaltransactions");
        returnCount += 1;                         
        }
    for(int i=0; i<returnCount; i++){
        System.out.println("| " + results[i][0] + " | " + results[i][1] + " | " + results[i][2] + " | ");
    }    
    }            
    catch (Exception exp) {
        System.out.println("Error: " + exp);
    }
    System.out.println("End of Query.\n\n\n");
    System.out.println("Return to manager menu.\n\n\n");
}
}

