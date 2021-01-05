import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class OpeningScene {

  @FXML
  private Button log_in_button;

  @FXML
  private Button create_account_button;

  public void pressedLogIn(ActionEvent event) throws IOException {
    Parent pane = FXMLLoader.load(getClass().getResource("LogInScene.fxml"));

    Scene scene = new Scene(pane);

    Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
    window.setScene(scene);
    window.show();
  }

  public void pressedCreateAccount(ActionEvent event) throws IOException {
    Parent pane = FXMLLoader.load(getClass().getResource("CreateAccountScene.fxml"));

    Scene scene = new Scene(pane);

    Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
    window.setScene(scene);
    window.show();
  }


}
