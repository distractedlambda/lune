package org.lunelang.language;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;

@TruffleLanguage.Registration(id = "lune", name = "Lune")
public final class LuneLanguage extends TruffleLanguage<LuneContext> {
    private static final LanguageReference<LuneLanguage> REFERENCE = LanguageReference.create(LuneLanguage.class);

    @Override
    protected LuneContext createContext(Env env) {
        return new LuneContext();
    }

    public static LuneLanguage get(Node node) {
        return REFERENCE.get(node);
    }
}
