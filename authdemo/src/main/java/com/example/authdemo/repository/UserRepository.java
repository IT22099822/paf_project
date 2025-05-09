//C:\Users\Shevon\Downloads\authdemo\src\main\java\com\example\authdemo\repository\UserRepository.java

package com.example.authdemo.repository;

import com.example.authdemo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
