<<<<<<< HEAD
public class Node < P extends Serializable > implements RequestHandler , MessageListener , MembershipListener { private static final String LOCK_ID = "BroadCastLock" ; private static final String MESSAGE_CHANNEL_PROPERTIES = "UDP(mcast_addr=228.8.8.8;mcast_port=45566;ip_ttl=32;" + "mcast_send_buf_size=150000;mcast_recv_buf_size=80000):" + "PING(timeout=2000;num_initial_members=3):" + "MERGE2(min_interval=5000;max_interval=10000):" + "FD_SOCK:" + "VERIFY_SUSPECT(timeout=1500):" + "pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800):" + "UNICAST(timeout=5000):" + "pbcast.STABLE(desired_avg_gossip=20000):" + "FRAG(frag_size=4096;down_thread=false;up_thread=false):" + "pbcast.GMS(join_timeout=5000;join_retry_timeout=2000;" + "shun=false;print_local_addr=true):" + "pbcast.STATE_TRANSFER" ; static final String LOCK_CHANNEL_PROPERTIES = "" + "UDP(mcast_addr=228.3.11.76;mcast_port=12345;ip_ttl=1;" + "mcast_send_buf_size=150000;mcast_recv_buf_size=80000)" + ":PING(timeout=500;num_initial_members=1)" + ":FD" + ":VERIFY_SUSPECT(timeout=1500)" + ":pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800)" + ":UNICAST(timeout=5000)" + ":pbcast.STABLE(desired_avg_gossip=200)" + ":FRAG(frag_size=4096)" + ":pbcast.GMS(join_timeout=5000;join_retry_timeout=1000;" + "shun=false;print_local_addr=false)" ; private JChannel channel ; private JChannel lockChannel ; private MessageDispatcher dispatcher ; private ClusterListener < P > listener ; @ SuppressWarnings ( "deprecation" ) private DistributedLockManager distributedLockManager ; private static final Object broadcastLock = new Object ( ) ; public Node ( ) { this ( null ) ; } @ SuppressWarnings ( "deprecation" ) public Node ( ClusterListener < P > listener ) { try { lockChannel = new JChannel ( LOCK_CHANNEL_PROPERTIES ) ; distributedLockManager = new DistributedLockManager ( new VotingAdapter ( lockChannel ) , String . valueOf ( System . currentTimeMillis ( ) ) ) ; lockChannel . connect ( Node . class . getName ( ) + "Lock" ) ; Thread . sleep ( 1000 ) ; this . listener = listener ; channel = new JChannel ( MESSAGE_CHANNEL_PROPERTIES ) ; channel . setOpt ( Channel . GET_STATE_EVENTS , new Boolean ( true ) ) ; dispatcher = new MessageDispatcher ( channel , this , this , this ) ; channel . connect ( Node . class . getName ( ) ) ; acquireClusterWideLock ( ) ; channel . getState ( null , 0 ) ; Thread . sleep ( 1000 ) ; } catch ( Exception e ) { throw new RuntimeException ( e . toString ( ) ) ; } } public void viewAccepted ( View view ) { System . out . println ( "Members " + view . size ( ) ) ; } public void suspect ( Address suspected_mbr ) { } public void block ( ) { } public void receive ( Message msg ) { } public byte [ ] getState ( ) { System . out . println ( "Getting state" ) ; if ( listener != null ) { try { return Util . objectToByteBuffer ( listener . getState ( ) ) ; } catch ( Exception e ) { e . printStackTrace ( ) ; throw new RuntimeException ( e . toString ( ) ) ; } } return null ; } @ SuppressWarnings ( "unchecked" ) public void setState ( byte [ ] state ) { System . out . println ( "Setting state" ) ; try { if ( listener != null ) { listener . setState ( ( P ) Util . objectFromByteBuffer ( state ) ) ; } } catch ( Exception e ) { throw new RuntimeException ( e . toString ( ) ) ; } finally { try { releaseClusterWideLock ( ) ; } catch ( ChannelException e ) { e . printStackTrace ( ) ; } } } public void waitForConnection ( ) throws InterruptedException { broadcast ( "Node Connecting" ) ; } public int getNumberOfNodesInCluster ( ) { return channel . getView ( ) . getMembers ( ) . size ( ) ; } @ SuppressWarnings ( "deprecation" ) public void broadcast ( Serializable message ) { try { acquireClusterWideLock ( ) ; synchronized ( broadcastLock ) { System . out . println ( "Broadcasting " + message ) ; dispatcher . castMessage ( null , new Message ( null , null , message ) , GroupRequest . GET_ALL , 0 ) ; } releaseClusterWideLock ( ) ; } catch ( Exception e ) { e . printStackTrace ( ) ; throw new RuntimeException ( e . toString ( ) ) ; } } @ SuppressWarnings ( "deprecation" ) private void releaseClusterWideLock ( ) throws ChannelException { System . out . println ( "Releasing cluster lock" ) ; boolean lockReleased = false ; while ( ! lockReleased ) { try { distributedLockManager . unlock ( LOCK_ID , lockChannel . getLocalAddress ( ) . toString ( ) ) ; lockReleased = true ; } catch ( LockNotReleasedException e ) { } } System . out . println ( "Broadcast lock released" ) ; } @ SuppressWarnings ( "deprecation" ) private void acquireClusterWideLock ( ) throws ChannelException { System . out . println ( "Grabbing cluster lock" ) ; boolean lockAcquired = false ; while ( ! lockAcquired ) { try { distributedLockManager . lock ( LOCK_ID , lockChannel . getLocalAddress ( ) . toString ( ) , 1000 ) ; lockAcquired = true ; } catch ( LockNotGrantedException e ) { } } } @ SuppressWarnings ( "unchecked" ) public Object handle ( Message message ) { System . out . println ( "Received message " + message + " on " + getAddress ( ) ) ; if ( listener != null ) { return listener . receive ( ( ClusteredTransaction < P > ) message . getObject ( ) ) ; } return null ; } public boolean isMaster ( ) { return getAddress ( ) . equals ( getMasterAddress ( ) ) ; } public Address getMasterAddress ( ) { try { Vector < Address > members = channel . getView ( ) . getMembers ( ) ; Address master = members . firstElement ( ) ; channel . getState ( master , 1000 ) ; return channel . getView ( ) . getMembers ( ) . firstElement ( ) ; } catch ( Exception e ) { throw new RuntimeException ( e . toString ( ) ) ; } } public void shutdown ( ) { channel . close ( ) ; lockChannel . close ( ) ; dispatcher . stop ( ) ; try { Thread . sleep ( 1000 ) ; } catch ( InterruptedException e ) { } } public void setListener ( ClusterListener < P > listener ) { this . listener = listener ; } @ SuppressWarnings ( "deprecation" ) public Address getAddress ( ) { return channel . getLocalAddress ( ) ; } } 
=======
public class TokenMgrError extends Error { static final int LEXICAL_ERROR = 0 ; static final int STATIC_LEXER_ERROR = 1 ; static final int INVALID_LEXICAL_STATE = 2 ; static final int LOOP_DETECTED = 3 ; int errorCode ; protected static final String addEscapes ( String str ) { StringBuffer retval = new StringBuffer ( ) ; char ch ; for ( int i = 0 ; i < str . length ( ) ; i ++ ) { switch ( str . charAt ( i ) ) { case 0 : continue ; case '\b' : retval . append ( "\\b" ) ; continue ; case '\t' : retval . append ( "\\t" ) ; continue ; case '\n' : retval . append ( "\\n" ) ; continue ; case '\f' : retval . append ( "\\f" ) ; continue ; case '\r' : retval . append ( "\\r" ) ; continue ; case '\"' : retval . append ( "\\\"" ) ; continue ; case '\'' : retval . append ( "\\\'" ) ; continue ; case '\\' : retval . append ( "\\\\" ) ; continue ; default : if ( ( ch = str . charAt ( i ) ) < 0x20 || ch > 0x7e ) { String s = "0000" + Integer . toString ( ch , 16 ) ; retval . append ( "\\u" + s . substring ( s . length ( ) - 4 , s . length ( ) ) ) ; } else { retval . append ( ch ) ; } continue ; } } return retval . toString ( ) ; } protected static String LexicalError ( boolean EOFSeen , int lexState , int errorLine , int errorColumn , String errorAfter , char curChar ) { return ( "Lexical error at line " + errorLine + ", column " + errorColumn + ".  Encountered: " + ( EOFSeen ? "<EOF> " : ( "\"" + addEscapes ( String . valueOf ( curChar ) ) + "\"" ) + " (" + ( int ) curChar + "), " ) + "after : \"" + addEscapes ( errorAfter ) + "\"" ) ; } public String getMessage ( ) { return super . getMessage ( ) ; } public TokenMgrError ( ) { } public TokenMgrError ( String message , int reason ) { super ( message ) ; errorCode = reason ; } public TokenMgrError ( boolean EOFSeen , int lexState , int errorLine , int errorColumn , String errorAfter , char curChar , int reason ) { this ( LexicalError ( EOFSeen , lexState , errorLine , errorColumn , errorAfter , curChar ) , reason ) ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
