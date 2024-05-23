package com.google.refine.groovy;

import java.util.Properties;

import com.google.refine.expr.EvalError;
import com.google.refine.expr.Evaluable;
import com.google.refine.expr.HasFields;
import com.google.refine.expr.LanguageSpecificParser;
import com.google.refine.expr.ParsingException;

import groovy.lang.Binding;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;


/**
 * A parser for expressions written in Groovy.
 * Based on {@link com.google.refine.expr.ClojureParser}
 */
public class GroovyParser implements LanguageSpecificParser {

    @Override
    public Evaluable parse(String s) throws ParsingException {
        try {

            CompilerConfiguration config = new CompilerConfiguration();
            config.setDebug(true);
            GroovyShell shell = new GroovyShell(config);
            
            Object eval = shell.evaluate(
                "[value cell cells row rowIndex]" + s
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
                        groovy.lang.Binding sharedData = new Binding(
                            map(
                                "value", bindings.get("value"),
                                "cell",  bindings.get("cell"),
                                "cells", bindings.get("cells"),
                                "row",   bindings.get("row"),
                                "rowIndex", bindings.get("rowIndex")
                            )
                        );
                        return sharedData;   
               ;
                        

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
