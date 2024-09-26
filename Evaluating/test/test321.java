<<<<<<< HEAD
public class LongValue extends NumberValue { private static final long serialVersionUID = 1L ; public static final LongValue MINUS_ONE = new LongValue ( - 1 ) ; public static final LongValue ZERO = new LongValue ( 0 ) ; public static final LongValue ONE = new LongValue ( 1 ) ; public static final int STATIC_MIN = - 1024 ; public static final int STATIC_MAX = 2048 ; public static final LongValue [ ] STATIC_VALUES ; private final long _value ; public LongValue ( long value ) { _value = value ; } public static LongValue create ( long value ) { if ( STATIC_MIN <= value && value <= STATIC_MAX ) return STATIC_VALUES [ ( int ) ( value - STATIC_MIN ) ] ; else return new LongValue ( value ) ; } public static LongValue create ( Number value ) { if ( value == null ) return LongValue . ZERO ; else return LongValue . create ( value . longValue ( ) ) ; } public String getType ( ) { return "integer" ; } @ Override public ValueType getValueType ( ) { return ValueType . LONG ; } public boolean isLongConvertible ( ) { return true ; } @ Override public boolean isNumeric ( ) { return true ; } @ Override public boolean isLong ( ) { return true ; } @ Override public boolean isDouble ( ) { return false ; } public boolean isScalar ( ) { return true ; } @ Override public boolean isEmpty ( ) { return _value == 0 ; } public boolean toBoolean ( ) { return _value != 0 ; } public long toLong ( ) { return _value ; } public double toDouble ( ) { return _value ; } public String toString ( ) { return String . valueOf ( _value ) ; } @ Override public StringValue toStringBuilder ( Env env ) { return env . createUnicodeBuilder ( ) . append ( _value ) ; } public LongValue toLongValue ( ) { return this ; } public Value toKey ( ) { return this ; } public Object toObject ( ) { return String . valueOf ( _value ) ; } public Object toJavaObject ( ) { return new Long ( _value ) ; } public Value neg ( ) { return LongValue . create ( - _value ) ; } public Value pos ( ) { return this ; } public Value increment ( int incr ) { return LongValue . create ( _value + incr ) ; } @ Override public Value add ( Value value ) { return value . add ( _value ) ; } @ Override public Value add ( long lLong ) { return LongValue . create ( lLong + _value ) ; } @ Override public Value sub ( Value rValue ) { if ( rValue . isLongConvertible ( ) ) return LongValue . create ( _value - rValue . toLong ( ) ) ; else return DoubleValue . create ( _value - rValue . toDouble ( ) ) ; } @ Override public Value sub ( long rLong ) { return LongValue . create ( _value - rLong ) ; } public Value abs ( ) { if ( _value >= 0 ) return this ; else return LongValue . create ( - _value ) ; } public boolean eql ( Value rValue ) { rValue = rValue . toValue ( ) ; if ( ! ( rValue instanceof LongValue ) ) return false ; long rLong = ( ( LongValue ) rValue ) . _value ; return _value == rLong ; } public int cmp ( Value rValue ) { if ( rValue . isBoolean ( ) ) { boolean lBool = toBoolean ( ) ; boolean rBool = rValue . toBoolean ( ) ; if ( ! lBool && rBool ) return - 1 ; if ( lBool && ! rBool ) return 1 ; return 0 ; } long l = _value ; double r = rValue . toDouble ( ) ; if ( l == r ) return 0 ; else if ( l < r ) return - 1 ; else return 1 ; } @ Override public long nextIndex ( long oldIndex ) { if ( oldIndex <= _value ) return _value + 1 ; else return oldIndex ; } public void print ( Env env ) { env . print ( _value ) ; } @ Override public StringValue appendTo ( UnicodeBuilderValue sb ) { return sb . append ( _value ) ; } @ Override public StringValue appendTo ( BinaryBuilderValue sb ) { return sb . append ( _value ) ; } @ Override public StringValue appendTo ( StringBuilderValue sb ) { return sb . append ( _value ) ; } @ Override public StringValue appendTo ( LargeStringBuilderValue sb ) { return sb . append ( _value ) ; } public void serialize ( Env env , StringBuilder sb ) { sb . append ( "i:" ) ; sb . append ( _value ) ; sb . append ( ";" ) ; } public void varExport ( StringBuilder sb ) { sb . append ( _value ) ; } public void generate ( PrintWriter out ) throws IOException { if ( _value == 0 ) out . print ( "LongValue.ZERO" ) ; else if ( _value == 1 ) out . print ( "LongValue.ONE" ) ; else if ( _value == - 1 ) out . print ( "LongValue.MINUS_ONE" ) ; else if ( STATIC_MIN <= _value && _value <= STATIC_MAX ) out . print ( "LongValue.STATIC_VALUES[" + ( _value - STATIC_MIN ) + "]" ) ; else out . print ( "new LongValue(" + _value + "L)" ) ; } public final int hashCode ( ) { long v = _value ; return ( int ) ( 17 * v + 65537 * ( v > > 32 ) ) ; } public boolean equals ( Object o ) { if ( this == o ) return true ; else if ( ! ( o instanceof LongValue ) ) return false ; LongValue value = ( LongValue ) o ; return _value == value . _value ; } public void varDumpImpl ( Env env , WriteStream out , int depth , IdentityHashMap < Value , String > valueSet ) throws IOException { out . print ( "int(" + toLong ( ) + ")" ) ; } private Object readResolve ( ) { if ( STATIC_MIN <= _value && _value <= STATIC_MAX ) return STATIC_VALUES [ ( int ) ( _value - STATIC_MIN ) ] ; else return this ; } static { STATIC_VALUES = new LongValue [ STATIC_MAX - STATIC_MIN + 1 ] ; for ( int i = STATIC_MIN ; i <= STATIC_MAX ; i ++ ) { STATIC_VALUES [ i - STATIC_MIN ] = new LongValue ( i ) ; } } } 
=======
class ElementPathPattern extends PathPattern { ElementPathPattern ( String [ ] names , boolean [ ] descendantsOrSelf ) { super ( names , descendantsOrSelf ) ; } boolean isAttribute ( ) { return false ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
