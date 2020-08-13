package taokdao.plugins.apk.signer.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.apksig.ApkSigner;
import com.android.apksig.apk.ApkFormatException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import taokdao.api.file.util.FileUtils;
import taokdao.api.main.IMainContext;
import taokdao.api.main.action.MainAction;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.plugins.apk.signer.R;
import taokdao.plugins.apk.signer.key.bean.KeyAliasItem;
import taokdao.plugins.apk.signer.key.bean.KeyStoreItem;
import taokdao.plugins.apk.signer.key.load.AliasLoader;
import taokdao.plugins.apk.signer.key.load.KeyPassManager;
import taokdao.plugins.apk.signer.key.load.KeyStoreManager;
import taokdao.plugins.apk.signer.key.load.StorePassManager;
import taokdao.plugins.apk.signer.key.manage.KeyStoreManageDialog;
import taokdao.plugins.apk.signer.key.manage.adapter.SimpleAdapterViewOnItemSelectedListener;
import taokdao.plugins.apk.signer.key.manage.adapter.SpinnerUtils;
import tiiehenry.android.ui.dialogs.api.strategy.Dialogs;

public class APKSigner {
    private final IMainContext main;
    private final PluginManifest pluginManifest;
    private final Context pluginContext;
    private final KeyPassManager keyPassManager;
    private final File keysDir;

    public APKSigner(IMainContext main, PluginManifest pluginManifest, Context pluginContext) {
        this.main = main;
        this.pluginManifest = pluginManifest;
        this.pluginContext = pluginContext;
        keyPassManager = KeyPassManager.from(main, pluginManifest);
        keysDir = KeyStoreManager.getKeysDir(main, pluginManifest);
    }

