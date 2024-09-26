<<<<<<< HEAD
public class StringUtils { public static final String LINE_DELIMITER = System . getProperty ( "line.separator" , "\r\n" ) ; public static final String BULLET = "• " ; public static final String EMPTY = "" ; public static final String TAB = "\t" ; public static final String SPACE = " " ; public static final String COLON = ":" ; protected StringUtils ( ) { } public static String join ( String delimiter , String [ ] items ) { if ( items == null ) { return null ; } int length = items . length ; String result = StringUtils . EMPTY ; if ( length > 0 ) { StringBuffer sb = new StringBuffer ( ) ; for ( int i = 0 ; i < length - 1 ; i ++ ) { sb . append ( items [ i ] ) . append ( delimiter ) ; } sb . append ( items [ length - 1 ] ) ; result = sb . toString ( ) ; } return result ; } public static String stripHTML ( String text ) { if ( text == null ) { return null ; } String tempText = text . replaceAll ( "<p>" , "\n" ) ; return tempText . replaceAll ( "\\<.*?\\>" , StringUtils . EMPTY ) ; } public static String replaceNullWithEmpty ( String input ) { if ( input == null ) { return StringUtils . EMPTY ; } return input ; } public static String urlEncodeForSpaces ( String input ) { if ( input == null ) { return null ; } return urlEncodeForSpaces ( input . toCharArray ( ) ) ; } public static String urlEncodeKeyValuePair ( String key , String value ) { String result = null ; try { result = java . net . URLEncoder . encode ( key , "UTF-8" ) + "=" + java . net . URLEncoder . encode ( value , "UTF-8" ) ; } catch ( UnsupportedEncodingException e ) { } return result ; } public static String urlEncodeForSpaces ( char [ ] input ) { if ( input == null ) { return null ; } StringBuffer retu = new StringBuffer ( input . length ) ; for ( int i = 0 ; i < input . length ; i ++ ) { if ( input [ i ] == ' ' ) { retu . append ( "%20" ) ; } else { retu . append ( input [ i ] ) ; } } return retu . toString ( ) ; } public static String urlEncodeFilename ( char [ ] input ) { if ( input == null ) { return null ; } StringBuffer retu = new StringBuffer ( input . length ) ; for ( int i = 0 ; i < input . length ; i ++ ) { if ( input [ i ] == ' ' ) { retu . append ( "%20" ) ; } else if ( input [ i ] == '[' ) { retu . append ( "%5B" ) ; } else if ( input [ i ] == ']' ) { retu . append ( "%5D" ) ; } else if ( input [ i ] == '{' ) { retu . append ( "%7B" ) ; } else if ( input [ i ] == '}' ) { retu . append ( "%7D" ) ; } else if ( input [ i ] == '`' ) { retu . append ( "%60" ) ; } else if ( input [ i ] == '+' ) { retu . append ( "%2B" ) ; } else { retu . append ( input [ i ] ) ; } } return retu . toString ( ) ; } public static String urlDecodeFilename ( char [ ] input ) { if ( input == null ) { return null ; } StringBuffer retu = new StringBuffer ( input . length ) ; for ( int i = 0 ; i < input . length ; i ++ ) { if ( input [ i ] != '%' || i + 2 >= input . length ) { retu . append ( input [ i ] ) ; } else { String test = new String ( input , i , 3 ) ; if ( test . equals ( "%20" ) ) { retu . append ( " " ) ; i += 2 ; } else if ( test . equals ( "%5B" ) ) { retu . append ( "[" ) ; i += 2 ; } else if ( test . equals ( "%5D" ) ) { retu . append ( "]" ) ; i += 2 ; } else if ( test . equals ( "%7B" ) ) { retu . append ( "{" ) ; i += 2 ; } else if ( test . equals ( "%7D" ) ) { retu . append ( "}" ) ; i += 2 ; } else if ( test . equals ( "%60" ) ) { retu . append ( "`" ) ; i += 2 ; } else if ( test . equals ( "%2B" ) ) { retu . append ( "+" ) ; i += 2 ; } else { retu . append ( input [ i ] ) ; } } } return retu . toString ( ) ; } public static String stripCarriageReturns ( String text ) { if ( text == null ) { return null ; } return text . replaceAll ( "\n" , StringUtils . EMPTY ) ; } public static String stripWhitespace ( String text ) { if ( text == null ) { return null ; } return text . replaceAll ( "\\s+" , " " ) ; } public static String formatAsPlainText ( String text ) { String tempText = StringUtils . stripCarriageReturns ( text ) ; tempText = StringUtils . stripWhitespace ( tempText ) ; tempText = StringUtils . replace ( tempText , "</li>" , StringUtils . EMPTY ) ; tempText = StringUtils . replace ( tempText , "<li>" , LINE_DELIMITER + "\t" + BULLET ) ; tempText = StringUtils . replace ( tempText , "<p>" , LINE_DELIMITER ) ; tempText = StringUtils . stripHTML ( tempText ) ; return tempText . trim ( ) ; } public static String trimStringQuotes ( String stringToTrim ) { if ( stringToTrim == null ) { return null ; } String trimmed = stringToTrim . trim ( ) ; if ( trimmed . startsWith ( "\"" ) || trimmed . startsWith ( "'" ) ) { trimmed = trimmed . substring ( 1 ) ; } if ( trimmed . endsWith ( "\"" ) || trimmed . endsWith ( "'" ) ) { trimmed = trimmed . substring ( 0 , trimmed . length ( ) - 1 ) ; } return trimmed ; } public static String trimBrackets ( String stringToTrim ) { if ( stringToTrim == null ) { return null ; } String trimmed = stringToTrim . trim ( ) ; if ( trimmed . startsWith ( "[" ) ) { trimmed = trimmed . substring ( 1 ) ; } if ( trimmed . endsWith ( "]" ) ) { trimmed = trimmed . substring ( 0 , trimmed . length ( ) - 1 ) ; } return trimmed ; } public static String trimStart ( String stringToTrim ) { if ( stringToTrim == null ) { return null ; } char [ ] chars = stringToTrim . toCharArray ( ) ; int index = 0 ; int length = chars . length ; while ( index < length && chars [ index ] <= ' ' ) { index ++ ; } if ( index > 0 ) { return stringToTrim . substring ( index ) ; } else { return stringToTrim ; } } public static String trimEnd ( String stringToTrim ) { if ( stringToTrim == null ) { return null ; } char [ ] chars = stringToTrim . toCharArray ( ) ; int index = chars . length ; while ( index > 0 && chars [ index - 1 ] < ' ' ) { index -- ; } if ( index > 0 ) { return stringToTrim . substring ( 0 , index ) ; } else { return stringToTrim ; } } public static String replace ( String str , String pattern , String replace ) { int s = 0 ; int e = 0 ; StringBuffer result = new StringBuffer ( ) ; while ( ( e = str . indexOf ( pattern , s ) ) >= 0 ) { result . append ( str . substring ( s , e ) ) ; result . append ( replace ) ; s = e + pattern . length ( ) ; } result . append ( str . substring ( s ) ) ; return result . toString ( ) ; } public static String format ( String str , long replacement ) { return MessageFormat . format ( str , new Object [ ] { Long . toString ( replacement ) } ) ; } public static String format ( String str , int replacement ) { return MessageFormat . format ( str , new Object [ ] { Integer . toString ( replacement ) } ) ; } public static String format ( String str , String replacement ) { return MessageFormat . format ( str , new Object [ ] { replacement } ) ; } public static String format ( String str , Object replacement ) { return MessageFormat . format ( str , new Object [ ] { replacement . toString ( ) } ) ; } public static String format ( String str , Object [ ] replacements ) { return MessageFormat . format ( str , replacements ) ; } public static String ellipsify ( String message ) { return message + CoreStrings . ELLIPSIS ; } public static String makeFormLabel ( String message ) { return message + COLON ; } public static String convertWildcardExpressionToRegex ( String wildcardExpression , boolean caseInsensitive ) { if ( wildcardExpression == null ) { return null ; } if ( wildcardExpression . startsWith ( "/" ) && wildcardExpression . endsWith ( "/" ) ) { return wildcardExpression . substring ( 1 , wildcardExpression . length ( ) - 1 ) ; } String string = wildcardExpression . replaceAll ( "\\.(?=[^\\*])" , "\\\\." ) ; string = string . replaceAll ( "\\*" , ".*" ) ; if ( caseInsensitive ) { string = "(?i)" + string ; } return string ; } public static String getSpaces ( int length ) { String defaultIndent = "                                        " ; String indentString = defaultIndent ; if ( length < indentString . length ( ) ) { indentString = defaultIndent . substring ( 0 , length ) ; } return indentString ; } public static String [ ] getArrayOfSpaces ( int length ) { ArrayList prefixes = new ArrayList ( ) ; for ( int i = length ; i > 0 ; i -- ) { prefixes . add ( StringUtils . getSpaces ( i ) ) ; } return ( String [ ] ) prefixes . toArray ( new String [ 0 ] ) ; } public static String findStartWhitespace ( String text ) { if ( text == null || text . length ( ) == 0 ) { return text ; } String [ ] s = text . split ( "\\S" ) ; if ( s . length > 0 ) { return s [ 0 ] ; } else { return "" ; } } public static String findEndWhitespace ( String text ) { if ( text == null || text . length ( ) == 0 ) { return text ; } String trimmed = trimEnd ( text ) ; int textLength = text . length ( ) ; if ( trimmed . length ( ) == textLength ) { return "" ; } else { return text . substring ( trimmed . length ( ) ) ; } } public static int getNumberOfNewlines ( String text ) { if ( text == null ) { return 0 ; } int count = 0 ; String sourceBit = StringUtils . replace ( text , "\r\n" , "\n" ) ; sourceBit = StringUtils . replace ( sourceBit , "\r" , "\n" ) ; char [ ] split = sourceBit . toCharArray ( ) ; for ( int i = 0 ; i < split . length ; i ++ ) { char c = split [ i ] ; if ( c == '\n' ) { count ++ ; } } return count ; } public static int compareVersions ( String left , String right ) { int result ; String [ ] lparts = left . split ( "\\." ) ; String [ ] rparts = right . split ( "\\." ) ; for ( int i = 0 ; i < lparts . length && i < rparts . length ; ++ i ) { result = lparts [ i ] . compareToIgnoreCase ( rparts [ i ] ) ; if ( result != 0 ) { return result ; } } return ( lparts . length - rparts . length ) ; } public static String getPublishableMessage ( Object object ) { if ( object == null ) { return "null" ; } String text = object . toString ( ) ; text = text . replaceAll ( "password=.+," , "PASSWORD," ) ; text = text . replaceAll ( "password=.+}" , "PASSWORD}" ) ; text = text . replaceAll ( "<password.*>.+</password>" , "<password>PASSWORD</password>" ) ; text = text . replaceAll ( "<app_password.*>.+<.*>" , "<app_password>PASSWORD</app_password>" ) ; text = text . replaceAll ( "number=[0-9]+," , "number=XXXXXXXXXXXXXXXX" ) ; text = text . replaceAll ( "<number.*>[0-9]+<.*>" , "<number>XXXXXXXXXXXXXXXX</number>" ) ; return text ; } } 
=======
public class Polyline extends AbstractInt { public static final Polyline THE_INSTANCE = new Polyline ( ) ; private Polyline ( ) { super ( ) ; } @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { List < CharSequenceWithOffset > list = split ( literal , ',' ) ; if ( list . size ( ) < 6 ) { throw newDatatypeException ( "A polyline must have at least six comma-separated integers." ) ; } if ( list . size ( ) % 2 != 0 ) { throw newDatatypeException ( "A polyline must have an even number of comma-separated integers." ) ; } for ( CharSequenceWithOffset item : list ) { checkInt ( item . getSequence ( ) , item . getOffset ( ) ) ; } } @ Override public String getName ( ) { return "polyline" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
