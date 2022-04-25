package antifraud.repository;

import antifraud.entity.IP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPRepository extends CrudRepository<IP, Long> {

    Optional<IP> findByIp(String ip);

    List<IP> findAll();

    boolean existsByIp(String ip);
}
