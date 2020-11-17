package com.jamaica.japp.helper;

import android.view.View;

import com.jamaica.japp.modelsList.catSubCatlistModel;

public interface CatSubCatOnclicklinstener {
    void onItemClick(catSubCatlistModel item);
    void onItemTouch(catSubCatlistModel item);
    void addToFavClick(View v, String position);

}
