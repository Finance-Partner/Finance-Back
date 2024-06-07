package hello.financepartner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class EmailVerify {
    @Id
    @GeneratedValue
    @Column(name = "email_verify_id")
    private Long id;

    private String email;
    private String Certification;
    private int isVerified; // 0 인증 안됨, 1 인증됨
}
