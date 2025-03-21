package sn.groupeisi.projetgestionprofesseurs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.mindrot.jbcrypt.BCrypt;
import sn.groupeisi.projetgestionprofesseurs.dao.UserImplement;
import sn.groupeisi.projetgestionprofesseurs.entities.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class UserSceneController implements Initializable {

    private User currentUser = new User();
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private UserImplement userImplement = new UserImplement();
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private TableColumn<User, String> colEmail;

    @FXML
    private TableColumn<User, Long> colId;

    @FXML
    private TableColumn<User, String> colNom;

    @FXML
    private TableColumn<User, String> colPrenom;

    @FXML
    private TableColumn<User, User.Role> colRole;

    @FXML
    private TableView<User> tableUser;

    @FXML
    private TextField fieldEmail;

    @FXML
    private TextField fieldNom;

    @FXML
    private TextField fieldPassword;

    @FXML
    private TextField fieldPrenom;

    @FXML
    private Label lbEmail;

    @FXML
    private Label lbNom;

    @FXML
    private Label lbPassword;

    @FXML
    private Label lbPrenom;

    @FXML
    private Label lbRole;

    @FXML
    private ComboBox<User.Role> cmbRole;

    @FXML
    void addUser() {
        try {
            if (fieldNom.getText().isEmpty() || fieldPrenom.getText().isEmpty() || fieldEmail.getText().isEmpty() || fieldPassword.getText().isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Veuillez remplir tous les champs.").showAndWait();
                return;
            }
            User user = new User();
            user.setNom(fieldNom.getText());
            user.setPrenom(fieldPrenom.getText());
            user.setEmail(fieldEmail.getText());
            String password = BCrypt.hashpw(fieldPassword.getText(), BCrypt.gensalt());
            user.setPassword(password);
            user.setRole(cmbRole.getValue());
            userImplement.add(user);
            load();
            clearField();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void clearField() {
        ClearField();
        btnAdd.setDisable(false);
    }

    @FXML
    void deleteUser(ActionEvent event) {
        User userSelected = tableUser.getSelectionModel().getSelectedItem();
        if (userSelected != null) {
            userImplement.delete(userSelected.getId());
            load();
            clearField();
        }
    }

    @FXML
    void editUser(ActionEvent event) {
        User userSelected = tableUser.getSelectionModel().getSelectedItem();
        if (userSelected != null) {
            userSelected.setNom(fieldNom.getText());
            userSelected.setPrenom(fieldPrenom.getText());
            userSelected.setEmail(fieldEmail.getText());
            userSelected.setRole(cmbRole.getValue());
            userImplement.update(userSelected);
            load();
            clearField();
        }
    }

    private void load(){
        tableUser.getItems().setAll(userImplement.getAll());
        //cmbRole.getItems().addAll(User.Role.values());
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        btnClear.setVisible(false);
        btnAdd.setVisible(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        cmbRole.getItems().addAll(User.Role.values());
        load();
        tableUser.setOnMouseClicked(event -> handleTableClick(event));
    }

    private void handleTableClick(MouseEvent event){
        if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
            btnAdd.setDisable(true);
            btnAdd.setVisible(false);
            User selectedUser = tableUser.getSelectionModel().getSelectedItem();
            if(selectedUser != null){
                fieldNom.setText(selectedUser.getNom());
                fieldPrenom.setText(selectedUser.getPrenom());
                fieldEmail.setText(selectedUser.getEmail());
                //fieldPassword.setText(selectedUser.getPassword());
                lbPassword.setVisible(false);
                fieldPassword.setVisible(false);
                cmbRole.getSelectionModel().select(selectedUser.getRole());
                btnEdit.setVisible(true);
                btnDelete.setVisible(true);
                btnClear.setVisible(true);

            }
        }
    }

    private  void ClearField(){
        fieldNom.clear();
        fieldPrenom.clear();
        fieldEmail.clear();
        fieldPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
    }

}
