package antifraud.service;

import antifraud.entity.TransType;
import antifraud.entity.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final CardService cardService;

    private final IPService ipService;

    private TransType transType;
    private List<String> info;

    public TransactionService(CardService cardService, IPService ipService) {
        this.cardService = cardService;
        this.ipService = ipService;
    }

    public Map<String, String> transe(Transaction transaction) {
        transType = TransType.PROHIBITED;
        info = new ArrayList<>();

        checkNumber(transaction.getNumber());
        checkIp(transaction.getIp());
        checkAmount(transaction.getAmount());
        return Map.of("result", transType.name(),
                "info", getErrorInfo(info));
    }

    public void checkNumber(String number) {
        if (cardService.existInBlacklist(number)) {
            info.add("card-number");
        }
    }

    public void checkIp(String ip) {
        if (ipService.existInBlacklist(ip)) {
            info.add("ip");
        }
    }

    public void checkAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong amount!");
        }

        int MAX_AMOUNT_FOR_ALLOWED = 200;
        int MAX_AMOUNT_FOR_MANUAL_PROCESSING = 1500;

        if (amount > MAX_AMOUNT_FOR_MANUAL_PROCESSING) {
            info.add("amount");
        } else if (amount > MAX_AMOUNT_FOR_ALLOWED && info.size() < 1) {
            transType = TransType.MANUAL_PROCESSING;
            info.add("amount");
        } else if (info.size() < 1) {
            transType = TransType.ALLOWED;
            info.add("none");
        }
    }

    private String getErrorInfo(List<String> errors) {
        StringBuilder info = new StringBuilder();
        errors.sort((String::compareToIgnoreCase));
        info.append(errors.get(0));
        for (int i = 1; i < errors.size(); i++) {
            info.append(", ").append(errors.get(i));
        }
        return info.toString();
    }
}
