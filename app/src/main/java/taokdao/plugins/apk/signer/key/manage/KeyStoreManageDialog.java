package taokdao.plugins.apk.signer.key.manage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import taokdao.api.main.IMainContext;
import taokdao.api.plugin.bean.PluginManifest;
import taokdao.api.setting.theme.ThemeParts;
import taokdao.plugins.apk.signer.R;
import taokdao.plugins.apk.signer.key.bean.KeyAliasItem;
import taokdao.plugins.apk.signer.key.bean.KeyStoreItem;
import taokdao.plugins.apk.signer.key.bean.KeyStoreType;
import taokdao.plugins.apk.signer.key.generate.KeyGenerator;
import taokdao.plugins.apk.signer.key.generate.KeyParam;
import taokdao.plugins.apk.signer.key.load.AliasLoader;
import taokdao.plugins.apk.signer.key.load.KeyPassManager;
import taokdao.plugins.apk.signer.key.load.KeyStoreManager;
import taokdao.plugins.apk.signer.key.load.StoreLoader;
import taokdao.plugins.apk.signer.key.load.StorePassManager;
import taokdao.plugins.apk.signer.key.manage.adapter.ItemListAdapter;
import taokdao.plugins.apk.signer.key.manage.adapter.PairTextAdapter;
import taokdao.plugins.apk.signer.key.manage.adapter.PairTextItem;
import taokdao.plugins.apk.signer.key.manage.adapter.SimpleAdapterViewOnItemSelectedListener;
import taokdao.plugins.apk.signer.key.manage.adapter.SpinnerUtils;
import tiiehenry.android.ui.dialogs.api.IDialog;
import tiiehenry.android.ui.dialogs.api.strategy.Dialogs;
import tiiehenry.android.ui.dialogs.api.strategy.input.IInputDialog;

public class KeyStoreManageDialog {
    private final IMainContext main;
    private final File keysDir;
    private final Context pluginContext;
    private final KeyPassManager keyPassManager;

    public KeyStoreManageDialog(IMainContext iMainContext, PluginManifest pluginManifest, Context pluginContext) {
        this.main = iMainContext;
        this.pluginContext = pluginContext;
        keysDir = KeyStoreManager.getKeysDir(iMainContext, pluginManifest);

        keyPassManager = KeyPassManager.from(iMainContext, pluginManifest);
    }

    private void showStoreManageDialog(List<KeyStoreItem> keyStoreItemList) {
        List<CharSequence> nameList = new ArrayList<>();
        for (KeyStoreItem keyStoreItem : keyStoreItemList) {
            nameList.add(keyStoreItem.storeFile.getName());
        }

        ItemListAdapter adapter = new ItemListAdapter(pluginContext,main, nameList);
        adapter.setOnItemClickListener((itemView, item, position) -> prepareShowStoreInfo(keyStoreItemList.get(position)));

        IDialog dialog = Dialogs.global
                .asList()
                .typeCustom()
                .title("KeyStore Manage")
                .adapter(adapter)
//                .itemsCallback(
//                        (dialog,  position, text) ->
//                                prepareShowStoreInfo(keyStoreItemList.get(position))
//                )
//                .itemsLongCallback(
//                        (dialog,  position, text) ->
//                                manageKeyStore(keyStoreItemList.get(position), dialog)
//                )
                .neutralText("导入")
                .onNeutral(
                        this::importKeyStore
                )
                .positiveText()
                .onPositive(IDialog::dismiss)
                .autoDismiss(false)
                .show();
        adapter.setOnItemLongClickListener((itemView, item, position) -> manageKeyStore(keyStoreItemList.get(position), dialog));
    }


    private void prepareShowStoreInfo(KeyStoreItem keyStoreItem) {
        StorePassManager storePassManager = keyPassManager.getStorePassManager(keyStoreItem.storeFile.getName());

        if (keyStoreItem.keyStore != null) {
            showStoreInfoDialog(keyStoreItem, storePassManager);
        } else {
            showStorePassInputDialog(storePassManager, keyStoreItem.storeFile, keyStoreItem.storePass, (keyStore, storePass) -> {
                keyStoreItem.keyStore = keyStore;
                keyStoreItem.storePass = storePass;
                showStoreInfoDialog(keyStoreItem, storePassManager);
            });
        }
    }

