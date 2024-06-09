package hello.financepartner.repository;

import hello.financepartner.domain.History;
import hello.financepartner.domain.status.IsIncom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History,Long> {
    public List<History> findByFinancialLedger_Id(Long flId);
    public List<History> findByFinancialLedger_IdAndDateBetween(Long flId, LocalDate startDate, LocalDate endDate);
    public List<History> findByFinancialLedger_IdAndDate(Long financialLedgerId, LocalDate date);
    public List<History> findByFinancialLedger_IdAndUser_Id(Long financialLedgerId, Long userId);

    public List<History> findByFinancialLedger_IdAndIsIncomAndDateBetween(Long id, IsIncom isIncom, LocalDate startOfMonth, LocalDate endOfMonth);
}
