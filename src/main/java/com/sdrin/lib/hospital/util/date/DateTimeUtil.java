/*
Copyright 2019-2020 上海石指健康科技有限公司

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.sdrin.lib.hospital.util.date;

import com.sdrin.lib.hospital.config.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 上海石指(健康)科技有限公司 sdrin.com 2020/2/6 12:31 下午
 *
 * @author 胡树铭
 */
public class DateTimeUtil {
    /**
     * 根据参数 formatter ，解析 string 形式的 localDate 为 LocalDate 变量，为什么需要重写，因为我这里集中处理了 Exception，
     * 这个方法无法 test，我已经在 main 里面测试过了，确实可以捕捉异常，并且不终端程序
     *
     * @param localDateStr LocalDate String ，需要被解析的对象
     * @param formatter    LocalDateString 的格式，如 yyyy-MM-dd
     * @return 如果能解析出，则返回LocalDate 对象，否则返回 null
     */
    public static LocalDate parseLocalDate(String localDateStr, DateTimeFormatter formatter) {
        if (localDateStr == null) return null;
        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(localDateStr, formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localDate;
    }

    /**
     * 使用默认的时间格式，返回LocalDate 对象
     *
     * @param localDateStr 被解析的时间string
     * @return 返回LocalDate 对象
     */
    public static LocalDate parseLocalDate(String localDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.LOCAL_DATE_FORMAT);
        return parseLocalDate(localDateStr, formatter);
    }

    /**
     * 根据参数 formatter ，解析 string 形式的 localDate 为 LocalDate 变量，为什么需要重写，因为我这里集中处理了 Exception，
     * 这个方法无法 test，我已经在 main 里面测试过了，确实可以捕捉异常，并且不终端程序
     *
     * @param localTimeStr LocalDate String ，需要被解析的对象
     * @param formatter    LocalDateString 的格式，如 hh:mm
     * @return 如果能解析出，则返回LocalDate 对象，否则返回 null
     */
    public static LocalTime parseLocalTime(String localTimeStr, DateTimeFormatter formatter) {
        if (localTimeStr == null) return null;
        LocalTime localTime = null;
        try {
            localTime = LocalTime.parse(localTimeStr, formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localTime;
    }

    /**
     * 使用默认的时间格式，解析小时，分钟
     *
     * @param localTimeStr 被解析的string
     * @return 返回localtime格式
     */
    public static LocalTime parseLocalTime(String localTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.LOCAL_TIME_FORMAT);
        return parseLocalTime(localTimeStr, formatter);
    }

    /**
     * 根据参数 formatter ，解析 string 形式的 localDate 为 LocalDate 变量，为什么需要重写，因为我这里集中处理了 Exception，
     * 这个方法无法 test，我已经在 main 里面测试过了，确实可以捕捉异常，并且不终端程序
     *
     * @param localDateTimeStr LocalDateTime String ，需要被解析的对象
     * @param formatter        LocalDateString 的格式，如 hh:mm
     * @return 如果能解析出，则返回LocalDate 对象，否则返回 null
     */
    public static LocalDateTime parseLocalDateTime(String localDateTimeStr, DateTimeFormatter formatter) {
        if (localDateTimeStr == null) return null;
        LocalDateTime localDateTime = null;
        try {
            localDateTime = LocalDateTime.parse(localDateTimeStr, formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localDateTime;
    }

    /**
     * 解析string 的时间，默认格式，为java time对象
     *
     * @param localDateTimeStr 默认格式的string时间。
     * @return 对象
     */
    public static LocalDateTime parseLocalDateTime(String localDateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.LOCAL_DATE_TIME_FORMAT);
        return parseLocalDateTime(localDateTimeStr, formatter);
    }

    /**
     * 将string格式的时间，解析为java date
     *
     * @param date   string 格式
     * @param format 格式
     * @return date对象。
     */
    public static Date parseDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将日期转为默认的格式
     *
     * @param localDateTime 被解析的时间
     * @return string
     */
    public static String toString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.LOCAL_DATE_TIME_FORMAT);
        return localDateTime.format(formatter);
    }

    /**
     * 将日期解析成自定义格式的string
     *
     * @param localDateTime 日期时间
     * @param formatter     格式
     * @return string
     */
    public static String toString(LocalDateTime localDateTime, DateTimeFormatter formatter) {
        return localDateTime.format(formatter);
    }

    /**
     * 将日期解析string
     *
     * @param localDate 被解析时间
     * @param format    格式
     * @return string
     */
    public static String toString(LocalDate localDate, DateTimeFormatter format) {
        return localDate.format(format);
    }

    /**
     * 将日期转为默认格式。
     *
     * @param localDate 被解析的时间
     * @return string
     */
    public static String toString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.LOCAL_DATE_FORMAT);
        return localDate.format(formatter);
    }

    /**
     * 将时间解析成默认的格式。
     *
     * @param localTime 被解析的时间
     * @return 返回string格式。
     */
    public static String toString(LocalTime localTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.LOCAL_TIME_FORMAT);
        return localTime.format(formatter);
    }

    /**
     * 将java8 time 解析成string
     *
     * @param localTime 被解析的时间
     * @param format    格式
     * @return string
     */
    public static String toString(LocalTime localTime, DateTimeFormatter format) {
        return localTime.format(format);
    }

    /**
     * 将java date，解析成string自定义的格式
     *
     * @param date   时间
     * @param format 格式
     * @return string
     */
    public static String toString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
