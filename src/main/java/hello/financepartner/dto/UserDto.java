package hello.financepartner.dto;

import hello.financepartner.domain.JoinList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class UserDto {


    @Builder
    @Data
    @Schema(description = "회원 가입시 넘겨주는 정보")
    public static class JoinInfo {
        String email;
        String password;
        String name;
    }


    @Builder
    @Data
    @Schema(description = "로그인시 넘겨줘야하는 정보")
    public static class SignInRequest {
        String email;
        String password;
    }

    @Builder
    @Data
    @Schema(description = "토큰 정보 정보")
    public static class Token {
        String token;
    }

    @Builder
    @Data
    @Schema(description = "로그인시 넘겨줘야하는 정보")
    public static class PasswordResetInfo {
        String email;
        String name;
    }

    @Builder
    @Data
    @Schema(description = "비밀번호  정보")
    public static class PasswordcheckInfo {
        String password;
    }

    @Builder
    @Data
    @Schema(description = "이메일 정보")
    public static class EmailInfo {
        String email;
    }

    @Builder
    @Data
    @Schema(description = "이름 정보")
    public static class NamecheckInfo {
        String name;
        String photo;
    }

    @Builder
    @Data
    @Schema(description = "일치여부")
    public static class CheckResult {
        String result;
    }

    @Builder
    @Data
    @Schema(description = "유저 정보 변경 정보")
    public static class ChangeInfo {
        String name;
        String password;
    }

    @Builder
    @Data
    @Schema(description = "유저 정보 조회시 제공되는 정보")
    public static class ProvideInfo {
        String name;
        String email;
        String photo;
        Long userId;
        List<LegendInfo> myFlLists;
        List<Long> invitedLists;
    }

    @Builder
    @Data
    public static class LegendInfo {
        Long id;
        String name;
    }

    @Builder
    @Data
    @Schema(description = "이메일 인증시 사용하는 코드")
    public static class EmailVerify {
        String email;
        String verifyCode;
    }

}
