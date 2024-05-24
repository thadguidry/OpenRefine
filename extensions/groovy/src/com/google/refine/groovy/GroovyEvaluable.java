package com.google.refine.groovy;

import java.util.Map;
import java.util.Properties;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import com.google.refine.expr.EvalError;
import com.google.refine.expr.Evaluable;
import com.google.refine.expr.LanguageSpecificParser;
import com.google.refine.expr.ParsingException;

/**
 * A parser for expressions written in Groovy.
 * Based on {@link com.google.refine.jython.JythonEvaluable}
 */
public class GroovyEvaluable implements Evaluable{

    private final String s;

    public static LanguageSpecificParser createParser() {
        return new LanguageSpecificParser() {

            @Override
            public Evaluable parse(String s) throws ParsingException {
                return new GroovyEvaluable(s);
            }
        };
    }

    public GroovyEvaluable(String s) {
        this.s = s;
    }

    @Override
    public Object evaluate(Properties bindings) {
        try {
          
            // For JythonEvaluable, we call a custom function built.
            // For ClojureParser, we use it's IFn .invoke()
            // For GroovyParser, we use .evaluate()

            groovy.lang.Binding sharedData = new Binding(
                Map.of(
                    "value", bindings.get("value"),
                    "cell",  bindings.get("cell"),
                    "cells", bindings.get("cells"),
                    "row",   bindings.get("row"),
                    "rowIndex", bindings.get("rowIndex")
                )
            );

            String script = "$value $cell $cells $row $rowIndex" + s;
            GroovyShell groovyShell = new GroovyShell(sharedData);
            return groovyShell.evaluate(script); 
                    
            } catch (Exception e) {
            return new EvalError(e.getMessage());
        }
    }
}



             
