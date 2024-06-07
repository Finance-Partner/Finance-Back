package hello.financepartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Fixed {

    @Id
    @GeneratedValue
    @Column(name = "fixed_id")
    private Long id;
    private String content; // 고정 수입/지출 내용
    private Long amount; // 고정 수입/지출 금액
    private int date; // 매달 지출/수입 날짜
    private boolean isIncome; // 수입인지 지출인지


    @ManyToOne(fetch = FetchType.LAZY)
    private FinancialLedger financialLedger;


}
