package hello.financepartner.repository;

import hello.financepartner.domain.Fixed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedRepository extends JpaRepository<Fixed, Long> {
}
