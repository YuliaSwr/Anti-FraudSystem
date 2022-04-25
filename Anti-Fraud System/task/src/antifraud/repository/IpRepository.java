package antifraud.repository;

import antifraud.entity.SuspIp;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface IpRepository extends CrudRepository<SuspIp, Long> {

    Optional<SuspIp> findByIp(String ip);

    List<SuspIp> findAll();

    boolean existsByIp(String ip);
}
