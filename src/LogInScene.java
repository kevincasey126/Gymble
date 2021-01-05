import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInScene {

  @FXML
  private TextField username_input;

  @FXML
  private PasswordField password_input;

  @FXML
  private Label error_code;

  /**
   * back button from the log in screen to the opening menu
   * @param event
   * @throws IOException
   */
  public void pressedBackButton(ActionEvent event) throws IOException {
    Parent pane = FXMLLoader.load(getClass().getResource("OpeningScene.fxml"));

    Scene scene = new Scene(pane);

    Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
    window.setScene(scene);
    window.show();
  }

  /**
   * attempting to log in and set the user's information
   * @param event
   * @throws IOException
   */
  public void pressedLogInButton(ActionEvent event) throws IOException{

    LogInReturn logInAttempt = Server.attemptLogIn(username_input.getText(),
        password_input.getText());

    if(logInAttempt.getSuccess()) {

      User.setAttributes(logInAttempt.getProfile());

      Parent pane = FXMLLoader.load(getClass().getResource("MainMenuScene.fxml"));

      Scene scene = new Scene(pane);

      Stage window = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
      window.setScene(scene);
      window.show();
    }
    else{
      error_code.setVisible(true);
    }
  }
}
