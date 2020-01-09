package com.ecommerce.api.user;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class User {

    private @Id @GeneratedValue Long id;
    private String name;

    User(){};

    User(String name) {
        this.name = name;
    }

}
