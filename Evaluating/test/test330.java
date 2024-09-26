<<<<<<< HEAD
public class BcmathModule extends AbstractQuercusModule { private static final L10N L = new L10N ( BcmathModule . class ) ; private static final BigDecimal ZERO = BigDecimal . ZERO ; private static final BigDecimal ONE = BigDecimal . ONE ; private static final BigDecimal TWO = new BigDecimal ( 2 ) ; private static final int SQRT_MAX_ITERATIONS = 50 ; private static final IniDefinitions _iniDefinitions = new IniDefinitions ( ) ; public String [ ] getLoadedExtensions ( ) { return new String [ ] { "bcmath" } ; } public IniDefinitions getIniDefinitions ( ) { return _iniDefinitions ; } private static BigDecimal toBigDecimal ( Value value ) { try { if ( value instanceof StringValue ) return new BigDecimal ( value . toString ( ) ) ; if ( value instanceof DoubleValue ) return new BigDecimal ( value . toDouble ( ) ) ; else if ( value instanceof LongValue ) return new BigDecimal ( value . toLong ( ) ) ; else return new BigDecimal ( value . toString ( ) ) ; } catch ( NumberFormatException ex ) { return ZERO ; } catch ( IllegalArgumentException ex ) { return ZERO ; } } private static int calculateScale ( Env env , int scale ) { if ( scale < 0 ) { Value iniValue = env . getIni ( "bcmath.scale" ) ; if ( iniValue != null ) scale = iniValue . toInt ( ) ; } if ( scale < 0 ) scale = 0 ; return scale ; } public static String bcadd ( Env env , Value value1 , Value value2 , @ Optional ( "-1" ) int scale ) { scale = calculateScale ( env , scale ) ; BigDecimal bd1 = toBigDecimal ( value1 ) ; BigDecimal bd2 = toBigDecimal ( value2 ) ; BigDecimal bd = bd1 . add ( bd2 ) ; bd = bd . setScale ( scale , RoundingMode . DOWN ) ; return bd . toPlainString ( ) ; } public static int bccomp ( Env env , Value value1 , Value value2 , @ Optional ( "-1" ) int scale ) { scale = calculateScale ( env , scale ) ; BigDecimal bd1 = toBigDecimal ( value1 ) ; BigDecimal bd2 = toBigDecimal ( value2 ) ; bd1 = bd1 . setScale ( scale , RoundingMode . DOWN ) ; bd2 = bd2 . setScale ( scale , RoundingMode . DOWN ) ; return bd1 . compareTo ( bd2 ) ; } public static String bcdiv ( Env env , Value value1 , Value value2 , @ Optional ( "-1" ) int scale ) { scale = calculateScale ( env , scale ) ; BigDecimal bd1 = toBigDecimal ( value1 ) ; BigDecimal bd2 = toBigDecimal ( value2 ) ; if ( bd2 . compareTo ( ZERO ) == 0 ) { env . warning ( L . l ( "division by zero" ) ) ; return null ; } BigDecimal result ; if ( scale > 0 ) { result = bd1 . divide ( bd2 , scale + 2 , RoundingMode . DOWN ) ; } else { result = bd1 . divide ( bd2 , 2 , RoundingMode . DOWN ) ; } result = result . setScale ( scale , RoundingMode . DOWN ) ; return result . toPlainString ( ) ; } public static String bcmod ( Env env , Value value , Value modulus ) { BigDecimal bd1 = toBigDecimal ( value ) . setScale ( 0 , RoundingMode . DOWN ) ; BigDecimal bd2 = toBigDecimal ( modulus ) . setScale ( 0 , RoundingMode . DOWN ) ; if ( bd2 . compareTo ( ZERO ) == 0 ) { env . warning ( L . l ( "division by zero" ) ) ; return null ; } BigDecimal bd = bd1 . remainder ( bd2 , MathContext . DECIMAL128 ) ; bd = bd . setScale ( 0 , RoundingMode . DOWN ) ; return bd . toPlainString ( ) ; } public static String bcmul ( Env env , Value value1 , Value value2 , @ Optional ( "-1" ) int scale ) { scale = calculateScale ( env , scale ) ; BigDecimal bd1 = toBigDecimal ( value1 ) ; BigDecimal bd2 = toBigDecimal ( value2 ) ; BigDecimal bd = bd1 . multiply ( bd2 ) ; if ( bd . compareTo ( ZERO ) == 0 ) { if ( scale > 0 ) return "0.0" ; else return "0" ; } bd = bd . setScale ( scale , RoundingMode . DOWN ) ; bd = bd . stripTrailingZeros ( ) ; return bd . toPlainString ( ) ; } public static String bcpow ( Env env , Value base , Value exp , @ Optional ( "-1" ) int scale ) { scale = calculateScale ( env , scale ) ; BigDecimal bd1 = toBigDecimal ( base ) ; BigDecimal bd2 = toBigDecimal ( exp ) ; if ( bd2 . scale ( ) > 0 ) env . warning ( "fractional exponent not supported" ) ; int exponent = bd2 . toBigInteger ( ) . intValue ( ) ; if ( exponent == 0 ) return "1" ; boolean isNeg ; if ( exponent < 0 ) { isNeg = true ; exponent *= - 1 ; } else isNeg = false ; BigDecimal bd = bd1 . pow ( exponent ) ; if ( isNeg ) bd = ONE . divide ( bd , scale + 2 , RoundingMode . DOWN ) ; bd = bd . setScale ( scale , RoundingMode . DOWN ) ; if ( bd . compareTo ( BigDecimal . ZERO ) == 0 ) return "0" ; bd = bd . stripTrailingZeros ( ) ; return bd . toPlainString ( ) ; } public static String bcpowmod ( Env env , Value base , Value exp , Value modulus , @ Optional ( "-1" ) int scale ) { scale = calculateScale ( env , scale ) ; String pow = bcpow ( env , base , exp , scale ) ; if ( pow == null ) return null ; return bcmod ( env , env . createStringOld ( pow ) , modulus ) ; } public static boolean bcscale ( Env env , int scale ) { env . setIni ( "bcmath.scale" , String . valueOf ( scale ) ) ; return true ; } public static String bcsqrt ( Env env , Value operand , @ Optional ( "-1" ) int scale ) { scale = calculateScale ( env , scale ) ; BigDecimal value = toBigDecimal ( operand ) ; int compareToZero = value . compareTo ( ZERO ) ; if ( compareToZero < 0 ) { env . warning ( L . l ( "square root of negative number" ) ) ; return null ; } else if ( compareToZero == 0 ) { return "0" ; } int compareToOne = value . compareTo ( ONE ) ; if ( compareToOne == 0 ) return "1" ; int cscale ; BigDecimal initialGuess ; if ( compareToOne < 1 ) { initialGuess = ONE ; cscale = value . scale ( ) ; } else { BigInteger integerPart = value . toBigInteger ( ) ; int length = integerPart . toString ( ) . length ( ) ; if ( ( length % 2 ) == 0 ) length -- ; length /= 2 ; initialGuess = ONE . movePointRight ( length ) ; cscale = Math . max ( scale , value . scale ( ) ) + 2 ; } BigDecimal guess = initialGuess ; BigDecimal lastGuess ; for ( int iteration = 0 ; iteration < SQRT_MAX_ITERATIONS ; iteration ++ ) { lastGuess = guess ; guess = value . divide ( guess , cscale , RoundingMode . DOWN ) ; guess = guess . add ( lastGuess ) ; guess = guess . divide ( TWO , cscale , RoundingMode . DOWN ) ; if ( lastGuess . equals ( guess ) ) { break ; } } value = guess ; value = value . setScale ( scale , RoundingMode . DOWN ) ; return value . toPlainString ( ) ; } public static String bcsub ( Env env , Value value1 , Value value2 , @ Optional ( "-1" ) int scale ) { scale = calculateScale ( env , scale ) ; BigDecimal bd1 = toBigDecimal ( value1 ) ; BigDecimal bd2 = toBigDecimal ( value2 ) ; BigDecimal bd = bd1 . subtract ( bd2 ) ; bd = bd . setScale ( scale , RoundingMode . DOWN ) ; return bd . toPlainString ( ) ; } public static final IniDefinition INI_BCMATH_SCALE = _iniDefinitions . add ( "bcmath.scale" , 0 , PHP_INI_ALL ) ; } 
=======
abstract class PathPattern extends Pattern { private final String [ ] names ; private final boolean [ ] descendantsOrSelf ; static final String ANY = "#any" ; PathPattern ( String [ ] names , boolean [ ] descendantsOrSelf ) { this . names = names ; this . descendantsOrSelf = descendantsOrSelf ; } abstract boolean isAttribute ( ) ; boolean matches ( Path path , int rootDepth ) { return ( isAttribute ( ) == path . isAttribute ( ) && matchSegment ( path , rootDepth , path . length ( ) - rootDepth , 0 , names . length > > 1 , false ) ) ; } private boolean matchSegment ( Path path , int pathStartIndex , int pathLength , int patternStartIndex , int patternLength , boolean ignoreRightmostDescendantsOrSelf ) { if ( patternLength > pathLength ) return false ; while ( patternLength > 0 && ( ignoreRightmostDescendantsOrSelf || ! descendantsOrSelf [ patternStartIndex + patternLength ] ) ) { if ( ! matchStep ( path , pathStartIndex + pathLength - 1 , patternStartIndex + patternLength - 1 ) ) return false ; pathLength -- ; patternLength -- ; ignoreRightmostDescendantsOrSelf = false ; } while ( patternLength > 0 && ! descendantsOrSelf [ patternStartIndex ] ) { if ( ! matchStep ( path , pathStartIndex , patternStartIndex ) ) return false ; pathStartIndex ++ ; patternStartIndex ++ ; pathLength -- ; patternLength -- ; } if ( patternLength == 0 ) return descendantsOrSelf [ patternStartIndex ] || pathLength == 0 ; for ( pathLength -- ; pathLength >= patternLength ; pathLength -- ) if ( matchSegment ( path , pathStartIndex , pathLength , patternStartIndex , patternLength , true ) ) return true ; return false ; } private boolean matchStep ( Path path , int pathIndex , int patternIndex ) { patternIndex *= 2 ; return ( matchName ( path . getNamespaceUri ( pathIndex ) , names [ patternIndex ] ) && matchName ( path . getLocalName ( pathIndex ) , names [ patternIndex + 1 ] ) ) ; } private static boolean matchName ( String str , String pattern ) { if ( pattern == ElementPathPattern . ANY ) return true ; return str . equals ( pattern ) ; } public String toString ( ) { StringBuffer buf = new StringBuffer ( ) ; for ( int i = 0 , j = 0 ; i < names . length ; i += 2 , j ++ ) { if ( j != 0 ) buf . append ( descendantsOrSelf [ j ] ? "//" : "/" ) ; else if ( descendantsOrSelf [ 0 ] ) buf . append ( ".//" ) ; if ( isAttribute ( ) && i + 2 == names . length ) buf . append ( '@' ) ; if ( names [ i ] == ANY ) buf . append ( '*' ) ; else { if ( names [ i ] . length ( ) != 0 ) { buf . append ( '{' ) ; buf . append ( names [ i ] ) ; buf . append ( '}' ) ; } buf . append ( names [ i + 1 ] == ANY ? "*" : names [ i + 1 ] ) ; } } if ( names . length == 0 ) buf . append ( descendantsOrSelf [ 0 ] ? ".//." : "." ) ; else if ( descendantsOrSelf [ descendantsOrSelf . length - 1 ] ) buf . append ( "//." ) ; return buf . toString ( ) ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
