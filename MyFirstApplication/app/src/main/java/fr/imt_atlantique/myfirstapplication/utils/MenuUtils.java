package fr.imt_atlantique.myfirstapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class MenuUtils {

    public static void shareCity(Context context, String city) {
        if (!city.isEmpty()) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "City Information");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this city: " + city + "!");
            context.startActivity(Intent.createChooser(shareIntent, "Share city via"));
        } else {
            Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openCityWikipediaPage(Context context, String cityName) {
        if (!cityName.isEmpty()) {
            Uri uri = Uri.parse("http://en.wikipedia.org/?search=" + Uri.encode(cityName));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Please enter a city name to search", Toast.LENGTH_SHORT).show();
        }
    }
}