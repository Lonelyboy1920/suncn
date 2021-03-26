
#Top-----------------友盟使用情况统计------------------
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class com.suncn.ihold_zxztc.R$*{
    public static final int *;
}
#End-----------------友盟使用情况统计------------------
#Top-----------------阿里热更新------------------
#基线包使用，生成mapping.txt
-printmapping mapping.txt
#生成的mapping.txt在app/build/outputs/mapping/release路径下，移动到/app路径下
#修复后的项目使用，保证混淆结果一致
#-applymapping mapping.txt
#hotfix
-keep class com.taobao.sophix.**{*;}
-keep class com.ta.utdid2.device.**{*;}
-dontwarn com.alibaba.sdk.android.utils.**
#防止inline
-dontoptimize
-keepclassmembers class com.suncn.ihold_zxztc.MyApplication {
    public <init>();
}
# 如果不使用android.support.annotation.Keep则需加上此行
# -keep class com.my.pkg.SophixStubApplication$RealApplicationStub
#End-----------------阿里热更新------------------
