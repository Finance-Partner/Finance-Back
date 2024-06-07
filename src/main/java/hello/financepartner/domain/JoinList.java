package hello.financepartner.domain;

import hello.financepartner.domain.status.Joined;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class JoinList {
    @Id
    @GeneratedValue
    @Column(name = "join_list_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fl_id")
    private FinancialLedger financialLedger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    private Joined joined;

    private LocalDate invitedDate;
}
