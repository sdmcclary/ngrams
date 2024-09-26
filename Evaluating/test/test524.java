<<<<<<< HEAD
public class InetRange implements Cloneable { Hashtable host_names ; Vector all ; Vector end_names ; boolean useSeparateThread = true ; public InetRange ( ) { all = new Vector ( ) ; host_names = new Hashtable ( ) ; end_names = new Vector ( ) ; } public synchronized boolean add ( String s ) { if ( s == null ) return false ; s = s . trim ( ) ; if ( s . length ( ) == 0 ) return false ; Object [ ] entry ; if ( s . charAt ( s . length ( ) - 1 ) == '.' ) { int [ ] addr = ip2intarray ( s ) ; long from , to ; from = to = 0 ; if ( addr == null ) return false ; for ( int i = 0 ; i < 4 ; ++ i ) { if ( addr [ i ] >= 0 ) from += ( ( ( long ) addr [ i ] ) << 8 * ( 3 - i ) ) ; else { to = from ; while ( i < 4 ) to += 255l << 8 * ( 3 - i ++ ) ; break ; } } entry = new Object [ ] { s , null , new Long ( from ) , new Long ( to ) } ; all . addElement ( entry ) ; } else if ( s . charAt ( 0 ) == '.' ) { end_names . addElement ( s ) ; all . addElement ( new Object [ ] { s , null , null , null } ) ; } else { StringTokenizer tokens = new StringTokenizer ( s , " \t\r\n\f:" ) ; if ( tokens . countTokens ( ) > 1 ) { entry = new Object [ ] { s , null , null , null } ; resolve ( entry , tokens . nextToken ( ) , tokens . nextToken ( ) ) ; all . addElement ( entry ) ; } else { entry = new Object [ ] { s , null , null , null } ; all . addElement ( entry ) ; host_names . put ( s , entry ) ; resolve ( entry ) ; } } return true ; } public synchronized void add ( InetAddress ip ) { long from , to ; from = to = ip2long ( ip ) ; all . addElement ( new Object [ ] { ip . getHostName ( ) , ip , new Long ( from ) , new Long ( to ) } ) ; } public synchronized void add ( InetAddress from , InetAddress to ) { all . addElement ( new Object [ ] { from . getHostAddress ( ) + ":" + to . getHostAddress ( ) , null , new Long ( ip2long ( from ) ) , new Long ( ip2long ( to ) ) } ) ; } public synchronized boolean contains ( String host ) { return contains ( host , true ) ; } public synchronized boolean contains ( String host , boolean attemptResolve ) { if ( all . size ( ) == 0 ) return false ; host = host . trim ( ) ; if ( host . length ( ) == 0 ) return false ; if ( checkHost ( host ) ) return true ; if ( checkHostEnding ( host ) ) return true ; long l = host2long ( host ) ; if ( l >= 0 ) return contains ( l ) ; if ( ! attemptResolve ) return false ; try { InetAddress ip = InetAddress . getByName ( host ) ; return contains ( ip ) ; } catch ( UnknownHostException uhe ) { } return false ; } public synchronized boolean contains ( InetAddress ip ) { if ( checkHostEnding ( ip . getHostName ( ) ) ) return true ; if ( checkHost ( ip . getHostName ( ) ) ) return true ; return contains ( ip2long ( ip ) ) ; } public synchronized String [ ] getAll ( ) { int size = all . size ( ) ; Object entry [ ] ; String all_names [ ] = new String [ size ] ; for ( int i = 0 ; i < size ; ++ i ) { entry = ( Object [ ] ) all . elementAt ( i ) ; all_names [ i ] = ( String ) entry [ 0 ] ; } return all_names ; } public synchronized boolean remove ( String s ) { Enumeration enum = all . elements ( ) ; while ( enum . hasMoreElements ( ) ) { Object [ ] entry = ( Object [ ] ) enum . nextElement ( ) ; if ( s . equals ( entry [ 0 ] ) ) { all . removeElement ( entry ) ; end_names . removeElement ( s ) ; host_names . remove ( s ) ; return true ; } } return false ; } public String toString ( ) { String all [ ] = getAll ( ) ; if ( all . length == 0 ) return "" ; String s = all [ 0 ] ; for ( int i = 1 ; i < all . length ; ++ i ) s += "; " + all [ i ] ; return s ; } public Object clone ( ) { InetRange new_range = new InetRange ( ) ; new_range . all = ( Vector ) all . clone ( ) ; new_range . end_names = ( Vector ) end_names . clone ( ) ; new_range . host_names = ( Hashtable ) host_names . clone ( ) ; return new_range ; } private synchronized boolean contains ( long ip ) { Enumeration enum = all . elements ( ) ; while ( enum . hasMoreElements ( ) ) { Object [ ] obj = ( Object [ ] ) enum . nextElement ( ) ; Long from = obj [ 2 ] == null ? null : ( Long ) obj [ 2 ] ; Long to = obj [ 3 ] == null ? null : ( Long ) obj [ 3 ] ; if ( from != null && from . longValue ( ) <= ip && to . longValue ( ) >= ip ) return true ; } return false ; } private boolean checkHost ( String host ) { return host_names . containsKey ( host ) ; } private boolean checkHostEnding ( String host ) { Enumeration enum = end_names . elements ( ) ; while ( enum . hasMoreElements ( ) ) { if ( host . endsWith ( ( String ) enum . nextElement ( ) ) ) return true ; } return false ; } private void resolve ( Object [ ] entry ) { long ip = host2long ( ( String ) entry [ 0 ] ) ; if ( ip >= 0 ) { entry [ 2 ] = entry [ 3 ] = new Long ( ip ) ; } else { InetRangeResolver res = new InetRangeResolver ( entry ) ; res . resolve ( useSeparateThread ) ; } } private void resolve ( Object [ ] entry , String from , String to ) { long f , t ; if ( ( f = host2long ( from ) ) >= 0 && ( t = host2long ( to ) ) >= 0 ) { entry [ 2 ] = new Long ( f ) ; entry [ 3 ] = new Long ( t ) ; } else { InetRangeResolver res = new InetRangeResolver ( entry , from , to ) ; res . resolve ( useSeparateThread ) ; } } static long ip2long ( InetAddress ip ) { long l = 0 ; byte [ ] addr = ip . getAddress ( ) ; if ( addr . length == 4 ) { for ( int i = 0 ; i < 4 ; ++ i ) l += ( ( ( long ) addr [ i ] & 0xFF ) << 8 * ( 3 - i ) ) ; } else { return 0 ; } return l ; } long host2long ( String host ) { long ip = 0 ; if ( ! Character . isDigit ( host . charAt ( 0 ) ) ) return - 1 ; int [ ] addr = ip2intarray ( host ) ; if ( addr == null ) return - 1 ; for ( int i = 0 ; i < addr . length ; ++ i ) ip += ( ( long ) ( addr [ i ] >= 0 ? addr [ i ] : 0 ) ) << 8 * ( 3 - i ) ; return ip ; } static int [ ] ip2intarray ( String host ) { int [ ] address = { - 1 , - 1 , - 1 , - 1 } ; int i = 0 ; StringTokenizer tokens = new StringTokenizer ( host , "." ) ; if ( tokens . countTokens ( ) > 4 ) return null ; while ( tokens . hasMoreTokens ( ) ) { try { address [ i ++ ] = Integer . parseInt ( tokens . nextToken ( ) ) & 0xFF ; } catch ( NumberFormatException nfe ) { return null ; } } return address ; } } class InetRangeResolver implements Runnable { Object [ ] entry ; String from , to ; InetRangeResolver ( Object [ ] entry ) { this . entry = entry ; from = to = null ; } InetRangeResolver ( Object [ ] entry , String from , String to ) { this . entry = entry ; this . from = from ; this . to = to ; } public final void resolve ( ) { resolve ( true ) ; } public final void resolve ( boolean inSeparateThread ) { if ( inSeparateThread ) { Thread t = new Thread ( this ) ; t . start ( ) ; } else run ( ) ; } public void run ( ) { try { if ( from == null ) { InetAddress ip = InetAddress . getByName ( ( String ) entry [ 0 ] ) ; entry [ 1 ] = ip ; Long l = new Long ( InetRange . ip2long ( ip ) ) ; entry [ 2 ] = entry [ 3 ] = l ; } else { InetAddress f = InetAddress . getByName ( from ) ; InetAddress t = InetAddress . getByName ( to ) ; entry [ 2 ] = new Long ( InetRange . ip2long ( f ) ) ; entry [ 3 ] = new Long ( InetRange . ip2long ( t ) ) ; } } catch ( UnknownHostException uhe ) { } } } 
=======
public final class DatetimeLocal extends AbstractDatetime { public static final DatetimeLocal THE_INSTANCE = new DatetimeLocal ( ) ; private static final Pattern THE_PATTERN = Pattern . compile ( "^([0-9]{4,})-([0-9]{2})-([0-9]{2})[T ]([0-9]{2}):([0-9]{2})(?::([0-9]{2})(?:\\.[0-9]{1,3})?)?$" ) ; private DatetimeLocal ( ) { super ( ) ; } protected final Pattern getPattern ( ) { return THE_PATTERN ; } @ Override public String getName ( ) { return "local datetime" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
