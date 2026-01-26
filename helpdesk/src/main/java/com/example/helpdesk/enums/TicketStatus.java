package com.example.helpdesk.enums;

public enum TicketStatus {
    OPEN("Open", "info"),
    IN_PROGRESS("In Progress", "warning"),
    RESOLVED("Resolved", "success"),
    CLOSED("Closed", "secondary"),
    REOPENED("Reopened", "danger");

    private final String displayName;
    private final String badgeClass;

    TicketStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
