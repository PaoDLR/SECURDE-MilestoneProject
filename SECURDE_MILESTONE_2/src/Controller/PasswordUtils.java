package Controller;

import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
  
public class PasswordUtils { 
    
    public static String encryptThisString(String input) { 
        try { 
            // getInstance() method is called with algorithm SHA-512 
            MessageDigest md = MessageDigest.getInstance("SHA-512"); 
  
            // digest() method is called 
            // to calculate message digest of the input string 
            // returned as array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            // Add preceding 0s to make it 32 bit 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
  
            // return the HashText 
            return hashtext; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    }
    
    public boolean bContainsSpecialCharacter(String s) {
        
         if (s == null || s.trim().isEmpty()) {
             //System.out.println("Incorrect format of string");
             return false;
         }
         
         Pattern p = Pattern.compile("[^A-Za-z0-9]");
         Matcher m = p.matcher(s);
        
         boolean b = m.find();
         
         return b;
         
     }
    
    public static boolean bCheckString (String str) {
        
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        
        if (str.length() >= 8) {
        
            for(int i=0;i < str.length();i++) {
                ch = str.charAt(i);

                if( Character.isDigit(ch)) {
                    numberFlag = true;
                }
                else if (Character.isUpperCase(ch)) {
                    capitalFlag = true;
                } else if (Character.isLowerCase(ch)) {
                    lowerCaseFlag = true;
                }
                if(numberFlag && capitalFlag && lowerCaseFlag)
                    return true;
            }
        }
        else
            return false;
            //System.out.println("Password should be 8 characters long or more.");
        
        return false;
    }
    
}