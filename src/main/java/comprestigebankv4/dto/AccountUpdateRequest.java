package comprestigebankv4.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateRequest {
    private String address;
    private String phoneNumber;
    private String password;
}
