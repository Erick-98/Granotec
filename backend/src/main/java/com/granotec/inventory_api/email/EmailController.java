package com.granotec.inventory_api.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forgot-password")
public class EmailController {

    private final EmailService service;

    @PostMapping("/send")
    public ResponseEntity<String> sendTextEmail(@RequestBody EmailRequest emailRequest) {
        try{
            service.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
            return ResponseEntity.ok("Email sent successfully");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/send-html")
    public ResponseEntity<String> sendHtmlEmail(@RequestBody EmailRequest emailRequest) {
        try{
            service.sendHtmlEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
            return ResponseEntity.ok("HTML Email sent successfully");
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("Failed to send HTML email: " + e.getMessage());
        }
    }
}
