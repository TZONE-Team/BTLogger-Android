package com.tzone.btloggerexample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tzone.bluetooth.BleManager;
import com.tzone.bluetooth.callback.BleScanCallback;
import com.tzone.bluetooth.data.BleDevice;
import com.tzone.bluetooth.data.BleScanState;
import com.tzone.bluetooth.scan.BleScanRuleConfig;
import com.tzone.devices.BaseDevice;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScanActivity extends ListActivity {
    public  final String TAG = "ScanActivity";
    private ListView_ScanDeviceListAdapter _ListView_deviceAdapter;
    private Timer _Timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Scan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopScan();
    }

    public void ShowTips(String tips) {
        try {
            Toast toast = Toast.makeText(this, tips, Toast.LENGTH_LONG);
            toast.setText(tips);
            toast.show();
        } catch (Exception ex) {
        }
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

    public ScanActivity.ListViewCallBack _ListViewCallBack = new ScanActivity.ListViewCallBack() {
        @Override
        public void OnSelect(final Scan device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StopScan();

                    Intent intent = new Intent(ScanActivity.this, DeviceActivity.class);
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
