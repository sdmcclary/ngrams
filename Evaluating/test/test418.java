public class TaintableLocatorImpl extends LocatorImpl { private boolean tainted ; public TaintableLocatorImpl ( Locator locator ) { super ( locator ) ; this . tainted = false ; } public void markTainted ( ) { tainted = true ; } public boolean isTainted ( ) { return tainted ; } } 