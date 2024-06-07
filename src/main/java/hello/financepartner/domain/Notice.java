package hello.financepartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Notice {
    @Id
    @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    private String content;
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fl_id")
    private FinancialLedger financialLedger;
}
