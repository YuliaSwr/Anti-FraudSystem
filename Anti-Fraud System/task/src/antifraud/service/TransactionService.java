package antifraud.service;

import antifraud.entity.Card;
import antifraud.entity.IP;
import antifraud.entity.TransType;
import antifraud.entity.Transaction;
import antifraud.repository.IPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private CardService cardService;

    @Autowired
    private IPService ipService;

    public Map<String, String> transe(Transaction transaction) {
        if (transaction.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        List<String> info = new ArrayList<>();

        if (cardService.existInBlacklist(transaction.getNumber())) {
            info.add("card-number");
        }

        if (ipService.existInBlacklist(transaction.getIp())) {
            info.add("ip");
        }

        if (transaction.getAmount() <= 200 && info.isEmpty()) {
            return Map.of("result", TransType.ALLOWED.name(),
                    "info", "none");
        } else if (transaction.getAmount() > 200 && transaction.getAmount() <= 1500) {
            info.add("amount");
            String in = info.toString().replace('[', ' ').replace(']', ' ').trim();

            return Map.of("result", TransType.MANUAL_PROCESSING.name(),
                    "info", in);

        } else {
            info.add("amount");
            String in = info.toString().replace('[', ' ').replace(']', ' ').trim();

            return Map.of("result", TransType.PROHIBITED.name(),
                    "info", in);
        }
    }
}
