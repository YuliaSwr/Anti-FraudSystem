package antifraud.service;

import antifraud.entity.IP;
import antifraud.repository.IPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class IPService {

    @Autowired
    private IPRepository ipRepository;

    public IP addSuspiciousIP(String ip) {
        if (ipRepository.findByIp(ip).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        IP currentIp = new IP(ip);
        ipRepository.save(currentIp);
        return currentIp;
    }

    public void deleteSuspiciousIP(String ip) {
        IP currentIp = ipRepository.findByIp(ip).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ipRepository.delete(currentIp);
    }

    public List<IP> getAllSuspiciousIP() {
        if (ipRepository.findAll().isEmpty()) {
            return new ArrayList<IP>();
        }
        return ipRepository.findAll();
    }

    public boolean existInBlacklist(String ip) {
        return ipRepository.existsByIp(ip);
    }
}
