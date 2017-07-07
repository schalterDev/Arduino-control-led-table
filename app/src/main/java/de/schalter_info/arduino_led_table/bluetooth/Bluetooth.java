package de.schalter_info.arduino_led_table.bluetooth;

import android.app.Activity;

/**
 * Created by martin on 08.03.17.
 */
public class Bluetooth {

    public static final int RAINBOW = 1;
    public static final int COLOR_PALETTE = 2;
    public static final int STARS = 3;
    public static final int VU_METER = 4;
    public static final int DICE = 5;
    public static final int TETRIS = 6;
    public static final int SNAKE = 7;
    public static final int PONG = 8;
    public static final int BRICKS = 9;
    public static final int LEDS = 10;
    public static final int BUZZZER = 11;
    public static final int FTW = 12;

    public static final int COMMAND_UP = 1;
    public static final int COMMAND_DOWN = 2;
    public static final int COMMAND_RIGHT = 4;
    public static final int COMMAND_LEFT = 3;
    public static final int COMMAND_START = 5;
    public static final int COMMAND_END = 6;

    private Ardutooth ardutooth;
    private static Bluetooth bluetooth;
    private static Ardutooth.BluetoothListener bluetoothListener;

    public static Bluetooth getInstance(Activity activity, Ardutooth.BluetoothListener bluetoothListener) {
        Bluetooth.bluetoothListener = bluetoothListener;

        return getInstance(activity);
    }

    public static Bluetooth getInstance(Activity activity) {
        if(bluetooth == null)
            bluetooth = new Bluetooth(activity);

        return bluetooth;
    }

    private Bluetooth(Activity activity) {
        init(activity);
    }

    private void init(Activity activity) {
       ardutooth = new Ardutooth(activity, bluetoothListener);
    }

    public void connect() {
        ardutooth.startScan();
    }

    /**
     * Sends a command to the arduino that the selected program should be finished
     * This will start the program "black". That means no led is on
     * @return
     */
    public boolean sendEndGame() {
        sendCommand(COMMAND_END);
        return false;
    }

    /**
     * Sends a command to start the specific program.
     * The Arduino will only start a program when the program before was finished.
     * This can be done by sendEndGame()
     * @param program which should be started
     * @return true if the device replies with "received" and starts the program
     * and false if the device does not reply as expected
     */
    public boolean startProgram(int program) {
        sendCommand("p", program);
        return true;
    }

    /**
     * Sends a command to the connected bluetooth device
     * @param command this string will be send to the arduino
     * @return true if the device replies with "received" and
     * false if the device does not reply as expected
     */
    private boolean sendCommand(String command) {
        ardutooth.sendData(command + "?");
        return true;
    }

    private boolean sendCommand(String command, int number) {
        ardutooth.sendData(command);
        ardutooth.sendData(number);
        ardutooth.sendData("?");
        return true;
    }

    /**
     * Sends a command to the connected bluetooth device
     * @param command this command will be send to the arduino
     * @return true if the device replies with "received" and
     * false if the device does not reply as expected
     */
    public boolean sendCommand(int command) {
        sendCommand("c", command);
        return false;
    }

}
