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
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateGymScene {

    @FXML
    private ListView country_list, province_list, city_list, gym_list;

    @FXML
    private Label warning_label;

    public void pressedBackButton(ActionEvent event) throws IOException {

        Parent pane = FXMLLoader.load(getClass().getResource("ProfileSettingsScene.fxml"));

        Scene scene = new Scene(pane);

        Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    public void pressedUpdateButton(ActionEvent event) throws IOException{
        if(gym_list.getSelectionModel().isEmpty()){
            warning_label.setVisible(true);
        }
        else{
            Server.removeGymMembership(User.getUsername(), Server.getUsersGym(User.getUsername()));
            Server.setGymMemberShip(User.getUsername(),
                    gym_list.getSelectionModel().getSelectedItem().toString());
            Parent pane = FXMLLoader.load(getClass().getResource("ProfileSettingsScene.fxml"));

            Scene scene = new Scene(pane);

            Stage window = (Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }
    }


    public void initialize(){

        ObservableList<String> countries = FXCollections.observableArrayList();
        ObservableList<String> provinces = FXCollections.observableArrayList();
        ObservableList<String> cities = FXCollections.observableArrayList();
        ObservableList<String> gyms = FXCollections.observableArrayList();

        countries.addAll(Server.getAllCountries());
        FXCollections.sort(countries);

        country_list.setItems(countries);
        province_list.setItems(provinces);
        city_list.setItems(cities);
        gym_list.setItems(gyms);


        country_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                provinces.clear();
                cities.clear();
                gyms.clear();
                provinces.addAll(Server.getProvinces(newValue));
                FXCollections.sort(provinces);
                province_list.refresh();
            }
        });

        province_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                cities.clear();
                gyms.clear();
                cities.addAll(Server.getCities(newValue));
                FXCollections.sort(cities);
                city_list.refresh();
            }
        });

        city_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                gyms.clear();
                gyms.addAll(Server.getGymNames(newValue));
                FXCollections.sort(gyms);
                gym_list.refresh();
            }
        });
    }
}