    public void requestSignApk(String path) {
        Dialogs.global
                .asLoading()
                .addLoadingTask("加载秘钥中", iLoadingDialog -> {
                    try {
                        List<KeyStoreItem> keyStoreItemList = KeyStoreManager.loadItemListFromDir(keyPassManager, keysDir);
                        main.runOnUIThread(() -> {
                            showSignAPKDialog(keyStoreItemList, path);
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
//                .minDisplayTime(500)
                .show();
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private void showSignAPKDialog(List<KeyStoreItem> keyStoreItemList, String path) {
        if (keyStoreItemList.isEmpty()) {
            Dialogs.global
                    .asConfirm()
                    .title("没有导入的秘钥")
                    .content("请在插件中进行秘钥导入或创建")
                    .positiveText()
                    .show();
            return;
        }
        LayoutInflater layoutInflater = LayoutInflater.from(pluginContext);
        View layout = layoutInflater.inflate(R.layout.apk_sign_request_dialog, null);

        Spinner sp_store = layout.findViewById(R.id.sp_store);
        ArrayList<String> sp_store_list = new ArrayList<>();
        for (KeyStoreItem keyStoreItem : keyStoreItemList) {
            if (keyStoreItem.keyStore != null)
                sp_store_list.add(keyStoreItem.storeFile.getName());
        }
        ArrayAdapter<String> storeListAdapter = SpinnerUtils.newSpinnerAdapter(pluginContext, sp_store_list);
        sp_store.setAdapter(storeListAdapter);
        Spinner sp_alias = layout.findViewById(R.id.sp_alias);
        AtomicReference<KeyStoreItem> keyStoreItemRef = new AtomicReference<>(keyStoreItemList.get(0));
        AtomicReference<KeyAliasItem> keyAliasItemRef = new AtomicReference<>();

        sp_store.setOnItemSelectedListener((SimpleAdapterViewOnItemSelectedListener) (parent, view, position, id) -> {
            keyStoreItemRef.set(keyStoreItemList.get(position));
            ArrayList<KeyAliasItem> keyAliasItems = keyStoreItemRef.get().keyAliasItems;

            ArrayList<String> sp_alias_list = new ArrayList<>();
            for (KeyAliasItem item : keyAliasItems) {
                sp_alias_list.add(item.alias);
            }
            ArrayAdapter<String> aliasListAdapter = SpinnerUtils.newSpinnerAdapter(pluginContext, sp_alias_list);
            keyAliasItemRef.set(null);
            sp_alias.setAdapter(aliasListAdapter);
        });
        sp_alias.setOnItemSelectedListener((SimpleAdapterViewOnItemSelectedListener) (parent, view, position, id) -> {
            keyAliasItemRef.set(keyStoreItemRef.get().keyAliasItems.get(position));
        });
        Switch sp_v1 = layout.findViewById(R.id.sp_v1);
        Switch sp_v2 = layout.findViewById(R.id.sp_v2);
        TextView tv_message = layout.findViewById(R.id.tv_message);
        Dialogs.global
                .asCustom()
                .title("APK签名")
                .customView(layout, false)
                .negativeText()
                .positiveText()
                .onPositive(iDialog -> {
                    tv_message.setText("");
                    boolean v1 = sp_v1.isChecked();
                    boolean v2 = sp_v2.isChecked();
                    KeyStoreItem keyStoreItem = keyStoreItemRef.get();
                    KeyAliasItem keyAliasItem = keyAliasItemRef.get();
                    if (keyAliasItem == null) {
                        tv_message.setText("秘钥库没有私钥");
                        return;
                    }
                    if (keyAliasItem.keyPass == null) {
                        StorePassManager storePassManager = keyPassManager.getStorePassManager(keyStoreItem.storeFile);
                        new KeyStoreManageDialog(main, pluginManifest, pluginContext).showAliasPassInputDialog(storePassManager, keyStoreItem.keyStore, keyAliasItem, () -> {

                        });
                        return;
                    }
                    try {
                        new AliasLoader(keyStoreItem.keyStore, keyAliasItem).load();
                    } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        tv_message.setText("秘钥加载失败！");
                        return;
                    }
                    if (!v1 && !v2) {
                        tv_message.setText("至少选择一项签名方案");
                        return;
                    }
                    File input = new File(path);
                    File output = new File(input.getParent(), FileUtils.getNameWithoutExtension(input) + "_signed.apk");
                    showSignDialog(input, output, 14, v1, v2, keyAliasItem);
                    iDialog.dismiss();
                })
                .autoDismiss(false)
                .show();
    }

    private void showSignDialog(File in, File out, int minSdk, boolean v1, boolean v2, KeyAliasItem keyAliasItem) {
        Dialogs.global
                .asLoading()
                .addLoadingTask("签名APK中", iLoadingDialog -> {
                    try {
                        sign(in, out, minSdk, v1, v2, keyAliasItem);
                    } catch (ApkFormatException | NoSuchAlgorithmException | SignatureException | InvalidKeyException | IOException e) {
                        e.printStackTrace();
                        out.delete();
                        main.runOnUIThread(() -> {
                            Dialogs.global
                                    .asConfirm()
                                    .title("签名出错！")
                                    .content("错误信息：" + e.getMessage())
                                    .negativeText()
                                    .show();

                        });
                    }
                    main.runOnUIThread(() -> {
                        MainAction.onFileCreated.runObservers(main);
                        iLoadingDialog.dismiss();
                    });
                })
                .show();
    }

    public static void sign(File in, File out, int minSdk, boolean v1, boolean v2, KeyAliasItem keyAliasItem) throws ApkFormatException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        ApkSigner.SignerConfig signerConfig = new ApkSigner.SignerConfig.Builder(keyAliasItem.alias, keyAliasItem.privateKey, Collections.singletonList(keyAliasItem.certificate))
                .build();
        Log.e(APKSigner.class.getSimpleName(), "sign: " + keyAliasItem);
        ApkSigner signer = new ApkSigner.Builder(Collections.singletonList(signerConfig))
                .setInputApk(in)
                .setOutputApk(out)
//                .setCreatedBy("TaoKDao-Plugin_APK_Signer")
//                .setMinSdkVersion(minSdk)
                .setV1SigningEnabled(v1)
                .setV2SigningEnabled(v2)
                .build();
        signer.sign();
    }
}
