package com.example.BasicSecuritySetup.demo;

import com.example.BasicSecuritySetup.user.User;
import com.example.BasicSecuritySetup.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
public class DemoService {

    @Autowired
    UserRepository userRepository;




    public ResponseEntity<String> deleteById(Long id) {
       Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()){
            return ResponseEntity.ok("User not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User with id + " + id + " deleted");
    }
}
