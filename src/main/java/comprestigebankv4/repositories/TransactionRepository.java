package comprestigebankv4.repositories;

import comprestigebankv4.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountUsername(String accountUsername);
}
