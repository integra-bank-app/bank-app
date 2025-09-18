package clf.integra.backend.service;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.exceptions.BalanceUpdateFailedException;
import clf.integra.backend.exceptions.InsufficientFundsException;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.model.Account;
import clf.integra.backend.model.Branch;
import clf.integra.backend.model.TransactionType;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.BranchRepository;
import clf.integra.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final TransactionService transactionService;

    @Transactional
    public UUID addUserWithName(String firstName, String middleName, String lastName, UUID branchId) {
        Branch branch = branchRepository.findById(branchId).get();
        if(branch == null) {
            throw new NotFoundException("Branch not found");
        }
        User newUser = User.builder()
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .branch(branch)
                .build();

        Account account = Account.builder()
                .balance(0.0)
                .user(newUser)
                .build();

        newUser.getAccounts().add(account);
        userRepository.save(newUser);
        return newUser.getId();
    }

    @Transactional
    public double addBalance(UUID uuid, double amount) {
        if (!userRepository.existsById(uuid)) {
            throw new NotFoundException("User not found");
        }

        //Simulate a random chance of 20% (in the issue is 10%, but I had bad luck) for the operation to fail
        double randomValue = Math.random();
        if (randomValue >= 0.8) {
            throw new BalanceUpdateFailedException("An unknown error has occurred intentionally");
        }

        User user = userRepository.findById(uuid).orElseThrow(() -> new NotFoundException("User not found"));
        user.getAccounts().getFirst().setBalance(user.getAccounts().getFirst().getBalance() + amount);
        userRepository.save(user);

        transactionService.createTransaction(user, amount, TransactionType.TOP_UP, "Top-up of " + amount);

        return user.getAccounts().getFirst().getBalance();
    }

    public Double getUserTotalBalanceById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID can not be null!");
        }

        List<Account> accounts = userRepository.getReferenceById(id).getAccounts();
        Double totalBalance = accounts.stream()
                .mapToDouble(Account::getBalance)
                .sum();

        if (totalBalance == null) {
            throw new NotFoundException("User not found or has no accounts");
        }

        return totalBalance;
    }

    public List<UserDTO> getAllUsersByBranch(UUID branchId) {
        return userRepository.findByBranchId(branchId).stream()
                .map(user -> new UserDTO(user.getFirstName(), user.getMiddleName(), user.getLastName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public double collectTaxesAndFeesFromBranch(UUID branchId) {
        if (branchId == null) {
            throw new IllegalArgumentException("Branch ID can not be null!");
        }

        List<User> usersBranch = userRepository.findByBranchId(branchId);

        if (usersBranch.isEmpty()) {
            throw new IllegalArgumentException("The branch does not have any customer!");
        }

        double revenue = 0;
        for (User user : usersBranch) {
            double userBalance = user.getAccounts().getFirst().getBalance();
            double fee = calculateFee(userBalance);

            if (fee > 0) {
                user.getAccounts().getFirst().setBalance(userBalance - fee);
                revenue += fee;

                transactionService.createTransaction(user, -fee, TransactionType.FEE, "Fee of " + fee + " collected");
            }
        }
        userRepository.saveAll(usersBranch);
        return revenue;
    }

    public double calculateFee(Double balance) {
        return balance < 100 ? balance * 0.1 : 10;
    }

    public double transferMoney(UUID fromUserId, UUID toUserId, double amount) throws NotFoundException, InsufficientFundsException {
        User fromUser = userRepository.getReferenceById(fromUserId);
        User toUser = userRepository.getReferenceById(toUserId);

        if (fromUser == null || toUser == null) {
            throw new NotFoundException("One or both users not found");
        }

        if (fromUser.getAccounts().getFirst().getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        fromUser.getAccounts().getFirst().setBalance(fromUser.getAccounts().getFirst().getBalance() - amount);
        toUser.getAccounts().getFirst().setBalance(toUser.getAccounts().getFirst().getBalance() + amount);

        userRepository.save(fromUser);
        userRepository.save(toUser);

        transactionService.createTransaction(fromUser, -amount, TransactionType.TRANSFER_OUT, "Transfer of " + amount + " to user " + toUserId);
        transactionService.createTransaction(toUser, amount, TransactionType.TRANSFER_IN, "Transfer of " + amount + " from user " + fromUserId);

        return fromUser.getAccounts().getFirst().getBalance();
    }

    public List<UUID> getUserAccounts(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        List<Account> accounts = userRepository.getReferenceById(id).getAccounts();
        return accounts.stream().map(Account::getId).collect(Collectors.toList());
    }

    public Double getUserAccountBalance(UUID id, UUID accountId) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
        User user = userRepository.getReferenceById(id);
        return user.getAccounts().stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"))
                .getBalance();
    }
}