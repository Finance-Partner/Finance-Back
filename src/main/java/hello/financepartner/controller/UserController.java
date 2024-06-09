package hello.financepartner.controller;

import hello.financepartner.config.s3.S3Upload;
import hello.financepartner.dto.UserDto;
import hello.financepartner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "User", description = "User 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Upload s3Upload;


    @Operation(
            summary = "회원 가입",
            description = "회원가입을 합니다. 바로 회원가입이 되는 api 이니 프론트에서 이메일 인증, 닉네임 중복확인을 하고 넘겨야합니다."
    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "정보 조회 성공시", content = @Content(schema = @Schema(implementation = GetMemberInfo.class))),
//    })
    @PostMapping("/user")
    public ResponseEntity<UserDto.CheckResult> join(UserDto.JoinInfo joinDto) {
        Long userId = userService.join(joinDto.getEmail(), joinDto.getPassword(), joinDto.getName());
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("회원가입 성공\n"+ "회원아이디: " + userId).build());

    }


    @Operation(
            summary = "로그인",
            description = "로그인을 합니다. 이메일과 비밀번호를 json으로 넘기면, json으로 토큰을 제공합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<UserDto.Token> login(UserDto.SignInRequest signInRequest) {
        String jwt = userService.signIn(signInRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", jwt);
        return ResponseEntity.ok(UserDto.Token.builder().token(jwt).build());
    }

    @PostMapping("/user/reset")
    @Operation(
            summary = "비밀번호 재설정",
            description = "이메일, 이름, 생일 정보를 입력이 이메일로 재설정된 비밀번호를 발송합니다."
    )
    public ResponseEntity<UserDto.CheckResult> resetPassword(UserDto.PasswordResetInfo userDto) {
        userService.findPassword(userDto.getEmail(), userDto.getName());
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("비밀번호 초기화 성공").build());
    }

    @GetMapping("/password")
    @Operation(
            summary = "비밀번호 확인",
            description = "토큰을 기준으로 비밀번호가 맞는지를 확인합니다."
    )
    public ResponseEntity<UserDto.CheckResult> checkPassword(UserDto.PasswordcheckInfo passwordcheckInfo) {
        String password = passwordcheckInfo.getPassword();
        String result = userService.checkPassword(password);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result(result).build());
    }

    @GetMapping("/email")
    @Operation(
            summary = "이메일 중복 확인",
            description = "이메일 중복여부를 확인합니다. true : 중복임, false : 중복아님"
    )
    public ResponseEntity<UserDto.CheckResult> emailDoubleCheck(UserDto.EmailInfo emailcheckInfo) {
        String email = emailcheckInfo.getEmail();
        String result = userService.emailDoubleCheck(email);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result(result).build());

    }


    @GetMapping("/name")
    @Operation(
            summary = "이름 중복 확인",
            description = "이름 중복여부를 확인합니다. true : 중복임, false : 중복아님"
    )
    public ResponseEntity<UserDto.CheckResult> nameDoubleCheck(UserDto.PasswordcheckInfo passwordcheckInfo) {
        String name = passwordcheckInfo.getPassword();
        String result = userService.nameDoubleCheck(name);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result(result).build());
    }


    @PostMapping("/photo")
    @Operation(
            summary = "프로필 사진 등록",
            description = "프로필 사진을 등록합니다. 이때 헤더에 토큰과 함께, 멀티파트 폼데이터로 img라는 이름으로 보내야합니다.",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(
                                    type = "string",
                                    format = "binary"
                            )
                    )
            )
    )
    public ResponseEntity<UserDto.CheckResult> nameDoubleCheck(@RequestParam("img") MultipartFile multipartFile) throws IOException {
        long fileSize = multipartFile.getSize();
        String url = s3Upload.upload(multipartFile.getInputStream(), multipartFile.getOriginalFilename(), fileSize);
        userService.savePhoto(url);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("저장 완료").build());
    }

    @DeleteMapping("/photo")
    @Operation(
            summary = "프로필 사진 삭제",
            description = "등록된 프로필 사진을 삭제합니다."
    )
    public ResponseEntity<UserDto.CheckResult> deletePhoto() {
        userService.deletePhoto();
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("프로필 사진 삭제 완료").build());
    }

    @GetMapping("/emailtoname")
    @Operation(
            summary = "이메일로 유저 이름 가져오기",
            description = "이메일로 유저의 이름을 가져오는데 사용합니다."
    )
    public ResponseEntity<UserDto.NamecheckInfo> emailToName(UserDto.EmailInfo emailInfo) {
        String email = emailInfo.getEmail();
        UserDto.NamecheckInfo namecheckInfo = userService.emailToName(email);
        return ResponseEntity.ok(namecheckInfo);
    }

    @DeleteMapping("/user")
    @Operation(
            summary = "유저 탈퇴하기",
            description = "토큰을 기반으로 유저를 삭제합니다. 삭제하면 가계부도 함께 삭제됩니다."
    )
    public ResponseEntity<UserDto.CheckResult> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("유저 탈퇴 완료").build());
    }


    @GetMapping("/user")
    @Operation(
            summary = "본인 정보 가져오기",
            description = "토큰을 기반으로 본인의 정보를 가져옵니다."
    )
    public ResponseEntity<UserDto.ProvideInfo> getUserInfo() {
        UserDto.ProvideInfo info = userService.getUserInfo();
        return ResponseEntity.ok(info);
    }

    @PatchMapping("/user")
    @Operation(
            summary = "본인 정보 수정하기",
            description = "토큰을 기반으로 유저의 정보를 수정합니다."
    )
    public ResponseEntity<UserDto.CheckResult> changeUserInfo(UserDto.ChangeInfo changeInfo) {
        userService.changeUserInfo(changeInfo);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("유저 정보 변경 완료").build());
    }

    @PostMapping("/email/sending")
    @Operation(
            summary = "이메일 인증번호 보내기",
            description = "이메일로 인증번호를 보냅니다."
    )
    public ResponseEntity<UserDto.CheckResult> verifyEmail(String email) {
        userService.verifyEmail(email);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result("인증메일 전송 완료").build());
    }


    @PostMapping("/email/verify")
    @Operation(
            summary = "이메일 인증번호 확인",
            description = "발급 받은 인증번호가 맞는지 확인합니다. true: 인증완료, false : 인증안됨"
    )
    public ResponseEntity<UserDto.CheckResult> checkVerifyEmail(UserDto.EmailVerify emailVerify) {
        String result = userService.checkEmailVerify(emailVerify);
        return ResponseEntity.ok(UserDto.CheckResult.builder().result(result).build());
    }





}
