package org.lunelang.language.nodes.closure;

import org.lunelang.language.nodes.DestinationNode;
import org.lunelang.language.nodes.SourceNode;

public abstract class ClosureCaptureDestinationNode extends DestinationNode {
    @Child private SourceNode closureSourceNode;
}
