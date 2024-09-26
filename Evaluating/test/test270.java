public final class AsynchronousLookup { private static Resolver defaultResolver ; private static Name [ ] defaultSearchPath ; private static Map < Integer , Cache > defaultCaches = new HashMap < Integer , Cache > ( ) ; private Resolver resolver ; private Name [ ] searchPath ; private Cache cache ; private boolean temporary_cache ; private int credibility ; private Name name ; private int type ; private int dclass ; private boolean verbose ; private int iterations ; private boolean foundAlias ; private boolean done ; private boolean doneCurrent ; private List < Name > aliases ; private Record [ ] answers ; private int result ; private String error ; private boolean nxdomain ; private boolean badresponse ; private String badresponse_error ; private boolean networkerror ; private boolean timedout ; private boolean nametoolong ; private boolean referral ; private static final Name [ ] noAliases = new Name [ 0 ] ; public static final int SUCCESSFUL = 0 ; public static final int UNRECOVERABLE = 1 ; public static final int TRY_AGAIN = 2 ; public static final int HOST_NOT_FOUND = 3 ; public static final int TYPE_NOT_FOUND = 4 ; public static synchronized Resolver getDefaultResolver ( ) { return defaultResolver ; } public static synchronized void setDefaultResolver ( Resolver resolver ) { defaultResolver = resolver ; } public static synchronized Cache getDefaultCache ( int dclass ) { Cache c = defaultCaches . get ( dclass ) ; if ( c == null ) { c = new Cache ( dclass ) ; defaultCaches . put ( dclass , c ) ; } return c ; } public static synchronized void setDefaultCache ( Cache cache , int dclass ) { defaultCaches . put ( dclass , cache ) ; } public static synchronized Name [ ] getDefaultSearchPath ( ) { return defaultSearchPath ; } public static synchronized void setDefaultSearchPath ( Name [ ] domains ) { defaultSearchPath = domains ; } public static synchronized void setDefaultSearchPath ( String [ ] domains ) throws TextParseException { if ( domains == null ) { defaultSearchPath = null ; return ; } Name [ ] newdomains = new Name [ domains . length ] ; for ( int i = 0 ; i < domains . length ; i ++ ) newdomains [ i ] = Name . fromString ( domains [ i ] , Name . root ) ; defaultSearchPath = newdomains ; } private final void reset ( ) { iterations = 0 ; foundAlias = false ; done = false ; doneCurrent = false ; aliases = null ; answers = null ; result = - 1 ; error = null ; nxdomain = false ; badresponse = false ; badresponse_error = null ; networkerror = false ; timedout = false ; nametoolong = false ; referral = false ; if ( temporary_cache ) cache . clearCache ( ) ; } public AsynchronousLookup ( Name name , int type , int dclass ) { if ( ! Type . isRR ( type ) && type != Type . ANY ) throw new IllegalArgumentException ( "Cannot query for " + "meta-types other than ANY" ) ; this . name = name ; this . type = type ; this . dclass = dclass ; synchronized ( AsynchronousLookup . class ) { this . resolver = getDefaultResolver ( ) ; this . searchPath = getDefaultSearchPath ( ) ; this . cache = getDefaultCache ( dclass ) ; } this . credibility = Credibility . NORMAL ; this . verbose = Options . check ( "verbose" ) ; this . result = - 1 ; } public AsynchronousLookup ( Name name , int type ) { this ( name , type , DClass . IN ) ; } public AsynchronousLookup ( Name name ) { this ( name , Type . A , DClass . IN ) ; } public AsynchronousLookup ( String name , int type , int dclass ) throws TextParseException { this ( Name . fromString ( name ) , type , dclass ) ; } public AsynchronousLookup ( String name , int type ) throws TextParseException { this ( Name . fromString ( name ) , type , DClass . IN ) ; } public AsynchronousLookup ( String name ) throws TextParseException { this ( Name . fromString ( name ) , Type . A , DClass . IN ) ; } public void setResolver ( Resolver resolver ) { this . resolver = resolver ; } public void setSearchPath ( Name [ ] domains ) { this . searchPath = domains ; } public void setSearchPath ( String [ ] domains ) throws TextParseException { if ( domains == null ) { this . searchPath = null ; return ; } Name [ ] newdomains = new Name [ domains . length ] ; for ( int i = 0 ; i < domains . length ; i ++ ) newdomains [ i ] = Name . fromString ( domains [ i ] , Name . root ) ; this . searchPath = newdomains ; } public void setCache ( Cache cache ) { if ( cache == null ) { this . cache = new Cache ( dclass ) ; this . temporary_cache = true ; } else { this . cache = cache ; this . temporary_cache = false ; } } public void setCredibility ( int credibility ) { this . credibility = credibility ; } private < A > void follow ( Name name , Name oldname , A attachment , CompletionHandler < Record [ ] , A > handler ) { foundAlias = true ; badresponse = false ; networkerror = false ; timedout = false ; nxdomain = false ; referral = false ; iterations ++ ; if ( iterations >= 6 || name . equals ( oldname ) ) { result = UNRECOVERABLE ; error = "CNAME loop" ; done = true ; handler . failed ( new Exception ( error ) , attachment ) ; return ; } if ( aliases == null ) aliases = new ArrayList < Name > ( ) ; aliases . add ( oldname ) ; lookup ( name , attachment , handler ) ; } private < A > void processResponse ( Name name , SetResponse response , A attachment , CompletionHandler < Record [ ] , A > handler ) { if ( response . isSuccessful ( ) ) { RRset [ ] rrsets = response . answers ( ) ; List < Record > l = new ArrayList < Record > ( ) ; Iterator < Record > it ; int i ; for ( i = 0 ; i < rrsets . length ; i ++ ) { it = rrsets [ i ] . rrs ( ) ; while ( it . hasNext ( ) ) l . add ( it . next ( ) ) ; } result = SUCCESSFUL ; answers = l . toArray ( new Record [ l . size ( ) ] ) ; done = true ; handler . completed ( getAnswers ( ) , attachment ) ; } else if ( response . isNXDOMAIN ( ) ) { nxdomain = true ; doneCurrent = true ; result = HOST_NOT_FOUND ; done = true ; handler . failed ( new UnknownHostException ( ) , attachment ) ; } else if ( response . isNXRRSET ( ) ) { result = TYPE_NOT_FOUND ; answers = null ; done = true ; handler . failed ( new Exception ( getErrorString ( ) ) , attachment ) ; } else if ( response . isCNAME ( ) ) { CNAMERecord cname = response . getCNAME ( ) ; follow ( cname . getTarget ( ) , name , attachment , handler ) ; } else if ( response . isDNAME ( ) ) { DNAMERecord dname = response . getDNAME ( ) ; try { follow ( name . fromDNAME ( dname ) , name , attachment , handler ) ; } catch ( NameTooLongException e ) { result = UNRECOVERABLE ; error = "Invalid DNAME target" ; done = true ; handler . failed ( new Exception ( error ) , attachment ) ; } } else if ( response . isDelegation ( ) ) { referral = true ; } } private synchronized < A > void lookup ( final Name current , final A attachment , final CompletionHandler < Record [ ] , A > handler ) { final Record question = Record . newRecord ( current , type , dclass ) ; final Message query = Message . newQuery ( question ) ; resolver . sendAsync ( query , new ResolverListener ( ) { public void handleException ( Object id , Exception e ) { if ( e instanceof InterruptedIOException || e instanceof TimeoutException ) timedout = true ; else networkerror = true ; checkError ( ) ; handler . failed ( e , attachment ) ; } public void receiveMessage ( Object id , Message response ) { int rcode = response . getHeader ( ) . getRcode ( ) ; if ( rcode != Rcode . NOERROR && rcode != Rcode . NXDOMAIN ) { if ( rcode == Rcode . SERVFAIL ) result = TRY_AGAIN ; badresponse = true ; badresponse_error = Rcode . string ( rcode ) ; checkError ( ) ; handler . failed ( new Exception ( getErrorString ( ) ) , attachment ) ; return ; } if ( ! query . getQuestion ( ) . equals ( response . getQuestion ( ) ) ) { badresponse = true ; badresponse_error = "response does not match query" ; checkError ( ) ; handler . failed ( new Exception ( getErrorString ( ) ) , attachment ) ; return ; } SetResponse sr = cache . addMessage ( response ) ; if ( sr == null ) sr = cache . lookupRecords ( current , type , credibility ) ; if ( verbose ) { System . err . println ( "queried " + current + " " + Type . string ( type ) ) ; System . err . println ( sr ) ; } processResponse ( current , sr , attachment , handler ) ; } } ) ; } private synchronized < A > void resolve ( Name current , Name suffix , A attachment , CompletionHandler < Record [ ] , A > handler ) { doneCurrent = false ; Name tname = null ; if ( suffix == null ) tname = current ; else { try { tname = Name . concatenate ( current , suffix ) ; } catch ( NameTooLongException e ) { nametoolong = true ; checkError ( ) ; handler . failed ( e , attachment ) ; return ; } } lookup ( tname , attachment , handler ) ; } public synchronized < A > void run ( A attachment , CompletionHandler < Record [ ] , A > handler ) { if ( done ) reset ( ) ; if ( name . isAbsolute ( ) ) resolve ( name , null , attachment , handler ) ; else if ( searchPath == null ) resolve ( name , Name . root , attachment , handler ) ; else { throw new RuntimeException ( this . getClass ( ) . getName ( ) + ": Search path not implemented" ) ; } } private void checkError ( ) { if ( ! done ) { if ( badresponse ) { result = TRY_AGAIN ; error = badresponse_error ; done = true ; } else if ( timedout ) { result = TRY_AGAIN ; error = "timed out" ; done = true ; } else if ( networkerror ) { result = TRY_AGAIN ; error = "network error" ; done = true ; } else if ( nxdomain ) { result = HOST_NOT_FOUND ; done = true ; } else if ( referral ) { result = UNRECOVERABLE ; error = "referral" ; done = true ; } else if ( nametoolong ) { result = UNRECOVERABLE ; error = "name too long" ; done = true ; } if ( verbose ) System . err . println ( name + " lookup failed: " + getErrorString ( ) ) ; } } private void checkDone ( ) { if ( done && result != - 1 ) return ; StringBuffer sb = new StringBuffer ( "Lookup of " + name + " " ) ; if ( dclass != DClass . IN ) sb . append ( DClass . string ( dclass ) + " " ) ; sb . append ( Type . string ( type ) + " isn't done" ) ; throw new IllegalStateException ( sb . toString ( ) ) ; } public Record [ ] getAnswers ( ) { checkDone ( ) ; return answers ; } public Name [ ] getAliases ( ) { checkDone ( ) ; if ( aliases == null ) return noAliases ; return aliases . toArray ( new Name [ aliases . size ( ) ] ) ; } public int getResult ( ) { checkDone ( ) ; return result ; } public String getErrorString ( ) { checkDone ( ) ; if ( error != null ) return error ; switch ( result ) { case SUCCESSFUL : return "successful" ; case UNRECOVERABLE : return "unrecoverable error" ; case TRY_AGAIN : return "try again" ; case HOST_NOT_FOUND : return "host not found" ; case TYPE_NOT_FOUND : return "type not found" ; } throw new IllegalStateException ( "unknown result" ) ; } } 