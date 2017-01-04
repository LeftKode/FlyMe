package com.mobiledev.uom.flyme.classes;

import android.content.Context;
import android.widget.Toast;

//Κλάση για να δείχνει μόνο ένα toast και να διακόπτεται από ένα νέο
public class SingleToast {

    private static Toast mToast;

    public static void show(Context context, String text, int duration) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(context, text, duration);
        mToast.show();
    }
}
