package com.cleven.clchat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

public class CLImageLoadUtil {

    public static void loadRoundImg(ImageView imageView, String url, @DrawableRes int placeholderId, float radius) {

        Glide.with(imageView.getContext()).load(url)
                .apply(new RequestOptions().placeholder(placeholderId).error(placeholderId).centerCrop()
                        .transform(new CLCornerTransform(imageView.getContext(), radius)))
                .thumbnail(loadTransform(imageView.getContext(),placeholderId,radius))
                .thumbnail(loadTransform(imageView.getContext(),placeholderId,radius))
                .into(imageView);
    }

    private static RequestBuilder<Drawable> loadTransform(Context context, @DrawableRes int placeholderId, float radius) {
        return Glide.with(context)
                .load(placeholderId)
                .apply(new RequestOptions().centerCrop()
                        .transform(new CLCornerTransform(context,radius)));


    }
}
