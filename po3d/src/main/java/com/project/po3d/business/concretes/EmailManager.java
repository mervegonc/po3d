package com.project.po3d.business.concretes;

import org.springframework.stereotype.Service;

import com.project.po3d.business.abstracts.EmailService;

@Service
public class EmailManager implements EmailService {

    // İleride JavaMailSender gibi şeyler buraya entegre edilir
    @Override
    public void send(String to, String subject, String body) {
        System.out.println("📧 E-Posta Gönderiliyor:");
        System.out.println("Kime: " + to);
        System.out.println("Konu: " + subject);
        System.out.println("İçerik:\n" + body);
    }
}

