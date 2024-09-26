<<<<<<< HEAD
public class FileTreeSelectionDialog extends ElementTreeSelectionDialog { private IFileStore selection ; private static class FileLabelProvider extends LabelProvider { private static final Image IMG_FOLDER = PlatformUI . getWorkbench ( ) . getSharedImages ( ) . getImage ( ISharedImages . IMG_OBJ_FOLDER ) ; private static final Image IMG_FILE = PlatformUI . getWorkbench ( ) . getSharedImages ( ) . getImage ( ISharedImages . IMG_OBJ_FILE ) ; private ResourceManager resourceManager = new LocalResourceManager ( JFaceResources . getResources ( ) ) ; public Image getImage ( Object element ) { IFileInfo fileInfo = FileSystemUtils . getFileInfo ( element ) ; if ( fileInfo != null ) { return fileInfo . isDirectory ( ) ? IMG_FOLDER : IMG_FILE ; } IFileStore fileStore = FileSystemUtils . getFileStore ( element ) ; if ( fileStore != null && Path . ROOT . toPortableString ( ) . equals ( fileStore . getName ( ) ) ) { return IMG_FOLDER ; } if ( element instanceof IAdaptable ) { IWorkbenchAdapter workbenchAdapter = ( IWorkbenchAdapter ) ( ( IAdaptable ) element ) . getAdapter ( IWorkbenchAdapter . class ) ; if ( workbenchAdapter != null ) { ImageDescriptor imageDescriptor = workbenchAdapter . getImageDescriptor ( element ) ; if ( imageDescriptor != null ) { return ( Image ) resourceManager . get ( imageDescriptor ) ; } } } return null ; } public String getText ( Object element ) { IFileStore fileStore = FileSystemUtils . getFileStore ( element ) ; if ( fileStore != null ) { return fileStore . getName ( ) ; } return super . getText ( element ) ; } } private static class FileContentProvider implements ITreeContentProvider { protected static final String SELECTION_EXPANDER_KEY = "selection_expander" ; private boolean allowFiles ; public FileContentProvider ( boolean allowFiles ) { super ( ) ; this . allowFiles = allowFiles ; } public Object [ ] getChildren ( Object parentElement ) { return new Object [ 0 ] ; } public Object getParent ( Object element ) { return null ; } public boolean hasChildren ( Object element ) { return false ; } public Object [ ] getElements ( Object inputElement ) { return new Object [ 1 ] ; } public void dispose ( ) { } public void inputChanged ( Viewer viewer , Object oldInput , Object newInput ) { AbstractTreeViewer treeViewer = ( AbstractTreeViewer ) viewer ; if ( treeViewer . getContentProvider ( ) != this ) { return ; } DeferredTreeContentManager deferredTreeContentManager = new DeferredTreeContentManager ( treeViewer ) { @ Override protected void addChildren ( Object parent , Object [ ] children , IProgressMonitor monitor ) { if ( ! allowFiles ) { List < Object > filtered = new ArrayList < Object > ( ) ; for ( Object i : children ) { if ( i instanceof IAdaptable ) { IFileInfo fileInfo = ( IFileInfo ) ( ( IAdaptable ) i ) . getAdapter ( IFileInfo . class ) ; if ( fileInfo != null ) { if ( fileInfo . isDirectory ( ) ) { filtered . add ( i ) ; } continue ; } } filtered . add ( i ) ; } children = filtered . toArray ( ) ; } super . addChildren ( parent , children , monitor ) ; } } ; treeViewer . setContentProvider ( new FileTreeDeferredContentProvider ( deferredTreeContentManager ) { @ Override public Object [ ] getElements ( Object element ) { if ( element instanceof IConnectionPoint ) { try { return new Object [ ] { ( ( IConnectionPoint ) element ) . getRoot ( ) } ; } catch ( CoreException e ) { IdeLog . logImportant ( IOUIPlugin . getDefault ( ) , StringUtils . EMPTY , e ) ; } } return super . getElements ( element ) ; } } ) ; treeViewer . setComparer ( new FileSystemElementComparer ( ) ) ; DeferredTreeSelectionExpander selectionExpander = new DeferredTreeSelectionExpander ( deferredTreeContentManager , treeViewer ) ; treeViewer . setData ( SELECTION_EXPANDER_KEY , selectionExpander ) ; } } public FileTreeSelectionDialog ( Shell parent , boolean allowFiles ) { super ( parent , new DecoratingLabelProvider ( new FileLabelProvider ( ) , PlatformUI . getWorkbench ( ) . getDecoratorManager ( ) . getLabelDecorator ( ) ) , new FileContentProvider ( allowFiles ) ) ; setTitle ( "Browse" ) ; setComparator ( new FileTreeNameSorter ( ) ) ; } @ Override public void setInput ( Object input ) { Assert . isLegal ( input instanceof IConnectionPoint || input instanceof IFileStore ) ; super . setInput ( input ) ; } @ Override public void setInitialSelection ( Object selection ) { if ( selection instanceof IFileStore ) { this . selection = ( IFileStore ) selection ; } } @ Override public void create ( ) { super . create ( ) ; if ( selection == null ) { return ; } BusyIndicator . showWhile ( null , new Runnable ( ) { public void run ( ) { TreeViewer treeViewer = getTreeViewer ( ) ; Object input = treeViewer . getInput ( ) ; try { IFileStore fileStore = null ; if ( input instanceof IConnectionPoint ) { fileStore = ( ( IConnectionPoint ) input ) . getRoot ( ) ; if ( ! fileStore . isParentOf ( selection ) ) { return ; } } else if ( input instanceof IFileStore ) { fileStore = ( IFileStore ) input ; if ( fileStore . equals ( selection ) || ! fileStore . isParentOf ( selection ) ) { return ; } } else { return ; } List < IFileStore > list = new ArrayList < IFileStore > ( ) ; IFileStore i = selection ; while ( i != null ) { list . add ( 0 , i ) ; if ( i . equals ( fileStore ) ) { break ; } i = i . getParent ( ) ; } TreePath treePath = new TreePath ( list . toArray ( ) ) ; DeferredTreeSelectionExpander selectionExpander = ( DeferredTreeSelectionExpander ) treeViewer . getData ( FileContentProvider . SELECTION_EXPANDER_KEY ) ; if ( selectionExpander != null ) { selectionExpander . setSelection ( treePath ) ; } } catch ( CoreException e ) { IdeLog . logImportant ( IOUIPlugin . getDefault ( ) , StringUtils . EMPTY , e ) ; } } } ) ; } } 
=======
public class SMSBarrage extends Application { private static final String TAG = "SMSBarrage" ; @ Override public void onCreate ( ) { PreferenceManager . setDefaultValues ( this , R . xml . preferences , false ) ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
