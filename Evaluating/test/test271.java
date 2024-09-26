public class SimpleResolver implements Resolver { private final UDPSocketLocator locator ; private final UDPChannel channel ; private ILogger logger ; public static final int DEFAULT_EDNS_PAYLOADSIZE = 1280 ; private boolean ignoreTruncation ; private OPTRecord queryOPT ; private TSIG tsig ; private int timeoutValue = 5 * 1000 ; private static final short DEFAULT_UDPSIZE = 512 ; private Map < Integer , ResponseContext > contexts = new HashMap < Integer , ResponseContext > ( ) ; class ResponseContext { final ResolverListener listener ; final long deadline ; ResponseContext ( ResolverListener listener , long deadline ) { this . listener = listener ; this . deadline = deadline ; } } class LazyResponse implements ResolverListener { Exception exception ; Message message ; public synchronized void handleException ( Object id , Exception e ) { this . exception = e ; notifyAll ( ) ; } public synchronized void receiveMessage ( Object id , Message m ) { this . message = m ; notifyAll ( ) ; } } public SimpleResolver ( UDPChannel channel ) { this . channel = channel ; this . locator = null ; readResponses ( ) ; } public void setLogger ( ILogger logger ) { this . logger = logger ; } private void error ( String message , Throwable exception ) { if ( logger != null ) { logger . error ( message , exception ) ; } else { } } private void readResponses ( ) { final ByteBuffer dst = ByteBuffer . allocate ( 4096 ) ; channel . read ( dst , timeoutValue , TimeUnit . MILLISECONDS , null , new CompletionHandler < Integer , Void > ( ) { public void cancelled ( Void attachment ) { } public void completed ( Integer result , Void attachment ) { dst . flip ( ) ; byte [ ] in = new byte [ dst . remaining ( ) ] ; dst . get ( in ) ; dst . clear ( ) ; channel . read ( dst , timeoutValue , TimeUnit . MILLISECONDS , attachment , this ) ; try { handleResponse ( in ) ; } catch ( WireParseException e ) { error ( "reading response" , e ) ; } } public void failed ( Throwable e , Void attachment ) { if ( e instanceof SocketTimeoutException ) { checkTimeOut ( ) ; channel . read ( dst , timeoutValue , TimeUnit . MILLISECONDS , attachment , this ) ; } else { error ( "Unexpected error in SimpleResolver read" , e ) ; } } } ) ; } private void handleResponse ( byte [ ] in ) throws WireParseException { if ( in . length < Header . LENGTH ) { throw new WireParseException ( "Invalid DNS header - too short (" + in . length + ")" ) ; } Integer id = ( ( in [ 0 ] & 0xFF ) << 8 ) + ( in [ 1 ] & 0xFF ) ; ResponseContext context ; synchronized ( this ) { context = contexts . remove ( id ) ; } if ( context == null ) { } else { try { Message response = parseMessage ( in ) ; context . listener . receiveMessage ( id , response ) ; } catch ( Exception e ) { context . listener . handleException ( id , e ) ; error ( "handling response" , e ) ; } } } private void handleException ( int id , Exception e ) { ResponseContext context ; synchronized ( this ) { context = contexts . remove ( id ) ; } if ( context != null ) context . listener . handleException ( id , e ) ; } @ Deprecated public void setTCP ( boolean flag ) { } public void setIgnoreTruncation ( boolean flag ) { this . ignoreTruncation = flag ; } @ SuppressWarnings ( "unchecked" ) public void setEDNS ( int level , int payloadSize , int flags , List options ) { if ( level != 0 && level != - 1 ) throw new IllegalArgumentException ( "invalid EDNS level - " + "must be 0 or -1" ) ; if ( payloadSize == 0 ) payloadSize = DEFAULT_EDNS_PAYLOADSIZE ; queryOPT = new OPTRecord ( payloadSize , 0 , level , flags , options ) ; } public void setEDNS ( int level ) { setEDNS ( level , 0 , 0 , null ) ; } public void setTSIGKey ( TSIG key ) { tsig = key ; } TSIG getTSIGKey ( ) { return tsig ; } public void setTimeout ( int secs , int msecs ) { timeoutValue = secs * 1000 + msecs ; } public void setTimeout ( int secs ) { setTimeout ( secs , 0 ) ; } long getTimeout ( ) { return timeoutValue ; } private Message parseMessage ( byte [ ] b ) throws WireParseException { try { return ( new Message ( b ) ) ; } catch ( IOException e ) { if ( Options . check ( "verbose" ) ) e . printStackTrace ( ) ; if ( ! ( e instanceof WireParseException ) ) e = new WireParseException ( "Error parsing message" ) ; throw ( WireParseException ) e ; } } private void verifyTSIG ( Message query , Message response , byte [ ] b , TSIG tsig ) { if ( tsig == null ) return ; } private void applyEDNS ( Message query ) { if ( queryOPT == null || query . getOPT ( ) != null ) return ; query . addRecord ( queryOPT , Section . ADDITIONAL ) ; } public Message send ( Message query ) throws IOException { if ( Options . check ( "verbose" ) ) if ( query . getHeader ( ) . getOpcode ( ) == Opcode . QUERY ) { Record question = query . getQuestion ( ) ; if ( question != null && question . getType ( ) == Type . AXFR ) return sendAXFR ( query ) ; } LazyResponse lazyResponse = new LazyResponse ( ) ; sendAsync ( query , lazyResponse ) ; try { synchronized ( lazyResponse ) { lazyResponse . wait ( timeoutValue * 2 ) ; if ( lazyResponse . exception instanceof IOException ) throw ( IOException ) lazyResponse . exception ; if ( lazyResponse . exception != null ) throw new RuntimeException ( lazyResponse . exception ) ; if ( lazyResponse . message == null ) throw new SocketTimeoutException ( ) ; return lazyResponse . message ; } } catch ( InterruptedException e ) { error ( "sending query" , e ) ; } return null ; } public synchronized Object sendAsync ( Message query , ResolverListener listener ) { final long deadline = System . currentTimeMillis ( ) + timeoutValue ; final int id = getMessageId ( query ) ; if ( id == - 1 ) { listener . handleException ( null , new RuntimeException ( "Could not find valid DNS message ID" ) ) ; } else { contexts . put ( id , new ResponseContext ( listener , deadline ) ) ; } query = ( Message ) query . clone ( ) ; applyEDNS ( query ) ; if ( tsig != null ) tsig . apply ( query , null ) ; byte [ ] out = query . toWire ( Message . MAXLENGTH ) ; ByteBuffer buffer = ByteBuffer . wrap ( out ) ; channel . write ( buffer , timeoutValue , TimeUnit . MILLISECONDS , null , new CompletionHandler < Integer , Void > ( ) { public void cancelled ( Void attachment ) { handleException ( id , new Exception ( "Request send cancelled" ) ) ; } public void completed ( Integer result , Void attachment ) { } public void failed ( Throwable exc , Void attachment ) { if ( exc instanceof Exception ) handleException ( id , ( Exception ) exc ) ; else handleException ( id , new Exception ( exc ) ) ; } } ) ; return id ; } private int getMessageId ( Message message ) { int id = message . getHeader ( ) . getID ( ) ; int count = 0 ; while ( count < 10 ) { if ( ! contexts . containsKey ( id ) ) return id ; count ++ ; id = ( id + 1 ) & 0xFFFF ; message . getHeader ( ) . setID ( id ) ; } return - 1 ; } @ SuppressWarnings ( "unchecked" ) private Message sendAXFR ( Message query ) throws IOException { Name qname = query . getQuestion ( ) . getName ( ) ; ZoneTransferIn xfrin = ZoneTransferIn . newAXFR ( qname , locator . getAddress ( ) . toInetAddress ( ) . getHostAddress ( ) , locator . getPort ( ) , tsig ) ; xfrin . setTimeout ( ( int ) ( getTimeout ( ) / 1000 ) ) ; try { xfrin . run ( ) ; } catch ( ZoneTransferException e ) { throw new WireParseException ( e . getMessage ( ) ) ; } List < Record > records = xfrin . getAXFR ( ) ; Message response = new Message ( query . getHeader ( ) . getID ( ) ) ; response . getHeader ( ) . setFlag ( Flags . AA ) ; response . getHeader ( ) . setFlag ( Flags . QR ) ; response . addRecord ( query . getQuestion ( ) , Section . QUESTION ) ; Iterator < Record > it = records . iterator ( ) ; while ( it . hasNext ( ) ) response . addRecord ( it . next ( ) , Section . ANSWER ) ; return response ; } public void setPort ( int port ) { throw new RuntimeException ( "SimpleResolver cannot implement setPort!" ) ; } public synchronized void checkTimeOut ( ) { long now = System . currentTimeMillis ( ) ; List < Integer > timedOutKeys = new ArrayList < Integer > ( ) ; for ( Integer key : contexts . keySet ( ) ) if ( now > contexts . get ( key ) . deadline ) timedOutKeys . add ( key ) ; for ( Integer key : timedOutKeys ) { ResponseContext context = contexts . remove ( key ) ; context . listener . handleException ( key , new SocketTimeoutException ( "Request " + key + " timed out" ) ) ; } } public UDPSocketLocator getRemoteAddress ( ) { return channel . getRemoteAddress ( ) ; } public synchronized void shutdown ( ) throws IOException { channel . close ( ) ; for ( Integer key : contexts . keySet ( ) ) { contexts . get ( key ) . listener . handleException ( key , new SocketException ( "The resolver was shut down" ) ) ; } } } 