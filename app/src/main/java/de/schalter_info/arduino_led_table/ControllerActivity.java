package de.schalter_info.arduino_led_table;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import de.schalter_info.arduino_led_table.bluetooth.Bluetooth;

/**
 * Created by martin on 13.03.17.
 */
public class ControllerActivity extends AppCompatActivity {

    private Bluetooth bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller_activity);

        init();
    }

    @Override
    public void onBackPressed() {
        bluetooth.sendCommand(Bluetooth.COMMAND_END);
        finish();
    }

    private void init() {
        bluetooth = Bluetooth.getInstance(this);

        Button left = (Button) findViewById(R.id.button_left);
        Button right = (Button) findViewById(R.id.button_right);
        Button top = (Button) findViewById(R.id.button_top);
        Button down = (Button) findViewById(R.id.button_down);

        Button start = (Button) findViewById(R.id.button_start);
        Button end = (Button) findViewById(R.id.button_end);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.sendCommand(Bluetooth.COMMAND_LEFT);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.sendCommand(Bluetooth.COMMAND_RIGHT);
            }
        });

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.sendCommand(Bluetooth.COMMAND_UP);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.sendCommand(Bluetooth.COMMAND_DOWN);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.sendCommand(Bluetooth.COMMAND_START);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.sendCommand(Bluetooth.COMMAND_END);
                finish();
            }
        });

        Intent intent = getIntent();
        int[] buttons = intent.getIntArrayExtra(MainActivity.EXTRA_BUTTONS);

        for (int button : buttons) {
            switch (button) {
                case Bluetooth.COMMAND_DOWN:
                    down.setEnabled(false);
                    break;
                case Bluetooth.COMMAND_UP:
                    top.setEnabled(false);
                    break;
                case Bluetooth.COMMAND_RIGHT:
                    right.setEnabled(false);
                    break;
                case Bluetooth.COMMAND_LEFT:
                    left.setEnabled(false);
                    break;
                case Bluetooth.COMMAND_START:
                    start.setEnabled(false);
                    break;
                case Bluetooth.COMMAND_END:
                    end.setEnabled(false);
                    break;
            }
        }
    }


}
