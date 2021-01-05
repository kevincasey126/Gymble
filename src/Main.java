import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application { 
  @Override
  public void start(Stage primaryStage) throws Exception{
    
    Server.connectServer("bolt://localhost:7687", "neo4j", "password");
    ProfilePicture.connectS3();
    
    Parent root = FXMLLoader.load(getClass().getResource("OpeningScene.fxml"));
    Scene scene = new Scene(root, 600, 400);
    
    primaryStage.setTitle("Gymble");
    
    primaryStage.setScene(scene);
    primaryStage.show();
    
    }
    
  @Override
  public void stop(){
    Server.close();
  }
  
  
  public static void main(String[] args) {
    launch(args);
  }
}
