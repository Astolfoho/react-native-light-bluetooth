package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;

import android.bluetooth.*;
import android.util.Base64;
import android.util.Log;
import android.os.ParcelUuid;
import java.util.Set;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public class LightBluetoothModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private BluetoothDevice _device;
    private BluetoothSocket _socket;
    private OutputStream _outputStream;
    private InputStream _inputStream;

    public LightBluetoothModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "LightBluetooth";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }


    @ReactMethod
    public void init(String deviceName, Promise pro) {
        try {
            BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
            if (blueAdapter != null) {
                if (blueAdapter.isEnabled()) {
                    Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                    if (bondedDevices.size() > 0) {
                        for (Object d : bondedDevices.toArray()) {
                            BluetoothDevice device = (BluetoothDevice) d;
                            if (device.getName().equals(deviceName)) {
                                _device = device;
                                ParcelUuid[] uuids = device.getUuids();
                                _socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                                _socket.connect();
                                _outputStream = _socket.getOutputStream();
                                _inputStream = _socket.getInputStream();
                                pro.resolve(null);
                                return;
                            }
                        }
                    }
                    pro.reject("No appropriate paired devices.");
                    Log.e("error", "No appropriate paired devices.");
                } else {
                    pro.reject("Bluetooth is disabled.");
                    Log.e("error", "Bluetooth is disabled.");
                }
            }
            pro.reject("BluetoothAdapter is Null.");
        } catch (IOException ex) {
            pro.reject(ex.getMessage());
        }
    }

    @ReactMethod
    public void isDeviceBounded(String deviceName, Promise pro) {
        try {
            BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
            if (blueAdapter != null) {
                if (blueAdapter.isEnabled()) {
                    Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                    if (bondedDevices.size() > 0) {
                        for (Object d : bondedDevices.toArray()) {
                            BluetoothDevice device = (BluetoothDevice) d;
                            if (device.getName().equals(deviceName)) {
                                pro.resolve("true");
                                return;
                            }
                        }
                    }
                    pro.reject("false");
                    Log.e("error", "No appropriate paired devices.");
                } else {
                    pro.reject("false");
                    Log.e("error", "Bluetooth is disabled.");
                }
            }
            pro.reject("false");
        } catch (Exception ex) {
            pro.reject("false");
        }
    }

    @ReactMethod
    public void write(String base64Data, Promise pro) {
        try {
            byte[] data = Base64.decode(base64Data, Base64.DEFAULT);
            this._outputStream.write(data);
            pro.resolve(null);
        } catch (Exception ex) {
            pro.reject(ex.getMessage());
        }
    }
}
