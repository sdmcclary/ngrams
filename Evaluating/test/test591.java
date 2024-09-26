<<<<<<< HEAD
public class DefaultPreviewConfigurationPage extends BasePreviewConfigurationPage { private static final String TEMP_EXTENSION = "tmp" ; private Composite displayArea ; private Composite browserArea ; private ProgressListener listener ; private Listener locListener ; private Event lastEvent = null ; public DefaultPreviewConfigurationPage ( MultiPageHTMLEditor editor ) { super ( editor ) ; this . url = this . editor . getURL ( ) ; locListener = new Listener ( ) { public void handleEvent ( Event event ) { if ( lastEvent == null || event . time > lastEvent . time ) { lastEvent = event ; if ( event . data instanceof Image ) { DefaultPreviewConfigurationPage . this . editor . setTabIcon ( DefaultPreviewConfigurationPage . this , ( Image ) event . data ) ; } String addOn = event . text ; if ( addOn != null ) { DefaultPreviewConfigurationPage . this . editor . setTabTooltip ( DefaultPreviewConfigurationPage . this , url + " " + event . text ) ; } } } } ; listener = new ProgressListener ( ) { public void completed ( ProgressEvent event ) { BrowserExtensionLoader . getDecorator ( browser , locListener ) ; } public void changed ( ProgressEvent event ) { } } ; } public void createControl ( Composite parent ) { displayArea = new Composite ( parent , SWT . NONE ) ; GridLayout daLayout = new GridLayout ( 1 , false ) ; daLayout . marginHeight = 0 ; daLayout . marginWidth = 0 ; displayArea . setLayout ( daLayout ) ; displayArea . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , true ) ) ; this . browserArea = new Composite ( displayArea , SWT . NONE ) ; GridData baData = new GridData ( SWT . FILL , SWT . FILL , true , true ) ; baData . exclude = true ; GridLayout baLayout = new GridLayout ( 1 , false ) ; baLayout . marginHeight = 0 ; baLayout . marginWidth = 0 ; this . browserArea . setLayout ( baLayout ) ; this . browserArea . setLayoutData ( baData ) ; this . browserArea . setVisible ( false ) ; } public void setBrowser ( ContributedBrowser browser , String label ) { if ( this . browser != null ) { this . browser . dispose ( ) ; } this . browserLabel = label ; if ( this . browser != null ) { this . browser . removeProgressListener ( listener ) ; } this . browser = browser ; this . created = false ; if ( CoreUIUtils . onMacOSX ) { create ( ) ; } } public void showEditArea ( ) { displayArea . layout ( true , true ) ; } public void showBrowserArea ( ) { GridData baData = ( GridData ) browserArea . getLayoutData ( ) ; baData . exclude = false ; this . browserArea . setVisible ( true ) ; displayArea . layout ( true , true ) ; } private void create ( ) { if ( ! created ) { created = true ; this . browser . createControl ( browserArea ) ; this . browser . addProgressListener ( listener ) ; browserArea . layout ( true , true ) ; } } public void setURL ( String url ) { if ( this . browser != null ) { create ( ) ; String value = null ; String type = null ; IEditorInput input = this . editor . getEditorInput ( ) ; boolean isProjectFile = false ; String extension = getExtensionByURL ( url ) ; IPreviewConfiguration configuration = null ; if ( extension != null ) { configuration = PreviewConfigurations . getConfiguration ( extension ) ; } if ( configuration == null ) { configuration = new HTMLPreviewConfiguration ( ) ; } if ( input instanceof IFileEditorInput ) { isProjectFile = true ; IFile file = ( ( IFileEditorInput ) input ) . getFile ( ) ; if ( configuration . projectOverridesPreview ( file . getProject ( ) ) ) { type = configuration . getPreviewType ( file . getProject ( ) ) ; value = configuration . getPreviewName ( file . getProject ( ) ) ; } } else { type = HTMLPreviewPropertyPage . FILE_BASED_TYPE ; value = url ; } if ( type == null || value == null ) { type = configuration . getPreviewType ( ) ; value = configuration . getPreviewName ( ) ; } PreviewInfo previewInfo = ServerFileTypeHandlers . getPreviewInfoFromURL ( url ) ; String alternativeValue = null ; String serverTypeRestriction = null ; if ( previewInfo != null ) { alternativeValue = previewInfo . serverID ; serverTypeRestriction = previewInfo . serverTypeID ; } if ( configuration . isFileBasedType ( type ) ) { this . url = url ; } else if ( configuration . isServerBasedType ( type ) || configuration . isAppendedServerBasedType ( type ) ) { if ( isProjectFile ) { IServer [ ] servers = ServerCore . getServerManager ( ) . getServers ( ) ; this . url = null ; if ( alternativeValue != null && alternativeValue . length ( ) != 0 ) { for ( int i = 0 ; i < servers . length ; i ++ ) { final IServer curr = servers [ i ] ; if ( curr . getId ( ) . equals ( alternativeValue ) && curr . getServerType ( ) . getId ( ) . equals ( serverTypeRestriction ) ) { this . url = HTMLPreviewHelper . getServerURL ( curr , input , configuration . isAppendedServerBasedType ( type ) , previewInfo . pathHeader ) ; break ; } } } if ( this . url == null ) { for ( int i = 0 ; i < servers . length ; i ++ ) { final IServer curr = servers [ i ] ; if ( curr . getId ( ) . equals ( value ) ) { if ( alternativeValue != null && alternativeValue . length ( ) == 0 & curr . getServerType ( ) . getId ( ) . equals ( serverTypeRestriction ) ) { this . url = HTMLPreviewHelper . getServerURL ( curr , input , configuration . isAppendedServerBasedType ( type ) , previewInfo . pathHeader ) ; } else { this . url = HTMLPreviewHelper . getServerURL ( curr , input , configuration . isAppendedServerBasedType ( type ) ) ; } break ; } } } } } else if ( configuration . isConfigurationBasedType ( type ) ) { ILaunchManager launchManager = DebugPlugin . getDefault ( ) . getLaunchManager ( ) ; ILaunchConfigurationType launchType = launchManager . getLaunchConfigurationType ( "com.aptana.ide.debug.core.jsLaunchConfigurationType" ) ; try { ILaunchConfiguration [ ] configs = launchManager . getLaunchConfigurations ( launchType ) ; for ( int i = 0 ; i < configs . length ; i ++ ) { final ILaunchConfiguration current = configs [ i ] ; if ( current . getName ( ) . equals ( value ) ) { this . url = HTMLPreviewHelper . getConfigURL ( current , input ) ; break ; } } } catch ( CoreException e ) { } } else if ( configuration . isAbsoluteBasedType ( type ) || configuration . isAppendedAbsoluteBasedType ( type ) ) { this . url = HTMLPreviewHelper . getAbsoluteURL ( value , input , configuration . isAppendedAbsoluteBasedType ( type ) ) ; } if ( this . url == null ) { this . url = url ; } editor . setTabTooltip ( this , this . url ) ; try { this . url = URLEncoder . encode ( new URL ( this . url ) ) . toExternalForm ( ) ; } catch ( MalformedURLException e ) { IdeLog . logError ( HTMLPlugin . getDefault ( ) , Messages . DefaultPreviewConfigurationPage_ERR_Encode , e ) ; } this . browser . setURL ( this . url ) ; } } public Control getControl ( ) { return this . displayArea ; } public boolean isDefaultPage ( ) { return true ; } public boolean isDeletable ( ) { return false ; } public boolean isReadOnly ( ) { return true ; } public boolean run ( String actionID ) { return false ; } private String getExtensionByURL ( String urlStr ) { String file = urlStr ; int questionPos = urlStr . indexOf ( '?' ) ; if ( questionPos >= 0 ) { file = file . substring ( 0 , questionPos ) ; } int lastPointPos = file . lastIndexOf ( '.' ) ; if ( lastPointPos == - 1 || lastPointPos == file . length ( ) - 1 ) { return null ; } StringBuilder result = new StringBuilder ( ) ; for ( int i = lastPointPos + 1 ; i < file . length ( ) ; i ++ ) { char ch = file . charAt ( i ) ; if ( Character . isJavaIdentifierPart ( ch ) ) { result . append ( ch ) ; } else { break ; } } if ( TEMP_EXTENSION . equals ( result . toString ( ) ) ) { return getExtensionByURL ( file . substring ( 0 , lastPointPos ) ) ; } return result . toString ( ) ; } } 
=======
public final class DatatypeStreamingValidatorImpl implements DatatypeStreamingValidator { private final AbstractDatatype datatype ; private final StringBuilder buffer ; public DatatypeStreamingValidatorImpl ( AbstractDatatype datatype ) { super ( ) ; this . datatype = datatype ; this . buffer = new StringBuilder ( ) ; } public void addCharacters ( char [ ] buf , int start , int len ) { buffer . append ( buf , start , len ) ; } public boolean isValid ( ) { try { datatype . checkValid ( buffer ) ; } catch ( DatatypeException e ) { return false ; } return true ; } public void checkValid ( ) throws DatatypeException { datatype . checkValid ( buffer ) ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
