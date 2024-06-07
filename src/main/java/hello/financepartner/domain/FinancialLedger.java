package hello.financepartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
public class FinancialLedger {
    @Id
    @GeneratedValue
    @Column(name = "fl_id")
    private Long id;

    private String title;
    private Long budget;


    @OneToMany(mappedBy = "financialLedger", cascade = CascadeType.ALL)
    private List<JoinList> joinLists = new ArrayList<>();

    @OneToMany(mappedBy = "financialLedger", cascade = CascadeType.ALL)
    private List<History> historys = new ArrayList<>();

    @OneToMany(mappedBy = "financialLedger", cascade = CascadeType.ALL)
    private List<Notice> notices = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;
}
