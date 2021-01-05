import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLOutput;

public class TestDriver {


  @Before
  public void serverTestSetUp(){
    //Server.connectServer("bolt://174.2.15.198:7687", "neo4j", "cmpt370");
    //this line is for my local testing, use my external IP above
    Server.connectServer("bolt://localhost:7687", "neo4j", "password");

    //this top line is temporary as it will literally delete all
    // nodes but the source before beginning
    Server.removeAll();
    Server.addCountry("Canada");
    Server.addCountry("United States");
    Server.addCountry("Mexico");
    Server.addProvince("Saskatchewan", "Canada");
    Server.addProvince("Alberta", "Canada");
    Server.addProvince("BritishColumbia", "Canada");
    Server.addCity("Saskatoon", "Saskatchewan");
    Server.addCity("Regina", "Saskatchewan");
    Server.addCity("PrinceAlbert", "Saskatchewan");
    Server.addGym("IronWorksGym", "Saskatoon");
    Server.addGym("Fit-4-Less", "Saskatoon");
    Server.addGym("MotionFitness", "Saskatoon");
    Server.addPerson("AdaUN", "password", "Ada", "AdaLN", "21", "AdaBio");
    Server.addPerson("AliceUN", "password", "Alice", "AliceLN", "22", "AliceBio");
    Server.addPerson("BobUN", "password", "Bob", "BobLN", "23", "BobBio");
    Server.addPerson("JoeUN", "password", "Joe", "JoeLN", "25", "JoeBio");
    Server.createFriendship("AdaUN", "BobUN");
    Server.createFriendship("AliceUN", "AdaUN");

  }

  @Test
  public void serverTesting(){
    //System.out.println("Testing get node count...");
    //Assert.assertEquals(3, Server.getNodeCount());
    //System.out.println("Passes");

    System.out.println("Testing check username availability...");
    Assert.assertEquals(false, Server.checkUsernameAvailability("BobUN"));
    System.out.println("Passes");

    System.out.println("Testing attempted log in failure...");
    LogInReturn logInAttempt1 = Server.attemptLogIn("BobUN", "asd");
    Assert.assertEquals(false, logInAttempt1.getSuccess());
    System.out.println("Passes");


    System.out.println("Testing add new person...");
    Assert.assertEquals(true, Server.addPerson("JeffUN", "password",
            "Jeff", "JeffLN", "21", "JeffBio"));
    System.out.println("Passes");

    System.out.println("Testing attempted log in success...");
    LogInReturn logInAttempt2 = Server.attemptLogIn("JeffUN", "password");
    Assert.assertEquals(true, logInAttempt2.getSuccess());
    System.out.println("Passes");

    System.out.println("Testing get attributes...");
    Assert.assertEquals("AdaUN", Server.getAttributes("AdaUN").get("username"));
    System.out.println("Passes");

    System.out.println("Testing remove person...");
    Server.removePerson("JeffUN");
    Assert.assertEquals(null, Server.getAttributes("JeffUN"));
    Assert.assertEquals(true, Server.checkUsernameAvailability("JeffUN"));
    System.out.println("Passes");

    System.out.println("Testing get all friends...");
    Assert.assertEquals(true, Server.getAllFriends("AdaUN").keySet().contains("BobUN"));
    Assert.assertEquals(true, Server.getAllFriends("AdaUN").keySet().contains("AliceUN"));
    System.out.println("Passes");

    System.out.println("Testing remove friendship...");
    Server.removeFriendship("AdaUN", "AliceUN");
    Assert.assertEquals(false, Server.getAllFriends("AdaUN").keySet().contains("AliceUN"));
    Assert.assertEquals(true, Server.getAllFriends("AdaUN").keySet().contains("BobUN"));
    Assert.assertEquals(false, Server.getAllFriends("AliceUN").keySet().contains("AdaUN"));
    System.out.println("Passes");

    System.out.println("Testing create friendship...");
    Server.createFriendship("AdaUN", "AliceUN");
    Assert.assertEquals(true, Server.getAllFriends("AdaUN").keySet().contains("AliceUN"));
    Assert.assertEquals(true, Server.getAllFriends("AliceUN").keySet().contains("AdaUN"));
    System.out.println("Passes");

    System.out.println("Testing update user bio...");
    Server.updateUserBio("BobUN", "ay yo its ya boi bobbby");
    Assert.assertEquals("ay yo its ya boi bobbby",
            Server.getAttributes("BobUN").get("bio").toString());
    System.out.println("Passes");

    System.out.println("Testing set membership...");
    Server.setGymMemberShip("BobUN", "IronWorksGym");
    Assert.assertEquals("IronWorksGym", Server.getUsersGym("BobUN"));
    System.out.println("Passes");

    System.out.println("Testing get user's gym...");
    Assert.assertEquals("IronWorksGym", Server.getUsersGym("BobUN"));
    Assert.assertEquals("No Gym Assigned", Server.getUsersGym("AliceUN"));
    System.out.println("Passes");


    System.out.println("Testing set membership...");
    Server.setGymMemberShip("JoeUN", "IronWorksGym");
    Server.setGymMemberShip("AdaUN", "IronWorksGym");
    Server.setGymMemberShip("AliceUN", "IronWorksGym");
    Assert.assertEquals("IronWorksGym", Server.getUsersGym("JoeUN"));
    System.out.println("Passes");

    System.out.println("Testing swiped on new pm...");
    Server.swipeOn("JoeUN", "AliceUN");
    Assert.assertEquals(true, Server.getCurrentSwipedOn("AliceUN").containsKey("JoeUN"));
    Assert.assertEquals(false, Server.getCurrentSwipedOn("JoeUN").containsKey("AliceUN"));
    Assert.assertEquals(true, Server.getAllPotentialMatches("AliceUN").containsKey("JoeUN"));
    Assert.assertEquals(false, Server.getAllPotentialMatches("JoeUN").containsKey("AliceUN"));
    System.out.println("Passes");

    System.out.println("Testing swipe to complete match...");
    Server.swipeOn("AliceUN", "JoeUN");
    Assert.assertEquals(false, Server.getCurrentSwipedOn("AliceUN").containsKey("JoeUN"));
    Assert.assertEquals(true, Server.getMatches("JoeUN").containsKey("AliceUN"));
    Assert.assertEquals(true, Server.getMatches("AliceUN").containsKey("JoeUN"));
    Assert.assertEquals(false, Server.getMatches("JoeUN").containsKey("BobUN"));
    System.out.println("Passes");

   /* System.out.println("Testing remove a match...");
    Server.removeMatch("AliceUN", "JoeUN");
    System.out.println(Server.getMatches("AliceUN"));
    Assert.assertEquals(false, Server.getMatches("AliceUN").containsKey("JoeUN"));
    Assert.assertEquals(false, Server.getMatches("JoeUN").containsKey("AliceUN"));
    System.out.println("Passes");*/

/*    //also works
    System.out.println("Testing set all potential matches");
    Server.createAllPotentialMatches("JoeUN");
    Assert.assertEquals(true, Server.getAllPotentialMatches("JoeUN").keySet().contains("AdaUN"));
    Assert.assertEquals(false, Server.getAllPotentialMatches("JoeUN").keySet().contains("AliceUN"));*/

    System.out.println("Great Success");


   /* System.out.println(Server.getAllCountries());
    System.out.println(Server.getProvinces("Canada"));
    System.out.println(Server.getCities("Saskatchewan"));
    System.out.println(Server.getGymNames("Saskatoon"));*/
    //Server.removeGymMembership("JoeUN", "IronWorksGym");
  }

  @After
  public void serverTestClose(){
    Server.close();
  }

}
