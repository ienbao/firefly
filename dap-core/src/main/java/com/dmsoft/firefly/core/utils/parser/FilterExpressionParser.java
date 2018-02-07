/*
 *
 *  * Copyright (c) 2016. For Intelligent Group.
 *
 */

package com.dmsoft.firefly.core.utils.parser;

import com.example.define.spcmongo.utils.parser.gen.SEPLexer;
import com.example.define.spcmongo.utils.parser.gen.SEPParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Created by Peter on 2016/8/25.
 */
public class FilterExpressionParser {

    /**
     * Constructor.
     */
    public FilterExpressionParser() {
    }

    /**
     * Parser source string to SEPResult tree struct.
     *
     * @param input the input string
     * @return SEPResult
     */
    public SEPResult doParser(String input) {
        ANTLRInputStream inputStream = new ANTLRInputStream(input);
        SEPLexer lexer = new SEPLexer(inputStream);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SEPParser parser = new SEPParser(tokens);

        ParseTree tree = parser.return_statement();

        SEPVisitor visitor = new SEPVisitor();
        SEPResult result = visitor.visit(tree);

        return result;
    }
}
