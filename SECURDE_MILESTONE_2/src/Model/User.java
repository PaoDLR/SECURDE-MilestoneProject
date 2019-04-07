package Model;

//import java.util.ArrayList;

public class User {
    private int id;
    private String username;
    private String password;
    private int role;
    private int lockout;
    private int tries;
    
//  private ArrayList<Product> cart;
    
//  private int locked = 0;
    

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.lockout = 0;
        this.tries = 0;
//        this.cart = new ArrayList<Product>();
    }
    
    public User(int id, String username, String password, int role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.lockout = 0;
        this.tries = 0;
//        this.cart = new ArrayList<Product>();
    }
    
    public User(int id, String username, String password, int role, int lockout){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.lockout = lockout;
//        this.cart = new ArrayList<Product>();
    }
    
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
    
    public int isLockout() {
        return lockout;
    }
    
    public void setLockout(int lock) {
        this.lockout = lock;
    }

    
    public int getTries() {
        return this.tries;
    }
    
    public void setTries(int tries) {
        this.tries = tries;
    }
    
//    public ArrayList<Product> getCart(){
//        return cart;
//    }

}
