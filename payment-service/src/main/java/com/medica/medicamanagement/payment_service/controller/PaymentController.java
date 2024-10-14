package com.medica.medicamanagement.payment_service.controller;

import com.medica.medicamanagement.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * The type Payment controller.
 */
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * Gets payment page.
     *
     * @param amount        the amount
     * @param appointmentId the appointment id
     * @param model         the model
     * @return the payment page
     */
    @GetMapping(value = "/payment-interface")
    public String getPaymentPage(@RequestParam("amount") String amount, String appointmentId, Model model) {
        return this.paymentService.getClientToken(appointmentId, amount, model);
    }

    /**
     * Process payment string.
     *
     * @param nonce         the nonce
     * @param amount        the amount
     * @param appointmentId the appointment id
     * @param paymentMethod the payment method
     * @param model         the model
     * @return the string
     */
    @PostMapping("/process")
    public String processPayment(@RequestParam String nonce,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam("appointmentId") String appointmentId,
                                 @RequestParam("paymentMethod") String paymentMethod,
                                 Model model) {
        return this.paymentService.processPayment(nonce, amount, paymentMethod, appointmentId, model);
    }
}
