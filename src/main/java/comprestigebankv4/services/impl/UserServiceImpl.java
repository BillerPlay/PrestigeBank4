package comprestigebankv4.services.impl;


import comprestigebankv4.dto.*;
import comprestigebankv4.model.Transaction;
import comprestigebankv4.repositories.TransactionRepository;
import comprestigebankv4.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import comprestigebankv4.model.Account;
import comprestigebankv4.repositories.AccountRepository;
import org.springframework.security.core.context.SecurityContextHolder;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    EmailService emailService;


    public Account findAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }


    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        // Check existing account by username
        if (accountRepository.existsByUsername(userRequest.getUsername())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        if (accountRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.PHONE_NUMBER_EXISTS_CODE)
                    .responseMessage(AccountUtils.PHONE_NUMBER_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        // Checking an existing account by phone number
        if (userRequest.getAlternatePhoneNumber() != null && !userRequest.getAlternatePhoneNumber().isEmpty()) {
            if (accountRepository.existsByPhoneNumber(userRequest.getAlternatePhoneNumber()) ||
                    accountRepository.existsByAlternatePhoneNumber(userRequest.getAlternatePhoneNumber())) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.PHONE_NUMBER_EXISTS_CODE)
                        .responseMessage(AccountUtils.PHONE_NUMBER_EXISTS_MESSAGE)
                        .accountInfo(null)
                        .build();
            }
        }

        String accountNumber = AccountUtils.generateAccountNumber();
        Account newUser = Account.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(accountNumber)
                .accountIBAN(AccountUtils.generateAccountIBAN(accountNumber))
                .cardNumber(AccountUtils.generateCardNumber())
                .accountBalance(BigDecimal.ZERO)
                .cardExpiry(AccountUtils.generateExpirationDate())
                .cvvCode(AccountUtils.generateCVVCode())
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternatePhoneNumber(userRequest.getAlternatePhoneNumber())
                .status("ACTIVE")
                .role("USER")
                .build();

        // Save the account in the database
        Account savedUser = accountRepository.save(newUser);

        // Sending mail
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getUsername())
                .subject("SİZİN HESABINIZ YARADILDI!")
                .messageBody("Təbriklər! Sizin hesabınız yaradıldı.\nHesabınızın məlumatları: \n" +
                        "Hesabınızın adı: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nHesabınızın nömrəsi: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmail(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("Email ya da parol düzgün yazılmayıb");
        }
        return new Account(
                account.getUsername(),
                account.getPassword(),
                account.getBalance(),
                account.getTransactions(),
                authorities()
        );
    }

    public Collection<? extends GrantedAuthority> authorities() {
        return Arrays.asList(new SimpleGrantedAuthority("Account"));
    }


    public void deposit(Account account, BigDecimal amount) {
        account.setAccountBalance(account.getAccountBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                amount,
                "Deposit",
                LocalDateTime.now(),
                account
        );
        transactionRepository.save(transaction);
    }

    public void withdraw(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        account.setAccountBalance(account.getAccountBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                amount,
                "Withdraw",
                LocalDateTime.now(),
                account
        );
        transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsHistory(Account account) {
        return transactionRepository.findByAccountUsername(account.getUsername());
    }


    public void transferAmount(Account fromAccount, String toUsername, BigDecimal amount) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        Account toAccount = accountRepository.findByCardNumber(toUsername)
                .orElseThrow(() -> new RuntimeException("Recipient account not found"));

        fromAccount.setAccountBalance(fromAccount.getAccountBalance().subtract(amount));
        accountRepository.save(fromAccount);
        toAccount.setAccountBalance(toAccount.getAccountBalance().add(amount));
        accountRepository.save(toAccount);
        Transaction debitTransaction = new Transaction(
                amount,
                "Transfer Out to " + AccountUtils.maskCardNumber(toAccount.getCardNumber()),
                LocalDateTime.now(),
                fromAccount
        );
        transactionRepository.save(debitTransaction);
        Transaction creditTransaction = new Transaction(
                amount,
                "Transfer In to " + AccountUtils.maskCardNumber(toAccount.getCardNumber()),
                LocalDateTime.now(),
                toAccount
        );
        transactionRepository.save(creditTransaction);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("PUL KÖÇÜRMƏSİ")
                .recipient(fromAccount.getUsername())
                .messageBody("Hesabınızdan " + amount.toString() + " AZN cıxıldı." + "\nCari balansınız " + fromAccount.getAccountBalance() + "AZN-dir"
                        + "\n\nƏgər siz deyildinizsə, lütfən, parolunuzu dəyişdirin və ya banka zəng edin.")
                .build();
        emailService.sendEmail(debitAlert);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(toAccount.getUsername())  // Corrected to recipient
                .messageBody("Siz " + AccountUtils.maskCardNumber(fromAccount.getCardNumber()) + "-dən " + amount.toString() + " AZN aldınız. Hazırda balansınız " + toAccount.getAccountBalance().toString() + " AZN-dir.")
                .build();
        emailService.sendEmail(creditAlert);
    }

    public void changeDetails(AccountUpdateRequest accountUpdateRequest) throws Exception {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // We get the user account from the database
        Optional<Account> accountOptional = accountRepository.findByUsername(currentUsername);
        if (accountOptional.isEmpty()) {
            throw new Exception("Account not found");
        }

        Account account = accountOptional.get();

        // We update data only if it is not empty.
        if (accountUpdateRequest.getAddress() != null && !accountUpdateRequest.getAddress().trim().isEmpty()) {
            account.setAddress(accountUpdateRequest.getAddress());
        }

        if (accountUpdateRequest.getPhoneNumber() != null && !accountUpdateRequest.getPhoneNumber().trim().isEmpty()) {
            account.setPhoneNumber(accountUpdateRequest.getPhoneNumber());
        }

        if (accountUpdateRequest.getPassword() != null && !accountUpdateRequest.getPassword().trim().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountUpdateRequest.getPassword()));
        }

        // Сохраняем изменения
        accountRepository.save(account);
    }
}

