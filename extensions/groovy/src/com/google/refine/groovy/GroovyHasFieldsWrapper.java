

package com.google.refine.groovy;

import java.util.Properties;

import groovy.lang.GroovyObjectSupport;

import com.google.refine.expr.HasFields;

public class GroovyHasFieldsWrapper extends GroovyObjectSupport {

    private static final long serialVersionUID = -1275353513262385099L;

    public HasFields _obj;

    private Properties _bindings;

    public GroovyHasFieldsWrapper(HasFields obj, Properties bindings) {
        _obj = obj;
        _bindings = bindings;
    }

@Override
    public Object getProperty(String name) {
        Object v = _obj.getField(name, _bindings);
        if (v != null) {
            
            if (v instanceof HasFields) {
                return new GroovyHasFieldsWrapper((HasFields) v, _bindings);
            } else {
                return v;
            }
        } else {
            return null;
        }
    }
}

 
    