package com.anysoftkeyboard.ime;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class IntentMapper {

    public static Intent mapCategoryToIntent(String category, String data) {
        if (TextUtils.isEmpty(category) || TextUtils.isEmpty(data)) {
            return null;
        }


        String trimmedData = data.trim();

        switch (category.toLowerCase()) {
            case "address":

                return new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(trimmedData)));

            case "phone":

                return new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + trimmedData));

            case "url":

                String url = trimmedData;
                if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) {
                    url = "http://" + url;
                }


                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                return browserIntent;


            case "email":

                return new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + trimmedData));

            default:

                return null;
        }
    }
}
