/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.core.utils.parser;

import com.dmsoft.firefly.core.utils.parser.gen.SEPBaseVisitor;
import com.dmsoft.firefly.core.utils.parser.gen.SEPParser;

/**
 * Created by Peter on 2016/8/29.
 */
public class SEPVisitor extends SEPBaseVisitor<SEPResult> {

    @Override
    public SEPResult visitNumber(SEPParser.NumberContext ctx) {
        SEPResult result = new SEPResult();
        result.setRightExpr(ctx.NUMBER().getText());
        result.setLeftExpr(ctx.NUMBER().getText());
        return result;
    }

    @Override
    public SEPResult visitString(SEPParser.StringContext ctx) {
        SEPResult result = new SEPResult();
        result.setRightExpr(ctx.getText().replace("\"", ""));
        result.setLeftExpr(ctx.getText().replace("\"", ""));
        return result;
    }

    @Override
    public SEPResult visitParen(SEPParser.ParenContext ctx) {
        SEPResult result = visit(ctx.expression());
        result.setLeftExpr(result.getLeftExpr());
        result.setRightExpr(result.getRightExpr());
        return result;
    }

    @Override
    public SEPResult visitLogicalAndOrNot(SEPParser.LogicalAndOrNotContext ctx) {
        SEPResult left = visit(ctx.equality_expression(0));
        SEPResult right = visit(ctx.equality_expression(1));

        SEPResult result = new SEPResult();
        result.setLeftExpr(left);
        result.setRightExpr(right);

        if (ctx.op.getType() == SEPParser.LOGICAL_AND) {
            result.setLogicalToken(ParserToken.AND.getCode());
        } else if (ctx.op.getType() == SEPParser.LOGICAL_OR) {
            result.setLogicalToken(ParserToken.OR.getCode());
        }

        return result;
    }

    @Override
    public SEPResult visitLogicalFalse(SEPParser.LogicalFalseContext ctx) {
        SEPResult result = new SEPResult();
        result.setRightExpr(false);
        return result;
    }

    @Override
    public SEPResult visitParen2(SEPParser.Paren2Context ctx) {
        return visit(ctx.equality_expression());
    }

    @Override
    public SEPResult visitLogicalOp(SEPParser.LogicalOpContext ctx) {
        SEPResult result = new SEPResult();
        String left = visit(ctx.expression(0)).getLeftExpr().toString();
        String right = visit(ctx.expression(1)).getRightExpr().toString();

        if (ctx.op.getType() == SEPParser.GREATE_THAN) {
            result.setLeftExpr(left);
            result.setRightExpr(right);
            result.setToken(ParserToken.GT.getCode());
        }
        if (ctx.op.getType() == SEPParser.GREATE_EQUAL_THAN) {
            result.setLeftExpr(left);
            result.setRightExpr(right);
            result.setToken(ParserToken.GET.getCode());

        }
        if (ctx.op.getType() == SEPParser.LESS_THAN) {
            result.setLeftExpr(left);
            result.setRightExpr(right);
            result.setToken(ParserToken.LT.getCode());
        }
        if (ctx.op.getType() == SEPParser.LESS_EQUAL_THAN) {
            result.setLeftExpr(left);
            result.setRightExpr(right);
            result.setToken(ParserToken.LET.getCode());
        }
        if (ctx.op.getType() == SEPParser.EQUAL) {
            result.setLeftExpr(left);
            result.setRightExpr(right);
            result.setToken(ParserToken.EQ.getCode());
        }
        if (ctx.op.getType() == SEPParser.NOT_EQUAL) {
            result.setLeftExpr(left);
            result.setRightExpr(right);
            result.setToken(ParserToken.NE.getCode());
        }
        if (ctx.op.getType() == SEPParser.LIKE) {
            result.setLeftExpr(left);
            result.setRightExpr(right);
            result.setToken(ParserToken.LIKE.getCode());
        }

        return result;
    }

    @Override
    public SEPResult visitLogicalTrue(SEPParser.LogicalTrueContext ctx) {
        SEPResult result = new SEPResult();
        result.setRightExpr(true);
        return result;
    }

    @Override
    public SEPResult visitReturn(SEPParser.ReturnContext ctx) {
        SEPResult result = visit(ctx.equality_expression());
        return result;
    }

}
