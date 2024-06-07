package hello.financepartner.repository;

import hello.financepartner.domain.FinancialLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialLedgerRepository extends JpaRepository<FinancialLedger,Long> {
}
