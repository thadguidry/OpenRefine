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
            config.setVerbose(true);
            GroovyShell groovyShell = new GroovyShell(config);
            
            Object result = groovyShell.evaluate(
                "[value cell cells row rowIndex]" + s
                );

            return new Evaluable() {

                // Do we need to really init something
                // for GroovyShell?  or can we skip this?

                public Evaluable init(GroovyShell result) {
                    _fn = result;
                    return this;
                }

                @Override
                public Object evaluate(Properties bindings) {
                    try {
                        // need to actually run script here? seems like
                        // For JythonEvaluable, we call a custom function built.
                        // For ClojureParser, we use it's IFn .invoke()
                        // For GroovyParser, we use .evaluate() ???

                        return _fn.evaluate(
                            script,
                            groovy.lang.Binding sharedData = new Binding(
                                map(
                                    "value", bindings.get("value"),
                                    "cell",  bindings.get("cell"),
                                    "cells", bindings.get("cells"),
                                    "row",   bindings.get("row"),
                                    "rowIndex", bindings.get("rowIndex")
                                )
                        );
        
               ;
                        

                    } catch (Exception e) {
                        return new EvalError(e.getMessage());
                    }
                }
            }.init(result);
        } catch (Exception e) {
            throw new ParsingException(e.getMessage());
        }
    }
}
