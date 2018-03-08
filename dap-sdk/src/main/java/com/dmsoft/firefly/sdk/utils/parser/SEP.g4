grammar SEP;
@header
{
}
@members
{
}
/*
* Parser Rules
*/
/*
* Expression
*/
expression
    : NUMBER            #Number
    | STRING            #String
    | SUB expression    #SubExpr
    | LEFT_PAREN expression RIGHT_PAREN     #Paren
;
equality_expression
    : TRUE        #LogicalTrue
    | FALSE        #LogicalFalse
    | expression op=(GREATE_THAN | GREATE_EQUAL_THAN | LESS_THAN | LESS_EQUAL_THAN | EQUAL | NOT_EQUAL | LIKE) expression    #LogicalOp
    | equality_expression op=(LOGICAL_NOT | LOGICAL_AND | LOGICAL_OR | EQUAL | NOT_EQUAL) equality_expression    #LogicalAndOrNot
    | LEFT_PAREN equality_expression RIGHT_PAREN        #Paren2
;
/*
* Return
*/
return_statement
        :  equality_expression     #Return
;
elseif_list
    : elseif+
;
elseif
    : ELSEIF LEFT_PAREN expression RIGHT_PAREN block
;
if_statement
    : IF LEFT_PAREN expression RIGHT_PAREN block
    | IF LEFT_PAREN expression RIGHT_PAREN block ELSE block
    | IF LEFT_PAREN expression RIGHT_PAREN block elseif_list
    | IF LEFT_PAREN expression RIGHT_PAREN block elseif_list ELSE block
;
statement
        : expression
        | if_statement
;
block
    : LEFT_CURLY statement_list RIGHT_CURLY
    | LEFT_CURLY RIGHT_CURLY
;
statement_list
        : statement+
;
/*
* Lexer Rules
*/
NUMBER : [1-9][0-9]*|[0]|([0-9]+[.][0-9]+) ;
STRING : '"' ('\\"'|.)*? '"' ;
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
//ADD : '+' ;
//SUB : '-' ;
//MUL : '*' ;
//DIV : '/' ;
//MOD : '%' ;
GREATE_THAN : '>' ;
GREATE_EQUAL_THAN : '>=' ;
LESS_THAN : '<' ;
LESS_EQUAL_THAN : '<=' ;
EQUAL : '=' ;
TRUE : 'true' ;
FALSE : 'false' ;
NOT_EQUAL : '!=' ;
LIKE : '%=' ;
LOGICAL_AND : '&' ;
LOGICAL_OR : '|' ;
LOGICAL_NOT : '!' ;
LEFT_PAREN : '(' ;
RIGHT_PAREN : ')' ;
LEFT_CURLY : '{' ;
RIGHT_CURLY : '}' ;
CR : '\n' ;
IF : 'if' ;
ELSE : 'else' ;
ELSEIF : 'else if' ;
SEMICOLON : ';' ;
DOUBLE_QUOTATION : '"' ;
RETURN : 'return' ;
LINE_COMMENT : '//' .*? '\n' -> skip ;
COMMENT : '/*' .*? '*/' -> skip ;