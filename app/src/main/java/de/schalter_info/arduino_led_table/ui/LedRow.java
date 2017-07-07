package de.schalter_info.arduino_led_table.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by martin on 15.03.17.
 */
public class LedRow extends LinearLayout {

    private Context context;

    public LedRow(Context context, int countLeds) {
        super(context);
        this.context = context;

        initView(context, countLeds);
    }

    private void initView(Context context, int countLeds) {
        this.setOrientation(HORIZONTAL);

        for(int i = 0; i < countLeds; i++) {
            addLed(255,0,0);
        }
    }

    private void addLed(int r, int g, int b) {
        Led led = new Led(context, r, g, b);

        led.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2.0f));

        this.addView(led);
    }

    public void setColorForLed(int countLed, int r, int g, int b) {
        Led led = (Led) this.getChildAt(countLed);
        led.setColor(r, g, b);
    }
}
