// Generated from /Users/Peter/projects/ispc_desktop/ispc.d.foundation/src/main/java/com/intelligent/ispc/foundation/parser/SEP.g4 by ANTLR 4.5.3
package com.dmsoft.firefly.sdk.utils.parser.gen;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SEPParser extends Parser {
    public static final int
            NUMBER = 1, STRING = 2, WS = 3, GREATE_THAN = 4, GREATE_EQUAL_THAN = 5, LESS_THAN = 6,
            LESS_EQUAL_THAN = 7, EQUAL = 8, TRUE = 9, FALSE = 10, NOT_EQUAL = 11, LIKE = 12, LOGICAL_AND = 13,
            LOGICAL_OR = 14, LOGICAL_NOT = 15, LEFT_PAREN = 16, RIGHT_PAREN = 17, LEFT_CURLY = 18,
            RIGHT_CURLY = 19, CR = 20, IF = 21, ELSE = 22, ELSEIF = 23, SEMICOLON = 24, DOUBLE_QUOTATION = 25,
            RETURN = 26, LINE_COMMENT = 27, COMMENT = 28, SUB = 29;
    public static final int
            RULE_expression = 0, RULE_equality_expression = 1, RULE_return_statement = 2,
            RULE_elseif_list = 3, RULE_elseif = 4, RULE_if_statement = 5, RULE_statement = 6,
            RULE_block = 7, RULE_statement_list = 8;
    public final static String[] ruleNames = {
            "expression", "equality_expression", "return_statement", "elseif_list",
            "elseif", "if_statement", "statement", "block", "statement_list"
    };
    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\37r\4\2\t\2\4\3\t" +
                    "\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2" +
                    "\3\2\3\2\3\2\3\2\3\2\5\2\35\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3" +
                    "\3\3\3\5\3*\n\3\3\3\3\3\3\3\7\3/\n\3\f\3\16\3\62\13\3\3\4\3\4\3\5\6\5" +
                    "\67\n\5\r\5\16\58\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7" +
                    "\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3" +
                    "\7\3\7\3\7\3\7\3\7\3\7\5\7_\n\7\3\b\3\b\5\bc\n\b\3\t\3\t\3\t\3\t\3\t\3" +
                    "\t\5\tk\n\t\3\n\6\nn\n\n\r\n\16\no\3\n\2\3\4\13\2\4\6\b\n\f\16\20\22\2" +
                    "\4\4\2\6\n\r\16\5\2\n\n\r\r\17\21v\2\34\3\2\2\2\4)\3\2\2\2\6\63\3\2\2" +
                    "\2\b\66\3\2\2\2\n:\3\2\2\2\f^\3\2\2\2\16b\3\2\2\2\20j\3\2\2\2\22m\3\2" +
                    "\2\2\24\35\7\3\2\2\25\35\7\4\2\2\26\27\7\37\2\2\27\35\5\2\2\2\30\31\7" +
                    "\22\2\2\31\32\5\2\2\2\32\33\7\23\2\2\33\35\3\2\2\2\34\24\3\2\2\2\34\25" +
                    "\3\2\2\2\34\26\3\2\2\2\34\30\3\2\2\2\35\3\3\2\2\2\36\37\b\3\1\2\37*\7" +
                    "\13\2\2 *\7\f\2\2!\"\5\2\2\2\"#\t\2\2\2#$\5\2\2\2$*\3\2\2\2%&\7\22\2\2" +
                    "&\'\5\4\3\2\'(\7\23\2\2(*\3\2\2\2)\36\3\2\2\2) \3\2\2\2)!\3\2\2\2)%\3" +
                    "\2\2\2*\60\3\2\2\2+,\f\4\2\2,-\t\3\2\2-/\5\4\3\5.+\3\2\2\2/\62\3\2\2\2" +
                    "\60.\3\2\2\2\60\61\3\2\2\2\61\5\3\2\2\2\62\60\3\2\2\2\63\64\5\4\3\2\64" +
                    "\7\3\2\2\2\65\67\5\n\6\2\66\65\3\2\2\2\678\3\2\2\28\66\3\2\2\289\3\2\2" +
                    "\29\t\3\2\2\2:;\7\31\2\2;<\7\22\2\2<=\5\2\2\2=>\7\23\2\2>?\5\20\t\2?\13" +
                    "\3\2\2\2@A\7\27\2\2AB\7\22\2\2BC\5\2\2\2CD\7\23\2\2DE\5\20\t\2E_\3\2\2" +
                    "\2FG\7\27\2\2GH\7\22\2\2HI\5\2\2\2IJ\7\23\2\2JK\5\20\t\2KL\7\30\2\2LM" +
                    "\5\20\t\2M_\3\2\2\2NO\7\27\2\2OP\7\22\2\2PQ\5\2\2\2QR\7\23\2\2RS\5\20" +
                    "\t\2ST\5\b\5\2T_\3\2\2\2UV\7\27\2\2VW\7\22\2\2WX\5\2\2\2XY\7\23\2\2YZ" +
                    "\5\20\t\2Z[\5\b\5\2[\\\7\30\2\2\\]\5\20\t\2]_\3\2\2\2^@\3\2\2\2^F\3\2" +
                    "\2\2^N\3\2\2\2^U\3\2\2\2_\r\3\2\2\2`c\5\2\2\2ac\5\f\7\2b`\3\2\2\2ba\3" +
                    "\2\2\2c\17\3\2\2\2de\7\24\2\2ef\5\22\n\2fg\7\25\2\2gk\3\2\2\2hi\7\24\2" +
                    "\2ik\7\25\2\2jd\3\2\2\2jh\3\2\2\2k\21\3\2\2\2ln\5\16\b\2ml\3\2\2\2no\3" +
                    "\2\2\2om\3\2\2\2op\3\2\2\2p\23\3\2\2\2\n\34)\608^bjo";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    private static final String[] _LITERAL_NAMES = {
            null, null, null, null, "'>'", "'>='", "'<'", "'<='", "'='", "'true'",
            "'false'", "'!='", "'%='", "'&'", "'|'", "'!'", "'('", "')'", "'{'", "'}'",
            "'\n'", "'if'", "'else'", "'else if'", "';'", "'\"'", "'return'"
    };
    private static final String[] _SYMBOLIC_NAMES = {
            null, "NUMBER", "STRING", "WS", "GREATE_THAN", "GREATE_EQUAL_THAN", "LESS_THAN",
            "LESS_EQUAL_THAN", "EQUAL", "TRUE", "FALSE", "NOT_EQUAL", "LIKE", "LOGICAL_AND",
            "LOGICAL_OR", "LOGICAL_NOT", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_CURLY",
            "RIGHT_CURLY", "CR", "IF", "ELSE", "ELSEIF", "SEMICOLON", "DOUBLE_QUOTATION",
            "RETURN", "LINE_COMMENT", "COMMENT", "SUB"
    };
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    static {
        RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION);
    }

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }

    public SEPParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        String[] names = tokenNames;
        return names;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getGrammarFileName() {
        return "SEP.g4";
    }

    @Override
    public String[] getRuleNames() {
        String[] names = ruleNames;
        return names;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public final ExpressionContext expression() throws RecognitionException {
        ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_expression);
        try {
            setState(26);
            switch (_input.LA(1)) {
                case NUMBER:
                    _localctx = new NumberContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                {
                    setState(18);
                    match(NUMBER);
                }
                break;
                case STRING:
                    _localctx = new StringContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                {
                    setState(19);
                    match(STRING);
                }
                break;
                case SUB:
                    _localctx = new SubExprContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                {
                    setState(20);
                    match(SUB);
                    setState(21);
                    expression();
                }
                break;
                case LEFT_PAREN:
                    _localctx = new ParenContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                {
                    setState(22);
                    match(LEFT_PAREN);
                    setState(23);
                    expression();
                    setState(24);
                    match(RIGHT_PAREN);
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final Equality_expressionContext equality_expression() throws RecognitionException {
        return equality_expression(0);
    }

    private Equality_expressionContext equality_expression(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        Equality_expressionContext _localctx = new Equality_expressionContext(_ctx, _parentState);
        Equality_expressionContext _prevctx = _localctx;
        int _startState = 2;
        enterRecursionRule(_localctx, 2, RULE_equality_expression, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(39);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                    case 1: {
                        _localctx = new LogicalTrueContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;

                        setState(29);
                        match(TRUE);
                    }
                    break;
                    case 2: {
                        _localctx = new LogicalFalseContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(30);
                        match(FALSE);
                    }
                    break;
                    case 3: {
                        _localctx = new LogicalOpContext(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(31);
                        expression();
                        setState(32);
                        ((LogicalOpContext) _localctx).op = _input.LT(1);
                        _la = _input.LA(1);
                        if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GREATE_THAN) | (1L << GREATE_EQUAL_THAN) | (1L << LESS_THAN) | (1L << LESS_EQUAL_THAN) | (1L << EQUAL) | (1L << NOT_EQUAL) | (1L << LIKE))) != 0))) {
                            ((LogicalOpContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                        } else {
                            consume();
                        }
                        setState(33);
                        expression();
                    }
                    break;
                    case 4: {
                        _localctx = new Paren2Context(_localctx);
                        _ctx = _localctx;
                        _prevctx = _localctx;
                        setState(35);
                        match(LEFT_PAREN);
                        setState(36);
                        equality_expression(0);
                        setState(37);
                        match(RIGHT_PAREN);
                    }
                    break;
                    default:
                        break;
                }
                _ctx.stop = _input.LT(-1);
                setState(46);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            {
                                _localctx = new LogicalAndOrNotContext(new Equality_expressionContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, _startState, RULE_equality_expression);
                                setState(41);
                                if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                setState(42);
                                ((LogicalAndOrNotContext) _localctx).op = _input.LT(1);
                                _la = _input.LA(1);
                                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQUAL) | (1L << NOT_EQUAL) | (1L << LOGICAL_AND) | (1L << LOGICAL_OR) | (1L << LOGICAL_NOT))) != 0))) {
                                    ((LogicalAndOrNotContext) _localctx).op = (Token) _errHandler.recoverInline(this);
                                } else {
                                    consume();
                                }
                                setState(43);
                                equality_expression(3);
                            }
                        }
                    }
                    setState(48);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public final Return_statementContext return_statement() throws RecognitionException {
        Return_statementContext _localctx = new Return_statementContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_return_statement);
        try {
            _localctx = new ReturnContext(_localctx);
            enterOuterAlt(_localctx, 1);
            {
                setState(49);
                equality_expression(0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final Elseif_listContext elseif_list() throws RecognitionException {
        Elseif_listContext _localctx = new Elseif_listContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_elseif_list);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(51);
                            elseif();
                        }
                    }
                    setState(54);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == ELSEIF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final ElseifContext elseif() throws RecognitionException {
        ElseifContext _localctx = new ElseifContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_elseif);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                match(ELSEIF);
                setState(57);
                match(LEFT_PAREN);
                setState(58);
                expression();
                setState(59);
                match(RIGHT_PAREN);
                setState(60);
                block();
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final If_statementContext if_statement() throws RecognitionException {
        If_statementContext _localctx = new If_statementContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_if_statement);
        try {
            setState(92);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(62);
                    match(IF);
                    setState(63);
                    match(LEFT_PAREN);
                    setState(64);
                    expression();
                    setState(65);
                    match(RIGHT_PAREN);
                    setState(66);
                    block();
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(68);
                    match(IF);
                    setState(69);
                    match(LEFT_PAREN);
                    setState(70);
                    expression();
                    setState(71);
                    match(RIGHT_PAREN);
                    setState(72);
                    block();
                    setState(73);
                    match(ELSE);
                    setState(74);
                    block();
                }
                break;
                case 3:
                    enterOuterAlt(_localctx, 3);
                {
                    setState(76);
                    match(IF);
                    setState(77);
                    match(LEFT_PAREN);
                    setState(78);
                    expression();
                    setState(79);
                    match(RIGHT_PAREN);
                    setState(80);
                    block();
                    setState(81);
                    elseif_list();
                }
                break;
                case 4:
                    enterOuterAlt(_localctx, 4);
                {
                    setState(83);
                    match(IF);
                    setState(84);
                    match(LEFT_PAREN);
                    setState(85);
                    expression();
                    setState(86);
                    match(RIGHT_PAREN);
                    setState(87);
                    block();
                    setState(88);
                    elseif_list();
                    setState(89);
                    match(ELSE);
                    setState(90);
                    block();
                }
                break;
                default:
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final StatementContext statement() throws RecognitionException {
        StatementContext _localctx = new StatementContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_statement);
        try {
            setState(96);
            switch (_input.LA(1)) {
                case NUMBER:
                case STRING:
                case LEFT_PAREN:
                case SUB:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(94);
                    expression();
                }
                break;
                case IF:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(95);
                    if_statement();
                }
                break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final BlockContext block() throws RecognitionException {
        BlockContext _localctx = new BlockContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_block);
        try {
            setState(104);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 6, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                {
                    setState(98);
                    match(LEFT_CURLY);
                    setState(99);
                    statement_list();
                    setState(100);
                    match(RIGHT_CURLY);
                }
                break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                {
                    setState(102);
                    match(LEFT_CURLY);
                    setState(103);
                    match(RIGHT_CURLY);
                }
                break;
                default:
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public final Statement_listContext statement_list() throws RecognitionException {
        Statement_listContext _localctx = new Statement_listContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_statement_list);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(107);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(106);
                            statement();
                        }
                    }
                    setState(109);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NUMBER) | (1L << STRING) | (1L << LEFT_PAREN) | (1L << IF) | (1L << SUB))) != 0));
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 1:
                return equality_expression_sempred((Equality_expressionContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean equality_expression_sempred(Equality_expressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 2);
        }
        return true;
    }

    public static class ExpressionContext extends ParserRuleContext {
        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public ExpressionContext() {
        }

        @Override
        public int getRuleIndex() {
            return RULE_expression;
        }

        public void copyFrom(ExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class NumberContext extends ExpressionContext {
        public NumberContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public TerminalNode NUMBER() {
            return getToken(SEPParser.NUMBER, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterNumber(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitNumber(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitNumber(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class SubExprContext extends ExpressionContext {
        public SubExprContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public TerminalNode SUB() {
            return getToken(SEPParser.SUB, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterSubExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitSubExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitSubExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class StringContext extends ExpressionContext {
        public StringContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public TerminalNode STRING() {
            return getToken(SEPParser.STRING, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterString(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitString(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitString(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ParenContext extends ExpressionContext {
        public ParenContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public TerminalNode LEFT_PAREN() {
            return getToken(SEPParser.LEFT_PAREN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RIGHT_PAREN() {
            return getToken(SEPParser.RIGHT_PAREN, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterParen(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitParen(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitParen(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class Equality_expressionContext extends ParserRuleContext {
        public Equality_expressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public Equality_expressionContext() {
        }

        @Override
        public int getRuleIndex() {
            return RULE_equality_expression;
        }

        public void copyFrom(Equality_expressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class LogicalAndOrNotContext extends Equality_expressionContext {
        public Token op;

        public LogicalAndOrNotContext(Equality_expressionContext ctx) {
            copyFrom(ctx);
        }

        public List<Equality_expressionContext> equality_expression() {
            return getRuleContexts(Equality_expressionContext.class);
        }

        public Equality_expressionContext equality_expression(int i) {
            return getRuleContext(Equality_expressionContext.class, i);
        }

        public TerminalNode LOGICAL_NOT() {
            return getToken(SEPParser.LOGICAL_NOT, 0);
        }

        public TerminalNode LOGICAL_AND() {
            return getToken(SEPParser.LOGICAL_AND, 0);
        }

        public TerminalNode LOGICAL_OR() {
            return getToken(SEPParser.LOGICAL_OR, 0);
        }

        public TerminalNode EQUAL() {
            return getToken(SEPParser.EQUAL, 0);
        }

        public TerminalNode NOT_EQUAL() {
            return getToken(SEPParser.NOT_EQUAL, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterLogicalAndOrNot(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitLogicalAndOrNot(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitLogicalAndOrNot(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class LogicalFalseContext extends Equality_expressionContext {
        public LogicalFalseContext(Equality_expressionContext ctx) {
            copyFrom(ctx);
        }

        public TerminalNode FALSE() {
            return getToken(SEPParser.FALSE, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterLogicalFalse(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitLogicalFalse(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitLogicalFalse(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class Paren2Context extends Equality_expressionContext {
        public Paren2Context(Equality_expressionContext ctx) {
            copyFrom(ctx);
        }

        public TerminalNode LEFT_PAREN() {
            return getToken(SEPParser.LEFT_PAREN, 0);
        }

        public Equality_expressionContext equality_expression() {
            return getRuleContext(Equality_expressionContext.class, 0);
        }

        public TerminalNode RIGHT_PAREN() {
            return getToken(SEPParser.RIGHT_PAREN, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterParen2(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitParen2(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitParen2(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class LogicalOpContext extends Equality_expressionContext {
        public Token op;

        public LogicalOpContext(Equality_expressionContext ctx) {
            copyFrom(ctx);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode GREATE_THAN() {
            return getToken(SEPParser.GREATE_THAN, 0);
        }

        public TerminalNode GREATE_EQUAL_THAN() {
            return getToken(SEPParser.GREATE_EQUAL_THAN, 0);
        }

        public TerminalNode LESS_THAN() {
            return getToken(SEPParser.LESS_THAN, 0);
        }

        public TerminalNode LESS_EQUAL_THAN() {
            return getToken(SEPParser.LESS_EQUAL_THAN, 0);
        }

        public TerminalNode EQUAL() {
            return getToken(SEPParser.EQUAL, 0);
        }

        public TerminalNode NOT_EQUAL() {
            return getToken(SEPParser.NOT_EQUAL, 0);
        }

        public TerminalNode LIKE() {
            return getToken(SEPParser.LIKE, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterLogicalOp(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitLogicalOp(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitLogicalOp(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class LogicalTrueContext extends Equality_expressionContext {
        public LogicalTrueContext(Equality_expressionContext ctx) {
            copyFrom(ctx);
        }

        public TerminalNode TRUE() {
            return getToken(SEPParser.TRUE, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterLogicalTrue(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitLogicalTrue(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitLogicalTrue(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class Return_statementContext extends ParserRuleContext {
        public Return_statementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public Return_statementContext() {
        }

        @Override
        public int getRuleIndex() {
            return RULE_return_statement;
        }

        public void copyFrom(Return_statementContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class ReturnContext extends Return_statementContext {
        public ReturnContext(Return_statementContext ctx) {
            copyFrom(ctx);
        }

        public Equality_expressionContext equality_expression() {
            return getRuleContext(Equality_expressionContext.class, 0);
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterReturn(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitReturn(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitReturn(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class Elseif_listContext extends ParserRuleContext {
        public Elseif_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public List<ElseifContext> elseif() {
            return getRuleContexts(ElseifContext.class);
        }

        public ElseifContext elseif(int i) {
            return getRuleContext(ElseifContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_elseif_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterElseif_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitElseif_list(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitElseif_list(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ElseifContext extends ParserRuleContext {
        public ElseifContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode ELSEIF() {
            return getToken(SEPParser.ELSEIF, 0);
        }

        public TerminalNode LEFT_PAREN() {
            return getToken(SEPParser.LEFT_PAREN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RIGHT_PAREN() {
            return getToken(SEPParser.RIGHT_PAREN, 0);
        }

        public BlockContext block() {
            return getRuleContext(BlockContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_elseif;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterElseif(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitElseif(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitElseif(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class If_statementContext extends ParserRuleContext {
        public If_statementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode IF() {
            return getToken(SEPParser.IF, 0);
        }

        public TerminalNode LEFT_PAREN() {
            return getToken(SEPParser.LEFT_PAREN, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode RIGHT_PAREN() {
            return getToken(SEPParser.RIGHT_PAREN, 0);
        }

        public List<BlockContext> block() {
            return getRuleContexts(BlockContext.class);
        }

        public BlockContext block(int i) {
            return getRuleContext(BlockContext.class, i);
        }

        public TerminalNode ELSE() {
            return getToken(SEPParser.ELSE, 0);
        }

        public Elseif_listContext elseif_list() {
            return getRuleContext(Elseif_listContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_if_statement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterIf_statement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitIf_statement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitIf_statement(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class StatementContext extends ParserRuleContext {
        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public If_statementContext if_statement() {
            return getRuleContext(If_statementContext.class, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_statement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitStatement(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class BlockContext extends ParserRuleContext {
        public BlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public TerminalNode LEFT_CURLY() {
            return getToken(SEPParser.LEFT_CURLY, 0);
        }

        public Statement_listContext statement_list() {
            return getRuleContext(Statement_listContext.class, 0);
        }

        public TerminalNode RIGHT_CURLY() {
            return getToken(SEPParser.RIGHT_CURLY, 0);
        }

        @Override
        public int getRuleIndex() {
            return RULE_block;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterBlock(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitBlock(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitBlock(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class Statement_listContext extends ParserRuleContext {
        public Statement_listContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public List<StatementContext> statement() {
            return getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return getRuleContext(StatementContext.class, i);
        }

        @Override
        public int getRuleIndex() {
            return RULE_statement_list;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).enterStatement_list(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof SEPListener) ((SEPListener) listener).exitStatement_list(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof SEPVisitor) return ((SEPVisitor<? extends T>) visitor).visitStatement_list(this);
            else return visitor.visitChildren(this);
        }
    }
}