package Model;

public class User {
    private int id;
    private String username;
    private String password;
    private int role;
    private boolean lockout;
    private int tries;
    

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.lockout = false;
        this.tries = 0;
    }
    
    public User(int id, String username, String password, int role){
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.lockout = false;
        this.tries = 0;
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

    public boolean isLockout() {
        return lockout;
    }

    public void setLockout(boolean lockout) {
        this.lockout = lockout;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }
    
    
    
}
