import java.util.HashMap;
import java.util.Map;

public class User {

  public static String username = "";
  public static String first_name = "";
  public static String last_name = "";
  public static String age = "";
  public static String bio = "";
  public static String contact_info = "";

  public static HashMap<String, Map<String, Object>> friends = null;
  public static HashMap<String, Map<String, Object>> friend_requests = null;
  public static HashMap<String, Map<String, Object>> pending_requests = null;
  public static HashMap<String, Map<String, Object>> potential_matches = null;
  public static HashMap<String, Map<String, Object>> matches = null;

  /**
   * All user attributes must be set one by one, as they may not all be there from the query
   */
  private User(){}

  /**
   * This will update the user's attributes based on what they have saved in the server
   * @param attributes
   */
  public static void setAttributes(Map<String, Object> attributes){
    if(attributes.containsKey("username")){
      username = attributes.get("username").toString();
    }
    if(attributes.containsKey("first_name")){
      first_name = attributes.get("first_name").toString();
    }
    if(attributes.containsKey("last_name")){
      last_name = attributes.get("last_name").toString();
    }
    if(attributes.containsKey("age")){
      age = attributes.get("age").toString();
    }
    if(attributes.containsKey("bio")){
      bio = attributes.get("bio").toString();
    }
    if(attributes.containsKey("conatctInfo")){
      bio = attributes.get("contactInfo").toString();
    }
    updateFriends();
    updatePotentialMatches();
    updateFriendRequests();
    updatePendingRequests();
    updateMatches();
  }

  //Getters and setters
  /**
   * gets the username of the user
   * @return username
   */
  public static String getUsername(){
    return username;
  }

  /**
   * gets the first name of the user
   * @return string of first name
   */
  public static String getFirst_name(){
    return first_name;
  }

  /**
   * get the last name of the user
   * @return string of last name
   */
  public static String getLast_name(){
    return last_name;
  }

  /**
   * get the age of the user
   * @return string of user's age
   */
  public static String getAge(){
    return age;
  }

  /**
   * sets the username of the user
   * @param newUsername the given username
   */
  public static void setUsername(String newUsername){
    username = newUsername;
  }

  //Friend functions
  /**
   * gets a list of all friend's usernames
   * @return returns an object list of usernames
   */
  public static Object[] getFriendsName(){
    return friends.keySet().toArray();
  }

  /**
   * goes to the server to update all of a person's current friends
   */
  public static void updateFriends(){
    friends = Server.getAllFriends(username);
  }

  /**
   * gets a map of all a friend's attributes
   * @param friendUsername the friend to get attributes of
   * @return a map with all the friends attributes
   */
  public static Map<String, Object> getFriendAttributes(String friendUsername){
    return friends.get(friendUsername);
  }

  /**
   * is this user a friend
   * @param maybeFriend the username in question
   * @return true if friend, false other
   */
  public static boolean isFriend(String maybeFriend){
    Object[] friends = getFriendsName();
    for(Object name: friends){
      if(name.toString().toLowerCase().equals(maybeFriend.toLowerCase())){
        return true;
      }
    }
    return false;
  }

  //Friend request functions
  /**
   * gets a list of all friend request's usernames
   * @return returns an object list of usernames
   */
  public static Object[] getFriendRequestNames(){
    return friend_requests.keySet().toArray();
  }

  /**
   * goes to the server to update all of a person's current friends
   */
  public static void updateFriendRequests(){
    friend_requests = Server.getFriendRequests(username);
  }

  /**
   * gets a map of all a friend request's attributes
   * @param friendRequestUsername the friend request to get attributes of
   * @return a map with all the friend requests attributes
   */
  public static Map<String, Object> getFriendRequestAttributes(String friendRequestUsername){
    return friend_requests.get(friendRequestUsername);
  }

  /**
   * check if the friend request already exists
   * @param maybeRequest the user to check for a request already
   * @return true if the request already exists
   */
  public static boolean isFriendRequest(String maybeRequest){
    Object[] friend_requests = getFriendRequestNames();
    for(Object name: friend_requests){
      if(name.toString().toLowerCase().equals(maybeRequest.toLowerCase())){
        return true;
      }
    }
    return false;
  }

  //Pending friend requests (sent)
  /**
   * gets a list of all pending request's usernames
   * @return returns an object list of usernames
   */
  public static Object[] getPendingRequestNames(){
    return pending_requests.keySet().toArray();
  }

  /**
   * goes to the server to update all of a person's current pending request
   */
  public static void updatePendingRequests(){
    pending_requests = Server.getPendingRequests(username);
  }

  /**
   * gets a map of all a pending request's attributes
   * @param pendingRequestUsername
   * @return
   */
  public static Map<String, Object> getPendingRequestAttributes(String pendingRequestUsername){
    return pending_requests.get(pendingRequestUsername);
  }

  /**
   * checks if the user already sent this request
   * @param maybePending the user to check if a request was already sent
   * @return true if already pending, false otherwise
   */
  public static boolean isPending(String maybePending){
    Object[] pending_requests = getPendingRequestNames();
    for(Object name: pending_requests){
      if(name.toString().toLowerCase().equals(maybePending.toLowerCase())){
        return true;
      }
    }
    return false;
  }

  //Potential Matches functions
  /**
   * gets a list of all the potential match's names
   * @return returns an object list of potential matches
   */
  public static Object[] getPotentialMatchesNames(){
    return potential_matches.keySet().toArray();
  }

  /**
   * goes to the server to update all of a person's potential matches
   */
  public static void updatePotentialMatches(){
    potential_matches = Server.getAllPotentialMatches(username);
  }

  /**
   * get a map of all of a potential match's attributes
   * @param matchUsername the username of the potential match to get
   * @return a map with all the potential match's attributes
   */
  public static Map<String, Object> getPotentialMatchesAttributes(String matchUsername){
    return potential_matches.get(matchUsername);
  }

  //Matches functions

  /**
   * get a list of all the usernames of the user's matches
   * @return said list
   */
  public static Object[] getMatchesNames(){
    return matches.keySet().toArray();
  }

  /**
   * goes to the server to update all of a person's potential matches
   */
  public static void updateMatches(){
    matches = Server.getMatches(username);
  }

  /**
   * get the attributes of a specific match
   * @param matchUsername the match to find the usernames of
   * @return the attributes
   */
  public static Map<String, Object> getMatchesAttributes(String matchUsername){
    return matches.get(matchUsername);
  }

  //Bio functions
  /**
   * Set the user's bio
   * @param newBio the new bio
   */
  public static void setBio(String newBio){
    bio = newBio;
    Server.updateUserBio(username, newBio);
  }

  /**
   * get the user's bio
   * @return the user's bio
   */
  public static String getBio(){
    return bio;
  }

  //Contact info functions

  /**
   * set the user's contact info
   * @param new_info
   */
  public static void setConactInfo(String new_info){
    contact_info = new_info;
    Server.updateConatctInfo(username, new_info);
  }

  /**
   * get the user's contact info
   * @return
   */
  public static String getContactInfo(){
    return contact_info;
  }

  public static void clearAllAttributes(){
    username = "";
    first_name = "";
    last_name = "";
    age = "";

    friends = null;
    potential_matches = null;
    matches = null;
    friend_requests = null;
    pending_requests = null;
  }


}
