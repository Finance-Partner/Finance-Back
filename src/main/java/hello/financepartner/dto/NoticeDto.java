package hello.financepartner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoticeDto {

    @Builder
    @Data
    @Schema(description = "채팅 조회시 제공할 정보")
    public static class NoticeInfo {
        String content;
        String name;
        Long userId;
        LocalDateTime time;
    }
}
