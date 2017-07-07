package de.schalter_info.arduino_led_table;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.logging.Handler;

import de.schalter_info.arduino_led_table.bluetooth.Ardutooth;
import de.schalter_info.arduino_led_table.bluetooth.Bluetooth;

public class MainActivity extends AppCompatActivity implements Ardutooth.BluetoothListener {

    private static boolean CONNECTED = true;
    private static boolean DISCONNECTED = false;

    public static String EXTRA_BUTTONS = "buttons";

    private Bluetooth bluetooth;
    private TextView txt_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         init();
    }

    private void init() {
        bluetooth = Bluetooth.getInstance(this, this);

        txt_status = (TextView) findViewById(R.id.textView_status_bluetooth);

        Button button_rainbow = (Button) findViewById(R.id.button_rainbow);
        Button button_colorPalette = (Button) findViewById(R.id.button_colorPalette);
        Button button_stars = (Button) findViewById(R.id.button_stars);
        Button button_vu = (Button) findViewById(R.id.button_vu);
        Button button_dice = (Button) findViewById(R.id.button_dice);
        Button button_tetris = (Button) findViewById(R.id.button_tetris);
        Button button_pong = (Button) findViewById(R.id.button_pong);
        Button button_bricks = (Button) findViewById(R.id.button_bricks);
        Button button_leds = (Button) findViewById(R.id.button_leds);
        Button button_buzzer = (Button) findViewById(R.id.button_buzzer);
        Button button_snake = (Button) findViewById(R.id.button_snake);
        Button button_connect = (Button) findViewById(R.id.button_connect);
        Button button_ftw = (Button) findViewById(R.id.button_ftw);
        Button button_ftw_auto_play = (Button) findViewById(R.id.button_ftw_auto_play);

        button_rainbow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRainbow();
            }
        });

        button_colorPalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startColorPalette();
            }
        });

        button_stars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStars();
            }
        });

        button_vu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVu();
            }
        });

        button_dice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDice();
            }
        });

        button_tetris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTetris();
            }
        });

        button_pong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPong();
            }
        });

        button_bricks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBricks();
            }
        });

        button_leds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLeds();
            }
        });

        button_buzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBuzzer();
            }
        });

        button_snake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSnake();
            }
        });

        button_ftw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFtw();
            }
        });

        button_ftw_auto_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutoPlay();
            }
        });

        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.connect();
            }
        });

        //TODO activate button
        button_buzzer.setEnabled(false);
        button_vu.setEnabled(false);
        button_leds.setEnabled(false);

        updateBluetoothStatus(false);
    }

    private void updateBluetoothStatus(boolean connected) {
        if(connected == CONNECTED) {
            txt_status.setText(R.string.connected);
        } else if(connected == DISCONNECTED) {
            txt_status.setText(R.string.not_connected);
        }
    }

    private boolean autoRun;
    private int timeToWait = 600;
    private boolean moveRight;
    private int moveSteps;

    private void startAutoPlay() {
        autoRun = true;
        Thread autoRunThread = new Thread(new Runnable() {
            @Override
            public void run() {
                bluetooth.startProgram(Bluetooth.FTW);

                try {
                    Thread.sleep(timeToWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                bluetooth.sendCommand(Bluetooth.COMMAND_START);

                try {
                    Thread.sleep(timeToWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Random random = new Random();

                do {

                    moveRight = random.nextBoolean();
                    moveSteps = random.nextInt(26);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Move " + moveSteps + " to right? " + moveRight, Toast.LENGTH_SHORT).show();
                        }
                    });

                    for (int i = 0; i < moveSteps; i++) {
                        if (moveRight)
                            bluetooth.sendCommand(Bluetooth.COMMAND_RIGHT);
                        else
                            bluetooth.sendCommand(Bluetooth.COMMAND_LEFT);

                        try {
                            Thread.sleep(timeToWait);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    bluetooth.sendCommand(Bluetooth.COMMAND_DOWN);

                    try {
                        Thread.sleep(timeToWait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    bluetooth.sendCommand(Bluetooth.COMMAND_START);

                    try {
                        Thread.sleep(timeToWait);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (autoRun);
            }
        });
        autoRunThread.start();
    }

    /**
     * Starts the controller activity with the given buttons deactivated
     * @param deactivatedButtons use the Bluetooth commands to find the right buttons
     */
    private void startControllerActivity(int... deactivatedButtons) {
        Intent intent = new Intent(this, ControllerActivity.class);
        intent.putExtra(EXTRA_BUTTONS, deactivatedButtons);
        startActivity(intent);
        autoRun = false;
    }

    private void startLedActivity() {
        Intent intent = new Intent(this, LedActivity.class);
        startActivity(intent);
    }

    private void startRainbow() {
        bluetooth.startProgram(Bluetooth.RAINBOW);

        startControllerActivity(Bluetooth.COMMAND_DOWN, Bluetooth.COMMAND_UP,
                Bluetooth.COMMAND_LEFT, Bluetooth.COMMAND_RIGHT);
    }

    private void startColorPalette() {
        bluetooth.startProgram(Bluetooth.COLOR_PALETTE);

        startControllerActivity(Bluetooth.COMMAND_DOWN, Bluetooth.COMMAND_UP,
                Bluetooth.COMMAND_LEFT, Bluetooth.COMMAND_RIGHT);
    }

    private void startStars() {
        bluetooth.startProgram(Bluetooth.STARS);

        startControllerActivity(Bluetooth.COMMAND_DOWN, Bluetooth.COMMAND_UP,
                Bluetooth.COMMAND_LEFT, Bluetooth.COMMAND_RIGHT);
    }

    private void startVu() {
        bluetooth.startProgram(Bluetooth.VU_METER);

        startControllerActivity(Bluetooth.COMMAND_DOWN, Bluetooth.COMMAND_UP,
                Bluetooth.COMMAND_LEFT, Bluetooth.COMMAND_RIGHT);
    }

    private void startDice() {
        bluetooth.startProgram(Bluetooth.DICE);

        startControllerActivity(Bluetooth.COMMAND_DOWN, Bluetooth.COMMAND_UP,
                Bluetooth.COMMAND_LEFT, Bluetooth.COMMAND_RIGHT);
    }

    private void startTetris() {
        bluetooth.startProgram(Bluetooth.TETRIS);

        startControllerActivity();
    }

    private void startPong() {
        bluetooth.startProgram(Bluetooth.PONG);

        startControllerActivity(Bluetooth.COMMAND_DOWN, Bluetooth.COMMAND_UP);
    }

    private void startBricks() {
        bluetooth.startProgram(Bluetooth.BRICKS);

        startControllerActivity(Bluetooth.COMMAND_DOWN, Bluetooth.COMMAND_UP);
    }

    private void startLeds() {
        startLedActivity();
    }

    private void startBuzzer() {
        //TODO startBuzzer
    }

    private void startSnake() {
        bluetooth.startProgram(Bluetooth.SNAKE);

        startControllerActivity();
    }

    private void startFtw() {
        bluetooth.startProgram(Bluetooth.FTW);

        startControllerActivity(Bluetooth.COMMAND_UP);
    }

    @Override
    public void discoveryStarted() {
        
    }

    @Override
    public void discoveryEnded() {

    }

    @Override
    public void connected(BluetoothDevice bluetoothDevice) {
        updateBluetoothStatus(CONNECTED);
    }

    @Override
    public void disconnected() {
        updateBluetoothStatus(DISCONNECTED);
    }
}
