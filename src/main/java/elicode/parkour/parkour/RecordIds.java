package elicode.parkour.parkour;

public class RecordIds {

    private final String courseId, timeId, uuid;
    private final long time, create_at;

    private String reason;
    private int id;

    public RecordIds(String courseId, String timeId, String uuid, long time, long creared_at) {
        this.courseId = courseId;
        this.timeId = timeId;
        this.uuid = uuid;
        this.time = time;
        this.create_at = creared_at;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTimeId() {
        return timeId;
    }

    public String getUUID() {
        return uuid;
    }

    public long getTime() {
        return time;
    }

    public long getCreate_at() {
        return create_at;
    }
}
