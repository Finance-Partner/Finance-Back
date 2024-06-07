package hello.financepartner.repository;

import hello.financepartner.domain.Fixed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixedRepository extends JpaRepository<Fixed, Long> {
    public List<Fixed> findByFinancialLedger_Id(Long flId);
}
