<<<<<<< HEAD
public class AMQConnection extends Closeable implements Connection , QueueConnection , TopicConnection { private static final Logger _logger = LoggerFactory . getLogger ( AMQConnection . class ) ; private final IdFactory _idFactory = new IdFactory ( ) ; private final Object _failoverMutex = new Object ( ) ; public static class BrokerDetail { public String host ; public int port ; public BrokerDetail ( String host , int port ) { this . host = host ; this . port = port ; } public String toString ( ) { return ( host + ":" + port ) ; } public boolean equals ( Object o ) { if ( ! ( o instanceof BrokerDetail ) ) { return false ; } BrokerDetail bd = ( BrokerDetail ) o ; return host . equals ( bd . host ) && ( port == bd . port ) ; } } private long _maximumChannelCount ; private long _maximumFrameSize ; private AMQProtocolHandler _protocolHandler ; private final Map _sessions = new LinkedHashMap ( ) ; private String _clientName ; private BrokerDetail [ ] _brokerDetails ; private int _activeBrokerIndex = - 1 ; private String _username ; private String _password ; private String _virtualPath ; private ExceptionListener _exceptionListener ; private ConnectionListener _connectionListener ; private boolean _started ; public AMQConnection ( String host , int port , String username , String password , String clientName , String virtualPath ) throws AMQException { this ( new BrokerDetail ( host , port ) , username , password , clientName , virtualPath ) ; } public AMQConnection ( String brokerDetails , String username , String password , String clientName , String virtualPath ) throws AMQException { this ( parseBrokerDetails ( brokerDetails ) , username , password , clientName , virtualPath ) ; } public AMQConnection ( BrokerDetail brokerDetail , String username , String password , String clientName , String virtualPath ) throws AMQException { this ( new BrokerDetail [ ] { brokerDetail } , username , password , clientName , virtualPath ) ; } public AMQConnection ( BrokerDetail [ ] brokerDetails , String username , String password , String clientName , String virtualPath ) throws AMQException { if ( brokerDetails == null || brokerDetails . length == 0 ) { throw new IllegalArgumentException ( "Broker details must specify at least one broker" ) ; } _brokerDetails = brokerDetails ; _clientName = clientName ; _username = username ; _password = password ; _virtualPath = virtualPath ; _protocolHandler = new AMQProtocolHandler ( this ) ; for ( int i = 0 ; i < brokerDetails . length ; i ++ ) { try { makeBrokerConnection ( brokerDetails [ i ] ) ; _activeBrokerIndex = i ; break ; } catch ( Exception e ) { _logger . info ( "Unable to connect to broker at " + brokerDetails [ i ] , e ) ; } } if ( _activeBrokerIndex == - 1 ) { StringBuffer buf = new StringBuffer ( ) ; for ( int i = 0 ; i < brokerDetails . length ; i ++ ) { buf . append ( brokerDetails [ i ] . toString ( ) ) . append ( ' ' ) ; } throw new AMQException ( "Unable to connect to any specified broker in list " + buf . toString ( ) ) ; } } protected AMQConnection ( String username , String password , String clientName , String virtualPath ) { _clientName = clientName ; _username = username ; _password = password ; _virtualPath = virtualPath ; } private static BrokerDetail [ ] parseBrokerDetails ( String brokerDetails ) { if ( brokerDetails == null ) { throw new IllegalArgumentException ( "Broker string cannot be null" ) ; } LinkedList ll = new LinkedList ( ) ; StringTokenizer tokenizer = new StringTokenizer ( brokerDetails , ";" ) ; while ( tokenizer . hasMoreTokens ( ) ) { String token = tokenizer . nextToken ( ) ; int index = token . indexOf ( ":" ) ; if ( index == - 1 ) { throw new IllegalArgumentException ( "Invalid broker string: " + token + ". Must be in format host:port" ) ; } else { int port = Integer . parseInt ( token . substring ( index + 1 ) ) ; int hostStart = 0 ; if ( token . charAt ( 0 ) == '$' ) { hostStart = 1 ; } BrokerDetail bd = new BrokerDetail ( token . substring ( hostStart , index ) , port ) ; ll . add ( bd ) ; } } BrokerDetail [ ] bd = new BrokerDetail [ ll . size ( ) ] ; return ( BrokerDetail [ ] ) ll . toArray ( bd ) ; } private void makeBrokerConnection ( BrokerDetail brokerDetail ) throws IOException , AMQException { TransportConnection . getInstance ( ) . connect ( _protocolHandler , brokerDetail ) ; _protocolHandler . attainState ( AMQState . CONNECTION_OPEN ) ; } public boolean attemptReconnection ( String host , int port ) { int index = - 1 ; BrokerDetail bd = new BrokerDetail ( host , port ) ; for ( int i = 0 ; i < _brokerDetails . length ; i ++ ) { if ( _brokerDetails [ i ] . equals ( bd ) ) { index = i ; break ; } } if ( index == - 1 ) { int len = _brokerDetails . length + 1 ; BrokerDetail [ ] newDetails = new BrokerDetail [ len ] ; System . arraycopy ( _brokerDetails , 0 , newDetails , 0 , _brokerDetails . length ) ; index = len - 1 ; newDetails [ index ] = bd ; } try { makeBrokerConnection ( bd ) ; _activeBrokerIndex = index ; return true ; } catch ( Exception e ) { _logger . info ( "Unable to connect to broker at " + bd ) ; } return false ; } public boolean attemptReconnection ( ) { if ( _activeBrokerIndex < 0 ) { _activeBrokerIndex = 0 ; } else { try { _logger . info ( "Retrying " + _brokerDetails [ _activeBrokerIndex ] ) ; makeBrokerConnection ( _brokerDetails [ _activeBrokerIndex ] ) ; return true ; } catch ( Exception e ) { _logger . info ( "Unable to reconnect to broker at " + _brokerDetails [ _activeBrokerIndex ] ) ; } } for ( int i = 0 ; i < _brokerDetails . length ; i ++ ) { if ( i == _activeBrokerIndex ) { continue ; } try { makeBrokerConnection ( _brokerDetails [ i ] ) ; _activeBrokerIndex = i ; return true ; } catch ( Exception e ) { _logger . info ( "Unable to connect to broker at " + _brokerDetails [ i ] ) ; } } return false ; } public BrokerDetail getActiveBrokerDetails ( ) { if ( _activeBrokerIndex < 0 ) { return null ; } return _brokerDetails [ _activeBrokerIndex ] ; } public Session createSession ( final boolean transacted , final int acknowledgeMode ) throws JMSException { return createSession ( transacted , acknowledgeMode , AMQSession . DEFAULT_PREFETCH ) ; } public org . openamq . jms . Session createSession ( final boolean transacted , final int acknowledgeMode , final int prefetch ) throws JMSException { checkNotClosed ( ) ; if ( channelLimitReached ( ) ) { throw new ChannelLimitReachedException ( _maximumChannelCount ) ; } else { return ( org . openamq . jms . Session ) new FailoverSupport ( ) { public Object operation ( ) throws JMSException { int channelId = _idFactory . getChannelId ( ) ; AMQFrame frame = ChannelOpenBody . createAMQFrame ( channelId , null ) ; if ( _logger . isDebugEnabled ( ) ) { _logger . debug ( "Write channel open frame for channel id " + channelId ) ; } AMQSession session = new AMQSession ( AMQConnection . this , channelId , transacted , acknowledgeMode , prefetch ) ; _protocolHandler . addSessionByChannel ( channelId , session ) ; registerSession ( channelId , session ) ; try { _protocolHandler . writeCommandFrameAndWaitForReply ( frame , new SpecificMethodFrameListener ( channelId , ChannelOpenOkBody . class ) ) ; _protocolHandler . writeCommandFrameAndWaitForReply ( BasicQosBody . createAMQFrame ( channelId , 0 , prefetch , false ) , new SpecificMethodFrameListener ( channelId , BasicQosOkBody . class ) ) ; if ( transacted ) { if ( _logger . isDebugEnabled ( ) ) { _logger . debug ( "Issuing TxSelect for " + channelId ) ; } _protocolHandler . writeCommandFrameAndWaitForReply ( TxSelectBody . createAMQFrame ( channelId ) , new SpecificMethodFrameListener ( channelId , TxSelectOkBody . class ) ) ; } } catch ( AMQException e ) { _protocolHandler . removeSessionByChannel ( channelId ) ; deregisterSession ( channelId ) ; throw new JMSException ( "Error creating session: " + e ) ; } if ( _started ) { session . start ( ) ; } return session ; } } . execute ( this ) ; } } public QueueSession createQueueSession ( boolean transacted , int acknowledgeMode ) throws JMSException { return ( QueueSession ) createSession ( transacted , acknowledgeMode ) ; } public TopicSession createTopicSession ( boolean transacted , int acknowledgeMode ) throws JMSException { return ( TopicSession ) createSession ( transacted , acknowledgeMode ) ; } private boolean channelLimitReached ( ) { return _maximumChannelCount != 0 && _sessions . size ( ) == _maximumChannelCount ; } public String getClientID ( ) throws JMSException { checkNotClosed ( ) ; return _clientName ; } public void setClientID ( String clientID ) throws JMSException { checkNotClosed ( ) ; _clientName = clientID ; } public ConnectionMetaData getMetaData ( ) throws JMSException { checkNotClosed ( ) ; return null ; } public ExceptionListener getExceptionListener ( ) throws JMSException { checkNotClosed ( ) ; return _exceptionListener ; } public void setExceptionListener ( ExceptionListener listener ) throws JMSException { checkNotClosed ( ) ; _exceptionListener = listener ; } public void start ( ) throws JMSException { checkNotClosed ( ) ; if ( ! _started ) { final Iterator it = _sessions . entrySet ( ) . iterator ( ) ; while ( it . hasNext ( ) ) { final AMQSession s = ( AMQSession ) ( ( Map . Entry ) it . next ( ) ) . getValue ( ) ; s . start ( ) ; } _started = true ; } } public void stop ( ) throws JMSException { checkNotClosed ( ) ; if ( _started ) { for ( Iterator i = _sessions . values ( ) . iterator ( ) ; i . hasNext ( ) ; ) { ( ( AMQSession ) i . next ( ) ) . stop ( ) ; } _started = false ; } } public void close ( ) throws JMSException { synchronized ( getFailoverMutex ( ) ) { if ( ! _closed . getAndSet ( true ) ) { try { closeAllSessions ( null ) ; _protocolHandler . closeConnection ( ) ; } catch ( AMQException e ) { throw new JMSException ( "Error closing connection: " + e ) ; } } } } private void markAllSessionsClosed ( ) { final LinkedList sessionCopy = new LinkedList ( _sessions . values ( ) ) ; final Iterator it = sessionCopy . iterator ( ) ; while ( it . hasNext ( ) ) { final AMQSession session = ( AMQSession ) it . next ( ) ; session . markClosed ( ) ; } _sessions . clear ( ) ; } private void closeAllSessions ( Throwable cause ) throws JMSException { final LinkedList sessionCopy = new LinkedList ( _sessions . values ( ) ) ; final Iterator it = sessionCopy . iterator ( ) ; JMSException sessionException = null ; while ( it . hasNext ( ) ) { final AMQSession session = ( AMQSession ) it . next ( ) ; if ( cause != null ) { session . closed ( cause ) ; } else { try { session . close ( ) ; } catch ( JMSException e ) { _logger . error ( "Error closing session: " + e ) ; sessionException = e ; } } } _sessions . clear ( ) ; if ( sessionException != null ) { throw sessionException ; } } public ConnectionConsumer createConnectionConsumer ( Destination destination , String messageSelector , ServerSessionPool sessionPool , int maxMessages ) throws JMSException { checkNotClosed ( ) ; return null ; } public ConnectionConsumer createConnectionConsumer ( Queue queue , String messageSelector , ServerSessionPool sessionPool , int maxMessages ) throws JMSException { checkNotClosed ( ) ; return null ; } public ConnectionConsumer createConnectionConsumer ( Topic topic , String messageSelector , ServerSessionPool sessionPool , int maxMessages ) throws JMSException { checkNotClosed ( ) ; return null ; } public ConnectionConsumer createDurableConnectionConsumer ( Topic topic , String subscriptionName , String messageSelector , ServerSessionPool sessionPool , int maxMessages ) throws JMSException { checkNotClosed ( ) ; return null ; } IdFactory getIdFactory ( ) { return _idFactory ; } public long getMaximumChannelCount ( ) { checkNotClosed ( ) ; return _maximumChannelCount ; } public void setConnectionListener ( ConnectionListener listener ) { _connectionListener = listener ; } public ConnectionListener getConnectionListener ( ) { return _connectionListener ; } public void setMaximumChannelCount ( long maximumChannelCount ) { checkNotClosed ( ) ; _maximumChannelCount = maximumChannelCount ; } public void setMaximumFrameSize ( long frameMax ) { _maximumFrameSize = frameMax ; } public long getMaximumFrameSize ( ) { return _maximumFrameSize ; } public Map getSessions ( ) { return _sessions ; } public String getUsername ( ) { return _username ; } public String getPassword ( ) { return _password ; } public String getVirtualPath ( ) { return _virtualPath ; } public AMQProtocolHandler getProtocolHandler ( ) { return _protocolHandler ; } public void bytesSent ( long writtenBytes ) { if ( _connectionListener != null ) { _connectionListener . bytesSent ( writtenBytes ) ; } } public void bytesReceived ( long receivedBytes ) { if ( _connectionListener != null ) { _connectionListener . bytesReceived ( receivedBytes ) ; } } public boolean firePreFailover ( boolean redirect ) { boolean proceed = true ; if ( _connectionListener != null ) { proceed = _connectionListener . preFailover ( redirect ) ; } return proceed ; } public boolean firePreResubscribe ( ) throws JMSException { if ( _connectionListener != null ) { boolean resubscribe = _connectionListener . preResubscribe ( ) ; if ( ! resubscribe ) { markAllSessionsClosed ( ) ; } return resubscribe ; } else { return true ; } } public void fireFailoverComplete ( ) { if ( _connectionListener != null ) { _connectionListener . failoverComplete ( ) ; } } public final Object getFailoverMutex ( ) { return _failoverMutex ; } public void blockUntilNotFailingOver ( ) throws InterruptedException { _protocolHandler . blockUntilNotFailingOver ( ) ; } public void exceptionReceived ( Throwable cause ) { JMSException je = null ; if ( _exceptionListener != null ) { if ( cause instanceof JMSException ) { je = ( JMSException ) cause ; } else { je = new JMSException ( "Exception thrown against " + toString ( ) + ": " + cause ) ; if ( cause instanceof Exception ) { je . setLinkedException ( ( Exception ) cause ) ; } } if ( cause instanceof IOException ) { _closed . set ( true ) ; } _exceptionListener . onException ( je ) ; } if ( je == null || ! ( je . getLinkedException ( ) instanceof AMQUndeliveredException ) ) { try { _closed . set ( true ) ; closeAllSessions ( cause ) ; } catch ( JMSException e ) { _logger . error ( "Error closing all sessions: " + e , e ) ; } } } void registerSession ( int channelId , AMQSession session ) { _sessions . put ( new Integer ( channelId ) , session ) ; } void deregisterSession ( int channelId ) { _sessions . remove ( new Integer ( channelId ) ) ; } public void resubscribeSessions ( ) throws AMQException { ArrayList sessions = new ArrayList ( _sessions . values ( ) ) ; for ( Iterator it = sessions . iterator ( ) ; it . hasNext ( ) ; ) { AMQSession s = ( AMQSession ) it . next ( ) ; _protocolHandler . addSessionByChannel ( s . getChannelId ( ) , s ) ; reopenChannel ( s . getChannelId ( ) , s . getDefaultPrefetch ( ) ) ; s . resubscribe ( ) ; } } private void reopenChannel ( int channelId , int prefetch ) throws AMQException { AMQFrame frame = ChannelOpenBody . createAMQFrame ( channelId , null ) ; try { _protocolHandler . writeCommandFrameAndWaitForReply ( frame , new SpecificMethodFrameListener ( channelId , ChannelOpenOkBody . class ) ) ; _protocolHandler . writeFrame ( BasicQosBody . createAMQFrame ( channelId , 0 , prefetch , false ) ) ; } catch ( AMQException e ) { _protocolHandler . removeSessionByChannel ( channelId ) ; deregisterSession ( channelId ) ; throw new AMQException ( "Error reopening channel " + channelId + " after failover: " + e ) ; } } public String toString ( ) { StringBuffer buf = new StringBuffer ( "AMQConnection:\n" ) ; if ( _activeBrokerIndex == - 1 ) { buf . append ( "No active broker connection" ) ; } else { buf . append ( "Host: " ) . append ( String . valueOf ( _brokerDetails [ _activeBrokerIndex ] . host ) ) ; buf . append ( "\nPort: " ) . append ( String . valueOf ( _brokerDetails [ _activeBrokerIndex ] . port ) ) ; } buf . append ( "\nVirtual Path: " ) . append ( String . valueOf ( _virtualPath ) ) ; buf . append ( "\nClient ID: " ) . append ( String . valueOf ( _clientName ) ) ; buf . append ( "\nActive session count: " ) . append ( _sessions == null ? 0 : _sessions . size ( ) ) ; return buf . toString ( ) ; } } 
=======
public class XMLDocumentIdentifier extends Identifier { private final String namespaceUri ; public static final String MEDIA_TYPE = "application/xml" ; public XMLDocumentIdentifier ( String href , String base , String namespaceUri ) { super ( href , base ) ; this . namespaceUri = namespaceUri ; } public String getNamespaceUri ( ) { return namespaceUri ; } public String getMediaType ( ) { return MEDIA_TYPE ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
