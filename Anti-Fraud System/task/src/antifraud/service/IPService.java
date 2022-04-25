package antifraud.service;

import antifraud.entity.IP;
import antifraud.repository.IpRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class IPService {

    private final IpRepository ipRepository;

    public IPService(IpRepository ipRepository) {
        this.ipRepository = ipRepository;
    }

    public IP addSuspiciousIP(String ip) {
        checkIP(ip);
        if (ipRepository.findByIp(ip).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        IP currentIp = new IP(ip);
        ipRepository.save(currentIp);
        return currentIp;
    }

    public void deleteSuspiciousIP(String ip) {
        checkIP(ip);
        IP currentIp = ipRepository.findByIp(ip).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        ipRepository.delete(currentIp);
    }

    public List<IP> getAllSuspiciousIP() {
        return ipRepository.findAll();
    }

    public boolean existInBlacklist(String ip) {
        return ipRepository.existsByIp(ip);
    }

    public void checkIP(String ip) {
        Pattern PATTERN = Pattern.compile(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        if (!PATTERN.matcher(ip).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT ip!");
        }
    }
}
