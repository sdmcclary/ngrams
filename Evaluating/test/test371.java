public class IniDefinition { private L10N L = new L10N ( IniDefinition . class ) ; public static final int PHP_INI_USER = 1 ; public static final int PHP_INI_PERDIR = 2 ; public static final int PHP_INI_SYSTEM = 4 ; public static final int PHP_INI_ALL = 7 ; public static IniDefinition NULL = new Null ( ) ; private final String _name ; private final int _scope ; private final Value _deflt ; private final Type _type ; public enum Type { BOOLEAN , STRING , LONG } ; public IniDefinition ( String name , Type type , Value deflt , int scope ) { assert name != null ; assert deflt != null ; assert scope == PHP_INI_USER || scope == PHP_INI_PERDIR || scope == PHP_INI_SYSTEM || scope == PHP_INI_ALL ; _name = name . intern ( ) ; _type = type ; _scope = scope ; _deflt = deflt ; } protected String getName ( ) { return _name ; } protected Value getDefault ( ) { return _deflt ; } public int getScope ( ) { return _scope ; } public boolean isRuntimeDefinition ( ) { return false ; } private static BooleanValue toBooleanValue ( Value value ) { if ( value instanceof BooleanValue ) return ( BooleanValue ) value ; if ( ! ( value instanceof StringValue ) ) return BooleanValue . create ( value . toBoolean ( ) ) ; String valueAsString = value . toString ( ) . trim ( ) ; if ( valueAsString . length ( ) == 0 || valueAsString . equalsIgnoreCase ( "false" ) || valueAsString . equalsIgnoreCase ( "off" ) || valueAsString . equalsIgnoreCase ( "0" ) ) return BooleanValue . FALSE ; else return BooleanValue . TRUE ; } public void set ( Quercus quercus , Value value ) { set ( quercus . getIniMap ( true ) , PHP_INI_SYSTEM , value ) ; } public void set ( Quercus quercus , String value ) { set ( quercus , new ConstStringValue ( value ) ) ; } public void set ( Env env , Value value ) { set ( env . getIniMap ( true ) , PHP_INI_USER , value ) ; } public void set ( Env env , String value ) { set ( env . getIniMap ( true ) , PHP_INI_USER , env . createStringOld ( value ) ) ; } protected void set ( HashMap < String , Value > map , int scope , Value value ) { if ( scope == PHP_INI_USER && ! ( _scope == PHP_INI_USER || _scope == PHP_INI_ALL ) ) { } else if ( _type == Type . BOOLEAN ) { map . put ( _name , toBooleanValue ( value ) ) ; } else map . put ( _name , value ) ; } private Value get ( HashMap < String , Value > envMap , HashMap < String , Value > quercusMap ) { Value value = null ; if ( envMap != null ) value = envMap . get ( _name ) ; if ( value == null && quercusMap != null ) value = quercusMap . get ( _name ) ; if ( value == null ) value = _deflt ; if ( value == null ) value = NullValue . NULL ; return value ; } public Value getValue ( Quercus quercus ) { return get ( null , quercus . getIniMap ( false ) ) ; } public Value getValue ( Env env ) { return get ( env . getIniMap ( false ) , env . getQuercus ( ) . getIniMap ( false ) ) ; } public StringValue getAsStringValue ( Quercus quercus ) { return get ( null , quercus . getIniMap ( false ) ) . toStringValue ( ) ; } public StringValue getAsStringValue ( Env env ) { return getValue ( env ) . toStringValue ( env ) ; } public String getAsString ( Env env ) { StringValue value = getAsStringValue ( env ) ; return ( value . length ( ) == 0 ) ? null : value . toString ( ) ; } public boolean getAsBoolean ( Quercus quercus ) { return getAsBooleanValue ( quercus ) . toBoolean ( ) ; } public boolean getAsBoolean ( Env env ) { return getAsBooleanValue ( env ) . toBoolean ( ) ; } public BooleanValue getAsBooleanValue ( Quercus quercus ) { return getAsBooleanValue ( null , quercus . getIniMap ( false ) ) ; } public BooleanValue getAsBooleanValue ( Env env ) { return getAsBooleanValue ( env . getIniMap ( false ) , env . getQuercus ( ) . getIniMap ( false ) ) ; } private BooleanValue getAsBooleanValue ( HashMap < String , Value > overrideMap , HashMap < String , Value > iniMap ) { Value value = get ( overrideMap , iniMap ) ; return toBooleanValue ( value ) ; } public LongValue getAsLongValue ( Quercus quercus ) { return getAsLongValue ( null , quercus . getIniMap ( false ) ) ; } public LongValue getAsLongValue ( Env env ) { return getAsLongValue ( env . getIniMap ( false ) , env . getQuercus ( ) . getIniMap ( false ) ) ; } private LongValue getAsLongValue ( HashMap < String , Value > overrideMap , HashMap < String , Value > iniMap ) { Value value = get ( overrideMap , iniMap ) ; if ( value instanceof LongValue ) return ( LongValue ) value ; else if ( value instanceof BooleanValue ) return value . toBoolean ( ) ? LongValue . ONE : LongValue . ZERO ; else return LongValue . create ( value . toLong ( ) ) ; } public long getAsLong ( Env env ) { return getAsLongValue ( env ) . toLong ( ) ; } public long getAsLongBytes ( Env env , long deflt ) { String bytes = getAsString ( env ) ; if ( bytes == null ) return deflt ; long value = 0 ; long sign = 1 ; int i = 0 ; int length = bytes . length ( ) ; if ( length == 0 ) return deflt ; if ( bytes . charAt ( i ) == '-' ) { sign = - 1 ; i ++ ; } else if ( bytes . charAt ( i ) == '+' ) { i ++ ; } if ( length <= i ) return deflt ; int ch ; for ( ; i < length && ( ch = bytes . charAt ( i ) ) >= '0' && ch <= '9' ; i ++ ) value = 10 * value + ch - '0' ; value = sign * value ; if ( bytes . endsWith ( "gb" ) || bytes . endsWith ( "g" ) || bytes . endsWith ( "G" ) ) { return value * 1024L * 1024L * 1024L ; } else if ( bytes . endsWith ( "mb" ) || bytes . endsWith ( "m" ) || bytes . endsWith ( "M" ) ) { return value * 1024L * 1024L ; } else if ( bytes . endsWith ( "kb" ) || bytes . endsWith ( "k" ) || bytes . endsWith ( "K" ) ) { return value * 1024L ; } else if ( bytes . endsWith ( "b" ) || bytes . endsWith ( "B" ) ) { return value ; } else if ( value < 0 ) return value ; else { env . warning ( L . l ( "byte-valued expression '{0}' for ini value '{1}' must have units.  '16B' for bytes, '16K' for kilobytes, '16M' for megabytes, '16G' for gigabytes" , _name ) ) ; return deflt ; } } static public class Unsupported extends IniDefinition { private L10N L = new L10N ( Unsupported . class ) ; public Unsupported ( String name , Type type , Value deflt , int scope ) { super ( name , type , deflt , scope ) ; } @ Override public void set ( HashMap < String , Value > map , int scope , Value value ) { Env env = Env . getInstance ( ) ; if ( env == null ) return ; if ( toBooleanValue ( value ) . equals ( toBooleanValue ( getDefault ( ) ) ) ) return ; else env . warning ( L . l ( "ini value `{0}' is not supported" , getName ( ) ) ) ; } } static private class Null extends Unsupported { private L10N L = new L10N ( Unsupported . class ) ; public Null ( ) { super ( "#null" , IniDefinition . Type . STRING , NullValue . NULL , PHP_INI_ALL ) ; } @ Override public void set ( HashMap < String , Value > map , int scope , Value value ) { if ( true ) throw new UnsupportedOperationException ( ) ; Env . getInstance ( ) . warning ( L . l ( "ini value `{0}' is not supported" , getName ( ) ) ) ; } } static public class Runtime extends IniDefinition { public Runtime ( String name ) { super ( name , IniDefinition . Type . STRING , NullValue . NULL , IniDefinition . PHP_INI_ALL ) ; } @ Override public boolean isRuntimeDefinition ( ) { return true ; } } } 