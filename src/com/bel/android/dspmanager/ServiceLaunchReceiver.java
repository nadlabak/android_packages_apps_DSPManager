package com.bel.android.dspmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bel.android.dspmanager.activity.DSPManager;

/**
* This receiver keeps our HeadsetService running. It listens to UPDATE events which correspond to
* preferences changes, and BOOT_COMPLETE event, and then consults application's preferences
* to evaluate whether {@link HeadsetService} needs starting.
*
* @author alankila
*/
public class ServiceLaunchReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences preferencesHeadset = context.getSharedPreferences(DSPManager.SHARED_PREFERENCES_BASENAME + ".headset", 0);
		SharedPreferences preferencesSpeaker = context.getSharedPreferences(DSPManager.SHARED_PREFERENCES_BASENAME + ".speaker", 0);
		SharedPreferences preferencesBluetooth = context.getSharedPreferences(DSPManager.SHARED_PREFERENCES_BASENAME + ".bluetooth", 0);
		SharedPreferences preferences = context.getSharedPreferences(DSPManager.SHARED_PREFERENCES_BASENAME + "_preferences", 0);

		boolean serviceNeeded = false;

		if (preferences.getBoolean("dsp.compression.allow", false))
			serviceNeeded = true;
		else
			/* check through the config if any of the master checkboxes are set. */
			for (SharedPreferences p : new SharedPreferences[] {
					preferencesHeadset, preferencesSpeaker, preferencesBluetooth
			}) {
				for (String s : new String[] {
					"dsp.bass.enable",
					"dsp.headphone.enable",
					"dsp.tone.enable",
				}) {
					if (p.getBoolean(s, false)) {
						serviceNeeded = true;
					}
				}
			}

		if (serviceNeeded) {
			context.startService(new Intent(HeadsetService.NAME));
		} else {
			context.stopService(new Intent(HeadsetService.NAME));
		}
	}
}
