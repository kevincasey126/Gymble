import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class FindPeopleScene {

    @FXML
    private ImageView profile_pic;

    @FXML
    private TextField name_field;

    @FXML
    private TextArea bio_field;

    @FXML
    private Label no_pms;

    String currUsername = "";
    Map<String, Object> attributes = null;

    public void pressedBackButton(ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("MainMenuScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void updateFindPeopleProfile(){
        Random random = new Random();
        Object[] usernames = User.getPotentialMatchesNames();
        currUsername = usernames[random.nextInt(usernames.length)].toString();
        attributes = User.getPotentialMatchesAttributes(currUsername);
    }

    public void pressedYesButton(ActionEvent event) throws IOException{
            Server.swipeOn(User.username, currUsername);
            User.updatePotentialMatches();
            Parent pane = FXMLLoader.load(getClass().getResource("FindPeopleScene.fxml"));

            Scene scene = new Scene(pane);

            Stage window = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
    }

    public void pressedNoButton(ActionEvent event) throws IOException{
            Server.removePotentialMatch(User.getUsername(), currUsername);
            User.updatePotentialMatches();
            Parent pane = FXMLLoader.load(getClass().getResource("FindPeopleScene.fxml"));

            Scene scene = new Scene(pane);

            Stage window = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
    }

    public void initialize(){
        User.updatePotentialMatches();
        if(User.getPotentialMatchesNames().length > 0) {
            no_pms.setVisible(false);
            updateFindPeopleProfile();

            name_field.setText(attributes.get("first_name").toString() + ", "
                    + attributes.get("age").toString());
            bio_field.setText(attributes.get("bio").toString());
            profile_pic.setImage(ProfilePicture.getProfilePic(currUsername));
        }
        else{
            no_pms.setVisible(true);
        }
    }
}
