import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainMenuScene {


    /**
     * Pressed the log out button to log out
     * @param event
     * @throws IOException
     */
    public void pressedLogOutButton(ActionEvent event) throws IOException {
        User.clearAllAttributes();
        Parent pane = FXMLLoader.load(getClass().getResource("OpeningScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedFriendsButton(ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("FriendsScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedUpdateProfileButton(ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("ProfileSettingsScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedFindPeopleButton(ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("FindPeopleScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedMatchesButton(ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("MatchesScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
