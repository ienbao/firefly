// Generated from /Users/Peter/projects/ispc_desktop/ispc.d.foundation/src/main/java/com/intelligent/ispc/foundation/parser/SEP.g4 by ANTLR 4.5.3
package com.dmsoft.firefly.core.utils.parser.gen;


import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SEPParser}.
 */
public interface SEPListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by the {@code Number}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     */
    void enterNumber(SEPParser.NumberContext ctx);

    /**
     * Exit a parse tree produced by the {@code Number}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     */
    void exitNumber(SEPParser.NumberContext ctx);

    /**
     * Enter a parse tree produced by the {@code String}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     */
    void enterString(SEPParser.StringContext ctx);

    /**
     * Exit a parse tree produced by the {@code String}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     */
    void exitString(SEPParser.StringContext ctx);

    /**
     * Enter a parse tree produced by the {@code SubExpr}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     */
    void enterSubExpr(SEPParser.SubExprContext ctx);

    /**
     * Exit a parse tree produced by the {@code SubExpr}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     */
    void exitSubExpr(SEPParser.SubExprContext ctx);

    /**
     * Enter a parse tree produced by the {@code Paren}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     */
    void enterParen(SEPParser.ParenContext ctx);

    /**
     * Exit a parse tree produced by the {@code Paren}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     */
    void exitParen(SEPParser.ParenContext ctx);

    /**
     * Enter a parse tree produced by the {@code LogicalAndOrNot}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void enterLogicalAndOrNot(SEPParser.LogicalAndOrNotContext ctx);

    /**
     * Exit a parse tree produced by the {@code LogicalAndOrNot}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void exitLogicalAndOrNot(SEPParser.LogicalAndOrNotContext ctx);

    /**
     * Enter a parse tree produced by the {@code LogicalFalse}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void enterLogicalFalse(SEPParser.LogicalFalseContext ctx);

    /**
     * Exit a parse tree produced by the {@code LogicalFalse}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void exitLogicalFalse(SEPParser.LogicalFalseContext ctx);

    /**
     * Enter a parse tree produced by the {@code Paren2}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void enterParen2(SEPParser.Paren2Context ctx);

    /**
     * Exit a parse tree produced by the {@code Paren2}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void exitParen2(SEPParser.Paren2Context ctx);

    /**
     * Enter a parse tree produced by the {@code LogicalOp}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void enterLogicalOp(SEPParser.LogicalOpContext ctx);

    /**
     * Exit a parse tree produced by the {@code LogicalOp}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void exitLogicalOp(SEPParser.LogicalOpContext ctx);

    /**
     * Enter a parse tree produced by the {@code LogicalTrue}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void enterLogicalTrue(SEPParser.LogicalTrueContext ctx);

    /**
     * Exit a parse tree produced by the {@code LogicalTrue}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     */
    void exitLogicalTrue(SEPParser.LogicalTrueContext ctx);

    /**
     * Enter a parse tree produced by the {@code Return}
     * labeled alternative in {@link SEPParser#return_statement}.
     *
     * @param ctx the parse tree
     */
    void enterReturn(SEPParser.ReturnContext ctx);

    /**
     * Exit a parse tree produced by the {@code Return}
     * labeled alternative in {@link SEPParser#return_statement}.
     *
     * @param ctx the parse tree
     */
    void exitReturn(SEPParser.ReturnContext ctx);

    /**
     * Enter a parse tree produced by {@link SEPParser#elseif_list}.
     *
     * @param ctx the parse tree
     */
    void enterElseif_list(SEPParser.Elseif_listContext ctx);

    /**
     * Exit a parse tree produced by {@link SEPParser#elseif_list}.
     *
     * @param ctx the parse tree
     */
    void exitElseif_list(SEPParser.Elseif_listContext ctx);

    /**
     * Enter a parse tree produced by {@link SEPParser#elseif}.
     *
     * @param ctx the parse tree
     */
    void enterElseif(SEPParser.ElseifContext ctx);

    /**
     * Exit a parse tree produced by {@link SEPParser#elseif}.
     *
     * @param ctx the parse tree
     */
    void exitElseif(SEPParser.ElseifContext ctx);

    /**
     * Enter a parse tree produced by {@link SEPParser#if_statement}.
     *
     * @param ctx the parse tree
     */
    void enterIf_statement(SEPParser.If_statementContext ctx);

    /**
     * Exit a parse tree produced by {@link SEPParser#if_statement}.
     *
     * @param ctx the parse tree
     */
    void exitIf_statement(SEPParser.If_statementContext ctx);

    /**
     * Enter a parse tree produced by {@link SEPParser#statement}.
     *
     * @param ctx the parse tree
     */
    void enterStatement(SEPParser.StatementContext ctx);

    /**
     * Exit a parse tree produced by {@link SEPParser#statement}.
     *
     * @param ctx the parse tree
     */
    void exitStatement(SEPParser.StatementContext ctx);

    /**
     * Enter a parse tree produced by {@link SEPParser#block}.
     *
     * @param ctx the parse tree
     */
    void enterBlock(SEPParser.BlockContext ctx);

    /**
     * Exit a parse tree produced by {@link SEPParser#block}.
     *
     * @param ctx the parse tree
     */
    void exitBlock(SEPParser.BlockContext ctx);

    /**
     * Enter a parse tree produced by {@link SEPParser#statement_list}.
     *
     * @param ctx the parse tree
     */
    void enterStatement_list(SEPParser.Statement_listContext ctx);

    /**
     * Exit a parse tree produced by {@link SEPParser#statement_list}.
     *
     * @param ctx the parse tree
     */
    void exitStatement_list(SEPParser.Statement_listContext ctx);
}