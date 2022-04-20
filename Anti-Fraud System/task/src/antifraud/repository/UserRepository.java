package antifraud.repository;

import antifraud.entity.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {

    List<AppUser> findAll();

    Optional<AppUser> findByUsernameIgnoreCase(String username);
}
