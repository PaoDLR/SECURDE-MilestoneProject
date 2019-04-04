package Controller;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLite {
    
    String driverURL = "jdbc:sqlite:" + "database.db";
    private PasswordUtils passwordUtils = new PasswordUtils();
    private int lock = 0;
    public int DEBUG_MODE = 0;
    
    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {}
    }
    
    public void createHistoryTable() {
        String sql = "CREATE TABLE IF NOT EXISTS history (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL,\n"
            + " name TEXT NOT NULL,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db created.");
        } catch (Exception ex) {}
    }
        
    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS logs (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " event TEXT NOT NULL,\n"
            + " username TEXT NOT NULL,\n"
            + " desc TEXT NOT NULL,\n"
            + " timestamp TEXT NOT NULL\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db created.");
        } catch (Exception ex) {}
    }
        
    public void createProductTable() {
        String sql = "CREATE TABLE IF NOT EXISTS product (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " name TEXT NOT NULL UNIQUE,\n"
            + " stock INTEGER DEFAULT 0,\n"
            + " price REAL DEFAULT 0.00\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db created.");
        } catch (Exception ex) {}
    }
    
    public void dropHistoryTable() {
        String sql = "DROP TABLE IF EXISTS history;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table history in database.db dropped.");
        } catch (Exception ex) {}
    }
    
    public void dropLogsTable() {
        String sql = "DROP TABLE IF EXISTS logs;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table logs in database.db dropped.");
        } catch (Exception ex) {}
    }
    
    public void dropProductTable() {
        String sql = "DROP TABLE IF EXISTS product;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table product in database.db dropped.");
        } catch (Exception ex) {}
    }
    
    public void addHistory(String username, String name, int stock, String timestamp) {
        String sql = "INSERT INTO history(username,name,stock,timestamp) VALUES('" + username + "','" + name + "','" + stock + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {}
    }
    
    public void addLogs(String event, String username, String desc, String timestamp) {
        String sql = "INSERT INTO logs(event,username,desc,timestamp) VALUES('" + event + "','" + username + "','" + desc + "','" + timestamp + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {}
    }
    
    public void addProduct(String name, int stock, double price) {
        String sql = "INSERT INTO product(name,stock,price) VALUES('" + name + "','" + stock + "','" + price + "')";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
        } catch (Exception ex) {}
    }
    
    public ArrayList<History> getHistory(){
        String sql = "SELECT id, username, name, stock, timestamp FROM history";
        ArrayList<History> histories = new ArrayList<History>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                histories.add(new History(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {}
        return histories;
    }
    
    public ArrayList<Logs> getLogs(){
        String sql = "SELECT id, event, username, desc, timestamp FROM logs";
        ArrayList<Logs> logs = new ArrayList<Logs>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                logs.add(new Logs(rs.getInt("id"),
                                   rs.getString("event"),
                                   rs.getString("username"),
                                   rs.getString("desc"),
                                   rs.getString("timestamp")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return logs;
        
    }
    
    public ArrayList<Product> getProduct(){
        String sql = "SELECT id, name, stock, price FROM product";
        ArrayList<Product> products = new ArrayList<Product>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                products.add(new Product(rs.getInt("id"),
                                   rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price")));
            }
        } catch (Exception ex) {}
        return products;
    }
    
    public Product getProduct(String name){
        String sql = "SELECT name, stock, price FROM product WHERE name='" + name + "';";
        Product product = null;
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            product = new Product(rs.getString("name"),
                                   rs.getInt("stock"),
                                   rs.getFloat("price"));
        } catch (Exception ex) {}
        return product;
    }
    
    public void createUserTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
            + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
            + " username TEXT NOT NULL,\n"
            + " password TEXT NOT NULL,\n"
            + " role INTEGER DEFAULT 2\n"
            + ");";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db created.");
        } catch (Exception ex) {}
    }
    
    public void dropUserTable() {
        String sql = "DROP TABLE users;";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table users in database.db dropped.");
        } catch (Exception ex) {}
    }
    
    public ArrayList<User> getUsers(){
        String sql = "SELECT id, username, password, role FROM users";
        ArrayList<User> users = new ArrayList<User>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("password"),
                                   rs.getInt("role")
                                   ));
            
            }
        } catch (Exception ex) {}
        return users;
    }
    
    public void addUser(String username, String password) {
        
            password = passwordUtils.encryptThisString(password);

            String sql = "INSERT INTO users(username,password) VALUES('" + username + "','" + password + "')";

            try (Connection conn = DriverManager.getConnection(driverURL);
                Statement stmt = conn.createStatement()){
                stmt.execute(sql);

            //  For this activity, we would not be using prepared statements first.
            //      String sql = "INSERT INTO users(username,password) VALUES(?,?)";
            //      PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //      pstmt.setString(1, username);
            //      pstmt.setString(2, password);
            //      pstmt.executeUpdate();
            } catch (Exception ex) {}
            
    }
    
    public void addUser(String username, String password, int role) {
    
                password = passwordUtils.encryptThisString(password);

                String sql = "INSERT INTO users(username,password,role) VALUES('" + username + "','" + password + "','" + role + "')";

                try (Connection conn = DriverManager.getConnection(driverURL);
                    Statement stmt = conn.createStatement()){
                    stmt.execute(sql);

                } catch (Exception ex) {}
                
    }
    
    
    public void removeUser(String username) {
        String sql = "DELETE FROM users WHERE username='" + username + "');";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User " + username + " has been deleted.");
        } catch (Exception ex) {}
    }
    
    public User loginUser (String username, String password) {
        boolean login = false;
        User user = null;
        ArrayList<User> users = null;
        
 
        
        if (!(username.contains("SELECT") || username.contains("INSERT") 
                || username.contains(";") || username.contains("--") || username.contains("++")))
            if (!(password.contains("SELECT") || password.contains("INSERT") 
                || password.contains(";") || password.contains("--") || password.contains("++"))){
        
        
            users = getUsers();
        
//        try (Connection conn = DriverManager.getConnection(driverURL);
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(sql)){
//            
//            while (rs.next()) {
//                users.add(new User(rs.getInt("id"),
//                                   rs.getString("username"),
//                                   rs.getString("password"),
//                                   rs.getInt("role")));
//            
//            }
//        } catch (Exception ex) {}
       

            for (int i=0;i<users.size();i++){
                if (users.get(i).getUsername().toLowerCase().equals(username.toLowerCase())){
//                    System.out.println("Found");
//                    System.out.println("Username: " + users.get(i).getUsername());
//                    System.out.println("Hashed Password: " + users.get(i).getPassword());
//                    System.out.println("Plain Password: " + password);
//                    System.out.println("Plain Password Hashed: " + passwordUtils.encryptThisString(password));

                    user = users.get(i);
                    

                    if (passwordUtils.encryptThisString(password).equals(user.getPassword())){
                            login = true;
                            break; 
                    }

                }
            }
        }
        
        if (login == false)
            return null;
            
        System.out.println("loginUser: " + login);
        
        return user;
        
    }
    
    public void registerUser (String username, String password) {
        ArrayList<User> users = this.getUsers();
        boolean found = false;
        String hash = "";
        
        for (int i=0;i<users.size();i++){
            if (users.get(i).getUsername().toLowerCase().equals(username.toLowerCase())){
                found = true;
                break;
            }
        }
        
        if (!found){
            if (passwordUtils.bContainsSpecialCharacter(password)){
                if (passwordUtils.bCheckString(password)){
            
//                hash = passwordUtils.encryptThisString(password);
                this.addUser(username, password, 2);
                System.out.println(username + " has been added to the system.");
                
                ArrayList<User> users2 = getUsers();
                for(int nCtr = 0; nCtr < users2.size(); nCtr++){
                    System.out.println("===== User " + users2.get(nCtr).getId() + " =====");
                    System.out.println(" Username: " + users2.get(nCtr).getUsername());
                    System.out.println(" Password: " + users2.get(nCtr).getPassword());
                    System.out.println(" Role: " + users2.get(nCtr).getRole());

                }
                
                }//checkString
                else
                    //System.out.println("Password must contain at least one capital letter and one number");
                    System.out.println(new Timestamp(System.currentTimeMillis()) + " Register attempt from user " + username + " failed - password complexity");
            }//special
            else
                System.out.println(new Timestamp(System.currentTimeMillis()) + " Register attempt from user " + username + " failed - password topology");
//                System.out.println("Password must contain at least one special character.");
        }//found
        else
//            System.out.println("This user already exists.");
            System.out.println(new Timestamp(System.currentTimeMillis()) + " Register attempt from user " + username + " failed - user exists");
        
    }    
}
