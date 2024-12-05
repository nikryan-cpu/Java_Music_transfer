package by.springarc.webapp;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("api")
public class ApiController {
 
    @GetMapping("ping")
    public String ping() {
        return "pong";
    }
 
}