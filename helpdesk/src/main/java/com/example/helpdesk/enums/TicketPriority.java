package com.example.helpdesk.enums;

public enum TicketPriority {
    LOW("Low", "success"),
    MEDIUM("Medium", "warning"),
    HIGH("High", "danger"),
    URGENT("Urgent", "danger");

    private final String displayName;
    private final String badgeClass;

    TicketPriority(String displayName, String badgeClass) {
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
