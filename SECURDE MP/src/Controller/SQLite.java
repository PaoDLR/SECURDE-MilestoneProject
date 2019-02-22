package Controller;

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
    
    public void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(driverURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database database.db created.");
            }
        } catch (Exception ex) {}
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
