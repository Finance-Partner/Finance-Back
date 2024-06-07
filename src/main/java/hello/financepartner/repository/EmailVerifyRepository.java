package hello.financepartner.repository;

import hello.financepartner.domain.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify,Long> {
    EmailVerify findByEmail(String email);
}
