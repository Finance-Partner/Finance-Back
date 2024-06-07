package hello.financepartner.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Users {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String password;
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String photo;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<JoinList> joinlists = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<History> histories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FinancialLedger> financialLedgers = new ArrayList<>();


}
