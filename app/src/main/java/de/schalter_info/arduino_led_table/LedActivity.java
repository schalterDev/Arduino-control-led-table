package de.schalter_info.arduino_led_table;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.schalter_info.arduino_led_table.ui.LedRow;

public class LedActivity extends AppCompatActivity {

    private static final int ROW_COUNT = 10;
    private static final int COLUMN_COUNT = 15;

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        initView();
    }

    private void initView() {
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout_led_rows);

        for(int i = 0; i < ROW_COUNT; i++) {
            LedRow row = new LedRow(this, COLUMN_COUNT);
            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2.0f));
            linearLayout.addView(row);
        }
    }

}
