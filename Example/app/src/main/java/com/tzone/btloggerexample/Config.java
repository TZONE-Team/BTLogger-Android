package com.tzone.btloggerexample;

import com.tzone.devices.AlarmSetting;
import com.tzone.devices.TemperatureUnitType;

import java.util.List;

public class Config {
    /**
     * Temperature Unit
     */
    public TemperatureUnitType TemperatureUnitTypeID;
    /**
     * Logging Interval
     */
    public long LoggingInterval;
    /**
     * Start Delay
     */
    public long StartDelay;
    /**
     * Start Mode
     */
    public int StartMode;
    /**
     * Start Time
     */
    public long StartTime;
    /**
     * Stop Button
     */
    public boolean StopButton;
    /**
     * Repeat Start
     */
    public boolean RepeatStart;
    /**
     * FullCoverage
     */
    public boolean FullCoverage;
    /**
     * Locklevel
     */
    public int Locklevel;
    /**
     * Password
     */
    public String Password;
    /**
     * Alarm list
     */
    public List<AlarmSetting> Alarmlist = null;
    /**
     * TimeZone
     */
    public int TimeZone;
    /**
     * DST
     */
    public boolean DST;
    /**
     * Data Format
     */
    public int DataFormat;
    /**
     * ShowTabularData
     */
    public boolean ShowTabularData;
    /**
     * Language
     */
    public String Language;

    /**
     * Description
     */
    public String Description;
}
