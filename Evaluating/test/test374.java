<<<<<<< HEAD
public class JavaClassDef < T > extends ClassDef { private final static Logger log = Logger . getLogger ( JavaClassDef . class . getName ( ) ) ; private final static L10N L = new L10N ( JavaClassDef . class ) ; private final ModuleContext _moduleContext ; private final String _name ; private final Class < T > _type ; private QuercusClass _quercusClass ; private HashSet < String > _instanceOfSet ; private HashSet < String > _instanceOfSetLowerCase ; private final boolean _isAbstract ; private final boolean _isInterface ; private final boolean _isDelegate ; private String _resourceType ; private JavaClassDef _componentDef ; protected volatile boolean _isInit ; private final HashMap < String , Value > _constMap = new HashMap < String , Value > ( ) ; private final MethodMap < AbstractJavaMethod > _functionMap = new MethodMap < AbstractJavaMethod > ( ) ; private final HashMap < StringValue , AbstractJavaMethod > _getMap = new HashMap < StringValue , AbstractJavaMethod > ( ) ; private final HashMap < StringValue , AbstractJavaMethod > _setMap = new HashMap < StringValue , AbstractJavaMethod > ( ) ; private final HashMap < StringValue , FieldMarshalPair > _fieldMap = new HashMap < StringValue , FieldMarshalPair > ( ) ; private AbstractJavaMethod _cons ; private AbstractJavaMethod __construct ; private JavaMethod __fieldGet ; private JavaMethod __fieldSet ; private FunctionArrayDelegate _funArrayDelegate ; private ArrayDelegate _arrayDelegate ; private JavaMethod __call ; private JavaMethod __toString ; private Method _printRImpl ; private Method _varDumpImpl ; private Method _entrySet ; private TraversableDelegate _traversableDelegate ; private CountDelegate _countDelegate ; private Method _iteratorMethod ; private Marshal _marshal ; private String _extension ; public JavaClassDef ( ModuleContext moduleContext , String name , Class < T > type ) { super ( null , name , null , new String [ ] { } ) ; _moduleContext = moduleContext ; _name = name ; _type = type ; _isAbstract = Modifier . isAbstract ( type . getModifiers ( ) ) ; _isInterface = type . isInterface ( ) ; _isDelegate = type . isAnnotationPresent ( ClassImplementation . class ) ; if ( type . isArray ( ) && ! isArray ( ) ) throw new IllegalStateException ( L . l ( "'{0}' needs to be called with JavaArrayClassDef" , type ) ) ; } public JavaClassDef ( ModuleContext moduleContext , String name , Class < T > type , String extension ) { this ( moduleContext , name , type ) ; _extension = extension ; moduleContext . addExtensionClass ( extension , name ) ; } private void fillInstanceOfSet ( Class type , boolean isTop ) { if ( type == null ) return ; if ( isTop && _isDelegate ) { _instanceOfSet . add ( _name ) ; _instanceOfSetLowerCase . add ( _name . toLowerCase ( ) ) ; } else { String name = type . getSimpleName ( ) ; _instanceOfSet . add ( name ) ; _instanceOfSetLowerCase . add ( name . toLowerCase ( ) ) ; } fillInstanceOfSet ( type . getSuperclass ( ) , false ) ; Class [ ] ifaceList = type . getInterfaces ( ) ; if ( ifaceList != null ) { for ( Class iface : ifaceList ) fillInstanceOfSet ( iface , false ) ; } } @ SuppressWarnings ( "unchecked" ) public static JavaClassDef create ( ModuleContext moduleContext , String name , Class < ? > type ) { if ( Double . class . isAssignableFrom ( type ) || Float . class . isAssignableFrom ( type ) ) return new DoubleClassDef ( moduleContext ) ; else if ( Long . class . isAssignableFrom ( type ) || Integer . class . isAssignableFrom ( type ) || Short . class . isAssignableFrom ( type ) || Byte . class . isAssignableFrom ( type ) ) return new LongClassDef ( moduleContext ) ; else if ( BigDecimal . class . isAssignableFrom ( type ) ) return new BigDecimalClassDef ( moduleContext ) ; else if ( BigInteger . class . isAssignableFrom ( type ) ) return new BigIntegerClassDef ( moduleContext ) ; else if ( String . class . isAssignableFrom ( type ) || Character . class . isAssignableFrom ( type ) ) return new StringClassDef ( moduleContext ) ; else if ( Boolean . class . isAssignableFrom ( type ) ) return new BooleanClassDef ( moduleContext ) ; else if ( Calendar . class . isAssignableFrom ( type ) ) return new CalendarClassDef ( moduleContext ) ; else if ( Date . class . isAssignableFrom ( type ) ) return new DateClassDef ( moduleContext ) ; else if ( URL . class . isAssignableFrom ( type ) ) return new URLClassDef ( moduleContext ) ; else if ( Map . class . isAssignableFrom ( type ) ) return new JavaMapClassDef < Object , Object > ( moduleContext , name , ( Class < Map < Object , Object > > ) type ) ; else if ( List . class . isAssignableFrom ( type ) ) return new JavaListClassDef < Object > ( moduleContext , name , ( Class < List < Object > > ) type ) ; else if ( Collection . class . isAssignableFrom ( type ) && ! Queue . class . isAssignableFrom ( type ) ) return new JavaCollectionClassDef < Object > ( moduleContext , name , ( Class < Collection < Object > > ) type ) ; else return null ; } @ Override public String getName ( ) { return _name ; } public String getSimpleName ( ) { return _type . getSimpleName ( ) ; } public Class getType ( ) { return _type ; } public String getResourceType ( ) { return _resourceType ; } protected ModuleContext getModuleContext ( ) { return _moduleContext ; } @ Override public String getExtension ( ) { return _extension ; } @ Override public boolean isA ( String name ) { if ( _instanceOfSet == null ) { _instanceOfSet = new HashSet < String > ( ) ; _instanceOfSetLowerCase = new HashSet < String > ( ) ; fillInstanceOfSet ( _type , true ) ; } return ( _instanceOfSet . contains ( name ) || _instanceOfSetLowerCase . contains ( name . toLowerCase ( ) ) ) ; } @ Override public void addInterfaces ( HashSet < String > interfaceSet ) { addInterfaces ( interfaceSet , _type , true ) ; } protected void addInterfaces ( HashSet < String > interfaceSet , Class type , boolean isTop ) { if ( type == null || Object . class . equals ( type ) ) return ; if ( isTop ) interfaceSet . add ( _name . toLowerCase ( ) ) ; else interfaceSet . add ( type . getSimpleName ( ) . toLowerCase ( ) ) ; if ( type . getInterfaces ( ) != null ) { for ( Class iface : type . getInterfaces ( ) ) { addInterfaces ( interfaceSet , iface , false ) ; } } addInterfaces ( interfaceSet , type . getSuperclass ( ) , false ) ; } @ Override public boolean isAbstract ( ) { return _isAbstract ; } public boolean isArray ( ) { return false ; } @ Override public boolean isInterface ( ) { return _isInterface ; } public boolean isDelegate ( ) { return _isDelegate ; } public JavaClassDef getComponentDef ( ) { if ( _componentDef == null ) { Class compType = getType ( ) . getComponentType ( ) ; _componentDef = _moduleContext . getJavaClassDefinition ( compType . getName ( ) ) ; } return _componentDef ; } public Value wrap ( Env env , T obj ) { if ( ! _isInit ) init ( ) ; if ( _resourceType != null ) return new JavaResourceValue < T > ( env , obj , this ) ; else return new JavaValue < T > ( env , obj , this ) ; } @ SuppressWarnings ( "unchecked" ) private int cmpObject ( Object lValue , Object rValue ) { if ( lValue == rValue ) return 0 ; if ( lValue == null ) return - 1 ; if ( rValue == null ) return 1 ; if ( lValue instanceof Comparable ) { if ( ! ( rValue instanceof Comparable ) ) return - 1 ; return ( ( Comparable < Object > ) lValue ) . compareTo ( rValue ) ; } else if ( rValue instanceof Comparable ) { return 1 ; } if ( lValue . equals ( rValue ) ) return 0 ; String lName = lValue . getClass ( ) . getName ( ) ; String rName = rValue . getClass ( ) . getName ( ) ; return lName . compareTo ( rName ) ; } public int cmpObject ( Object lValue , Object rValue , JavaClassDef < ? > rClassDef ) { int cmp = cmpObject ( lValue , rValue ) ; if ( cmp != 0 ) return cmp ; for ( Map . Entry < StringValue , FieldMarshalPair > lEntry : _fieldMap . entrySet ( ) ) { StringValue lFieldName = lEntry . getKey ( ) ; FieldMarshalPair rFieldPair = rClassDef . _fieldMap . get ( lFieldName ) ; if ( rFieldPair == null ) return 1 ; FieldMarshalPair lFieldPair = lEntry . getValue ( ) ; try { Object lResult = lFieldPair . _field . get ( lValue ) ; Object rResult = rFieldPair . _field . get ( lValue ) ; int resultCmp = cmpObject ( lResult , rResult ) ; if ( resultCmp != 0 ) return resultCmp ; } catch ( IllegalAccessException e ) { log . log ( Level . FINE , L . l ( e . getMessage ( ) ) , e ) ; return 0 ; } } return 0 ; } public Value getField ( Env env , Value qThis , StringValue name ) { AbstractJavaMethod get = _getMap . get ( name ) ; if ( get != null ) { try { return get . callMethod ( env , qThis ) ; } catch ( Exception e ) { log . log ( Level . FINE , L . l ( e . getMessage ( ) ) , e ) ; return null ; } } FieldMarshalPair fieldPair = _fieldMap . get ( name ) ; if ( fieldPair != null ) { try { Object result = fieldPair . _field . get ( qThis . toJavaObject ( ) ) ; return fieldPair . _marshal . unmarshal ( env , result ) ; } catch ( Exception e ) { log . log ( Level . FINE , L . l ( e . getMessage ( ) ) , e ) ; return null ; } } if ( __fieldGet != null ) { try { return __fieldGet . callMethod ( env , qThis , name ) ; } catch ( Exception e ) { log . log ( Level . FINE , L . l ( e . getMessage ( ) ) , e ) ; return null ; } } return null ; } public Value putField ( Env env , Value qThis , StringValue name , Value value ) { AbstractJavaMethod setter = _setMap . get ( name ) ; if ( setter != null ) { try { return setter . callMethod ( env , qThis , value ) ; } catch ( Exception e ) { log . log ( Level . FINE , L . l ( e . getMessage ( ) ) , e ) ; return NullValue . NULL ; } } FieldMarshalPair fieldPair = _fieldMap . get ( name ) ; if ( fieldPair != null ) { try { Class < ? > type = fieldPair . _field . getType ( ) ; Object marshaledValue = fieldPair . _marshal . marshal ( env , value , type ) ; fieldPair . _field . set ( qThis . toJavaObject ( ) , marshaledValue ) ; return value ; } catch ( Exception e ) { log . log ( Level . FINE , L . l ( e . getMessage ( ) ) , e ) ; return NullValue . NULL ; } } if ( __fieldSet != null ) { try { return __fieldSet . callMethod ( env , qThis , name , value ) ; } catch ( Exception e ) { log . log ( Level . FINE , L . l ( e . getMessage ( ) ) , e ) ; return NullValue . NULL ; } } return null ; } public Marshal getMarshal ( ) { return _marshal ; } @ Override public ObjectValue newInstance ( Env env , QuercusClass qClass ) { return null ; } public Value newInstance ( ) { return null ; } @ Override public Value callNew ( Env env , Value [ ] args ) { if ( _cons != null ) { Value value = _cons . callMethod ( env , null , args ) ; if ( __construct != null ) __construct . callMethod ( env , value , args ) ; return value ; } else return NullValue . NULL ; } public AbstractFunction findFunction ( String name ) { return _functionMap . get ( name ) ; } public AbstractFunction getCallMethod ( ) { return __call ; } public Value callMethod ( Env env , Value qThis , int hash , char [ ] name , int nameLen , Expr [ ] args ) { AbstractJavaMethod method = _functionMap . get ( hash , name , nameLen ) ; if ( method == null ) { env . warning ( L . l ( "{0}::{1} is an unknown method." , _name , toMethod ( name , nameLen ) ) ) ; return NullValue . NULL ; } return method . callMethod ( env , qThis , args ) ; } public Value callMethod ( Env env , Value qThis , int hash , char [ ] name , int nameLen , Value [ ] args ) { AbstractJavaMethod method = _functionMap . get ( hash , name , nameLen ) ; if ( method != null ) return method . callMethod ( env , qThis , args ) ; else if ( __call != null ) { Value [ ] extArgs = new Value [ args . length + 1 ] ; extArgs [ 0 ] = env . createString ( name , nameLen ) ; System . arraycopy ( args , 0 , extArgs , 1 , args . length ) ; return __call . callMethod ( env , qThis , extArgs ) ; } else { env . error ( L . l ( "'{0}::{1}' is an unknown method" , _name , toMethod ( name , nameLen ) ) ) ; return NullValue . NULL ; } } public Value callMethod ( Env env , Value qThis , int hash , char [ ] name , int nameLen ) { AbstractJavaMethod method = _functionMap . get ( hash , name , nameLen ) ; if ( method != null ) return method . callMethod ( env , qThis ) ; else if ( __call != null ) return __call . callMethod ( env , qThis , env . createString ( name , nameLen ) ) ; else { env . error ( L . l ( "'{0}::{1}()' is an unknown method" , _name , toMethod ( name , nameLen ) ) ) ; return NullValue . NULL ; } } public Value callMethod ( Env env , Value qThis , int hash , char [ ] name , int nameLen , Value a1 ) { AbstractJavaMethod method = _functionMap . get ( hash , name , nameLen ) ; if ( method != null ) return method . callMethod ( env , qThis , a1 ) ; else if ( __call != null ) return __call . callMethod ( env , qThis , env . createString ( name , nameLen ) , a1 ) ; else { env . error ( L . l ( "'{0}::{1}(a1)' is an unknown method" , _name , toMethod ( name , nameLen ) ) ) ; return NullValue . NULL ; } } public Value callMethod ( Env env , Value qThis , int hash , char [ ] name , int nameLen , Value a1 , Value a2 ) { AbstractJavaMethod method = _functionMap . get ( hash , name , nameLen ) ; if ( method != null ) return method . callMethod ( env , qThis , a1 , a2 ) ; else if ( __call != null ) return __call . callMethod ( env , qThis , env . createString ( name , nameLen ) , a1 , a2 ) ; else { env . error ( L . l ( "'{0}::{1}(a1,a2)' is an unknown method" , _name , toMethod ( name , nameLen ) ) ) ; return NullValue . NULL ; } } public Value callMethod ( Env env , Value qThis , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 ) { AbstractJavaMethod method = _functionMap . get ( hash , name , nameLen ) ; if ( method != null ) return method . callMethod ( env , qThis , a1 , a2 , a3 ) ; else if ( __call != null ) return __call . callMethod ( env , qThis , env . createString ( name , nameLen ) , a1 , a2 , a3 ) ; else { env . error ( L . l ( "'{0}::{1}(a1,a2,a3)' is an unknown method" , _name , toMethod ( name , nameLen ) ) ) ; return NullValue . NULL ; } } public Value callMethod ( Env env , Value qThis , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 , Value a4 ) { AbstractJavaMethod method = _functionMap . get ( hash , name , nameLen ) ; if ( method != null ) return method . callMethod ( env , qThis , a1 , a2 , a3 , a4 ) ; else if ( __call != null ) return __call . callMethod ( env , qThis , env . createString ( name , nameLen ) , a1 , a2 , a3 , a4 ) ; else { env . error ( L . l ( "'{0}::{1}(a1,a2,a3,a4)' is an unknown method" , _name , toMethod ( name , nameLen ) ) ) ; return NullValue . NULL ; } } public Value callMethod ( Env env , Value qThis , int hash , char [ ] name , int nameLen , Value a1 , Value a2 , Value a3 , Value a4 , Value a5 ) { AbstractJavaMethod method = _functionMap . get ( hash , name , nameLen ) ; if ( method != null ) return method . callMethod ( env , qThis , a1 , a2 , a3 , a4 , a5 ) ; else if ( __call != null ) return __call . callMethod ( env , qThis , new Value [ ] { env . createString ( name , nameLen ) , a1 , a2 , a3 , a4 , a5 } ) ; else { env . error ( L . l ( "'{0}::{1}(a1,a2,a3,a4,a5)' is an unknown method" , _name , toMethod ( name , nameLen ) ) ) ; return NullValue . NULL ; } } private String toMethod ( char [ ] name , int nameLen ) { return new String ( name , 0 , nameLen ) ; } @ SuppressWarnings ( "unchecked" ) public Set < Map . Entry < Value , Value > > entrySet ( Object obj ) { try { if ( _entrySet == null ) { return null ; } return ( Set < Map . Entry < Value , Value > > ) _entrySet . invoke ( obj ) ; } catch ( Exception e ) { throw new QuercusException ( e ) ; } } @ Override public void initClass ( QuercusClass cl ) { init ( ) ; if ( _cons != null ) { cl . setConstructor ( _cons ) ; cl . addMethod ( "__construct" , _cons ) ; } if ( __construct != null ) { cl . addMethod ( "__construct" , __construct ) ; } for ( AbstractJavaMethod value : _functionMap . values ( ) ) { cl . addMethod ( value . getName ( ) , value ) ; } if ( __fieldGet != null ) cl . setFieldGet ( __fieldGet ) ; if ( __fieldSet != null ) cl . setFieldSet ( __fieldSet ) ; if ( __call != null ) cl . setCall ( __call ) ; if ( __toString != null ) { cl . addMethod ( "__toString" , __toString ) ; } if ( _arrayDelegate != null ) cl . setArrayDelegate ( _arrayDelegate ) ; else if ( _funArrayDelegate != null ) cl . setArrayDelegate ( _funArrayDelegate ) ; if ( _traversableDelegate != null ) cl . setTraversableDelegate ( _traversableDelegate ) ; else if ( cl . getTraversableDelegate ( ) == null && _iteratorMethod != null ) { cl . setTraversableDelegate ( new JavaTraversableDelegate ( _iteratorMethod ) ) ; } if ( _countDelegate != null ) cl . setCountDelegate ( _countDelegate ) ; for ( Map . Entry < String , Value > entry : _constMap . entrySet ( ) ) { cl . addConstant ( entry . getKey ( ) , new LiteralExpr ( entry . getValue ( ) ) ) ; } } public Value findConstant ( Env env , String name ) { return _constMap . get ( name ) ; } public void initInstance ( Env env , Value value ) { } public QuercusClass getQuercusClass ( ) { if ( _quercusClass == null ) { init ( ) ; _quercusClass = new QuercusClass ( _moduleContext , this , null ) ; } return _quercusClass ; } @ Override public AbstractFunction findConstructor ( ) { return null ; } @ Override public final void init ( ) { if ( _isInit ) return ; synchronized ( this ) { if ( _isInit ) return ; super . init ( ) ; try { initInterfaceList ( _type ) ; introspect ( ) ; } finally { _isInit = true ; } } } private void initInterfaceList ( Class type ) { Class [ ] ifaces = type . getInterfaces ( ) ; if ( ifaces == null ) return ; for ( Class iface : ifaces ) { JavaClassDef javaClassDef = _moduleContext . getJavaClassDefinition ( iface ) ; if ( javaClassDef != null ) addInterface ( javaClassDef . getName ( ) ) ; initInterfaceList ( iface ) ; } } private void introspect ( ) { introspectConstants ( _type ) ; introspectMethods ( _moduleContext , _type ) ; introspectFields ( _moduleContext , _type ) ; _marshal = new JavaMarshal ( this , false ) ; Method consMethod = getConsMethod ( _type ) ; if ( consMethod != null ) { if ( Modifier . isStatic ( consMethod . getModifiers ( ) ) ) _cons = new JavaMethod ( _moduleContext , consMethod ) ; else __construct = new JavaMethod ( _moduleContext , consMethod ) ; } if ( _cons == null ) { Constructor [ ] cons = _type . getConstructors ( ) ; if ( cons . length > 0 ) { int i ; for ( i = 0 ; i < cons . length ; i ++ ) { if ( cons [ i ] . isAnnotationPresent ( Construct . class ) ) break ; } if ( i < cons . length ) { _cons = new JavaConstructor ( _moduleContext , cons [ i ] ) ; } else { _cons = new JavaConstructor ( _moduleContext , cons [ 0 ] ) ; for ( i = 1 ; i < cons . length ; i ++ ) { _cons = _cons . overload ( new JavaConstructor ( _moduleContext , cons [ i ] ) ) ; } } } else _cons = null ; } introspectAnnotations ( _type ) ; } private void introspectAnnotations ( Class type ) { try { if ( type == null || type == Object . class ) return ; for ( Class < ? > iface : type . getInterfaces ( ) ) introspectAnnotations ( iface ) ; introspectAnnotations ( type . getSuperclass ( ) ) ; for ( Annotation annotation : type . getAnnotations ( ) ) { if ( annotation . annotationType ( ) == Delegates . class ) { Class [ ] delegateClasses = ( ( Delegates ) annotation ) . value ( ) ; for ( Class cl : delegateClasses ) { boolean isDelegate = addDelegate ( cl ) ; if ( ! isDelegate ) throw new IllegalArgumentException ( L . l ( "unknown @Delegate class '{0}'" , cl ) ) ; } } else if ( annotation . annotationType ( ) == ResourceType . class ) { _resourceType = ( ( ResourceType ) annotation ) . value ( ) ; } } } catch ( RuntimeException e ) { throw e ; } catch ( InstantiationException e ) { throw new QuercusModuleException ( e . getCause ( ) ) ; } catch ( Exception e ) { throw new QuercusModuleException ( e ) ; } } private boolean addDelegate ( Class cl ) throws InstantiationException , IllegalAccessException { boolean isDelegate = false ; if ( TraversableDelegate . class . isAssignableFrom ( cl ) ) { _traversableDelegate = ( TraversableDelegate ) cl . newInstance ( ) ; isDelegate = true ; } if ( ArrayDelegate . class . isAssignableFrom ( cl ) ) { _arrayDelegate = ( ArrayDelegate ) cl . newInstance ( ) ; isDelegate = true ; } if ( CountDelegate . class . isAssignableFrom ( cl ) ) { _countDelegate = ( CountDelegate ) cl . newInstance ( ) ; isDelegate = true ; } return isDelegate ; } private Method getConsMethod ( Class type ) { Method [ ] methods = type . getMethods ( ) ; for ( int i = 0 ; i < methods . length ; i ++ ) { Method method = methods [ i ] ; if ( ! method . getName ( ) . equals ( "__construct" ) ) continue ; if ( ! Modifier . isPublic ( method . getModifiers ( ) ) ) continue ; return method ; } return null ; } protected void setCons ( Method method ) { _cons = new JavaMethod ( _moduleContext , method ) ; } private void introspectFields ( ModuleContext moduleContext , Class type ) { if ( type == null || type . equals ( Object . class ) ) return ; if ( ! Modifier . isPublic ( type . getModifiers ( ) ) ) return ; Method [ ] methods = type . getMethods ( ) ; for ( Method method : methods ) { if ( Modifier . isStatic ( method . getModifiers ( ) ) ) continue ; String methodName = method . getName ( ) ; int length = methodName . length ( ) ; if ( length > 3 ) { if ( methodName . startsWith ( "get" ) ) { StringValue quercusName = javaToQuercusConvert ( methodName . substring ( 3 , length ) ) ; AbstractJavaMethod existingGetter = _getMap . get ( quercusName ) ; AbstractJavaMethod newGetter = new JavaMethod ( moduleContext , method ) ; if ( existingGetter != null ) { try { newGetter = existingGetter . overload ( newGetter ) ; } catch ( Exception e ) { log . log ( Level . WARNING , "Could not introspect a overloaded method" , e ) ; continue ; } } _getMap . put ( quercusName , newGetter ) ; } else if ( methodName . startsWith ( "is" ) ) { StringValue quercusName = javaToQuercusConvert ( methodName . substring ( 2 , length ) ) ; AbstractJavaMethod existingGetter = _getMap . get ( quercusName ) ; AbstractJavaMethod newGetter = new JavaMethod ( moduleContext , method ) ; if ( existingGetter != null ) { newGetter = existingGetter . overload ( newGetter ) ; } _getMap . put ( quercusName , newGetter ) ; } else if ( methodName . startsWith ( "set" ) ) { StringValue quercusName = javaToQuercusConvert ( methodName . substring ( 3 , length ) ) ; AbstractJavaMethod existingSetter = _setMap . get ( quercusName ) ; AbstractJavaMethod newSetter = new JavaMethod ( moduleContext , method ) ; if ( existingSetter != null ) newSetter = existingSetter . overload ( newSetter ) ; _setMap . put ( quercusName , newSetter ) ; } else if ( "__get" . equals ( methodName ) ) { if ( _funArrayDelegate == null ) _funArrayDelegate = new FunctionArrayDelegate ( ) ; _funArrayDelegate . setArrayGet ( new JavaMethod ( moduleContext , method ) ) ; } else if ( "__set" . equals ( methodName ) ) { if ( _funArrayDelegate == null ) _funArrayDelegate = new FunctionArrayDelegate ( ) ; _funArrayDelegate . setArrayPut ( new JavaMethod ( moduleContext , method ) ) ; } else if ( "__getField" . equals ( methodName ) ) { __fieldGet = new JavaMethod ( moduleContext , method ) ; } else if ( "__setField" . equals ( methodName ) ) { __fieldSet = new JavaMethod ( moduleContext , method ) ; } else if ( "__fieldGet" . equals ( methodName ) ) { __fieldGet = new JavaMethod ( moduleContext , method ) ; } else if ( "__fieldSet" . equals ( methodName ) ) { __fieldSet = new JavaMethod ( moduleContext , method ) ; } } } Field [ ] fields = type . getFields ( ) ; for ( Field field : fields ) { if ( Modifier . isStatic ( field . getModifiers ( ) ) ) continue ; MarshalFactory factory = moduleContext . getMarshalFactory ( ) ; Marshal marshal = factory . create ( field . getType ( ) , false ) ; _fieldMap . put ( new ConstStringValue ( field . getName ( ) ) , new FieldMarshalPair ( field , marshal ) ) ; } } private StringValue javaToQuercusConvert ( String s ) { if ( s . length ( ) == 1 ) { return new ConstStringValue ( new char [ ] { Character . toLowerCase ( s . charAt ( 0 ) ) } ) ; } if ( Character . isUpperCase ( s . charAt ( 1 ) ) ) return new ConstStringValue ( s ) ; else { StringBuilderValue sb = new StringBuilderValue ( ) ; sb . append ( Character . toLowerCase ( s . charAt ( 0 ) ) ) ; int length = s . length ( ) ; for ( int i = 1 ; i < length ; i ++ ) { sb . append ( s . charAt ( i ) ) ; } return sb ; } } private void introspectConstants ( Class type ) { if ( type == null || type . equals ( Object . class ) ) return ; if ( ! Modifier . isPublic ( type . getModifiers ( ) ) ) return ; Class [ ] ifcs = type . getInterfaces ( ) ; for ( Class ifc : ifcs ) { introspectConstants ( ifc ) ; } Field [ ] fields = type . getDeclaredFields ( ) ; for ( Field field : fields ) { if ( _constMap . get ( field . getName ( ) ) != null ) continue ; else if ( ! Modifier . isPublic ( field . getModifiers ( ) ) ) continue ; else if ( ! Modifier . isStatic ( field . getModifiers ( ) ) ) continue ; else if ( ! Modifier . isFinal ( field . getModifiers ( ) ) ) continue ; try { Value value = Quercus . objectToValue ( field . get ( null ) ) ; if ( value != null ) _constMap . put ( field . getName ( ) . intern ( ) , value ) ; } catch ( Throwable e ) { log . log ( Level . FINER , e . toString ( ) , e ) ; } } introspectConstants ( type . getSuperclass ( ) ) ; } private void introspectMethods ( ModuleContext moduleContext , Class type ) { if ( type == null || type . equals ( Object . class ) ) return ; Method [ ] methods = type . getMethods ( ) ; for ( Method method : methods ) { if ( ! Modifier . isPublic ( method . getModifiers ( ) ) ) continue ; if ( method . getDeclaringClass ( ) == Object . class ) continue ; if ( "iterator" . equals ( method . getName ( ) ) && method . getParameterTypes ( ) . length == 0 && Iterator . class . isAssignableFrom ( method . getReturnType ( ) ) ) { _iteratorMethod = method ; } if ( "printRImpl" . equals ( method . getName ( ) ) ) { _printRImpl = method ; } else if ( "varDumpImpl" . equals ( method . getName ( ) ) ) { _varDumpImpl = method ; } else if ( method . isAnnotationPresent ( EntrySet . class ) ) { _entrySet = method ; } else if ( "__call" . equals ( method . getName ( ) ) ) { __call = new JavaMethod ( moduleContext , method ) ; } else if ( "__toString" . equals ( method . getName ( ) ) ) { __toString = new JavaMethod ( moduleContext , method ) ; } else { if ( method . getName ( ) . startsWith ( "quercus_" ) ) throw new UnsupportedOperationException ( L . l ( "{0}: use @Name instead" , method . getName ( ) ) ) ; JavaMethod newFun = new JavaMethod ( moduleContext , method ) ; AbstractJavaMethod fun = _functionMap . get ( newFun . getName ( ) ) ; if ( fun != null ) { try { fun = fun . overload ( newFun ) ; } catch ( Exception e ) { log . log ( Level . WARNING , "Could not introspect a overloaded method" , e ) ; continue ; } } else fun = newFun ; _functionMap . put ( fun . getName ( ) , fun ) ; } } introspectMethods ( moduleContext , type . getSuperclass ( ) ) ; Class [ ] ifcs = type . getInterfaces ( ) ; for ( Class ifc : ifcs ) { introspectMethods ( moduleContext , ifc ) ; } } public JavaMethod getToString ( ) { return __toString ; } public StringValue toReprString ( Env env , JavaValue value ) { if ( __toString == null ) return null ; return __toString . callMethod ( env , value , new Expr [ 0 ] ) . toStringValue ( env ) ; } public boolean printRImpl ( Env env , Object obj , WriteStream out , int depth , IdentityHashMap < Value , String > valueSet ) throws IOException { try { if ( _printRImpl == null ) { return false ; } _printRImpl . invoke ( obj , env , out , depth , valueSet ) ; return true ; } catch ( Exception e ) { throw new QuercusException ( e ) ; } } public boolean varDumpImpl ( Env env , Object obj , WriteStream out , int depth , IdentityHashMap < Value , String > valueSet ) throws IOException { try { if ( _varDumpImpl == null ) return false ; _varDumpImpl . invoke ( obj , env , out , depth , valueSet ) ; return true ; } catch ( Exception e ) { throw new QuercusException ( e ) ; } } private class JavaTraversableDelegate implements TraversableDelegate { private Method _iteratorMethod ; public JavaTraversableDelegate ( Method iterator ) { _iteratorMethod = iterator ; } public Iterator < Map . Entry < Value , Value > > getIterator ( Env env , ObjectValue qThis ) { try { Iterator iterator = ( Iterator ) _iteratorMethod . invoke ( qThis . toJavaObject ( ) ) ; return new JavaIterator ( env , iterator ) ; } catch ( Exception e ) { throw new QuercusRuntimeException ( e ) ; } } public Iterator < Value > getKeyIterator ( Env env , ObjectValue qThis ) { try { Iterator iterator = ( Iterator ) _iteratorMethod . invoke ( qThis . toJavaObject ( ) ) ; return new JavaKeyIterator ( iterator ) ; } catch ( Exception e ) { throw new QuercusRuntimeException ( e ) ; } } public Iterator < Value > getValueIterator ( Env env , ObjectValue qThis ) { try { Iterator iterator = ( Iterator ) _iteratorMethod . invoke ( qThis . toJavaObject ( ) ) ; return new JavaValueIterator ( env , iterator ) ; } catch ( Exception e ) { throw new QuercusRuntimeException ( e ) ; } } } private class JavaKeyIterator implements Iterator < Value > { private Iterator _iterator ; private int _index ; public JavaKeyIterator ( Iterator iterator ) { _iterator = iterator ; } public Value next ( ) { _iterator . next ( ) ; return LongValue . create ( _index ++ ) ; } public boolean hasNext ( ) { return _iterator . hasNext ( ) ; } public void remove ( ) { throw new UnsupportedOperationException ( ) ; } } private class JavaValueIterator implements Iterator < Value > { private Env _env ; private Iterator _iterator ; public JavaValueIterator ( Env env , Iterator iterator ) { _env = env ; _iterator = iterator ; } public Value next ( ) { return _env . wrapJava ( _iterator . next ( ) ) ; } public boolean hasNext ( ) { if ( _iterator != null ) return _iterator . hasNext ( ) ; else return false ; } public void remove ( ) { throw new UnsupportedOperationException ( ) ; } } private class JavaIterator implements Iterator < Map . Entry < Value , Value > > { private Env _env ; private Iterator _iterator ; private int _index ; public JavaIterator ( Env env , Iterator iterator ) { _env = env ; _iterator = iterator ; } @ SuppressWarnings ( "unchecked" ) public Map . Entry < Value , Value > next ( ) { Object next = _iterator . next ( ) ; int index = _index ++ ; if ( next instanceof Map . Entry ) { Map . Entry entry = ( Map . Entry ) next ; if ( entry . getKey ( ) instanceof Value && entry . getValue ( ) instanceof Value ) { return ( Map . Entry < Value , Value > ) entry ; } else { Value key = _env . wrapJava ( entry . getKey ( ) ) ; Value val = _env . wrapJava ( entry . getValue ( ) ) ; return new JavaEntry ( key , val ) ; } } else { return new JavaEntry ( LongValue . create ( index ) , _env . wrapJava ( next ) ) ; } } public boolean hasNext ( ) { if ( _iterator != null ) return _iterator . hasNext ( ) ; else return false ; } public void remove ( ) { throw new UnsupportedOperationException ( ) ; } } private class JavaEntry implements Map . Entry < Value , Value > { private Value _key ; private Value _value ; public JavaEntry ( Value key , Value value ) { _key = key ; _value = value ; } public Value getKey ( ) { return _key ; } public Value getValue ( ) { return _value ; } public Value setValue ( Value value ) { throw new UnsupportedOperationException ( ) ; } } private static class FieldMarshalPair { public Field _field ; public Marshal _marshal ; public FieldMarshalPair ( Field field , Marshal marshal ) { _field = field ; _marshal = marshal ; } } private static class LongClassDef extends JavaClassDef < Long > { LongClassDef ( ModuleContext module ) { super ( module , "Long" , Long . class ) ; } @ Override public Value wrap ( Env env , Long obj ) { return LongValue . create ( ( ( Number ) obj ) . longValue ( ) ) ; } } private static class DoubleClassDef extends JavaClassDef < Double > { DoubleClassDef ( ModuleContext module ) { super ( module , "Double" , Double . class ) ; } @ Override public Value wrap ( Env env , Double obj ) { return new DoubleValue ( ( ( Number ) obj ) . doubleValue ( ) ) ; } } private static class BigIntegerClassDef extends JavaClassDef < BigInteger > { BigIntegerClassDef ( ModuleContext module ) { super ( module , "BigInteger" , BigInteger . class ) ; } @ Override public Value wrap ( Env env , BigInteger obj ) { return new BigIntegerValue ( env , obj , this ) ; } } private static class BigDecimalClassDef extends JavaClassDef < BigDecimal > { BigDecimalClassDef ( ModuleContext module ) { super ( module , "BigDecimal" , BigDecimal . class ) ; } @ Override public Value wrap ( Env env , BigDecimal obj ) { return new BigDecimalValue ( env , obj , this ) ; } } private static class StringClassDef extends JavaClassDef < String > { StringClassDef ( ModuleContext module ) { super ( module , "String" , String . class ) ; } @ Override public Value wrap ( Env env , String obj ) { return env . createStringOld ( ( String ) obj ) ; } } private static class BooleanClassDef extends JavaClassDef < Boolean > { BooleanClassDef ( ModuleContext module ) { super ( module , "Boolean" , Boolean . class ) ; } @ Override public Value wrap ( Env env , Boolean obj ) { if ( Boolean . TRUE . equals ( obj ) ) return BooleanValue . TRUE ; else return BooleanValue . FALSE ; } } private static class CalendarClassDef extends JavaClassDef < Calendar > { CalendarClassDef ( ModuleContext module ) { super ( module , "Calendar" , Calendar . class ) ; } @ Override public Value wrap ( Env env , Calendar obj ) { return new JavaCalendarValue ( env , ( Calendar ) obj , this ) ; } } private static class DateClassDef extends JavaClassDef < Date > { DateClassDef ( ModuleContext module ) { super ( module , "Date" , Date . class ) ; } @ Override public Value wrap ( Env env , Date obj ) { return new JavaDateValue ( env , obj , this ) ; } } private static class URLClassDef extends JavaClassDef < URL > { URLClassDef ( ModuleContext module ) { super ( module , "URL" , URL . class ) ; } @ Override public Value wrap ( Env env , URL obj ) { return new JavaURLValue ( env , obj , this ) ; } } } 
=======
public class Constants { private Constants ( ) { } static public final String RELAXNG_COMPACT_URI = "http://www.iana.org/assignments/media-types/application/relax-ng-compact-syntax" ; static public final String RELAXNG_XML_URI = XMLConstants . RELAXNG_NS_URI ; } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
