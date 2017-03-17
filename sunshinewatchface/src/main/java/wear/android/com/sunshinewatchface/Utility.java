/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wear.android.com.sunshinewatchface;

import android.content.Context;

import java.text.DateFormat;
import java.util.Date;

public class Utility {
    // We'll default our latlong to 0. Yay, "Earth!"
    public static float DEFAULT_LATLONG = 0F;

    public static String formatTemperature(Context context, double temperature) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        String suffix = "\u00B0";

        // For presentation, assume the user doesn't care about tenths of a degree.
        return String.format(context.getString(R.string.format_temperature), temperature);
    }

    static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }


    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.mipmap.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.mipmap.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.mipmap.art_rain;
        } else if (weatherId == 511) {
            return R.mipmap.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.mipmap.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.mipmap.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.mipmap.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.mipmap.art_storm;
        } else if (weatherId == 800) {
            return R.mipmap.art_clear;
        } else if (weatherId == 801) {
            return R.mipmap.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.mipmap.art_clouds;
        }
        return -1;
    }

}