package hello.financepartner.domain;

import hello.financepartner.domain.status.Category;
import hello.financepartner.domain.status.IsIncom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class History {
    @Id
    @GeneratedValue
    @Column(name = "history_id")
    private Long id;


    private Long amount;
    private LocalDate date;
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private IsIncom isIncom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fl_id")
    private FinancialLedger financialLedger;
}
