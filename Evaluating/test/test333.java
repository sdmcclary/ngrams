<<<<<<< HEAD
public class HttpModule extends AbstractQuercusModule { private static final L10N L = new L10N ( HttpModule . class ) ; private final static QDate _calendar = new QDate ( false ) ; @ SuppressWarnings ( "unchecked" ) private static List < String > getHeaders ( Env env ) { List < String > headers = ( List < String > ) env . getSpecialValue ( "caucho.headers" ) ; if ( headers == null ) { headers = new ArrayList < String > ( ) ; env . setSpecialValue ( "caucho.headers" , headers ) ; } return headers ; } public static Value header ( Env env , StringValue headerStr , @ Optional ( "true" ) boolean replace , @ Optional long httpResponseCode ) { HttpServletResponse res = env . getResponse ( ) ; if ( res == null ) { env . error ( L . l ( "header requires an http context" ) ) ; return NullValue . NULL ; } String header = headerStr . toString ( ) ; int len = header . length ( ) ; if ( header . startsWith ( "HTTP/" ) ) { int p = header . indexOf ( ' ' ) ; int status = 0 ; int ch ; for ( ; p < len && header . charAt ( p ) == ' ' ; p ++ ) { } for ( ; p < len && '0' <= ( ch = header . charAt ( p ) ) && ch <= '9' ; p ++ ) { status = 10 * status + ch - '0' ; } for ( ; p < len && header . charAt ( p ) == ' ' ; p ++ ) { } if ( status > 0 ) { res . setStatus ( status ) ; return NullValue . NULL ; } } int colonIndex = header . indexOf ( ':' ) ; if ( colonIndex > 0 ) { String key = header . substring ( 0 , colonIndex ) . trim ( ) ; String value = header . substring ( colonIndex + 1 ) . trim ( ) ; if ( key . equalsIgnoreCase ( "Location" ) ) { res . setStatus ( 302 ) ; } if ( replace ) { res . setHeader ( key , value ) ; List < String > headers = getHeaders ( env ) ; int regionEnd = colonIndex + 1 ; for ( int i = 0 ; i < headers . size ( ) ; i ++ ) { String compare = headers . get ( i ) ; if ( compare . regionMatches ( true , 0 , header , 0 , regionEnd ) ) { headers . remove ( i ) ; break ; } } headers . add ( header ) ; } else { res . addHeader ( key , value ) ; getHeaders ( env ) . add ( header ) ; } if ( key . equalsIgnoreCase ( "Content-Type" ) ) { String encoding = env . getOutputEncoding ( ) ; if ( encoding != null ) { if ( value . indexOf ( "charset" ) < 0 ) { if ( value . indexOf ( "text/" ) < 0 ) res . setCharacterEncoding ( encoding ) ; } else if ( "" . equals ( res . getCharacterEncoding ( ) ) ) { res . setCharacterEncoding ( encoding ) ; } } } } else { if ( header . equals ( "Not Modified" ) || header . equals ( "No Content" ) ) { if ( httpResponseCode != 0 ) { res . setStatus ( ( int ) httpResponseCode ) ; } } } return NullValue . NULL ; } public static ArrayValue headers_list ( Env env ) { List < String > headersList = getHeaders ( env ) ; int size = headersList . size ( ) ; ArrayValueImpl headersArray = new ArrayValueImpl ( size ) ; for ( int i = 0 ; i < size ; i ++ ) headersArray . put ( headersList . get ( i ) ) ; return headersArray ; } public static boolean headers_sent ( Env env , @ Optional @ Reference Value file , @ Optional @ Reference Value line ) { HttpServletResponse res = env . getResponse ( ) ; return res . isCommitted ( ) ; } public static boolean setcookie ( Env env , String name , @ Optional String value , @ Optional long expire , @ Optional String path , @ Optional String domain , @ Optional boolean secure , @ Optional boolean httpOnly ) { long now = System . currentTimeMillis ( ) ; if ( value == null || value . equals ( "" ) ) value = "" ; StringBuilder sb = new StringBuilder ( ) ; int len = value . length ( ) ; for ( int i = 0 ; i < len ; i ++ ) { char ch = value . charAt ( i ) ; if ( '0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || ch == '-' || ch == '.' || ch == '_' ) { sb . append ( ch ) ; } else if ( ch == ' ' ) { sb . append ( '+' ) ; } else { sb . append ( '%' ) ; int d = ( ch / 16 ) & 0xf ; if ( d < 10 ) sb . append ( ( char ) ( '0' + d ) ) ; else sb . append ( ( char ) ( 'A' + d - 10 ) ) ; d = ch & 0xf ; if ( d < 10 ) sb . append ( ( char ) ( '0' + d ) ) ; else sb . append ( ( char ) ( 'A' + d - 10 ) ) ; } } Cookie cookie = new Cookie ( name , sb . toString ( ) ) ; int maxAge = 0 ; if ( expire > 0 ) { maxAge = ( int ) ( expire - now / 1000 ) ; if ( maxAge > 0 ) cookie . setMaxAge ( maxAge ) ; else cookie . setMaxAge ( 0 ) ; } if ( path != null && ! path . equals ( "" ) ) cookie . setPath ( path ) ; if ( domain != null && ! domain . equals ( "" ) ) cookie . setDomain ( domain ) ; if ( secure ) cookie . setSecure ( true ) ; env . getResponse ( ) . addCookie ( cookie ) ; StringBuilder cookieHeader = new StringBuilder ( ) ; cookieHeader . append ( "Set-Cookie: " ) ; cookieHeader . append ( cookie . getName ( ) ) ; cookieHeader . append ( "=" ) ; cookieHeader . append ( cookie . getValue ( ) ) ; if ( maxAge == 0 ) { cookieHeader . append ( "; expires=Thu, 01-Dec-1994 16:00:00 GMT" ) ; } else { _calendar . setGMTTime ( now + 1000L * ( long ) maxAge ) ; cookieHeader . append ( "; expires=" ) ; cookieHeader . append ( _calendar . format ( "%a, %d-%b-%Y %H:%M:%S GMT" ) ) ; } if ( path != null && ! path . equals ( "" ) ) { cookieHeader . append ( "; path=" ) ; cookieHeader . append ( path ) ; } if ( domain != null && ! domain . equals ( "" ) ) { cookieHeader . append ( "; domain=" ) ; cookieHeader . append ( domain ) ; } if ( secure ) cookieHeader . append ( "; secure" ) ; getHeaders ( env ) . add ( cookieHeader . toString ( ) ) ; return true ; } } 
=======
interface PatternManager { void registerPattern ( Pattern pattern , SelectionHandler handler ) ; void registerValueHandler ( ValueHandler handler ) ; } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
