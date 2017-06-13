# DexClassLoaderDemo
## 0.0 Dex文件是什么？
**Dex** 文件本质是一种经过 **Android** 打包工具优化后的 **Class** 文件，是 **Android** 平台上( **Dalvik** 虚拟机)的可执行文件， **Android** 中通过 **DexClassLoader** 类来加载这样特殊的 **Class** 文件。
## 0.1 为什么要加载其他应用的Dex？
在进行 **Android** 逆向分析的时候，有时候会碰到一些关键数据需要解密，但在反编译过程中发现解密算法太过复杂，无法或难以直接还原，这时可以尝试通过加载 **Dex** 的方法，直接调用应用内的解密方法来进行解密。  
## 1. 获取应用Dex文件  
假装 **logindemo-debug.apk** 是一个加密很复杂的应用。  
![00.png](https://raw.githubusercontent.com/wuxiaosu/DexClassLoaderDemo/master/images/00.png)
- 方式一  
直接通过解压缩的方式获取，将 **apk** 后缀名改为 **.rar** 或者 **.zip** 用压缩软件打开，解压后目录结构如下  
```
-- logindemo-debug
   -- META-INF
   -- res
   -- AndroidManifest.xml
   -- classes.dex
   -- resources.arsc
```
**classes.dex** 就是我们需要的文件。

- 方式二  
通过Android反编译集成工具APKDB 安卓逆手 反编译获取，APKDB 安卓逆手是什么鬼以及怎么安装，见官网 [http://idoog.me/?tag=apkdb](http://idoog.me/?tag=apkdb) 
![01.png](https://raw.githubusercontent.com/wuxiaosu/DexClassLoaderDemo/master/images/01.png)
![02.png](https://raw.githubusercontent.com/wuxiaosu/DexClassLoaderDemo/master/images/02.png)
效果一样
```
-- logindemo-debug
   -- original
   -- res
   -- AndroidManifest.xml
   -- classes.dex
   -- apktool.yml
```
## 3.调用dex内的方法  
**logindemo** 主要登录代码
```
    private void attemptLogin() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (email.equals("su@wuxiaosu.com") &&
                password.equals(EncryptUtils.decrypt("HhLiIBqa/Zk="))) {
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        }
    }
```
现在我们需要调用 **EncryptUtils** 类中的方法 **decrypt** 来对字符串 **"HhLiIBqa/Zk="** 进行解密。
- 首先  
在 **Android studio**  中新建一个 **Android** 项目 **DexClassLoaderDemo**，将前面解压的 **classes.dex** 文件放到项目 **assets** 文件夹中（其实加载 **.apk** 或者 **.jar** 也可以）
- 然后  
运行时将 **classes.dex** 释放到设备上，需要添加 **WRITE_EXTERNAL_STORAGE** 权限，这里代码不贴了
- 然后  
用 **DexClassLoader** 加载 **.dex** 文件，再用反射调用 **decrypt** 方法，关于 **DexClassLoader** 详情可以查看官方文档 [https://developer.android.com/reference/dalvik/system/DexClassLoader.html](https://developer.android.com/reference/dalvik/system/DexClassLoader.html)
```
DexClassLoader mLoader = new DexClassLoader(
        dexPath, optimizedDirectory, null, this.getClass().getClassLoader());
//dexPath：需要装载的APK或者Jar文件的路径
//optimizedDirectory:优化后的dex文件存放目录，不能为null
//librarySearchPath:目标类中使用的C/C++库的列表，可为null
//parent:该类装载器的父装载器，一般用当前执行类的装载器
Class<?> clazz;
try {
    clazz mLoader.loadClass("com.wuxiaosu.logindemo.util.EncryptUtils");
    Method method = clazz.getMethod("decrypt"String.class);
    Log.e("biu", "======>> " + method.invoke(null"HhLiIBqa/Zk="));
} catch (ClassNotFoundException e) {
    e.printStackTrace();
} catch (IllegalAccessException e) {
    e.printStackTrace();
} catch (NoSuchMethodException e) {
    e.printStackTrace();
} catch (InvocationTargetException e) {
    e.printStackTrace();
}
```
输出结果
```
E/biu: ======>> 123456
```
![03.png](https://raw.githubusercontent.com/wuxiaosu/DexClassLoaderDemo/master/images/03.png)  
Bingo！
## 4. 最后
抽成了一个工具类，方便以后使用
```
DexClassManage.Builder builder = neDexClassManage.Builder(this);
DexClassManage dexManage;
builder.setDexName("classes.dex");  // .dex 文件名
builder.setClassName("com.wuxiaosu.logindemo.util.EncryptUtils");   // 类名
builder.setMethod("decrypt", true, String.class); //方法名，是否为静态方法，参数类型...
builder.setArgs("HhLiIBqa/Zk=");    //参数...
dexManage = builder.create();
String v0_1 = (String) dexManage.invoke();
Log.e("biu", "======>> " + v0_1);
```
注：如果原方法参数为数组类型，直接在 **builder.setArgs(...)** 传递 **String[]** 会报异常，需要再次用 **Object[]** 包装一下,就是酱紫

```
class A {
    private void biu(String[] arg) {
        //...
    }
}

String[] arg = new String[]{"A", "B", "C"};

builder.setMethod("biu", false, String[].class);
builder.setArgs(new Object[]{arg});
```
