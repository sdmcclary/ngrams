<<<<<<< HEAD
public class RemoteSniffingDaemon implements ISniffingDaemon { private final IProbe probe ; private final ILogger logger ; private String messengerError ; private final BlockingQueue < IProbeMessage > sendQueue ; private final Thread sendThread ; private ICaptureFileProgress progress ; private List < InterfaceRecord > interfaceRecords ; private List < ModuleRecord > moduleRecords ; private Object lock = new Object ( ) ; private final EventListenerManager stateChangeListeners ; private boolean isRunning ; public RemoteSniffingDaemon ( IProbe probe , ILogger logger , IEventHandler changeHandler ) { this . probe = probe ; this . logger = logger ; stateChangeListeners = new EventListenerManager ( ) ; stateChangeListeners . addListener ( changeHandler ) ; sendQueue = new ArrayBlockingQueue < IProbeMessage > ( 10 ) ; sendThread = new Thread ( createSendMessageRunnable ( ) ) ; sendThread . start ( ) ; refreshInterfaceInformation ( ) ; refreshModuleInformation ( ) ; refreshStatus ( ) ; } public ICaptureFileInterface createCaptureFileInterface ( String path ) { final CaptureFileValid valid = ( CaptureFileValid ) exchangeMessage ( new CaptureFileValid ( path ) ) ; if ( valid == null ) { logger . warning ( "CaptureFileValid failed" ) ; return null ; } return new CaptureFileStub ( path , valid . isValid ( ) , valid . getErrorMessage ( ) ) ; } public void enableInterfaces ( Collection < ICaptureInterface > interfaces ) { final List < InterfaceRecord > interfaceRecords = new ArrayList < InterfaceRecord > ( ) ; for ( ICaptureInterface iface : interfaces ) { interfaceRecords . add ( new InterfaceRecord ( iface . getName ( ) , true , true ) ) ; } sendQueue . add ( new SetInterfaceEnableState ( interfaceRecords ) ) ; } public void enableModules ( Set < ISniffingModule > enabledModuleSet ) { final List < ModuleRecord > moduleRecords = new ArrayList < ModuleRecord > ( ) ; for ( ISniffingModule module : enabledModuleSet ) { moduleRecords . add ( new ModuleRecord ( module . getName ( ) , true ) ) ; } sendQueue . add ( new SetModuleEnableState ( moduleRecords ) ) ; } public Collection < ICaptureInterface > getInterfaces ( ) { synchronized ( lock ) { while ( interfaceRecords == null ) { try { lock . wait ( ) ; } catch ( InterruptedException e ) { Thread . currentThread ( ) . interrupt ( ) ; return Collections . emptyList ( ) ; } } return new ArrayList < ICaptureInterface > ( interfaceRecords ) ; } } private List < InterfaceRecord > getInterfaceRecords ( ) { final RequestInterfaceInformation response = ( RequestInterfaceInformation ) exchangeMessage ( new RequestInterfaceInformation ( ) ) ; if ( response == null ) { logger . warning ( "Failed to get capture interface information: " + getLastError ( ) ) ; return null ; } return response . getInterfaceRecords ( ) ; } public Set < ISniffingModule > getModules ( ) { synchronized ( lock ) { while ( moduleRecords == null ) { try { lock . wait ( ) ; } catch ( InterruptedException e ) { Thread . currentThread ( ) . interrupt ( ) ; return Collections . emptySet ( ) ; } } return new HashSet < ISniffingModule > ( moduleRecords ) ; } } private List < ModuleRecord > getModuleRecords ( ) { final RequestModuleInformation response = ( RequestModuleInformation ) exchangeMessage ( new RequestModuleInformation ( ) ) ; if ( response == null ) { logger . warning ( "Failed to get module information: " + getLastError ( ) ) ; return null ; } return response . getModuleRecords ( ) ; } public boolean isEnabled ( ICaptureInterface iface ) { synchronized ( lock ) { while ( interfaceRecords == null ) { try { lock . wait ( ) ; } catch ( InterruptedException e ) { Thread . currentThread ( ) . interrupt ( ) ; return false ; } } for ( InterfaceRecord record : interfaceRecords ) { if ( record . getName ( ) . equals ( iface . getName ( ) ) ) return record . isEnabled ( ) ; } logger . warning ( "Interface not found for name " + iface . getName ( ) ) ; return false ; } } public boolean isEnabled ( ISniffingModule module ) { synchronized ( lock ) { while ( moduleRecords == null ) { try { lock . wait ( ) ; } catch ( InterruptedException e ) { Thread . currentThread ( ) . interrupt ( ) ; return false ; } } for ( ModuleRecord record : moduleRecords ) { if ( record . getName ( ) . equals ( module . getName ( ) ) ) { return record . isEnabled ( ) ; } } logger . warning ( "Module not found for name " + module . getName ( ) ) ; return false ; } } public void runCaptureFile ( long spaceId , ICaptureFileInterface iface , ICaptureFileProgress progress ) { this . progress = progress ; if ( ! sendMessage ( new RunCaptureFile ( spaceId , iface . getPath ( ) ) ) ) { logger . warning ( "Error running capture file " + getLastError ( ) ) ; } } public void cancelCaptureFile ( ) { if ( ! sendMessage ( new CancelCaptureFile ( ) ) ) { logger . warning ( "Error cancelling capture file " + getLastError ( ) ) ; } } public void setEnabled ( ICaptureInterface iface , boolean enable ) { final InterfaceRecord interfaceRecord = new InterfaceRecord ( iface . getName ( ) , iface . captureAvailable ( ) , enable ) ; sendQueue . add ( new SetInterfaceEnableState ( interfaceRecord ) ) ; refreshInterfaceInformation ( ) ; } public void setEnabled ( ISniffingModule module , boolean enable ) { final ModuleRecord moduleRecord = new ModuleRecord ( module . getName ( ) , enable ) ; sendQueue . add ( new SetModuleEnableState ( moduleRecord ) ) ; refreshModuleInformation ( ) ; } public boolean isRunning ( ) { return isRunning ; } public void start ( long spaceId ) { sendQueue . add ( new StartSniffingDaemon ( spaceId ) ) ; isRunning = true ; refreshStatus ( ) ; } public void stop ( ) { sendQueue . add ( new StopSniffingDaemon ( ) ) ; isRunning = false ; refreshStatus ( ) ; } public void captureFileProgress ( CaptureFileProgress progressUpdate ) { if ( progressUpdate . isUpdate ( ) ) { progress . updateProgress ( progressUpdate . getPercent ( ) , progressUpdate . getCount ( ) ) ; } else if ( progressUpdate . isError ( ) ) { progress . error ( progressUpdate . getErrorMessage ( ) , null ) ; } else { progress . finished ( ) ; } } public void sniffingModuleOutput ( String output ) { logger . getManager ( ) . logRaw ( output ) ; } private String getLastError ( ) { return messengerError ; } private boolean sendMessage ( IProbeMessage message ) { try { probe . getMessenger ( ) . sendMessage ( message ) ; return true ; } catch ( MessengerException e ) { messengerError = e . getMessage ( ) ; return false ; } } private IProbeMessage exchangeMessage ( IProbeMessage message ) { try { IProbeMessage response = probe . getMessenger ( ) . exchangeMessage ( message ) ; if ( response instanceof StatusMessage ) { return null ; } else { return response ; } } catch ( MessengerException e ) { messengerError = e . getMessage ( ) ; return null ; } } private Runnable createSendMessageRunnable ( ) { return new Runnable ( ) { public void run ( ) { while ( ! Thread . interrupted ( ) ) { try { IProbeMessage message = sendQueue . take ( ) ; if ( ! sendMessage ( message ) ) { logger . error ( "failed to send message : " + messengerError ) ; } synchronized ( sendQueue ) { sendQueue . notifyAll ( ) ; } } catch ( InterruptedException e ) { Thread . currentThread ( ) . interrupt ( ) ; } } } } ; } private void waitForEmptySendQueue ( ) { synchronized ( sendQueue ) { while ( ! sendQueue . isEmpty ( ) ) { try { sendQueue . wait ( ) ; } catch ( InterruptedException e ) { Thread . currentThread ( ) . interrupt ( ) ; return ; } } } } private void refreshInterfaceInformation ( ) { Thread t = new Thread ( new Runnable ( ) { public void run ( ) { waitForEmptySendQueue ( ) ; synchronized ( lock ) { interfaceRecords = getInterfaceRecords ( ) ; lock . notifyAll ( ) ; } } } ) ; t . start ( ) ; } private void refreshModuleInformation ( ) { Thread t = new Thread ( new Runnable ( ) { public void run ( ) { waitForEmptySendQueue ( ) ; synchronized ( lock ) { moduleRecords = getModuleRecords ( ) ; lock . notifyAll ( ) ; } } } ) ; t . start ( ) ; } private void refreshStatus ( ) { Thread t = new Thread ( new Runnable ( ) { public void run ( ) { waitForEmptySendQueue ( ) ; SniffingDaemonStatus status = ( SniffingDaemonStatus ) exchangeMessage ( new SniffingDaemonStatus ( ) ) ; if ( status == null ) { logger . warning ( "Failed to receive status message" ) ; return ; } if ( status . isRunning != isRunning ) { isRunning = status . isRunning ; stateChangeListeners . fireEvent ( new IEvent ( ) { } ) ; } } } ) ; t . start ( ) ; } } 
=======
class Mode { static final String ANY_NAMESPACE = "##any" ; static final int ATTRIBUTE_PROCESSING_NONE = 0 ; static final int ATTRIBUTE_PROCESSING_QUALIFIED = 1 ; static final int ATTRIBUTE_PROCESSING_FULL = 2 ; static final Mode CURRENT = new Mode ( "#current" , null ) ; private final String name ; private Mode baseMode ; private boolean defined ; private Locator whereDefined ; private Locator whereUsed ; private final Hashtable elementMap = new Hashtable ( ) ; private final Hashtable attributeMap = new Hashtable ( ) ; private int attributeProcessing = - 1 ; Mode ( String name , Mode baseMode ) { this . name = name ; this . baseMode = baseMode ; } String getName ( ) { return name ; } Mode getBaseMode ( ) { return baseMode ; } void setBaseMode ( Mode baseMode ) { this . baseMode = baseMode ; } ActionSet getElementActions ( String ns ) { ActionSet actions = getElementActionsExplicit ( ns ) ; if ( actions == null ) { actions = getElementActionsExplicit ( ANY_NAMESPACE ) ; } return actions ; } private ActionSet getElementActionsExplicit ( String ns ) { ActionSet actions = ( ActionSet ) elementMap . get ( ns ) ; if ( actions == null && baseMode != null ) { actions = baseMode . getElementActionsExplicit ( ns ) ; if ( actions != null ) { actions = actions . changeCurrentMode ( this ) ; elementMap . put ( ns , actions ) ; } } return actions ; } AttributeActionSet getAttributeActions ( String ns ) { AttributeActionSet actions = getAttributeActionsExplicit ( ns ) ; if ( actions == null ) { actions = getAttributeActionsExplicit ( ANY_NAMESPACE ) ; } return actions ; } private AttributeActionSet getAttributeActionsExplicit ( String ns ) { AttributeActionSet actions = ( AttributeActionSet ) attributeMap . get ( ns ) ; if ( actions == null && baseMode != null ) { actions = baseMode . getAttributeActionsExplicit ( ns ) ; if ( actions != null ) attributeMap . put ( ns , actions ) ; } return actions ; } int getAttributeProcessing ( ) { if ( attributeProcessing == - 1 ) { if ( baseMode != null ) attributeProcessing = baseMode . getAttributeProcessing ( ) ; else attributeProcessing = ATTRIBUTE_PROCESSING_NONE ; for ( Enumeration e = attributeMap . keys ( ) ; e . hasMoreElements ( ) && attributeProcessing != ATTRIBUTE_PROCESSING_FULL ; ) { String ns = ( String ) e . nextElement ( ) ; AttributeActionSet actions = ( AttributeActionSet ) attributeMap . get ( ns ) ; if ( ! actions . getAttach ( ) || actions . getReject ( ) || actions . getSchemas ( ) . length > 0 ) attributeProcessing = ( ( ns . equals ( "" ) || ns . equals ( ANY_NAMESPACE ) ) ? ATTRIBUTE_PROCESSING_FULL : ATTRIBUTE_PROCESSING_QUALIFIED ) ; } } return attributeProcessing ; } Locator getWhereDefined ( ) { return whereDefined ; } boolean isDefined ( ) { return defined ; } Locator getWhereUsed ( ) { return whereUsed ; } void noteUsed ( Locator locator ) { if ( whereUsed == null && locator != null ) whereUsed = new LocatorImpl ( locator ) ; } void noteDefined ( Locator locator ) { defined = true ; if ( whereDefined == null && locator != null ) whereDefined = new LocatorImpl ( locator ) ; } boolean bindElement ( String ns , ActionSet actions ) { if ( elementMap . get ( ns ) != null ) return false ; elementMap . put ( ns , actions ) ; return true ; } boolean bindAttribute ( String ns , AttributeActionSet actions ) { if ( attributeMap . get ( ns ) != null ) return false ; attributeMap . put ( ns , actions ) ; return true ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
