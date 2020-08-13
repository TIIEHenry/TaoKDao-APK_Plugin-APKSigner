package taokdao.plugins.apk.signer.file.operators;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import taokdao.api.file.base.FileType;
import taokdao.api.file.operate.wrapped.SuffixIFileOperator;
import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.plugins.apk.signer.AConstant;
import taokdao.plugins.apk.signer.file.APKSigner;

public class APKSignOperator extends SuffixIFileOperator {

    private final Context pluginContext;
    private final PluginManifest pluginManifest;

    public APKSignOperator(Context pluginContext, PluginManifest pluginManifest) {
        this.pluginContext = pluginContext;
        this.pluginManifest = pluginManifest;
        supportType = FileType.FILE;
        supportSuffix.add("apk");
    }

    @Nullable
    @Override
    public Drawable getIcon() {
        return null;
    }

    @NonNull
    @Override
    public String getLabel() {
        return "签名APK";
    }

    @Nullable
    @Override
    public String getDescription() {
        return "使用秘钥签名APK";
    }

    @NonNull
    @Override
    public String id() {
        return AConstant.FileOperator.SIGN_APK;
    }

    @Override
    public boolean call(IMainContext main, String path) {
        new APKSigner(main, pluginManifest, pluginContext).requestSignApk(path);
        return true;
    }

}