    private void showStoreInfoDialog(KeyStoreItem keyStoreItem, StorePassManager storePassManager) {
        LayoutInflater layoutInflater = LayoutInflater.from(pluginContext);
        PairTextAdapter adapter = new PairTextAdapter(layoutInflater, main.getThemeManager().getThemeColors(ThemeParts.CONTENT), main.getThemeManager().getThemeDrawables(ThemeParts.CONTENT), main);
        adapter.setOnItemClickListener((itemView, item, position) -> item.onClick(itemView));
        adapter.setOnItemLongClickListener((itemView, item, position) -> item.onLongClick(itemView));
        adapter.add(new PairTextItem("文件名", keyStoreItem.storeFile.getName()) {
            @Override
            public void onClick(View v) {
                copyToClipboard(message);
            }
        });
        adapter.add(new PairTextItem("路径", keyStoreItem.storeFile.getAbsolutePath()) {
            @Override
            public void onClick(View v) {
                copyToClipboard(message);
            }
        });
        KeyStoreType keyType = KeyStoreType.getFromFile(keyStoreItem.storeFile);
        String name = "UnKnow";
        if (keyType != null)
            name = keyType.name;
        adapter.add(new PairTextItem("类型", name) {
            @Override
            public void onClick(View v) {
                copyToClipboard(message);
            }
        });
        StringBuilder s = new StringBuilder("*");
        for (int i = 0; i < keyStoreItem.storePass.length(); i++) {
            s.append("*");
        }
        if (s.toString().isEmpty())
            s.append("无");
        adapter.add(new PairTextItem("密码", s.toString() + "(点击修改)") {
            @Override
            public void onClick(View v) {
                showStorePassInputDialog(storePassManager, keyStoreItem.storeFile, keyStoreItem.storePass, (keyStore, storePass) -> {
                    keyStoreItem.keyStore = keyStore;
                    keyStoreItem.storePass = storePass;
                });
            }
        });
        String aliasText = String.valueOf(keyStoreItem.keyAliasItems.size());
        if (keyStoreItem.keyAliasItems.size() == 0) {
            aliasText = "密码错误";
        }
        adapter.add(new PairTextItem("别名数", aliasText + "(点击查看)") {
            @Override
            public void onClick(View v) {
                showAliasListDialog(keyStoreItem, storePassManager);
            }
        });
        Dialogs.global
                .asList()
                .typeCustom()
                .title(keyStoreItem.storeFile.getName())
                .adapter(adapter)
                .positiveText()
                .show();
    }


    public static void showStorePassInputDialog(StorePassManager storePassManager, File storeFile, String storePass, InputResults<KeyStore, String> onSuccess) {
        Dialogs.global
                .asInput()
                .title(storeFile.getName())
                .input("Enter Password", storePass,
                        (dialog, input) -> {
                            KeyStore keyStore = checkKeyStorePass(storePassManager, storeFile, input.toString(), dialog);
                            if (keyStore != null) {
                                dialog.dismiss();
                                onSuccess.onCall(keyStore, input.toString());
                            }
                        }
                )
                .autoDismiss(false)
                .negativeText()
                .show();
    }

    public static KeyStore checkKeyStorePass(StorePassManager storePassManager, File storeFile, String storePass, IInputDialog dialog) {
        try {
            StoreLoader storeLoader = new StoreLoader(storeFile, storePassManager).load(storePass);
            storePassManager.setStorePass(storePass);
            return storeLoader.keyStore;
        } catch (KeyStoreException | IOException e) {
            e.printStackTrace();
        }
        dialog.setInputError("UnCorrect Password!");
        return null;
    }

