<<<<<<< HEAD
public class JSRemoteLaunchConfigurationDelegate extends JSLaunchConfigurationDelegate { private static final Pattern HOST_PATTERN = Pattern . compile ( "^((\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(\\w+(\\.\\w+)*))(:(\\d{4,7}))?$" ) ; public void launch ( ILaunchConfiguration configuration , String mode , ILaunch launch , IProgressMonitor monitor ) throws CoreException { boolean debug = "debug" . equals ( mode ) ; String serverHost = configuration . getAttribute ( ILaunchConfigurationConstants . CONFIGURATION_SERVER_HOST , ( String ) null ) ; if ( serverHost == null || ! HOST_PATTERN . matcher ( serverHost ) . matches ( ) ) { throw new CoreException ( new Status ( IStatus . ERROR , JSDebugPlugin . ID , IStatus . OK , StringUtils . format ( "Invalid server host" , serverHost ) , null ) ) ; } int serverType = configuration . getAttribute ( ILaunchConfigurationConstants . CONFIGURATION_SERVER_TYPE , ILaunchConfigurationConstants . DEFAULT_SERVER_TYPE ) ; int startActionType = configuration . getAttribute ( ILaunchConfigurationConstants . CONFIGURATION_START_ACTION_TYPE , ILaunchConfigurationConstants . DEFAULT_START_ACTION_TYPE ) ; LocalResourceMapper resourceMapper = null ; HttpServerProcess httpServer = null ; boolean launchHttpServer = false ; URL baseURL = null ; try { if ( serverType == ILaunchConfigurationConstants . SERVER_INTERNAL ) { if ( startActionType != ILaunchConfigurationConstants . START_ACTION_START_URL ) { launchHttpServer = true ; } } else { baseURL = new URL ( configuration . getAttribute ( ILaunchConfigurationConstants . CONFIGURATION_EXTERNAL_BASE_URL , StringUtils . EMPTY ) ) ; resourceMapper = new LocalResourceMapper ( ) ; resourceMapper . addMapping ( baseURL , ResourcesPlugin . getWorkspace ( ) . getRoot ( ) . getLocation ( ) . toFile ( ) ) ; } } catch ( MalformedURLException e ) { throw new CoreException ( new Status ( IStatus . ERROR , JSDebugPlugin . ID , IStatus . OK , Messages . JSLaunchConfigurationDelegate_MalformedServerURL , e ) ) ; } try { URL launchURL = null ; try { if ( startActionType == ILaunchConfigurationConstants . START_ACTION_START_URL ) { launchURL = new URL ( configuration . getAttribute ( ILaunchConfigurationConstants . CONFIGURATION_START_PAGE_URL , StringUtils . EMPTY ) ) ; } else { IResource resource = null ; if ( startActionType == ILaunchConfigurationConstants . START_ACTION_CURRENT_PAGE ) { resource = getCurrentEditorResource ( ) ; } else if ( startActionType == ILaunchConfigurationConstants . START_ACTION_SPECIFIC_PAGE ) { String resourcePath = configuration . getAttribute ( ILaunchConfigurationConstants . CONFIGURATION_START_PAGE_PATH , ( String ) null ) ; if ( resourcePath != null && resourcePath . length ( ) > 0 ) { resource = ResourcesPlugin . getWorkspace ( ) . getRoot ( ) . findMember ( new Path ( resourcePath ) ) ; } } if ( resource != null ) { if ( baseURL == null && launchHttpServer ) { monitor . subTask ( Messages . JSLaunchConfigurationDelegate_LaunchingHTTPServer ) ; IHttpServerProviderAdapter httpServerProvider = ( IHttpServerProviderAdapter ) getContributedAdapter ( IHttpServerProviderAdapter . class ) ; IServer server = null ; if ( httpServerProvider != null ) { server = httpServerProvider . getServer ( resource ) ; } File root = resource . getProject ( ) . getLocation ( ) . toFile ( ) ; if ( server != null ) { baseURL = new URL ( server . getHost ( ) ) ; } else { httpServer = new HttpServerProcess ( launch ) ; httpServer . setServerRoot ( root ) ; baseURL = httpServer . getBaseURL ( ) ; } resourceMapper = new LocalResourceMapper ( ) ; resourceMapper . addMapping ( baseURL , root ) ; JSLaunchConfigurationHelper . setResourceMapping ( configuration , baseURL , resourceMapper , httpServer ) ; launchURL = new URL ( baseURL , resource . getProjectRelativePath ( ) . makeRelative ( ) . toPortableString ( ) ) ; } else if ( baseURL != null ) { IProject project = resource . getProject ( ) ; resourceMapper . addMapping ( baseURL , project . getLocation ( ) . toFile ( ) ) ; launchURL = new URL ( baseURL , resource . getProjectRelativePath ( ) . makeRelative ( ) . toPortableString ( ) ) ; } else { launchURL = resource . getLocation ( ) . toFile ( ) . toURI ( ) . toURL ( ) ; } } else if ( startActionType == ILaunchConfigurationConstants . START_ACTION_CURRENT_PAGE ) { IPath path = getCurrentEditorPath ( ) ; if ( path != null ) { launchURL = path . toFile ( ) . toURI ( ) . toURL ( ) ; } else { launchURL = getCurrentEditorURL ( ) ; } } } if ( launchURL == null ) { throw new CoreException ( new Status ( IStatus . ERROR , JSDebugPlugin . ID , IStatus . OK , Messages . JSLaunchConfigurationDelegate_LaunchURLNotDefined , null ) ) ; } String httpGetQuery = configuration . getAttribute ( ILaunchConfigurationConstants . CONFIGURATION_HTTP_GET_QUERY , StringUtils . EMPTY ) ; if ( httpGetQuery != null && httpGetQuery . length ( ) > 0 && launchURL . getQuery ( ) == null && launchURL . getRef ( ) == null ) { if ( httpGetQuery . charAt ( 0 ) != '?' ) { httpGetQuery = '?' + httpGetQuery ; } launchURL = new URL ( launchURL , launchURL . getFile ( ) + httpGetQuery ) ; } } catch ( MalformedURLException e ) { throw new CoreException ( new Status ( IStatus . ERROR , JSDebugPlugin . ID , IStatus . OK , Messages . JSLaunchConfigurationDelegate_MalformedLaunchURL , e ) ) ; } monitor . subTask ( Messages . JSRemoteLaunchConfigurationDelegate_ConnectingServer ) ; Socket socket = null ; try { String host = serverHost ; int port = DEFAULT_PORT ; int index = serverHost . indexOf ( ':' ) ; if ( index > 0 ) { host = serverHost . substring ( 0 , index ) ; try { port = Integer . parseInt ( serverHost . substring ( index + 1 ) ) ; } catch ( NumberFormatException e ) { port = 0 ; } } socket = new Socket ( ) ; socket . connect ( new InetSocketAddress ( host , port ) , DebugConnection . SOCKET_TIMEOUT ) ; } catch ( IOException e ) { throw new CoreException ( new Status ( IStatus . ERROR , JSDebugPlugin . ID , IStatus . OK , Messages . JSLaunchConfigurationDelegate_SocketConnectionError , e ) ) ; } monitor . subTask ( Messages . JSLaunchConfigurationDelegate_InitializingDebugger ) ; JSDebugTarget debugTarget = null ; try { JSDebugProcess debugProcess = new JSDebugProcess ( launch , Messages . JSRemoteLaunchConfigurationDelegate_Server , null ) ; DebugConnection controller = DebugConnection . createConnection ( socket ) ; debugTarget = new JSDebugTarget ( launch , debugProcess , httpServer , resourceMapper , controller , debug ) ; monitor . subTask ( StringUtils . format ( Messages . JSLaunchConfigurationDelegate_OpeningPage , launchURL ) ) ; debugTarget . openURL ( launchURL ) ; } catch ( CoreException e ) { JSDebugPlugin . log ( e ) ; if ( debugTarget != null ) { debugTarget . terminate ( ) ; } else { try { socket . close ( ) ; } catch ( IOException ignore ) { } } throw e ; } } catch ( CoreException e ) { if ( httpServer != null ) { launch . removeProcess ( httpServer ) ; try { httpServer . terminate ( ) ; } catch ( DebugException e1 ) { IdeLog . logError ( JSDebugPlugin . getDefault ( ) , StringUtils . EMPTY , e1 ) ; } } throw e ; } } } 
=======
public class SimpleColor extends AbstractDatatype { public static final SimpleColor THE_INSTANCE = new SimpleColor ( ) ; private SimpleColor ( ) { super ( ) ; } @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { if ( literal . length ( ) != 7 ) { throw newDatatypeException ( "Incorrect length for color string." ) ; } char c = literal . charAt ( 0 ) ; if ( c != '#' ) { throw newDatatypeException ( 0 , "Color starts with incorrect character " , c , ". Expected the number sign." ) ; } for ( int i = 1 ; i < 7 ; i ++ ) { c = literal . charAt ( i ) ; if ( ! ( ( c >= '0' && c <= '9' ) || ( c >= 'A' && c <= 'F' ) || ( c >= 'a' && c <= 'f' ) ) ) { throw newDatatypeException ( 0 , "" , c , " is not a valid hexadecimal digit." ) ; } } } @ Override public String getName ( ) { return "simple color" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
