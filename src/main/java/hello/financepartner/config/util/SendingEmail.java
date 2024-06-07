package hello.financepartner.config.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
public class SendingEmail {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String title, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(htmlContent, true); // true는 HTML 이메일임을 나타냅니다.
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("이메일을 보내는데 실패했습니다.", e);
        }
    }
}
