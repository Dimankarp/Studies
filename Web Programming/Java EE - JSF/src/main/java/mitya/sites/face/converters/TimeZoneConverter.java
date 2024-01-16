package mitya.sites.face.converters;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.util.TimeZone;
@FacesConverter("mitya.sites.face.converters.TimeZoneConverter")
public class TimeZoneConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String value){
        return TimeZone.getTimeZone(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((TimeZone)value).getID();
    }


}
