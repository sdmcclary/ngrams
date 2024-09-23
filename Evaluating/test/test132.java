public abstract class NormalizedNameClass implements com . thaiopensource . relaxng . match . NameClass { private final Set < Name > includedNames ; public NormalizedNameClass ( Set < Name > includedNames ) { this . includedNames = immutable ( includedNames ) ; } public boolean isEmpty ( ) { return includedNames . isEmpty ( ) ; } public boolean contains ( Name name ) { return includedNames . contains ( name ) ; } public boolean isAnyNameIncluded ( ) { return false ; } public Set < String > getExcludedNamespaces ( ) { return null ; } public Set < Name > getIncludedNames ( ) { return includedNames ; } public Set < Name > getExcludedNames ( ) { return null ; } public Set < String > getIncludedNamespaces ( ) { return Collections . emptySet ( ) ; } public Set < String > getExcludedLocalNames ( String ns ) { return null ; } public abstract boolean equals ( Object obj ) ; boolean equal ( NormalizedNameClass nc1 , NormalizedNameClass nc2 ) { return nc1 . includedNames . equals ( nc2 . includedNames ) ; } public int hashCode ( ) { return includedNames . hashCode ( ) ; } < T > Set < T > immutable ( Set < T > set ) { if ( set . isEmpty ( ) ) return Collections . emptySet ( ) ; return Collections . unmodifiableSet ( set ) ; } abstract boolean includesNamespace ( String ns ) ; } 