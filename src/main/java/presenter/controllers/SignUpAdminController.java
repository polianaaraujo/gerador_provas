package presenter.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.DAO.UserDAO;
import model.DAO.TeacherDAO;
import model.entities.Teacher;
import model.entities.User;

import model.services.UserService;
import model.services.TeacherService;
import model.entities.UserRole;
import presenter.utils.SceneManager;

public class SignUpController {
    @FXML
    private TextField lbEmail;

    @FXML
    private TextField lbName;

    @FXML
    private TextField lbNumber;

    @FXML
    private PasswordField lbPassword;

    @FXML
    private Label lbError;

    private UserService userService;
    private TeacherService teacherService;

    public SignUpController() {
        this.userService = new UserService(new UserDAO());
        this.teacherService = new TeacherService(new TeacherDAO(), userService);
    }

    @FXML
    public void btnSignUp(ActionEvent event) {
        String email = lbEmail.getText();
        String name = lbName.getText();
        String registrationNumber = lbNumber.getText();
        String password = lbPassword.getText();

        lbError.setText("");

        try {
            User newuser = new User(name, email, password, UserRole.TEACHER);
            Teacher newteacher = new Teacher(newuser, registrationNumber);
            teacherService.insert(newteacher);
            SceneManager.getInstance().navigateTo("/view/login.fxml", "Cadastro de Professor");

        } catch (Exception e) {
            lbError.setText(e.getMessage());
        }
    }

    @FXML
    public void btnLogin(ActionEvent event) {
        SceneManager.getInstance().navigateTo("/view/login.fxml", "Cadastro de Professor");
    }
}