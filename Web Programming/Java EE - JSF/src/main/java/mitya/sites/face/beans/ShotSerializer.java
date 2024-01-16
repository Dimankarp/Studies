package mitya.sites.face.beans;

import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import mitya.sites.face.data.model.ShotRecord;

import java.io.Serializable;
import java.util.Collection;

@Named("shotSerializer")
@ApplicationScoped
public class ShotSerializer implements Serializable {
    public ShotSerializer(){
    }
    public String toJSON(Collection<ShotRecord> recordedShots){
        if(recordedShots != null){
            return new Gson().toJson(recordedShots.toArray());
        }
        else{
            return new Gson().toJson(new ShotRecord[]{});
        }

    }

}
