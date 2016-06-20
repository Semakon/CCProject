grammar Atlantis;

// Actual program
program : PROG VAR SEMI block
        ;

// Block with statements
block   : LBRACE (stat SEMI)+ RBRACE
        ;

// Statements
stat    : type? target ASS expr         #assStat
        | IF expr THEN block
          (ELSE block)?                 #ifStat
        | WHILE expr DO block           #whileStat
        | IN LPAR STR COMMA VAR RPAR    #inStat
        | OUT LPAR STR COMMA expr RPAR  #outStat
        ;

target  : VAR               #varTarget
        | VAR LSQ expr RSQ  #arrayTarget
        ;

// Expressions
expr    : not expr                      #notExpr
        | expr HAT expr                 #hatExpr
        | expr multOp expr              #multExpr
        | expr plusOp expr              #plusExpr
        | expr compOp expr              #compExpr
        | expr boolOp expr              #boolOpExpr
        | LPAR expr RPAR                #parExpr
        | VAR                           #varExpr
        | NUM                           #numExpr
        | STR                           #strExpr
        | BOOL                          #boolExpr
        | VAR LSQ expr RSQ              #indexExpr
        | LSQ (expr (COMMA expr)*)? RSQ #arrayExpr
        ;

// Negation
not     : NOT | MINUS;

// Multipliative operator
multOp  : MULT | SLASH;

// Addition operator
plusOp  : PLUS | MINUS;

// Comparative operator
compOp  : EQ | NE | GT | GE | LT | LE;

// Boolean operator
boolOp  : AND | OR;

// Data types
type    : BOOLEAN               #boolType
        | INT                   #intType
        | STRING                #stringType
        | type LSQ NUM RSQ      #arrayType
        ;

// Reserved keywords
PROG:       P R O G R A M;
FUNC:       F U N C T I O N;
BOOLEAN:    B O O L E A N;
INT:        I N T E G E R;
STRING:     S T R I N G;
WHILE:      W H I L E;
DO:         D O;
IF:         I F;
THEN:       T H E N;
ELSE:       E L S E;
TRUE:       T R U E;
FALSE:      F A L S E;
NOT:        N O T;
IN:         I N;
OUT:        O U T;
RETURN:     R E T U R N;

// Symbols
ASS:        '=';
EQ:         '==';
NE:         '<>';
GT:         '>';
GE:         '>=';
LT:         '<';
LE:         '<=';
COLON:      ':';
SEMI:       ';';
COMMA:      ',';
DOT:        '.';
DQUOTE:     '"';
SQUOTE:     '\'';
LBRACE:     '{';
RBRACE:     '}';
LSQ:        '[';
RSQ:        ']';
LPAR:       '(';
RPAR:       ')';
PLUS:       '+';
MINUS:      '-';
SLASH:      '/';
MULT:       '*';
HAT:        '^';
AND:        '&&';
OR:         '||';
BSLASH:     '\\';
HASH:       '#';

// Tokens with content
VAR:        LETTER (LETTER | DIGIT)*;
NUM:        DIGIT+;
STR:        DQUOTE .*? DQUOTE;
BOOL:       TRUE | FALSE;

fragment LETTER:    [A-Za-z];
fragment DIGIT:     [0-9];

// Skips
COMMENT:    HASH .*? HASH -> skip;
WS:         [ \t\n\r] -> skip;

// Letter fragments
fragment A: [aA];
fragment B: [bB];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment G: [gG];
fragment H: [hH];
fragment I: [iI];
fragment J: [jJ];
fragment K: [kK];
fragment L: [lL];
fragment M: [mM];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment Q: [qQ];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment X: [xX];
fragment Y: [yY];
fragment Z: [zZ];
