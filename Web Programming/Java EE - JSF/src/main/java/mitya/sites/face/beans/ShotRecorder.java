package mitya.sites.face.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import mitya.sites.face.data.model.ShotRecord;

import java.io.Serializable;
import java.util.List;

@Named("shotRecorder")
@SessionScoped
public class ShotRecorder implements Serializable{
    private ShotRecord lastShotRecord;
    public ShotRecord getLastShotRecord() {
        return lastShotRecord;
    }
    public void setLastShotRecord(ShotRecord lastShotRecord) {
        this.lastShotRecord = lastShotRecord;
    }

    private List<ShotRecord> recordedShots;
    public List<ShotRecord> getRecordedShots() {
        return  recordedShots != null ? List.copyOf(recordedShots) : null;
    }
    public void setRecordedShots(List<ShotRecord> recordedShots) {
        this.recordedShots = recordedShots;
    }
    @Inject
    private ShotSerializer serializer;

    public ShotRecorder(){}
    public String getJSON(){
        return serializer.toJSON(recordedShots);
    }

    @Override
    public String toString() {
        return "ShotRecorder{" +
                "lastShotRecord=" + lastShotRecord +
                ", recordedShots=" + recordedShots +
                '}';
    }

}


