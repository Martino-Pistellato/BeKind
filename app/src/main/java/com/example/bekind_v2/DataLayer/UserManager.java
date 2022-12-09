package com.example.bekind_v2.DataLayer;

public class UserManager {
    private UserLoginRepository userLoginRepository;
    private UserDatabaseRepository userDatabaseRepository;

    public UserManager(UserLoginRepository userLoginRepository, UserDatabaseRepository userDatabaseRepository){
        this.userLoginRepository = userLoginRepository;
        this.userDatabaseRepository = userDatabaseRepository;
    }

    public void createUser(String password, String name, String surname, String birth, String email, String city, String street, String street_number, String neighbourhoodID){
        userLoginRepository.register(email, password);
        userDatabaseRepository.createUser(getUserId(), name, surname, birth, email, city, street, street_number, neighbourhoodID);
    }

    public void login(String email, String password){
        userLoginRepository.login(email, password);
    }

    public void logout(){
        userLoginRepository.logout();
    }

    public boolean isLogged(){
        return userLoginRepository.isLogged();
    }

    public String getUserId(){
        return userLoginRepository.getUid();
    }

    public String getEmail(){
        return userLoginRepository.getEmail();
    }

    public void updateUser(String name, String surname, String email, String city, String street, String street_number, String neighborhoodID, String oldPassword, String newPassword){
        userLoginRepository.updateCredentials(email,oldPassword,newPassword);
        userDatabaseRepository.updateUser(getUserId(), name, surname, email, city, street, street_number, neighborhoodID);
    }

    public String getName(){
        return userDatabaseRepository.getName(getUserId());
    }

    public String getSurname(){
        return userDatabaseRepository.getSurname(getUserId());
    }

    public String getBirth(){
        return userDatabaseRepository.getBirth(getUserId());
    }

    public String getCity(){
        return userDatabaseRepository.getCity(getUserId());
    }

    public String getStreet(){
        return userDatabaseRepository.getStreet(getUserId());
    }

    public String getStreetNumber(){
        return userDatabaseRepository.getStreetNumber(getUserId());
    }

    public String getNeighbourhoodID(){
        return userDatabaseRepository.getNeighbourhoodID(getUserId());
    }
}
