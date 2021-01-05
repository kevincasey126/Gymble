import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateProfilePicScene {

    @FXML
    private ImageView profile_pic;


    public void pressedBackButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("ProfileSettingsScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedBrowseButton(ActionEvent event) throws IOException {
        ProfilePicture.setProfilePic();

        Parent pane = FXMLLoader.load(getClass().getResource("UpdateProfilePicScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();

    }

    public void initialize(){
        profile_pic.setImage(ProfilePicture.getProfilePic(User.getUsername()));
    }
}
