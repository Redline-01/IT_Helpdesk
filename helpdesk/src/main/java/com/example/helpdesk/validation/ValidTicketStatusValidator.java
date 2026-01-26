package com.example.helpdesk.validation;

import com.example.helpdesk.enums.TicketStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidTicketStatusValidator implements ConstraintValidator<ValidTicketStatus, TicketStatus> {

    @Override
    public boolean isValid(TicketStatus status, ConstraintValidatorContext context) {
        if (status == null) {
            return false;
        }
        // Add custom validation logic here
        // For example, prevent setting status to CLOSED without going through RESOLVED
        return true;
    }
}
