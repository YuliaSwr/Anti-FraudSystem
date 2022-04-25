package antifraud.controller;

import antifraud.entity.IP;
import antifraud.entity.Transaction;
import antifraud.service.IPService;
import antifraud.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/api/antifraud/transaction")
    public Map<String, String> analyzeTrans(@RequestBody Transaction transaction) {
        return transactionService.transe(transaction);
    }

}
