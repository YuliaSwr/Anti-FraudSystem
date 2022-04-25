package antifraud.controller;

import antifraud.entity.Card;
import antifraud.entity.IP;
import antifraud.service.CardService;
import antifraud.service.IPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("api/antifraud")
public class AntifraudController {

    @Autowired
    private IPService ipService;

    @Autowired
    private CardService cardService;

    @PostMapping("/suspicious-ip")
    public IP addSuspiciousIP(@RequestBody Map<String, String> request) {
        String ip = request.get("ip");
        return ipService.addSuspiciousIP(ip);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public Map<String, String> deleteSuspiciousIP(@PathVariable String ip) {
        ipService.deleteSuspiciousIP(ip);
        return Map.of("status", "IP " + ip + " successfully removed!");
    }

    @GetMapping("/suspicious-ip")
    public List<IP> getAllSuspiciousIP() {
        return ipService.getAllSuspiciousIP();
    }

    @PostMapping("/stolencard")
    public Card addStolenCard(@RequestBody Map<String, String> request) {
        String number = request.get("number");
        return cardService.addStolenCard(number);
    }

    @DeleteMapping("/stolencard/{number}")
    public Map<String, String> deleteStolenCard(@PathVariable String number) {
        cardService.deleteStolenCard(number);
        return Map.of("status", "IP " + number + " successfully removed!");
    }

    @GetMapping("/stolencard")
    public List<Card> getAllStolenCard() {
        return cardService.getAllStolenCard();
    }
}
