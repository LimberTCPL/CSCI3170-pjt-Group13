//package Administrator;
import java.io.*;
import java.rmi.ConnectIOException;
import java.sql.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Administrator {
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
//Admin menu
    public static void admin(String[] args) throws IOException {
        int i=1;
        Connection conn = connectToMySQL();
        while(i==1){
        System.out.println("----Operations for administrator menu----");
        System.out.println("What kinds of operation would you like to perform?");
        System.out.println("1. Create all tables");
        System.out.println("2. Delete all tables");
        System.out.println("3. Load form datafile");
        System.out.println("4. Show content of a table");
        System.out.println("5. Return to the main menu");
        System.out.printf("Enter Your Choice: ");
        BufferedReader nochoice = new BufferedReader(new InputStreamReader(System.in));
        int admchoice = Integer.parseInt(nochoice.readLine());
        switch(admchoice){
            case 1:{
                System.out.printf("Processing...");
                Createtable(conn);//call fcn Create table
                //return main menu?
                break;
            }
            case 2:{
                System.out.printf("Processing...");
                Deletetable(conn);//call fcn delete table
                //return main menu?
                break;
            }
            case 3:{
                InsertData(conn);//call fcn import file
                //return main menu?
                break;
            }
            case 4:{
                ShowContent(conn);//call fcn show contact
                //return main menu?
                break;
            }
            case 5:{
                mainmenu.main(args);
                //call fcn return main menu
                break;
            }
            default:{
                System.err.println("[Error]: Input Choice invaild!\nPlease Enter again");
                i=0;
                admin(args);
            }
        }}
}
//function
public static void Createtable(Connection con) throws IOException {
    try{
        Statement stmt = con.createStatement();
        String category = "CREATE TABLE category"
                         + "(cID INTEGER(1) NOT NULL,"
                          + "cName VARCHAR(20) NOT NULL,"
                           + "PRIMARY KEY(cID),"
                            + "CHECK (cID > 0))";
        stmt.executeUpdate(category);
        String Manufacturer = "CREATE TABLE manufacturer"
                             + "(mID INTEGER(1) NOT NULL,"
                              + "mName VARCHAR(20) NOT NULL,"
                               + "mAddress VARCHAR(50) NOT NULL,"
                                + "mPhoneNumber INTEGER(8) NOT NULL,"
                                 + "PRIMARY KEY(mID),"
                                  + "CONSTRAINT c_manufacturer CHECK (mID > 0 AND mPhoneNumber > 10000000))";
        stmt.executeUpdate(Manufacturer);
        String Salesperson = "CREATE TABLE salesperson"
                             + "(sID INTEGER(2) NOT NULL,"
                              + "sName VARCHAR(20) NOT NULL,"
                               + "sAddress VARCHAR(50) NOT NULL,"
                                + "sPhoneNumber INTEGER(8),"
                                 + "sExperience INTEGER(1),"
                                  + "PRIMARY KEY (sID),"
                                   + "CONSTRAINT check_salesperson CHECK (sID > 0 AND pPhoneNumber > 10000000))";
        stmt.executeUpdate(Salesperson);
        String Part = "CREATE TABLE part"
                     + "(pID INTEGER(3) NOT NULL,"
                      + "pName VARCHAR(20) NOT NULL,"
                       + "pPrice INTEGER(20) NOT NULL,"
                        + "mID INTEGER(1) NOT NULL,"
                         + "cID INTEGER(1) NOT NULL,"
                          + "pWarrantyPeriod INTEGER(2) NOT NULL,"
                           + "pAvailableQuantity INTEGER(2) NOT NULL,"
                            + "PRIMARY KEY (pID),"
                               + "CONSTRAINT check_part CHECK (pID > 0 AND pWarrantyPeriod > 10))";
        stmt.executeUpdate(Part);
        String Transaction = "CREATE TABLE transaction"
                             + "(tID INTEGER(4) NOT NULL,"
                              + "pID INTEGER(3) NOT NULL,"
                               + "sID INTEGER(2) NOT NULL,"
                                + "tDate DATE NOT NULL,"
                                 + "PRIMARY KEY (tID),"
                                    + "CHECK (tID > 0)) ";
        stmt.executeUpdate(Transaction);
        String fk_p1 = "ALTER TABLE part ADD CONSTRAINT fk_p1 FOREIGN KEY (cID) REFERENCES category (cID)";
        stmt.execute(fk_p1);
        String fk_p2 = "ALTER TABLE part ADD CONSTRAINT fk_p2 FOREIGN KEY (mID) REFERENCES manufacturer (mID)";
        stmt.execute(fk_p2);
        String fk_t1 = "ALTER TABLE transaction ADD CONSTRAINT fk_t1 FOREIGN KEY (pID) REFERENCES part (pID)";
        stmt.execute(fk_t1);
        String fk_t2 = "ALTER TABLE transaction ADD CONSTRAINT fk_t2 FOREIGN KEY (sID) REFERENCES salesperson (sID)";
        stmt.execute(fk_t2);
        System.out.println("Done! Database is initialized!\n");
    }
    catch (SQLException e) 
        {
            System.err.println("Fail! Cannot connect to Database!\n");
            e.printStackTrace();
        }
    System.out.println("Return to Administrator menu.\n\n\n");
}

public static void Deletetable(Connection con) throws IOException {
    try{
        Statement stmt = con.createStatement();
        stmt.execute("ALTER TABLE part DROP FOREIGN KEY fk_p1");
        stmt.execute("ALTER TABLE part DROP FOREIGN KEY fk_p2");
        stmt.execute("ALTER TABLE transaction DROP FOREIGN KEY fk_t1");
        stmt.execute("ALTER TABLE transaction DROP FOREIGN KEY fk_t2");
        String Drop = "DROP TABLE category";
        stmt.executeUpdate(Drop);
        String Drop2 = "DROP TABLE manufacturer";
        stmt.executeUpdate(Drop2);
        String Drop3 = "DROP TABLE salesperson";
        stmt.executeUpdate(Drop3);
        String Drop4 = "DROP TABLE part";
        stmt.executeUpdate(Drop4);
        String Drop5 = "DROP TABLE transaction";
        stmt.executeUpdate(Drop5);
        System.out.println("Done! Database is removed!\n");
    }
    catch (SQLException e) 
        {
            System.err.println("Fail! Cannot connect to Database!\n");
            e.printStackTrace();
        }
    System.out.println("Return to administrator menu.\n\n\n");
}

public static void InsertData(Connection con) throws IOException {
    System.out.printf("Type in the Source Data Folder Path:");
    BufferedReader ip = new BufferedReader(new InputStreamReader(System.in));
    String path = ip.readLine();
    System.out.printf("Processing...");
    try{
        Statement stmt = con.createStatement();
        //category
        Scanner scannerc = new Scanner(new File(path + "/category.txt"));
        while (scannerc.hasNextLine()) {
        String line = scannerc.nextLine();
        String[] arrline = line.split("\t");
        int cid = Integer.parseInt(arrline[0]);
        String cname = arrline[1];
        String insertc = "INSERT INTO category VALUES"+
                    "('"+cid+"','"+cname+"')";
        stmt.executeUpdate(insertc);}
        //manufacturer
        Scanner scannerm = new Scanner(new File(path + "/manufacturer.txt"));
        while (scannerm.hasNextLine()) {
        String line = scannerm.nextLine();
        String[] arrline = line.split("\t");
        int mid = Integer.parseInt(arrline[0]);
        String mname = arrline[1];
        String maddress = arrline[2];
        String mpno = arrline[3];
        String insertm = "INSERT INTO manufacturer VALUES"+
                    "('" + mid + "','" + mname + "','" + maddress + "','" + mpno + "')";
        stmt.executeUpdate(insertm);}
        //part
        Scanner scannerp = new Scanner(new File(path + "/part.txt"));
        while (scannerp.hasNextLine()) {
        String line = scannerp.nextLine();
        String[] arrline = line.split("\t");
        int pid = Integer.parseInt(arrline[0]);
        String pname = arrline[1];
        int pprice = Integer.parseInt(arrline[2]);
        int mid = Integer.parseInt(arrline[4]);
        int cid = Integer.parseInt(arrline[3]);
        int pperiod = Integer.parseInt(arrline[5]);
        int pquantity = Integer.parseInt(arrline[6]);
        String insertp = "INSERT INTO part VALUES" + 
                    "('" + pid + "','" + pname + "','" + pprice + "','" + cid + "','" + mid + "','" + pperiod + "','" + pquantity + "')";
        stmt.executeUpdate(insertp);}
        //salesperson
        Scanner scanners = new Scanner(new File(path + "/salesperson.txt"));
        while (scanners.hasNextLine()) {
        String line = scanners.nextLine();
        String[] arrline = line.split("\t");
        int sid = Integer.parseInt(arrline[0]);
        String sname = arrline[1];
        String saddress = arrline[2];
        int spno = Integer.parseInt(arrline[3]);
        int sexp = Integer.parseInt(arrline[4]);
        String inserts = "INSERT INTO salesperson VALUES"+
                    "('" + sid + "','" + sname + "','" + saddress + "','" + spno + "','" + sexp + "')";
        stmt.executeUpdate(inserts);}
        //transaction
        Scanner scannert = new Scanner(new File(path + "/transaction.txt"));
        while (scannert.hasNextLine()) {
        String line = scannert.nextLine();
        String[] arrline = line.split("\t");
        int tid = Integer.parseInt(arrline[0]);
        int pid = Integer.parseInt(arrline[1]);
        int sid = Integer.parseInt(arrline[2]);
        String getd = arrline[3];
        String[] getdate = getd.split("/");
        String tdate = (getdate[2] + "-" + getdate[1] + "-" + getdate[0]);
        String insertt = "INSERT INTO transaction VALUES"+
                    "('" + tid + "','" + pid + "','" + sid + "','" + tdate + "')";
        stmt.executeUpdate(insertt);}
        System.out.println("Done! Data is inputted to the database!");
        }
    catch (SQLException e) 
        {
            System.err.println("Cannot connect ! ");
            e.printStackTrace();
        }
    System.out.println("Return to administrator menu.\n\n\n");
}

public static void ShowContent(Connection con) throws IOException{
    System.out.printf("Which table would you like to show: ");
    BufferedReader ip = new BufferedReader(new InputStreamReader(System.in));
    String tablen = ip.readLine();
    System.out.println("Content of table " + tablen + ":");
    try{
        HashMap<Integer, ArrayList<String>> hm = new HashMap<Integer, ArrayList<String>>();
        Statement stmt = con.createStatement();
        String tablecontent = "SELECT * FROM " + tablen;
        ResultSet resultSet = stmt.executeQuery(tablecontent);
        switch(tablen){
            case "Category":
            case "category":{
                System.out.println("|  c_ID  |  c_Name  |");
                while (resultSet.next()) {
                    int cID = resultSet.getInt("cID");
                    String cName = resultSet.getString("cName");
                    System.out.println("| " + cID + " | " + cName + " |");
                }
                }break;
            case "Manufacturer":
            case "manufacturer":{
                System.out.println("|  m_ID  |  m_Name  |  m_Address  |  m_PhoneNumber  |");
                while (resultSet.next()) {
                    int mID = resultSet.getInt("mID");
                    String mName = resultSet.getString("mName");
                    String mAddress = resultSet.getString("mAddress");
                    String mPhoneNumber = resultSet.getString("mPhoneNumber");
                    System.out.println("| " + mID + " | " + mName + " | " + mAddress + " | " + mPhoneNumber + " |");
                }
                }break;
            case "Part":
            case "part":{
                System.out.println("|  p_ID  |  p_Name  |  p_Price  |  m_ID  |  c_ID  |  p_WarrantyPeriod  |  p_AvailableQuantity  |");
                while (resultSet.next()){
                    int pID = resultSet.getInt("pID");
                    String pName = resultSet.getString("pName");
                    int pPrice = resultSet.getInt("pPrice");
                    int mID = resultSet.getInt("mID");
                    int cID = resultSet.getInt("cID");
                    int pWarrantyPeriod = resultSet.getInt("pWarrantyPeriod");
                    int pAvailableQuantity = resultSet.getInt("pAvailableQuantity");
                    System.out.println("| " + pID + " | " + pName + " | " + pPrice + " | " + cID + " | " + mID + " | " + pWarrantyPeriod + " | " + pAvailableQuantity + " |");
                }
                }break;
            case "Salesperson":
            case "salesperson":{
                System.out.println("|  sID  |  sName  |  sAddress  |  sPhoneNumber  |  sExperience  |");
                while (resultSet.next()){
                    int sID = resultSet.getInt("sID");
                    String sName = resultSet.getString("sName");
                    String sAddress = resultSet.getString("sAddress");
                    int sPhoneNumber = resultSet.getInt("sPhoneNumber");
                    int sExperience = resultSet.getInt("sExperience");
                    System.out.println("| " + sID + " | " + sName + " | " + sAddress + " | " + sPhoneNumber + " | " + sExperience + " |");
                }
                }break;
            case "Transaction":
            case "transaction":{
                System.out.println("|  tID  |  pID  |  sID  |  tDate  |");
                while (resultSet.next()){
                    int tID = resultSet.getInt("tID");
                    int pID = resultSet.getInt("pID");
                    int sID = resultSet.getInt("sID");
                    String getd = resultSet.getString("tDate");
                    String[] getdate = getd.split("-");
                    String tDate = (getdate[2] + "/" + getdate[1] + "/" + getdate[0]);
                    System.out.println("| " + tID + " | " + pID + " | " + sID + " | " + tDate + " |");
                }
                }break;
            default:
                System.err.println("[Error]: Input invaild!\nPlease Enter again");
                ShowContent(con);
            }
        }
        
    catch (SQLException e) 
        {
            System.err.println("Cannot connect ! ");
            e.printStackTrace();
        }
    System.out.println("Return to administrator menu.\n\n\n");
}

}