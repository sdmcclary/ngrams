public abstract class SocksProxyBase { protected InetRange directHosts = new InetRange ( ) ; protected InetAddress proxyIP = null ; protected String proxyHost = null ; protected int proxyPort ; protected Socket proxySocket = null ; protected InputStream in ; protected OutputStream out ; protected int version ; protected SocksProxyBase chainProxy = null ; protected static SocksProxyBase defaultProxy = null ; SocksProxyBase ( SocksProxyBase chainProxy , String proxyHost , int proxyPort ) throws UnknownHostException { this . chainProxy = chainProxy ; this . proxyHost = proxyHost ; if ( chainProxy == null ) { this . proxyIP = InetAddress . getByName ( proxyHost ) ; } this . proxyPort = proxyPort ; } SocksProxyBase ( String proxyHost , int proxyPort ) throws UnknownHostException { this ( null , proxyHost , proxyPort ) ; } SocksProxyBase ( SocksProxyBase chainProxy , InetAddress proxyIP , int proxyPort ) { this . chainProxy = chainProxy ; this . proxyIP = proxyIP ; this . proxyPort = proxyPort ; } SocksProxyBase ( InetAddress proxyIP , int proxyPort ) { this ( null , proxyIP , proxyPort ) ; } SocksProxyBase ( SocksProxyBase p ) { this . proxyIP = p . proxyIP ; this . proxyPort = p . proxyPort ; this . version = p . version ; this . directHosts = p . directHosts ; } public int getPort ( ) { return proxyPort ; } public InetAddress getInetAddress ( ) { return proxyIP ; } public void addDirect ( InetAddress ip ) { directHosts . add ( ip ) ; } public boolean addDirect ( String host ) { return directHosts . add ( host ) ; } public void addDirect ( InetAddress from , InetAddress to ) { directHosts . add ( from , to ) ; } public void setDirect ( InetRange ir ) { directHosts = ir ; } public InetRange getDirect ( ) { return directHosts ; } public boolean isDirect ( String host ) { return directHosts . contains ( host ) ; } public boolean isDirect ( InetAddress host ) { return directHosts . contains ( host ) ; } public void setChainProxy ( SocksProxyBase chainProxy ) { this . chainProxy = chainProxy ; } public SocksProxyBase getChainProxy ( ) { return chainProxy ; } public String toString ( ) { return ( proxyIP . getHostName ( ) + ":" + proxyPort + "\tVersion " + version ) ; } public static void setDefaultProxy ( String hostName , int port , String user ) throws UnknownHostException { defaultProxy = new Socks4Proxy ( hostName , port , user ) ; } public static void setDefaultProxy ( InetAddress ipAddress , int port , String user ) { defaultProxy = new Socks4Proxy ( ipAddress , port , user ) ; } public static void setDefaultProxy ( String hostName , int port ) throws UnknownHostException { defaultProxy = new Socks5Proxy ( hostName , port ) ; } public static void setDefaultProxy ( InetAddress ipAddress , int port ) { defaultProxy = new Socks5Proxy ( ipAddress , port ) ; } public static void setDefaultProxy ( SocksProxyBase p ) { defaultProxy = p ; } public static SocksProxyBase getDefaultProxy ( ) { return defaultProxy ; } public static SocksProxyBase parseProxy ( String proxy_entry ) { String proxy_host ; int proxy_port = 1080 ; String proxy_user = null ; String proxy_password = null ; SocksProxyBase proxy ; final java . util . StringTokenizer st = new java . util . StringTokenizer ( proxy_entry , ":" ) ; if ( st . countTokens ( ) < 1 ) { return null ; } proxy_host = st . nextToken ( ) ; if ( st . hasMoreTokens ( ) ) { try { proxy_port = Integer . parseInt ( st . nextToken ( ) . trim ( ) ) ; } catch ( final NumberFormatException nfe ) { } } if ( st . hasMoreTokens ( ) ) { proxy_user = st . nextToken ( ) ; } if ( st . hasMoreTokens ( ) ) { proxy_password = st . nextToken ( ) ; } try { if ( proxy_user == null ) { proxy = new Socks5Proxy ( proxy_host , proxy_port ) ; } else if ( proxy_password == null ) { proxy = new Socks4Proxy ( proxy_host , proxy_port , proxy_user ) ; } else { proxy = new Socks5Proxy ( proxy_host , proxy_port ) ; final UserPasswordAuthentication upa = new UserPasswordAuthentication ( proxy_user , proxy_password ) ; ( ( Socks5Proxy ) proxy ) . setAuthenticationMethod ( UserPasswordAuthentication . METHOD_ID , upa ) ; } } catch ( final UnknownHostException uhe ) { return null ; } return proxy ; } protected void startSession ( ) throws SocksException { try { if ( chainProxy == null ) { proxySocket = new Socket ( proxyIP , proxyPort ) ; } else if ( proxyIP != null ) { proxySocket = new SocksSocket ( chainProxy , proxyIP , proxyPort ) ; } else { proxySocket = new SocksSocket ( chainProxy , proxyHost , proxyPort ) ; } in = proxySocket . getInputStream ( ) ; out = proxySocket . getOutputStream ( ) ; } catch ( final SocksException se ) { throw se ; } catch ( final IOException io_ex ) { throw new SocksException ( SOCKS_PROXY_IO_ERROR , "" + io_ex ) ; } } protected abstract SocksProxyBase copy ( ) ; protected abstract ProxyMessage formMessage ( int cmd , InetAddress ip , int port ) ; protected abstract ProxyMessage formMessage ( int cmd , String host , int port ) throws UnknownHostException ; protected abstract ProxyMessage formMessage ( InputStream in ) throws IOException ; protected ProxyMessage connect ( InetAddress ip , int port ) throws SocksException { try { startSession ( ) ; final ProxyMessage request = formMessage ( SOCKS_CMD_CONNECT , ip , port ) ; return exchange ( request ) ; } catch ( final SocksException se ) { endSession ( ) ; throw se ; } } protected ProxyMessage connect ( String host , int port ) throws UnknownHostException , SocksException { try { startSession ( ) ; final ProxyMessage request = formMessage ( SOCKS_CMD_CONNECT , host , port ) ; return exchange ( request ) ; } catch ( final SocksException se ) { endSession ( ) ; throw se ; } } protected ProxyMessage bind ( InetAddress ip , int port ) throws SocksException { try { startSession ( ) ; final ProxyMessage request = formMessage ( SOCKS_CMD_BIND , ip , port ) ; return exchange ( request ) ; } catch ( final SocksException se ) { endSession ( ) ; throw se ; } } protected ProxyMessage bind ( String host , int port ) throws UnknownHostException , SocksException { try { startSession ( ) ; final ProxyMessage request = formMessage ( SOCKS_CMD_BIND , host , port ) ; return exchange ( request ) ; } catch ( final SocksException se ) { endSession ( ) ; throw se ; } } protected ProxyMessage accept ( ) throws IOException { ProxyMessage msg ; try { msg = formMessage ( in ) ; } catch ( final InterruptedIOException iioe ) { throw iioe ; } catch ( final IOException io_ex ) { endSession ( ) ; throw new SocksException ( SOCKS_PROXY_IO_ERROR , "While Trying accept:" + io_ex ) ; } return msg ; } protected ProxyMessage udpAssociate ( InetAddress ip , int port ) throws SocksException { try { startSession ( ) ; final ProxyMessage request = formMessage ( SOCKS_CMD_UDP_ASSOCIATE , ip , port ) ; if ( request != null ) { return exchange ( request ) ; } } catch ( final SocksException se ) { endSession ( ) ; throw se ; } endSession ( ) ; throw new SocksException ( SOCKS_METHOD_NOTSUPPORTED , "This version of proxy does not support UDP associate, use version 5" ) ; } protected ProxyMessage udpAssociate ( String host , int port ) throws UnknownHostException , SocksException { try { startSession ( ) ; final ProxyMessage request = formMessage ( SOCKS_CMD_UDP_ASSOCIATE , host , port ) ; if ( request != null ) { return exchange ( request ) ; } } catch ( final SocksException se ) { endSession ( ) ; throw se ; } endSession ( ) ; throw new SocksException ( SOCKS_METHOD_NOTSUPPORTED , "This version of proxy does not support UDP associate, use version 5" ) ; } protected void endSession ( ) { try { if ( proxySocket != null ) { proxySocket . close ( ) ; } proxySocket = null ; } catch ( final IOException io_ex ) { } } protected void sendMsg ( ProxyMessage msg ) throws IOException { msg . write ( out ) ; } protected ProxyMessage readMsg ( ) throws IOException { return formMessage ( in ) ; } protected ProxyMessage exchange ( ProxyMessage request ) throws SocksException { ProxyMessage reply ; try { request . write ( out ) ; reply = formMessage ( in ) ; } catch ( final SocksException s_ex ) { throw s_ex ; } catch ( final IOException ioe ) { throw ( new SocksException ( SOCKS_PROXY_IO_ERROR , "" + ioe ) ) ; } return reply ; } public static final int SOCKS_SUCCESS = 0 ; public static final int SOCKS_FAILURE = 1 ; public static final int SOCKS_BADCONNECT = 2 ; public static final int SOCKS_BADNETWORK = 3 ; public static final int SOCKS_HOST_UNREACHABLE = 4 ; public static final int SOCKS_CONNECTION_REFUSED = 5 ; public static final int SOCKS_TTL_EXPIRE = 6 ; public static final int SOCKS_CMD_NOT_SUPPORTED = 7 ; public static final int SOCKS_ADDR_NOT_SUPPORTED = 8 ; public static final int SOCKS_NO_PROXY = 1 << 16 ; public static final int SOCKS_PROXY_NO_CONNECT = 2 << 16 ; public static final int SOCKS_PROXY_IO_ERROR = 3 << 16 ; public static final int SOCKS_AUTH_NOT_SUPPORTED = 4 << 16 ; public static final int SOCKS_AUTH_FAILURE = 5 << 16 ; public static final int SOCKS_JUST_ERROR = 6 << 16 ; public static final int SOCKS_DIRECT_FAILED = 7 << 16 ; public static final int SOCKS_METHOD_NOTSUPPORTED = 8 << 16 ; static final int SOCKS_CMD_CONNECT = 0x1 ; static final int SOCKS_CMD_BIND = 0x2 ; static final int SOCKS_CMD_UDP_ASSOCIATE = 0x3 ; } 