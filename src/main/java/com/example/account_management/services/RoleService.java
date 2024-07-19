package unicore.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import unicore.api.entities.Role;
import unicore.api.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }
}
