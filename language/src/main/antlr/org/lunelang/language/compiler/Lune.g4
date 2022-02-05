grammar Lune;

@header {
package org.lunelang.language.compiler;
}

And: 'and';
Break: 'break';
Do: 'do';
Else: 'else';
Elseif: 'elseif';
End: 'end';
False: 'false';
For: 'for';
Function: 'function';
Goto: 'goto';
If: 'if';
In: 'in';
Local: 'local';
Nil: 'nil';
Not: 'not';
Or: 'or';
Repeat: 'repeat';
Return: 'return';
Then: 'then';
True: 'true';
Until: 'until';
While: 'while';

Plus: '+';
Minus: '-';
Star: '*';
Slash: '/';
Percent: '%';
Caret: '^';
Pound: '#';
Ampersand: '&';
Tilde: '~';
Pipe: '|';
LAngle2: '<<';
RAngle2: '>>';
Slash2: '//';
Equals2: '==';
TildeEquals: '~=';
LAngleEquals: '<=';
RAngleEquals: '>=';
LAngle: '<';
RAngle: '>';
Equals: '=';
LParen: '(';
RParen: ')';
LCurly: '{';
RCurly: '}';
LSquare: '[';
RSquare: ']';
Colon2: '::';
Semi: ';';
Colon: ':';
Comma: ',';
Dot: '.';
Dot2: '..';
Dot3: '...';

Whitespace:
    [ \f\n\r\t\u{b}] -> skip;

fragment NonDigitNameCharacter:
    [\p{Ll}\p{Lm}\p{Lo}\p{Lt}\p{Lu}\p{Mc}\p{Me}\p{Mn}\p{Nl}\p{Pc}];

Name:
    NonDigitNameCharacter (NonDigitNameCharacter | [\p{Nd}\p{No}])*;

