package Controller;

import Model.User;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLite {
    
    String driverURL = "jdbc:sqlite:" + "database.db";
    private PasswordUtils passwordUtils = new PasswordUtils();
    
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
            + " role INTEGER DEFAULT 2,\n"
            + " tries INTEGER DEFAULT 0\n"
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

                String sql = "INSERT INTO users(username,password,role,tries) VALUES('" + username + "','" + password + "','" + role + "',0)";

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
        String sql;
        
        int tries;
        int index;
 
        
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
                    System.out.println("Found");
                    System.out.println("Username: " + users.get(i).getUsername());
                    System.out.println("Hashed Password: " + users.get(i).getPassword());
                    System.out.println("Plain Password: " + password);
                    System.out.println("Plain Password Hashed: " + passwordUtils.encryptThisString(password));

                    user = users.get(i);
                    index = i;

                    if (passwordUtils.encryptThisString(password).equals(user.getPassword())){
                        if (!user.isLockout()){
                            login = true;
                            user.setTries(0);
                            break; 
                        }   
                        else
                            System.out.println("This account is locked out");
                    }
                    else {
                        System.out.println("User Tries: " + user.getTries());
                        System.out.println("Failed attempts: " + user.getTries());
                        
                        tries = user.getTries()+1;
                        user.setTries(tries);
                        
                        sql = "UPDATE 'users' SET 'tries' = " + tries + " WHERE 'username' = '" + user.getUsername() + "';";
                        System.out.println(sql);

                        try (Connection conn = DriverManager.getConnection(driverURL);
                            Statement stmt = conn.createStatement()){
                            stmt.executeUpdate(sql);
                            System.out.println("Edited");

                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                        
//                        users = getUsers();
//                        System.out.println(users.get(index).getUsername());
//                        System.out.println(users.get(index).getTries());

                        if (user.getTries() == 5){
                            user.setLockout(true);
                            System.out.println("This account has been locked out");
                        }
                        

                    }
                }
            }
        }
        
        if (login == false)
            user = null;
            
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
                    System.out.println(" Tries: " + users2.get(nCtr).getTries());
                }
                
                }//checkString
                else
                    System.out.println("Password must contain at least one capital letter and one number");
            }//special
            else
                System.out.println("Password must contain at least one special character.");
        }//found
        else
            System.out.println("This user already exists.");
        
    }    
}
