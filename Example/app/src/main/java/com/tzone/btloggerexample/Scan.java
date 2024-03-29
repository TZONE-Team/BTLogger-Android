package com.tzone.btloggerexample;

import android.util.Log;

import com.tzone.bluetooth.data.BleDevice;
import com.tzone.devices.BaseDevice;
import com.tzone.devices.DeviceRecordType;
import com.tzone.devices.DeviceType;
import com.tzone.devices.TemperatureUnitType;

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
     * <p>
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
    private int[] AlarmStatus;
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
        this.AlarmStatus = new int[]{-1, -1};
        this.DeviceStatus = new int[]{-1, -1};
        this.LastActiveTime = 0;
    }

    public boolean fromBroadcast(BleDevice bleDevice) {
        if (super.fromBroadcast(bleDevice)) {
            if (getDeviceType() == DeviceType.BT04) {
                com.tzone.devices.bt04.Device bt04 = new com.tzone.devices.bt04.Device();
                if (bt04.fromBroadcast(bleDevice)) {
                    this.ID = bt04.getID();
                    this.Name = bt04.getName();
                    this.Temperature = bt04.getTemperature();
                    this.Humidity = bt04.getHumidity();
                    this.Battery = bt04.getBattery();
                    this.RecordStatus = DeviceRecordType.Unknown;
                    this.AlarmStatus = bt04.getAlarmStatus();
                    this.Version = bt04.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT04B) {
                com.tzone.devices.bt04b.Device bt04b = new com.tzone.devices.bt04b.Device();
                if (bt04b.fromBroadcast(bleDevice)) {
                    this.ID = bt04b.getID();
                    this.Name = bt04b.getName();
                    this.Temperature = bt04b.getTemperature();
                    this.Humidity = bt04b.getHumidity();
                    this.Battery = bt04b.getBattery();
                    this.RecordStatus = DeviceRecordType.Unknown;
                    this.AlarmStatus = bt04b.getAlarmStatus();
                    this.Version = bt04b.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT05) {
                com.tzone.devices.bt05.Device bt05 = new com.tzone.devices.bt05.Device();
                if (bt05.fromBroadcast(bleDevice)) {
                    this.ID = bt05.getID();
                    this.Name = bt05.getName();
                    this.Temperature = bt05.getTemperature();
                    this.Battery = bt05.getBattery();
                    this.RecordStatus = DeviceRecordType.Unknown;
                    this.AlarmStatus = bt05.getAlarmStatus();
                    this.Version = bt05.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT05B) {
                com.tzone.devices.bt05b.Device bt05b = new com.tzone.devices.bt05b.Device();
                if (bt05b.fromBroadcast(bleDevice)) {
                    this.ID = bt05b.getID();
                    this.Name = bt05b.getName();
                    this.Temperature = bt05b.getTemperature();
                    this.Battery = bt05b.getBattery();
                    this.RecordStatus = DeviceRecordType.Unknown;
                    this.AlarmStatus = bt05b.getAlarmStatus();
                    this.Version = bt05b.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.TempU06L60) {
                com.tzone.devices.u06L60.Device u06 = new com.tzone.devices.u06L60.Device();
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
            } else if (getDeviceType() == DeviceType.TempU06L80) {
                com.tzone.devices.u06L80.Device u06 = new com.tzone.devices.u06L80.Device();
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
            } else if (getDeviceType() == DeviceType.TempU06L100) {
                com.tzone.devices.u06L100.Device u06 = new com.tzone.devices.u06L100.Device();
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
            } else if (getDeviceType() == DeviceType.TempU06L200) {
                com.tzone.devices.u06L200.Device u06 = new com.tzone.devices.u06L200.Device();
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
            } else if (getDeviceType() == DeviceType.BT06) {
                com.tzone.devices.bt06.Device bt06 = new com.tzone.devices.bt06.Device();
                if (bt06.fromBroadcast(bleDevice)) {
                    this.ID = bt06.getID();
                    this.Name = bt06.getName();
                    this.Voltage = bt06.getVoltage();
                    this.TemperatureUnitTypeID = bt06.getTemperatureUnitTypeID();
                    this.Temperature = bt06.getTemperature();
                    this.Humidity = bt06.getHumidity();
                    this.Locklevel = bt06.getLocklevel();
                    this.RecordStatus = bt06.getRecordStatus();
                    this.AlarmStatus = bt06.getAlarmStatus();
                    this.DeviceStatus = new int[]{0, bt06.getDataFull() ? 1 : 0};
                    this.Version = bt06.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT06) {
                com.tzone.devices.bt06.Device bt06 = new com.tzone.devices.bt06.Device();
                if (bt06.fromBroadcast(bleDevice)) {
                    this.ID = bt06.getID();
                    this.Name = bt06.getName();
                    this.Voltage = bt06.getVoltage();
                    this.TemperatureUnitTypeID = bt06.getTemperatureUnitTypeID();
                    this.Temperature = bt06.getTemperature();
                    this.Humidity = bt06.getHumidity();
                    this.Locklevel = bt06.getLocklevel();
                    this.RecordStatus = bt06.getRecordStatus();
                    this.DeviceStatus = new int[]{0, bt06.getDataFull() ? 1 : 0};
                    this.Version = bt06.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT03) {
                com.tzone.devices.bt03.Device bt03 = new com.tzone.devices.bt03.Device();
                if (bt03.fromBroadcast(bleDevice)) {
                    this.ID = bt03.getID();
                    this.Name = bt03.getName();
                    this.Voltage = bt03.getVoltage();
                    this.TemperatureUnitTypeID = bt03.getTemperatureUnitTypeID();
                    this.Temperature = bt03.getTemperature();
                    this.Humidity = -1000;
                    this.Locklevel = bt03.getLocklevel();
                    this.RecordStatus = bt03.getRecordStatus();
                    this.AlarmStatus = new int[]{bt03.getAlarmStatus(), -1};
                    this.DeviceStatus = new int[]{0, bt03.getDataFull() ? 1 : 0};
                    this.Version = bt03.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            }  else if (getDeviceType() == DeviceType.TempU02B) {
                com.tzone.devices.u02b.Device u02b = new com.tzone.devices.u02b.Device();
                if(u02b.fromBroadcast(bleDevice)){
                    this.ID = u02b.getID();
                    this.Voltage = u02b.getVoltage();
                    this.Version = u02b.getVersion();
                    this.LastActiveTime = new Date().getTime();
                    return true;
                }
            } else if (getDeviceType() == DeviceType.BT07) {
                com.tzone.devices.bt07.Device bt07 = new com.tzone.devices.bt07.Device();
                if (bt07.fromBroadcast(bleDevice)) {
                    this.ID = bt07.getID();
                    this.Name = bt07.getName();
                    this.Voltage = bt07.getVoltage();
                    this.TemperatureUnitTypeID = bt07.getTemperatureUnitTypeID();
                    this.Temperature = bt07.getTemperature();
                    this.Humidity = -1000;
                    this.Locklevel = bt07.getLocklevel();
                    this.RecordStatus = bt07.getRecordStatus();
                    this.AlarmStatus = new int[]{bt07.getAlarmStatus(), -1};
                    this.DeviceStatus = new int[]{0, bt07.getDataFull() ? 1 : 0};
                    this.Version = bt07.getVersion();
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

    public int getBatteryBar() {
        if (this.getDeviceType() == DeviceType.TempU06L60
                || this.getDeviceType() == DeviceType.TempU06L80
                || this.getDeviceType() == DeviceType.TempU06L100
                || this.getDeviceType() == DeviceType.TempU06L200) {
            if (this.getVoltage() != -1000) {
                if (this.getVoltage() > 3.55)
                    return 4;
                else if (this.getVoltage() > 3.4)
                    return 3;
                else if (this.getVoltage() > 3.2)
                    return 2;
                else
                    return 1;
            }
        } else if (this.getDeviceType() == DeviceType.BT06) {
            if (this.getVoltage() != -1000) {
                if (this.getVoltage() >= 2.8)
                    return 4;
                else if (this.getVoltage() >= 2.7)
                    return 3;
                else if (this.getVoltage() >= 2.55)
                    return 2;
                else
                    return 1;
            }
        } else {
            if (this.getBattery() != -1000) {
                if (this.getBattery() > 85)
                    return 4;
                else if (this.getBattery() > 65)
                    return 3;
                else if (this.getBattery() > 35)
                    return 2;
                else
                    return 1;
            }
        }
        return 4;
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

    public int[] getAlarmStatus() {
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