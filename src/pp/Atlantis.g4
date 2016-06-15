grammar Atlantis;

// Actual program
program : PROG VAR SEMI function* block
        ;

function: FUNC type VAR pars? block SEMI
        ;

pars    : LPAR type COLON VAR
          (COMMA type COLON VAR)* RPAR
        ;

// Block with statements
block   : LBRACE (stat SEMI)+ RBRACE
        ;

// Statements
stat    : type VAR                      #declStat
        | VAR ASS expr                  #assStat
        | IF expr THEN block
          (ELSE block)?                 #ifStat
        | WHILE expr DO block           #whileStat
        | RETURN expr                   #retStat
        | IN LPAR STR COMMA VAR RPAR    #inStat
        | OUT LPAR STR COMMA expr RPAR  #outStat
        | VAR args                      #callStat
        ;

// Expressions
expr    : not expr          #notExpr
        | expr HAT expr     #hatExpr
        | expr multOp expr  #multExpr
        | expr plusOp expr  #plusExpr
        | expr compOp expr  #compExpr
        | expr boolOp expr  #boolOpExpr
        | LPAR expr RPAR    #parExpr
        | VAR args          #callExpr
        | VAR               #varExpr
        | NUM               #numExpr
        | BOOL              #boolExpr
        ;

args    : LPAR (expr (COMMA expr)*)? RPAR
        ;

// Negation
not     : NOT | MINUS;

// Multipliative operator
multOp  : MULT | SLASH;

// Addition operator
plusOp  : PLUS | MINUS;

// Comparative operator
compOp  : EQ | NE | GT | GE | LT | LE;

// Boolean operator TODO: add xor ?
boolOp  : AND | OR;

// Data types
type    : BOOL | INT
        | type RBRACK NUM LBRACK
        ;

// Reserved keywords
PROG:       P R O G R A M;
FUNC:       F U N C T I O N;
BOOLEAN:    B O O L E A N;
INT:        I N T E G E R;
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
LBRACK:     '[';
RBRACK:     ']';
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
