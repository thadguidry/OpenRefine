package com.google.refine.groovy;

import java.util.Properties;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;

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
          
            // Set the shared data (between Java <--> Groovy) for our Objects [value, cell, cells, row, and rowIndex] already binded.
            // Additionally, in order for the script (s) to properly access those Java objects having fields,
            // we first have to convert them in GroovyHasFieldsWrapper.java
            
            groovy.lang.Binding sharedData = new Binding();
            sharedData.setProperty("value", bindings.get("value"));
            sharedData.setProperty("cell", new GroovyHasFieldsWrapper((HasFields) bindings.get("cell"), bindings));
            sharedData.setProperty("cells", new GroovyHasFieldsWrapper((HasFields) bindings.get("cells"), bindings));
            sharedData.setProperty("row", new GroovyHasFieldsWrapper((HasFields) bindings.get("row"), bindings));
            sharedData.setProperty("rowIndex", bindings.get("rowIndex"));
            
            // There are many Compiler options, see Groovy Docs
            CompilerConfiguration config = new CompilerConfiguration();
            // config.setDebug(true);

            GroovyShell groovyShell = new GroovyShell(sharedData, config);
            // The GroovyShell, rather than Eval, is used to evaluate the Groovy script (s)
            // and return the result since GroovyShell supports caching.
            // NOTE: the sharedData is passed seamlessly behind the scenes
            // during the automatic Groovy Script.run() that happens during evaluate().
            return groovyShell.evaluate(s); 
                    
            } catch (Exception e) {
            return new EvalError(e.getMessage());
        }
    }
}



             
