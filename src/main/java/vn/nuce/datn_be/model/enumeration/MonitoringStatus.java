package vn.nuce.datn_be.model.enumeration;

public enum MonitoringStatus {
    NORMAL("NORMAL"),
    WARN("WARN"),
    ALERT("ALERT");

    MonitoringStatus(String name){
    }

    public String getName() {
        return this.name();
    }

    public static MonitoringStatus getMonitoringStatusByName(String name) {
        for (MonitoringStatus monitoringStatus : MonitoringStatus.values()) {
            if (name.equalsIgnoreCase(monitoringStatus.getName())) {
                return monitoringStatus;
            }
        }
        return null;
    }
}
