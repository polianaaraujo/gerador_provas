package presenter.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.DAO.TeacherDAO;
import model.DAO.UserDAO;
// Importe o AdminDAO e AdminService se já existirem no seu projeto
// import model.DAO.AdminDAO;
// import model.services.AdminService;
import model.entities.Teacher;
import model.entities.User;
import model.entities.UserRole;
// import model.entities.Admin;
import model.services.TeacherService;
import model.services.UserService;
import presenter.utils.SessionManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML private StackPane rootStackPane;
    @FXML private VBox vboxUsersList;

    @FXML private Button btnCreateUser;

    @FXML private VBox modalUserForm;
    @FXML private Label lblFormTitle;

    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbRole;
    @FXML private TextField txtRegistrationNumber;
    @FXML private Label lblRegistrationNumber;
    @FXML private Label lblPassword;

    private UserService userService;
    private TeacherService teacherService;

    private User currentEditingUser = null;

    public UserController() {
        this.userService = new UserService(new UserDAO());
        this.teacherService = new TeacherService(new TeacherDAO(), userService);
        // this.adminService = new AdminService(new AdminDAO(), userService); // Descomente quando for usar
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (modalUserForm != null) {
            modalUserForm.setVisible(false);
        }

        if (cbRole != null) {
            cbRole.getItems().addAll("ADMIN", "TEACHER");
            cbRole.valueProperty().addListener((observable, oldValue, newValue) -> {
                boolean isAdmin = "ADMIN".equals(newValue);
                txtRegistrationNumber.setVisible(!isAdmin);
                txtRegistrationNumber.setManaged(!isAdmin);
                lblRegistrationNumber.setVisible(!isAdmin);
                lblRegistrationNumber.setManaged(!isAdmin);
            });
        }

        applySessionRules();
        loadUsers();
    }

    private void applySessionRules() {
        User loggedUser = SessionManager.getInstance().getCurrentUser();

        if (btnCreateUser != null) {
            boolean isAdmin = loggedUser != null && loggedUser.getRole() == UserRole.ADMIN;
            btnCreateUser.setVisible(isAdmin);
            btnCreateUser.setManaged(isAdmin);
        }
    }

    private void loadUsers() {
        try {
            if (vboxUsersList != null) vboxUsersList.getChildren().clear();

            User loggedUser = SessionManager.getInstance().getCurrentUser();
            List<User> usersToRender = new ArrayList<>();

            if (loggedUser.getRole() == UserRole.ADMIN) {
                usersToRender = userService.findAll();
            } else {
                usersToRender.add(loggedUser);
            }

            for (User user : usersToRender) {
                HBox row = createUserRow(user, loggedUser);
                vboxUsersList.getChildren().add(row);
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Falha ao carregar usuários: " + e.getMessage());
        }
    }

    private HBox createUserRow(User targetUser, User loggedUser) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(10, 15, 10, 15));
        hbox.setSpacing(15);
        hbox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Label lblName = new Label(targetUser.getName());
        Label lblEmail = new Label(targetUser.getEmail());
        Label lblRole = new Label(targetUser.getRole().toString());
        Label lblStatus = new Label(targetUser.getStatus() ? "Ativo" : "Inativo");

        lblName.setPrefWidth(150);
        lblRole.setPrefWidth(100);
        lblStatus.setPrefWidth(80);

        lblEmail.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblEmail, Priority.ALWAYS);

        Button btnEdit = new Button();
        Button btnDelete = new Button();

        try {
            ImageView viewEdit = new ImageView(new Image(getClass().getResourceAsStream("/icons/editar.png")));
            ImageView viewDelete = new ImageView(new Image(getClass().getResourceAsStream("/icons/ativar.png")));

            viewEdit.setFitWidth(16); viewEdit.setFitHeight(16);
            viewDelete.setFitWidth(16); viewDelete.setFitHeight(16);

            btnEdit.setGraphic(viewEdit);
            btnDelete.setGraphic(viewDelete);
        } catch (NullPointerException e) {
            btnEdit.setText("Editar");
            btnDelete.setText("Excluir");
        }

        btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        btnEdit.setOnAction(event -> handleOpenEditModal(targetUser));
        btnDelete.setOnAction(event -> handleToggleUserStatus(targetUser));

        if (loggedUser.getRole() != UserRole.ADMIN || loggedUser.getIdUser() == targetUser.getIdUser()) {
            btnDelete.setVisible(false);
            btnDelete.setManaged(false);
        }

        HBox actionBox = new HBox(10, btnEdit, btnDelete);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPrefWidth(80);

        hbox.getChildren().addAll(lblName, lblEmail, lblRole, lblStatus, actionBox);
        return hbox;
    }

    @FXML
    public void handleOpenCreateModal(ActionEvent event) {
        currentEditingUser = null;
        clearFormFields();

        if (lblFormTitle != null) lblFormTitle.setText("Novo Usuário");

        setPasswordVisibility(true);
        cbRole.setDisable(false);

        modalUserForm.setVisible(true);
        modalUserForm.toFront();
    }

    private void handleOpenEditModal(User user) {
        currentEditingUser = user;
        User loggedUser = SessionManager.getInstance().getCurrentUser();

        if (lblFormTitle != null) lblFormTitle.setText("Editar Usuário");

        txtName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        cbRole.setValue(user.getRole().toString());

        cbRole.setDisable(true);

        if (user.getRole() == UserRole.TEACHER) {
            try {
                Teacher t = teacherService.findById(user.getIdUser());
                if (t != null) txtRegistrationNumber.setText(t.getRegistration_number());
            } catch (Exception e) {
                System.out.println("Não foi possível carregar o número de matrícula: " + e.getMessage());
            }
        } else {
            txtRegistrationNumber.clear();
        }

        boolean isEditingSelf = loggedUser.getIdUser() == user.getIdUser();
        setPasswordVisibility(isEditingSelf);

        modalUserForm.setVisible(true);
        modalUserForm.toFront();
    }

    @FXML
    public void handleCloseModal(ActionEvent event) {
        modalUserForm.setVisible(false);
        clearFormFields();
    }

    @FXML
    public void handleSaveUser(ActionEvent event) {
        User loggedUser = SessionManager.getInstance().getCurrentUser();

        String email = txtEmail.getText();
        String name = txtName.getText();
        String registrationNumber = txtRegistrationNumber.getText();
        String password = txtPassword.getText();
        String selectedRole = cbRole.getValue();

        if (name.isEmpty() || email.isEmpty() || selectedRole == null) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

        UserRole roleEnum = "ADMIN".equals(selectedRole) ? UserRole.ADMIN : UserRole.TEACHER;

        try {
            if (currentEditingUser == null) {
                if (loggedUser.getRole() != UserRole.ADMIN) {
                    showAlert(Alert.AlertType.ERROR, "Acesso Negado", "Apenas administradores podem criar usuários.");
                    return;
                }

                User newUser = new User(name, email, password, roleEnum);

                if (roleEnum == UserRole.TEACHER) {
                    if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                        showAlert(Alert.AlertType.WARNING, "Aviso", "O número de matrícula é obrigatório para professores.");
                        return;
                    }
                    Teacher newTeacher = new Teacher(newUser, registrationNumber);
                    teacherService.insert(newTeacher);
                } else {
                    userService.insert(newUser);
                }
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário criado com sucesso!");

            } else {
                currentEditingUser.setName(name);
                currentEditingUser.setEmail(email);

                if (txtPassword.isVisible() && password != null && !password.trim().isEmpty()) {
                    currentEditingUser.setPassword(password);
                }

                userService.update(currentEditingUser);

                if (roleEnum == UserRole.TEACHER) {
                    Teacher teacher = teacherService.findById(currentEditingUser.getIdUser());

                    if (teacher != null) {
                        teacher.setRegistration_number(registrationNumber);
                        teacherService.update(teacher);
                    } else {
                        Teacher newTeacher = new Teacher(currentEditingUser, registrationNumber);
                        teacherService.insert(newTeacher);
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário atualizado com sucesso!");
            }

            handleCloseModal(null);
            loadUsers();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro ao salvar usuário", e.getMessage());
        }
    }

    private void handleToggleUserStatus(User user) {
        boolean isCurrentlyActive = user.getStatus();

        String actionText = isCurrentlyActive ? "desativar" : "ativar";
        String successText = isCurrentlyActive ? "desativado" : "ativado";

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Tem certeza de que deseja " + actionText + " " + user.getName() + "?",
                ButtonType.YES, ButtonType.NO
        );
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            try {
                if (isCurrentlyActive) {
                    userService.inactivate(user.getIdUser());
                } else {
                    userService.activate(user.getIdUser());
                }

                loadUsers();
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Usuário " + successText + " com sucesso.");

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível " + actionText + ": " + e.getMessage());
            }
        }
    }

    private void clearFormFields() {
        if (txtName != null) txtName.clear();
        if (txtEmail != null) txtEmail.clear();
        if (txtPassword != null) txtPassword.clear();
        if (txtRegistrationNumber != null) txtRegistrationNumber.clear();
        if (cbRole != null) cbRole.getSelectionModel().clearSelection();
    }

    private void setPasswordVisibility(boolean isVisible) {
        txtPassword.setVisible(isVisible);
        txtPassword.setManaged(isVisible);
        if (lblPassword != null) {
            lblPassword.setVisible(isVisible);
            lblPassword.setManaged(isVisible);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}