package com.medica.medicamanagement.payment_service.controller;

import com.medica.medicamanagement.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping(value = "/payment-interface")
    public String getPaymentPage(@RequestParam("amount") String amount, String appointmentId, Model model) {
        return this.paymentService.getClientToken(appointmentId, amount, model);
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam String nonce,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam("appointmentId") String appointmentId,
                                 @RequestParam("paymentMethod") String paymentMethod,
                                 Model model) {
        return this.paymentService.processPayment(nonce, amount, paymentMethod, appointmentId, model);
    }
}
