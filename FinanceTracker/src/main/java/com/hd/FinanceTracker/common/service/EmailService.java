package com.hd.FinanceTracker.common.service;

import com.hd.FinanceTracker.budget.entity.Budget;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendBudgetAlert(String userName,
                                String userEmail, Budget budget, BigDecimal spent) {
        String category = budget.getCategory() != null
                ? budget.getCategory().getCategoryName()
                : "Overall";

        String text = """
                Hi %s,
                
                You've spent ₹%s of your ₹%s %s budget.
                Period: %s to %s
                
                Consider reviewing your spending.
                
                — Finance Tracker
                """.formatted(
                userName, spent, budget.getAmount(),
                category, budget.getStartDate(),
                budget.getEndDate()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        // Todo replace with @Value annotation
        message.setFrom("harshdubey1011@gmail.com");
        message.setTo(userEmail);
        message.setSubject("Budget Alert — You've exceeded your " + category + " budget");
        message.setText(text);

        try {
            mailSender.send(message);
            log.info("Budget alert sent to {} for budget {}", userEmail, budget.getId());
        } catch(Exception e) {
            log.error("Failed to send budget alert to {}: {}", userEmail, e.getMessage(), e);
        }
    }
}