package com.dspark.smsassistance.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class UniqueID {

    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected volatile static UUID uuid;

    Context context;

    public UniqueID(Context _context) {
        context = _context;
        if (uuid == null) {
            synchronized (UniqueID.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context
                            .getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the ids previously computed and stored in the
                        // prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Settings.Secure.getString(
                                context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        // Use the Android ID unless it's broken, in which case
                        // fallback on deviceId,
                        // unless it's not available, then fallback on a random
                        // number which we store to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId
                                        .getBytes("utf8"));
                            } else {
                                final String deviceId = (
                                        (TelephonyManager) context
                                                .getSystemService(Context.TELEPHONY_SERVICE))
                                        .getDeviceId();
                                uuid = deviceId != null ? UUID
                                        .nameUUIDFromBytes(deviceId
                                                .getBytes("utf8")) : UUID
                                        .randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        // Write the value out to the prefs file
                        prefs.edit()
                                .putString(PREFS_DEVICE_ID, uuid.toString())
                                .commit();
                    }
                }
            }
        }
    }

    public String getUniqueID(){
        try {
            String id = "";
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                id = telephonyManager.getLine1Number();
            } catch (Exception e) {

            }

            if (id.isEmpty() == true)
                id = telephonyManager.getDeviceId(); //imei

            if (id.isEmpty() == true)
                id = telephonyManager.getSubscriberId(); //imsi

            if (id.isEmpty() == true)
                id = uuid.toString();

            return id;
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    public String getDeviceUuid() {
        return uuid.toString();
    }

    public  String serialnumber(){
        try {
            String idBySerialNumber = (String) Build.class.getField("SERIAL").get(null);
            return idBySerialNumber;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
