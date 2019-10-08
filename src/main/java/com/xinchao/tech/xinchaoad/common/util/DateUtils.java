package com.xinchao.tech.xinchaoad.common.util;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtils {
    public static final String Y = "yyyy";
    public static final String M = "MM";
    public static final String D = "dd";
    public static final String YM = "yyyy-MM";
    public static final String YMD = "yyyy-MM-dd";
    public static final String YMDNOINTERVAL = "yyyyMMdd";
    public static final String YMDH = "yyyy-MM-dd HH";
    public static final String YMDHM = "yyyy-MM-dd HH:mm";
    public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String HMS = "HH:mm:ss";
    public static final String HHmmss = "HH时mm分ss秒";

    public static final long MILLIS_OF_DAY = 86400000L;

    private DateUtils() {
    }

    public static List<String> getBeforDays(int num, String formatStr) {
        if (formatStr == null) {
            formatStr = "yyyy-MM-dd";
        }

        List list = new ArrayList();
        Calendar today = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        list.add(format.format(today.getTime()));

        while (num < 0) {
            today.add(5, -1);
            list.add(format.format(today.getTime()));
            ++num;
        }

        return list;
    }

    public static String getDaysByNum(int num, String formatStr) {
        if (formatStr == null) {
            formatStr = "yyyy-MM-dd";
        }

        Calendar today = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        today.add(Calendar.DATE, num);
        return format.format(today.getTime());
    }

    public static String getDaysByNum(int num, String formatStr, Date date) {
        if (formatStr == null) {
            formatStr = "yyyy-MM-dd";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        return new SimpleDateFormat(formatStr).format(calendar.getTime());
    }

    /**
     * 获取输入日期间隔天数的日期
     */
    public static Date getDaysByNum(int num, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, num);
        return calendar.getTime();
    }

    public static String getAmountDay(Date date, int amount, String formatStr) {
        if (formatStr == null) {
            formatStr = "yyyy-MM-dd";
        }

        Calendar day = Calendar.getInstance();
        day.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        day.add(Calendar.DATE, amount);
        return format.format(day.getTime());
    }

    public static Date getDaysByNum(int num) {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, num);
        return today.getTime();
    }

    public static int daysBetween(Date now, Date returnDate) {
        Calendar cNow = Calendar.getInstance();
        Calendar cReturnDate = Calendar.getInstance();
        cNow.setTime(now);
        cReturnDate.setTime(returnDate);
        setTimeToMidnight(cNow);
        setTimeToMidnight(cReturnDate);
        long todayMs = cNow.getTimeInMillis();
        long returnMs = cReturnDate.getTimeInMillis();
        long intervalMs = todayMs - returnMs;
        return millisecondsToDays(intervalMs);
    }


    /**
     * 获取指定2天之间相隔天数
     */
    public static int daysBetween(String startDate, String endDate, String pattern) {
        Date end = parseStringToDate(endDate, pattern);
        Date start = parseStringToDate(startDate, pattern);
        long intervalMs = Math.abs(end.getTime() - start.getTime());
        return millisecondsToDays(intervalMs) + 1;
    }

    public static int millisecondsToDays(long intervalMs) {
        return (int) (intervalMs / 86400000L);
    }

    public static void setTimeToMidnight(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
    }

    public static int getIntervalDays(Date startDate, Date endDate) {
        long intervalTime = endDate.getTime() - startDate.getTime();
        intervalTime = Math.abs(intervalTime);
        int days = (int) (intervalTime / 86400000L);
        return days;
    }

    public static int getIntervalDays(String startDate, String endDate, String pattern) {
        Date date1 = parseStringToDate(startDate, pattern);
        Date date2 = parseStringToDate(endDate, pattern);
        long intervalTime = date2.getTime() - date1.getTime();
        intervalTime = Math.abs(intervalTime);
        int days = (int) (intervalTime / 86400000L);
        return days;
    }

    public static String formatDateToString(Date date, String pattern) {
        if (date == null) {
            return null;
        } else {
            pattern = pattern == null ? "yyyy-MM-dd HH:mm:ss" : pattern;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        }
    }

    public static String formatDateToString(Date date) {
        return formatDateToString(date, null);
    }

    public static String formatDateToStringNew(Date date, String pattern) {
        if (date == null) {
            return null;
        } else {
            pattern = pattern == null ? "yyyy-MM-dd" : pattern;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        }
    }

    public static String formatDateToStringNew(Date date) {
        return formatDateToStringNew(date, null);
    }

    public static Date parseStringToDate(String source, String pattern) {
        if (source == null) {
            return null;
        } else {
            pattern = pattern == null ? "yyyy-MM-dd HH:mm:ss" : pattern;

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                return sdf.parse(source);
            } catch (ParseException var3) {
                return null;
            }
        }
    }

    public static List<String> getDateStringList(Date startDate, Date endDate, String pattern) {
        return getDateStringList(startDate.getTime(), endDate.getTime(), pattern);
    }

    public static List<String> getDateStringList(long startTime, long endTime, String pattern) {
        if (endTime < startTime) {
            throw new RuntimeException("Start time can not large than end time!");
        } else {
            if (pattern == null) {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }

            List<String> list = new ArrayList();
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);

            for (long currentTime = endTime; currentTime >= startTime; currentTime -= 86400000L) {
                list.add(sdf.format(currentTime));
            }

            return list;
        }
    }

    public static Date getTodayMidnightTime() {
        Calendar ca = Calendar.getInstance();
        ca.set(11, 0);
        ca.set(12, 0);
        ca.set(13, 0);
        ca.setTimeInMillis(ca.getTimeInMillis() / 1000L * 1000L);
        Date date = ca.getTime();
        return date;
    }

    public static Date getMidnightTime(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(11, 0);
        ca.set(12, 0);
        ca.set(13, 0);
        ca.setTimeInMillis(ca.getTimeInMillis() / 1000L * 1000L);
        return ca.getTime();
    }

    public static boolean isNowHourBetween(int startHour, int endHour) {
        int nowHour = Calendar.getInstance().get(11);
        return nowHour >= startHour && nowHour < endHour;
    }

    public static boolean beforeToday(Date date) {
        return date.compareTo(getTodayMidnightTime()) < 0;
    }

    public static String getYesterday() {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DATE, -1);
        return formatDateToString(ca.getTime(), "yyyy-MM-dd");
    }

    public static Date getFriday(String dateStr) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(parseStringToDate(dateStr, "yyyy-MM-dd"));
        if (calendar.get(7) == 7) {
            calendar.set(11, 23);
            calendar.set(12, 59);
            calendar.set(13, 59);
            return calendar.getTime();
        } else {
            calendar.set(7, 6);
            calendar.set(11, 23);
            calendar.set(12, 59);
            calendar.set(13, 59);
            return calendar.getTime();
        }
    }

    public static Date getSaturday(String dateStr) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(parseStringToDate(dateStr, "yyyy-MM-dd"));
        if (calendar.get(7) == 7) {
            return calendar.getTime();
        } else {
            calendar.set(7, 1);
            calendar.add(7, -1);
            return calendar.getTime();
        }
    }

    public static Date getFirstDayOfMonth(String dateStr) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(parseStringToDate(dateStr, "yyyy-MM-dd"));
        calendar.set(5, 1);
        return calendar.getTime();
    }

    public static String getFirstDayOfMonth(Date date, String dateFormat) {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = "yyyy-MM-dd";
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        return getDaysByNum(0, dateFormat, calendar.getTime());
    }

    public static String getFirstDayOfMonth(String date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseStringToDate(date, pattern));
        calendar.set(Calendar.DATE, 1);
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    public static String getLastDayOfMonth(String date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseStringToDate(date, pattern));
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    public static Date getFirstDayOfMonthInDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getLastdayOfMonth(String dateStr) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(parseStringToDate(dateStr, "yyyy-MM-dd"));
        calendar.add(2, 1);
        calendar.set(5, 0);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        return calendar.getTime();
    }

    /**
     * 获取当前时区时间
     */
    public static Date getNowTime() {
        //设置为东八区
        TimeZone time = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(time);
        return calendar.getTime();
    }

    public static int getOutDateIndex(int incrementDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, incrementDay);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(calendar.getTime());

        return Integer.parseInt(dateString);
    }

    public static Date getLastSecondInDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 23);
        calendar.add(Calendar.MINUTE, 59);
        calendar.add(Calendar.SECOND, 59);

        return calendar.getTime();
    }

    public static List<String> findDates(Date dBegin, Date dEnd) {

        List<String> lDate = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        lDate.add(sd.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(sd.format(calBegin.getTime()));
        }
        return lDate;
    }

    public static String dateStringFormatDateString(String dateString, String currentFormat, String targetFormat) {

        Date date = null;
        try {
            date = new SimpleDateFormat(currentFormat).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newDateString = new SimpleDateFormat(targetFormat).format(date);
        return newDateString;
    }

    /**
     * 计算两个小时数之间的时间差，结果以小时为单位
     */
    public static float calculateHourInterval(String hour1, String hour2, boolean isDayAcross) {
        long time1 = parseStringHour(hour1);
        long time2 = parseStringHour(hour2);
        return ((float) (time2 - time1)) / 1000 / 60 / 60;
    }

    /**
     * 解析string类型的24小时数（例如 15:30），返回一个毫秒数
     */
    public static long parseStringHour(String hour) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            return simpleDateFormat.parse(hour).getTime();
        } catch (ParseException e) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), e.getMessage());
        }
    }

    public static Date getDayStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }

    public static Date getDayStartTime(String dateStr, String pattern) {
        Date date = parseStringToDate(dateStr, pattern);
        return getDayStartTime(date);
    }

    public static Date getDayEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        return calendar.getTime();
    }

    public static Date getDayEndTime(String dateStr, String pattern) {
        Date date = parseStringToDate(dateStr, pattern);
        return getDayEndTime(date);
    }

    /**
     * 获取上个月的当天
     * 若当月有31日，上月没有，返回值为当天日期减去上月天数
     */
    public static Date getSameDayInLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        Date lastDate = calendar.getTime();
        return lastDate;
    }

    /**
     * 获取上月第一天
     */
    public static Date getFirstDayOfLastMonth(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * String类型的date获取前面指定天数的日期
     * 默认格式 yyyyMMdd
     */
    public static String getIntervalDay(String date, int interval, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM-dd";
        }
        Date srcDate = parseStringToDate(date, pattern);
        return getDaysByNum(interval, pattern, srcDate);
    }

    /**
     * 获取指定日期当周第一天，格式yyyyMMdd
     */
    public static String getWeekStartDate(String pattern, String dateStr) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseStringToDate(dateStr, pattern));

        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        // 所在周开始日期
        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    /**
     * 获取指定日期当周最后一天，格式yyyyMMdd
     */
    public static String getWeekEndDate(String pattern, String dateStr) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parseStringToDate(dateStr, pattern));

        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        cal.add(Calendar.DAY_OF_WEEK, 6);
        // 所在周结束日期
        return new SimpleDateFormat(pattern).format(cal.getTime());
    }

    /**
     * 获取指定日期当月的最大天数
     */
    public static int getMaxDayNumOfMonth(String date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = YMDHMS;
        }
        Date targetDate = parseStringToDate(date, pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取日期是一周的第几天
     */
    public static int getDayInWeek(String date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = YMDHMS;
        }
        Date targetDate = parseStringToDate(date, pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        day = day == 0 ? 7 : day;
        return day;
    }

    /**
     * 获取日期是一月的第几天
     */
    public static int getDayInMonth(String date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = YMDHMS;
        }
        Date targetDate = parseStringToDate(date, pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public static String dateYMDToString(LocalDate localDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(YMD);
        return df.format(localDate);
    }

    public static String dateYYYMMDDToString(LocalDate localDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(YMDNOINTERVAL);
        return df.format(localDate);
    }

    public static String dateYYYMMDDHHToString(LocalDateTime localDateTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHH");
        return df.format(localDateTime);
    }

    public static String dateYYYMMDDHHSSToString(LocalDateTime localDateTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(YMDHMS);
        return df.format(localDateTime);
    }

    public static String dateMMDDHHMMToString(LocalDateTime localDateTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        return df.format(localDateTime);
    }

    public static String dateMMDDYYYYToString(LocalDate localDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return df.format(localDate);
    }

    public static String dateYYMMDDToString(LocalDate localDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyMMdd");
        return df.format(localDate);
    }

    public static String dateYYMMToString(LocalDate localDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyMM");
        return df.format(localDate);
    }

    public static String timeToString(LocalTime localTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(HHmmss);
        return df.format(localTime);
    }

    /**
     * 保持在最下面
     */
    public static void main(String[] args) throws Exception {
        System.out.println(timeToString(LocalTime.now()));
        System.out.println(ResultCode.FAIL_ILLEGAL_RESULT.getCode());
    }


}