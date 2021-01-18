package dtos;

import entities.User;


public class UserDTO {
    
    private String name, age, weight;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.name = user.getUserName();
        this.age = user.getAge();
        this.weight = user.getWeight();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
    
    
    
}
