package com.zjk.wifiproject.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

public class BaseFragment extends Fragment {

    protected Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }
}
