package org.lunelang.language.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.lunelang.language.LuneContext;
import org.lunelang.language.LuneLanguage;

@NodeInfo(language = "Lune")
@TypeSystemReference(LuneTypeSystem.class)
public abstract class LuneNode extends Node {
    public final LuneContext getContext() {
        return LuneContext.get(this);
    }

    public final LuneLanguage getLanguage() {
        return LuneLanguage.get(this);
    }
}
