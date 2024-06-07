package hello.financepartner.repository;

import hello.financepartner.domain.JoinList;
import hello.financepartner.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinListRepository extends JpaRepository<JoinList,Long> {
    public JoinList findByUser_IdAndFinancialLedger_Id(Long userId, Long flId);
    public List<JoinList> findByFinancialLedger_Id(Long flId);
    public List<JoinList> findByUser_Id(Long userId);
}
