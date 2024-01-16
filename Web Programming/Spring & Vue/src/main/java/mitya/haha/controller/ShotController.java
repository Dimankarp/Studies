package mitya.haha.controller;

import mitya.haha.model.ShotParams;
import mitya.haha.model.ShotRecordDTO;
import mitya.haha.model.User;
import mitya.haha.service.ShooterService;
import mitya.haha.service.ShotProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shots")
public class ShotController {

    private final ShooterService userService;
    private final ShotProcessingService shotService;

    @Autowired
    public ShotController(ShotProcessingService shotService, ShooterService userService) {
        this.shotService = shotService;
        this.userService = userService;
    }

    @GetMapping("/")
    public List<ShotRecordDTO> getShots(Authentication authentication) throws UsernameNotFoundException {
        User user = userService.loadUserByUsernameWithRecords(authentication.getName());
        return shotService.convertToDTO(user.getRecords());
    }

    @PostMapping("/")
    public ShotRecordDTO postShot(Authentication authentication, ShotParams params) throws UsernameNotFoundException {
        User user = userService.loadUserEntityByUsername(authentication.getName());
        return shotService.convertToDTO(shotService.registerShot(params, user));
    }

}
