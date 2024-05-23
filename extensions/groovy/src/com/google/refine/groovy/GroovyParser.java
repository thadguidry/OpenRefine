package com.google.refine.groovy;

import java.util.Properties;

import groovy.lang.GroovyShell;

/**
 * A parser for expressions written in Groovy.
 */
public class GroovyParser implements LanguageSpecificParser {

    @Override
    public Evaluable parse(String s) throws ParsingException {
        try {

            GroovyShell shell = new GroovyShell();
            groovy.lang.Script eval = shell.evaluate(
                "return { value, cell, cells, row, rowIndex -> " + s + " }"
                );

            return new Evaluable() {

                private GroovyShell _fn;

                public Evaluable init(GroovyShell eval) {
                    _fn = eval;
                    return this;
                }

                @Override
                public Object evaluate(Properties bindings) {
                    try {
                        return _fn.invoke(
                                bindings.get("value"),
                                bindings.get("cell"),
                                bindings.get("cells"),
                                bindings.get("row"),
                                bindings.get("rowIndex"));
                    } catch (Exception e) {
                        return new EvalError(e.getMessage());
                    }
                }
            }.init(eval);
        } catch (Exception e) {
            throw new ParsingException(e.getMessage());
        }
    }
}
