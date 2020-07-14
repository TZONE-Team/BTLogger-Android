package com.tzone.btloggerexample;

import android.util.Log;

import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bt.BaseDevice;
import com.tzone.bt.DeviceRecordType;
import com.tzone.bt.DeviceType;
import com.tzone.bt.TemperatureUnitType;

import java.util.Date;

public class Scan extends BaseDevice {

    /**
     * battery voltage
     */
    private double Voltage;
    /**
     * battery power
     */
    private int Battery;
    /**
     * 0 = Celsius
     * 1 = Fahrenheit
     */
    private TemperatureUnitType TemperatureUnitTypeID;
    /**
     * Temperature
     */
    private double Temperature;
    /**
     * Humidity
     */
    private double Humidity;
    /**
     * Lock level
     *
     * 0 = Unlocked
     * 1 = Low level lock
     * 2 = High level lock
     */
    private int Locklevel;
    /**
     * RecordStatus
     */
    private DeviceRecordType RecordStatus;
    /**
     * AlarmStatus
     * 0 = No temperature alarm
     * 1 = Upper temperature limit alarm
     * 2 = Low temperature alarm
     * 3 = Upper and lower temperature alarm
     */
    private int AlarmStatus;
    /**
     * Device status
     * i = 0,Whether the USB is inserted
     * i = 1,Whether the data is full
     */
    private int[] DeviceStatus;

    private long LastActiveTime;

    public Scan() {
        this.Voltage = -1000;
        this.Battery = -1000;
        this.TemperatureUnitTypeID = TemperatureUnitType.C;
        this.Temperature = -1000;
        this.Humidity = -1000;
        this.Locklevel = -1;
        this.RecordStatus = DeviceRecordType.Initialize;
        this.AlarmStatus = -1;
        this.DeviceStatus = new int[]{-1, -1};
        this.LastActiveTime = 0;
    }

    public boolean fromBroadcast(BleDevice bleDevice) {
        if (super.fromBroadcast(bleDevice)) {
            /*if (getDeviceType() == DeviceType.BT04) {
                com.tzone.bt.bt04.Device bt04 = new com.tzone.bt.bt04.Device();
                if (bt04.fromBroadcast(bleDevice)) {
                    this.ID = bt04.getID();
                    this.Name = bt04.getName();
                    this.Temperature = bt04.getTemperature();
                    this.Humidity = bt04.getHumidity();
                    this.Battery = bt04.getBattery();
                    this.RecordStatus = DeviceRecordType.Recording;
                    this.Version = bt04.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT04B) {
                com.tzone.bt.bt04b.Device bt04b = new com.tzone.bt.bt04b.Device();
                if (bt04b.fromBroadcast(bleDevice)) {
                    this.ID = bt04b.getID();
                    this.Name = bt04b.getName();
                    this.Temperature = bt04b.getTemperature();
                    this.Humidity = bt04b.getHumidity();
                    this.Battery = bt04b.getBattery();
                    this.RecordStatus = DeviceRecordType.Recording;
                    this.Version = bt04b.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT05) {
                com.tzone.bt.bt05.Device bt05 = new com.tzone.bt.bt05.Device();
                if (bt05.fromBroadcast(bleDevice)) {
                    this.ID = bt05.getID();
                    this.Name = bt05.getName();
                    this.Temperature = bt05.getTemperature();
                    this.Battery = bt05.getBattery();
                    this.RecordStatus = DeviceRecordType.Recording;
                    this.Version = bt05.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT05B) {
                com.tzone.bt.bt05b.Device bt05b = new com.tzone.bt.bt05b.Device();
                if (bt05b.fromBroadcast(bleDevice)) {
                    this.ID = bt05b.getID();
                    this.Name = bt05b.getName();
                    this.Temperature = bt05b.getTemperature();
                    this.Battery = bt05b.getBattery();
                    this.RecordStatus = DeviceRecordType.Recording;
                    this.Version = bt05b.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else */
            if (getDeviceType() == DeviceType.TempU06) {
                com.tzone.bt.u06.Device u06 = new com.tzone.bt.u06.Device();
                if (u06.fromBroadcast(bleDevice)) {
                    this.ID = u06.getID();
                    this.Name = u06.getName();
                    this.Voltage = u06.getVoltage();
                    this.TemperatureUnitTypeID = u06.getTemperatureUnitTypeID();
                    this.Temperature = u06.getTemperature();
                    this.Humidity = u06.getHumidity();
                    this.Locklevel = u06.getLocklevel();
                    this.RecordStatus = u06.getRecordStatus();
                    this.AlarmStatus = u06.getAlarmStatus();
                    this.DeviceStatus = new int[]{u06.getUSBPlugIn() ? 1 : 0, u06.getDataFull() ? 1 : 0};
                    this.Version = u06.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            }

        }
        return false;
    }

    public void BroadcastUpdate(Scan device) {
        this.Rssi = device.getRssi();
        this.Name = device.getName();
        this.ID = device.getID();
        this.Voltage = device.getVoltage();
        this.Battery = device.getBattery();
        this.TemperatureUnitTypeID = device.getTemperatureUnitTypeID();
        this.Temperature = device.getTemperature();
        this.Humidity = device.getHumidity();
        this.Locklevel = device.getLocklevel();
        this.RecordStatus = device.getRecordStatus();
        this.AlarmStatus = device.getAlarmStatus();
        this.DeviceStatus = device.getDeviceStatus();
        this.Version = device.getVersion();
        this.LastActiveTime = new Date().getTime();
    }

    public double getVoltage() {
        return Voltage;
    }
    public int getBattery() {
        return Battery;
    }

    public TemperatureUnitType getTemperatureUnitTypeID() {
        return TemperatureUnitTypeID;
    }

    public double getTemperature() {
        return Temperature;
    }

    public double getHumidity() {
        if (this.Humidity < 0 || this.Humidity > 100)
            return -1000;
        return Humidity;
    }

    public int getLocklevel() {
        return Locklevel;
    }

    public DeviceRecordType getRecordStatus() {
        return RecordStatus;
    }

    public int getAlarmStatus() {
        return AlarmStatus;
    }

    public int[] getDeviceStatus() {
        return DeviceStatus;
    }

    public long getLastActiveTime() {
        return LastActiveTime;
    }

    public void setLastActiveTime(long lastActiveTime) {
        LastActiveTime = lastActiveTime;
    }

}