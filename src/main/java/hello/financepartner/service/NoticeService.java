package hello.financepartner.service;

import hello.financepartner.domain.FinancialLedger;
import hello.financepartner.domain.JoinList;
import hello.financepartner.domain.Notice;
import hello.financepartner.domain.Users;
import hello.financepartner.dto.NoticeDto;
import hello.financepartner.repository.FinancialLedgerRepository;
import hello.financepartner.repository.JoinListRepository;
import hello.financepartner.repository.NoticeRepository;
import hello.financepartner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    private final JoinListRepository joinListRepository;
    private final FinancialLedgerRepository flRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    public void createNotice(Long flId, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        Users user = userRepository.findById(userId).get();
        FinancialLedger financialLedger = flRepository.findById(flId).get();

        Boolean isMember = false;

        for (JoinList joinList : joinLists) {
            if (joinList.getFinancialLedger().getId().equals(flId))
                isMember = true;
        }

        if (isMember == true) {
            Notice notice = new Notice();
            notice.setDate(LocalDateTime.now());
            notice.setUser(user);
            notice.setContent(content);
            notice.setFinancialLedger(financialLedger);
            noticeRepository.save(notice);
        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 채팅을 작성할 수 있습니다.");
    }

    public List<NoticeDto.NoticeInfo> readNotice(Long flId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        Boolean isMember = false;
        for (JoinList joinList : joinLists) {
            if (joinList.getFinancialLedger().getId().equals(flId))
                isMember = true;
        }

        if (isMember == true) {
            List<Notice> notices = noticeRepository.findByFinancialLedger_Id(flId);
            return notices.stream().map(notice -> NoticeDto.NoticeInfo.builder()
                            .content(notice.getContent())
                            .name(notice.getUser().getName()) // Lazy 로딩으로 인해 N+1 문제가 발생할 수 있습니다.
                            .userId(notice.getUser().getId())
                            .time(notice.getDate())
                            .build())
                    .collect(Collectors.toList());
        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 채팅을 조회할 수 있습니다.");
    }

    public void updateNotice(Long commentId, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);

        Notice notice = noticeRepository.findById(commentId).get();

        Long commentedId = notice.getUser().getId();

        if(!commentedId.equals(userId))
            throw new IllegalArgumentException("본인만 채팅을 수정할 수 있습니다.");

        notice.setContent("(수정됨) "+content);

    }

    public void deleteNotice(Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);

        Notice notice = noticeRepository.findById(commentId).get();

        Long commentedId = notice.getUser().getId();

        if(!commentedId.equals(userId))
            throw new IllegalArgumentException("본인만 채팅을 삭제할 수 있습니다.");

        noticeRepository.deleteById(commentId);
    }
}
