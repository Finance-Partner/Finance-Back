package hello.financepartner.controller;

import hello.financepartner.dto.NoticeDto;
import hello.financepartner.dto.UserDto;
import hello.financepartner.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notice", description = "가계부 채팅 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;
    @PostMapping("/notice")
    @Operation(
            summary = "가계부 채팅 작성",
            description = "가계부 채팅을 작성합니다."
    )
    public ResponseEntity<UserDto.CheckResult> createNotice(Long flId, String content) {
        noticeService.createNotice(flId,content);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("채팅 작성 완료").build());
    }

    @GetMapping("/notice")
    @Operation(
            summary = "가계부 채팅 작성",
            description = "가계부 채팅을 작성합니다."
    )
    public ResponseEntity<List<NoticeDto.NoticeInfo>> readNotice(Long flId) {
        List<NoticeDto.NoticeInfo> noticeInfos = noticeService.readNotice(flId);
        return ResponseEntity.ok(noticeInfos);
    }

    @PatchMapping("/notice")
    @Operation(
            summary = "가계부 채팅 수정",
            description = "가계부 채팅을 수정합니다."
    )
    public ResponseEntity<UserDto.CheckResult> updateNotice(Long commentId, String content) {
        noticeService.updateNotice(commentId,content);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("채팅 수정 완료").build());
    }

    @DeleteMapping("/notice")
    @Operation(
            summary = "가계부 채팅 삭제",
            description = "가계부 채팅을 삭제합니다."
    )
    public ResponseEntity<UserDto.CheckResult> deleteNotice(Long commentId) {
        noticeService.deleteNotice(commentId);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("채팅 삭제 완료").build());
    }
}
