package Controller;

import Model.History;
import Model.Logs;
import Model.Product;
import Model.User;
import View.Frame;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class SQLite {
    
    String driverURL = "jdbc:sqlite:" + "database.db";
    private PasswordUtils passwordUtils = new PasswordUtils();
    private int lock = 0;
    public int DEBUG_MODE = 0;
    
    private User loggedIn;
    
    private ArrayList<String> invalidArr = new ArrayList<String>();
    
    public SQLite () {
        invalidArr.add("=");
        invalidArr.add("'");
        invalidArr.add("<");
        invalidArr.add(">");
        invalidArr.add(";");
    }
    
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
        System.out.println("New product added: " + name);
            Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Product {1} has been added", new Object[]{new Timestamp(System.currentTimeMillis()), name});
            this.addLogs("ADD PRODUCT", name, "Product added " + name, new Timestamp(System.currentTimeMillis()).toString());
        } catch (Exception ex) {}
    }
    
    public void editProduct(String oldname, String name, int stock, double price) {
        String sql = "UPDATE product SET name='" +name + "', stock=" + stock + ", price=" + price + " WHERE name = '" + oldname + "';";
        System.out.println(sql);
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
            System.out.println("Product edited: " + name);
            Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Product {1} has been edited", new Object[]{new Timestamp(System.currentTimeMillis()), name});
            this.addLogs("EDIT PRODUCT", name, "Product edited " + name, new Timestamp(System.currentTimeMillis()).toString());
        } catch (Exception ex) {}
    }
    
    public void deleteProduct(String name){
        String sql = "DELETE FROM product WHERE name='" +name + "';";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()){
            stmt.execute(sql);
            System.out.println("Product deleted: " + name);
            Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Product {1} has been deleted", new Object[]{new Timestamp(System.currentTimeMillis()), name});
            this.addLogs("DELETE PRODUCT", name, "Product deleted " + name, new Timestamp(System.currentTimeMillis()).toString());
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
            + " username TEXT NOT NULL UNIQUE,\n"
            + " password TEXT NOT NULL,\n"
            + " role INTEGER DEFAULT 2,\n"
            + " locked INTEGER DEFAULT 0\n"
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
        String sql = "SELECT id, username, password, role, locked FROM users";
        ArrayList<User> users = new ArrayList<User>();
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                                   rs.getString("username"),
                                   rs.getString("password"),
                                   rs.getInt("role"),
                                   rs.getInt("locked")
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
        String sql = "DELETE FROM users WHERE username='" + username + "';";

        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User " + username + " has been deleted.");
        } catch (Exception ex) {}
    }
    
    public void editRole(String username, char role) {
        String sql = "UPDATE users SET role=" + role + " WHERE username='" + username + "';";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Role of " + username + " updated to " + role);
            Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Role of account {1} has been updated to {2}", new Object[]{new Timestamp(System.currentTimeMillis()), username, role});
            this.addLogs("EDIT ROLE - " + role, username, "Role has been modified to " + role, new Timestamp(System.currentTimeMillis()).toString());
        } catch (Exception ex) {}
    }
    
    public void editPassword(String username, String password) {
        if (passwordUtils.bContainsSpecialCharacter(password)){
            if (passwordUtils.bCheckString(password)){
        
                String encrypt = passwordUtils.encryptThisString(password);
                String sql = "UPDATE users SET password='" + encrypt + "' WHERE username='" + username + "';";
                //System.out.println(sql);

                try (Connection conn = DriverManager.getConnection(driverURL);
                    Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("Password of " + username + " updated");
                    Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Password of account {1} has been updated", new Object[]{new Timestamp(System.currentTimeMillis()), username});
                    this.addLogs("EDIT PASSWORD", username, "Password of " + username + " modified", new Timestamp(System.currentTimeMillis()).toString());
                } catch (Exception ex) {}

            }
            else{
                Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Failed to edit password of {1}", new Object[]{new Timestamp(System.currentTimeMillis()), username});
                this.addLogs("EDIT PW FAILED", username, "Password modification failure", new Timestamp(System.currentTimeMillis()).toString());
                JOptionPane.showMessageDialog(null, "Edit Password Failed - Must contain a number, capital letter and be 8 characters long");
            }
        }else{
            Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Failed to edit password of {1}", new Object[]{new Timestamp(System.currentTimeMillis()), username});
            this.addLogs("EDIT PW FAILED", username, "Password modification failure", new Timestamp(System.currentTimeMillis()).toString());
            JOptionPane.showMessageDialog(null, "Edit Password Failed - Must contain a special character.");
        }
    }
    
    public void lockUser(String username) {
        String sql = "UPDATE users SET locked=1 WHERE username='" + username + "';";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User " + username + " has been locked.");
        } catch (Exception ex) {}
    }
    
    public void unlockUser(String username) {
        String sql = "UPDATE users SET locked=0 WHERE username='" + username + "';";
        
        try (Connection conn = DriverManager.getConnection(driverURL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User " + username + " has been unlocked.");
        } catch (Exception ex) {}
    }
    
    public User loginUser (String username, String password) {
        boolean login = false;
        User user = null;
        ArrayList<User> users = null;
        
        boolean valid = true;
        
        for (int j=0;j<invalidArr.size();j++)
            if (username.contains(invalidArr.get(j)) || password.contains(invalidArr.get(j))){
                valid = false;
                break;
            }
                
        if (valid){
        
        
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
        
        if (!valid)        
            return null;
        else if (login == false){
            if (user.getTries() < 4){
                int tries = user.getTries() + 1;
                user.setTries(tries);
            }
            else if (user.getTries() > 4){
                user.setLockout(1);
                this.lockUser(username);
                Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Account '{1}' has been locked", new Object[]{new Timestamp(System.currentTimeMillis()), username});
                this.addLogs("ACCOUNT LOCK", username, "Account lockout due to too many failed attempts", new Timestamp(System.currentTimeMillis()).toString());
            }
            
            return null;
        }
            
        System.out.println("loginUser: " + login);
        
        if (login)
            loggedIn = user;
        
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
//                System.out.println(username + " has been added to the system.");
                Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Register attempt with username {1} successful", new Object[]{new Timestamp(System.currentTimeMillis()), username});
                this.addLogs("REGISTER SUCCESS", username, "Register attempt - success", new Timestamp(System.currentTimeMillis()).toString());
                
                ArrayList<User> users2 = getUsers();
                for(int nCtr = 0; nCtr < users2.size(); nCtr++){
                    System.out.println("===== User " + users2.get(nCtr).getId() + " =====");
                    System.out.println(" Username: " + users2.get(nCtr).getUsername());
                    System.out.println(" Password: " + users2.get(nCtr).getPassword());
                    System.out.println(" Role: " + users2.get(nCtr).getRole());

                }
                
                }//checkString
                else{
                    //System.out.println("Password must contain at least one capital letter and one number");
                    System.out.println(new Timestamp(System.currentTimeMillis()) + " Register attempt from user " + username + " failed - password complexity");
                    Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Register attempt with username {1} failed", new Object[]{new Timestamp(System.currentTimeMillis()), username});
                    this.addLogs("REGISTER FAIL", username, "Register attempt failed, password complexity", new Timestamp(System.currentTimeMillis()).toString());
                }
            }//special
            else{
                System.out.println(new Timestamp(System.currentTimeMillis()) + " Register attempt from user " + username + " failed - password topology");
                Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Register attempt with username {1} failed", new Object[]{new Timestamp(System.currentTimeMillis()), username});
                this.addLogs("REGISTER FAIL", username, "Register attempt failed, password topology", new Timestamp(System.currentTimeMillis()).toString());
            }
        }//found
        else{ 
//            System.out.println("This user already exists.");
            System.out.println(new Timestamp(System.currentTimeMillis()) + " Register attempt from user " + username + " failed - user exists");
            Logger.getLogger(Frame.class.getName()).log(Level.INFO, "{0} Register attempt with username {1} failed", new Object[]{new Timestamp(System.currentTimeMillis()), username});
            this.addLogs("REGISTER FAIL", username, "Register attempt failed, user already exists", new Timestamp(System.currentTimeMillis()).toString());
        }
    }

    public User getLoggedIn(){
        return loggedIn;
    }
    
    public void setLoggedIn(User user){
        this.loggedIn = user;
    }
    
    
}
