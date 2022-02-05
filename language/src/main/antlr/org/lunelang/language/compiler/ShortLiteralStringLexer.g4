lexer grammar ShortLiteralStringLexer;

@header{
package org.lunelang.language.compiler;
}

LiteralSegment:
    ~[\\\r\n]+;

BellEscape:
    '\\a';

BackspaceEscape:
    '\\b';

FormFeedEscape:
    '\\f';

NewlineEscape:
    '\\n';

CarriageReturnEscape:
    '\\r';

HorizontalTabEscape:
    '\\t';

VerticalTabEscape:
    '\\v';

BackslashEscape:
    '\\\\';

DoubleQuoteEscape:
    '\\"';

SingleQuoteEscape:
    '\\\'';

LineBreakEscape:
    '\\' ('\r' '\n'? | '\n' '\r'?);

WhitespaceEscape:
    '\\z' [\f\n\r\t\u{b}\p{Zl}\p{Zp}\p{Zs}]*;

fragment HexadecimalDigit:
    [0-9a-fA-F];

HexadecimalByteEscape:
    '\\x' HexadecimalDigit HexadecimalDigit;

fragment DecimalDigit:
    [0-9];

DecimalByteEscape:
    '\\d' DecimalDigit DecimalDigit? DecimalDigit?;

UnicodeScalarEscape:
    '\\u{' HexadecimalDigit+ '}';
