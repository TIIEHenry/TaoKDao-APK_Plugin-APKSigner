package taokdao.plugins.apk.signer.invoke;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import java.util.Map;

import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.api.plugin.bridge.invoke.IInvokeCallback;
import taokdao.plugins.apk.signer.AConstant;

public class InvokeController {
    public String onInvoke(@NonNull IMainContext main, @NonNull PluginManifest manifest, @NonNull String method, @Nullable String params, @Nullable IInvokeCallback invokeCallback) {
        switch (method) {
            case AConstant.Invoker.PARAM_SIGN_APK:
                return onSignApk(main, manifest, params, invokeCallback);
            case "request_sign_apk":
                return requestSignApk(main, manifest, params, invokeCallback);
        }
        return null;
    }

    private String requestSignApk(IMainContext main, PluginManifest manifest, String params, IInvokeCallback invokeCallback) {
        return null;
    }

    private String onSignApk(@NonNull IMainContext main, @NonNull PluginManifest manifest, @Nullable String params, @Nullable IInvokeCallback invokeCallback) {
        try {
            Map<String, String> map = JSON.parseObject(params, Map.class);
            String inPath = map.get("in");
            String outPath = map.get("out");
            String storePath = map.get("storePath");
            String storePass = map.get("storePass");
            String alias = map.get("alias");
            String aliasPass = map.get("aliasPass");
            String v1 = map.get("v1");
            String v2 = map.get("v2");

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }
}
