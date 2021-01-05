import org.neo4j.driver.*;
import org.neo4j.driver.Record;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class Server {

  //Driver to connect with the database server
  public static Driver driver;

  /**
   * This is a faked static class, should not be instanced
   */
  public Server(){}

  /**
   * connect to the database with a proper connection
   * @param uri localhost or the server IP
   * @param user the username of the server
   * @param password the password for the server
   */
  public static void connectServer(String uri, String user, String password){
    driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
  }

  /**
   * remove all nodes *DANGER*
   */
  public static void removeAll(){
    try(Session session = driver.session()){
      session.run("MATCH (n) DETACH DELETE n");
    }
  }

  //Log in function
  /**
   * this function checks all usernames as to not create duplicates, only needed when a new user is created
   * @param username the name of the new user
   * @return true if the username is available, false otherwise
   */
  public static boolean checkUsernameAvailability(String username) {
    try (Session session = driver.session()) {
      Result result = session.run("MATCH (a:Person) WHERE toLower(a.username)=$x1 RETURN a",
              parameters("x1", username.toLowerCase()));
      if(result.hasNext()){
        return false;
      }
      return true;
    }
  }

  /**
   * Logs in with username and password, passwords are now encypted
   * @param username: person's username
   * @param password: person's password
   * @return returns a custom class for a tuple that contains whether the query was a success,
   * and if it was all the attributes along side it
   */
  public static LogInReturn attemptLogIn(String username, String password){
    try(Session session = driver.session()){
      Result result = session.run("MATCH (a:Person) WHERE toLower(a.username)=$x1" +
                      " RETURN properties(a)",
              parameters("x1",username.toLowerCase()));
      if(result.hasNext()){
        Map<String, Object> returnable = result.next().get("properties(a)").asMap();

        if(PasswordHashing.checkHashedPassword(password, (byte[])returnable.get("password"),
                (byte[])returnable.get("salt"))) {
          return new LogInReturn(true, returnable);
        }
        else{
          return new LogInReturn(false, null);
        }

      }
    }
    return new LogInReturn(false, null);
  }

  //Person functions
  /**
   * this function returns all the attributes excluding passwords
   * @param username the username to get the attributes from
   * @return all the user's attributes in a map with the key as the attribute's name and the
   * value as the attribute's value
   */
  public static Map<String, Object> getAttributes(String username){
    try(Session session = driver.session()){
      Result result = session.run("MATCH (a:Person) WHERE toLower(a.username)=$x1" +
                      " RETURN properties(a)",
              parameters("x1",username.toLowerCase()));
      if(result.hasNext()){
        //find a way to remove the password, or never query it at all
        return result.next().get("properties(a)").asMap();

      }
    }
    return null;
  }

  /**
   * Add new node into the database, password needs to be encrypted
   * @param username: the name of the new person, other features will be added later
   * @return true if completed, false otherwise
   */
  public static boolean addPerson(String username, String password, String first_name,
                                  String last_name, String age, String bio) {
    if (checkUsernameAvailability(username)) {

      Map<String, byte[]> hashingInfo = PasswordHashing.hashNewPassword(password);

      try (Session session = driver.session()) {
        session.writeTransaction(transaction -> transaction.run("MERGE (a:Person " +
                        "{username:$x1, password:$x2, first_name:$x3, last_name:$x4, age:$x5," +
                        "bio:$x6, " +
                        "pic:\"default_profile_pic.png\"," +
                        "salt:$x7," +
                        "contactInfo:$x8})",
                parameters("x1", username, "x2", hashingInfo.get("hashedPassword"),
                        "x3", first_name, "x4", last_name, "x5", age, "x6", bio, "x7",
                        hashingInfo.get("salt"), "x8", "")));
        return true;
      }
    }
    else{
      System.out.println("this name is already taken");
      return false;
    }
  }

  /**
   * remove a node from the database using their name
   * @param username: name of the person to remove, will use different features in the future
   */
  public static void removePerson(String username){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run(
              "MATCH (a:Person) WHERE toLower(a.username)=$x DETACH DELETE a",
              parameters("x", username.toLowerCase())));
    }
  }


  /**
   * This function will update the user's bio
   * @param username the user that is trying to update their bio
   * @param newBio the new bio to set
   */
  public static void updateUserBio(String username, String newBio){
      try (Session session = driver.session()) {
        session.writeTransaction(transaction -> transaction.run("MATCH (a:Person) WHERE " +
                        "toLower(a.username)=$x1 SET a.bio=$x2",
                parameters("x1", username.toLowerCase(), "x2", newBio)));
      }
  }

  /**
   * this function will update the key to the profile picture
   * @param username the user looking to update their profile pic
   * @param new_pic the new key for the S3 server to the new picture
   */
  public static void updateProfilePicURL(String username, String new_pic){
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH (a:Person) WHERE " +
                      "toLower(a.username)=$x1 SET a.pic=$x2",
              parameters("x1", username.toLowerCase(), "x2", new_pic)));
    }
  }

  /**
   * this functionw ill update the contact info
   * @param username
   * @param new_info
   */
  public static void updateConatctInfo(String username, String new_info){
    try (Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH (a:Person) WHERE " +
                      "toLower(a.username)=$x1 SET a.contactInfo=$x2",
              parameters("x1", username.toLowerCase(), "x2", new_info)));
    }
  }

  //Friendship functions
  /**
   * Create a two way friendship between two nodes
   * @param username1: name of the first new friend
   * @param username2: name of the second new friend
   */
  public static void createFriendship(String username1, String username2){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 and toLower(b.username)=$x2 " +
              "CREATE (a)-[f:FRIENDS]->(b) " +
              "RETURN a,b", parameters("x1", username1.toLowerCase(),
              "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
              "CREATE (a)-[f:FRIENDS]->(b) " +
              "RETURN a,b", parameters("x1", username2.toLowerCase(),
              "x2", username1.toLowerCase())));
    }
  }

  /**
   * Gets all of a user's friends, with all of their attributes
   * @param username the user to find their friends
   * @return this will return a hashmap with all the friend's usernames as the keys, and
   * their properties as the value map, with this keys as the attribute and value as the values
   */
  public static HashMap<String, Map<String, Object>> getAllFriends(String username){
    HashMap<String, Map<String, Object>> friends = new HashMap<>();
    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)-[:FRIENDS]->(b:Person) " +
              "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1",
              username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        friends.put(record.get("properties(b)").get("username").asString(),
                friend);
      }
    }
    return friends;
  }

  /**
   * remove a friendship between two nodes, in both directions
   * @param username1: name of the first friend to remove
   * @param username2: name of the second friend to remove
   */
  public static void removeFriendship(String username1, String username2){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (a)-[f:FRIENDS]->(b) " +
                      "DELETE f",
              parameters("x1", username1.toLowerCase(), "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (b)-[f:FRIENDS]->(a) " +
                      "DELETE f",
              parameters("x1", username1.toLowerCase(), "x2", username2.toLowerCase())));
    }
  }

  //Friend request functions
  /**
   * username1 sends a friend request to username2, with a pending relationship in the other
   * direction
   * @param sender the user sending the request
   * @param receiver the user getting the request
   */
  public static void sendFriendRequest(String sender, String receiver){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 and toLower(b.username)=$x2 " +
              "CREATE (a)-[f:FRIENDREQUEST]->(b) " +
              "RETURN a,b", parameters("x1", sender.toLowerCase(),
              "x2", receiver.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
              "CREATE (a)-[f:REQUESTPENDING]->(b) " +
              "RETURN a,b", parameters("x1", receiver.toLowerCase(),
              "x2", sender.toLowerCase())));
    }
  }

  /**
   * remove a friend request, either when accepted to upgrade to friendship or when denied
   * @param sender the user who sent the request
   * @param receiver the user who received the request
   */
  public static void removeFriendRequest(String sender, String receiver){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (a)-[r:FRIENDREQUEST]->(b) " +
                      "DELETE r",
              parameters("x1", sender.toLowerCase(), "x2", receiver.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (b)-[p:REQUESTPENDING]->(a) " +
                      "DELETE p",
              parameters("x1", sender.toLowerCase(), "x2", receiver.toLowerCase())));
    }
  }

  /**
   * gets all of a user's requests, with all of their attributes
   * @param username the user to get their requests
   * @return this will return a hashmap with all the request's usernames as the keys, and
   * their properties as the value map, with this keys as the attribute and value as the values
   */
  public static HashMap<String, Map<String, Object>> getFriendRequests(String username){
    HashMap<String, Map<String, Object>> requests = new HashMap<>();
    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)<-[:FRIENDREQUEST]-(b:Person) " +
              "WHERE toLower(a.username)=$x1 Return properties(b) ",
              parameters("x1", username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        requests.put(record.get("properties(b)").get("username").asString(),
                friend);
      }
    }
    return requests;
  }

  /**
   * get the people this user has sent requests to
   * @param username the user to find the requests from
   * @return this will return a hashmap with all the pending request's usernames as the keys, and
   * their properties as the value map, with this keys as the attribute and value as the values
   */
  public static HashMap<String, Map<String, Object>> getPendingRequests(String username){
    HashMap<String, Map<String, Object>> requests = new HashMap<>();
    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)<-[:REQUESTPENDING]-(b:Person) " +
              "WHERE toLower(a.username)=$x1 Return properties(b) ",
              parameters("x1", username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        requests.put(record.get("properties(b)").get("username").asString(),
                friend);
      }
    }
    return requests;
  }

  /**
   * this function removes the friend request and upgrades it to a friendship
   * @param sender the user that sent the friend request
   * @param receiver the user that received the request
   */
  public static void acceptFriendRequest(String sender, String receiver){
    removeFriendRequest(sender, receiver);
    createFriendship(sender, receiver);
  }

  //Gym Functions

  /**
   * takes a gym name and a username and assigned the user to that gym
   * @param username the name of the new member
   * @param gymName the gym name
   */
  public static void setGymMemberShip(String username, String gymName){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Gym) " +
              "WHERE toLower(a.username)=$x1 and b.name=$x2 " +
              "CREATE (a)-[m:MEMBER]->(b) " +
              "RETURN a,b", parameters("x1", username.toLowerCase(),
              "x2", gymName)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Gym),(b:Person) " +
              "WHERE a.name=$x1 AND toLower(b.username)=$x2 " +
              "CREATE (a)-[m:MEMBER]->(b) " +
              "RETURN a,b", parameters("x1", gymName,
              "x2", username.toLowerCase())));
      createAllPotentialMatches(username);
    }
  }

  /**
   * removes a user from being a member at a gym, including potential matches
   * @param username the user leaving the gym
   * @param gymName the gym they are leaving
   */
  public static void removeGymMembership(String username, String gymName){
    try(Session session = driver.session()) {
      removeAllPotentialMatches(username);
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Gym) " +
                      "WHERE toLower(a.username)=$x1 AND b.name=$x2 " +
                      "MATCH (a)-[m:MEMBER]->(b) " +
                      "DELETE m",
              parameters("x1", username.toLowerCase(), "x2", gymName)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Gym) " +
                      "WHERE toLower(a.username)=$x1 AND b.name=$x2 " +
                      "MATCH (b)-[m:MEMBER]->(a) " +
                      "DELETE m",
              parameters("x1", username.toLowerCase(), "x2", gymName)));
    }
  }

  /**
   * return a string with the name of the gym
   * @param username the user to check for gym status
   * @return the name as a string or No Gym Assigned
   */
  public static String getUsersGym(String username) {
    try (Session session = driver.session()) {
      Result result = session.run("Match (a:Person)-[:MEMBER]->(b:Gym) " +
              "WHERE a.username=$x1 Return properties(b) ", parameters("x1", username));
      if (result.hasNext()) {
        //Some names were having issues with unexpected quotes, this just clears it
        return result.next().get("properties(b)").get("name").toString().replace("\"", "");
      }
      return "No Gym Assigned";
    }
  }

  /**
   * get all the members at the given gym
   * @param gymName the gym name
   * @return a list of the members names
   */
  public static Object[] getAllMemberUsernames(String gymName){
    HashMap<String, Map<String, Object>> members = new HashMap<>();
    try(Session session = driver.session()){
      Result result = session.run("Match (a:Gym)-[:MEMBER]->(b:Person) " +
              "WHERE a.name=$x1 Return properties(b) ", parameters("x1", gymName));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> member = record.get("properties(b)").asMap();
        members.put(record.get("properties(b)").get("username").asString(),
                member);
      }
    }
    return members.keySet().toArray();
  }


  //Potential match functions
  /**
   * Creates a potential match between two people
   * @param username1 the first username
   * @param username2 the second username
   */
  public static void createPotentialMatch(String username1, String username2){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 and toLower(b.username)=$x2 " +
              "CREATE (a)-[f:PM]->(b) " +
              "RETURN a,b", parameters("x1", username1.toLowerCase(),
              "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 and toLower(b.username)=$x2 " +
              "CREATE (a)-[f:PM]->(b) " +
              "RETURN a,b", parameters("x1", username2.toLowerCase(),
              "x2", username1.toLowerCase())));
    }
  }

  /**
   * When a user first assigns themself to a new gym, this function will use the gym members to
   * create potential new matches
   * @param username the user to get all the matches for
   */
  public static void createAllPotentialMatches(String username){
    HashMap<String, Map<String, Object>> currentFriends = getAllFriends(username);
    HashMap<String, Map<String, Object>> currentPMs = getAllPotentialMatches(username);
    Object[] gymMembers = getAllMemberUsernames(getUsersGym(username));
    System.out.println(gymMembers.length);
    for(Object member: gymMembers){
      if(!currentFriends.containsKey(member.toString()) && !member.toString().equals(username) &&
         !currentPMs.containsKey(member.toString())){
        createPotentialMatch(member.toString(), username);
      }
    }
  }

  /**
   * Removes both directions of a potential match
   * @param username1 the first username
   * @param username2 the second username
   */
  public static void removePotentialMatch(String username1, String username2){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (a)-[p:PM]->(b) " +
                      "DELETE p",
              parameters("x1", username1.toLowerCase(), "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (b)-[p:PM]->(a) " +
                      "DELETE p",
              parameters("x1", username1.toLowerCase(), "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (a)-[r:SWIPED]->(b) " +
                      "DELETE r",
              parameters("x1", username1.toLowerCase(), "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (b)-[p:SWIPED]->(a) " +
                      "DELETE p",
              parameters("x1", username1.toLowerCase(), "x2", username2.toLowerCase())));
    }
  }

  /**
   * removes all the potential matches attached to a user
   * @param username
   */
  public static void removeAllPotentialMatches(String username){
    Object[] gymMembers = getAllMemberUsernames(getUsersGym(username));
    for(Object member: gymMembers) {
      removePotentialMatch(member.toString(), username);
    }
  }

  /**
   * Get all of one user's potential matches, with their attributes
   * @param username the user to get the potential matches from
   * @return a map with the key as strings of the pm's name, and the value as a attribute map
   */
  public static HashMap<String, Map<String, Object>> getAllPotentialMatches(String username){
    HashMap<String, Map<String, Object>> potentials = new HashMap<>();
    try(Session session = driver.session()){
      Result resultPMs = session.run("Match (a:Person)-[:PM]->(b:Person) " +
              "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1",
              username.toLowerCase()));
      while(resultPMs.hasNext()){
        Record record = resultPMs.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        potentials.put(record.get("properties(b)").get("username").asString(),
                friend);
      }
    }
    return potentials;
  }


  //Partial matches

  /**
   * gets a hashmap of all the the other users that have already swiped in the user that is put
   * into this function
   * @param username
   * @return
   */
  public static HashMap<String, Map<String, Object>> getCurrentSwipedOn(String username) {
    HashMap<String, Map<String, Object>> swipers = new HashMap<>();
    try(Session session = driver.session()){
      Result resultSwipes = session.run("Match (a:Person)<-[:SWIPED]-(b:Person) " +
              "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1",
              username.toLowerCase()));
      while (resultSwipes.hasNext()) {
        Record record = resultSwipes.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        swipers.put(record.get("properties(b)").get("username").asString(),
                friend);
      }
    }
    return swipers;
  }

  /**
   * When a user (the swiper) swipes on a potential match, it will get a list of the other users
   * that have already swiped on them to determine if there should be a match or not. If there is
   * no swipe done already then just one of the relationships are are upgraded to "swiped". If
   * there is a swipe already then
   * @param swiper
   * @param swipee
   */
  public static void swipeOn(String swiper, String swipee){
    HashMap<String, Map<String, Object>> alreadySwiped = getCurrentSwipedOn(swiper);
    if(alreadySwiped.containsKey(swipee)){
      removePotentialMatch(swiper, swipee);
      createMatch(swiper, swipee);
    }
    else{
      try(Session session = driver.session()) {
        session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                        "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                        "MATCH (a)-[p:PM]->(b) " +
                        "DELETE p",
                parameters("x1", swiper.toLowerCase(), "x2", swipee.toLowerCase())));
        session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                        "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                        "MERGE (a)-[p:SWIPED]->(b) " +
                        "RETURN a,b",
                parameters("x1", swiper.toLowerCase(), "x2", swipee.toLowerCase())));
      }
    }

  }


  //Match functions

  /**
   * create a match between two people
   * @param username1
   * @param username2
   */
  public static void createMatch(String username1, String username2){
    try(Session session = driver.session()){
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 and toLower(b.username)=$x2 " +
              "MERGE (a)-[f:MATCH]->(b) " +
              "RETURN a,b", parameters("x1", username1.toLowerCase(),
              "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
              "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
              "MERGE (a)-[f:MATCH]->(b) " +
              "RETURN a,b", parameters("x1", username2.toLowerCase(),
              "x2", username1.toLowerCase())));
    }
  }

  /**
   * get a map of all the matches of a user
   * @param username
   * @return
   */
  public static HashMap<String, Map<String, Object>> getMatches(String username){
    HashMap<String, Map<String, Object>> matches = new HashMap<>();
    try(Session session = driver.session()){
      Result result = session.run("Match (a:Person)-[:MATCH]->(b:Person) " +
              "WHERE toLower(a.username)=$x1 Return properties(b) ", parameters("x1",
              username.toLowerCase()));
      while(result.hasNext()){
        Record record = result.next();
        Map<String, Object> friend = record.get("properties(b)").asMap();
        matches.put(record.get("properties(b)").get("username").asString(),
                friend);
      }
    }
    return matches;
  }

  /**
   * remove a match between two users
   * @param username1
   * @param username2
   */
  public static void removeMatch(String username1, String username2){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (a)-[m:MATCH]->(b) " +
                      "DELETE m",
              parameters("x1", username1.toLowerCase(), "x2", username2.toLowerCase())));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Person),(b:Person) " +
                      "WHERE toLower(a.username)=$x1 AND toLower(b.username)=$x2 " +
                      "MATCH (b)-[m:MATCH]->(a) " +
                      "DELETE m",
              parameters("x1", username1.toLowerCase(), "x2", username2.toLowerCase())));
    }
  }

  //Location Functions

  /**
   * adds a country to the db
   * @param country
   */
  public static void addCountry(String country){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run(("MERGE (a:Country{name:$x1})"),
              parameters("x1", country)));
    }
  }

  /**
   * add a province with a link to a country
   * @param province
   * @param country
   */
  public static void addProvince(String province, String country){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run(("MERGE (a:Province {name:$x1})"),
              parameters("x1", province)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Country),(b:Province) " +
              "WHERE a.name=$x1 and b.name=$x2 " +
              "CREATE (a)-[p:PROVINCE]->(b) " +
              "RETURN a,b", parameters("x1", country,
              "x2", province)));
    }
  }

  /**
   * add a city with a relationship with a province
   * @param city
   * @param province
   */
  public static void addCity(String city, String province){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run(("MERGE (a:City {name:$x1})"),
              parameters("x1", city)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:Province),(b:City) " +
              "WHERE a.name=$x1 and b.name=$x2 " +
              "CREATE (a)-[p:CITY]->(b) " +
              "RETURN a,b", parameters("x1", province,
              "x2", city)));
    }
  }

  /**
   * add a gym with a relationship with a city
   * @param gym
   * @param city
   */
  public static void addGym(String gym, String city){
    try(Session session = driver.session()) {
      session.writeTransaction(transaction -> transaction.run(("MERGE (a:Gym {name:$x1})"),
              parameters("x1", gym)));
      session.writeTransaction(transaction -> transaction.run("MATCH(a:City),(b:Gym) " +
              "WHERE a.name=$x1 and b.name=$x2 " +
              "CREATE (a)-[p:GYM]->(b) " +
              "RETURN a,b", parameters("x1", city,
              "x2", gym)));
    }
  }

  /**
   * this function will query and obtain all the countries
   * @return the list of countries names as strings
   */
  public static List<String> getAllCountries() {
    List<String> country_names = new ArrayList<>();
    try (Session session = driver.session()) {
      Result result = session.run("MATCH (country:Country) RETURN country");
      while (result.hasNext()) {
        Record record = result.next();
        country_names.add(record.get("country").get("name").toString().replace("\"", ""));
      }
    }
    return country_names;
  }

  /**
   * returns a list of all the provinces in a country
   * @param country the country to find the provinces within
   * @return the list of gym name strings
   */
  public static List<String> getProvinces(String country){
    List<String> province_names = new ArrayList<>();
    try (Session session = driver.session()) {
      Result result = session.run("MATCH (country:Country)-[:PROVINCE]->(province:Province) " +
              "WHERE country.name=$x1 RETURN province.name AS prov_name",
              parameters("x1", country));
      while (result.hasNext()) {
        Record record = result.next();
        province_names.add(record.get("prov_name").toString().replace("\"", ""));
      }
    }
    return province_names;
  }

  /**
   * returns a list of all the cities in a province
   * @param prov the province to find the cities within
   * @return
   */
  public static List<String> getCities(String prov){
    List<String> city_names = new ArrayList<>();
    try (Session session = driver.session()) {
      Result result = session.run("MATCH (province:Province)-[:CITY]->(city:City) " +
                      "WHERE province.name=$x1 RETURN city.name AS city_name",
              parameters("x1", prov));
      while (result.hasNext()) {
        Record record = result.next();
        city_names.add(record.get("city_name").toString().replace("\"", ""));
      }
    }
    return city_names;
  }

  /**
   * returns a list of all the gyms in the city
   * @param city the city to find the gyms within
   * @return the list of gym name strings
   */
  public static List<String> getGymNames(String city){
    List<String> gym_names = new ArrayList<>();
    try (Session session = driver.session()) {
      Result result = session.run("MATCH (city:City)-[:GYM]->(gym:Gym) " +
                      "WHERE city.name=$x1 RETURN gym.name AS gym_name",
              parameters("x1", city));
      while (result.hasNext()) {
        Record record = result.next();
        gym_names.add(record.get("gym_name").toString().replace("\"", ""));
      }
    }
    return gym_names;
  }

  /**
   * counts how many nodes are in the database, excluding the source node
   * @return the int of how many nodes there are for testing purposes
   */
  public static int getNodeCount(){
    try(Session session = driver.session()) {
      Result result = session.run("MATCH (n) RETURN n");
      int count = (int)result.stream().count();
      return count;
    }
  }

  /**
   * close out of driver after use
   */
  public static void close(){

    driver.close();
  }
}