    private void showAliasListDialog(KeyStoreItem keyStoreItem, StorePassManager storePassManager) {
        ArrayList<CharSequence> aliases = new ArrayList<>();
        for (KeyAliasItem keyAliasItem : keyStoreItem.keyAliasItems) {
            aliases.add(keyAliasItem.alias);
        }

        ItemListAdapter adapter = new ItemListAdapter(pluginContext, main, aliases);
        adapter.setOnItemClickListener((itemView, item, position) ->            prepareShowAlias(keyStoreItem.keyStore, keyStoreItem.keyAliasItems.get(position), storePassManager));


        Dialogs.global
                .asList()
                .typeCustom()
                .title(keyStoreItem.storeFile.getName())
                .adapter(adapter)
                .autoDismiss(false)
                .positiveText()
                .onPositive(IDialog::dismiss)
                .show();
        adapter.setOnItemLongClickListener((itemView, item, position) -> manageKeyStoreAlias(keyStoreItem, keyStoreItem.keyAliasItems.get(position)));
    }

    private void prepareShowAlias(KeyStore keyStore, KeyAliasItem keyAliasItem, StorePassManager storePassManager) {
        if (keyAliasItem.passCorrect) {
            showAliasInfoDialog(keyStore, keyAliasItem, storePassManager);
        } else {
            showAliasPassInputDialog(storePassManager, keyStore, keyAliasItem, () -> {
                showAliasInfoDialog(keyStore, keyAliasItem, storePassManager);
            });
        }
    }

    private void showAliasInfoDialog(KeyStore keyStore, KeyAliasItem keyAliasItem, StorePassManager storePassManager) {
        LayoutInflater layoutInflater = LayoutInflater.from(pluginContext);
        PairTextAdapter adapter = new PairTextAdapter(layoutInflater, main.getThemeManager().getThemeColors(ThemeParts.CONTENT), main.getThemeManager().getThemeDrawables(ThemeParts.CONTENT), main);
        adapter.setOnItemClickListener((itemView, item, position) -> item.onClick(itemView));
        adapter.setOnItemLongClickListener((itemView, item, position) -> item.onLongClick(itemView));
        adapter.add(new PairTextItem("别名", keyAliasItem.alias) {
            @Override
            public void onClick(View v) {
                copyToClipboard(message);
            }
        });
        StringBuilder s = new StringBuilder("*");
        for (int i = 0; i < keyAliasItem.keyPass.length(); i++) {
            s.append("*");
        }
        if (s.toString().isEmpty())
            s.append("无");
        adapter.add(new PairTextItem("密码", s.toString() + "(点击修改)") {
            @Override
            public void onClick(View v) {
                showAliasPassInputDialog(storePassManager, keyStore, keyAliasItem, () -> {

                });
            }
        });

        Dialogs.global
                .asList()
                .typeCustom()
                .title("Alias Info")
                .adapter(adapter)
                .positiveText()
                .show();
    }

    public void showAliasPassInputDialog(StorePassManager storePassManager, KeyStore keyStore, KeyAliasItem keyAliasItem, Runnable onSuccess) {
        Dialogs.global
                .asInput()
                .title(keyAliasItem.alias)
                .input("Enter Password", keyAliasItem.keyPass,
                        (dialog, input) -> {
                            boolean correct = checkAliasPassInput(storePassManager, keyStore, keyAliasItem, input.toString(), dialog);
                            if (correct) {
                                dialog.dismiss();
                                onSuccess.run();
                            }
                        }
                )
                .negativeText()
                .autoDismiss(false)
                .show();
    }

