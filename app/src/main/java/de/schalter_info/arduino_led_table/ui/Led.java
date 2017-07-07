package de.schalter_info.arduino_led_table.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import de.schalter_info.arduino_led_table.R;

/**
 * Created by martin on 15.03.17.
 */
public class Led extends ImageView {

    public Led(Context context, int r, int g, int b) {
        super(context);

        initView(context, r, g, b);
    }

    private void initView(Context context, int r, int g, int b) {
        this.setImageResource(R.drawable.led_background);

        setColor(r, g, b);
    }

    public void setColor(int r, int g, int b) {
        int color = Color.rgb(r, g, b);
        this.setColorFilter(color);
    }
}
