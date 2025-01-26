package comprestigebankv4.repositories;

import comprestigebankv4.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByAlternatePhoneNumber(String phoneNumber);
    Optional<Account> findByUsername(String username);
    Optional<Account> findByCardNumber(String cardNumber);
}