//packge mainmenu
import java.io.*;
import java.rmi.ConnectIOException;
import java.sql.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class mainmenu {

    public static void main(String[] args) throws IOException{
    int cont=1;
    while(cont==1){
    System.out.println("-----Main menu-----");
    System.out.println("1. Operations for administrator");
    System.out.println("2. Operations for salesperson");
    System.out.println("3. Operations for manager");
    System.out.println("4. Exist this program");
    System.out.printf("Enter Your Choice: ");
    BufferedReader nochoice = new BufferedReader(new InputStreamReader(System.in));
    int input = Integer.parseInt(nochoice.readLine());
    if(input==1){
        Administrator.admin(args);
    }else if(input==2){
        Salesperson.sales(args);
    }else if(input==3){
        Manager.manager(args);
    }else if(input==4){
        //exit
        System. exit(0);
    }
    }}
}
