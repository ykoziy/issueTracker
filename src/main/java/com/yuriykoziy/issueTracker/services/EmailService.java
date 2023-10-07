package com.yuriykoziy.issueTracker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  @Autowired
  private JavaMailSender javaMailSender;

  public void sendVerificationEmail(String to, String token) {
    // Create an email message
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Email Verification");
    message.setText("Hello,\n"
        + "\nThank you for signing up, please verify your email.\n\n"
        + "\nClick the link below to verify your email:\n\n"
        + "\nhttp://localhost:8080/api/v1/auth/verify-email?token=" + token);

    // Send the email
    javaMailSender.send(message);
  }
}
