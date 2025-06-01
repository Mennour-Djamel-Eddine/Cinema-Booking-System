package model;

public class User {
    private int ID;
    private String Email, Name, Password;
    private UserRole userRole;

    public User(int ID, String Name, String Email, String Password, UserRole userRole) {
        this.ID = ID;
        this.Name = Name;
        this.Email = Email;
        this.userRole = userRole;
        this.Password = Password;
    }

    // getters and setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return Email;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }

    public UserRole getUserRole() {
        return userRole;
    }
}
