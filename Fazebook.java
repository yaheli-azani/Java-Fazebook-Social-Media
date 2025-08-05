package fazebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/*
 * This class represents a pseudo social media platform named Fazebook. It has
 * a comparator field used to initialize its EWDGraph field that is used to
 * represent the platforms and finally it has an Object field that acts as a
 * lock for the threads in the readSocialNetworkData() method. Any null 
 * parameters will result in false or null being returned. The addUser()
 * method adds a user without friends to the platform. If there is already a
 * user with the parameter name or if the parameter name is empty it just 
 * returns false without changing anything. The getAllUsers() method returns
 * a collection of all the current users of the platform. The addFriends()
 * method takes two userNames as parameters and returns true if they
 * successfully become friends. If the names are invalid false will be returned.
 * If either user is not a current user, it will add them to the platform and
 * then make them friends. If they're already friends or if someone tries 
 * to create a friendship themselves by putting two identical names as 
 * parameters false is returned. The getFriends() method takes a user name as
 * a parameter and returns a Collection of that user's friends. If the user
 * has no friends, or isn't on the platform an empty Collection is returned. The
 * unfriend() method takes two user names as parameters and removes the
 * friendship between them. If either of the parameter users isn't on the 
 * platform, or if they are on the platform but aren't friends, false is 
 * returned and nothing changes. The peopleYouMayWannaKnow() method takes a user
 * name as a parameter and returns a Collection of names of friends of that 
 * user's friends. The collection will not include any names of people the user
 * is already friends with or the user himself. If the parameter user isn't on
 * the platform an empty Collection is returned. This is also returned if the
 * user is on the platform but doesn't have any friends or his friends don't
 * have friends. Finally the readSocialNetworkData() method uses an inner
 * MyThread class to pass the name of one file from the parameter Collection
 * and have the thread read it and change the platform based on the file's
 * contents. Each thread reads a file using a Scanner and either adds a user or
 * a friendship based on the contents of the file. The method will join all the
 * threads (wait for them to finish) to ensure that when the method call is
 * over no more changes happen to the current Fazebook object. This way another
 * method can be called after readSocialNetworkData() without us having to 
 * worry that the new method is dealing with an unfinished current object. The
 * method returns true if successfully read the files and false if not. To 
 * ensure no data race happens the lock field grants a lock to one thread at a
 * time when that thread is editing the current shared Fazebook object. If 
 * some files in the parameter Collection are invalid the method will disregard
 * those files and read the valid files.
 */

public class Fazebook {
    
    private Comparator<String> c = new StringComparator();
    private EWDGraph<String> graph = new EWDGraph<String>(c);
    private Object lock = new Object();

    //adds a User to the current object and returns true if successfully added.
    //The addUser() method adds a user without friends to the platform. 
    //If there is already a user with the parameter name or if the parameter 
    //name is empty it just returns false without changing anything.
    public boolean addUser(String userName) {
        boolean result = false;
        
        //checks for invalid parameters
        if (userName != null && !userName.equals("")) {
            result = graph.newEWDGraphVertex(userName);
        }
                
        return result;
    }

    //returns a collection of all the users in the current object.
    public Collection<String> getAllUsers() {
        return graph.getEWDGraphVertices();
    }

    //creates a friendship between both parameter users, returns true if 
    //successful. If the names are invalid false will be returned.
    //If either user is not a current user, it will add them to the platform 
    //and then make them friends. If they're already friends or if someone tries 
    //to create a friendship themselves by putting two identical names as 
    //parameters false is returned.
    public boolean addFriends(String userName1, String userName2) {
        boolean result = false;
        
        //checks for invalid parameters
        if (userName1 != null && !userName1.equals("") && 
                userName2 != null && !userName2.equals("")) {
            
            result = (graph.newEWDGraphEdge(userName1, userName2, 1) && 
                    graph.newEWDGraphEdge(userName2, userName1, 1));
        }
                
        return result;
    }

