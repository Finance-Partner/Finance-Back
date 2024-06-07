package hello.financepartner.repository;

import hello.financepartner.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Long> {
    Users findByEmail(String email);
    Users findByName(String name);
}
