package com.map524s1a.scinvestments;

import android.arch.lifecycle.ViewModel;

import com.map524s1a.scinvestments.model.Payment;

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
