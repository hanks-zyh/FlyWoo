package com.zjk.wifiproject.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

public class BaseFragment extends Fragment {

    protected Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }
}
