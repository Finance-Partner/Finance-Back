package hello.financepartner.service;

import hello.financepartner.config.jwt.JwtService;
import hello.financepartner.config.util.SendingEmail;
import hello.financepartner.domain.EmailVerify;
import hello.financepartner.domain.JoinList;
import hello.financepartner.domain.Users;
import hello.financepartner.domain.status.Joined;
import hello.financepartner.dto.UserDto;
import hello.financepartner.repository.EmailVerifyRepository;
import hello.financepartner.repository.JoinListRepository;
import hello.financepartner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final SendingEmail sendingEmail;
    private final JwtService jwtService;
    private final EmailVerifyRepository emailVerifyRepository;
    private final JoinListRepository joinListRepository;

    //회원가입
    public Long join(String email, String password, String name) {
        validateDuplicateMember(email); // 중복 회원 검증
        Users user = new Users();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        userRepository.save(user);
        Long userId = userRepository.findByEmail(email).getId();

        //토큰 생성
        return userId;
    }

    // 로그인
    public String signIn(UserDto.SignInRequest request) {
        Users user = userRepository.findByEmail(request.getEmail());
        String token;
        if (user == null) {
            throw new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다.");
        }

        if (user.getPassword().equals(request.getPassword())) {
            //토큰 생성
            token = jwtService.createToken(user.getId());    // 토큰 생성
        } else {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        return token;    // 생성자에 토큰 추가
    }


    // 비밀번호 찾기
    public void findPassword(String email, String name) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다.");
        }
        if (user.getName().equals(name)) {
            // 비밀번호 변경
            String newPassword = getTempPassword();
            String htmlContent = "<html>"
                    + "<body>"
                    + "<h1>비밀번호 변경 안내</h1>"
                    + "<p>안녕하세요, 회원님의 비밀번호를 임시로 변경해드립니다.</p>"
                    + "<p><strong>새로운 비밀번호: </strong>" + newPassword + "</p>"
                    + "<p>로그인 후, 비밀번호를 반드시 변경해주시기 바랍니다.</p>"
                    + "<a href='http://localhost:5173' target='_blank'>홈페이지로 이동</a>"
                    + "<p>감사합니다.</p>"
                    + "</body>"
                    + "</html>";
            user.setPassword(newPassword);
            sendingEmail.sendMail(email, "[Finance Partner] 초기화된 비밀번호 입니다.", htmlContent);
        } else {
            throw new IllegalStateException("이름 또는 생년월일이 일치하지 않습니다.");
        }

    }

    public String checkPassword(String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        String real_password = userRepository.findById(userId).get().getPassword();
        if (real_password.equals(password)) {
            return "true";
        } else
            return "false";
    }

    public String emailDoubleCheck(String email) {
        Users user = userRepository.findByEmail(email);
        if (user == null || user.getEmail() == null)
            return "false";
        else
            return "true";
    }

    public String nameDoubleCheck(String name) {
        Users user = userRepository.findByName(name);
        if (user == null || user.getName() == null)
            return "false";
        else
            return "true";
    }

    public void savePhoto(String url) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        userRepository.findById(userId).get().setPhoto(url);
    }

    public void deletePhoto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        userRepository.findById(userId).get().setPhoto(null);
    }


    public UserDto.NamecheckInfo emailToName(String email) {
        String name = userRepository.findByEmail(email).getName();
        String photo = userRepository.findByEmail(email).getPhoto();
        return UserDto.NamecheckInfo.builder().name(name).photo(photo).build();
    }


    public void deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        userRepository.deleteById(userId);
    }

    public UserDto.ProvideInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        Users user = userRepository.findById(userId).get();
        List<JoinList> joinLists = joinListRepository.findByUser_Id(userId);

        List<UserDto.LegendInfo> myFlList = new ArrayList<>();
        List<UserDto.LegendInfo> myManageFlList = new ArrayList<>();
        List<UserDto.LegendInfo> invitedList = new ArrayList<>();

        for (JoinList joinList : joinLists) {
            if (joinList.getJoined() == Joined.JOINED)
            {
                // 가계부의 id와 이름을 LegendInfo에 저장후 이것을 myFlList에 저장
                myFlList.add(UserDto.LegendInfo.builder().id(joinList.getFinancialLedger().getId()).name(joinList.getFinancialLedger().getTitle()).build());
                // 가계부장인 경우 가계부의 id와 이름을 LegendInfo에 저장후 이것을 myManageFlList에 저장
                if (joinList.getFinancialLedger().getUser().getId() == userId)
                    myManageFlList.add(UserDto.LegendInfo.builder().id(joinList.getFinancialLedger().getId()).name(joinList.getFinancialLedger().getTitle()).build());
            } else {
                // 초대된 가계부의 id를 invitedList에 저장
                invitedList.add(UserDto.LegendInfo.builder().id(joinList.getFinancialLedger().getId()).name(joinList.getFinancialLedger().getTitle()).build());
            }

        }


        return UserDto.ProvideInfo.builder().
                photo(user.getPhoto()).
                userId(userId).
                email(user.getEmail()).
                name(user.getName()).
                myFlLists(myFlList).
                invitedLists(invitedList)
                .myManageFlLists(myManageFlList)
                .build();

    }

    public void changeUserInfo(UserDto.ChangeInfo changeInfo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = Long.parseLong(username);
        Users user = userRepository.findById(userId).get();
        if (changeInfo.getName() != null)
            user.setName(changeInfo.getName());
        if (changeInfo.getPassword() != null)
            user.setPassword(changeInfo.getPassword());
    }


    public void verifyEmail(String email) {
        Users user = userRepository.findByEmail(email);
        if (user != null) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다..");
        }

        if (emailVerifyRepository.findByEmail(email) != null)
            emailVerifyRepository.delete(emailVerifyRepository.findByEmail(email));

        String verifyCode = getVerifyCode();
        String htmlContent = "<html>"
                + "<body>"
                + "<h1>인증번호 안내</h1>"
                + "<p>안녕하세요, 회원님의 인증코드를 보내드립니다.</p>"
                + "<p><strong>인증번호: </strong>" + verifyCode + "</p>"
                + "<a href='http://fphosting.s3-website.ap-northeast-2.amazonaws.com/' target='_blank'>홈페이지로 이동</a>"
                + "<p>감사합니다.</p>"
                + "</body>"
                + "</html>";

        EmailVerify emailVerify = new EmailVerify();
        emailVerify.setIsVerified(0);
        emailVerify.setEmail(email);
        emailVerify.setCertification(verifyCode);
        emailVerifyRepository.save(emailVerify);
        sendingEmail.sendMail(email, "[MOAYO] 인증번호입니다", htmlContent);
    }

    public String checkEmailVerify(UserDto.EmailVerify emailVerify) {
        String realCode = emailVerifyRepository.findByEmail(emailVerify.getEmail()).getCertification();
        if (emailVerify.getVerifyCode().equals(realCode)) {
            emailVerifyRepository.findByEmail(emailVerify.getEmail()).setIsVerified(1);
            return "true";
        } else
            return "false";
    }


    private String getVerifyCode() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        String str = "";

        int idx = 0;
        for (int i = 0; i < 4; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }


    // 회원가입시 중복확인 검사
    void validateDuplicateMember(String email) {
        Users findUsers = userRepository.findByEmail(email);
        if (findUsers != null) { // 이미 존재하는 회원
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    public String getTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }
}
