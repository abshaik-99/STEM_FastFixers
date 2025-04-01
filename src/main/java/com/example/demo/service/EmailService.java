package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String name, String email, String phone, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("stemfastfixers@gmail.com");
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);

        // ✅ Include phone number in the email body
        mailMessage.setText("From: " + name + " (" + email + ")\nPhone: " + phone + "\n\n" + message);

        mailSender.send(mailMessage);
    }

    public void sendInvitationEmail(String name, String email) {
        String subject = "Welcome to Handyman Jobs";
        String body = "Hello " + name + ",\n\n"
            + "Thank you for registering with Handyman Jobs! "
            + "You can now explore and post handyman jobs.\n"
            + "At STEM FastFixers, we've got a unique and budget-friendly way to make life easier for busy adults and seniors. "
            + "We provide premium, trustworthy handyman services, take care of your home inside and out, and offer specialized senior modifications.\n\n"
            + "Best Regards,\n"
            + "Handyman Team";

            sendEmail(email, name, email, "", subject, body);
    }
}

