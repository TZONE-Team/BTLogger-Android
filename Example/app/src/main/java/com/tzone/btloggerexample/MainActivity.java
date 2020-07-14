package com.tzone.btloggerexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.callback.BleScanCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.bluetooth.scan.BleScanRuleConfig;
import com.tzone.bt.BaseDevice;
import com.tzone.bt.ConfigManagerBase;
import com.tzone.bt.DeviceType;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ListActivity {
    private final String TAG = "MainActivity";
    private ListView_ScanDeviceListAdapter _ListView_deviceAdapter;
    private Timer _Timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (_ListView_deviceAdapter == null) {
            _ListView_deviceAdapter = new ListView_ScanDeviceListAdapter(this, _ListViewCallBack);
            setListAdapter(_ListView_deviceAdapter);
        }

        try {
            BleManager.getInstance().init(getApplication());
            BleManager.getInstance()
                    .setReConnectCount(3, 5000)
                    .setConnectOverTime(20000)
                    .setOperateTimeout(5000);

            if (BleManager.getInstance().isSupportBle() == false
                    || BleManager.getInstance().isBlueEnable() == false) {
                ShowTips("Please turn on Bluetooth!");
                return;
            }
        } catch (Exception ex) {
            Log.e(TAG, "onCreate => " + ex.toString());
        }

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }else{
            Scan();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Scan();
                } else {
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void ShowTips(String tips) {
        try {
            Toast toast = Toast.makeText(this, tips, Toast.LENGTH_LONG);
            toast.setText(tips);
            toast.show();
        } catch (Exception ex) {
        }
    }



    public void InitApp(){

    }

    /**
     * 扫描
     */
    public void Scan(){
        try {
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan();
            BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                    .setScanTimeOut(1000 * 60)
                    .build();
            BleManager.getInstance().initScanRule(scanRuleConfig);
            BleManager.getInstance().scan(DeviceScanCallback);

            if (_Timer != null)
                _Timer.cancel();
            _Timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_IDLE)
                                BleManager.getInstance().scan(DeviceScanCallback);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RefreshUI();
                                }
                            });
                        }
                    } catch (Exception ex) {
                    }
                }
            };
            _Timer.schedule(timerTask, 1000, 500);
        } catch (Exception ex) {
            Log.e(TAG, "Scan => " + ex.toString());
        }
    }
    /**
     * 停止扫描
     */
    public void StopScan(){
        try {
            if (_Timer != null)
                _Timer.cancel();
            if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan();
        } catch (Exception ex) {
            Log.e(TAG, "StopScan => " + ex.toString());
        }
    }

    public BleScanCallback DeviceScanCallback = new BleScanCallback() {
        @Override
        public void onScanFinished(List<BleDevice> scanResultList) {
            Log.i(TAG, "onScanFinished => " + scanResultList.size());
        }

        @Override
        public void onScanStarted(boolean success) {
            Log.i(TAG, "onScanStarted => " + success);
        }

        @Override
        public void onScanning(BleDevice bleDevice) {
            if (bleDevice == null)
                return;
            Log.i(TAG, "onScanning => " + bleDevice.getMac() + " " + bleDevice.getName());
            Scan device = new Scan();
            if (device.fromBroadcast(bleDevice))
                _ListView_deviceAdapter.AddOrUpdate(device);
        }
    };

    public Date RefreshTime = new Date();
    public boolean IsRefreshing = false;

    /**
     * Refresh UI
     */
    private void RefreshUI() {
        try {
            if (IsRefreshing)
                return;

            if (_ListView_deviceAdapter != null) {
                Date now = new Date();
                long t = (now.getTime() - RefreshTime.getTime());
                IsRefreshing = true;
                if (t > 1000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (this) {
                                try {
                                    _ListView_deviceAdapter.notifyDataSetChanged();
                                } catch (Exception ex) {
                                }
                                IsRefreshing = false;
                            }
                        }
                    });
                    RefreshTime = now;
                } else {
                    IsRefreshing = false;
                }
            }

        } catch (Exception ex) {
            IsRefreshing = false;
        }
    }


    public interface ListViewCallBack {
        public abstract void OnSelect(Scan device);
    }

    public ListViewCallBack _ListViewCallBack = new ListViewCallBack() {
        @Override
        public void OnSelect(final Scan device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StopScan();

                    Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                    Bundle bundle = new Bundle();
                    BaseDevice baseDevice = new BaseDevice();
                    baseDevice.ID = device.getID();
                    baseDevice.Name = device.getName();
                    baseDevice.Mac = device.getMac();
                    baseDevice.HardwareType = device.getHardwareType();
                    baseDevice.Version = device.getVersion();
                    intent.putExtra("d", JSON.toJSONString(baseDevice));
                    startActivity(intent);
                    finish();
                }
            });
        }
    };
}