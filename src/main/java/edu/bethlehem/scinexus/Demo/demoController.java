package edu.bethlehem.scinexus.Demo;


import edu.bethlehem.scinexus.User.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SignatureException;

@RestController
@RequestMapping("api/v1/demo-controller")

public class demoController {

    @GetMapping
    public ResponseEntity<String> sayHello(){



        return ResponseEntity.ok("Hello from Sercured Endpoint");
    }
}