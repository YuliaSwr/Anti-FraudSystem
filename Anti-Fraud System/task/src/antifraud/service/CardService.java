package antifraud.service;

import antifraud.entity.Card;
import antifraud.entity.IP;
import antifraud.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Card addStolenCard(String number) {
        cardRepository.findByNumber(number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT));
        Card card = new Card(number);
        return cardRepository.save(card);
    }

    public void deleteStolenCard(String number) {
        Card card = cardRepository.findByNumber(number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        cardRepository.delete(card);
    }

    public List<Card> getAllStolenCard() {
        return cardRepository.findAll();
    }

    public boolean existInBlacklist(String number) {
        return cardRepository.existsByNumber(number);
    }
}
