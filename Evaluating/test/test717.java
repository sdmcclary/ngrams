public class LocalConnectionPropertyDialog extends TitleAreaDialog implements IPropertyDialog { private static final String DEFAULT_NAME = "New Local Shortcut" ; private LocalConnectionPoint localConnectionPoint ; private boolean isNew = false ; private Text nameText ; private Text localPathText ; private Button browseButton ; private Image titleImage ; private ModifyListener modifyListener ; public LocalConnectionPropertyDialog ( Shell parentShell ) { super ( parentShell ) ; } public void setPropertySource ( Object element ) { localConnectionPoint = null ; if ( element instanceof LocalConnectionPoint ) { localConnectionPoint = ( LocalConnectionPoint ) element ; } } public Object getPropertySource ( ) { return localConnectionPoint ; } private String getConnectionPointType ( ) { return LocalConnectionPoint . TYPE ; } @ Override protected Control createDialogArea ( Composite parent ) { Composite dialogArea = ( Composite ) super . createDialogArea ( parent ) ; titleImage = IOUIPlugin . getImageDescriptor ( "/icons/full/wizban/local.png" ) . createImage ( ) ; dialogArea . addDisposeListener ( new DisposeListener ( ) { public void widgetDisposed ( DisposeEvent e ) { if ( titleImage != null ) { setTitleImage ( null ) ; titleImage . dispose ( ) ; titleImage = null ; } } } ) ; setTitleImage ( titleImage ) ; if ( localConnectionPoint != null ) { setTitle ( "Edit the Local Shortcut" ) ; getShell ( ) . setText ( "Edit Local Shortcut" ) ; } else { setTitle ( "Create a Local Shortcut" ) ; getShell ( ) . setText ( "New Local Shortcut" ) ; } Composite container = new Composite ( dialogArea , SWT . NONE ) ; container . setLayoutData ( GridDataFactory . fillDefaults ( ) . grab ( true , true ) . create ( ) ) ; container . setLayout ( GridLayoutFactory . swtDefaults ( ) . margins ( convertHorizontalDLUsToPixels ( IDialogConstants . HORIZONTAL_MARGIN ) , convertVerticalDLUsToPixels ( IDialogConstants . VERTICAL_MARGIN ) ) . spacing ( convertHorizontalDLUsToPixels ( IDialogConstants . HORIZONTAL_SPACING ) , convertVerticalDLUsToPixels ( IDialogConstants . VERTICAL_SPACING ) ) . numColumns ( 3 ) . create ( ) ) ; Label label = new Label ( container , SWT . NONE ) ; label . setLayoutData ( GridDataFactory . swtDefaults ( ) . hint ( new PixelConverter ( label ) . convertHorizontalDLUsToPixels ( IDialogConstants . LABEL_WIDTH ) , SWT . DEFAULT ) . create ( ) ) ; label . setText ( StringUtils . makeFormLabel ( "Shortcut Name" ) ) ; nameText = new Text ( container , SWT . SINGLE | SWT . BORDER ) ; nameText . setLayoutData ( GridDataFactory . fillDefaults ( ) . hint ( convertHorizontalDLUsToPixels ( IDialogConstants . ENTRY_FIELD_WIDTH ) , SWT . DEFAULT ) . span ( 2 , 1 ) . grab ( true , false ) . create ( ) ) ; label = new Label ( container , SWT . NONE ) ; label . setLayoutData ( GridDataFactory . swtDefaults ( ) . hint ( new PixelConverter ( label ) . convertHorizontalDLUsToPixels ( IDialogConstants . LABEL_WIDTH ) , SWT . DEFAULT ) . create ( ) ) ; label . setText ( StringUtils . makeFormLabel ( "Local Path" ) ) ; localPathText = new Text ( container , SWT . SINGLE | SWT . BORDER ) ; localPathText . setLayoutData ( GridDataFactory . swtDefaults ( ) . hint ( convertHorizontalDLUsToPixels ( IDialogConstants . ENTRY_FIELD_WIDTH ) , SWT . DEFAULT ) . grab ( true , false ) . create ( ) ) ; browseButton = new Button ( container , SWT . PUSH ) ; browseButton . setText ( '&' + StringUtils . ellipsify ( CoreStrings . BROWSE ) ) ; browseButton . setLayoutData ( GridDataFactory . fillDefaults ( ) . hint ( Math . max ( new PixelConverter ( browseButton ) . convertHorizontalDLUsToPixels ( IDialogConstants . BUTTON_WIDTH ) , browseButton . computeSize ( SWT . DEFAULT , SWT . DEFAULT , true ) . x ) , SWT . DEFAULT ) . create ( ) ) ; addListeners ( ) ; browseButton . addSelectionListener ( new SelectionAdapter ( ) { @ Override public void widgetSelected ( SelectionEvent e ) { browseFileSystem ( ) ; } } ) ; if ( localConnectionPoint == null ) { try { localConnectionPoint = ( LocalConnectionPoint ) CoreIOPlugin . getConnectionPointManager ( ) . createConnectionPoint ( getConnectionPointType ( ) ) ; localConnectionPoint . setName ( DEFAULT_NAME ) ; isNew = true ; } catch ( CoreException e ) { IdeLog . logError ( IOUIPlugin . getDefault ( ) , "Create new connection failed" , e ) ; close ( ) ; } } loadPropertiesFrom ( localConnectionPoint ) ; return dialogArea ; } protected void addListeners ( ) { if ( modifyListener == null ) { modifyListener = new ModifyListener ( ) { public void modifyText ( ModifyEvent e ) { validate ( ) ; } } ; } nameText . addModifyListener ( modifyListener ) ; localPathText . addModifyListener ( modifyListener ) ; } protected void removeListeners ( ) { if ( modifyListener != null ) { nameText . removeModifyListener ( modifyListener ) ; localPathText . removeModifyListener ( modifyListener ) ; } } @ Override protected void okPressed ( ) { if ( ! isValid ( ) ) { return ; } if ( savePropertiesTo ( localConnectionPoint ) ) { } if ( isNew ) { CoreIOPlugin . getConnectionPointManager ( ) . addConnectionPoint ( localConnectionPoint ) ; } super . okPressed ( ) ; } @ Override protected Control createContents ( Composite parent ) { try { return super . createContents ( parent ) ; } finally { validate ( ) ; } } protected void loadPropertiesFrom ( LocalConnectionPoint connectionPoint ) { removeListeners ( ) ; try { nameText . setText ( valueOrEmpty ( connectionPoint . getName ( ) ) ) ; IPath path = connectionPoint . getPath ( ) ; localPathText . setText ( path != null ? path . toPortableString ( ) : StringUtils . EMPTY ) ; } finally { addListeners ( ) ; } } protected boolean savePropertiesTo ( LocalConnectionPoint connectionPoint ) { boolean updated = false ; String name = nameText . getText ( ) ; if ( ! name . equals ( connectionPoint . getName ( ) ) ) { connectionPoint . setName ( name ) ; updated = true ; } IPath path = Path . fromPortableString ( localPathText . getText ( ) ) ; if ( ! path . equals ( connectionPoint . getPath ( ) ) ) { connectionPoint . setPath ( path ) ; updated = true ; } return updated ; } private void browseFileSystem ( ) { DirectoryDialog dlg = new DirectoryDialog ( getShell ( ) ) ; dlg . setFilterPath ( localPathText . getText ( ) ) ; String path = dlg . open ( ) ; if ( path != null ) { localPathText . setText ( Path . fromOSString ( path ) . toPortableString ( ) ) ; if ( DEFAULT_NAME . equals ( nameText . getText ( ) ) ) { nameText . setText ( Path . fromOSString ( path ) . lastSegment ( ) ) ; } } } public void validate ( ) { boolean valid = isValid ( ) ; getButton ( OK ) . setEnabled ( valid ) ; } public boolean isValid ( ) { String message = null ; if ( nameText . getText ( ) . length ( ) == 0 ) { message = "Please specify shortcut name" ; } else { File file = Path . fromPortableString ( localPathText . getText ( ) ) . toFile ( ) ; if ( ! file . exists ( ) || ! file . isDirectory ( ) ) { message = "The location doesn't exist" ; } } if ( message != null ) { setErrorMessage ( message ) ; } else { setErrorMessage ( null ) ; setMessage ( null ) ; return true ; } return false ; } protected static String valueOrEmpty ( String value ) { if ( value != null ) { return value ; } return StringUtils . EMPTY ; } } 