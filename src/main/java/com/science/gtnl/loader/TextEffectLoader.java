package com.science.gtnl.loader;

import com.gtnllib.text.TextEffectFormat;
import com.science.gtnl.ScienceNotLeisure;

public class TextEffectLoader implements IContentLoader {

    @Override
    public String name() {
        return "texteffect";
    }

    @Override
    public void preInit() {
        TextEffectFormat.legacyNamespace(ScienceNotLeisure.MODID);
    }
}
