package antifraud.controller;

import antifraud.entity.Transaction;
import antifraud.entity.TransactionResult;
import antifraud.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/api/antifraud/transaction")
    public TransactionResult analyzeTrans(@RequestBody Transaction transaction) {
        return transactionService.processTransaction(transaction);
    }

    @PutMapping("/api/antifraud/transaction")
    public Transaction analyzeTrans(@RequestBody Map<String, String> request) {
        Long transId = Long.parseLong(request.get("transactionId"));
        String feedback = request.get("feedback");
        return transactionService.addFeedback(transId, feedback);
    }

    @GetMapping("/api/antifraud/history")
    public List<Transaction> getHistory(){
        return transactionService.getAllTransaction();
    }

    @GetMapping("/api/antifraud/history/{number}")
    public List<Transaction> getHistory(@PathVariable String number){
        return transactionService.getAllTransactionByNumber(number);
    }
}
