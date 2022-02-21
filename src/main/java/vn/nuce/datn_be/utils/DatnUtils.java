package vn.nuce.datn_be.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import vn.nuce.datn_be.security.UserDetailsImpl;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatnUtils {
    public static UserDetailsImpl principalToUser(Principal principal) {
        Assert.notNull(principal, "principal mustn't be null");
        return (UserDetailsImpl) principal;
    }

    public static Date getTimeSpecifyMinute(Date date){
        Assert.notNull(date, "date mustn't be null");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date cvtToGmt( Date date, Integer gmt){
        TimeZone tz = TimeZone.getDefault();
        Date ret = new Date( date.getTime() - tz.getRawOffset() );

        // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
        if ( tz.inDaylightTime( ret )){
            Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );

            // check to make sure we have not crossed back into standard time
            // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
            if ( tz.inDaylightTime( dstDate )){
                ret = dstDate;
            }
        }
        Calendar gmtCalendar= Calendar.getInstance();
        gmtCalendar.setTime(ret);
        gmtCalendar.add(Calendar.HOUR, gmt);
        return gmtCalendar.getTime();
    }
}
