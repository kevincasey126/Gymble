import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class MatchesScene {

    @FXML
    private Label no_matches, request_label;

    @FXML
    private ListView matches_list = new ListView<>();

    @FXML
    private TextArea bio_field;

    @FXML
    private TextField name_field;

    @FXML
    private ImageView profile_pic;


    public void pressedBackButton(ActionEvent event) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource("MainMenuScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedRemoveMatchButton(ActionEvent event) throws IOException{
        Server.removeMatch(User.getUsername(),
                matches_list.getSelectionModel().getSelectedItem().toString());
        User.updateMatches();

        Parent pane = FXMLLoader.load(getClass().getResource("MatchesScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedFriendRequestButton(ActionEvent event) throws IOException {
        Server.removeMatch(User.getUsername(),
                matches_list.getSelectionModel().getSelectedItem().toString());
        Server.sendFriendRequest(User.getUsername(),
                matches_list.getSelectionModel().getSelectedItem().toString());
        User.updateMatches();
        User.updateFriendRequests();

        request_label.setVisible(true);

        Parent pane = FXMLLoader.load(getClass().getResource("MatchesScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void initialize(){
        User.updateMatches();
        ObservableList<String> names = FXCollections.observableArrayList();
        for(Object name:User.getMatchesNames()){
            names.add((String)name);
        }
        request_label.setVisible(false);
        if(names.isEmpty()){
            no_matches.setVisible(true);
        }
        matches_list.setItems(names);


        matches_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!names.isEmpty()){
                    no_matches.setVisible(false);
                }
                Map<String, Object> match = User.getMatchesAttributes(newValue);
                name_field.setText(match.get("first_name").toString() + ", "
                        + match.get("age").toString());
                bio_field.setText("Home Gym: " + Server.getUsersGym(newValue) + "\n"
                        + match.get("bio").toString());
                profile_pic.setImage(ProfilePicture.getProfilePic(newValue));
            }
        });
    }
}
