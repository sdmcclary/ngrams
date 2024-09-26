<<<<<<< HEAD
public class MultiPageCSSEditor extends MultiPageEditorPart implements ITextEditor , ITextEditorExtension , IUnifiedEditor { private CSSEditor editor ; private ToolbarWidget toolbar ; private Composite displayArea ; private SourceEditorSite _siteEditor ; protected File prevTempFile = null ; private IElementStateListener elementListener = new IElementStateListener ( ) { public void elementMoved ( Object originalElement , Object movedElement ) { } public void elementDirtyStateChanged ( Object element , boolean isDirty ) { } public void elementDeleted ( Object element ) { if ( element . equals ( getEditorInput ( ) ) ) { IWorkbenchPartSite site = MultiPageCSSEditor . this . getSite ( ) ; if ( site == null ) { return ; } IWorkbenchWindow window = site . getWorkbenchWindow ( ) ; if ( window == null ) { return ; } IWorkbenchPage page = window . getActivePage ( ) ; if ( page == null ) { return ; } page . closeEditor ( MultiPageCSSEditor . this , true ) ; } } public void elementContentReplaced ( Object element ) { } public void elementContentAboutToBeReplaced ( Object element ) { } } ; private IPropertyListener propertyListener = new IPropertyListener ( ) { public void propertyChanged ( Object source , int propId ) { if ( propId == IEditorPart . PROP_INPUT && source instanceof CSSEditor ) { IEditorInput newInput = ( ( CSSEditor ) source ) . getEditorInput ( ) ; if ( newInput != null ) { setInput ( newInput ) ; setPartName ( newInput . getName ( ) ) ; setTitleToolTip ( newInput . getToolTipText ( ) ) ; } } } } ; private Map previews ; private String url ; public MultiPageCSSEditor ( ) { super ( ) ; previews = new HashMap ( ) ; editor = new CSSEditor ( ) ; } protected IEditorSite createSite ( IEditorPart editor ) { this . _siteEditor = new SourceEditorSite ( this , editor , getEditorSite ( ) ) ; return _siteEditor ; } private void createPage0 ( ) { try { addPage ( editor , getEditorInput ( ) ) ; setPageText ( 0 , " Source " ) ; setPartName ( getEditorInput ( ) . getName ( ) ) ; this . editor . addPropertyListener ( propertyListener ) ; this . editor . getDocumentProvider ( ) . addElementStateListener ( elementListener ) ; } catch ( PartInitException e ) { ErrorDialog . openError ( getSite ( ) . getShell ( ) , "Error creating nested text editor" , null , e . getStatus ( ) ) ; } } protected Composite createPageContainer ( Composite parent ) { displayArea = new Composite ( parent , SWT . NONE ) ; GridLayout daLayout = new GridLayout ( 1 , true ) ; daLayout . marginHeight = 0 ; daLayout . marginWidth = 0 ; displayArea . setLayout ( daLayout ) ; displayArea . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , true ) ) ; IPreferenceStore store = CSSPlugin . getDefault ( ) . getPreferenceStore ( ) ; boolean show = store . getBoolean ( IPreferenceConstants . SHOW_CSS_TOOLBAR ) ; if ( show ) { toolbar = new ToolbarWidget ( new String [ ] { CSSMimeType . MimeType } , new String [ ] { CSSMimeType . MimeType } , CSSPlugin . getDefault ( ) . getPreferenceStore ( ) , IPreferenceConstants . LINK_CURSOR_WITH_CSS_TOOLBAR_TAB , this ) ; toolbar . createControl ( displayArea ) ; } Composite editorArea = new Composite ( displayArea , SWT . NONE ) ; editorArea . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , true , true ) ) ; GridLayout eaLayout = new GridLayout ( 1 , true ) ; eaLayout . marginHeight = 0 ; eaLayout . marginWidth = 0 ; editorArea . setLayout ( new FillLayout ( ) ) ; return editorArea ; } protected void createPages ( ) { getSite ( ) . setSelectionProvider ( new MultiPageSelectionProvider ( this ) ) ; if ( getContainer ( ) instanceof CTabFolder ) { final CTabFolder tabs = ( CTabFolder ) getContainer ( ) ; tabs . addListener ( SWT . Traverse , new Listener ( ) { public void handleEvent ( Event event ) { if ( tabs . getItemCount ( ) == 1 && displayArea != null && ! displayArea . isDisposed ( ) ) { Composite parent = displayArea . getParent ( ) ; if ( parent != null && parent . getParent ( ) != null ) { if ( event . keyCode == SWT . PAGE_UP ) { parent . getParent ( ) . traverse ( SWT . TRAVERSE_PAGE_PREVIOUS ) ; } else if ( event . keyCode == SWT . PAGE_DOWN ) { parent . getParent ( ) . traverse ( SWT . TRAVERSE_PAGE_NEXT ) ; } } } } } ) ; Composite toolbar = new Composite ( tabs , SWT . NONE ) ; toolbar . setLayoutData ( new GridData ( GridData . HORIZONTAL_ALIGN_END , GridData . VERTICAL_ALIGN_BEGINNING ) ) ; GridLayout layout = new GridLayout ( 1 , true ) ; final ToolBar tb = new ToolBar ( toolbar , SWT . FLAT ) ; GridData tbData = new GridData ( GridData . HORIZONTAL_ALIGN_END ) ; tabs . setTabHeight ( 21 ) ; tb . setLayoutData ( tbData ) ; layout . marginWidth = 0 ; layout . marginHeight = 0 ; toolbar . setLayout ( layout ) ; tb . setLayout ( layout ) ; tb . setLayoutData ( new GridData ( SWT . FILL , SWT . FILL , false , false ) ) ; final ToolItem configure = new ToolItem ( tb , SWT . DROP_DOWN ) ; configure . setImage ( CSSPlugin . getImage ( "icons/configure.gif" ) ) ; configure . setToolTipText ( Messages . MultiPageCSSEditor_TTP_ConfigureCSSPreview ) ; configure . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { if ( e . detail != SWT . ARROW ) { PreferenceDialog dialog = PreferencesUtil . createPreferenceDialogOn ( Display . getDefault ( ) . getActiveShell ( ) , "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" , new String [ ] { "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" } , null ) ; dialog . open ( ) ; } } } ) ; final Menu menu = new Menu ( tabs . getShell ( ) , SWT . POP_UP ) ; MenuItem editTemplate = new MenuItem ( menu , SWT . PUSH ) ; editTemplate . setText ( Messages . MultiPageCSSEditor_LBL_EditDefaultPreviewTemplate ) ; editTemplate . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { PreferenceDialog dialog = PreferencesUtil . createPreferenceDialogOn ( Display . getDefault ( ) . getActiveShell ( ) , "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" , new String [ ] { "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" } , null ) ; dialog . open ( ) ; } } ) ; MenuItem editFileSettings = new MenuItem ( menu , SWT . PUSH ) ; if ( getEditorInput ( ) instanceof IFileEditorInput ) { editFileSettings . setText ( Messages . MultiPageCSSEditor_LB_FilePreviewSettings ) ; editFileSettings . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { if ( getEditorInput ( ) instanceof IFileEditorInput ) { IFile file = ( ( IFileEditorInput ) getEditorInput ( ) ) . getFile ( ) ; PreferenceDialog dialog = PreferencesUtil . createPropertyDialogOn ( Display . getDefault ( ) . getActiveShell ( ) , file , "com.aptana.ide.editor.css.preview.cssPreviewPropertyPage" , new String [ ] { "com.aptana.ide.editor.css.preview.cssPreviewPropertyPage" } , null ) ; dialog . open ( ) ; } } } ) ; MenuItem editProjectSettings = new MenuItem ( menu , SWT . PUSH ) ; editProjectSettings . setText ( Messages . MultiPageCSSEditor_LBL_ProjectPreviewSettings ) ; editProjectSettings . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { if ( getEditorInput ( ) instanceof IFileEditorInput ) { IFile file = ( ( IFileEditorInput ) getEditorInput ( ) ) . getFile ( ) ; PreferenceDialog dialog = PreferencesUtil . createPropertyDialogOn ( Display . getDefault ( ) . getActiveShell ( ) , file . getProject ( ) , "com.aptana.ide.editor.css.preview.cssPreviewPropertyPage" , new String [ ] { "com.aptana.ide.editor.css.preview.cssPreviewPropertyPage" } , null ) ; dialog . open ( ) ; } } } ) ; } MenuItem editWorkspaceSettings = new MenuItem ( menu , SWT . PUSH ) ; editWorkspaceSettings . setText ( Messages . MultiPageCSSEditor_LBL_WorkspacePreviewSettings ) ; editWorkspaceSettings . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { PreferenceDialog dialog = PreferencesUtil . createPreferenceDialogOn ( Display . getDefault ( ) . getActiveShell ( ) , "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" , new String [ ] { "com.aptana.ide.editor.css.preferences.PreviewPreferencePage" } , null ) ; dialog . open ( ) ; } } ) ; configure . addSelectionListener ( new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { if ( e . detail == SWT . ARROW ) { Rectangle rect = configure . getBounds ( ) ; Point pt = new Point ( rect . x , rect . y + rect . height ) ; pt = tb . toDisplay ( pt ) ; menu . setLocation ( pt . x , pt . y ) ; menu . setVisible ( true ) ; } else { } } } ) ; tabs . setTopRight ( toolbar , SWT . RIGHT ) ; } createPage0 ( ) ; loadBrowsers ( ) ; } public void setPreviewPageText ( int index , String title ) { setPageText ( index , title ) ; } public void dispose ( ) { if ( editor != null ) { if ( editor . getDocumentProvider ( ) != null ) { editor . getDocumentProvider ( ) . removeElementStateListener ( elementListener ) ; } editor . removePropertyListener ( propertyListener ) ; editor . dispose ( ) ; } if ( previews != null && previews . size ( ) != 0 ) { disposePreviews ( ) ; previews = null ; } if ( this . toolbar != null ) { this . toolbar . dispose ( ) ; } editor = null ; _siteEditor = null ; super . dispose ( ) ; } public void doSave ( IProgressMonitor monitor ) { IEditorPart editor = getEditor ( 0 ) ; editor . doSave ( monitor ) ; setInput ( editor . getEditorInput ( ) ) ; setPartName ( getEditorInput ( ) . getName ( ) ) ; } public void doSaveAs ( ) { IEditorPart editor = getEditor ( 0 ) ; editor . doSaveAs ( ) ; setInput ( editor . getEditorInput ( ) ) ; setPartName ( getEditorInput ( ) . getName ( ) ) ; } public void gotoMarker ( IMarker marker ) { setActivePage ( 0 ) ; IDE . gotoMarker ( getEditor ( 0 ) , marker ) ; } public void init ( IEditorSite site , IEditorInput editorInput ) throws PartInitException { super . init ( site , editorInput ) ; ICommandService commandService = ( ICommandService ) site . getService ( ICommandService . class ) ; final Command nextMultipageEditorTabCommand = commandService . getCommand ( "com.aptana.ide.editors.nextMultipageEditorTab" ) ; final Command previousMultipageEditorTabCommand = commandService . getCommand ( "com.aptana.ide.editors.previousMultipageEditorTab" ) ; IHandlerService handlerService = ( IHandlerService ) site . getService ( IHandlerService . class ) ; handlerService . activateHandler ( nextMultipageEditorTabCommand . getId ( ) , new AbstractHandler ( ) { public Object execute ( ExecutionEvent event ) throws ExecutionException { gotoNextMultipageEditorTab ( ) ; return null ; } } ) ; handlerService . activateHandler ( previousMultipageEditorTabCommand . getId ( ) , new AbstractHandler ( ) { public Object execute ( ExecutionEvent event ) throws ExecutionException { gotoPreviousMultipageEditorTab ( ) ; return null ; } } ) ; } private void gotoNextMultipageEditorTab ( ) { Composite comp = getContainer ( ) ; if ( comp instanceof CTabFolder ) { CTabFolder tabFolder = ( CTabFolder ) comp ; int itemCount = tabFolder . getItemCount ( ) ; if ( itemCount > 1 ) { int selectionIndex = tabFolder . getSelectionIndex ( ) ; selectionIndex ++ ; if ( selectionIndex >= itemCount ) { selectionIndex = 0 ; } setActivePage ( selectionIndex ) ; } } } private void gotoPreviousMultipageEditorTab ( ) { Composite comp = getContainer ( ) ; if ( comp instanceof CTabFolder ) { CTabFolder tabFolder = ( CTabFolder ) comp ; int itemCount = tabFolder . getItemCount ( ) ; if ( itemCount > 1 ) { int selectionIndex = tabFolder . getSelectionIndex ( ) ; selectionIndex -- ; if ( selectionIndex < 0 ) { selectionIndex = itemCount - 1 ; } setActivePage ( selectionIndex ) ; } } } public boolean isSaveAsAllowed ( ) { return true ; } public void setOffset ( int offset ) { setActivePage ( 0 ) ; editor . selectAndReveal ( offset , 0 ) ; } public void setToolbarVisible ( boolean visible ) { if ( toolbar != null && visible != toolbar . isVisible ( ) ) { toolbar . setVisible ( visible ) ; displayArea . setRedraw ( false ) ; displayArea . layout ( true , true ) ; displayArea . setRedraw ( true ) ; } } protected void pageChange ( int newPageIndex ) { super . pageChange ( newPageIndex ) ; if ( newPageIndex > 0 ) { updatePreview ( ) ; setToolbarVisible ( false ) ; } else if ( newPageIndex == 0 ) { setToolbarVisible ( true ) ; } } private String getExternalPreviewUrl ( IEditorInput input ) throws CoreException { String url = null ; FileEditorInput fei = ( FileEditorInput ) input ; IFile file = fei . getFile ( ) ; IProject project = file . getProject ( ) ; url = file . getPersistentProperty ( new QualifiedName ( "" , CSSPreviewPropertyPage . CSS_PREVIEW_PATH ) ) ; if ( url == null ) { url = project . getPersistentProperty ( new QualifiedName ( "" , CSSPreviewPropertyPage . CSS_PREVIEW_PATH ) ) ; } return url ; } private void updatePreview ( ) { String url = null ; IEditorInput input = this . editor . getEditorInput ( ) ; try { if ( input instanceof FileEditorInput ) { url = getExternalPreviewUrl ( input ) ; } if ( url != null && ! "" . equals ( url ) ) { this . setBrowserURL ( url ) ; } else { boolean useTemplate = CSSPlugin . getDefault ( ) . getPreferenceStore ( ) . getBoolean ( IPreferenceConstants . CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE ) ; if ( ! useTemplate ) { url = CSSPlugin . getDefault ( ) . getPreferenceStore ( ) . getString ( IPreferenceConstants . CSSEDITOR_BROWSER_URL_PREFERENCE ) ; this . setBrowserURL ( url ) ; } else { IDocumentProvider docProvider = editor . getDocumentProvider ( ) ; String css = docProvider . getDocument ( input ) . get ( ) ; IPreferenceStore store = CSSPlugin . getDefault ( ) . getPreferenceStore ( ) ; String template = store . getString ( IPreferenceConstants . CSSEDITOR_BROWSER_TEMPLATE_PREFERENCE ) ; css = "<html>" + template + "<style>" + css + "</style></html>" ; String charset = null ; if ( input instanceof IFileEditorInput ) { charset = ( ( IFileEditorInput ) input ) . getFile ( ) . getCharset ( ) ; } else if ( docProvider instanceof TextFileDocumentProvider ) { charset = ( ( TextFileDocumentProvider ) docProvider ) . getEncoding ( input ) ; if ( charset == null ) { charset = ( ( TextFileDocumentProvider ) docProvider ) . getDefaultEncoding ( ) ; } } File tmpFile ; tmpFile = writeTemporaryPreviewFile ( editor , input , css , charset ) ; String tmpUrl = CoreUIUtils . getURI ( tmpFile , false ) ; if ( prevTempFile != null && prevTempFile . equals ( tmpFile ) ) { setBrowserURL ( tmpUrl ) ; } else { if ( prevTempFile != null ) { prevTempFile . delete ( ) ; } prevTempFile = tmpFile ; setBrowserURL ( tmpUrl ) ; } } } } catch ( Exception e ) { IdeLog . logError ( CSSPlugin . getDefault ( ) , Messages . MultiPageCSSEditor_ERR_UnableToUpdatePreview , e ) ; } } private File writeTemporaryPreviewFile ( CSSEditor editor , IEditorInput input , String html , String charset ) throws CoreException , FileNotFoundException , UnsupportedEncodingException { File tmpFile = editor . getTempFile ( ) ; if ( tmpFile . exists ( ) ) { tmpFile . delete ( ) ; } FileOutputStream out = new FileOutputStream ( tmpFile ) ; PrintWriter pw = null ; if ( charset != null ) { pw = new PrintWriter ( new OutputStreamWriter ( out , charset ) , true ) ; } else { pw = new PrintWriter ( new OutputStreamWriter ( out ) , true ) ; } pw . write ( html ) ; pw . close ( ) ; try { out . close ( ) ; } catch ( IOException e ) { } tmpFile . deleteOnExit ( ) ; FileUtils . setHidden ( tmpFile ) ; return tmpFile ; } public Object getAdapter ( Class adapter ) { if ( editor != null ) { return editor . getAdapter ( adapter ) ; } else { return null ; } } private static class SourceEditorSite extends MultiPageEditorSite { private CSSEditor _editor = null ; private IEditorSite _site ; private ArrayList _menuExtenders ; private boolean isDisposing = false ; public SourceEditorSite ( MultiPageEditorPart multiPageEditor , IEditorPart editor , IEditorSite site ) { super ( multiPageEditor , editor ) ; this . _site = site ; this . _editor = ( CSSEditor ) editor ; } public String getId ( ) { return _site . getId ( ) ; } public IEditorActionBarContributor getActionBarContributor ( ) { return _site . getActionBarContributor ( ) ; } public void registerContextMenu ( String menuId , MenuManager menuManager , ISelectionProvider selectionProvider ) { if ( _editor != null ) { if ( _menuExtenders == null ) { _menuExtenders = new ArrayList ( 1 ) ; } _menuExtenders . add ( new PopupMenuExtender ( menuId , menuManager , selectionProvider , _editor ) ) ; } } public void dispose ( ) { if ( isDisposing ) { return ; } isDisposing = true ; super . dispose ( ) ; if ( _menuExtenders != null ) { for ( int i = 0 ; i < _menuExtenders . size ( ) ; i ++ ) { ( ( PopupMenuExtender ) _menuExtenders . get ( i ) ) . dispose ( ) ; } _menuExtenders = null ; } _editor = null ; if ( _site != null && _site instanceof EditorSite ) { ( ( EditorSite ) _site ) . dispose ( ) ; } _site = null ; } public IKeyBindingService getKeyBindingService ( ) { return _site . getKeyBindingService ( ) ; } public String getPluginId ( ) { return _site . getPluginId ( ) ; } public String getRegisteredName ( ) { return _site . getRegisteredName ( ) ; } public void registerContextMenu ( MenuManager menuManager , ISelectionProvider selProvider ) { _site . registerContextMenu ( menuManager , selProvider ) ; } } private void setBrowserURL ( String url ) { String index = Integer . toString ( this . getActivePage ( ) ) ; if ( ( previews . containsKey ( index ) ) ) { ( ( PreviewConfigurationPage ) previews . get ( index ) ) . setURL ( url ) ; } this . url = url ; } public String getURL ( ) { return this . url ; } private void loadBrowsers ( ) { previews . clear ( ) ; List browserList = BrowserExtensionLoader . loadBrowsers ( ) ; for ( int j = 0 ; j < browserList . size ( ) ; j ++ ) { IConfigurationElement element = ( IConfigurationElement ) browserList . get ( j ) ; String name = BrowserExtensionLoader . getBrowserLabel ( element ) ; String outlineClass = element . getAttribute ( UnifiedEditorsPlugin . OUTLINE_ATTR ) ; try { Object obj = element . createExecutableExtension ( UnifiedEditorsPlugin . CLASS_ATTR ) ; if ( obj instanceof ContributedBrowser ) { ContributedBrowser browser = ( ContributedBrowser ) obj ; PreviewConfigurationPage page = new PreviewConfigurationPage ( this ) ; page . createControl ( getContainer ( ) ) ; page . setBrowser ( browser , name ) ; page . setTitle ( StringUtils . format ( Messages . MultiPageCSSEditor_TTL_Preview , name ) ) ; page . showBrowserArea ( ) ; int index = addPage ( page . getControl ( ) ) ; previews . put ( Integer . toString ( index ) , page ) ; page . setIndex ( index ) ; setPageText ( index , StringUtils . SPACE + page . getTitle ( ) + StringUtils . SPACE ) ; browser . getControl ( ) . addKeyListener ( new KeyAdapter ( ) { public void keyReleased ( KeyEvent e ) { ( ( CTabFolder ) getContainer ( ) ) . traverse ( SWT . TRAVERSE_TAB_NEXT ) ; } } ) ; if ( outlineClass != null ) { Object ol = element . createExecutableExtension ( UnifiedEditorsPlugin . OUTLINE_ATTR ) ; if ( ol instanceof ContributedOutline ) { ContributedOutline outline = ( ContributedOutline ) ol ; browser . setOutline ( outline ) ; outline . setBrowser ( browser ) ; editor . getOutlinePage ( ) . addOutline ( outline , name ) ; } } } } catch ( Exception e ) { IdeLog . logError ( CSSPlugin . getDefault ( ) , StringUtils . format ( Messages . MultiPageCSSEditor_ERR_UnableToCreateBrowserControl , name ) , e ) ; } catch ( Error e ) { IdeLog . logError ( CSSPlugin . getDefault ( ) , StringUtils . format ( Messages . MultiPageCSSEditor_ERR_UnableToCreateBrowserControl , name ) , e ) ; } } } public void savePreviewsPages ( ) { } private void disposePreviews ( ) { Object [ ] _previews = previews . values ( ) . toArray ( ) ; for ( int i = 0 ; i < _previews . length ; i ++ ) { ( ( PreviewConfigurationPage ) _previews [ i ] ) . dispose ( ) ; } } public void addFileServiceChangeListener ( IFileServiceChangeListener listener ) { if ( editor != null ) { editor . addFileServiceChangeListener ( listener ) ; } } public void close ( boolean save ) { if ( editor != null ) { editor . close ( save ) ; } } public IUnifiedEditorContributor getBaseContributor ( ) { if ( editor != null ) { return editor . getBaseContributor ( ) ; } return null ; } public SourceViewerConfiguration getConfiguration ( ) { if ( editor != null ) { return editor . getConfiguration ( ) ; } return null ; } public IContextAwareness getContextAwareness ( ) { if ( editor != null ) { return editor . getContextAwareness ( ) ; } return null ; } public String getDefaultFileExtension ( ) { if ( editor != null ) { return editor . getDefaultFileExtension ( ) ; } return null ; } public IDocumentProvider getDocumentProvider ( ) { if ( editor != null ) { return editor . getDocumentProvider ( ) ; } return null ; } public IEditorPart getEditor ( ) { if ( editor != null ) { return editor . getEditor ( ) ; } return null ; } public EditorFileContext getFileContext ( ) { if ( editor != null ) { return editor . getFileContext ( ) ; } return null ; } public UnifiedOutlinePage getOutlinePage ( ) { if ( editor != null ) { return editor . getOutlinePage ( ) ; } return null ; } public UnifiedQuickOutlinePage createQuickOutlinePage ( ) { return editor . createQuickOutlinePage ( ) ; } public PairMatch getPairMatch ( int offset ) { if ( editor != null ) { return editor . getPairMatch ( offset ) ; } return null ; } public String getParentDirectoryHint ( ) { if ( editor != null ) { return editor . getParentDirectoryHint ( ) ; } return null ; } public ISourceViewer getViewer ( ) { if ( editor != null ) { return editor . getViewer ( ) ; } return null ; } public void removeFileServiceChangeListener ( IFileServiceChangeListener listener ) { if ( editor != null ) { editor . removeFileServiceChangeListener ( listener ) ; } } public void selectAndReveal ( int offset , int length ) { if ( editor != null ) { editor . selectAndReveal ( offset , length ) ; } } public void setParentDirectoryHint ( String hint ) { if ( editor != null ) { editor . setParentDirectoryHint ( hint ) ; } } public void showWhitespace ( boolean state ) { if ( editor != null ) { editor . showWhitespace ( state ) ; } } public void addSaveAsListener ( ISaveAsEvent listener ) { if ( editor != null ) { editor . addSaveAsListener ( listener ) ; } } public void addSaveListener ( ISaveEvent listener ) { if ( editor != null ) { editor . addSaveListener ( listener ) ; } } public void removeSaveAsListener ( ISaveAsEvent listener ) { if ( editor != null ) { editor . removeSaveAsListener ( listener ) ; } } public void removeSaveListener ( ISaveEvent listener ) { if ( editor != null ) { editor . removeSaveListener ( listener ) ; } } public void doRevertToSaved ( ) { if ( editor != null ) { editor . doRevertToSaved ( ) ; } } public IAction getAction ( String actionId ) { if ( editor != null ) { return editor . getAction ( actionId ) ; } return null ; } public IRegion getHighlightRange ( ) { if ( editor != null ) { return editor . getHighlightRange ( ) ; } return null ; } public ISelectionProvider getSelectionProvider ( ) { if ( editor != null ) { return editor . getSelectionProvider ( ) ; } return null ; } public boolean isEditable ( ) { if ( editor != null ) { return editor . isEditable ( ) ; } return false ; } public void removeActionActivationCode ( String actionId ) { if ( editor != null ) { editor . removeActionActivationCode ( actionId ) ; } } public void resetHighlightRange ( ) { if ( editor != null ) { editor . resetHighlightRange ( ) ; } } public void setAction ( String actionID , IAction action ) { if ( editor != null ) { editor . setAction ( actionID , action ) ; } } public void setActionActivationCode ( String actionId , char activationCharacter , int activationKeyCode , int activationStateMask ) { if ( editor != null ) { editor . setActionActivationCode ( actionId , activationCharacter , activationKeyCode , activationStateMask ) ; } } public void setHighlightRange ( int offset , int length , boolean moveCursor ) { if ( editor != null ) { editor . setHighlightRange ( offset , length , moveCursor ) ; } } public void showHighlightRangeOnly ( boolean showHighlightRangeOnly ) { if ( editor != null ) { editor . showHighlightRangeOnly ( showHighlightRangeOnly ) ; } } public boolean showsHighlightRangeOnly ( ) { if ( editor != null ) { return editor . showsHighlightRangeOnly ( ) ; } return false ; } public void showPianoKeys ( boolean state ) { if ( editor != null ) { editor . showPianoKeys ( state ) ; } } public void addRulerContextMenuListener ( IMenuListener listener ) { } public boolean isEditorInputReadOnly ( ) { if ( editor == null ) { return false ; } return editor . isEditorInputReadOnly ( ) ; } public void removeRulerContextMenuListener ( IMenuListener listener ) { } public void setStatusField ( IStatusField field , String category ) { if ( editor == null ) { return ; } editor . setStatusField ( field , category ) ; } } 
=======
public class DataUriException extends IOException { private final int index ; private final String head ; private final char literal ; private final String tail ; public DataUriException ( int index , String head , char literal , String tail ) { super ( head + '“' + literal + '”' + tail ) ; this . index = index ; this . head = head ; this . literal = literal ; this . tail = tail ; } public int getIndex ( ) { return index ; } public String getHead ( ) { return head ; } public char getLiteral ( ) { return literal ; } public String getTail ( ) { return tail ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
