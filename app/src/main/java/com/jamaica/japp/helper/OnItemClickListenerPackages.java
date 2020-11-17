package com.jamaica.japp.helper;

import com.jamaica.japp.modelsList.PackagesModel;

public interface OnItemClickListenerPackages {
    void onItemClick(PackagesModel item);
    void onItemTouch();
    void onItemSelected(PackagesModel packagesModel,int spinnerPosition);
}
