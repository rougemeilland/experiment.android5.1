package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import com.google.android.gms.location.LocationRequest

enum class LocationRequestSetting {
    NORMAL_MODE {
        override fun getLocationRequest(context: Context): LocationRequest {
            return LocationRequest.create().apply {
                interval =
                    context.resources.getInteger(
                        R.integer.location_watcher_location_request_interval_for_normal_mode
                    ).toLong()
                priority =
                    if (context.resources.getBoolean(R.bool.location_watcher_use_high_resolution))
                        LocationRequest.PRIORITY_HIGH_ACCURACY
                    else
                        LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            }
        }
    },
    POWER_SAVING_MODE {
        override fun getLocationRequest(context: Context): LocationRequest {
            return LocationRequest.create().apply {
                interval =
                    context.resources.getInteger(
                        R.integer.location_watcher_location_request_interval_for_power_saving_mode
                    ).toLong()
                priority =
                    if (context.resources.getBoolean(R.bool.location_watcher_use_high_resolution))
                        LocationRequest.PRIORITY_HIGH_ACCURACY
                    else
                        LocationRequest.PRIORITY_LOW_POWER
            }
        }
    }
    ;

    abstract fun getLocationRequest(context: Context): LocationRequest
}
