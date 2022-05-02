package antifraud.service;

import antifraud.entity.Card;
import antifraud.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Card addStolenCard(String number) {
        checkNumber(number);
        if (cardRepository.findByNumber(number).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        Card card = new Card(number);
        return cardRepository.save(card);
    }

    public void deleteStolenCard(String number) {
        checkNumber(number);
        Card card = cardRepository.findByNumber(number)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        cardRepository.delete(card);
    }

    public List<Card> getAllStolenCard() {
        return cardRepository.findAll();
    }

    public boolean exists(String number) {
        return cardRepository.findByNumber(number).isPresent();
    }

    public void checkNumber(String number) {
        int nDigits = number.length();

        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--) {
            int d = number.charAt(i) - '0';

            if (isSecond)
                d = d * 2;
            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        if (!(nSum % 10 == 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
