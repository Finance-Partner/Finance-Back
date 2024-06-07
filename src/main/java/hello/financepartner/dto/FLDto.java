package hello.financepartner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FLDto {

    @Builder
    @Data
    @Schema(description = "가계부 생성시 제공하는 정보")
    public static class FLInfo {
        Long budget;
        String title;
    }

    @Builder
    @Data
    @Schema(description = "가계부 생성시 제공하는 정보")
    public static class FLInfo2 {
        Long flId;
        Long budget;
        String title;
    }
    @Builder
    @Data
    @Schema(description = "가계부 생성시 제공하는 정보")
    public static class FLUsers {
        Long userId;
        String name;
    }

    @Builder
    @Data
    @Schema(description = "가계부 정보 조회시 줄 정보")
    public static class FLInfos {
        String title;
        Long budget;
        List<Long> userIds;
        Long headId;
    }
}
