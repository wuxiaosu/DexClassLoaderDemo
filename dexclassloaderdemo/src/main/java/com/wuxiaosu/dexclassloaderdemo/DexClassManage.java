package com.wuxiaosu.ciphertools.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by su on 2017/5/12.
 */

public class DexClassManage {

    private Context mContext;
    private DexClassLoader mLoader;
    private String mDexName = "classes.dex";
    private String mClassName;
    private String mMethodName;
    private Class<?>[] mParameterTypes;
    private Object[] mArgs;
    private Object[] mInitArgs;
    private Class<?>[] mInitArgTypes;
    private boolean mIsStatic = false;

    private DexClassManage(Context context) {
        mContext = context;
    }

    public static class Builder {
        private DexClassManage mDexClassManage;

        public Builder(Context context) {
            mDexClassManage = new DexClassManage(context);
        }

        public Builder setDexName(String dexName) {
            mDexClassManage.mDexName = dexName;
            return this;
        }

        public Builder setClassName(String className) {
            mDexClassManage.mClassName = className;
            return this;
        }

        public Builder setClassInitArgs(Object... initArgs) {
            mDexClassManage.mInitArgs = initArgs;
            return this;

        }

        public Builder setClassInitArgTypes(Class... parameterTypes) {
            mDexClassManage.mInitArgTypes = parameterTypes;
            return this;
        }

        public Builder setMethod(String methodName, boolean isStatic, Class<?>... parameterTypes) {
            mDexClassManage.mIsStatic = isStatic;
            mDexClassManage.mMethodName = methodName;
            mDexClassManage.mParameterTypes = parameterTypes;
            return this;
        }

        public Builder setArgs(Object... args) {
            mDexClassManage.mArgs = args;
            return this;
        }

        public DexClassManage create() {
            mDexClassManage.build();
            return mDexClassManage;
        }
    }

    private void build() {
        try {
            copyFiles(mContext, mDexName);
            mLoader = new DexClassLoader(mContext.getFilesDir().toString() + File.separator + mDexName,
                    mContext.getCacheDir().getAbsolutePath(), null, getClass().getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object invoke() {
        try {
            Method method;
            Class classes;
            classes = mLoader.loadClass(mClassName);
            Object instance = null;
            if (!mIsStatic) {
                Constructor localConstructor;
                if (mInitArgTypes != null && mInitArgs != null) {
                    localConstructor = classes.getConstructor(mInitArgTypes);
                    instance = localConstructor.newInstance(mInitArgs);
                } else {
                    localConstructor = classes.getConstructor();
                    instance = localConstructor.newInstance();
                }
            }
            method = classes.getMethod(mMethodName, mParameterTypes);
            return method.invoke(instance, mArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String copyFiles(Context context, String fileName) throws IOException {
        File path = context.getFilesDir();
        File fs = new File(path, fileName);

        if (!fs.exists()) {
            InputStream myInput;
            OutputStream myOutput = new FileOutputStream(fs);
            myInput = context.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        }
        return fs.getAbsolutePath();
    }
}
