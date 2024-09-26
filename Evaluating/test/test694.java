public class JettyServer extends AbstractServer { private static final IProcess [ ] NO_PROCESS = new IProcess [ 0 ] ; private int port ; private String serverAddress ; private Server server ; private String boundName ; private String documentRoot ; private IPath log ; @ Override protected void installConfig ( IAbstractConfiguration configuration ) { this . port = configuration . getIntAttribute ( IServer . KEY_PORT ) ; this . documentRoot = configuration . getStringAttribute ( JettyServerTypeDelegate . KEY_SERVERID ) ; this . boundName = configuration . getStringAttribute ( IServer . KEY_ASSOCIATION_SERVER_ID ) ; String id = configuration . getStringAttribute ( IServer . KEY_ID ) ; this . log = new Path ( FileUtils . systemTempDir ) . append ( "jetty_server_" + id + ".log" ) ; super . installConfig ( configuration ) ; } public void storeConfiguration ( IAbstractConfiguration config ) { config . setIntAttribute ( IServer . KEY_PORT , port ) ; config . setStringAttribute ( JettyServerTypeDelegate . KEY_SERVERID , documentRoot ) ; config . setStringAttribute ( IServer . KEY_ASSOCIATION_SERVER_ID , boundName ) ; super . storeConfiguration ( config ) ; } public JettyServer ( IServerType type , IAbstractConfiguration configuration ) { super ( type , configuration ) ; } @ Override protected IStatus restart ( String mode , IProgressMonitor monitor ) { try { stop ( true , monitor ) ; serverChanged ( ) ; try { Thread . sleep ( 100 ) ; } catch ( InterruptedException e ) { IdeLog . log ( JettyPlugin . getDefault ( ) , IStatus . WARNING , "interrupted while sleeping" , e ) ; } start ( mode , monitor ) ; return Status . OK_STATUS ; } catch ( Exception e ) { return new Status ( IStatus . ERROR , JettyPlugin . PLUGIN_ID , IStatus . ERROR , StringUtils . format ( Messages . JettyServer_START_EXCEPTION , getName ( ) ) , e ) ; } } @ Override protected IStatus start ( String mode , IProgressMonitor monitor ) { IResource findMember = ResourcesPlugin . getWorkspace ( ) . getRoot ( ) . getContainerForLocation ( new Path ( documentRoot ) ) ; server = new Server ( port ) ; RequestLogHandler logger = new RequestLogHandler ( ) ; NCSARequestLog log = new NCSARequestLog ( this . log . toFile ( ) . getAbsolutePath ( ) ) ; log . setLogCookies ( true ) ; log . setLogLatency ( true ) ; log . setRetainDays ( 90 ) ; log . setAppend ( true ) ; log . setExtended ( true ) ; log . setLogTimeZone ( "GMT" ) ; logger . setRequestLog ( log ) ; server . addHandler ( logger ) ; server . getConnectors ( ) [ 0 ] . setHost ( HttpServer . getServerAddress ( ) ) ; try { Context context = new Context ( server , "/" , Context . SESSIONS ) ; ResourceBaseServlet servlet = new ResourceBaseServlet ( findMember . getLocation ( ) . toFile ( ) . getAbsolutePath ( ) ) ; servlet . setNoCache ( true ) ; context . addServlet ( new ServletHolder ( servlet ) , "/" ) ; if ( this . boundName . length ( ) > 0 ) { IServer server = ServerManager . getInstance ( ) . findServer ( this . boundName ) ; if ( server != null ) { JettyServerBuilder . getInstance ( ) . buildServer ( context , JettyServerTypeDelegate . ID , this . boundName , server . getHostname ( ) , server . getPort ( ) , new JettyDocumentRootResolver ( documentRoot ) ) ; } } server . setStopAtShutdown ( true ) ; server . start ( ) ; setServerState ( IServer . STATE_STARTED ) ; return Status . OK_STATUS ; } catch ( Exception e ) { return new Status ( IStatus . ERROR , JettyPlugin . PLUGIN_ID , IStatus . ERROR , StringUtils . format ( Messages . JettyServer_Status_Exception , getName ( ) ) , e ) ; } } @ Override protected IStatus stop ( boolean force , IProgressMonitor monitor ) { try { server . stop ( ) ; server . destroy ( ) ; server = null ; setServerState ( IServer . STATE_STOPPED ) ; return Status . OK_STATUS ; } catch ( Exception e ) { return new Status ( IStatus . ERROR , JettyPlugin . PLUGIN_ID , IStatus . ERROR , StringUtils . format ( Messages . JettyServer_STOP_EXCEPTION , getName ( ) ) , e ) ; } } public IStatus canHaveModule ( IModule module ) { return new Status ( IStatus . ERROR , JettyPlugin . PLUGIN_ID , IStatus . ERROR , StringUtils . format ( Messages . JettyServer_Status_Exception , getName ( ) ) , null ) ; } public String getConfigurationDescription ( ) { return StringUtils . format ( Messages . JettyServer_DESCRIPTION , new Object [ ] { port , documentRoot } ) ; } public ILog getLog ( ) { return new ILog ( ) { public URI getURI ( ) { return log . toFile ( ) . toURI ( ) ; } public boolean exists ( ) { return log . toFile ( ) . exists ( ) ; } } ; } public IProcess [ ] getProcesses ( ) { return NO_PROCESS ; } public String getHost ( ) { return getHostname ( ) + ":" + this . port ; } public boolean isWebServer ( ) { return true ; } public IServer [ ] getAssociatedServers ( ) { if ( this . boundName . length ( ) > 0 ) { IServer server = ServerCore . getServerManager ( ) . findServer ( this . boundName ) ; if ( server != null ) { return new IServer [ ] { server } ; } } return new IServer [ 0 ] ; } public String getHostname ( ) { return serverAddress == null ? "127.0.0.1" : serverAddress ; } public int getPort ( ) { return this . port ; } public IPath getDocumentRoot ( ) { return new Path ( documentRoot ) ; } public String fetchStatistics ( ) { return null ; } } 