package com.ccnta.app.notification.enums;

public enum NotificationTypes {

    PROJECT_DELETED("Project Deleted"),
    PROJECT_UPDATED("Project Updated"),
    PROJECT_CREATED("Project Created"),
    COLLABORATOR_ADDED("Collaborator Added"),
    COLLABORATOR_REMOVED("Collaborator Removed"),
    NOTE_CREATION("Note Created"),
    NOTE_UPDATE("Note Updated"),
    NOTE_ARCHIVE("Note Archived"),
    NOTE_RESTORE("Note Restored"),
    IMAGE_UPLOAD("Image Uploaded"),
    NOTE_DELETE("Note Deleted"),
    DRAFT_CREATION("Draft Created"),
    DRAFT_UPDATE("Draft Updated"),
    DRAFT_DELETE("Draft Deleted"),
    IMAGE_DOWNLOAD("Image Downloaded"),
    NOTE_PINNED("Note Pinned"),
    NOTE_UNPINNED("Note Unpinned"),
    DRAFT_PINNED("Draft Pinned"),
    DRAFT_UNPINNED("Draft Unpinned");

    private final String description;

    NotificationTypes(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
