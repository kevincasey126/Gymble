import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;



import java.io.IOException;
import java.util.Map;

public class FriendsScene{

    @FXML
    private ListView friends_list = new ListView<>();

    @FXML
    private TextArea bio_field;

    @FXML
    private TextField name_field, contact_field;

    @FXML
    private Label no_friends;

    @FXML
    private ImageView profile_pic;

    public void pressedBackButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("MainMenuScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedFriendRequestButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("FriendRequestScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void initialize(){
        User.updateFriends();
        ObservableList<String> names = FXCollections.observableArrayList();
        for(Object name:User.getFriendsName()){
            names.add((String)name);
        }
        if(names.isEmpty()){
            no_friends.setVisible(true);
        }
        friends_list.setItems(names);


        friends_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!names.isEmpty()){
                    no_friends.setVisible(false);
                }
                Map<String, Object> friend = User.getFriendAttributes(newValue);
                name_field.setText(friend.get("first_name").toString() + " " +
                        friend.get("last_name").toString() + ", "
                        + friend.get("age").toString());
                bio_field.setText("Home Gym: " + Server.getUsersGym(newValue) + "\n"
                        + friend.get("bio").toString());
                profile_pic.setImage(ProfilePicture.getProfilePic(newValue));
                contact_field.setText("Contact info: " + friend.get("contactInfo"));

            }
        });
    }

}
