package com.ocbc.retailBank;

import com.ocbc.controller.UserController;
import com.ocbc.model.User;
import com.ocbc.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RetailBankApplicationTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserController userController;

    //Get List of Users from the Database
    public List<User> getUsers(){
        List<User> UserList = this.userController.getUsers();
        if(UserList.isEmpty()){
            System.out.println("Database is Empty");
            return null;
        }
            return UserList;
    }

    //Util class to find specific user from DB based on First Name
    public User findUser( String userName, List<User> UserList){
                if(UserList.isEmpty()){
            return null;
        }
        List<User> existingUser = UserList.stream().filter(user -> user.getFirstName().equalsIgnoreCase(userName)).collect(Collectors.toList());
        return existingUser.get(0);
    }

    public void printLoginDetails(User user){
        System.out.println("Hello, "+user.getFirstName()+"!");
        if(StringUtils.isNotBlank(user.getOweDebtFrom())){
            List<User> UserList = getUsers();
            User owedUser = findUser(user.getOweDebtFrom(),UserList);
              double pendingAmount = owedUser.getDebtBalance();
            System.out.println("Owing "+pendingAmount+" from " + user.getOweDebtFrom());
        }
        System.out.println("Your balance is, "+user.getBalance()+".");
        if(user.getDebtBalance() > 0){
            System.out.println("Owing "+user.getDebtBalance()+" to " + user.getPayDebtTo());
        }
    }

    public void printBalanceAmount(User user){
        if(StringUtils.isNotBlank(user.getOweDebtFrom())){
            List<User> UserList = getUsers();
            User owedUser = findUser(user.getOweDebtFrom(),UserList);
            double pendingAmount = owedUser.getDebtBalance();
            System.out.println("Owing "+pendingAmount+" from " + user.getOweDebtFrom());
        }
        System.out.println("Your balance is, "+user.getBalance()+".");
        if(user.getDebtBalance() > 0){
            System.out.println("Owing "+user.getDebtBalance()+" to " + user.getPayDebtTo());
        }
    }

    @Test
    @Order(1)
    public void createUserAlice() throws IOException {

        List<User> userList = getUsers();
        // Create User Alice if not present
        this.userController.userLogin("Alice");
        userList = getUsers();
        User createdUser = findUser("Alice", userList);
        printLoginDetails(createdUser);

    }

    @Test
    @Order(2)
    public void setInitialAmountToAlice() throws IOException {
        this.userController.userTopUp("Alice",100);
        List<User> userList  = getUsers();
        User user = findUser("Alice", userList);
        printBalanceAmount(user);
    }

    @Test
    @Order(3)
    public void createUserBob() throws IOException {
        List<User> userList = getUsers();
        // Create User Alice if not present
        this.userController.userLogin("Bob");
        userList = getUsers();
        User createdUser = findUser("Bob", userList);
        printLoginDetails(createdUser);

    }

    @Test
    @Order(4)
    public void setInitialAmountToBob() throws IOException {
        this.userController.userTopUp("Bob",80);
        List<User> userList  = getUsers();
        User user = findUser("Bob", userList);
        printBalanceAmount(user);
    }

    @Test
    @Order(5)
    public void sendPaymentToAlice_FromBob() throws IOException {
        this.userController.transfer("Bob",50,"Alice");
        List<User> userList  = getUsers();
        User user = findUser("Bob", userList);
        printBalanceAmount(user);
    }

    @Test
    @Order(6)
    public void sendPaymentToAlice_FromBob1() throws IOException {
        this.userController.transfer("Bob",100,"Alice");
        List<User> userList  = getUsers();
        User user = findUser("Bob", userList);
        printBalanceAmount(user);
    }

    @Test
    @Order(7)
    public void topUpBob_30() throws IOException {
        this.userController.userTopUp("Bob",30);
        //System.out.println("Transferred 30 to Alice.");
        List<User> userList  = getUsers();
        User user = findUser("Bob", userList);
        printBalanceAmount(user);
    }

    @Test
    @Order(8)
    public void loginAlice() throws IOException {

        List<User> userList = getUsers();
        // Create User Alice if not present
        this.userController.userLogin("Alice");
        userList = getUsers();
        User createdUser = findUser("Alice", userList);
        printLoginDetails(createdUser);

    }

    @Test
    @Order(9)
    public void sendPaymentToBob_FromAlice() throws IOException {
        this.userController.transfer("Alice",30,"Bob");
        List<User> userList  = getUsers();
        User user = findUser("Alice", userList);
        printBalanceAmount(user);

    }

    @Test
    @Order(10)
    public void loginBob() throws IOException {
        List<User> userList = getUsers();
        // Create User Alice if not present
        this.userController.userLogin("Bob");
        userList = getUsers();
        User createdUser = findUser("Bob", userList);
        printLoginDetails(createdUser);

    }

    @Test
    @Order(11)
    public void topUpBob_amount() throws IOException {
        this.userController.userTopUp("Bob",100);
        List<User> userList  = getUsers();
        User user = findUser("Bob", userList);
        printBalanceAmount(user);
    }

    @Test
    @Order(12)
    public void topUpBob_amount1() throws IOException {
        String ouptput =this.userController.userTopUp("Bo1b",100);
        System.out.println(ouptput);
    }
}
