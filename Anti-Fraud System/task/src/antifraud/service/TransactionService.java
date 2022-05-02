package antifraud.service;

import antifraud.entity.*;
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

    private Double limit_for_allowed = 200D;
    private Double limit_for_manual = 1500D;

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

        if (prohibited) {
            transaction.setResult(TransType.PROHIBITED);
            return new TransactionResult(TransType.PROHIBITED, info);
        } else if (allowed) {
            transaction.setResult(TransType.ALLOWED);
            return new TransactionResult(TransType.ALLOWED, info);
        } else {
            transaction.setResult(TransType.MANUAL_PROCESSING);
            return new TransactionResult(TransType.MANUAL_PROCESSING, info);
        }
    }

    private void checkAmount(Transaction transaction) {
        long amount = transaction.getAmount();

        if (amount <= limit_for_allowed && !prohibited && !manual) {
            allowed = true;
            reasons.add("none");
        }


        if (amount > limit_for_allowed && amount <= limit_for_manual && !prohibited) {
            reasons.add("amount");
            manual = true;
        }

        if (amount > limit_for_manual) {
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

    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    public Transaction addFeedback(Long transId, String feedback) {
        Transaction transaction = transactionRepository.findById(transId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (transaction.getFeedback() != null && transaction.getFeedback().name().equalsIgnoreCase(feedback)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        checkResultAndFeedback(transaction, feedback);
        setFeedback(transaction, feedback);

        return transactionRepository.save(transaction);
    }

    private void checkResultAndFeedback(Transaction transaction, String feedback) {
        String result = transaction.getResult().name();
        //String feedback = transaction.getFeedback().name();
        Long amount = transaction.getAmount();

        if (result.equalsIgnoreCase(feedback)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (result.equals("ALLOWED") && feedback.equals("MANUAL_PROCESSING")) {
            decreaseLimitForAllowed(amount);
        }

        if (result.equals("ALLOWED") && feedback.equals("PROHIBITED")) {
            decreaseLimitForAllowed(amount);
            decreaseLimitForManual(amount);
        }

        if (result.equals("MANUAL_PROCESSING") && feedback.equals("ALLOWED")) {
            increaseLimitForAllowed(amount);
        }

        if (result.equals("MANUAL_PROCESSING") && feedback.equals("PROHIBITED")) {
            decreaseLimitForManual(amount);
        }

        if (result.equals("PROHIBITED") && feedback.equals("ALLOWED")) {
            increaseLimitForAllowed(amount);
            increaseLimitForManual(amount);
        }

        if (result.equals("PROHIBITED") && feedback.equals("MANUAL_PROCESSING")) {
            increaseLimitForManual(amount);
        }
    }

    private void setFeedback(Transaction transaction, String feedback) {
        switch (feedback) {
            case "ALLOWED":
                transaction.setFeedback(FeedbackType.ALLOWED);
                break;
            case "MANUAL_PROCESSING":
                transaction.setFeedback(FeedbackType.MANUAL_PROCESSING);
                break;
            case "PROHIBITED":
                transaction.setFeedback(FeedbackType.PROHIBITED);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public List<Transaction> getAllTransactionByNumber(String number) {
        cardService.checkNumber(number);
        List<Transaction> transactions = transactionRepository.findAllByNumber(number);

        if (transactions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return transactions;
    }

    public void increaseLimitForAllowed(Long valueFromTransaction) {
        limit_for_allowed = Math.ceil(0.8 * limit_for_allowed + 0.2 * valueFromTransaction);
    }

    public void decreaseLimitForAllowed(Long valueFromTransaction) {
        limit_for_allowed = Math.ceil(0.8 * limit_for_allowed - 0.2 * valueFromTransaction);
    }

    public void increaseLimitForManual(Long valueFromTransaction) {
        limit_for_manual = Math.ceil(0.8 * limit_for_allowed + 0.2 * valueFromTransaction);
    }

    public void decreaseLimitForManual(Long valueFromTransaction) {
        limit_for_manual = Math.ceil(0.8 * limit_for_allowed - 0.2 * valueFromTransaction);
    }

}
