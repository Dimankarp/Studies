package mitya.haha.controller;

import mitya.haha.model.ShotParams;
import mitya.haha.model.ShotRecord;
import mitya.haha.model.User;
import mitya.haha.repository.ShotRecordRepository;
import mitya.haha.repository.UserRepository;
import mitya.haha.service.ShooterDetailsService;
import mitya.haha.service.ShotProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/open/shots")
public class ShotController {

    private final ShooterDetailsService userService;
    private final UserRepository userRepository;
    private final ShotProcessingService shotService;
    private final ShotRecordRepository shotRepository;
    @Autowired
    public ShotController(ShotProcessingService shotService, ShotRecordRepository shotRepository, ShooterDetailsService userService, UserRepository userRepository){
        this.shotService = shotService;
        this.userService = userService;
        this.shotRepository = shotRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{username}")
    @Transactional
    public List<ShotRecord> getShots(@PathVariable("username") String username){

        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if(userOptional.isPresent()){
            return userOptional.get().getRecords();
        }
        else{
            return new ArrayList<>();
        }
    }

    @PostMapping("/{username}")
    @Transactional
    public void postShot(@PathVariable("username") String username, ShotParams params){
        Optional<User> userOptional = userRepository.getUserByUsername(username);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            shotService.registerShot(params, user);
        }
    }

}
