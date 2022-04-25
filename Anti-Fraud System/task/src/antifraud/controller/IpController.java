package antifraud.controller;

import antifraud.entity.SuspIp;
import antifraud.service.IPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
public class IpController {

    @Autowired
    private IPService ipService;

    @PostMapping()
    public ResponseEntity<SuspIp> addSuspiciousIP(@RequestBody Map<String, String> request) {
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
    public ResponseEntity<List<SuspIp>> getAllSuspiciousIP() {
        return ResponseEntity.ok(ipService.getAllSuspiciousIP());
    }
}
