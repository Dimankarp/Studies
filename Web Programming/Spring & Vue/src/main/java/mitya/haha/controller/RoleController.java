package mitya.haha.controller;

import mitya.haha.model.AccessLevel;
import mitya.haha.model.Role;
import mitya.haha.repository.RolesRepository;
import mitya.haha.service.RoleAlreadyRegisteredException;
import mitya.haha.service.RolesService;
import mitya.haha.service.ShooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RolesService rolesService;
    private final ShooterService  shooterService;

    @Autowired
    public RoleController(RolesService rolesService, RolesRepository rolesRepository, ShooterService shooterService) {
        this.rolesService = rolesService;
        this.shooterService = shooterService;
    }

    @PostMapping("/")
    public String addRole(@RequestParam("role") String roleName, @RequestParam("level") AccessLevel level) throws RoleAlreadyRegisteredException {
        rolesService.registerNewRole(roleName, level);
        return "Successfully created role!";
    }

    @PostMapping("/{username}")
    public String setRole(@PathVariable("username") String username, @RequestParam("role") String roleName) throws UsernameNotFoundException, RoleNotFoundException {
        Role role = rolesService.loadRoleEntityByRolename(roleName);
        shooterService.addRole(username, role);
        return "Successfully added role!";
    }

}
