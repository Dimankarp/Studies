package mitya.sites.face.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Named("dateFormatter")
@SessionScoped
public class DateFormatter implements Serializable {
    private TimeZone zone = TimeZone.getDefault();
    private SimpleDateFormat format;
    public TimeZone getZone() {
        return zone;
    }
    public void setZone(TimeZone zone) {
        this.zone = zone;
        format.setTimeZone(zone);
    }
    public DateFormatter(){
        format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        format.setTimeZone(zone);
    }
    public String format(Date date){
        return format.format(date);
    }
}
