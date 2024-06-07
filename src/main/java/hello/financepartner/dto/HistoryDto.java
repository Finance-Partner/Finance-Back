package hello.financepartner.dto;

import hello.financepartner.domain.status.Category;
import hello.financepartner.domain.status.IsIncom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HistoryDto {

    @Builder
    @Data
    @Schema(description = "수입/지출 정보")
    public static class Info {
        Long id;
        LocalDate date;
        Long amount;
        String content;
        Category category;
        IsIncom isIncome;
    }


}
