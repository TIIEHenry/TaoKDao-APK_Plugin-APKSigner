package taokdao.plugins.apk.signer.file;

import java.io.File;
import java.io.IOException;

import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.plugins.apk.signer.key.load.KeyPassManager;
import taokdao.plugins.apk.signer.key.load.KeyStoreManager;
import taokdao.plugins.apk.signer.key.manage.KeyStoreManageDialog;
import taokdao.plugins.setup.io.Filej;
import tiiehenry.android.ui.dialogs.api.IDialog;
import tiiehenry.android.ui.dialogs.api.strategy.Dialogs;

public class KeyStoreImporter {

    private final File keysDir;
    private final PluginManifest pluginManifest;
    private final IMainContext main;

    public KeyStoreImporter(IMainContext iMainContext, PluginManifest pluginManifest) {
        this.main = iMainContext;
        this.keysDir = KeyStoreManager.getKeysDir(iMainContext, pluginManifest);
        this.pluginManifest = pluginManifest;
    }

    public void importKeyStore(String path) {
        File storeFile = new File(path);
        Dialogs.global
                .asConfirm()
                .title("导入秘钥库")
                .content(storeFile.getName())
                .positiveText()
                .onPositive(iDialog -> {
                    importKeyStoreFromPath(storeFile, iDialog);
                })
                .negativeText()
                .autoDismiss(false)
                .show();
    }


    private void importKeyStoreFromPath(File storeFile, IDialog iDialog) {
        File newFile = new File(keysDir, storeFile.getName());
        if (newFile.exists()) {
            Dialogs.global
                    .asConfirm()
                    .title("已存在")
                    .content("在已导入的秘钥库列表中有同名文件")
                    .positiveText()
                    .show();
            return;
        }
        KeyPassManager keyPassManager = KeyPassManager.from(main, pluginManifest);
        KeyStoreManageDialog.showStorePassInputDialog(keyPassManager.getStorePassManager(storeFile.getName()), storeFile, null, (keyStore, storePass) -> {
            String title = "导入失败";
            String msg = "可在秘钥库中查看";
            try {
                new Filej(storeFile).copyTo(newFile);
                title = "导入成功";
            } catch (IOException e) {
                e.printStackTrace();
                msg = e.getMessage();
            }
            iDialog.dismiss();
            Dialogs.global
                    .asConfirm()
                    .title(title)
                    .content(msg)
                    .positiveText()
                    .show();

        });
    }
}
