package clf.integra.backend.mapper;

import clf.integra.backend.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class NotificationMapper{
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Notification notification){
        System.out.println(notification);
        SimpleFilterProvider filter = new SimpleFilterProvider()
                .addFilter("notificationFilter", SimpleBeanPropertyFilter.filterOutAllExcept("id","user"));
        try {
            return objectMapper.writer(filter).writeValueAsString(notification);
        }
        catch (Exception e) {
            return "Could not generate notification, exception: " + e.getMessage();
        }
    }

}
