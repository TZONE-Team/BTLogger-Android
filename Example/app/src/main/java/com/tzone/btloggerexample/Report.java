package com.tzone.btloggerexample;

import android.util.Log;

import com.tzone.bt.AlarmSetting;
import com.tzone.bt.BaseDevice;
import com.tzone.bt.DeviceRecordType;
import com.tzone.bt.DeviceType;
import com.tzone.bt.LoggingData;
import com.tzone.bt.TemperatureUnitType;
import com.tzone.utils.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Report {
    public final String TAG = "Report";
    /**
     * ID
     */
    public int ID;
    /**
     * Device ID
     */
    public String DeviceID;
    /**
     * Serial ID
     */
    public String SerialID;
    /**
     * DeviceType
     */
    public DeviceType DeviceTypeID;
    /**
     * Device Name
     */
    public String DeviceName;
    /**
     * Version
     */
    public String Version;
    /**
     * Mac
     */
    public String Mac;
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
     * 0：Button Start
     * 1：Timing Start
     */
    public int StartMode;
    /**
     * Stop mode
     * 0: initialization state
     * 1: Press to stop
     * 2: Usb stop
     * 3: Storage full stop
     * 4: Bluetooth stop
     * 5: Recording
     * 6: Delay
     */
    public DeviceRecordType RecordStatus;
    /**
     * Description
     */
    public String Description;
    /**
     * BeginTime
     */
    public long BeginTime;
    /**
     * EndTime
     */
    public long EndTime;
    /**
     * Total Count
     */
    public int TotalCount;
    /**
     * Total Time
     */
    public long TotalTime;

    /**
     * Temperature Max
     */
    public double T_Max;
    /**
     * Temperature Min
     */
    public double T_Min;
    /**
     * Temperature Average
     */
    public double T_Average;
    /**
     * MKT
     */
    public double MKT;
    /**
     * Humidity Max
     */
    public double H_Max;
    /**
     * Humidity Min
     */
    public double H_Min;
    /**
     * Humidity Average
     */
    public double H_Average;

    /**
     * Status
     * 0 OK
     * 1 Alarm
     */
    public int Status;
    /**
     * Create Time
     */
    public long CreateTime;

    /**
     * Alarm List
     */
    public List<AlarmSetting> AlarmList;

    /**
     * Data List
     */
    public List<LoggingData> DataList;


    public Report(){
        DeviceID = null;
        SerialID = null;
        DeviceTypeID = DeviceType.Unknown;
        DeviceName = null;
        Version = null;
        Mac = null;
        TemperatureUnitTypeID = TemperatureUnitType.C;
        LoggingInterval = 0;
        StartDelay = 0;
        StartMode = 0;
        RecordStatus = DeviceRecordType.Initialize;
        Description = null;

        BeginTime = 0;
        EndTime = 0 ;
        TotalCount = 0;
        TotalTime = 0;
        T_Max = -1000;
        T_Min = -1000;
        T_Average = -1000;
        MKT = -1000;
        H_Max = -1000;
        H_Min = -1000;
        H_Average = -1000;
        Status = 0;
        CreateTime = new Date().getTime();

        DataList = new ArrayList<>();
        AlarmList = new ArrayList<>();
    }

    public Report(BaseDevice device, Config deviceConfig){
        this();

        DeviceID = device.getID();
        DeviceTypeID = device.getDeviceType();
        DeviceName = device.getName();
        Version = device.getVersion();
        Mac = device.getMac();

        TemperatureUnitTypeID = deviceConfig.TemperatureUnitTypeID;
        LoggingInterval = deviceConfig.LoggingInterval;
        StartDelay = deviceConfig.StartDelay;
        StartMode = deviceConfig.StartMode;
        Description = deviceConfig.Description;
    }

    public void Generate(){
        try {
            if(DataList == null || DataList.size() == 0)
                return;

            this.BeginTime = DataList.get(0).getCreateTime();
            this.EndTime = DataList.get(DataList.size() - 1).getCreateTime();
            this.TotalCount = DataList.size();
            this.TotalTime = (this.EndTime - this.BeginTime) > 0 ? (this.EndTime - this.BeginTime) / 1000 : 0;

            double totalTemperature = 0;
            int totalTemperature_count = 0;
            double totalHumidity = 0;
            int totalHumidity_count = 0;

            for (int i = 0; i < DataList.size(); i++) {
                try {
                    LoggingData d = DataList.get(i);

                    if(d.getTemperature() != -1000) {
                        totalTemperature += d.getTemperature();
                        totalTemperature_count++;
                        if (this.T_Max == -1000)
                            this.T_Max = d.getTemperature();
                        if (this.T_Min == -1000)
                            this.T_Min = d.getTemperature();

                        if (d.getTemperature() > this.T_Max)
                            this.T_Max = d.getTemperature();
                        if (d.getTemperature() < this.T_Min)
                            this.T_Min = d.getTemperature();
                    }

                    if (d.getHumidity() != -1000) {
                        totalHumidity += d.getHumidity();
                        totalHumidity_count++;
                        if (this.H_Max == -1000)
                            this.H_Max = d.getHumidity();
                        if (this.H_Min == -1000)
                            this.H_Min = d.getHumidity();

                        if (d.getHumidity() > this.H_Max)
                            this.H_Max = d.getHumidity();
                        if (d.getHumidity() < this.H_Min)
                            this.H_Min = d.getHumidity();
                    }
                }catch (Exception ex){
                    Log.e(TAG, "GetInfo: " + ex.toString());
                }
            }

            if (totalTemperature_count > 0) {
                this.T_Average = Double.parseDouble(StringUtil.ToString(totalTemperature / totalTemperature_count, 1));
                if(this.MKT == -1000)
                    this.MKT = CalcMKT();
            }
            if (totalHumidity_count > 0)
                this.H_Average = Double.parseDouble(StringUtil.ToString(totalHumidity / totalHumidity_count, 1));

            if(this.DeviceTypeID == DeviceType.TempU06){
                if(this.AlarmList != null && this.AlarmList.size() > 0){
                    AlarmSetting alarmSetting = this.AlarmList.get(0);
                    if(alarmSetting.getH_Enable() && alarmSetting.getH() != -1000){
                        List<LoggingData> keepList = new ArrayList<>();
                        int total = 0;
                        int time = 0;
                        for (int i = 0; i < this.DataList.size(); i++) {
                            LoggingData item = this.DataList.get(i);
                            if (item.getTemperature() != -1000){
                                if (item.getTemperature() > alarmSetting.getH()){
                                    total++;
                                    if (keepList.size() == 0)
                                        time++;
                                    keepList.add(item);
                                    if (alarmSetting.getH_AlarmType() == 0)
                                    {
                                        if (keepList.size()  * this.LoggingInterval > alarmSetting.getH_DelayTime())
                                        {
                                            this.Status = 1;
                                            if (item.getDataTypeID() == LoggingData.DataType.Mark || item.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                                item.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                            else
                                                item.setDataTypeID(LoggingData.DataType.Alarm_H);
                                        }
                                    }
                                    else
                                    {
                                        if (total * this.LoggingInterval > alarmSetting.getH_DelayTime())
                                        {
                                            this.Status = 1;
                                            if (item.getDataTypeID() == LoggingData.DataType.Mark || item.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                                item.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                            else
                                                item.setDataTypeID(LoggingData.DataType.Alarm_H);
                                        }
                                    }
                                }else
                                    keepList.clear();
                            }
                        }

                    }
                    if(alarmSetting.getL_Enable() && alarmSetting.getL() != -1000){
                        List<LoggingData> keepList = new ArrayList<>();
                        int total = 0;
                        int time = 0;
                        for (int i = 0; i < this.DataList.size(); i++) {
                            LoggingData item = this.DataList.get(i);
                            if (item.getTemperature() != -1000){
                                if (item.getTemperature() < alarmSetting.getL()){
                                    total++;
                                    if (keepList.size() == 0)
                                        time++;
                                    keepList.add(item);
                                    if (alarmSetting.getL_AlarmType() == 0)
                                    {
                                        if (keepList.size() * this.LoggingInterval > alarmSetting.getL_DelayTime())
                                        {
                                            this.Status = 1;
                                            if (item.getDataTypeID() == LoggingData.DataType.Mark || item.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                                item.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                            else
                                                item.setDataTypeID(LoggingData.DataType.Alarm_L);
                                        }
                                    }
                                    else
                                    {
                                        if (total * this.LoggingInterval > alarmSetting.getL_DelayTime())
                                        {
                                            this.Status = 1;
                                            if (item.getDataTypeID() == LoggingData.DataType.Mark || item.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                                item.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                            else
                                                item.setDataTypeID(LoggingData.DataType.Alarm_L);
                                        }
                                    }
                                }else
                                    keepList.clear();
                            }
                        }
                    }
                }
                if(this.AlarmList != null && this.AlarmList.size() > 1){
                    AlarmSetting alarmSetting = this.AlarmList.get(1);
                    if(alarmSetting.getH_Enable() && alarmSetting.getH() != -1000){
                        List<LoggingData> keepList = new ArrayList<>();
                        int total = 0;
                        int time = 0;
                        for (int i = 0; i < this.DataList.size(); i++) {
                            LoggingData item = this.DataList.get(i);
                            if (item.getTemperature() != -1000){
                                if (item.getTemperature() > alarmSetting.getH()){
                                    total++;
                                    if (keepList.size() == 0)
                                        time++;
                                    keepList.add(item);
                                    if (alarmSetting.getH_AlarmType() == 0)
                                    {
                                        if (keepList.size() * this.LoggingInterval > alarmSetting.getH_DelayTime())
                                        {
                                            this.Status = 1;
                                            if (item.getDataTypeID() == LoggingData.DataType.Mark || item.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                                item.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                            else
                                                item.setDataTypeID(LoggingData.DataType.Alarm_H);
                                        }
                                    }
                                    else
                                    {
                                        if (total * this.LoggingInterval > alarmSetting.getH_DelayTime())
                                        {
                                            this.Status = 1;
                                            if (item.getDataTypeID() == LoggingData.DataType.Mark || item.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                                item.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                            else
                                                item.setDataTypeID(LoggingData.DataType.Alarm_H);
                                        }
                                    }
                                }else
                                    keepList.clear();
                            }
                        }
                    }
                    if(alarmSetting.getL_Enable() && alarmSetting.getL() != -1000){
                        List<LoggingData> keepList = new ArrayList<>();
                        int total = 0;
                        int time = 0;
                        boolean flag = false;
                        for (int i = 0; i < this.DataList.size(); i++) {
                            LoggingData item = this.DataList.get(i);
                            if (item.getTemperature() != -1000){
                                if (item.getTemperature() < alarmSetting.getL()){
                                    total++;
                                    if (keepList.size() == 0)
                                        time++;
                                    keepList.add(item);
                                    if (!flag){
                                        if (alarmSetting.getL_AlarmType() == 0)
                                        {
                                            if (keepList.size() * this.LoggingInterval > alarmSetting.getL_DelayTime())
                                            {
                                                this.Status = 1;
                                                if (item.getDataTypeID() == LoggingData.DataType.Mark || item.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                                    item.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                                else
                                                    item.setDataTypeID(LoggingData.DataType.Alarm_L);
                                            }
                                        }
                                        else
                                        {
                                            if (total * this.LoggingInterval > alarmSetting.getL_DelayTime())
                                            {
                                                this.Status = 1;
                                                if (item.getDataTypeID() == LoggingData.DataType.Mark || item.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                                    item.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                                else
                                                    item.setDataTypeID(LoggingData.DataType.Alarm_L);
                                            }
                                        }
                                    }
                                }else
                                    keepList.clear();
                            }
                        }
                    }
                }
            }else{
                for (int i = 0; i < this.DataList.size(); i++) {
                    LoggingData data = this.DataList.get(i);
                    if(this.AlarmList != null && this.AlarmList.size() > 0){
                        for (int j = 0; j < this.AlarmList.size(); j++) {
                            AlarmSetting alarm = this.AlarmList.get(j);
                            if (data.getTemperature() != -1000) {
                                boolean flag = false;
                                if (alarm.L_Enable) {
                                    if (data.getTemperature() <= alarm.L) {
                                        flag = true;
                                        if(data.getDataTypeID() == LoggingData.DataType.Mark || data.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                            data.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                        else
                                            data.setDataTypeID(LoggingData.DataType.Alarm_L);
                                    }
                                }
                                if (alarm.H_Enable) {
                                    if (data.getTemperature() >= alarm.H) {
                                        flag = true;
                                        if(data.getDataTypeID() == LoggingData.DataType.Mark || data.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                                            data.setDataTypeID(LoggingData.DataType.MarkAlarm);
                                        else
                                            data.setDataTypeID(LoggingData.DataType.Alarm_H);
                                    }
                                }
                                if(flag){
                                    this.Status = 1;
                                }
                            }
                        }
                    }
                }
            }

        }catch (Exception ex){
            Log.e(TAG, "Generate: Exception => " + ex.toString());
        }
    }

    public double CalcMKT() {
        double result = -1000;
        if (this.DataList == null || this.DataList.size() == 0)
            return result;
        try {
            List<LoggingData> l = new ArrayList<>();
            for (int i = 0; i < this.DataList.size(); i++) {
                LoggingData item = this.DataList.get(i);
                if (item.getTemperature() != -1000)
                    l.add(item);
            }
            double mkt_sum = 0;
            double mkt_temp;
            for (int i = 0; i < l.size(); i++) {
                mkt_temp = l.get(i).getTemperature();
                mkt_temp += 2731;
                mkt_temp = -(100000.0 / mkt_temp);
                mkt_temp = Math.exp(mkt_temp);
                mkt_sum += mkt_temp;
            }
            mkt_temp = mkt_sum / (double) (l.size());
            mkt_temp = Math.log(mkt_temp);
            mkt_temp = (-10000.0) / mkt_temp;
            mkt_temp = mkt_temp * 10;
            mkt_temp -= 2731;

            result = Double.parseDouble(StringUtil.ToString(mkt_temp, 1));
        } catch (Exception ex) {
        }
        return result;
    }

    public List<LoggingData> GetMarks(){
        List<LoggingData> marks = new ArrayList<>();
        for (LoggingData m:DataList) {
            if(m.getDataTypeID() == LoggingData.DataType.Mark || m.getDataTypeID() == LoggingData.DataType.MarkAlarm)
                marks.add(m);
        }
        return marks;
    }

}
