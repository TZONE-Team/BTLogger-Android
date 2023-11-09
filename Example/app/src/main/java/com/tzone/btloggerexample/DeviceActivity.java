package com.tzone.btloggerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.callback.BleGattCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.exception.BleException;
import com.tzone.devices.AlarmSetting;
import com.tzone.devices.BaseDevice;
import com.tzone.devices.DataManagerBase;
import com.tzone.devices.DeviceRecordType;
import com.tzone.devices.DeviceType;
import com.tzone.devices.IDataCallback;
import com.tzone.devices.LoggingData;
import com.tzone.devices.TemperatureUnitType;
import com.tzone.utils.StringUtil;

import java.util.Date;
import java.util.List;

import javax.crypto.Mac;

public class DeviceActivity extends AppCompatActivity {
    private final String TAG = "DeviceActivity";

    public TextView txtPrintLog;

    public BaseDevice _Device = null;
    public BleDevice _BleDevice = null;
    public DataManagerBase _DataManager = null;
    public Report _Report = null;

    public String Password = "000000"; //Set Password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        txtPrintLog = (TextView)findViewById(R.id.txtPrintLog);

        try {
            String json = this.getIntent().getExtras().getString("d");
            ShowLog("Json:" + json);
            _Device = JSON.parseObject(json, BaseDevice.class);
            if(_Device == null)
                return;

            ShowLog("1、Connecting...");
            BleManager.getInstance().connect(_Device.getMac(), bleGattCallback);
        } catch (Exception e) {}

    }

    public String StrLog = "";
    public void ShowLog(String log){
        StrLog += log + "\r\n";
        txtPrintLog.setText(StrLog);
    }

    public void InitDataManager() {
        if (_BleDevice == null)
            return;
        try {
            if (_DataManager == null) {
                if (_Device.getDeviceType() == DeviceType.TempU06L60)
                    _DataManager = new com.tzone.devices.u06L60.DataManager();
                else if (_Device.getDeviceType() == DeviceType.TempU06L100)
                    _DataManager = new com.tzone.devices.u06L100.DataManager();
                else if (_Device.getDeviceType() == DeviceType.TempU06L200)
                    _DataManager = new com.tzone.devices.u06L200.DataManager();
                else if (_Device.getDeviceType() == DeviceType.BT04)
                    _DataManager = new com.tzone.devices.bt04.DataManager();
                else if (_Device.getDeviceType() == DeviceType.BT04B)
                    _DataManager = new com.tzone.devices.bt04b.DataManager();
                else if (_Device.getDeviceType() == DeviceType.BT05)
                    _DataManager = new com.tzone.devices.bt05.DataManager();
                else if (_Device.getDeviceType() == DeviceType.BT05B)
                    _DataManager = new com.tzone.devices.bt05b.DataManager();
                else if (_Device.getDeviceType() == DeviceType.BT06)
                    _DataManager = new com.tzone.devices.bt06.DataManager();
                else if (_Device.getDeviceType() == DeviceType.BT03)
                    _DataManager = new com.tzone.devices.bt03.DataManager();
                else if (_Device.getDeviceType() == DeviceType.BT07)
                    _DataManager = new com.tzone.devices.bt07.DataManager();
                else {
                    return;
                }
                _DataManager.Initialize(_BleDevice, dataCallback);
            }

            _Report = null;

            ShowLog("2、InitDataManager");
            if (_Device.getDeviceType() == DeviceType.TempU06L60
                    || _Device.getDeviceType() == DeviceType.TempU06L80
                    || _Device.getDeviceType() == DeviceType.TempU06L100
                    || _Device.getDeviceType() == DeviceType.TempU06L200
                    || _Device.getDeviceType() == DeviceType.BT06
                    || _Device.getDeviceType() == DeviceType.BT03
                    || _Device.getDeviceType() == DeviceType.BT07) {
                ShowLog("3、Notify");
                _DataManager.Notify();
            }else {
                ShowLog("4、Unlock");
                Unlock();
            }
        } catch (Exception ex) {
        }
    }

    public void Unlock() {
        if (_DataManager == null)
            return;
        try {
            _DataManager.Unlock(Password);
        } catch (Exception ex) {
        }
    }

    public BleGattCallback bleGattCallback = new BleGattCallback() {
        @Override
        public void onStartConnect() {
            Log.i(TAG, "onStartConnect: OK");
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, BleException exception) {
            Log.i(TAG, "onConnectFail: bleDevice => " + (bleDevice != null ? bleDevice.getMac() : " null"));
            ShowLog("onConnectFail");
            BleManager.getInstance().destroy();
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
            Log.i(TAG, "onConnectSuccess: bleDevice => " + (bleDevice != null ? bleDevice.getMac() : " null"));
            _BleDevice = bleDevice;
            ShowLog("onConnectSuccess");
            InitDataManager();
        }

        @Override
        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
            Log.i(TAG, "onDisConnected: bleDevice => " + (device != null ? device.getMac() : " null"));
            _BleDevice = null;
            ShowLog("onDisConnected");
        }
    };

    private String[] RequestDataInfo;
    public List<AlarmSetting> AlarmList = null;
    public List<Integer> MarkList = null;

    public IDataCallback dataCallback = new IDataCallback() {
        @Override
        public void onNotify(boolean status) {
            Log.i(TAG, "onNotify: " + status);
            ShowLog("onNotify: " + status);
            if (status) {

                ShowLog("4、Unlock");
                Unlock();
            }
        }

        @Override
        public void onUnlock(boolean status, boolean isOk) {
            Log.i(TAG, "onUnlock: " + status);
            ShowLog("onUnlock: " + status);
            if (status) {
                if (isOk) {
                    ShowLog("5、GetLogStatus");
                    _DataManager.GetLogStatus();
                } else {
                    //Password is error
                    Password = "";
                }
            }
        }

        @Override
        public void onGetLogStatus(boolean status, DeviceRecordType recordStatus) {
            Log.i(TAG, "onGetLogStatus: " + status);
            ShowLog("onGetLogStatus: " + status);
            if(status){
                ShowLog("6、SetConfig");
                _DataManager.SetConfig(0, 0);
            }
        }

        @Override
        public void onSetConfig(boolean status) {
            Log.i(TAG, "onSetConfig: " + status);
            ShowLog("onSetConfig: " + status);
            if (status) {
                ShowLog("7、GetAlarm");
                _DataManager.GetAlarm();
            }
        }

        @Override
        public void onGetAlarm(boolean status, List<AlarmSetting> tempAlarmList, List<AlarmSetting> rhAlarmList) {
            Log.i(TAG, "onGetAlarm: " + status);
            ShowLog("onGetAlarm: " + status);
            if(status){
                ShowLog("8、GetMark");
                _DataManager.GetMark();
            }
        }

        @Override
        public void onGetMark(boolean status, List<Integer> markList) {
            Log.i(TAG, "onGetMark: " + status);
            ShowLog("onGetMark: " + status);
            if(status){
                ShowLog("9、RequestDataInfo");
                _DataManager.RequestDataInfo();
            }
        }

        @Override
        public void onRequestDataInfo(boolean status, String[] info) {
            Log.i(TAG, "onRequestDataInfo: " + status);
            ShowLog("onRequestDataInfo: " + status);
            if (status) {
                int dataCount = 0;
                if (info != null) {
                    dataCount = Integer.parseInt(info[0]);
                    RequestDataInfo = info;
                }

                if (dataCount > 0) {
                    ShowLog("10、Receive(true)");
                    _DataManager.Receive(true);
                }
            }
        }

        @Override
        public void onReceive(boolean status, boolean isOpen) {
            Log.i(TAG, "onReceive: " + status + " isOpen:" + isOpen);
            ShowLog("onReceive: " + status + " isOpen:" + isOpen);
            if (isOpen) {
                if (status) {

                }
            }
        }

        @Override
        public void onProgress(int progress) {
            Log.i(TAG, "onProgress: progress => " + progress);
            ShowLog("onProgress: progress => " + progress);
        }

        @Override
        public void onData(final List<LoggingData> data) {
            ShowLog("11、Receive(false)");
            _DataManager.Receive(false);
            ShowLog("12、Generating Report");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GenerateReport(data);
                            }
                        });
                    } catch (Exception ex) {
                    }
                }
            }).start();
        }

        @Override
        public void onPassword(boolean status) {

        }

        @Override
        public void onFligthMode(boolean status) {

        }

        @Override
        public void onStart(boolean status) {

        }

        @Override
        public void onStop(boolean status) {

        }

        @Override
        public void onMark(boolean status) {

        }

        @Override
        public void onClearFlash(boolean b) {

        }
    };

    public void GenerateReport(final List<LoggingData> data) {
        try {
            if (data == null || data.size() == 0) {
                return;
            }

            int dataCount = 0;
            long beginTime = 0;
            long timeSpan = 0;
            DeviceRecordType recordStatus = DeviceRecordType.Initialize;
            long delayTime = 0;
            boolean repeatStart = false;
            TemperatureUnitType temperatureUnit = TemperatureUnitType.C;
            boolean stopButton = false;
            int startMode = 0;
            long startTime = 0;
            String description = null;
            double mkt = -1000;
            if (RequestDataInfo != null) {
                try {
                    dataCount = Integer.parseInt(RequestDataInfo[0]);
                    beginTime = Long.parseLong(RequestDataInfo[1]);
                    timeSpan = Long.parseLong(RequestDataInfo[2]);
                    recordStatus = DeviceRecordType.values()[Integer.parseInt(RequestDataInfo[3])];
                    delayTime = Long.parseLong(RequestDataInfo[4]);
                    repeatStart = Integer.parseInt(RequestDataInfo[5]) == 1 ? true : false;
                    temperatureUnit = RequestDataInfo[6].equals(TemperatureUnitType.F.toString()) ? TemperatureUnitType.F : TemperatureUnitType.C;
                    stopButton = Integer.parseInt(RequestDataInfo[7]) == 1 ? true : false;
                    startMode = Integer.parseInt(RequestDataInfo[8]);
                    startTime = Long.parseLong(RequestDataInfo[9]);
                    description = RequestDataInfo[10];
                    if(!RequestDataInfo[12].equals(""))
                        mkt = Double.parseDouble(RequestDataInfo[12]);
                } catch (Exception ex) {}

                if (beginTime == 0)
                    beginTime = data.get(0).getCreateTime();
            }
            if (dataCount != data.size()) {
                return;
            }

            String serialID = StringUtil.PadLeft("1", 6);
            _Report = new Report();
            _Report.DeviceID = _Device.getID();
            _Report.SerialID = serialID;
            _Report.DeviceTypeID = _Device.getDeviceType();
            _Report.DeviceName = _Device.getName();
            _Report.Version = _Device.getVersion();
            _Report.Mac = _Device.getMac();

            _Report.BeginTime = beginTime;
            _Report.TemperatureUnitTypeID = temperatureUnit;
            _Report.LoggingInterval = timeSpan;
            _Report.StartDelay = delayTime;
            _Report.StartMode = startMode;
            _Report.RecordStatus = recordStatus;
            _Report.MKT = mkt;
            _Report.Description = description;

            _Report.AlarmList = AlarmList;
            for (int i = 0; i < data.size(); i++) {
                LoggingData loggingData = data.get(i);
                _Report.DataList.add(loggingData);
            }
            if (MarkList != null && MarkList.size() > 0) {
                for (int j = 0; j < MarkList.size(); j++) {
                    int did = MarkList.get(j);
                    if (_Report.DataList != null && did < _Report.DataList.size())
                        _Report.DataList.get(did).setDataTypeID(LoggingData.DataType.Mark);
                }
            }
            _Report.CreateTime = new Date().getTime();
            _Report.Generate();

            ShowLog(JSON.toJSONString(_Report));
        } catch (Exception ex) {}
    }


}