fragment ShortLiteralStringEscape:
    [abfnrtv\\"']
  | '\r' '\n'?
  | '\n' '\r'?
  | 'z' Whitespace*
  | 'x' HexadecimalDigit HexadecimalDigit
  | 'd' DecimalDigit DecimalDigit? DecimalDigit?
  | 'u{' HexadecimalDigit+ '}';

ShortLiteralString:
    '"' (~[\\"\r\n] | '\\' ShortLiteralStringEscape)* '"'
  | '\'' (~[\\'\r\n] | '\\' ShortLiteralStringEscape)* '\'';

fragment LongBracketsBody:
    '=' LongBracketsBody '='
  | '[' .*? ']';

fragment LongBrackets:
    '[' LongBracketsBody ']';

LongLiteralString:
    LongBrackets;

LongComment:
    '--' LongBrackets -> skip;

fragment ShortCommentBody:
    EOF
  | ~'[' ~[\r\n]*
  | '[' '='* ~[=[] ~[\r\n]*;

ShortComment:
    '--' ShortCommentBody -> skip;

fragment DecimalDigit:
    [0-9];

fragment HexadecimalDigit:
    [0-9a-fA-F];

fragment HexadecimalPrefix:
    '0' [xX];

fragment DecimalExponent:
    [eE] [+\-] DecimalDigit+;

fragment HexadecimalExponent:
    [pP] [+\-] DecimalDigit+;

DecimalInteger:
    DecimalDigit+;

DecimalFloat:
    DecimalDigit+ ('.' DecimalDigit*)? DecimalExponent?
  | '.' DecimalDigit+ DecimalExponent?;

HexadecimalInteger:
    HexadecimalPrefix HexadecimalDigit+;

HexadecimalFloat:
    HexadecimalPrefix HexadecimalDigit+ ('.' HexadecimalDigit*)? HexadecimalExponent?
  | HexadecimalPrefix '.' HexadecimalDigit+ HexadecimalExponent?;

block:
    (statements+=statement)* (ret='return' (returnValues+=expression (',' returnValues+=expression)*)? ';'?)?;

statement:
    ';' #EmptyStatement
  | lhs+=variable (',' lhs+=variable)* '=' rhs+=expression (',' rhs+=expression)* #AssignmentStatement
  | receiver=prefixExpression (':' method=Name)? args=arguments #FunctionCallStatement
  | '::' name=Name '::' #LabelStatement
  | 'break' #BreakStatement
  | 'goto' target=Name #GotoStatement
  | 'do' body=block 'end' #BlockStatement
  | 'while' condition=expression 'do' body=block 'end' #WhileStatement
  | 'repeat' body=block 'until' condition=expression #RepeatStatement
  | 'if' conditions+=expression 'then' bodies+=block ('elseif' conditions+=expression 'then' bodies+=block)* ('else' alternate=block)? 'end' #IfStatement
  | 'for' name=Name '=' start=expression ',' stop=expression (',' step=expression)? 'do' body=block 'end' #NumericForStatement
  | 'for' lhs+=Name (',' lhs+=Name)* 'in' rhs+=expression (',' rhs+=expression)* 'do' body=block 'end' #GenericForStatement
  | 'function' name=Name ('.' memberPath+=Name)* (':' method=Name)? body=functionBody #FunctionStatement
  | 'local' 'function' name=Name body=functionBody #LocalFunctionStatement
  | 'local' lhs+=attributedName (',' lhs+=attributedName)* '=' rhs+=expression (',' rhs+=expression)* #LocalStatement;

attributedName:
    name=Name ('<' attribute=Name '>')?;

variable:
    name=Name #NamedVariable
  | receiver=prefixExpression '[' key=expression ']' #IndexedVariable
  | receiver=prefixExpression '.' key=Name #MemberVariable;

prefixExpression:
    name=Name #NameExpression
  | '(' wrapped=expression ')' #ParenthesizedExpression
  | receiver=prefixExpression '[' key=expression ']' #IndexExpression
  | receiver=prefixExpression '.' key=Name #MemberExpression
  | receiver=prefixExpression (':' method=Name)? args=arguments #CallExpression;

expression:
    'nil' #NilExpression
  | 'false' #FalseExpression
  | 'true' #TrueExpression
  | token=DecimalInteger #DecimalIntegerExpression
  | token=HexadecimalInteger #HexadecimalIntegerExpression
  | token=DecimalFloat #DecimalFloatExpression
  | token=HexadecimalFloat #HexadecimalFloatExpression
  | token=ShortLiteralString #ShortLiteralStringExpression
  | token=LongLiteralString #LongLiteralStringExpression
  | '...' #VarargsExpression
  | table=tableConstructor #TableExpression
  | 'function' body=functionBody #FunctionExpression
  | wrapped=prefixExpression #PrefixExpressionExpression
  | <assoc=right> lhs=expression '^' rhs=expression #PowerExpression
  | operator=('not' | '#' | '-' | '~') operand=expression #PrefixOperatorExpression
  | lhs=expression operator=('*' | '/' | '//' | '%') rhs=expression #MultiplyDivideModuloExpression
  | lhs=expression operator=('+' | '-') rhs=expression #AddSubtractExpression
  | <assoc=right> lhs=expression '..' rhs=expression #ConcatenateExpression
  | lhs=expression operator=('<<' | '>>') rhs=expression #ShiftExpression
  | lhs=expression '&' rhs=expression #BitwiseAndExpression
  | lhs=expression '~' rhs=expression #BitwiseXOrExpression
  | lhs=expression '|' rhs=expression #BitwiseOrExpression
  | lhs=expression operator=('<' | '>' | '<=' | '>=' | '~=' | '==') rhs=expression #ComparisonExpression
  | lhs=expression 'and' rhs=expression #AndExpression
  | lhs=expression 'or' rhs=expression #OrExpression;

arguments:
    '(' (values+=expression (',' values+=expression)*)? ')' #ExpressionListArguments
  | table=tableConstructor #TableArguments
  | token=ShortLiteralString #ShortLiteralStringArguments
  | token=LongLiteralString #LongLiteralStringArguments;

functionBody:
    '(' (formals+=Name (',' formals+=Name)* (',' varargs='...')? | varargs='...')? ')' body=block 'end';

tableConstructor:
    '{' (fields+=field ((',' | ';') fields+=field)* (',' | ';')?)? '}';

field:
    '[' key=expression ']' '=' value=expression #IndexedField
  | key=Name '=' value=expression #NamedField
  | value=expression #OrdinalField;
