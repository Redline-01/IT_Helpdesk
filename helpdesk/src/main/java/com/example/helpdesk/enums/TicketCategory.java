package com.example.helpdesk.enums;

public enum TicketCategory {
    HARDWARE("Hardware"),
    SOFTWARE("Software"),
    NETWORK("Network"),
    ACCESS("Access & Permissions"),
    EMAIL("Email"),
    PRINTER("Printer"),
    PHONE("Phone"),
    OTHER("Other");

    private final String displayName;

    TicketCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
