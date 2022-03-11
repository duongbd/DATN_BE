package vn.nuce.datn_be.model.enumeration;

public enum SendMailStatus {
    UNSENT("UNSENT"),       // chưa gửi
    SEND("SEND"),   // đã gửi
    FAIL("FAIL");         // thất bại

    private final String name;

    SendMailStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SendMailStatus getSendMailStatusByName(String name) {
        for (SendMailStatus sendMailStatus : SendMailStatus.values()) {
            if (name.equalsIgnoreCase(sendMailStatus.getName())) {
                return sendMailStatus;
            }
        }
        return null;
    }
}
