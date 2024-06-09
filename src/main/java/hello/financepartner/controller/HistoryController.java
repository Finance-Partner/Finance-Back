package hello.financepartner.controller;

import hello.financepartner.domain.status.Category;
import hello.financepartner.domain.status.IsIncom;
import hello.financepartner.dto.HistoryDto;
import hello.financepartner.dto.UserDto;
import hello.financepartner.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "History", description = "수입/지출 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    @PostMapping("/history")
    @Operation(
            summary = "수입/지출 추가",
            description = "수입/지출을 추가합니다."
    )
    public ResponseEntity<UserDto.CheckResult> createHistory(Long flId, int year, int month, int day, Long amount, String content, Category isCategory, IsIncom isIncome) {
        historyService.createHistory(flId,year, month,day,amount,content,isCategory,isIncome);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("수입/지출을 추가했습니다.").build());
    }


    @PatchMapping("/history")
    @Operation(
            summary = "수입/지출 내역 수정",
            description = "수입/지출 내역을 수정합니다."
    )
    public ResponseEntity<UserDto.CheckResult> updateHistory(Long historyId, int year, int month, int day, Long amount, String content, Category category,IsIncom isIncom) {
        historyService.updateHistory(historyId,year, month,day,amount,content,category,isIncom);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("수입/지출 내역을 수정했습니다.").build());
    }


    @DeleteMapping("/history")
    @Operation(
            summary = "수입/지출 내역 삭제",
            description = "수입/지출 내역을 삭제합니다."
    )
    public ResponseEntity<UserDto.CheckResult> deleteHistory(Long historyId) {
        historyService.deleteHistory(historyId);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("수입/지출을 삭제했습니다.").build());
    }

    @GetMapping("/history/{flId}")
    @Operation(
            summary = "수입/지출 내역 불러오기.",
            description = "가계부 하나의 수입/지출 내역을 전부 가져옵니다."
    )
    public ResponseEntity<List<HistoryDto.Info>> getHistory(@PathVariable Long flId) {
        List<HistoryDto.Info> history = historyService.getHistory(flId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{flId}/{year}")
    @Operation(
            summary = "수입/지출 1년 내역 불러오기",
            description = "가계부 하나의 1년 수입/지출 내역을 가져옵니다."
    )
    public ResponseEntity<List<HistoryDto.Info>> getYearHistory(@PathVariable Long flId,@PathVariable int year) {
        List<HistoryDto.Info> monthHistory = historyService.getYearHistory(flId, year);
        return ResponseEntity.ok(monthHistory);
    }

    @GetMapping("/history/{flId}/{year}/{month}")
    @Operation(
            summary = "수입/지출 한달 내역 불러오기",
            description = "가계부 하나의 한달 수입/지출 내역을 가져옵니다."
    )
    public ResponseEntity<List<HistoryDto.Info>> getMonthHistory(@PathVariable Long flId,@PathVariable int year, @PathVariable int month) {
        List<HistoryDto.Info> monthHistory = historyService.getMonthHistory(flId, year, month);
        return ResponseEntity.ok(monthHistory);
    }


    @GetMapping("/history/{flId}/{year}/{month}/{day}")
    @Operation(
            summary = "수입/지출 하루 내역 불러오기",
            description = "가계부 하루 수입/지출 내역을 가져옵니다."
    )
    public ResponseEntity<List<HistoryDto.Info>> getMonthAndDayHistory(@PathVariable Long flId,@PathVariable int year,@PathVariable int month,@PathVariable int day) {
        List<HistoryDto.Info> monthAndDayHistory = historyService.getMonthAndDayHistory(flId, year, month, day);
        return ResponseEntity.ok(monthAndDayHistory);
    }


    @GetMapping("/history/user")
    @Operation(
            summary = "멤버 한명의 수입/지출 내역 불러오기.",
            description = "가계부에 속한 멤버의 가계부 내역을 가져옵니다."
    )
    public ResponseEntity<List<HistoryDto.Info>> getMonthAndDayHistory(Long flId,Long userId) {
        List<HistoryDto.Info> userHistory = historyService.getUserHistory(flId, userId);
        return ResponseEntity.ok(userHistory);
    }

}