    private boolean checkAliasPassInput(StorePassManager storePassManager, KeyStore keyStore, KeyAliasItem alias, String aliasPass, IInputDialog dialog) {
        try {
            AliasLoader aliasLoader = new AliasLoader(keyStore, alias).load(aliasPass);
            alias.keyPass = aliasPass;
            storePassManager.setAliasPass(alias.alias, aliasPass);
            return true;
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        dialog.setInputError("UnCorrect Password!");
        return false;
    }

    private void copyToClipboard(String content) {
        ClipboardManager clipboardManager = (ClipboardManager) pluginContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("TaoKDao-Plugin_KeyStoreManage", content);
        clipboardManager.setPrimaryClip(mClipData);
        main.notify("已复制");
    }

    private boolean manageKeyStore(KeyStoreItem keyStoreItem, IDialog manageDialog) {
        StorePassManager storePassManager = keyPassManager.getStorePassManager(keyStoreItem.storeFile.getName());
        List<Pair<String, Runnable>> itemList = new ArrayList<>();
        List<CharSequence> items = new ArrayList<>();
        itemList.add(new Pair<>("重命名", () -> {
            showRenameDialog(keyStoreItem, manageDialog, storePassManager);
        }));
        itemList.add(new Pair<>("删除", () -> {
            requestDeleteFile(keyStoreItem.storeFile, storePassManager);

            manageDialog.dismiss();
        }));
        itemList.add(new Pair<>("导出", () -> {
            shareFile(keyStoreItem.storeFile);
            manageDialog.dismiss();
        }));
        for (Pair<String, Runnable> stringRunnablePair : itemList) {
            items.add(stringRunnablePair.first);
        }
        Dialogs.global
                .asList()
//                .typeCustom()
//                .adapter(adapter)
                .typeRegular()
                .title(keyStoreItem.storeFile.getName())
                .items(items)
                .itemsCallback(
                        (dialog, position, text) -> {
                            itemList.get(position).second.run();
                        }
                )
                .negativeText()
                .show();
        return true;
    }

    private void requestDeleteFile(File storeFile, StorePassManager storePassManager) {
        Dialogs.global
                .asConfirm()
                .title("删除文件")
                .content(storeFile.getAbsolutePath())
                .positiveText()
                .onPositive(iDialog -> {
                    boolean deleted = storeFile.delete();
                    if (deleted) {
                        storePassManager.clearStore();
                    } else {
                        main.notify("删除失败");
                    }
                })
                .negativeText()
                .show();
    }

    private void shareFile(File storeFile) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType("*/*");
        Uri contentUri = main.getFileProvider().getFileUri(storeFile);
        intentShareFile.putExtra(Intent.EXTRA_STREAM, contentUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "发送文件...");
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "发送文件...");
        pluginContext.startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    private void showRenameDialog(KeyStoreItem keyStoreItem, IDialog manageDialog, StorePassManager storePassManager) {
        String oldName = keyStoreItem.storeFile.getName();
        Dialogs.global
                .asInput()
                .title(oldName)
                .input("Enter a new name", oldName, (dialog, input) -> {
                    boolean success = false;
                    try {
                        File newFile = new File(keyStoreItem.storeFile.getParentFile(), input.toString());
                        success = keyStoreItem.storeFile.renameTo(newFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (success) {
                        storePassManager.renameTo(input.toString());

                        main.notify("重命名成功");
                    } else {
                        main.notify("重命名失败");
                    }
                    manageDialog.dismiss();
                    dialog.dismiss();
                })
                .autoDismiss(false)
                .negativeText()
                .show();
    }

    private void importKeyStore(IDialog manageDialog) {
        Dialogs.global
                .asConfirm()
                .title("导入秘钥库")
                .content("请在文件管理器中打开秘钥文件")
                .neutralText("新建秘钥")
                .onNeutral(iDialog -> {
                    manageDialog.dismiss();
                    showCreateKeyStoreDialog();
                })
                .positiveText()
                .show();
    }

    private void showCreateKeyStoreDialog() {
        KeyParam keyParam = new KeyParam();
        LayoutInflater layoutInflater = LayoutInflater.from(pluginContext);
        View layout = layoutInflater.inflate(R.layout.keystore_manage_create, null);

        Spinner sp_store_type = layout.findViewById(R.id.sp_store_type);
        String[] store_types = {"Keystore(JKS)", "Keystore(BKS)", "Keystore(PKCS12)", "pk8+x509.pem"};
        ArrayAdapter<String> storeTypeAdapter = SpinnerUtils.newSpinnerAdapter(pluginContext, store_types);
        sp_store_type.setAdapter(storeTypeAdapter);
        sp_store_type.setOnItemSelectedListener((SimpleAdapterViewOnItemSelectedListener) (parent, view, position, id) -> {
            switch (position) {
                case 0: {
                    keyParam.keyStoreType = KeyStoreType.KEYSTORE_JKS;
                    break;
                }
                case 1: {
                    keyParam.keyStoreType = KeyStoreType.KEYSTORE_BKS;
                    break;
                }
                case 2: {
                    keyParam.keyStoreType = KeyStoreType.KEYSTORE_PKCS12;
                    break;
                }
                default: {
                    keyParam.keyStoreType = KeyStoreType.PK8_X509_PEM;
                    break;
                }
            }
        });

        EditText edit_store_name = layout.findViewById(R.id.edit_store_name);
        EditText edit_store_pass = layout.findViewById(R.id.edit_store_pass);
        EditText edit_alias_name = layout.findViewById(R.id.edit_alias_name);
        Spinner sp_alias_size = layout.findViewById(R.id.sp_alias_size);
        String[] size_list = {"1024", "2048", "3072"};
        ArrayAdapter<String> storeSizeAdapter = SpinnerUtils.newSpinnerAdapter(pluginContext, size_list);
        sp_alias_size.setAdapter(storeSizeAdapter);
        sp_alias_size.setSelection(1);
        sp_alias_size.setOnItemSelectedListener((SimpleAdapterViewOnItemSelectedListener) (parent, view, position, id) -> {
            keyParam.keySize = Integer.parseInt(size_list[position]);
        });

        Spinner sp_alias_type = layout.findViewById(R.id.sp_alias_type);
        String[] alias_type_list = {"RSA"};
        ArrayAdapter<String> aliasTypeAdapter = SpinnerUtils.newSpinnerAdapter(pluginContext, alias_type_list);
        sp_alias_type.setAdapter(aliasTypeAdapter);
        sp_alias_type.setOnItemSelectedListener((SimpleAdapterViewOnItemSelectedListener) (parent, view, position, id) -> {
            keyParam.keyAliasType = alias_type_list[position];
        });
        EditText edit_alias_pass = layout.findViewById(R.id.edit_alias_pass);
        LinearLayout ll_more = layout.findViewById(R.id.ll_more);
        CheckBox cb_show_more = layout.findViewById(R.id.cb_show_more);
        cb_show_more.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ll_more.setVisibility(View.VISIBLE);
            } else {
                ll_more.setVisibility(View.GONE);
            }
        });
        EditText edit_validate = layout.findViewById(R.id.edit_validate);
        Spinner sp_alias_agr = layout.findViewById(R.id.sp_alias_agr);
        String[] alias_agr_list = {"SHA1withRSA", "SHA224withRSA", "SHA256withRSA", "SHA384withRSA", "SHA512withRSA"};
        ArrayAdapter<String> aliasAgrAdapter = SpinnerUtils.newSpinnerAdapter(pluginContext, alias_agr_list);
        sp_alias_agr.setAdapter(aliasAgrAdapter);
        sp_alias_agr.setSelection(alias_agr_list.length - 1);
        sp_alias_agr.setOnItemSelectedListener((SimpleAdapterViewOnItemSelectedListener) (parent, view, position, id) -> {
            keyParam.keyAliasAgr = alias_agr_list[position];
        });
        EditText edit_commonName = layout.findViewById(R.id.edit_commonName);
        EditText edit_organizationUnit = layout.findViewById(R.id.edit_organizationUnit);
        EditText edit_organizationName = layout.findViewById(R.id.edit_organizationName);
        EditText edit_localityName = layout.findViewById(R.id.edit_localityName);
        EditText edit_stateName = layout.findViewById(R.id.edit_stateName);
        EditText edit_country = layout.findViewById(R.id.edit_country);

