package com.webbasedsolutions.scinvestments;

import android.arch.lifecycle.ViewModel;

import com.webbasedsolutions.scinvestments.model.Payment;

public class PaymentViewModel extends ViewModel {

    private Payment payment;

    public PaymentViewModel(){

    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }
}
