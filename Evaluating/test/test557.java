public final class Pattern extends AbstractDatatype { public static final Pattern THE_INSTANCE = new Pattern ( ) ; private Pattern ( ) { super ( ) ; } public void checkValid ( CharSequence literal ) throws DatatypeException { ContextFactory cf = new ContextFactory ( ) ; Context cx = cf . enterContext ( ) ; RegExpImpl rei = new RegExpImpl ( ) ; String anchoredRegex = "^(?:" + literal + ")$" ; try { rei . compileRegExp ( cx , anchoredRegex , "" ) ; } catch ( EcmaError ee ) { throw newDatatypeException ( ee . getErrorMessage ( ) ) ; } finally { Context . exit ( ) ; } } @ Override public String getName ( ) { return "pattern" ; } } 