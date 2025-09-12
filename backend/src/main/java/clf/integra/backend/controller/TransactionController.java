package clf.integra.backend.controller;

import clf.integra.backend.dto.TransactionDTO;
import clf.integra.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/topup")
    public ResponseEntity<TransactionDTO> createTopUpTransaction(
            @RequestParam UUID userId,
            @RequestParam double amount,
            @RequestParam(required = false) String description) {

        TransactionDTO transaction = transactionService.saveTopUpTransaction(userId, amount, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> createTransferTransaction(
            @RequestParam UUID senderId,
            @RequestParam UUID receiverId,
            @RequestParam double amount,
            @RequestParam(required = false) String description) {

        transactionService.saveTransferTransactions(senderId, receiverId, amount, description);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transfer completed successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDTO>> getUserTransactionHistory(@PathVariable UUID userId) {
        List<TransactionDTO> transactions = transactionService.getUserTransactionHistory(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/{userId}/type/{transactionType}")
    public ResponseEntity<List<TransactionDTO>> getUserTransactionsByType(
            @PathVariable UUID userId,
            @PathVariable String transactionType) {

        List<TransactionDTO> transactions = transactionService.getUserTransactionsByType(userId, transactionType);
        return ResponseEntity.ok(transactions);
    }
}