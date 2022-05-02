package antifraud.service;

import antifraud.entity.TransType;
import antifraud.entity.Transaction;
import antifraud.entity.TransactionResult;
import antifraud.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CardService cardService;

    @Autowired
    private IPService ipService;

    private Set<String> reasons;

    private boolean allowed;
    private boolean manual;
    private boolean prohibited;

    public TransactionResult processTransaction(Transaction transaction) {
        transactionRepository.save(transaction);

        if (transaction.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        reasons = new TreeSet<>();

        allowed = false;
        manual = false;
        prohibited = false;

        checkCardBlacklist(transaction.getNumber());
        checkIpBlacklist(transaction.getIp());

        checkTransaction(transaction);
        checkAmount(transaction);

        String info = String.join(", ", reasons);

        if (prohibited) return new TransactionResult(TransType.PROHIBITED, info);
        if (allowed) return new TransactionResult(TransType.ALLOWED, info);
        else return new TransactionResult(TransType.MANUAL_PROCESSING, info);
    }

    private void checkAmount(Transaction transaction) {
        long amount = transaction.getAmount();

        if (amount <= 200 && !prohibited && !manual) {
            allowed = true;
            reasons.add("none");
        }


        if (amount > 200 && amount <= 1500 && !prohibited) {
            reasons.add("amount");
            manual = true;
        }

        if (amount > 1500) {
            reasons.add("amount");
            prohibited = true;
        }
    }

    private void checkCardBlacklist(String number) {
        if (cardService.exists(number)) {
            reasons.add("card-number");
            prohibited = true;
        }
    }

    public void checkIpBlacklist(String ip) {
        if (ipService.exists(ip)) {
            reasons.add("ip");
            prohibited = true;
        }
    }

    public void checkTransaction(Transaction transaction) {
        LocalDateTime end = transaction.getDate();
        LocalDateTime start = end.minusHours(1);

        List<Transaction> transactionsInLastHour = transactionRepository.findAllByDateBetweenAndNumber(start, end, transaction.getNumber());

        long regionsCount = transactionsInLastHour.stream().map(Transaction::getRegion).distinct().count();
        long ipCount = transactionsInLastHour.stream().map(Transaction::getIp).distinct().count();

        if (regionsCount > 2) {
            reasons.add("region-correlation");
            if (regionsCount == 3) manual = true;
            else prohibited = true;
        }

        if (ipCount > 2) {
            reasons.add("ip-correlation");
            if (ipCount == 3) manual = true;
            else prohibited = true;
        }
    }
}
