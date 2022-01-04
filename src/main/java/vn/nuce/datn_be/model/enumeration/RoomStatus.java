package vn.nuce.datn_be.model.enumeration;

public enum RoomStatus {
    ACTIVE("ACTIVE");

    private final String name;

    RoomStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static RoomStatus getRoomStatusByName(String name) {
        for (RoomStatus roomStatus : RoomStatus.values()) {
            if (name.equalsIgnoreCase(roomStatus.getName())) {
                return roomStatus;
            }
        }
        return null;
    }
}
