package com.kirsehirfilix.movieApi.Service;

import com.kirsehirfilix.movieApi.dto.MailBody;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service // Bu anotasyon, EmailService sınıfının bir Spring servis bileşeni olduğunu belirtir.
public class EmailService {

    private final JavaMailSender mailSender; // E-posta gönderimi için kullanılan JavaMailSender sınıfının bir örneğini tutar.

    // Yapıcı metot (constructor), JavaMailSender bağımlılığını alır ve mailSender alanına atar.
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Basit bir e-posta mesajı gönderir.
    public void sendSimpleMessage(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage(); // Yeni bir SimpleMailMessage nesnesi oluşturur.
        message.setTo(mailBody.to()); // E-posta alıcısını MailBody içindeki 'to' alanına ayarlar.
        message.setFrom("mertkacar5800@gmail.com"); // E-posta gönderen adresini ayarlar.
        message.setSubject(mailBody.subject()); // E-posta konusunu MailBody içindeki 'subject' alanına ayarlar.
        message.setText(mailBody.text()); // E-posta içeriğini MailBody içindeki 'text' alanına ayarlar.

        mailSender.send(message); // E-posta mesajını gönderir.
    }
}
