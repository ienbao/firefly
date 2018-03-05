// Generated from /Users/Peter/projects/ispc_desktop/ispc.d.foundation/src/main/java/com/intelligent/ispc/foundation/parser/SEP.g4 by ANTLR 4.5.3
package com.dmsoft.firefly.core.utils.parser.gen;


import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SEPParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SEPVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by the {@code Number}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNumber(SEPParser.NumberContext ctx);

    /**
     * Visit a parse tree produced by the {@code String}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitString(SEPParser.StringContext ctx);

    /**
     * Visit a parse tree produced by the {@code SubExpr}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSubExpr(SEPParser.SubExprContext ctx);

    /**
     * Visit a parse tree produced by the {@code Paren}
     * labeled alternative in {@link SEPParser#expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParen(SEPParser.ParenContext ctx);

    /**
     * Visit a parse tree produced by the {@code LogicalAndOrNot}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLogicalAndOrNot(SEPParser.LogicalAndOrNotContext ctx);

    /**
     * Visit a parse tree produced by the {@code LogicalFalse}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLogicalFalse(SEPParser.LogicalFalseContext ctx);

    /**
     * Visit a parse tree produced by the {@code Paren2}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParen2(SEPParser.Paren2Context ctx);

    /**
     * Visit a parse tree produced by the {@code LogicalOp}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLogicalOp(SEPParser.LogicalOpContext ctx);

    /**
     * Visit a parse tree produced by the {@code LogicalTrue}
     * labeled alternative in {@link SEPParser#equality_expression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLogicalTrue(SEPParser.LogicalTrueContext ctx);

    /**
     * Visit a parse tree produced by the {@code Return}
     * labeled alternative in {@link SEPParser#return_statement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitReturn(SEPParser.ReturnContext ctx);

    /**
     * Visit a parse tree produced by {@link SEPParser#elseif_list}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitElseif_list(SEPParser.Elseif_listContext ctx);

    /**
     * Visit a parse tree produced by {@link SEPParser#elseif}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitElseif(SEPParser.ElseifContext ctx);

    /**
     * Visit a parse tree produced by {@link SEPParser#if_statement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIf_statement(SEPParser.If_statementContext ctx);

    /**
     * Visit a parse tree produced by {@link SEPParser#statement}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStatement(SEPParser.StatementContext ctx);

    /**
     * Visit a parse tree produced by {@link SEPParser#block}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlock(SEPParser.BlockContext ctx);

    /**
     * Visit a parse tree produced by {@link SEPParser#statement_list}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStatement_list(SEPParser.Statement_listContext ctx);
}