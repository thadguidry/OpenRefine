package com.google.refine.expr;

import java.util.Properties;

import groovy.lang.GroovyShell;

/**
 * A parser for expressions written in Groovy.
 */
public class GroovyParser implements LanguageSpecificParser {

    @Override
    public Evaluable parse(String s) throws ParsingException {
        try {
//                    RT.load("clojure/core"); // Make sure RT is initialized
//            Object foo = RT.CURRENT_NS; // Make sure RT is initialized
//            IFn fn = (IFn) clojure.lang.Compiler.load(new StringReader(
//                    "(fn [value cell cells row rowIndex] " + s + ")"));

            GroovyShell shell = new GroovyShell();
            groovy.lang.Script eval = shell.evaluate(
                "return { value, cell, cells, row, rowIndex -> " + s + " }"
                );

            // TODO: We should to switch from using Compiler.load
            // because it's technically an internal interface
//                    Object code = CLOJURE_READ_STRING.invoke(
//                            "(fn [value cell cells row rowIndex] " + s + ")"
//                            );

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
