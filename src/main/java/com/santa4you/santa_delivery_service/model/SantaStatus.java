package com.santa4you.santa_delivery_service.model;

public enum SantaStatus {
        PREPARING("Preparing for Christmas - Making toys and checking the list!"),
        STARTING_JOURNEY("Starting Journey"),
        DELIVERING_PRESENTS("Delivering Presents"),
        FINAL_DELIVERIES("Final Deliveries"),
        JOURNEY_COMPLETE("Journey Complete - Merry Christmas!");
        
        private final String status;
        
        SantaStatus(String status) {
            this.status = status;
        }
        
        public String getStatus() {
            return status;
        }
    }