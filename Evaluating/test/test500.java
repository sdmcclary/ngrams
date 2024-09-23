public class LanguageData { private static final Pattern HYPHEN = Pattern . compile ( "-" ) ; private static final String [ ] [ ] EMPTY_DOUBLE_STRING_ARRAY = { } ; private static final String [ ] EMPTY_STRING_ARRAY = { } ; private static final String PREFIX = "prefix: " ; private static final String SUPPRESS_SCRIPT = "suppress-script: " ; private static final String SUBTAG = "subtag: " ; private static final String TAG = "tag: " ; private static final String TYPE = "type: " ; private static final String DEPRECATED = "deprecated: " ; private static final String PREFERRED_VALUE = "preferred-value: " ; private BufferedReader in ; private SortedSet < String > languageSet = new TreeSet < String > ( ) ; private SortedSet < String > extlangSet = new TreeSet < String > ( ) ; private SortedSet < String > scriptSet = new TreeSet < String > ( ) ; private SortedSet < String > regionSet = new TreeSet < String > ( ) ; private SortedSet < String > variantSet = new TreeSet < String > ( ) ; private SortedSet < String > grandfatheredSet = new TreeSet < String > ( ) ; private SortedSet < String > redundantSet = new TreeSet < String > ( ) ; private SortedSet < String > deprecatedLangSet = new TreeSet < String > ( ) ; private SortedSet < String > deprecatedSet = new TreeSet < String > ( ) ; private Map < String , String > suppressedScriptByLanguageMap = new HashMap < String , String > ( ) ; private Map < String , String > prefixByExtlangMap = new HashMap < String , String > ( ) ; private Map < String , String > preferredValueByLanguageMap = new HashMap < String , String > ( ) ; private Map < String , Set < String [ ] > > prefixesByVariantMap = new HashMap < String , Set < String [ ] > > ( ) ; private String [ ] languages = null ; private String [ ] extlangs = null ; private String [ ] scripts = null ; private String [ ] regions = null ; private String [ ] variants = null ; private String [ ] grandfathered = null ; private String [ ] redundant = null ; private String [ ] deprecatedLang = null ; private String [ ] deprecated = null ; private int [ ] suppressedScriptByLanguage = null ; private int [ ] prefixByExtlang = null ; private String [ ] [ ] [ ] prefixesByVariant = null ; public LanguageData ( ) throws IOException { super ( ) ; in = new BufferedReader ( new InputStreamReader ( LanguageData . class . getClassLoader ( ) . getResourceAsStream ( "nu/validator/localentities/files/language-subtag-registry" ) , "UTF-8" ) ) ; consumeRegistry ( ) ; prepareArrays ( ) ; } private void consumeRegistry ( ) throws IOException { while ( consumeRecord ( ) ) { } in . close ( ) ; } private void prepareArrays ( ) throws IOException { scripts = scriptSet . toArray ( EMPTY_STRING_ARRAY ) ; regions = regionSet . toArray ( EMPTY_STRING_ARRAY ) ; grandfathered = grandfatheredSet . toArray ( EMPTY_STRING_ARRAY ) ; redundant = redundantSet . toArray ( EMPTY_STRING_ARRAY ) ; deprecated = deprecatedSet . toArray ( EMPTY_STRING_ARRAY ) ; deprecatedLang = deprecatedLangSet . toArray ( EMPTY_STRING_ARRAY ) ; int i = 0 ; languages = new String [ languageSet . size ( ) ] ; suppressedScriptByLanguage = new int [ languageSet . size ( ) ] ; for ( String language : languageSet ) { languages [ i ] = language ; String suppressed = suppressedScriptByLanguageMap . get ( language ) ; if ( suppressed == null ) { suppressedScriptByLanguage [ i ] = - 1 ; } else { int index = Arrays . binarySearch ( scripts , suppressed ) ; if ( index < 0 ) { throw new IOException ( "Malformed registry: reference to non-existent script." ) ; } suppressedScriptByLanguage [ i ] = index ; } i ++ ; } i = 0 ; extlangs = new String [ extlangSet . size ( ) ] ; prefixByExtlang = new int [ extlangSet . size ( ) ] ; for ( String extlang : extlangSet ) { extlangs [ i ] = extlang ; String prefix = prefixByExtlangMap . get ( extlang ) ; if ( prefix == null ) { prefixByExtlang [ i ] = - 1 ; } else { int index = Arrays . binarySearch ( languages , prefix ) ; if ( index < 0 ) { throw new IOException ( "Malformed registry: reference to non-existent prefix for extlang." ) ; } prefixByExtlang [ i ] = index ; } i ++ ; } i = 0 ; variants = new String [ variantSet . size ( ) ] ; prefixesByVariant = new String [ variantSet . size ( ) ] [ ] [ ] ; for ( String variant : variantSet ) { variants [ i ] = variant ; Set < String [ ] > prefixes = prefixesByVariantMap . get ( variant ) ; if ( prefixes != null ) { prefixesByVariant [ i ] = prefixes . toArray ( EMPTY_DOUBLE_STRING_ARRAY ) ; } else { prefixesByVariant [ i ] = EMPTY_DOUBLE_STRING_ARRAY ; } i ++ ; } } private boolean consumeRecord ( ) throws IOException { boolean hasMore = true ; String type = null ; String subtag = null ; String suppressScript = null ; String preferredValue = null ; Set < String [ ] > prefixes = new HashSet < String [ ] > ( ) ; String singlePrefix = null ; boolean depr = false ; String line = null ; for ( ; ; ) { line = in . readLine ( ) ; if ( line == null ) { hasMore = false ; break ; } line = line . toLowerCase ( ) ; if ( "%%" . equals ( line ) ) { break ; } else if ( line . startsWith ( TYPE ) ) { type = line . substring ( TYPE . length ( ) ) . trim ( ) . intern ( ) ; } else if ( line . startsWith ( SUBTAG ) ) { subtag = line . substring ( SUBTAG . length ( ) ) . trim ( ) . intern ( ) ; } else if ( line . startsWith ( TAG ) ) { subtag = line . substring ( TAG . length ( ) ) . trim ( ) . intern ( ) ; } else if ( line . startsWith ( SUPPRESS_SCRIPT ) ) { suppressScript = line . substring ( SUPPRESS_SCRIPT . length ( ) ) . trim ( ) . intern ( ) ; } else if ( line . startsWith ( PREFIX ) ) { String [ ] prefixSubtags = HYPHEN . split ( line . substring ( PREFIX . length ( ) ) . trim ( ) ) ; for ( int i = 0 ; i < prefixSubtags . length ; i ++ ) { prefixSubtags [ i ] = prefixSubtags [ i ] . intern ( ) ; } prefixes . add ( prefixSubtags ) ; singlePrefix = prefixSubtags [ 0 ] ; } else if ( line . startsWith ( DEPRECATED ) ) { depr = true ; } else if ( line . startsWith ( PREFERRED_VALUE ) ) { preferredValue = line . substring ( PREFERRED_VALUE . length ( ) ) . trim ( ) . intern ( ) ; preferredValueByLanguageMap . put ( subtag , preferredValue ) ; } } if ( subtag == null ) { return hasMore ; } if ( depr ) { if ( "language" == type ) { deprecatedLangSet . add ( subtag ) ; } else { deprecatedSet . add ( subtag ) ; } } if ( "language" == type ) { languageSet . add ( subtag ) ; suppressedScriptByLanguageMap . put ( subtag , suppressScript ) ; } if ( "extlang" == type ) { extlangSet . add ( subtag ) ; prefixByExtlangMap . put ( subtag , singlePrefix ) ; } else if ( "region" == type ) { regionSet . add ( subtag ) ; } else if ( "script" == type ) { scriptSet . add ( subtag ) ; } else if ( "variant" == type ) { variantSet . add ( subtag ) ; prefixesByVariantMap . put ( subtag , prefixes ) ; } else if ( "grandfathered" == type ) { grandfatheredSet . add ( subtag ) ; } else if ( "redundant" == type ) { redundantSet . add ( subtag ) ; } return hasMore ; } public String [ ] getLanguages ( ) { return languages ; } public String [ ] getExtlangs ( ) { return extlangs ; } public String [ ] [ ] [ ] getPrefixesByVariant ( ) { return prefixesByVariant ; } public int [ ] getPrefixByExtlang ( ) { return prefixByExtlang ; } public String [ ] getRegions ( ) { return regions ; } public String [ ] getScripts ( ) { return scripts ; } public int [ ] getSuppressedScriptByLanguage ( ) { return suppressedScriptByLanguage ; } public String [ ] getVariants ( ) { return variants ; } public String [ ] getDeprecated ( ) { return deprecated ; } public Map < String , String > getPreferredValueByLanguageMap ( ) { return preferredValueByLanguageMap ; } public String [ ] getGrandfathered ( ) { return grandfathered ; } public String [ ] getRedundant ( ) { return redundant ; } public String [ ] getDeprecatedLang ( ) { return deprecatedLang ; } } 