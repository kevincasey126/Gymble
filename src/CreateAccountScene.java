import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CreateAccountScene {

  @FXML
  private TextField username_input, first_name_input, last_name_input, age_input;

  @FXML
  private TextField confirmed_password_input, password_input;

  @FXML
  private Label error_code;


  /**
   * Checks if any of the fields are empty
   * @return false if all fields are filled, true if any are empty
   */
  public boolean checkEmptyFields(){
    if(username_input.toString().trim().isEmpty() || first_name_input.toString().trim().isEmpty() ||
        last_name_input.toString().trim().isEmpty()|| age_input.toString().trim().isEmpty() ||
        confirmed_password_input.toString().trim().isEmpty() ||
        password_input.toString().trim().isEmpty()){
      return true;
    }
    return false;
  }

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
   * This function is used to finalize this account creation. The creation will not go through
   * if any of the fields are empty, the passwords are the same, or the username is taken.
   * @param event
   * @throws IOException
   */
  public void pressedCreateAccountButton(ActionEvent event) throws IOException {
    //this checks for empty fields
    if(checkEmptyFields()){
      error_code.setText("The fields are incomplete");
      error_code.setVisible(true);
    }

    //this checks for the confirmation of password
    else if(confirmed_password_input.getText().equals(password_input.getText())) {

      //this checks for the username availability and adds it
      if(Server.addPerson(username_input.getText(), password_input.getText(),
          first_name_input.getText(), last_name_input.getText(), age_input.getText(), "")) {

        User.setAttributes(Objects.requireNonNull(Server.getAttributes(username_input.getText())));

        Parent pane = FXMLLoader.load(getClass().getResource("MainMenuScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
      }
      else{
        error_code.setText("This username is taken");
        error_code.setVisible(true);
      }
    }
    else{
      error_code.setText("The password does not match");
      error_code.setVisible(true);
    }
  }

}
