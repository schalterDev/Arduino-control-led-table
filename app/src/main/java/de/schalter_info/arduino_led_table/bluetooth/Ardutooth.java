package de.schalter_info.arduino_led_table.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.schalter_info.arduino_led_table.R;

/**
 * Ardutooth allows developers to create easily a bluetooth connection between Android and Arduino.
 *
 * @author Martin Schalter
 */
public class Ardutooth extends Activity {

    private boolean connected = false;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;

    private Activity ac;
    private BluetoothAdapter adapter;
    private ProgressDialog mProgressDlg;
    private List<String> devicesList;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alertDialog;
    private IntentFilter filter;

    private BluetoothListener bluetoothListener;

    /**
     * Create a new Ardutooth object
     *
     * @param ac Activity where Ardutooth is supposed to act
     */
    public Ardutooth(Activity ac, BluetoothListener bluetoothListener) {
        this.ac = ac;
        this.bluetoothListener = bluetoothListener;

        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        ac.registerReceiver(mReceiver, filter);

        devicesList = new ArrayList<>();
        dialogBuilder = new AlertDialog.Builder(ac);

        mProgressDlg = new ProgressDialog(ac);
        mProgressDlg.setMessage("Scanning...");
        mProgressDlg.setCancelable(false);
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                adapter.cancelDiscovery();
            }
        });

        Connections.checkStatus(ac);

    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d("ARDUTOOTH", "Device found");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devicesList.add("Name: " + device.getName() + " Add: " + device.getAddress());

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("ARDUTOOTH", "Discovery finished");
                bluetoothListener.discoveryEnded();

                mProgressDlg.dismiss();

                final CharSequence[] devicesSequence = devicesList.toArray(new String[devicesList.size()]);
                dialogBuilder.setTitle(R.string.devices_found);
                dialogBuilder.setItems(devicesSequence, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String selectedText = devicesSequence[item].toString();  //Selected item in listview
                        String address = selectedText.substring(selectedText.length() - 17); // 17 is the length of a MAC address in form 00:00:00:00:00:00

                        BluetoothDevice btDev = adapter.getRemoteDevice(address);
                        pairDevice(btDev);
                        connect(btDev);
                    }
                });
                dialogBuilder.setCancelable(false);

                //Create alert dialog object via builder
                alertDialog = dialogBuilder.create();
                //Show the dialog
                alertDialog.show();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d("ARDUTOOTH", "Discovery started");
                bluetoothListener.discoveryStarted();
                mProgressDlg.show();

            }  else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                alertDialog.dismiss();
                Toast.makeText(ac.getApplicationContext(), R.string.connected, Toast.LENGTH_SHORT).show();
                connected = true;
                BluetoothDevice btDev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothListener.connected(btDev);
            } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
                        BluetoothAdapter.STATE_DISCONNECTED);

                if(state == BluetoothAdapter.STATE_DISCONNECTED) {
                    bluetoothListener.disconnected();
                } else if(state == BluetoothAdapter.STATE_CONNECTED) {
                    BluetoothDevice btDev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    bluetoothListener.connected(btDev);
                }
            }

        }
    };

    /**
     * Start discovering bluetooth device nearby
     */
    public void startScan() {
        // Getting the Bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();
        Log.d("ARDUTOOTH", "\nAdapter: " + adapter.toString() + "\n\nName: "
                + adapter.getName() + "\nAddress: " + adapter.getAddress());

        Log.d("ARDUTOOTH", "Starting discovery...");
        adapter.startDiscovery();

        // Listing paired devices
        Log.d("ARDUTOOTH", "\nDevices Pared:");
        Set<BluetoothDevice> devices = adapter.getBondedDevices();

        for (BluetoothDevice device : devices) {
            devicesList.add("Name: " + device.getName() + " Add: " + device.getAddress());
        }

    }

    /**
     * Pair a Bluetooth device.
     * Note that pairing a device is different from connect.
     *
     * @param device to pair with
     */
    public void pairDevice(BluetoothDevice device) {
        try {
            Log.d("", "Start Pairing...");

            Method m = device.getClass()
                    .getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            //Toast.makeText(ac.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ac.getApplicationContext(), R.string.wrong_pairing,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Unpair a Bluetooth device.
     * Note that unpairing a device is different from disconnect.
     *
     * @param device to pair with
     */
    public void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Toast.makeText(ac.getApplicationContext(), R.string.wrong_unpairing,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Connect current device with another Bluetooth device
     *
     * @param device to connect with
     */
    public boolean connect(BluetoothDevice device) {
        mDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Toast.makeText(ac.getApplicationContext(), R.string.wrong_socket, Toast.LENGTH_SHORT).show();
            return false;
        }

        return startConnection();
    }

    private boolean startConnection() {
        // Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mSocket.connect();
            return true;
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            Toast.makeText(ac.getApplicationContext(), R.string.wrong_connecting, Toast.LENGTH_SHORT).show();
            try {
                mSocket.close();
            } catch (IOException closeException) {
            }
            return false;
        }
    }

    /**
     * Send data to arduino by a String
     *
     * @param data String that represents data you want communicate to Arduino
     */
    public void sendData(String data) {
        if (mSocket != null) {
            try {
                // String must be converted in its bytes to be sent on serial
                // communication
                mSocket.getOutputStream().write(data.getBytes());
            } catch (IOException e) {
                Toast.makeText(ac.getApplicationContext(), R.string.error_passing_data, Toast.LENGTH_SHORT).show();
                Log.d("ARDUTOOTH", "Error sending data");
            }
        }
    }

    public void sendData(int data) {
        if (mSocket != null) {
            try {
                // String must be converted in its bytes to be sent on serial
                // communication
                mSocket.getOutputStream().write(data);
            } catch (IOException e) {
                Toast.makeText(ac.getApplicationContext(), R.string.error_passing_data, Toast.LENGTH_SHORT).show();
                Log.d("ARDUTOOTH", "Error sending data");
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        ac.unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        ac.registerReceiver(mReceiver, filter);

        if (connected == false) {
            startScan();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

        if (connected == false) {
            startScan();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first

        if (connected == false) {
            startScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        startScan();
    }

    public interface BluetoothListener {
        void discoveryStarted();
        void discoveryEnded();
        void connected(BluetoothDevice bluetoothDevice);
        void disconnected();
    }

}
