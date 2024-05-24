package com.google.refine.groovy;

import java.util.Properties;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import com.google.refine.expr.EvalError;
import com.google.refine.expr.Evaluable;
import com.google.refine.expr.HasFields;
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
            // For GroovyEvaluable, we use .evaluate()

            groovy.lang.Binding sharedData = new Binding();
            sharedData.setProperty("value", bindings.get("value"));
            sharedData.setProperty("cell", new GroovyHasFieldsWrapper((HasFields) bindings.get("cell"), bindings));
            sharedData.setProperty("cells", new GroovyHasFieldsWrapper((HasFields) bindings.get("cells"), bindings));
            sharedData.setProperty("row", new GroovyHasFieldsWrapper((HasFields) bindings.get("row"), bindings));
            sharedData.setProperty("rowIndex", bindings.get("rowIndex"));
            
            String script = "$value $cell $cells $row $rowIndex " + s;
            GroovyShell groovyShell = new GroovyShell(sharedData);
            return groovyShell.evaluate(s); 
                    
            } catch (Exception e) {
            return new EvalError(e.getMessage());
        }
    }
}



             
