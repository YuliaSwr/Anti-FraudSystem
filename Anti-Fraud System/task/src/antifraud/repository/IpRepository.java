package antifraud.repository;

import antifraud.entity.IP;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface IpRepository extends CrudRepository<IP, Long> {

    Optional<IP> findByIp(String ip);

    List<IP> findAll();

    boolean existsByIp(String ip);
}
