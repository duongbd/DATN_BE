package vn.nuce.datn_be.model.enumeration;

public enum CandidateStatus {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE"),
    BLOCK("BLOCK"),
    DISCONNECTED("DISCONNECTED");

    private final String name;

    CandidateStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static CandidateStatus getCandidateStatusByName(String name) {
        for (CandidateStatus candidateStatus : CandidateStatus.values()) {
            if (name.equalsIgnoreCase(candidateStatus.getName())) {
                return candidateStatus;
            }
        }
        return null;
    }
}
