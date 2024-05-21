
function init() {
    var libPath = new Packages.java.io.File(module.getPath(), "MOD-INF/lib/groovy/").getCanonicalPath();
  
    var S = Packages.java.lang.System;
    var currentLibPath = S.getProperty("groovy.path");
    if (currentLibPath == null) {
      currentLibPath = libPath;
    } else if (currentLibPath.indexOf(libPath) < 0) {
      currentLibPath = currentLibPath + Packages.java.io.File.pathSeparator + libPath;
    }
    S.setProperty("groovy.path", currentLibPath);
  
    Packages.com.google.refine.expr.MetaParser.registerLanguageParser(
      "groovy",
      "Groovy",
      Packages.org.openrefine.groovy.GroovyParser(),
      "return value"
    );
  }
  MP.registerLanguageParser("clojure", "Clojure", new Packages.com.google.refine.expr.ClojureParser(), "value");
