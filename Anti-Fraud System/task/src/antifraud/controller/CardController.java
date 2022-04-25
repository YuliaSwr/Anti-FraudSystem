package antifraud.controller;

import antifraud.entity.Card;
import antifraud.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/antifraud/stolencard")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping()
    public ResponseEntity<Card> addStolenCard(@RequestBody Map<String, String> request) {
            String number = request.get("number");
            return ResponseEntity.ok(cardService.addStolenCard(number));
    }

    @DeleteMapping("/{number}")
    public ResponseEntity<Map<String, String>> deleteStolenCard(@PathVariable String number) {
        cardService.deleteStolenCard(number);
        return ResponseEntity.ok(
                Map.of("status", "Card " + number + " successfully removed!"));
    }

    @GetMapping()
    public ResponseEntity<List<Card>> getAllStolenCard() {
        return ResponseEntity.ok(cardService.getAllStolenCard());
    }
}
