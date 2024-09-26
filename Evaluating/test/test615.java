public class GenericTextEditor extends EditorPart implements ITextEditor , ITextEditorExtension , IUnifiedEditor { public static final String ID = "com.aptana.ide.editor.text" ; public static final String PLAIN_MIME_TYPE = "text/plain" ; private UnifiedEditor editor ; private BaseContributor contributor ; private IFileServiceFactory fileService ; private BaseDocumentProvider documentProvider ; private IParser parser ; private File grammarFile ; private Job grammarFileMonitor ; private String language ; private String extension ; private Shell shell ; private class GenericDocumentProvider extends BaseDocumentProvider { protected class GenericFileInfo extends FileInfo { public IFileSourceProvider sourceProvider ; } public GenericDocumentProvider ( ) { super ( ) ; } public FileInfo getFileInfoPublic ( Object element ) { return getFileInfo ( element ) ; } protected FileInfo createEmptyFileInfo ( ) { return new GenericFileInfo ( ) ; } public IAnnotationModel getAnnotationModel ( Object element ) { IAnnotationModel annotationModel = super . getAnnotationModel ( element ) ; if ( annotationModel == null ) { FileInfo fileInfo = getFileInfo ( element ) ; if ( fileInfo != null ) { annotationModel = fileInfo . fModel ; } } return annotationModel ; } protected FileInfo createFileInfo ( Object element ) throws CoreException { if ( ! ( element instanceof IEditorInput ) ) { return null ; } try { FileInfo info = super . createFileInfo ( element ) ; if ( ! ( info instanceof GenericFileInfo ) ) { return null ; } GenericFileInfo cuInfo = ( GenericFileInfo ) info ; if ( element instanceof IAdaptable ) { if ( cuInfo . fTextFileBuffer . getAnnotationModel ( ) == null ) { IUniformResource uniformResource = ( IUniformResource ) ( ( IAdaptable ) element ) . getAdapter ( IUniformResource . class ) ; if ( uniformResource != null ) { } else { cuInfo . fModel = new AnnotationModel ( ) ; } } else { cuInfo . fModel = cuInfo . fTextFileBuffer . getAnnotationModel ( ) ; } } return info ; } catch ( RuntimeException ex ) { IdeLog . logError ( TextPlugin . getDefault ( ) , Messages . GenericTextEditor_ERROR , ex ) ; } return null ; } public void disconnect ( Object element ) { GenericFileInfo cuInfo = ( GenericFileInfo ) getFileInfo ( element ) ; if ( cuInfo != null && cuInfo . fCount == 1 ) { IFileSourceProvider sourceProvider = cuInfo . sourceProvider ; if ( sourceProvider != null ) { String uri = sourceProvider . getSourceURI ( ) ; if ( uri != null ) { FileContextManager . disconnectSourceProvider ( uri , cuInfo . sourceProvider ) ; } } } try { super . disconnect ( element ) ; } catch ( RuntimeException ex ) { IdeLog . logError ( TextPlugin . getDefault ( ) , Messages . GenericTextEditor_ERROR , ex ) ; } } public void connect ( Object element ) throws CoreException { super . connect ( element ) ; } } public void doSave ( IProgressMonitor monitor ) { if ( editor != null ) { editor . doSave ( monitor ) ; } } public void doSaveAs ( ) { if ( editor != null ) { editor . doSaveAs ( ) ; } } public IWorkbenchPartSite getSite ( ) { return editor != null ? editor . getSite ( ) : null ; } public IEditorInput getEditorInput ( ) { return editor != null ? editor . getEditorInput ( ) : null ; } public String getPartName ( ) { return editor != null ? editor . getPartName ( ) : "" ; } public IEditorSite getEditorSite ( ) { return editor != null ? editor . getEditorSite ( ) : null ; } public void init ( IEditorSite site , IEditorInput input ) throws PartInitException { String fileName = input . getName ( ) ; String ext = FileUtils . getExtension ( fileName ) ; if ( ext == null || ext . length ( ) == 0 ) { if ( input instanceof NonExistingFileEditorInput ) { IPath path = ( ( NonExistingFileEditorInput ) input ) . getPath ( input ) ; if ( path != null ) { fileName = path . lastSegment ( ) ; ext = FileUtils . getExtension ( fileName ) ; } } } IPreferenceStore store = TextPlugin . getDefault ( ) . getPreferenceStore ( ) ; IEditorRegistry registry = EclipseUIUtils . getWorkbenchEditorRegistry ( ) ; IFileEditorMapping [ ] mappings = registry . getFileEditorMappings ( ) ; IFileEditorMapping candidate = null ; for ( int i = 0 ; i < mappings . length ; i ++ ) { if ( mappings [ i ] . getLabel ( ) . equals ( fileName ) ) { candidate = mappings [ i ] ; break ; } else if ( mappings [ i ] . getExtension ( ) . equals ( ext ) ) { candidate = mappings [ i ] ; } } if ( candidate != null ) { String label = candidate . getLabel ( ) ; String grammarFilePath = store . getString ( TextPlugin . getGrammarPreference ( label ) ) ; String colorizerPath = TextPlugin . getColorizerPreference ( label ) ; extension = ext ; TokenList tokenList = LanguageRegistry . getTokenListByExtension ( extension ) ; if ( tokenList != null ) { language = tokenList . getLanguage ( ) ; try { if ( LanguageRegistry . hasParser ( language ) ) { parser = LanguageRegistry . getParser ( language ) ; } else { parser = new UnifiedParser ( language ) ; } } catch ( ParserInitializationException e ) { throw new PartInitException ( e . getMessage ( ) ) ; } } else { grammarFile = new File ( grammarFilePath ) ; } FileInputStream stream = null ; try { if ( grammarFile != null && grammarFile . exists ( ) && grammarFile . isFile ( ) && grammarFile . canRead ( ) ) { if ( language == null ) { AttributeSniffer sniffer = new AttributeSniffer ( "lexer" , "language" ) ; sniffer . read ( grammarFilePath ) ; if ( sniffer . getMatchedValue ( ) != null ) { language = sniffer . getMatchedValue ( ) ; } else { throw new PartInitException ( Messages . GenericTextEditor_No_Language_Defined ) ; } } if ( parser == null ) { parser = createParser ( ) ; } } if ( language != null ) { ColorizerReader reader = new ColorizerReader ( ) ; if ( LanguageRegistry . hasLanguageColorizer ( language ) ) { LanguageRegistry . getLanguageColorizer ( language ) ; } else { reader . loadColorization ( colorizerPath , true ) ; } } else { language = PLAIN_MIME_TYPE ; final TokenList list = new TokenList ( language ) ; parser = new UnifiedParser ( language ) { public void addLexerGrammar ( ILexerBuilder builder ) throws LexerException { builder . addTokenList ( list ) ; } } ; } if ( parser != null ) { createFileServiceFactory ( ) ; } contributor = createContributor ( ) ; documentProvider = new GenericDocumentProvider ( ) ; createEditor ( ) ; editor . init ( site , input ) ; if ( grammarFile != null && grammarFile . exists ( ) ) { createGrammarFileMonitor ( ) ; } } catch ( ParserInitializationException e ) { e . printStackTrace ( ) ; throw new PartInitException ( e . getMessage ( ) ) ; } catch ( FileNotFoundException e ) { throw new PartInitException ( e . getMessage ( ) ) ; } catch ( IOException e ) { throw new PartInitException ( e . getMessage ( ) ) ; } catch ( ParserConfigurationException e ) { throw new PartInitException ( e . getMessage ( ) ) ; } catch ( SAXException e ) { throw new PartInitException ( e . getMessage ( ) ) ; } finally { if ( stream != null ) { try { stream . close ( ) ; } catch ( IOException e ) { e . printStackTrace ( ) ; } } } } else { throw new PartInitException ( Messages . GenericTextEditor_ERROR_RETRIEVING_ASSOCIATION ) ; } } private void createGrammarFileMonitor ( ) { grammarFileMonitor = new Job ( Messages . GenericTextEditor_MONITOR_GRAMMAR_FILE ) { private long stamp = grammarFile . lastModified ( ) ; protected IStatus run ( IProgressMonitor monitor ) { if ( stamp < grammarFile . lastModified ( ) ) { stamp = grammarFile . lastModified ( ) ; TokenList oldList = LanguageRegistry . getTokenList ( language ) ; if ( oldList != null ) { LanguageRegistry . unregisterTokenList ( oldList ) ; } try { IParser newParser = createParser ( ) ; FileService context = FileContextManager . get ( GenericTextEditor . this . getEditorInput ( ) ) ; parser = newParser ; context . setParser ( parser ) ; editor . getFileContext ( ) . setFileContext ( context ) ; context . doFullParse ( ) ; UIJob refreshEditor = new UIJob ( Messages . GenericTextEditor_REFRESHING_LEXER ) { public IStatus runInUIThread ( IProgressMonitor monitor ) { editor . getViewer ( ) . getTextWidget ( ) . redraw ( ) ; editor . getViewer ( ) . getTextWidget ( ) . update ( ) ; return Status . OK_STATUS ; } } ; refreshEditor . schedule ( ) ; } catch ( final Exception e ) { if ( oldList != null ) { LanguageRegistry . registerTokenList ( oldList ) ; } UIJob errorJob = new UIJob ( Messages . GenericTextEditor_ERROR_PARSING_LEXER ) { public IStatus runInUIThread ( IProgressMonitor monitor ) { MessageDialog . openError ( shell , Messages . GenericTextEditor_ERROR_PARSING_LEXER , Messages . GenericTextEditor_ERROR_OCCURED_DURING_PARSE_LEXER ) ; return Status . OK_STATUS ; } } ; errorJob . schedule ( ) ; } } this . schedule ( 1000 ) ; return Status . OK_STATUS ; } } ; grammarFileMonitor . setSystem ( true ) ; grammarFileMonitor . schedule ( 1000 ) ; } private void createEditor ( ) { editor = new UnifiedEditor ( ) { public IFileServiceFactory getFileServiceFactory ( ) { return fileService ; } public String getDefaultFileExtension ( ) { return extension ; } protected IUnifiedEditorContributor createLocalContributor ( ) { return contributor ; } public IDocumentProvider createDocumentProvider ( ) { return documentProvider ; } protected String [ ] collectContextMenuPreferencePages ( ) { return GenericTextEditor . this . collectContextMenuPreferencePages ( ) ; } } ; editor . addPropertyListener ( new IPropertyListener ( ) { public void propertyChanged ( Object source , int propId ) { firePropertyChange ( propId ) ; } } ) ; } protected String [ ] collectContextMenuPreferencePages ( ) { return new String [ ] { "com.aptana.ide.editor.text.preferences.TextEditorPreferencePage" , "org.eclipse.ui.preferencePages.GeneralTextEditor" , "org.eclipse.ui.editors.preferencePages.Annotations" , "org.eclipse.ui.editors.preferencePages.QuickDiff" , "org.eclipse.ui.editors.preferencePages.Accessibility" , "org.eclipse.ui.editors.preferencePages.Spelling" , "org.eclipse.ui.editors.preferencePages.LinkedModePreferencePage" , } ; } protected BaseContributor createContributor ( ) { return new BaseContributor ( language ) { public IAutoEditStrategy [ ] getLocalAutoEditStrategies ( ISourceViewer sourceViewer , String contentType ) { return new IAutoEditStrategy [ ] { new UnifiedAutoIndentStrategy ( this . getFileContext ( ) , this . getParentConfiguration ( ) , sourceViewer ) { protected LexemeList getLexemeList ( ) { return getFileContext ( ) . getLexemeList ( ) ; } public IPreferenceStore getPreferenceStore ( ) { return TextPlugin . getDefault ( ) . getPreferenceStore ( ) ; } } } ; } public IUnifiedBracketInserter getLocalBracketInserter ( ISourceViewer sourceViewer , String contentType ) { EditorFileContext context = getFileContext ( ) ; IUnifiedBracketInserter result = null ; if ( context != null ) { result = new UnifiedBracketInserter ( sourceViewer , context ) { protected IPreferenceStore getPreferenceStore ( ) { return TextPlugin . getDefault ( ) . getPreferenceStore ( ) ; } } ; } return result ; } public UnifiedReconcilingStrategy getReconcilingStrategy ( ) { return new UnifiedReconcilingStrategy ( ) ; } } ; } private IParser createParser ( ) throws ParserInitializationException , FileNotFoundException { FileInputStream stream = new FileInputStream ( grammarFile ) ; TokenList tokenList = LanguageRegistry . createTokenList ( stream ) ; IParser result ; if ( tokenList != null ) { LanguageRegistry . registerTokenList ( tokenList ) ; } if ( LanguageRegistry . hasParser ( language ) ) { result = LanguageRegistry . getParser ( language ) ; } else { result = new UnifiedParser ( language ) ; } return result ; } private void createFileServiceFactory ( ) { fileService = new GenericTextFileServiceFactory ( ) ; } private class GenericTextFileServiceFactory extends BaseFileServiceFactory { public FileService createFileService ( IFileSourceProvider sourceProvider ) { return createFileService ( sourceProvider , true ) ; } public FileService createFileService ( IFileSourceProvider sourceProvider , boolean parse ) { IParseState parseState = parser . createParseState ( null ) ; FileService fileService = new FileService ( parser , parseState , sourceProvider , language ) ; ParentOffsetMapper parentMapper = new ParentOffsetMapper ( fileService ) ; BaseFileLanguageService languageService = new BaseFileLanguageService ( fileService , parseState , parser , parentMapper ) ; fileService . setErrorManager ( new UnifiedErrorManager ( fileService , language ) ) ; fileService . addLanguageService ( language , languageService ) ; if ( parse ) { fileService . doFullParse ( ) ; } return fileService ; } } public boolean isDirty ( ) { return editor != null ? editor . isDirty ( ) : false ; } public boolean isSaveAsAllowed ( ) { return editor != null ? editor . isSaveAsAllowed ( ) : false ; } public void createPartControl ( Composite parent ) { editor . createPartControl ( parent ) ; shell = parent . getShell ( ) ; } public void dispose ( ) { super . dispose ( ) ; if ( grammarFileMonitor != null ) { grammarFileMonitor . cancel ( ) ; } editor . dispose ( ) ; } public void setFocus ( ) { if ( editor != null ) { editor . setFocus ( ) ; } } public void close ( boolean save ) { if ( editor != null ) { editor . close ( save ) ; } } public IContextAwareness getContextAwareness ( ) { return editor != null ? editor . getContextAwareness ( ) : null ; } public IEditorPart getEditor ( ) { return editor ; } public EditorFileContext getFileContext ( ) { return editor != null ? editor . getFileContext ( ) : null ; } public UnifiedOutlinePage getOutlinePage ( ) { return editor != null ? editor . getOutlinePage ( ) : null ; } public UnifiedQuickOutlinePage createQuickOutlinePage ( ) { return editor != null ? editor . createQuickOutlinePage ( ) : null ; } public PairMatch getPairMatch ( int offset ) { return editor != null ? editor . getPairMatch ( offset ) : null ; } public String getParentDirectoryHint ( ) { return editor != null ? editor . getParentDirectoryHint ( ) : null ; } public void selectAndReveal ( int offset , int length ) { if ( editor != null ) { editor . selectAndReveal ( offset , length ) ; } } public void setParentDirectoryHint ( String hint ) { if ( editor != null ) { editor . setParentDirectoryHint ( hint ) ; } } public void showWhitespace ( boolean state ) { if ( editor != null ) { editor . showWhitespace ( state ) ; } } public void doRevertToSaved ( ) { if ( editor != null ) { editor . doRevertToSaved ( ) ; } } public IAction getAction ( String actionId ) { return editor != null ? editor . getAction ( actionId ) : null ; } public IDocumentProvider getDocumentProvider ( ) { return editor != null ? editor . getDocumentProvider ( ) : null ; } public ISelectionProvider getSelectionProvider ( ) { return editor != null ? editor . getSelectionProvider ( ) : null ; } public boolean isEditable ( ) { return editor != null ? editor . isEditable ( ) : false ; } public void removeActionActivationCode ( String actionId ) { if ( editor != null ) { editor . removeActionActivationCode ( actionId ) ; } } public void resetHighlightRange ( ) { if ( editor != null ) { editor . resetHighlightRange ( ) ; } } public void setAction ( String actionID , IAction action ) { if ( editor != null ) { editor . setAction ( actionID , action ) ; } } public void setActionActivationCode ( String actionId , char activationCharacter , int activationKeyCode , int activationStateMask ) { if ( editor != null ) { editor . setActionActivationCode ( actionId , activationCharacter , activationKeyCode , activationStateMask ) ; } } public void setHighlightRange ( int offset , int length , boolean moveCursor ) { if ( editor != null ) { editor . setHighlightRange ( offset , length , moveCursor ) ; } } public void showHighlightRangeOnly ( boolean showHighlightRangeOnly ) { if ( editor != null ) { editor . showHighlightRangeOnly ( showHighlightRangeOnly ) ; } } public boolean showsHighlightRangeOnly ( ) { return editor != null ? editor . showsHighlightRangeOnly ( ) : false ; } public void addRulerContextMenuListener ( IMenuListener listener ) { if ( editor != null ) { editor . addRulerContextMenuListener ( listener ) ; } } public boolean isEditorInputReadOnly ( ) { return editor != null ? editor . isEditorInputReadOnly ( ) : false ; } public void removeRulerContextMenuListener ( IMenuListener listener ) { if ( editor != null ) { editor . removeRulerContextMenuListener ( listener ) ; } } public void setStatusField ( IStatusField field , String category ) { if ( editor != null ) { editor . setStatusField ( field , category ) ; } } public void addSaveAsListener ( ISaveAsEvent listener ) { if ( editor != null ) { editor . addSaveAsListener ( listener ) ; } } public void addSaveListener ( ISaveEvent listener ) { if ( editor != null ) { editor . addSaveListener ( listener ) ; } } public void removeSaveAsListener ( ISaveAsEvent listener ) { if ( editor != null ) { editor . removeSaveAsListener ( listener ) ; } } public void removeSaveListener ( ISaveEvent listener ) { if ( editor != null ) { editor . removeSaveListener ( listener ) ; } } public IRegion getHighlightRange ( ) { return editor != null ? editor . getHighlightRange ( ) : null ; } public SourceViewerConfiguration getConfiguration ( ) { return editor != null ? editor . getConfiguration ( ) : null ; } public ISourceViewer getViewer ( ) { return editor != null ? editor . getViewer ( ) : null ; } public Object getAdapter ( Class adapter ) { return editor != null ? editor . getAdapter ( adapter ) : null ; } public boolean isSaveOnCloseNeeded ( ) { return editor != null ? editor . isSaveOnCloseNeeded ( ) : false ; } public String getTitleToolTip ( ) { return editor != null ? editor . getTitleToolTip ( ) : "" ; } public String getContentDescription ( ) { return editor != null ? editor . getContentDescription ( ) : "" ; } public int getOrientation ( ) { return editor != null ? editor . getOrientation ( ) : 0 ; } public String getTitle ( ) { return editor != null ? editor . getTitle ( ) : "" ; } public Image getTitleImage ( ) { return editor != null ? editor . getTitleImage ( ) : null ; } public void setInitializationData ( IConfigurationElement config , String propertyName , Object data ) { if ( editor != null ) { editor . setInitializationData ( config , propertyName , data ) ; } } public void showBusy ( boolean busy ) { if ( editor != null ) { editor . showBusy ( busy ) ; } } public IUnifiedEditorContributor getBaseContributor ( ) { return editor . getBaseContributor ( ) ; } public String getDefaultFileExtension ( ) { return editor . getDefaultFileExtension ( ) ; } public void addFileServiceChangeListener ( IFileServiceChangeListener listener ) { if ( editor != null ) { editor . addFileServiceChangeListener ( listener ) ; } } public void removeFileServiceChangeListener ( IFileServiceChangeListener listener ) { if ( editor != null ) { editor . removeFileServiceChangeListener ( listener ) ; } } public void showPianoKeys ( boolean state ) { if ( editor != null ) { editor . showPianoKeys ( state ) ; } } } 