/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailextractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ex094
 */
public class EmailExtractor {
    
    String pattern = "\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b"; //Email Address Pattern
    URL url; //URL Instance Variable
    StringBuilder contents; //Stores our URL Contents
    Set<String> emailAddresses = new HashSet<>(); //Contains unique email addresses
    
    EmailExtractor(String url) {
        
        try {
            this.url = new URL(url); //Initalizing our URL object
        } catch (MalformedURLException ex) {
           System.out.println("\tPlease include Protocol in your URL e.g. http://www.google.com");
           System.exit(1);
        }
    }
    
    public void readContents() {  
        try {
            //Open Connection to URL and get stream to read
            BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
            contents = new StringBuilder();
            //Read and Save Contents to StringBuilder variable
            String input = "";
            while((input = read.readLine()) != null) {
                contents.append(input);
            }
        } catch (IOException ex) {
            System.out.println("\tUnable to read URL due to Unknown Host..");
        }  
    }
    
    public void extractEmail() {
        //Creates a Pattern
        Pattern pat = Pattern.compile(pattern);
        //Matches contents against the given Email Address Pattern
        Matcher match = pat.matcher(contents);
        //If match found, append to emailAddresses
        while(match.find()) {
            emailAddresses.add(match.group());
        }
    }
    
    public void printAddresses() {
        //Check if email addresses have been extracted
        if(emailAddresses.size() > 0) {
            //Print out all the extracted emails
            System.out.println("\tExtracted Email Addresses: ");
            for(String emails : emailAddresses) {
                System.out.println(emails);
            }
        } else {
            //In case, no email addresses were extracted
            System.out.println("\tNo emails were extracted!");
        }
    }
    
    public void saveAddresses(String filename) {
        //Create a new .txt file
        File file = new File(filename + ".txt");
        //Setting charset
        Charset charset = Charset.forName("UTF-8");
        
        //Create a BufferedWriter to write emails to the file
        try(BufferedWriter write = new BufferedWriter(new FileWriter(file))) {
            //Write each email address on a newline in the file
            for(String emails : emailAddresses) {
                write.write(emails);
                write.newLine();
            }
            System.out.println("\tEmails have been saved to " + filename + ".txt");
        } catch (IOException ex) {
            System.out.println("\tCould not save email addresses to text file!");
        }
    }
    
    public static void main (String args[]) {
        
        EmailExtractor extract;
        
        //Check if arguments are supplied and URL is supplied
        if(args.length > 0 && args[0] != null) {
            
            extract = new EmailExtractor(args[0]);//Initalize Extractor with URL
            extract.readContents(); //Read the URL contents
            extract.extractEmail(); //Extract the email addresses
            
            //If -s in args, then save the emails
            if(args.length == 3 && args[1] != null && args[1].equals("-s") && args[2] != null) {
                extract.saveAddresses(args[2]); //Save the email address in a file with name from args[2]
            //Just print them normally    
            } else {
                extract.printAddresses(); //Otherwise normally display the email addresses
            }
        } else {
            System.out.println("\tInvalid Arguments supplied...");
        }
    }
}
