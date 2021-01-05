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

public class FriendRequestScene {

    @FXML
    private ListView<String> request_list;

    @FXML
    private TextArea bio_field;

    @FXML
    private TextField name_field, send_field;

    @FXML
    private Label no_requests, send_label;

    @FXML
    private ImageView profile_pic;

    private String sender = "";
    private String receiver = "";

    /**
     * goes back a scene
     * @param event
     * @throws IOException
     */
    public void pressedBackButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("FriendsScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * when a friend request is accepted, it refreshes the scene and the request is replaced
     * with a friendship
     * @param event
     * @throws IOException
     */
    public void pressedAcceptButton(ActionEvent event) throws IOException {
        Server.removeFriendRequest(sender, User.getUsername());
        Server.createFriendship(sender, User.getUsername());
        Parent pane = FXMLLoader.load(getClass().getResource("FriendRequestScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * when a friend request is denied, the request is deleted
     * @param event
     * @throws IOException
     */
    public void pressedDeclineButton(ActionEvent event) throws IOException {
        Server.removeFriendRequest(sender, User.getUsername());
        Parent pane = FXMLLoader.load(getClass().getResource("FriendRequestScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * sends a friend request if the user exists and is not already a friend
     * @param event
     * @throws IOException
     */
    public void pressedSendButton(ActionEvent event) throws IOException {
        receiver = send_field.getText();
        System.out.println(receiver);
        System.out.println(User.isFriend(receiver));
        if(!User.getUsername().toLowerCase().equals(receiver.toLowerCase())) {
            if (!Server.checkUsernameAvailability(receiver)) {
                if (!User.isFriend(receiver)) {
                    if (!User.isFriendRequest(receiver) && !User.isPending(receiver)) {
                        Server.removePotentialMatch(User.getUsername(), receiver);
                        Server.sendFriendRequest(User.getUsername(), receiver);
                        send_field.clear();
                        send_label.setText("Request sent!");
                    } else {
                        send_field.clear();
                        send_label.setText("There is already a request to this person!");
                    }
                } else {
                    send_field.clear();
                    send_label.setText("This user is already your friend!");
                }
            } else {
                send_field.clear();
                send_label.setText("That user does not exist!");
            }
        } else {
            send_field.clear();
            send_label.setText("You cannot add yourself!");
        }
    }

    /**
     * initializes the scene
     */
    public void initialize(){
        User.updateFriendRequests();
        ObservableList<String> names = FXCollections.observableArrayList();
        for(Object name:User.getFriendRequestNames()){
            names.add((String)name);
        }
        if(names.isEmpty()){
            no_requests.setVisible(true);
        }
        else{
            no_requests.setVisible(false);
        }
        request_list.setItems(names);


        request_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                sender = newValue;
                Map<String, Object> friend = User.getFriendRequestAttributes(newValue);
                name_field.setText(friend.get("first_name").toString() + ", "
                        + friend.get("age").toString());
                bio_field.setText( "Home Gym: " + Server.getUsersGym(newValue) + "\n"
                        + friend.get("bio").toString());
                profile_pic.setImage(ProfilePicture.getProfilePic(newValue));
            }
        });
    }
}