        Dialogs.global
                .asCustom()
                .title("新建秘钥")
                .customView(layout, true)
//                .neutralText("证书设置")
//                .onNeutral(
//                        iDialog -> {
//                            if (ll_more.isShown()) {
//                                ll_more.setVisibility(View.GONE);
//                            } else {
//                                ll_more.setVisibility(View.VISIBLE);
//                            }
//                        }
//                )
                .negativeText()
                .positiveText()
                .onPositive(iDialog -> {
                    if (edit_store_name.getText().toString().isEmpty()) {
                        edit_store_name.setError("文件名不能为空");
                        return;
                    }
                    File storeFile;
                    switch (keyParam.keyStoreType) {
                        case KEYSTORE_PKCS12:
                            storeFile = new File(keysDir, edit_store_name.getText().toString() + ".pk8");
                            break;
                        case PK8_X509_PEM:
                            storeFile = new File(keysDir, edit_store_name.getText().toString() + ".pk8");
                            keyParam.certPath = new File(keysDir, edit_store_name.getText().toString() + ".x509.pem").getAbsolutePath();
                            break;
                        default:
                            storeFile = new File(keysDir, edit_store_name.getText().toString() + "." + keyParam.keyStoreType.suffixList.get(0));
                    }
                    if (storeFile.exists()) {
                        edit_store_name.setError("文件名已存在");
                        return;
                    }
                    keyParam.storePath = storeFile.getAbsolutePath();
                    keyParam.storePass = edit_store_pass.getText().toString();
                    if (keyParam.storePass.isEmpty()) {
                        edit_store_pass.setError("文件名不能为空");
                        return;
                    }
                    keyParam.keyAlias = edit_alias_name.getText().toString();
                    if (keyParam.keyAlias.isEmpty()) {
                        edit_alias_name.setError("文件名不能为空");
                        return;
                    }
                    keyParam.keyPass = edit_alias_pass.getText().toString();
                    if (keyParam.keyPass.isEmpty()) {
                        edit_alias_pass.setError("密码不能为空");
                        return;
                    }
                    if (edit_validate.getText().toString().isEmpty()) {
                        edit_validate.setError("有效期不能为空");
                        return;
                    }
                    keyParam.validity = Integer.parseInt(edit_validate.getText().toString());
                    String commonName = edit_commonName.getText().toString();
                    if (commonName.isEmpty()) {
                        edit_commonName.setError("姓名不能为空");
                        return;
                    }
                    keyParam.userInfo = new KeyParam.UserInfo(
                            commonName,
                            edit_organizationUnit.getText().toString(),
                            edit_organizationName.getText().toString(),
                            edit_localityName.getText().toString(),
                            edit_stateName.getText().toString(),
                            edit_country.getText().toString()
                    );
                    try {
                        new KeyGenerator(keyParam).generate();
                        StorePassManager storePassManager = keyPassManager.getStorePassManager(storeFile.getName());
                        storePassManager.setStorePass(keyParam.storePass);
                        storePassManager.setAliasPass(keyParam.keyAlias, keyParam.keyPass);
                        main.notify("创建成功");
                    } catch (NoSuchAlgorithmException | InvalidKeyException | CertificateException | IOException | NoSuchProviderException | SignatureException | KeyStoreException e) {
                        e.printStackTrace();
                        main.notify(e.getMessage());
                    }
                    iDialog.dismiss();
                })
                .autoDismiss(false)
                .show();
    }


    private boolean manageKeyStoreAlias(KeyStoreItem keyStoreItem, KeyAliasItem keyAliasItem) {

        return false;
    }

    public void show() {
        Dialogs.global
                .asLoading()
                .addLoadingTask("加载秘钥中", iLoadingDialog -> {
                    Log.e(getClass().getSimpleName(), "加载秘钥中: ");
                    List<KeyStoreItem> keyStoreItemList = KeyStoreManager.loadItemListFromDir(keyPassManager, keysDir);
                    main.runOnUIThread(() -> showStoreManageDialog(keyStoreItemList));
                })
                .minDisplayTime(100)
                .show();
    }
}
