<<<<<<< HEAD
public class Utils { private Utils ( ) { } public static boolean equals ( Object o1 , Object o2 ) { if ( o1 == null ) { return o2 == null ; } else { return o1 . equals ( o2 ) ; } } public static String toString ( Object obj ) { return ( obj == null ? null : obj . toString ( ) ) ; } public static String toString ( Atom [ ] atoms ) { String s = "" ; if ( atoms != null ) { for ( int i = 0 ; i < atoms . length ; i ++ ) { if ( i > 0 ) { s += " " ; } s += atoms [ i ] . toString ( ) ; } } return s ; } public static Atom [ ] toAtoms ( Collection < ? extends Atomizer > objs ) { Atom [ ] atoms = new Atom [ objs . size ( ) ] ; int i = 0 ; for ( Atomizer obj : objs ) { atoms [ i ] = obj . toAtom ( ) ; i ++ ; } return atoms ; } public static boolean isNumber ( Atom atom ) { return atom . isInt ( ) || atom . isFloat ( ) ; } public static String detokenize ( Atom [ ] args ) { return detokenize ( null , args ) ; } public static String detokenize ( String msg , Atom [ ] args ) { StringBuilder input = new StringBuilder ( ) ; if ( msg != null ) { input . append ( detokenize ( msg ) ) . append ( " " ) ; } for ( int i = 0 ; i < args . length ; i ++ ) { if ( i > 0 ) { input . append ( " " ) ; } input . append ( detokenize ( args [ i ] ) ) ; } return input . toString ( ) ; } public static String detokenize ( Atom atom ) { if ( atom == null ) { return "" ; } return detokenize ( atom . toString ( ) ) ; } public static String detokenize ( String str ) { if ( str . contains ( " " ) ) { return '"' + str + '"' ; } else { return str ; } } public static File getFile ( String path ) { return getFile ( path , null ) ; } public static File getFile ( String path , MaxPatcher patcher ) { return getFile ( path , patcher , false ) ; } public static File getFile ( String path , MaxPatcher patcher , boolean suppressError ) { if ( path == null || path . length ( ) == 0 ) { path = MaxSystem . openDialog ( ) ; if ( path == null ) { return null ; } } File file ; String location = MaxSystem . maxPathToNativePath ( path ) ; if ( location != null ) { file = new File ( location ) ; if ( file != null && file . isFile ( ) ) { return file ; } } if ( patcher != null ) { File patcherFolder = new File ( patcher . getPath ( ) ) ; file = new File ( patcherFolder , path ) ; if ( file != null && file . isFile ( ) ) { return file ; } } location = MaxSystem . locateFile ( path ) ; if ( location != null ) { file = new File ( location ) ; if ( file != null && file . isFile ( ) ) { return file ; } } if ( ! suppressError ) { System . err . println ( "File not found: " + path ) ; } return null ; } public static String getFileAsString ( String path ) { return getFileAsString ( getFile ( path ) ) ; } public static String getFileAsString ( File file ) { if ( file == null || ! file . exists ( ) ) { return null ; } try { return getReaderAsString ( new FileReader ( file ) ) ; } catch ( IOException e ) { System . err . println ( e . getMessage ( ) ) ; return null ; } } public static String getInputStreamAsString ( InputStream in ) { return getReaderAsString ( new InputStreamReader ( in ) ) ; } public static String getReaderAsString ( Reader r ) { StringBuilder text = new StringBuilder ( 5000 ) ; BufferedReader reader = null ; try { reader = new BufferedReader ( r ) ; char [ ] buf = new char [ 1024 ] ; int charsRead = 0 ; while ( ( charsRead = reader . read ( buf ) ) != - 1 ) { text . append ( buf , 0 , charsRead ) ; } return text . toString ( ) ; } catch ( IOException e ) { System . err . println ( e . getMessage ( ) ) ; return null ; } finally { if ( reader != null ) { try { reader . close ( ) ; } catch ( IOException e ) { System . err . println ( e . getMessage ( ) ) ; } } } } public static String getStackTrace ( Throwable t ) { Writer stw = new StringWriter ( ) ; t . printStackTrace ( new PrintWriter ( stw ) ) ; return stw . toString ( ) ; } public static boolean isPatcherSaved ( MaxPatcher patcher ) { if ( patcher != null ) { String filePath = patcher . getFilePath ( ) ; if ( filePath != null ) { filePath = filePath . toLowerCase ( ) ; if ( filePath . endsWith ( ".maxpat" ) || filePath . endsWith ( ".maxhelp" ) || filePath . endsWith ( ".json" ) || filePath . endsWith ( ".amxd" ) ) { return true ; } } } return false ; } } 
=======
class Driver { static private String usageKey = "usage" ; static public void setUsageKey ( String key ) { usageKey = key ; } static public void main ( String [ ] args ) { System . exit ( new Driver ( ) . doMain ( args ) ) ; } private boolean timing = false ; private String encoding = null ; private Localizer localizer = new Localizer ( Driver . class ) ; public int doMain ( String [ ] args ) { ErrorHandlerImpl eh = new ErrorHandlerImpl ( System . out ) ; OptionParser op = new OptionParser ( "itcdfe:p:sC:" , args ) ; PropertyMapBuilder properties = new PropertyMapBuilder ( ) ; properties . put ( ValidateProperty . ERROR_HANDLER , eh ) ; RngProperty . CHECK_ID_IDREF . add ( properties ) ; SchemaReader sr = null ; boolean compact = false ; boolean outputSimplifiedSchema = false ; List < String > catalogUris = new ArrayList < String > ( ) ; try { while ( op . moveToNextOption ( ) ) { switch ( op . getOptionChar ( ) ) { case 'i' : properties . put ( RngProperty . CHECK_ID_IDREF , null ) ; break ; case 'C' : catalogUris . add ( UriOrFile . toUri ( op . getOptionArg ( ) ) ) ; break ; case 'c' : compact = true ; break ; case 'd' : { if ( sr == null ) sr = new AutoSchemaReader ( ) ; FlagOption option = ( FlagOption ) sr . getOption ( SchemaReader . BASE_URI + "diagnose" ) ; if ( option == null ) { eh . print ( localizer . message ( "no_schematron" , op . getOptionCharString ( ) ) ) ; return 2 ; } properties . put ( option . getPropertyId ( ) , Flag . PRESENT ) ; } break ; case 't' : timing = true ; break ; case 'e' : encoding = op . getOptionArg ( ) ; break ; case 'f' : RngProperty . FEASIBLE . add ( properties ) ; break ; case 's' : outputSimplifiedSchema = true ; break ; case 'p' : { if ( sr == null ) sr = new AutoSchemaReader ( ) ; StringOption option = ( StringOption ) sr . getOption ( SchemaReader . BASE_URI + "phase" ) ; if ( option == null ) { eh . print ( localizer . message ( "no_schematron" , op . getOptionCharString ( ) ) ) ; return 2 ; } try { properties . put ( option . getPropertyId ( ) , option . valueOf ( op . getOptionArg ( ) ) ) ; } catch ( OptionArgumentException e ) { eh . print ( localizer . message ( "invalid_phase" , op . getOptionArg ( ) ) ) ; return 2 ; } } break ; } } } catch ( OptionParser . InvalidOptionException e ) { eh . print ( localizer . message ( "invalid_option" , op . getOptionCharString ( ) ) ) ; return 2 ; } catch ( OptionParser . MissingArgumentException e ) { eh . print ( localizer . message ( "option_missing_argument" , op . getOptionCharString ( ) ) ) ; return 2 ; } if ( ! catalogUris . isEmpty ( ) ) { try { properties . put ( ValidateProperty . RESOLVER , new CatalogResolver ( catalogUris ) ) ; } catch ( LinkageError e ) { eh . print ( localizer . message ( "resolver_not_found" ) ) ; return 2 ; } } if ( compact ) sr = CompactSchemaReader . getInstance ( ) ; args = op . getRemainingArgs ( ) ; if ( args . length < 1 ) { eh . print ( localizer . message ( usageKey , Version . getVersion ( Driver . class ) ) ) ; return 2 ; } long startTime = System . currentTimeMillis ( ) ; long loadedPatternTime = - 1 ; boolean hadError = false ; try { ValidationDriver driver = new ValidationDriver ( properties . toPropertyMap ( ) , sr ) ; InputSource in = ValidationDriver . uriOrFileInputSource ( args [ 0 ] ) ; if ( encoding != null ) in . setEncoding ( encoding ) ; if ( driver . loadSchema ( in ) ) { loadedPatternTime = System . currentTimeMillis ( ) ; if ( outputSimplifiedSchema ) { String simplifiedSchema = driver . getSchemaProperties ( ) . get ( RngProperty . SIMPLIFIED_SCHEMA ) ; if ( simplifiedSchema == null ) { eh . print ( localizer . message ( "no_simplified_schema" ) ) ; hadError = true ; } else System . out . print ( simplifiedSchema ) ; } for ( int i = 1 ; i < args . length ; i ++ ) { if ( ! driver . validate ( ValidationDriver . uriOrFileInputSource ( args [ i ] ) ) ) hadError = true ; } } else hadError = true ; } catch ( SAXException e ) { hadError = true ; eh . printException ( e ) ; } catch ( IOException e ) { hadError = true ; eh . printException ( e ) ; } if ( timing ) { long endTime = System . currentTimeMillis ( ) ; if ( loadedPatternTime < 0 ) loadedPatternTime = endTime ; eh . print ( localizer . message ( "elapsed_time" , new Object [ ] { loadedPatternTime - startTime , endTime - loadedPatternTime , endTime - startTime } ) ) ; } if ( hadError ) return 1 ; return 0 ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
