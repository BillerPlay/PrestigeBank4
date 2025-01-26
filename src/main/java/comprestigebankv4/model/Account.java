package comprestigebankv4.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "otpVerified")
    private boolean otpVerified;
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    private String accountNumber;
    private String accountIBAN;
    private BigDecimal accountBalance;
    private String cardNumber;
    private String cardExpiry;
    private String cvvCode;
    private String username;
    private String phoneNumber;
    private String alternatePhoneNumber;
    private String status;
    private String role;
    private String password;
    @CreationTimestamp
    public LocalDateTime createdAt;
    @UpdateTimestamp
    public LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    public Account() {
    }

    public Account(String username, String password, BigDecimal accountBalance, List<Transaction> transactions, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.accountBalance = accountBalance;
        this.transactions = transactions;
        this.authorities = authorities;
    }
    public Account(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;

        this.authorities = authorities;
    }

    public BigDecimal getBalance() {
        return accountBalance;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