    //returns a Collection of all the parameter users friends. If the user
    //has no friends, or isn't on the platform an empty Collection is returned.
    public Collection<String> getFriends(String userName) {
        //checks for invalid parameters
        if (userName == null) {
            return null;
        }
        
        return graph.getNeighborsOfVertex(userName);
    }

    //removes a friendship between both parameter users. If either of the 
    //parameter users isn't on the platform, or if they are on the platform but 
    //aren't friends, false is returned and nothing changes. Returns true if
    //successful.
    public boolean unfriend(String userName1, String userName2) {
        boolean result = false;
        
        //checks for invalid parameters
        if (userName1 != null && !userName1.equals("") && 
                userName2 != null && !userName2.equals("")) {
            
            result = (graph.removeEWDGraphEdge(userName1, userName2) && 
                    graph.removeEWDGraphEdge(userName2, userName1));
        }
                
        return result;
    }

    //returns a Collection of friends of friends of the parameter user
    //excluding people who are already friends of the user or the user himself.
    //If the parameter user isn't on the platform an empty Collection is 
    //returned. This is also returned if the user is on the platform but 
    //doesn't have any friends or his friends don't have friends.
    public Collection<String> peopleYouMayWannaKnow(String userName) {
        //checks for invalid parameters
        if (userName == null) {
            return null;
        }
        
        Set<String> newFriends = new HashSet<String>();
        
        if (graph.isEWDGraphVertex(userName)) {
            
            Collection<String> friends = graph.getNeighborsOfVertex(userName);
        
            for (String s: friends) {
                for (String v: graph.getNeighborsOfVertex(s)) {
                    
                    //doesn't add any people who are already friends of the 
                    //parameter user or the user himself
                    if (!v.equals(userName) && !friends.contains(v)) {
                        newFriends.add(v);
                        
                    }
                }
            }
        }
        
        return newFriends;
    }

    //reads files from a Collection of file names and uses the contents of the
    //files to change the current Object. Creates threads, with each thread
    //reading a different file from the collection. Returns true if successful
    //and false otherwise. The threads are creating using an inner MyThread 
    //class that extends the Thread class. 
    public boolean readSocialNetworkData(Collection<String> filenames) {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        boolean result = false;
        
        if (filenames != null) {
            //creates threads for every file in the parameter collection and
            //puts it in the local Array list. 
            for (String s: filenames) {
                threads.add(new MyThread(s));
            }
        
            //starts all the threads and makes them execute code in run() 
            for (Thread t: threads) {
                t.start();
            }
        
            try {
                //joins all the threads (wait for them to finish) to ensure 
                //that when the method call is over no more changes happen to 
                //the current Fazebook object.
                for (Thread t: threads) {
                    t.join();
                }
            }
        
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        
            //if the parameter isn't invalid true is returned.
            result = true;
        
        }
        
        return result;
    }
    
    private class MyThread extends Thread {

        //field used to store the file name
        private String file;

        //constructor to initialize the field.
        public MyThread(String newFile) {
            file = newFile;
        }

        public void run() {
            //turns the file name into a file object
            File files = new File(file);
            Scanner input;
            
            try {
                //initializes the scanner to read the file
                input = new Scanner(files);
                String line;
                String[] arr;
                
                //while there is text left to read
                while (input.hasNextLine()) {
                    //stores each line of text from the file
                    line = input.nextLine();
                    
                    //splits the line into an array of words
                    arr = line.split("\\s+");
                    
                    //checks if the text in the file is instructing us to add a
                    //user or add a friendship
                    if (arr[0].equals("adduser")) {
                        //a lock from the Fazebook field is used to ensure only
                        //one thread edits this Fazebook object at a time
                        synchronized(lock){
                            //adds the user using the name listed in the file
                            addUser(arr[1]);
                        }
                    }
                    
                    else if (arr[0].equals("addfriends")) {
                        //same explanation as above lock
                        synchronized(lock){
                            //adds a friendship using the two following names
                            //in the line
                            addFriends(arr[1], arr[2]);
                        }
                    }
                }
                            
                //closes the scanner
                input.close();
            } 
            
            catch (FileNotFoundException e) {
            }
            
            catch(NullPointerException e) {
            }
        }
    }

}
