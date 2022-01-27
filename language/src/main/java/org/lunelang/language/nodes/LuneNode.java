package org.lunelang.language.nodes;

import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.source.SourceSection;
import org.lunelang.language.LuneContext;
import org.lunelang.language.LuneLanguage;

@NodeInfo(language = "Lune")
@TypeSystemReference(LuneTypeSystem.class)
public abstract class LuneNode extends Node {
    private SourceSection sourceSection;

    @Override
    public final SourceSection getSourceSection() {
        return sourceSection;
    }

    public final void setSourceSection(SourceSection sourceSection) {
        this.sourceSection = sourceSection;
    }

    public final LuneContext getContext() {
        return LuneContext.get(this);
    }

    public final LuneLanguage getLanguage() {
        return LuneLanguage.get(this);
    }
}
