package com.wuxiaosu.dexclassloaderdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dexLoad();
    }

    private void dexLoad() {
        DexClassManage.Builder builder = new DexClassManage.Builder(this);
        DexClassManage dexManage;
        builder.setDexName("classes.dex");  // .dex 文件名
        builder.setClassName("com.wuxiaosu.logindemo.util.EncryptUtils");   // 类名
        builder.setMethod("decrypt", true, String.class);   //方法名，是否为静态方法，参数类型...
        builder.setArgs("HhLiIBqa/Zk=");    //参数...
        dexManage = builder.create();
        String v0_1 = (String) dexManage.invoke();
        Log.e("biu", "======>> " + v0_1);
    }

}
