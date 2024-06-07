package hello.financepartner.service;

import hello.financepartner.domain.*;
import hello.financepartner.domain.status.Category;
import hello.financepartner.domain.status.IsIncom;
import hello.financepartner.dto.HistoryDto;
import hello.financepartner.dto.NoticeDto;
import hello.financepartner.repository.FinancialLedgerRepository;
import hello.financepartner.repository.HistoryRepository;
import hello.financepartner.repository.JoinListRepository;
import hello.financepartner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final JoinListRepository joinListRepository;
    private final FinancialLedgerRepository flRepository;

    public void createHistory(Long flId, int year, int month, int day, Long amount, String content, Category isCategory, IsIncom isIncom) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        Users user = userRepository.findById(userId).get();
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        FinancialLedger financialLedger = flRepository.findById(flId).get();

        Boolean isMember = false;
        for (JoinList joinList : joinLists) {
            if (joinList.getFinancialLedger().getId().equals(flId))
                isMember = true;
        }

        if (isMember == true) {
            History history = new History();
            history.setAmount(amount);
            history.setContent(content);
            history.setCategory(isCategory);
            history.setUser(user);
            history.setIsIncom(isIncom);
            history.setFinancialLedger(financialLedger);
            history.setDate(LocalDate.of(year, month, day));
            historyRepository.save(history);

        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 지출내역을 기록할 수 있습니다.");

    }


    public void updateHistory(Long historyId, int year, int month, int day, Long amount, String content, Category isCategory, IsIncom isIncome) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        Boolean isMember = false;
        Long flId = historyRepository.findById(historyId).get().getFinancialLedger().getId();
        History history = historyRepository.findById(historyId).get();
        for (JoinList joinList : joinLists) {
            if (joinList.getFinancialLedger().getId().equals(flId))
                isMember = true;
        }

        if (isMember == true) {
            history.setDate(LocalDate.of(year, month, day));
            history.setAmount(amount);
            history.setContent(content);
            history.setCategory(isCategory);
            history.setIsIncom(isIncome);
        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 지출내역을 기록할 수 있습니다. 있습니다.");

    }

    public void deleteHistory(Long historyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        Long flId = historyRepository.findById(historyId).get().getFinancialLedger().getId();

        Boolean isMember = false;
        for (JoinList joinList : joinLists) {
            if (joinList.getFinancialLedger().getId().equals(flId))
                isMember = true;
        }

        if (isMember == true) {
            historyRepository.deleteById(historyId);
        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 지출내역을 삭제할 수 있습니다. 있습니다.");

    }

    public List<HistoryDto.Info> getHistory(Long flId) {
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
            List<History> historys = historyRepository.findByFinancialLedger_Id(flId);
            List<HistoryDto.Info> historyInfoList = new ArrayList<>();

            for (History history : historys) {
                HistoryDto.Info historyInfo = HistoryDto.Info.builder()
                        .id(history.getId())
                        .date(history.getDate())
                        .amount(history.getAmount())
                        .content(history.getContent())
                        .category(history.getCategory())
                        .isIncome(history.getIsIncom())
                        .build();
                historyInfoList.add(historyInfo);
            }

            return historyInfoList;


        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 지출내역을 기록할 수 있습니다. 있습니다.");

    }

    public List<HistoryDto.Info> getMonthHistory(Long flId, int year, int month) {
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
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            List<History> historys = historyRepository.findByFinancialLedger_IdAndDateBetween(flId, startOfMonth, endOfMonth);
            List<HistoryDto.Info> historyInfoList = new ArrayList<>();

            for (History history : historys) {
                HistoryDto.Info historyInfo = HistoryDto.Info.builder()
                        .id(history.getId())
                        .date(history.getDate())
                        .amount(history.getAmount())
                        .content(history.getContent())
                        .category(history.getCategory())
                        .isIncome(history.getIsIncom())
                        .build();
                historyInfoList.add(historyInfo);
            }
            return historyInfoList;
        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 지출내역을 기록할 수 있습니다. 있습니다.");

    }


    public List<HistoryDto.Info> getMonthAndDayHistory(Long flId, int year, int month, int day) {
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
            LocalDate specificDate = LocalDate.of(year, month, day);

            List<History> historys = historyRepository.findByFinancialLedger_IdAndDate(flId, specificDate);
            List<HistoryDto.Info> historyInfoList = new ArrayList<>();

            for (History history : historys) {
                HistoryDto.Info historyInfo = HistoryDto.Info.builder()
                        .id(history.getId())
                        .date(history.getDate())
                        .amount(history.getAmount())
                        .content(history.getContent())
                        .category(history.getCategory())
                        .isIncome(history.getIsIncom())
                        .build();
                historyInfoList.add(historyInfo);
            }

            return historyInfoList;

        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 지출내역을 기록할 수 있습니다. 있습니다.");

    }

    public List<HistoryDto.Info> getUserHistory(Long flId, Long userIds) {
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
            List<History> historys = historyRepository.findByFinancialLedger_IdAndUser_Id(flId, userIds);
            List<HistoryDto.Info> historyInfoList = new ArrayList<>();

            for (History history : historys) {
                HistoryDto.Info historyInfo = HistoryDto.Info.builder()
                        .id(history.getId())
                        .date(history.getDate())
                        .amount(history.getAmount())
                        .content(history.getContent())
                        .category(history.getCategory())
                        .isIncome(history.getIsIncom())
                        .build();
                historyInfoList.add(historyInfo);
            }
            return historyInfoList;
        } else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 지출내역을 기록할 수 있습니다. 있습니다.");
    }
}
