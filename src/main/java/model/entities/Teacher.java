package model.entities;

public class Teacher {
    private User user;
    private String resgistration_number;

    public String getResgistration_number() {
        return resgistration_number;
    }

    public void setResgistration_number(String resgistration_number) {
        this.resgistration_number = resgistration_number;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

}
