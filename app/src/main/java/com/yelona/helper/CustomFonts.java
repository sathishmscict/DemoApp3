package com.yelona.helper;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by PC2 on 09-Mar-17.
 */

public class CustomFonts {

//beware if you use other folder into assets (/fonts/condensed.ttf).

    private final static String CONDENSED_FONT = "fonts/MavenPro-Regular.ttf";



    public static Typeface typefaceCondensed(Context context) {
        return Typeface.createFromAsset(context.getResources().getAssets(),
                CONDENSED_FONT);
    }

}
