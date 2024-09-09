package com.ashkiano.signitem;

import org.bukkit.configuration.file.YamlConfiguration;

public class TranslatableProvider {
    public String pluginBootThanks;
    public String onlyPlayerCanUseCommand;
    public String signItemCanNotBeAir;
    public String unsignItemCanNotBeAir;
    public String itemAlreadySigned;
    public String itemAlreadyUnsigned;
    public String itemUnsignByWrongPlayer;
    public String itemSignedSuccessfully;
    public String itemUnsignedSuccessfully;

    public TranslatableProvider(YamlConfiguration languageFile) {
        pluginBootThanks = languageFile.getString("pluginBootThanks");
        onlyPlayerCanUseCommand = languageFile.getString("onlyPlayerCanUseCommand");
        signItemCanNotBeAir = languageFile.getString("signItemCanNotBeAir");
        unsignItemCanNotBeAir = languageFile.getString("unsignItemCanNotBeAir");
        itemAlreadySigned = languageFile.getString("itemAlreadySigned");
        itemAlreadyUnsigned = languageFile.getString("itemAlreadyUnsigned");
        itemUnsignByWrongPlayer = languageFile.getString("itemUnsignByWrongPlayer");
        itemSignedSuccessfully = languageFile.getString("itemSignedSuccessfully");
        itemUnsignedSuccessfully = languageFile.getString("itemUnsignedSuccessfully");
    }
}
