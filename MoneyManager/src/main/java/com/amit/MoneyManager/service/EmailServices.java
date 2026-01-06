package com.amit.MoneyManager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EmailServices {

  private final JavaMailSender mailSender;

    private static final Logger log = LoggerFactory.getLogger(EmailServices.class);

  @Value("${spring.mail.properties.mail.smtp.from}")
  private String fromEmail;

  public void sendEmail(String to, String subject, String body) {
      try{
          SimpleMailMessage message = new SimpleMailMessage();
          message.setFrom(fromEmail);
          message.setTo(to);
          message.setSubject(subject);
          message.setText(body);
          mailSender.send(message);
      } catch (Exception e) {
          log.error("Error sending email to {}: {}", to, e.getMessage(), e);
      }
  }
}
