import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;


public class ProfileSettingsScene {

    @FXML
    private TextArea bio_area;

    @FXML
    private TextField name_field, contact_info;

    @FXML
    private ImageView profile_pic;

    public void pressedBackButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("MainMenuScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    public void pressedUpdateBioButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("UpdateBioScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    public void pressedUpdateProfilePicButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("UpdateProfilePicScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedUpdateGymButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("UpdateGymScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedUpdateContactInfoButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("UpdateContactInfoScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void initialize(){
        bio_area.setText("Home Gym: " + Server.getUsersGym(User.getUsername()) + "\n"
                + User.getBio());
        name_field.setText(User.getFirst_name() + " " + User.getLast_name() + ", " + User.getAge());
        contact_info.setText("Contact Info: " + User.getContactInfo());
        profile_pic.setImage(ProfilePicture.getProfilePic(User.getUsername()));



    }

}
