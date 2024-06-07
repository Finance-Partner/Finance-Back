package hello.financepartner.service;

import hello.financepartner.domain.FinancialLedger;
import hello.financepartner.domain.Fixed;
import hello.financepartner.domain.JoinList;
import hello.financepartner.domain.Users;
import hello.financepartner.domain.status.Joined;
import hello.financepartner.dto.FLDto;
import hello.financepartner.repository.FinancialLedgerRepository;
import hello.financepartner.repository.FixedRepository;
import hello.financepartner.repository.JoinListRepository;
import hello.financepartner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FLService {
    private final FinancialLedgerRepository flRepository;
    private final UserRepository userRepository;
    private final JoinListRepository joinListRepository;
    private final FixedRepository fixedRepository;

    public void createFL(FLDto.FLInfo flInfo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        Users user = userRepository.findById(userId).get();
        if (flInfo.getTitle() == null) {
            throw new IllegalArgumentException("가계부의 제목은 반드시 입력되어야 합니다.");
        }
        if (flInfo.getBudget() < 0) {
            throw new IllegalArgumentException("가계부의 예산은 음수가 될 수 없습니다.");
        }

        FinancialLedger fl = new FinancialLedger();
        fl.setUser(user);
        fl.setBudget(flInfo.getBudget());
        fl.setTitle(flInfo.getTitle());
        flRepository.save(fl);

        JoinList joinList = new JoinList();
        joinList.setUser(user);
        joinList.setFinancialLedger(fl);
        joinList.setInvitedDate(LocalDate.now());
        joinList.setJoined(Joined.JOINED);
        joinListRepository.save(joinList);
    }


    public void updateFL(FLDto.FLInfo2 flInfo) {
        //후에 멤버들이 다 수정할 수 있도록 바꿔야함
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);

        Long flId = flInfo.getFlId();
        if (flInfo.getBudget() < 0) {
            throw new IllegalArgumentException("가계부의 예산은 음수가 될 수 없습니다.");
        }

        JoinList finded = joinListRepository.findByUser_IdAndFinancialLedger_Id(userId, flId);

        if (finded != null && finded.getJoined() == Joined.JOINED) {
            if (flInfo.getBudget() != null)
                flRepository.findById(flId).get().setBudget(flInfo.getBudget());

            if (flInfo.getTitle() != null)
                flRepository.findById(flId).get().setTitle(flInfo.getTitle());
        } else
            throw new IllegalArgumentException("가계부에 속해있어야만 가계부를 수정할 수 있습니다.");
    }


    public void deleteFL(Long flId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        Long findedId = flRepository.findById(flId).get().getUser().getId();


        if (userId == findedId)
            flRepository.deleteById(flId);
        else
            throw new IllegalArgumentException("가계부의 장만이 가계부를 삭제할 수 있습니다.");
    }

    public List<FLDto.FLUsers> findFLUsers(Long flId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        JoinList finded = joinListRepository.findByUser_IdAndFinancialLedger_Id(userId, flId);

        if (finded != null && finded.getJoined() == Joined.JOINED) {
            List<JoinList> joinLists = joinListRepository.findByFinancialLedger_Id(flId);
            List<FLDto.FLUsers> flUsers = joinLists.stream()
                    .map(joinList -> FLDto.FLUsers.builder()
                            .userId(joinList.getUser().getId())
                            .name(joinList.getUser().getName())
                            .build())
                    .collect(Collectors.toList());
            return flUsers;
        } else
            throw new IllegalArgumentException("가계부에 속해있어야만 가계부를 조회할 수 있습니다.");
    }

    public void inviteUser(Long flId, String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        JoinList finded = joinListRepository.findByUser_IdAndFinancialLedger_Id(userId, flId);

        Users invitingUser = userRepository.findByEmail(email);

        if (invitingUser == null)
            throw new IllegalArgumentException("가입되어있는 사람만 초대할 수 있습니다.");

        JoinList finded2 = joinListRepository.findByUser_IdAndFinancialLedger_Id(invitingUser.getId(), flId);
        if (finded2 != null)
            throw new IllegalArgumentException("이미 초대되어있는 사람은 초대할 수 있습니다.");

        if (finded != null && finded.getJoined() == Joined.JOINED) {
            JoinList joinList = new JoinList();
            joinList.setInvitedDate(LocalDate.now());
            joinList.setFinancialLedger(flRepository.findById(flId).get());
            joinList.setUser(invitingUser);
            joinList.setJoined(Joined.WAIT);
            joinListRepository.save(joinList);
        } else
            throw new IllegalArgumentException("가계부에 속해있어야만 다른 사람을 초대할 수 있습니다.");
    }

    public void acceptInvite(Long flId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);

        // 가계부에 대한 초대 상태를 확인하기 위한 flag
        boolean isAlreadyJoined = false;

        for (JoinList joinList : joinLists) {
            if (joinList.getFinancialLedger().getId().equals(flId)) {
                // 이미 JOINED 상태인 경우 flag를 설정하고 반복을 종료합니다.
                if (joinList.getJoined().equals(Joined.JOINED)) {
                    isAlreadyJoined = true;
                    break;
                }

                // WAIT 상태인 경우, JOINED로 상태를 변경합니다.
                if (joinList.getJoined().equals(Joined.WAIT)) {
                    joinList.setJoined(Joined.JOINED);
                    joinListRepository.save(joinList); // 변경사항을 저장합니다.
                    return; // 메소드를 종료합니다.
                }
            }
        }

        // flag가 true라면 이미 JOINED 상태인 것이므로 예외를 던집니다.
        if (isAlreadyJoined) {
            throw new IllegalArgumentException("이미 가계부에 합류되어있습니다.");
        }
    }

    public void rejectInvite(Long flId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        // 가계부 초대 상태를 확인하기 위한 flag
        boolean isAlreadyJoined = false;

        for (JoinList joinList : joinLists) {
            if (joinList.getFinancialLedger().getId().equals(flId)) {
                // 이미 JOINED 상태인 경우 flag를 설정하고 반복을 종료합니다.
                if (joinList.getJoined().equals(Joined.JOINED)) {
                    isAlreadyJoined = true;
                    break;
                }

                // WAIT 상태인 경우, 해당 JoinList를 삭제합니다.
                if (joinList.getJoined().equals(Joined.WAIT)) {
                    joinListRepository.deleteById(joinList.getId());
                    return; // 메소드를 종료합니다.
                }
            }
        }

        // flag가 true라면 이미 JOINED 상태인 것이므로 예외를 던집니다.
        if (isAlreadyJoined) {
            throw new IllegalArgumentException("이미 가계부에 합류되어있습니다.");
        }

    }

    public void quitFl(Long flId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        Long findedId = flRepository.findById(flId).get().getUser().getId();

        // 본인이 가계부 장이면 삭제
        if (userId == findedId)
            flRepository.deleteById(flId);

        // 본인이 장이 아닐때 삭제하는것
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        for (JoinList joinList : joinLists) {
            if (joinList.getFinancialLedger().getId().equals(flId))
                joinListRepository.deleteById(joinList.getId());
        }
    }

    public void kickUser(Long flId, Long kickingUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        Long findedId = flRepository.findById(flId).get().getUser().getId();

        Long joinListID = joinListRepository.findByUser_IdAndFinancialLedger_Id(kickingUserId, flId).getId();
        // 본인이 가계부 장이면 삭제
        if (userId == findedId) {
            joinListRepository.deleteById(joinListID);
        } else
            throw new IllegalArgumentException("가계부 장만 멤버를 탈퇴시킬 수 있습니다.");

    }

    public FLDto.FLInfos getFlInfo(Long flId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        FinancialLedger financialLedger = flRepository.findById(flId).get();


        List<Long> userIds = joinListRepository.findByFinancialLedger_Id(flId)
                .stream()
                .map(joinList -> joinList.getUser().getId())
                .collect(Collectors.toList());


        Boolean isMember = false;

        for (JoinList joinList : joinLists) {
            if(joinList.getFinancialLedger().getId().equals(flId))
                isMember = true;
        }

        if(isMember == true){
            return FLDto.FLInfos.builder().
                    title(financialLedger.getTitle()).
                    budget(financialLedger.getBudget()).
                    userIds(userIds).
                    headId(financialLedger.getUser().getId()).build();
        }else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 정보를 조회할 수 있습니다.");



    }

    public void setBudget(Long flId, Long budget) {
        if(budget<0)
            throw new IllegalArgumentException("예산은 0 이상이여야 합니다.");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        Boolean isMember = false;

        for (JoinList joinList : joinLists) {
            if(joinList.getFinancialLedger().getId().equals(flId))
                isMember = true;
        }

        if(isMember == true){
            flRepository.findById(flId).get().setBudget(budget);
        }else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 예산을 수정할 수 있습니다.");
    }

    public Long createFixed(FLDto.FixedInfos fixedInfos) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);
        Boolean isMember = false;

        for (JoinList joinList : joinLists) {
            if(joinList.getFinancialLedger().getId().equals(fixedInfos.getFlId()))
                isMember = true;
        }

        FinancialLedger financialLedger = flRepository.findById(fixedInfos.getFlId()).get();

        if(financialLedger == null)
            throw new IllegalArgumentException("가계부가 존재하지 않습니다.");

        // 금액, 날짜가 정상적인 값이 아니면 예외처리
        if(fixedInfos.getAmount() < 0)
            throw new IllegalArgumentException("고정 지출/수입 금액은 0 이상이여야 합니다.");
        if(fixedInfos.getDate() < 1 || fixedInfos.getDate() > 31)
            throw new IllegalArgumentException("고정 지출/수입 날짜는 1~31 사이여야 합니다.");

        if(isMember == true){
            Fixed fixed = new Fixed();
            fixed.setAmount(fixedInfos.getAmount());
            fixed.setContent(fixedInfos.getContent());
            fixed.setDate(fixedInfos.getDate());
            fixed.setIncome(fixedInfos.isIncome());
            fixed.setFinancialLedger(financialLedger);
            fixedRepository.save(fixed);
            return fixed.getId();
        }else
            throw new IllegalArgumentException("본인이 속한 가계부에서만 고정 지출을 추가할 수 있습니다.");
    }
}
