package com.makeinindia.controller;

import android.app.Application;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by anant.y on 21-Apr-16.
 */
public class MyApplicationClass extends Application {

    private static MyApplicationClass instance;

    public static MyApplicationClass getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }
}