package antifraud.controller;

import antifraud.entity.IP;
import antifraud.service.IPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
public class IpController {

    private final IPService ipService;

    public IpController(IPService ipService) {
        this.ipService = ipService;
    }

    @PostMapping()
    public ResponseEntity<IP> addSuspiciousIP(@RequestBody Map<String, String> request) {
        String ip = request.get("ip");
        return ResponseEntity.ok(ipService.addSuspiciousIP(ip));
    }

    @DeleteMapping("/{ip}")
    public ResponseEntity<Map<String, String>> deleteSuspiciousIP(@PathVariable String ip) {
        ipService.deleteSuspiciousIP(ip);
        return ResponseEntity.ok(
                Map.of("status", "IP " + ip + " successfully removed!"));
    }

    @GetMapping()
    public ResponseEntity<List<IP>> getAllSuspiciousIP() {
        return ResponseEntity.ok(ipService.getAllSuspiciousIP());
    }
}
