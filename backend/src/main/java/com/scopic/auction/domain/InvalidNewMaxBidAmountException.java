package com.scopic.auction.domain;

public class InvalidNewMaxBidAmountException extends Exception {

    public InvalidNewMaxBidAmountException(String message) {
        super(message);
    }
}
