grammar Atlantis;

/* Actual program */
program : PROG VAR SEMI block
        ;

/* Block with statements */
block   : LBRACE (stat SEMI)+ RBRACE
        ;

/* Statements */
stat    : target ASS expr                   #assStat
        | GLOBAL? type target (ASS expr)?   #declStat
        | IF expr THEN block
          (ELSE block)?                     #ifStat
        | WHILE expr DO block               #whileStat
        | FORK block                        #forkStat
        | JOIN                              #joinStat
        | LOCK target block                 #lockStat
        ;

/* Target variable */
target  : VAR                               #varTarget
        ;

/* Expressions */
expr    : not expr                          #notExpr
        | expr multOp expr                  #multExpr
        | expr plusOp expr                  #plusExpr
        | expr compOp expr                  #compExpr
        | expr boolOp expr                  #boolOpExpr
        | LPAR expr RPAR                    #parExpr
        | VAR                               #varExpr
        | NUM                               #numExpr
        | FALSE                             #falseExpr
        | TRUE                              #trueExpr
        ;

/* Negation */
not     : NOT | MINUS;

/* Multipliative operator */
multOp  : MULT | SLASH;

/* Addition operator */
plusOp  : PLUS | MINUS;

/* Comparative operator */
compOp  : EQ | NE | GT | GE | LT | LE;

/* Boolean operator */
boolOp  : AND | OR;

/* Data types */
type    : BOOLEAN                           #boolType
        | INT                               #intType
        ;

/* Reserved keywords */
PROG:       P R O G R A M;
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
FORK:       F O R K;
JOIN:       J O I N;
GLOBAL:     G L O B A L;
LOCK:       L O C K;

/* Symbols */
ASS:        '=';
EQ:         '==';
NE:         '<>';
GT:         '>';
GE:         '>=';
LT:         '<';
LE:         '<=';
SEMI:       ';';
LBRACE:     '{';
RBRACE:     '}';
LPAR:       '(';
RPAR:       ')';
PLUS:       '+';
MINUS:      '-';
SLASH:      '/';
MULT:       '*';
AND:        '&&';
OR:         '||';
HASH:       '#';

/* Tokens with content */
VAR:        LETTER (LETTER | DIGIT)*;
NUM:        DIGIT+;
BOOL:       TRUE | FALSE;

fragment LETTER:    [A-Za-z];
fragment DIGIT:     [0-9];

/* Skipped content */
COMMENT:    HASH .*? HASH -> skip;
WS:         [ \t\n\r] -> skip;

/* Letter fragments */
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
