<<<<<<< HEAD
public class JavaValue < T > extends ObjectValue implements Serializable { private static final long serialVersionUID = 1L ; private JavaClassDef < T > _classDef ; protected Env _env ; protected T _object ; public JavaValue ( Env env , T object , JavaClassDef < T > def ) { super ( ) ; setQuercusClass ( env . createJavaQuercusClass ( def ) ) ; _env = env ; _classDef = def ; _object = object ; } protected JavaClassDef getJavaClassDef ( ) { return _classDef ; } @ Override public String getClassName ( ) { return _classDef . getName ( ) ; } public long toLong ( ) { return StringValue . parseLong ( toReprString ( Env . getInstance ( ) ) ) ; } public double toDouble ( ) { return toDouble ( toReprString ( Env . getInstance ( ) ) . toString ( ) ) ; } public static double toDouble ( String s ) { int len = s . length ( ) ; int i = 0 ; int ch = 0 ; if ( i < len && ( ( ch = s . charAt ( i ) ) == '+' || ch == '-' ) ) { i ++ ; } for ( ; i < len && '0' <= ( ch = s . charAt ( i ) ) && ch <= '9' ; i ++ ) { } if ( ch == '.' ) { for ( i ++ ; i < len && '0' <= ( ch = s . charAt ( i ) ) && ch <= '9' ; i ++ ) { } } if ( ch == 'e' || ch == 'E' ) { int e = i ++ ; if ( i < len && ( ch = s . charAt ( i ) ) == '+' || ch == '-' ) { i ++ ; } for ( ; i < len && '0' <= ( ch = s . charAt ( i ) ) && ch <= '9' ; i ++ ) { } if ( i == e + 1 ) i = e ; } if ( i != len ) return 1 ; else return Double . parseDouble ( s ) ; } @ Override public StringValue toReprString ( Env env ) { StringValue value = _classDef . toReprString ( env , this ) ; if ( value == null ) value = env . createStringOld ( toString ( ) ) ; return value ; } @ Override protected void printRImpl ( Env env , WriteStream out , int depth , IdentityHashMap < Value , String > valueSet ) throws IOException { if ( _classDef . printRImpl ( env , _object , out , depth , valueSet ) ) return ; Set < ? extends Map . Entry < Value , Value > > entrySet = entrySet ( ) ; if ( entrySet == null ) { out . print ( "resource(" + toReprString ( env ) + ")" ) ; return ; } out . print ( _classDef . getSimpleName ( ) ) ; out . println ( " Object" ) ; printRDepth ( out , depth ) ; out . print ( "(" ) ; for ( Map . Entry < Value , Value > entry : entrySet ) { out . println ( ) ; printRDepth ( out , depth ) ; out . print ( "    [" + entry . getKey ( ) + "] => " ) ; entry . getValue ( ) . printRImpl ( env , out , depth + 1 , valueSet ) ; } out . println ( ) ; printRDepth ( out , depth ) ; out . println ( ")" ) ; } @ Override protected void varDumpImpl ( Env env , WriteStream out , int depth , IdentityHashMap < Value , String > valueSet ) throws IOException { Value oldThis = env . setThis ( this ) ; try { if ( ! _classDef . varDumpImpl ( env , _object , out , depth , valueSet ) ) out . print ( "resource(" + toReprString ( env ) + ")" ) ; } finally { env . setThis ( oldThis ) ; } } @ Override public Value getField ( Env env , StringValue name ) { Value value = _classDef . getField ( env , this , name ) ; if ( value != null ) return value ; else return NullValue . NULL ; } @ Override public Value putField ( Env env , StringValue name , Value value ) { Value oldValue = _classDef . putField ( env , this , name , value ) ; if ( oldValue != null ) return oldValue ; else return NullValue . NULL ; } public Set < ? extends Map . Entry < Value , Value > > entrySet ( ) { return _classDef . entrySet ( _object ) ; } @ Override public Value toKey ( ) { return new LongValue ( System . identityHashCode ( this ) ) ; } @ Override public int cmpObject ( ObjectValue rValue ) { if ( rValue == this ) return 0 ; if ( ! ( rValue instanceof JavaValue ) ) return - 1 ; Object rObject = rValue . toJavaObject ( ) ; return _classDef . cmpObject ( _object , rObject , ( ( JavaValue ) rValue ) . _classDef ) ; } @ Override public boolean isObject ( ) { return true ; } @ Override public boolean isResource ( ) { return false ; } @ Override public String getType ( ) { return "object" ; } @ Override public AbstractFunction findFunction ( String methodName ) { return _classDef . findFunction ( methodName ) ; } @ Override public Value callMethod ( Env env , int hash , char [ ] name , int nameLen , Expr [ ] args ) { return _classDef . callMethod ( env , this , hash , name , nameLen , args ) ; } @ Override public Value callMethod ( Env env , int hash , char [ ] name , int nameLen , Value [ ] args ) { return _classDef . callMethod ( env , this , hash , name , nameLen , args ) ; } @ Override public Value callMethod ( Env env , int hash , char [ ] name , int nameLen ) { return _classDef . callMethod ( env , this , hash , name , nameLen ) ; } @ Override public Value callMethod ( Env env , int hash , char [ ] name , int nameLen , Value a1 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 ) ; } @ Override public Value callMethod ( Env env , int hash , char [ ] name , int nameLen , Value a1 , Value a2 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 , a2 ) ; } @ Override public Value callMethod ( Env env , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 , a2 , a3 ) ; } @ Override public Value callMethod ( Env env , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 , Value a4 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 , a2 , a3 , a4 ) ; } @ Override public Value callMethod ( Env env , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 , Value a4 , Value a5 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 , a2 , a3 , a4 , a5 ) ; } @ Override public Value callMethodRef ( Env env , int hash , char [ ] name , int nameLen , Expr [ ] args ) { return _classDef . callMethod ( env , this , hash , name , nameLen , args ) ; } @ Override public Value callMethodRef ( Env env , int hash , char [ ] name , int nameLen , Value [ ] args ) { return _classDef . callMethod ( env , this , hash , name , nameLen , args ) ; } @ Override public Value callMethodRef ( Env env , int hash , char [ ] name , int nameLen ) { return _classDef . callMethod ( env , this , hash , name , nameLen ) ; } @ Override public Value callMethodRef ( Env env , int hash , char [ ] name , int nameLen , Value a1 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 ) ; } @ Override public Value callMethodRef ( Env env , int hash , char [ ] name , int nameLen , Value a1 , Value a2 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 , a2 ) ; } @ Override public Value callMethodRef ( Env env , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 , a2 , a3 ) ; } @ Override public Value callMethodRef ( Env env , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 , Value a4 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 , a2 , a3 , a4 ) ; } @ Override public Value callMethodRef ( Env env , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 , Value a4 , Value a5 ) { return _classDef . callMethod ( env , this , hash , name , nameLen , a1 , a2 , a3 , a4 , a5 ) ; } @ Override public void serialize ( Env env , StringBuilder sb , SerializeMap map ) { String name = _classDef . getSimpleName ( ) ; Set < ? extends Map . Entry < Value , Value > > entrySet = entrySet ( ) ; if ( entrySet != null ) { sb . append ( "O:" ) ; sb . append ( name . length ( ) ) ; sb . append ( ":\"" ) ; sb . append ( name ) ; sb . append ( "\":" ) ; sb . append ( entrySet . size ( ) ) ; sb . append ( ":{" ) ; for ( Map . Entry < Value , Value > entry : entrySet ) { entry . getKey ( ) . serialize ( env , sb ) ; entry . getValue ( ) . serialize ( env , sb , map ) ; } sb . append ( "}" ) ; } else { sb . append ( "i:0;" ) ; } } public String toString ( ) { return String . valueOf ( _object ) ; } @ Override public Object toJavaObject ( ) { return _object ; } @ SuppressWarnings ( "unchecked" ) @ Override public < TT > TT toJavaObject ( Env env , Class < TT > type ) { if ( type . isAssignableFrom ( _object . getClass ( ) ) ) { return ( TT ) _object ; } else { env . warning ( L . l ( "Can't assign {0} to {1}" , _object . getClass ( ) . getName ( ) , type . getName ( ) ) ) ; return null ; } } @ SuppressWarnings ( "unchecked" ) @ Override public < TT > TT toJavaObjectNotNull ( Env env , Class < TT > type ) { Class < ? > objClass = _object . getClass ( ) ; if ( objClass == type || type . isAssignableFrom ( objClass ) ) { return ( TT ) _object ; } else { env . warning ( L . l ( "Can't assign {0} to {1}" , objClass . getName ( ) , type . getName ( ) ) ) ; return null ; } } @ SuppressWarnings ( "unchecked" ) @ Override public < K , V > Map < K , V > toJavaMap ( Env env , Class < ? extends Map < K , V > > type ) { if ( type . isAssignableFrom ( _object . getClass ( ) ) ) { return ( Map < K , V > ) _object ; } else { env . warning ( L . l ( "Can't assign {0} to {1}" , _object . getClass ( ) . getName ( ) , type . getName ( ) ) ) ; return null ; } } @ Override public InputStream toInputStream ( ) { if ( _object instanceof InputStream ) return ( InputStream ) _object ; else if ( _object instanceof File ) { try { InputStream is = new FileInputStream ( ( File ) _object ) ; Env . getCurrent ( ) . addCleanup ( new EnvCloseable ( is ) ) ; return is ; } catch ( IOException e ) { throw new QuercusException ( e ) ; } } else return super . toInputStream ( ) ; } private static void printRDepth ( WriteStream out , int depth ) throws IOException { for ( int i = 0 ; i < 8 * depth ; i ++ ) out . print ( ' ' ) ; } private void writeObject ( ObjectOutputStream out ) throws IOException { out . writeObject ( _classDef . getType ( ) . getCanonicalName ( ) ) ; out . writeObject ( _object ) ; } @ SuppressWarnings ( "unchecked" ) private void readObject ( ObjectInputStream in ) throws ClassNotFoundException , IOException { _env = Env . getInstance ( ) ; _classDef = _env . getJavaClassDefinition ( ( String ) in . readObject ( ) ) ; int id = _env . getQuercus ( ) . getClassId ( _classDef . getName ( ) ) ; setQuercusClass ( _env . createQuercusClass ( id , _classDef , null ) ) ; _object = ( T ) in . readObject ( ) ; } } 
=======
interface Constraint { void activate ( PatternManager pm ) ; } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
