<<<<<<< HEAD
public abstract class BrowserView extends ViewPart { private String partId ; private Browser browser ; private String url ; public static String Browser_homeButton_tooltip = Messages . BrowserView_Home ; public static String Browser_forwardButton_tooltip = Messages . BrowserView_NavigateToNextTopic ; public static String Browser_backwardButton_tooltip = Messages . BrowserView_NavigateToPreviousTopic ; public static String Browser_invalidConfig = Messages . BrowserView_InvalidConfiguration ; protected BrowserIntroPartLocationListener urlListener = new BrowserIntroPartLocationListener ( this ) ; protected History history = new History ( ) ; protected Action backAction = new Action ( ) { { setToolTipText ( BrowserView . Browser_backwardButton_tooltip ) ; setImageDescriptor ( BrowserView . getImageDescriptor ( "icons/backward_nav_on.gif" ) ) ; setDisabledImageDescriptor ( BrowserView . getImageDescriptor ( "icons/backward_nav_off.gif" ) ) ; } public void run ( ) { navigateBackward ( ) ; } } ; protected Action forwardAction = new Action ( ) { { setToolTipText ( BrowserView . Browser_forwardButton_tooltip ) ; setImageDescriptor ( BrowserView . getImageDescriptor ( "icons/forward_nav_on.gif" ) ) ; setDisabledImageDescriptor ( BrowserView . getImageDescriptor ( "icons/forward_nav_off.gif" ) ) ; } public void run ( ) { navigateForward ( ) ; } } ; public abstract String getStartUrl ( ) ; protected Action homeAction = new Action ( ) { { setToolTipText ( BrowserView . Browser_homeButton_tooltip ) ; setImageDescriptor ( BrowserView . getImageDescriptor ( "icons/home_nav_on.gif" ) ) ; setDisabledImageDescriptor ( BrowserView . getImageDescriptor ( "icons/home_nav_off.gif" ) ) ; } public void run ( ) { navigateHome ( ) ; } } ; public BrowserView ( ) { url = getStartUrl ( ) ; partId = getPartId ( ) ; } public abstract String getPartId ( ) ; public void init ( IViewSite site , IMemento memento ) throws PartInitException { super . init ( site , memento ) ; if ( memento != null ) { url = memento . getString ( partId ) ; sendMessage ( url ) ; } } public void saveState ( IMemento memento ) { if ( url != null ) { memento . putString ( partId , url ) ; } } public void createPartControl ( Composite parent ) { browser = new Browser ( parent , SWT . NONE ) ; if ( this . url != null ) { browser . setUrl ( this . url ) ; } PlatformUI . getWorkbench ( ) . getHelpSystem ( ) . setHelp ( browser , "com.aptana.ide.js.ui." + partId ) ; browser . addLocationListener ( urlListener ) ; browser . addProgressListener ( new ProgressListener ( ) { public void changed ( ProgressEvent event ) { } public void completed ( ProgressEvent event ) { urlListener . flagEndOfNavigation ( ) ; urlListener . flagEndOfFrameNavigation ( ) ; urlListener . flagRemovedTempUrl ( ) ; updateNavigationActionsState ( ) ; } } ) ; addToolBarActions ( ) ; } protected void addToolBarActions ( ) { IActionBars actionBars = getViewSite ( ) . getActionBars ( ) ; IToolBarManager toolBarManager = actionBars . getToolBarManager ( ) ; actionBars . setGlobalActionHandler ( ActionFactory . FORWARD . getId ( ) , forwardAction ) ; actionBars . setGlobalActionHandler ( ActionFactory . BACK . getId ( ) , backAction ) ; toolBarManager . add ( homeAction ) ; toolBarManager . add ( backAction ) ; toolBarManager . add ( forwardAction ) ; toolBarManager . update ( true ) ; actionBars . updateActionBars ( ) ; updateNavigationActionsState ( ) ; } public Browser getBrowser ( ) { return browser ; } public void setFocus ( ) { browser . setFocus ( ) ; } public void sendMessage ( String url ) { if ( url != null ) { this . url = url ; if ( browser != null ) { browser . setUrl ( url ) ; } } } protected void updateNavigationActionsState ( ) { forwardAction . setEnabled ( browser . isForwardEnabled ( ) ) ; backAction . setEnabled ( browser . isBackEnabled ( ) ) ; } public boolean navigateBackward ( ) { return browser . back ( ) ; } public boolean navigateForward ( ) { return browser . forward ( ) ; } public boolean navigateHome ( ) { String location = url ; boolean success = browser . setUrl ( location ) ; updateHistory ( location ) ; return success ; } public void updateHistory ( String location ) { history . updateHistory ( location ) ; updateNavigationActionsState ( ) ; } private static ImageDescriptor getImageDescriptor ( String imageFilePath ) { ImageDescriptor imageDescriptor = AbstractUIPlugin . imageDescriptorFromPlugin ( "org.eclipse.eclipsemonkey.ui" , imageFilePath ) ; if ( imageDescriptor == null ) { imageDescriptor = ImageDescriptor . getMissingImageDescriptor ( ) ; } return imageDescriptor ; } private class BrowserIntroPartLocationListener implements LocationListener { private BrowserView implementation ; public BrowserIntroPartLocationListener ( BrowserView implementation ) { this . implementation = implementation ; } public void changed ( LocationEvent event ) { String url = event . location ; if ( url == null ) { return ; } Browser browser = ( Browser ) event . getSource ( ) ; if ( browser . getData ( "navigation" ) != null && browser . getData ( "navigation" ) . equals ( "true" ) ) { return ; } } public void changing ( LocationEvent event ) { String url = event . location ; if ( url == null ) { return ; } } public void flagStartOfFrameNavigation ( ) { if ( implementation . getBrowser ( ) . getData ( "frameNavigation" ) == null ) { implementation . getBrowser ( ) . setData ( "frameNavigation" , "true" ) ; } } public void flagEndOfFrameNavigation ( ) { implementation . getBrowser ( ) . setData ( "frameNavigation" , null ) ; } public void flagStartOfNavigation ( ) { if ( implementation . getBrowser ( ) . getData ( "navigation" ) == null ) { implementation . getBrowser ( ) . setData ( "navigation" , "true" ) ; } } public void flagEndOfNavigation ( ) { implementation . getBrowser ( ) . setData ( "navigation" , null ) ; } public void flagStoredTempUrl ( ) { if ( implementation . getBrowser ( ) . getData ( "tempUrl" ) == null ) { implementation . getBrowser ( ) . setData ( "tempUrl" , "true" ) ; } } public void flagRemovedTempUrl ( ) { implementation . getBrowser ( ) . setData ( "tempUrl" , null ) ; } } private class History { private Vector history = new Vector ( ) ; private int navigationLocation = 0 ; class HistoryObject { String iframeUrl ; String url ; HistoryObject ( Object location ) { if ( location instanceof String ) { this . url = ( String ) location ; } } String getIFrameUrl ( ) { return iframeUrl ; } String getUrl ( ) { return url ; } boolean isURL ( ) { return ( url != null ) ? true : false ; } boolean isIFramePage ( ) { return ( iframeUrl != null ) ? true : false ; } } public void updateHistory ( String location ) { if ( ! history . isEmpty ( ) && isSameLocation ( location ) ) { return ; } doUpdateHistory ( location ) ; } private void doUpdateHistory ( Object location ) { if ( navigationLocation == getHistoryEndPosition ( ) ) { pushToHistory ( location ) ; } else { trimHistory ( location ) ; } } private boolean isSameLocation ( Object location ) { HistoryObject currentLocation = getCurrentLocation ( ) ; if ( location instanceof String && currentLocation . isURL ( ) ) { return currentLocation . getUrl ( ) . equals ( location ) ; } return false ; } private void pushToHistory ( Object location ) { history . add ( new HistoryObject ( location ) ) ; navigationLocation = getHistoryEndPosition ( ) ; } public void removeLastHistory ( ) { history . remove ( getHistoryEndPosition ( ) ) ; navigationLocation = getHistoryEndPosition ( ) ; } private void trimHistory ( Object location ) { List newHistory = history . subList ( 0 , navigationLocation + 1 ) ; history = new Vector ( newHistory ) ; history . add ( new HistoryObject ( location ) ) ; navigationLocation = getHistoryEndPosition ( ) ; } private int getHistoryEndPosition ( ) { if ( history . isEmpty ( ) ) { return 0 ; } return history . size ( ) - 1 ; } public void navigateHistoryBackward ( ) { if ( badNavigationLocation ( navigationLocation - 1 ) ) { return ; } -- navigationLocation ; } public void navigateHistoryForward ( ) { if ( badNavigationLocation ( navigationLocation + 1 ) ) { return ; } ++ navigationLocation ; } private boolean badNavigationLocation ( int navigationLocation ) { if ( navigationLocation < 0 || navigationLocation >= history . size ( ) ) { return true ; } return false ; } private HistoryObject getCurrentLocation ( ) { return ( HistoryObject ) history . elementAt ( navigationLocation ) ; } public boolean canNavigateForward ( ) { return navigationLocation != getHistoryEndPosition ( ) ? true : false ; } public boolean canNavigateBackward ( ) { return navigationLocation == 0 ? false : true ; } public boolean currentLocationIsUrl ( ) { return getCurrentLocation ( ) . isURL ( ) ; } public String getCurrentLocationAsUrl ( ) { return getCurrentLocation ( ) . getUrl ( ) ; } public void clear ( ) { history . clear ( ) ; navigationLocation = 0 ; } } } 
=======
public class Compose extends Activity { private static final String TAG = "ComposeBarrage" ; private static final int MENU_START_BARRAGE = 0 ; private static final int MENU_SAVE_DRAFT = 1 ; private static final int MENU_SAVE_AS_TEMPLATE = 2 ; private static final int MENU_CANCEL = 3 ; private long mID ; private RecipientsEditor mRecipientEditor ; private EditText mContents ; private Button mSend ; private Button mSave ; @ Override protected void onCreate ( Bundle savedInstanceState ) { super . onCreate ( savedInstanceState ) ; setContentView ( R . layout . compose ) ; mRecipientEditor = ( RecipientsEditor ) findViewById ( R . id . recipients_editor ) ; mContents = ( EditText ) findViewById ( R . id . message ) ; mSend = ( Button ) findViewById ( R . id . send ) ; mSave = ( Button ) findViewById ( R . id . save ) ; mRecipientEditor . setAdapter ( new RecipientsAdapter ( this ) ) ; } @ Override protected void onStart ( ) { super . onStart ( ) ; } @ Override public boolean onPrepareOptionsMenu ( Menu menu ) { menu . clear ( ) ; if ( true ) { menu . add ( 0 , MENU_START_BARRAGE , 0 , R . string . menu_start_barrage ) ; menu . add ( 0 , MENU_SAVE_DRAFT , 0 , R . string . menu_save_draft ) ; menu . add ( 0 , MENU_SAVE_AS_TEMPLATE , 0 , R . string . menu_save_as_template ) ; } menu . add ( 0 , MENU_CANCEL , 0 , R . string . menu_cancel ) ; return true ; } @ Override public boolean onOptionsItemSelected ( MenuItem item ) { switch ( item . getItemId ( ) ) { case MENU_START_BARRAGE : startBarrage ( ) ; return true ; case MENU_SAVE_DRAFT : draftBarrage ( ) ; return true ; case MENU_SAVE_AS_TEMPLATE : saveAsTemplate ( ) ; return true ; case MENU_CANCEL : this . finish ( ) ; return true ; } return false ; } private void startBarrage ( ) { } private void draftBarrage ( ) { } private void saveAsTemplate ( ) { } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
