//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\model\User.java

package com.example.authdemo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;

    private String name;
    private String email;
    private String password;
}
