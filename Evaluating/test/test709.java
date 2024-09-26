<<<<<<< HEAD
public class ConnectionPointComposite implements SelectionListener , ISelectionChangedListener , IDoubleClickListener , TransferDragSourceListener , DropTargetListener { public static interface Client { public void transfer ( ConnectionPointComposite source ) ; } private static final String [ ] COLUMN_NAMES = { Messages . ConnectionPointComposite_Column_Filename , Messages . ConnectionPointComposite_Column_Size , Messages . ConnectionPointComposite_Column_LastModified } ; private Composite fMain ; private Link fEndPointLink ; private ToolItem fRefreshItem ; private ToolItem fHomeItem ; private Link fPathLink ; private TreeViewer fTreeViewer ; private MenuItem fOpenItem ; private MenuItem fTransferItem ; private MenuItem fDeleteItem ; private MenuItem fRenameItem ; private MenuItem fRefreshMenuItem ; private MenuItem fPropertiesItem ; private String fName ; private IConnectionPoint fConnectionPoint ; private List < IAdaptable > fEndPointData ; private Client fClient ; public ConnectionPointComposite ( Composite parent , String name , Client client ) { fName = name ; fClient = client ; fEndPointData = new ArrayList < IAdaptable > ( ) ; fMain = createControl ( parent ) ; } public Control getControl ( ) { return fMain ; } public IAdaptable getCurrentInput ( ) { return ( IAdaptable ) fTreeViewer . getInput ( ) ; } public IAdaptable [ ] getSelectedElements ( ) { ISelection selection = fTreeViewer . getSelection ( ) ; if ( selection . isEmpty ( ) || ! ( selection instanceof IStructuredSelection ) ) { return new IAdaptable [ 0 ] ; } Object [ ] elements = ( ( IStructuredSelection ) selection ) . toArray ( ) ; List < IAdaptable > list = new ArrayList < IAdaptable > ( ) ; for ( Object element : elements ) { if ( element instanceof IAdaptable ) { list . add ( ( IAdaptable ) element ) ; } } return list . toArray ( new IAdaptable [ list . size ( ) ] ) ; } public void setFocus ( ) { fMain . setFocus ( ) ; } public void setConnectionPoint ( IConnectionPoint connection ) { fConnectionPoint = connection ; fEndPointData . clear ( ) ; if ( fConnectionPoint == null ) { fEndPointLink . setText ( "" ) ; } else { String label = connection . getName ( ) ; String tooltip = label ; if ( connection instanceof IBaseRemoteConnectionPoint ) { IPath path = ( ( IBaseRemoteConnectionPoint ) connection ) . getPath ( ) ; if ( path . segmentCount ( ) > 0 ) { tooltip = StringUtils . format ( "{0} ({1})" , new String [ ] { connection . getName ( ) , path . toPortableString ( ) } ) ; } } fEndPointLink . setText ( StringUtils . format ( "<a>{0}</a>" , label ) ) ; fEndPointLink . setToolTipText ( tooltip ) ; fEndPointData . add ( fConnectionPoint ) ; } setPath ( "" ) ; fMain . layout ( true , true ) ; fTreeViewer . setInput ( connection ) ; } public void addTreeFocusListener ( FocusListener listener ) { fTreeViewer . getControl ( ) . addFocusListener ( listener ) ; } public void refresh ( ) { Object input = fTreeViewer . getInput ( ) ; IResource resource = null ; if ( input instanceof IAdaptable ) { resource = ( IResource ) ( ( IAdaptable ) input ) . getAdapter ( IResource . class ) ; } if ( resource != null ) { try { resource . refreshLocal ( IResource . DEPTH_INFINITE , null ) ; } catch ( CoreException e ) { } } updateContent ( fEndPointData . get ( fEndPointData . size ( ) - 1 ) ) ; } public void widgetDefaultSelected ( SelectionEvent e ) { } public void widgetSelected ( SelectionEvent e ) { Object source = e . getSource ( ) ; if ( source == fRefreshItem ) { refresh ( ) ; } else if ( source == fHomeItem ) { gotoHome ( ) ; } else if ( source == fOpenItem ) { open ( fTreeViewer . getSelection ( ) ) ; } else if ( source == fTransferItem ) { if ( fClient != null ) { fClient . transfer ( this ) ; } } else if ( source == fDeleteItem ) { delete ( fTreeViewer . getSelection ( ) ) ; } else if ( source == fRenameItem ) { rename ( ) ; } else if ( source == fRefreshMenuItem ) { refresh ( fTreeViewer . getSelection ( ) ) ; } else if ( source == fPropertiesItem ) { openPropertyPage ( fTreeViewer . getSelection ( ) ) ; } else if ( source == fPathLink ) { updateContent ( fEndPointData . get ( Integer . parseInt ( e . text ) + 1 ) ) ; } else if ( source == fEndPointLink ) { gotoHome ( ) ; } } public void selectionChanged ( SelectionChangedEvent event ) { updateMenuStates ( ) ; } public void doubleClick ( DoubleClickEvent event ) { if ( fClient == null ) { open ( event . getSelection ( ) ) ; } else { Object object = ( ( IStructuredSelection ) event . getSelection ( ) ) . getFirstElement ( ) ; if ( object instanceof IAdaptable ) { IAdaptable adaptable = ( IAdaptable ) object ; IFileInfo fileInfo = SyncUtils . getFileInfo ( ( IAdaptable ) object ) ; if ( fileInfo . isDirectory ( ) ) { updateContent ( adaptable ) ; } else { fClient . transfer ( this ) ; } } } } public Transfer getTransfer ( ) { return LocalSelectionTransfer . getTransfer ( ) ; } public void dragFinished ( DragSourceEvent event ) { LocalSelectionTransfer . getTransfer ( ) . setSelection ( null ) ; LocalSelectionTransfer . getTransfer ( ) . setSelectionSetTime ( 0 ) ; } public void dragSetData ( DragSourceEvent event ) { event . data = fTreeViewer . getSelection ( ) ; } public void dragStart ( DragSourceEvent event ) { LocalSelectionTransfer . getTransfer ( ) . setSelection ( fTreeViewer . getSelection ( ) ) ; LocalSelectionTransfer . getTransfer ( ) . setSelectionSetTime ( event . time & 0xFFFFFFFFL ) ; } public void dragEnter ( DropTargetEvent event ) { if ( event . detail == DND . DROP_DEFAULT ) { if ( ( event . operations & DND . DROP_COPY ) == 0 ) { event . detail = DND . DROP_NONE ; } else { event . detail = DND . DROP_COPY ; } } } public void dragLeave ( DropTargetEvent event ) { } public void dragOperationChanged ( DropTargetEvent event ) { } public void dragOver ( DropTargetEvent event ) { } public void drop ( DropTargetEvent event ) { IFileStore targetStore = null ; if ( event . item == null ) { targetStore = SyncUtils . getFileStore ( ( IAdaptable ) fTreeViewer . getInput ( ) ) ; } else { TreeItem target = ( TreeItem ) event . item ; targetStore = getFolderStore ( ( IAdaptable ) target . getData ( ) ) ; } if ( targetStore == null ) { return ; } if ( event . data instanceof ITreeSelection ) { ITreeSelection selection = ( ITreeSelection ) event . data ; TreePath [ ] paths = selection . getPaths ( ) ; if ( paths . length > 0 ) { List < IAdaptable > elements = new ArrayList < IAdaptable > ( ) ; for ( TreePath path : paths ) { boolean alreadyIn = false ; for ( TreePath path2 : paths ) { if ( ! path . equals ( path2 ) && path . startsWith ( path2 , null ) ) { alreadyIn = true ; break ; } } if ( ! alreadyIn ) { elements . add ( ( IAdaptable ) path . getLastSegment ( ) ) ; } } CopyFilesOperation operation = new CopyFilesOperation ( getControl ( ) . getShell ( ) ) ; operation . copyFiles ( elements . toArray ( new IAdaptable [ elements . size ( ) ] ) , targetStore , new JobChangeAdapter ( ) { @ Override public void done ( IJobChangeEvent event ) { IOUIPlugin . refreshNavigatorView ( fTreeViewer . getInput ( ) ) ; CoreUIUtils . getDisplay ( ) . asyncExec ( new Runnable ( ) { public void run ( ) { refresh ( ) ; } } ) ; } } ) ; } } } public void dropAccept ( DropTargetEvent event ) { } protected Composite createControl ( Composite parent ) { Composite main = new Composite ( parent , SWT . NONE ) ; GridLayout layout = new GridLayout ( ) ; layout . marginHeight = 0 ; layout . marginWidth = 0 ; layout . verticalSpacing = 0 ; main . setLayout ( layout ) ; Composite top = createTopComposite ( main ) ; top . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , false ) ) ; Composite path = createPathComposite ( main ) ; path . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , false ) ) ; TreeViewer treeViewer = createTreeViewer ( main ) ; treeViewer . getControl ( ) . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , true ) ) ; return main ; } private Composite createTopComposite ( Composite parent ) { Composite main = new Composite ( parent , SWT . NONE ) ; GridLayout layout = new GridLayout ( 3 , false ) ; layout . marginHeight = 0 ; layout . marginWidth = 0 ; main . setLayout ( layout ) ; Label label = new Label ( main , SWT . NONE ) ; label . setText ( fName + ":" ) ; fEndPointLink = new Link ( main , SWT . NONE ) ; fEndPointLink . addSelectionListener ( this ) ; ToolBar toolbar = new ToolBar ( main , SWT . FLAT ) ; fHomeItem = new ToolItem ( toolbar , SWT . PUSH ) ; fHomeItem . setImage ( SyncingUIPlugin . getImage ( "icons/full/obj16/home.png" ) ) ; fHomeItem . setToolTipText ( Messages . ConnectionPointComposite_TTP_Home ) ; fHomeItem . addSelectionListener ( this ) ; return main ; } private Composite createPathComposite ( Composite parent ) { Composite main = new Composite ( parent , SWT . NONE ) ; GridLayout layout = new GridLayout ( 2 , false ) ; layout . marginHeight = 0 ; layout . marginWidth = 0 ; main . setLayout ( layout ) ; fPathLink = new Link ( main , SWT . NONE ) ; fPathLink . setLayoutData ( new GridData ( SWT . FILL , SWT . CENTER , true , false ) ) ; final Font font = new Font ( fPathLink . getDisplay ( ) , SWTUtils . boldFont ( fPathLink . getFont ( ) ) ) ; fPathLink . setFont ( font ) ; fPathLink . addDisposeListener ( new DisposeListener ( ) { public void widgetDisposed ( DisposeEvent e ) { font . dispose ( ) ; } } ) ; fPathLink . addSelectionListener ( this ) ; ToolBar toolbar = new ToolBar ( main , SWT . FLAT ) ; fRefreshItem = new ToolItem ( toolbar , SWT . PUSH ) ; fRefreshItem . setImage ( SyncingUIPlugin . getImage ( "icons/full/obj16/refresh.gif" ) ) ; fRefreshItem . setToolTipText ( Messages . ConnectionPointComposite_TTP_Refresh ) ; fRefreshItem . addSelectionListener ( this ) ; return main ; } private TreeViewer createTreeViewer ( Composite parent ) { fTreeViewer = new TreeViewer ( parent , SWT . BORDER | SWT . MULTI | SWT . FULL_SELECTION ) ; Tree tree = fTreeViewer . getTree ( ) ; tree . setHeaderVisible ( true ) ; TreeColumn column = new TreeColumn ( tree , SWT . LEFT ) ; column . setWidth ( 300 ) ; column . setText ( COLUMN_NAMES [ 0 ] ) ; column = new TreeColumn ( tree , SWT . LEFT ) ; column . setWidth ( 50 ) ; column . setText ( COLUMN_NAMES [ 1 ] ) ; column = new TreeColumn ( tree , SWT . LEFT ) ; column . setWidth ( 125 ) ; column . setText ( COLUMN_NAMES [ 2 ] ) ; fTreeViewer . setContentProvider ( new FileTreeContentProvider ( ) ) ; fTreeViewer . setLabelProvider ( new ConnectionPointLabelProvider ( ) ) ; fTreeViewer . setComparator ( new FileTreeNameSorter ( ) ) ; fTreeViewer . addSelectionChangedListener ( this ) ; fTreeViewer . addDoubleClickListener ( this ) ; fTreeViewer . addDragSupport ( DND . DROP_COPY | DND . DROP_DEFAULT , new Transfer [ ] { LocalSelectionTransfer . getTransfer ( ) } , this ) ; fTreeViewer . addDropSupport ( DND . DROP_COPY | DND . DROP_DEFAULT , new Transfer [ ] { LocalSelectionTransfer . getTransfer ( ) } , this ) ; tree . setMenu ( createMenu ( tree ) ) ; return fTreeViewer ; } private Menu createMenu ( Control parent ) { Menu menu = new Menu ( parent ) ; fOpenItem = new MenuItem ( menu , SWT . PUSH ) ; fOpenItem . setText ( CoreStrings . OPEN ) ; fOpenItem . setAccelerator ( SWT . F3 ) ; fOpenItem . addSelectionListener ( this ) ; fTransferItem = new MenuItem ( menu , SWT . PUSH ) ; fTransferItem . setText ( Messages . ConnectionPointComposite_LBL_Transfer ) ; fTransferItem . addSelectionListener ( this ) ; new MenuItem ( menu , SWT . SEPARATOR ) ; fDeleteItem = new MenuItem ( menu , SWT . PUSH ) ; fDeleteItem . setText ( CoreStrings . DELETE ) ; fDeleteItem . setImage ( PlatformUI . getWorkbench ( ) . getSharedImages ( ) . getImage ( ISharedImages . IMG_ETOOL_DELETE ) ) ; fDeleteItem . setAccelerator ( SWT . DEL ) ; fDeleteItem . addSelectionListener ( this ) ; fRenameItem = new MenuItem ( menu , SWT . PUSH ) ; fRenameItem . setText ( CoreStrings . RENAME ) ; fRenameItem . setAccelerator ( SWT . F2 ) ; fRenameItem . addSelectionListener ( this ) ; new MenuItem ( menu , SWT . SEPARATOR ) ; fRefreshMenuItem = new MenuItem ( menu , SWT . PUSH ) ; fRefreshMenuItem . setText ( CoreStrings . REFRESH ) ; fRefreshMenuItem . setImage ( SyncingUIPlugin . getImage ( "/icons/full/obj16/refresh.gif" ) ) ; fRefreshMenuItem . setAccelerator ( SWT . F5 ) ; fRefreshMenuItem . addSelectionListener ( this ) ; new MenuItem ( menu , SWT . SEPARATOR ) ; fPropertiesItem = new MenuItem ( menu , SWT . PUSH ) ; fPropertiesItem . setText ( CoreStrings . PROPERTIES ) ; fPropertiesItem . setAccelerator ( SWT . ALT | '\r' ) ; fPropertiesItem . addSelectionListener ( this ) ; return menu ; } private void gotoHome ( ) { updateContent ( fConnectionPoint ) ; } private void open ( ISelection selection ) { Object object = ( ( IStructuredSelection ) selection ) . getFirstElement ( ) ; if ( object instanceof IAdaptable ) { IAdaptable adaptable = ( IAdaptable ) object ; IFileInfo fileInfo = SyncUtils . getFileInfo ( ( IAdaptable ) object ) ; if ( fileInfo . isDirectory ( ) ) { updateContent ( adaptable ) ; } else { OpenFileAction action = new OpenFileAction ( ) ; action . updateSelection ( ( IStructuredSelection ) selection ) ; action . run ( ) ; } } } private void delete ( ISelection selection ) { final FileSystemDeleteAction action = new FileSystemDeleteAction ( getControl ( ) . getShell ( ) , fTreeViewer . getTree ( ) ) ; action . updateSelection ( ( IStructuredSelection ) selection ) ; action . addJobListener ( new JobChangeAdapter ( ) { @ Override public void done ( IJobChangeEvent event ) { CoreUIUtils . getDisplay ( ) . asyncExec ( new Runnable ( ) { public void run ( ) { refresh ( ) ; } } ) ; action . removeJobListener ( this ) ; } } ) ; action . run ( ) ; } private void rename ( ) { FileSystemRenameAction action = new FileSystemRenameAction ( getControl ( ) . getShell ( ) , fTreeViewer . getTree ( ) ) ; action . run ( ) ; refresh ( ) ; } private void refresh ( ISelection selection ) { if ( selection . isEmpty ( ) ) { refresh ( ) ; } else { Object [ ] elements = ( ( IStructuredSelection ) selection ) . toArray ( ) ; IResource resource ; for ( Object element : elements ) { resource = null ; if ( element instanceof IAdaptable ) { resource = ( IResource ) ( ( IAdaptable ) element ) . getAdapter ( IResource . class ) ; } if ( resource != null ) { try { resource . refreshLocal ( IResource . DEPTH_INFINITE , null ) ; } catch ( CoreException e ) { } } fTreeViewer . refresh ( element ) ; } } } private void openPropertyPage ( ISelection selection ) { IAdaptable element = ( IAdaptable ) ( ( IStructuredSelection ) selection ) . getFirstElement ( ) ; PreferenceDialog dialog = PreferencesUtil . createPropertyDialogOn ( getControl ( ) . getShell ( ) , element , null , null , null ) ; dialog . open ( ) ; } private void setComboData ( IAdaptable data ) { fEndPointData . clear ( ) ; if ( data instanceof IContainer ) { IContainer container = ( IContainer ) data ; IContainer root = ( IContainer ) fConnectionPoint . getAdapter ( IResource . class ) ; String path = getRelativePath ( root , container ) ; if ( path != null ) { String [ ] segments = ( new Path ( path ) ) . segments ( ) ; IContainer segmentPath = root ; for ( String segment : segments ) { segmentPath = ( IContainer ) segmentPath . findMember ( segment ) ; fEndPointData . add ( segmentPath ) ; } } } else { IFileStore fileStore = SyncUtils . getFileStore ( data ) ; if ( fileStore != null ) { IFileStore homeFileStore = SyncUtils . getFileStore ( fConnectionPoint ) ; while ( fileStore . getParent ( ) != null && ! fileStore . equals ( homeFileStore ) ) { fEndPointData . add ( 0 , fileStore ) ; fileStore = fileStore . getParent ( ) ; } } } fEndPointData . add ( 0 , fConnectionPoint ) ; } private void setPath ( String path ) { StringBuilder linkPath = new StringBuilder ( ) ; path = path . replace ( '\\' , '/' ) ; String separator = "/" ; if ( path . startsWith ( separator ) ) { path = path . substring ( 1 ) ; } String displayedPath = FileUtils . compressLeadingPath ( path , 60 ) ; if ( displayedPath . equals ( path ) ) { String [ ] folders = path . split ( separator ) ; int i ; for ( i = 0 ; i < folders . length - 1 ; ++ i ) { linkPath . append ( MessageFormat . format ( "<a href=\"{0}\">{1}</a>" , i , folders [ i ] ) ) ; linkPath . append ( separator ) ; } if ( folders . length > 0 ) { linkPath . append ( folders [ i ] ) ; } } else { linkPath . append ( "..." ) . append ( separator ) ; String endPath = displayedPath . substring ( 4 ) ; String [ ] endFolders = endPath . split ( separator ) ; int startIndex = path . split ( separator ) . length - endFolders . length ; int i ; for ( i = 0 ; i < endFolders . length - 1 ; ++ i ) { linkPath . append ( MessageFormat . format ( "<a href=\"{0}\">{1}</a>" , startIndex + i , endFolders [ i ] ) ) ; linkPath . append ( separator ) ; } if ( endFolders . length > 0 ) { linkPath . append ( endFolders [ i ] ) ; } } fPathLink . setText ( Messages . ConnectionPointComposite_LBL_Path + linkPath . toString ( ) ) ; } private void updateContent ( IAdaptable rootElement ) { setComboData ( rootElement ) ; if ( rootElement instanceof IContainer ) { setPath ( getRelativePath ( ( IContainer ) fConnectionPoint . getAdapter ( IResource . class ) , ( IContainer ) rootElement ) ) ; } else { IFileStore fileStore = SyncUtils . getFileStore ( rootElement ) ; if ( fileStore != null ) { String path = fileStore . toString ( ) ; IFileStore homeFileStore = SyncUtils . getFileStore ( fConnectionPoint ) ; if ( homeFileStore != null ) { String homePath = homeFileStore . toString ( ) ; int index = path . indexOf ( homePath ) ; if ( index > - 1 ) { path = path . substring ( index + homePath . length ( ) ) ; } } setPath ( path ) ; } } fTreeViewer . setInput ( rootElement ) ; } private void updateMenuStates ( ) { ISelection selection = fTreeViewer . getSelection ( ) ; boolean hasSelection = ! selection . isEmpty ( ) ; fOpenItem . setEnabled ( hasSelection ) ; fTransferItem . setEnabled ( hasSelection ) ; fDeleteItem . setEnabled ( hasSelection ) ; fRenameItem . setEnabled ( hasSelection ) ; fPropertiesItem . setEnabled ( hasSelection ) ; } private static IFileStore getFolderStore ( IAdaptable destination ) { IFileStore store = SyncUtils . getFileStore ( destination ) ; IFileInfo info = SyncUtils . getFileInfo ( destination ) ; if ( store != null && info != null && ! info . isDirectory ( ) ) { store = store . getParent ( ) ; } return store ; } private static String getRelativePath ( IContainer root , IContainer element ) { String rootPath = root . getFullPath ( ) . toString ( ) ; String elementPath = element . getFullPath ( ) . toString ( ) ; int index = elementPath . indexOf ( rootPath ) ; if ( index == - 1 ) { return null ; } return elementPath . substring ( index + rootPath . length ( ) ) ; } } 
=======
public class Telephony { public static final class Mms { public static final Pattern NAME_ADDR_EMAIL_PATTERN = Pattern . compile ( "\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*" ) ; public static boolean isEmailAddress ( String address ) { if ( TextUtils . isEmpty ( address ) ) { return false ; } String s = extractAddrSpec ( address ) ; Matcher match = Regex . EMAIL_ADDRESS_PATTERN . matcher ( s ) ; return match . matches ( ) ; } public static String extractAddrSpec ( String address ) { Matcher match = NAME_ADDR_EMAIL_PATTERN . matcher ( address ) ; if ( match . matches ( ) ) { return match . group ( 2 ) ; } return address ; } } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
