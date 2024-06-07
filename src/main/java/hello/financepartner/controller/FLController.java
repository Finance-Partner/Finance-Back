package hello.financepartner.controller;

import hello.financepartner.dto.FLDto;
import hello.financepartner.dto.UserDto;
import hello.financepartner.service.FLService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FL", description = "가계부 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class FLController {

    private final FLService flService;
    @PostMapping("/fl")
    @Operation(
            summary = "가계부 생성하기",
            description = "가계부를 생성합니다. 가계부 제목과 가계부 예산을 주면 가계부가 생성됩니다."
    )
    public ResponseEntity<UserDto.CheckResult> createFL(FLDto.FLInfo flInfo) {
        flService.createFL(flInfo);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부 생성 완료").build());
    }

    @PatchMapping("/fl")
    @Operation(
            summary = "가계부 정보 수정하기",
            description = "가계부 정보를 수정합니다."
    )
    public ResponseEntity<UserDto.CheckResult> updateFL(FLDto.FLInfo2 flInfo) {
        flService.updateFL(flInfo);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부 수정 완료").build());
    }

    @DeleteMapping("/fl")
    @Operation(
            summary = "가계부 정보 삭제하기",
            description = "가계부 정보를 삭제합니다."
    )
    public ResponseEntity<UserDto.CheckResult> deleteFL(Long flId) {
        flService.deleteFL(flId);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부 삭제 완료").build());
    }

    @GetMapping("/fl/users")
    @Operation(
            summary = "가계부 인원 확인하기",
            description = "가계부 인원 리스트를 확인합니다."
    )
    public ResponseEntity<List<FLDto.FLUsers>> getFLUsers(Long flId) {
        List<FLDto.FLUsers> flUsers =  flService.findFLUsers(flId);
        return ResponseEntity.ok(flUsers);
    }

    @PostMapping("/fl/invite")
    @Operation(
            summary = "가계부에 멤버 초대하기",
            description = "가계부를 생성합니다. 가계부 제목과 가계부 예산을 주면 가계부가 생성됩니다."
    )
    public ResponseEntity<UserDto.CheckResult> inviteUser(Long flId, String email) {
        flService.inviteUser(flId,email);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부에 초대 완료").build());
    }

    @PostMapping("/fl/invite/accept/{flId}")
    @Operation(
            summary = "가계부 초대 수락",
            description = "본인에게 온 초대를 받습니다. 본인에게 초대가 온 가계부에만 들어갈 수 있습니다."
    )
    public ResponseEntity<UserDto.CheckResult> AcceptInvite(@PathVariable Long flId) {
        flService.acceptInvite(flId);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부에 초대를 받았습니다.").build());
    }


    @PostMapping("/fl/invite/reject/{flId}")
    @Operation(
            summary = "가계부 초대 거절",
            description = "본인에게 온 초대를 거절합니다. 본인에게 초대가 온 가계부에만 들어갈 수 있습니다."
    )
    public ResponseEntity<UserDto.CheckResult> rejectInvite(@PathVariable Long flId) {
        flService.rejectInvite(flId);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부에 초대를 거절했습니다.").build());
    }


    @DeleteMapping("/fl/quit")
    @Operation(
            summary = "가계부 탈퇴",
            description = "본인이 가입한 가계부에서 탈퇴합니다. 이때 가계부 장이 탈퇴시 가계부가 삭제됩니다."
    )
    public ResponseEntity<UserDto.CheckResult> quitFl(Long flId) {
        flService.quitFl(flId);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부 "+ flId +"번에서 탈퇴했습니다.").build());
    }

    @PostMapping("/kick")
    @Operation(
            summary = "가계부에서 타멤버 추방",
            description = "가계부 장이 가계부 멤버를 탈퇴시킬 수 있습니다."
    )
    public ResponseEntity<UserDto.CheckResult> kickUser(Long flId, Long userId) {
        flService.kickUser(flId,userId);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부에서 "+ userId +"번 회원을 탈퇴시켰습니다.").build());
    }
    @GetMapping("/fl/info")
    @Operation(
            summary = "가계부 정보 받아오기",
            description = "본인이 속한 가계부의 정보를 가져올 수 있습니다."
    )
    public ResponseEntity<FLDto.FLInfos> getFlInfo(Long flId) {
        FLDto.FLInfos flInfos = flService.getFlInfo(flId);
        return ResponseEntity.ok(flInfos);
    }

    @PostMapping("/fl/budget")
    @Operation(
            summary = "가계부 예산 설정",
            description = "본인의 예산을 설정합니다. 예산은 0미만이 될 수 없습니다. 가계부장이 아니여도 멤버라면 수정 가능"
    )
    public ResponseEntity<UserDto.CheckResult> setBudget(Long flId, Long budget) {
        flService.setBudget(flId,budget);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("가계부 "+ flId +"번에서 예산을"+ budget+"로 변경했습니다.").build());
    }

    @PostMapping("/fl/fixed")
    @Operation(
            summary = "고정 수입/지출 추가",
            description = "고정 지출을 추가합니다. 고정 과 고정 지출 금액을 주면 고정 지출이 생성됩니다."
    )
    public ResponseEntity<UserDto.CheckResult> createFixed(FLDto.FixedInfos fixedInfos) {
        Long fixedId = flService.createFixed(fixedInfos);
        String string = fixedId.toString();
        return ResponseEntity.ok(UserDto.CheckResult.builder().result(string).build());
    }

}
