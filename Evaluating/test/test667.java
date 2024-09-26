<<<<<<< HEAD
class LogTab { static class LogSourceViewer extends SourceViewer { public LogSourceViewer ( Composite parent , IVerticalRuler verticalRuler , IOverviewRuler overviewRuler , boolean showAnnotationsOverview , int styles ) { super ( parent , verticalRuler , overviewRuler , showAnnotationsOverview , styles ) ; } protected void revealLastLine ( ) { try { IDocument doc = getVisibleDocument ( ) ; int endLine = doc . getLineOfOffset ( doc . getLength ( ) == 0 ? 0 : doc . getLength ( ) - 1 ) ; int startLine = endLine ; int top = getTextWidget ( ) . getTopIndex ( ) ; if ( top > - 1 ) { int bottom = getBottomIndex ( getTextWidget ( ) ) ; int lines = bottom - top ; if ( startLine >= top && startLine <= bottom && endLine >= top && endLine <= bottom ) { } else { int delta = Math . max ( 0 , lines - ( endLine - startLine ) ) ; getTextWidget ( ) . setTopIndex ( startLine - delta / 3 ) ; updateViewportListeners ( INTERNAL ) ; } } } catch ( BadLocationException e ) { throw new IllegalArgumentException ( "Illegal text range" ) ; } } public static int getBottomIndex ( StyledText widget ) { int lastPixel = computeLastVisiblePixel ( widget ) ; int bottom = widget . getLineIndex ( lastPixel ) ; if ( bottom == 0 ) return bottom ; int pixel = widget . getLinePixel ( bottom ) ; if ( pixel <= 0 ) return bottom ; int offset = widget . getOffsetAtLine ( bottom ) ; int height = widget . getLineHeight ( offset ) ; if ( pixel + height - 1 > lastPixel ) return bottom - 1 ; return bottom ; } private static int computeLastVisiblePixel ( StyledText widget ) { int caHeight = widget . getClientArea ( ) . height ; int lastPixel = caHeight - 1 ; return lastPixel ; } } private static class DocumentWriter extends Writer { private IDocument document ; public DocumentWriter ( IDocument document ) { this . document = document ; } @ Override public void close ( ) throws IOException { } @ Override public void flush ( ) throws IOException { } @ Override public void write ( char [ ] cbuf , int off , int len ) throws IOException { try { document . replace ( document . getLength ( ) , 0 , new String ( cbuf , off , len ) ) ; } catch ( BadLocationException e ) { throw new IOException ( e . getMessage ( ) ) ; } } } private static final String DEFAULT_ENCODING = "cp1251" ; private static final int MAX_NOTIFICATION_SIZE = 1024 ; private static final String BOOKMARK_ANNOTATION_TYPE = "org.eclipse.ui.workbench.texteditor.bookmark" ; private static final String DEFAULT_FONT = "DEFAULT_TAIL_VIEW_FONT" ; private static final String PREFIX = "LogTab" ; private static final String BOOKMARK_PREFIX = PREFIX + ".BookmarkAction" ; protected final static int VERTICAL_RULER_WIDTH = 12 ; protected int _maxColorizingColumns = 512 ; private final LogView logView ; private final URI uri ; private CTabItem item ; private IVerticalRuler ruler ; private long currentGlobalOffset = 0 ; private Set contentDependentActions = new HashSet ( ) ; private ILogWatcher watcher ; private ILogResource resource = null ; private MenuManager menuMgr ; private FontRegistry fontRegistry = new FontRegistry ( ) ; private ColorRegistry colorRegistry = new ColorRegistry ( ) ; private SourceViewer viewer ; private Font boldFont ; private Font normalFont ; private Font italicFont ; private Color textForeground ; private volatile boolean unreadDataAvailable ; private volatile boolean resourceAvailable ; private LoggingMarkerRulerAction actionAddBookmark ; private UniformResourceMarkerAnnotationModel model ; private IOverviewRuler overviewRuler ; private MarkerAnnotationPreferences fAnnotationPreferences ; private Color bookmarkColor = new Color ( Display . getCurrent ( ) , 0 , 0 , 255 ) ; private LineStyleListener _lineStyleListener ; private LoggingColorizer _colorizer ; private TextChangeListener _textChangeListener ; private ILexer lexer ; private boolean followTail = true ; private IDocument document ; private IPropertyChangeListener colorizationPreferencesListener ; private ILoggingPreferenceListener loggingPreferenceListener ; private ILogTailListener tailListener ; protected SourceViewerDecorationSupport decorationSupport ; protected LoggingLexemeManager lexememanager ; private Composite composite ; private boolean supportsErase ; public LogTab ( LogView logView , URI uri , String name , boolean supportsErase ) { this . logView = logView ; this . uri = uri ; this . supportsErase = supportsErase ; String tabName = name ; if ( tabName == null ) { tabName = getTabName ( uri . getPath ( ) ) ; } fAnnotationPreferences = EditorsPlugin . getDefault ( ) . getMarkerAnnotationPreferences ( ) ; item = createLogTabItem ( tabName , uri ) ; createColorizer ( ) ; try { resource = LogResourceFactory . createResource ( uri ) ; resource . setEncoding ( Charset . forName ( LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . getDefaultEncoding ( ) ) ) ; } catch ( IOException e ) { e . printStackTrace ( new PrintWriter ( new DocumentWriter ( getDocument ( ) ) ) ) ; return ; } initAnnotationModel ( ) ; createActions ( ) ; hookRulerContextMenu ( ) ; initializeFonts ( ) ; initializeColors ( ) ; getDocument ( ) . addDocumentListener ( new IDocumentListener ( ) { public void documentAboutToBeChanged ( DocumentEvent event ) { } public void documentChanged ( DocumentEvent event ) { } } ) ; LoggingPreferences preferences = LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) ; int timeout = preferences . getReadTimeout ( ) ; int readBuffer = preferences . getReadBuffer ( ) ; int maxBytesPerSecond = ( int ) ( ( ( ( long ) readBuffer ) * 1000l ) / ( ( long ) timeout ) ) ; watcher = resource . getResourceWatcher ( new DisplayThreadProxy ( item . getDisplay ( ) ) , maxBytesPerSecond , readBuffer , timeout , Charset . forName ( DEFAULT_ENCODING ) , LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . getBacklogLines ( ) ) ; registerTailListener ( ) ; registerResourceListener ( ) ; watcher . startWatching ( ) ; this . logView . tabFolder . setSelection ( item ) ; linkColorer ( ) ; } public URI getURI ( ) { return uri ; } public TextViewer getViewer ( ) { return viewer ; } public String getName ( ) { return item . getText ( ) ; } public boolean supportsLogErase ( ) { return this . supportsErase ; } public void setName ( String name ) { item . setText ( name ) ; } public void close ( ) { watcher . stopWatching ( ) ; watcher . close ( ) ; try { resource . close ( ) ; } catch ( IOException e ) { IdeLog . logError ( LoggingPlugin . getDefault ( ) , com . aptana . ide . logging . view . Messages . LogTab_4 , e ) ; } disposeListeners ( ) ; if ( boldFont != null ) { boldFont . dispose ( ) ; } if ( italicFont != null ) { italicFont . dispose ( ) ; } bookmarkColor . dispose ( ) ; item . dispose ( ) ; if ( decorationSupport != null ) { decorationSupport . dispose ( ) ; decorationSupport = null ; } } public void start ( ) { watcher . startWatching ( ) ; } public void stop ( ) { watcher . stopWatching ( ) ; } public boolean isWatching ( ) { return watcher . watchingStatus ( ) ; } public void reload ( ) { watcher . resetWatching ( ) ; clear ( ) ; watcher . startWatching ( ) ; } public CTabItem getItem ( ) { return item ; } public void clear ( ) { if ( document != null ) { document . set ( "" ) ; } currentGlobalOffset = 0 ; if ( lexememanager != null ) { List < String > empty = Collections . emptyList ( ) ; lexememanager . dataAvailable ( empty ) ; } } public void selected ( ) { unreadDataAvailable = false ; makeTabBold ( false ) ; if ( ! resourceAvailable ) { makeTabShaded ( true ) ; } } public void clearLogFile ( ) { try { resource . clear ( ) ; } catch ( IOException e ) { MessageDialog . openError ( this . getItem ( ) . getControl ( ) . getShell ( ) , com . aptana . ide . logging . view . Messages . LogTab_2 , com . aptana . ide . logging . view . Messages . LogTab_1 + resource . getURI ( ) . getPath ( ) ) ; } reload ( ) ; } public boolean hasUnreadData ( ) { return unreadDataAvailable ; } public void setFollowTail ( boolean followTail ) { this . followTail = followTail ; } public void invertFollowTail ( ) { followTail = ! followTail ; } String getSelection ( ) { if ( viewer == null ) { return null ; } TextSelection selection = ( TextSelection ) viewer . getSelection ( ) ; if ( selection == null ) { return null ; } try { return getDocument ( ) . get ( selection . getOffset ( ) , selection . getLength ( ) ) ; } catch ( BadLocationException ex ) { IdeLog . logError ( LoggingPlugin . getDefault ( ) , com . aptana . ide . logging . view . Messages . LogTab_ERR_Exception , ex ) ; return null ; } } void refreshViewer ( ) { viewer . refresh ( ) ; } private CTabItem createLogTabItem ( String tabName , URI uri ) { CTabItem item = new CTabItem ( this . logView . tabFolder , SWT . DEFAULT ) ; item . setText ( tabName ) ; if ( uri != null ) { item . setToolTipText ( uri . getPath ( ) ) ; } document = new Document ( ) ; lexememanager = new LoggingLexemeManager ( document , LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) ) ; IAnnotationAccess access = new DefaultMarkerAnnotationAccess ( ) ; ruler = createVerticalRuler ( access ) ; overviewRuler = createOverviewRuler ( access ) ; createViewer ( item ) ; return item ; } void recreateViewer ( ) { decorationSupport . dispose ( ) ; decorationSupport = null ; composite . dispose ( ) ; viewer = null ; LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . removePreferenceListener ( loggingPreferenceListener ) ; createViewer ( item ) ; } private void createViewer ( CTabItem item ) { int viewerStyle = 0 ; if ( LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . getWrapping ( ) ) { viewerStyle = SWT . V_SCROLL | SWT . WRAP | SWT . BORDER ; } else { viewerStyle = SWT . V_SCROLL | SWT . H_SCROLL | SWT . BORDER ; } composite = new Composite ( this . logView . tabFolder , SWT . NONE ) ; GridLayout layout = new GridLayout ( 1 , true ) ; layout . marginHeight = 3 ; layout . marginWidth = 3 ; GridData data = new GridData ( SWT . FILL , SWT . FILL , true , true ) ; composite . setLayout ( layout ) ; composite . setLayoutData ( data ) ; composite . setBackground ( UnifiedColorManager . getInstance ( ) . getColor ( new RGB ( 220 , 220 , 220 ) ) ) ; viewer = new LogSourceViewer ( composite , ruler , overviewRuler , true , viewerStyle ) ; viewer . addSelectionChangedListener ( new ISelectionChangedListener ( ) { public void selectionChanged ( SelectionChangedEvent event ) { int i = 0 ; i ++ ; } } ) ; viewer . setDocument ( getDocument ( ) ) ; Control control = viewer . getControl ( ) ; control . setLayoutData ( data ) ; viewer . getTextWidget ( ) . setFont ( LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . getFont ( ) ) ; item . setControl ( composite ) ; Menu menu = logView . menuMgr . createContextMenu ( viewer . getTextWidget ( ) ) ; viewer . getTextWidget ( ) . setMenu ( menu ) ; TextListener listener = this . logView . new TextListener ( viewer ) ; viewer . addTextInputListener ( listener ) ; viewer . addTextListener ( listener ) ; viewer . activatePlugins ( ) ; createSourceViewerDecorationSupport ( viewer ) ; bindToColorizationSave ( viewer ) ; } void recreateItem ( String name , int newPos ) { item = new CTabItem ( this . logView . tabFolder , SWT . DEFAULT , newPos ) ; item . setText ( name ) ; item . setControl ( composite ) ; } private void bindToColorizationSave ( final SourceViewer viewer ) { colorizationPreferencesListener = new IPropertyChangeListener ( ) { public void propertyChange ( PropertyChangeEvent event ) { if ( LoggingStructureProvider . COLORIZATION_SAVED . equals ( event . getProperty ( ) ) ) { createColorizer ( ) ; lexememanager . clearCache ( ) ; if ( viewer != null ) { viewer . setRedraw ( false ) ; ( ( StyledText ) LogTab . this . viewer . getTextWidget ( ) ) . removeLineStyleListener ( _lineStyleListener ) ; ( ( StyledText ) LogTab . this . viewer . getTextWidget ( ) ) . addLineStyleListener ( _lineStyleListener ) ; viewer . setRedraw ( true ) ; } } } } ; UnifiedEditorsPlugin . getDefault ( ) . getPreferenceStore ( ) . addPropertyChangeListener ( colorizationPreferencesListener ) ; LoggingPreferences preferences = LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) ; loggingPreferenceListener = new ILoggingPreferenceListener ( ) { public void rulesChanged ( ) { } public void wrappingChanged ( boolean wrapping ) { recreateViewer ( ) ; } public void fontChanged ( Font font ) { viewer . getTextWidget ( ) . setFont ( font ) ; } public void textForegroundColorChanged ( Color color ) { textForeground = color ; } } ; preferences . addPreferenceListener ( loggingPreferenceListener ) ; } private void hookRulerContextMenu ( ) { menuMgr = new MenuManager ( "#PopupMenu" ) ; menuMgr . setRemoveAllWhenShown ( true ) ; menuMgr . addMenuListener ( new IMenuListener ( ) { public void menuAboutToShow ( IMenuManager manager ) { logView . setFocus ( ) ; fillRulerContextMenu ( manager ) ; } } ) ; Menu menu = menuMgr . createContextMenu ( ruler . getControl ( ) ) ; ruler . getControl ( ) . setMenu ( menu ) ; logView . getSite ( ) . registerContextMenu ( menuMgr , viewer . getSelectionProvider ( ) ) ; } private void fillRulerContextMenu ( IMenuManager manager ) { manager . add ( actionAddBookmark ) ; } protected IVerticalRuler createVerticalRuler ( IAnnotationAccess access ) { return new VerticalRuler ( VERTICAL_RULER_WIDTH , access ) ; } protected IOverviewRuler createOverviewRuler ( IAnnotationAccess access ) { ISharedTextColors sharedColors = EditorsPlugin . getDefault ( ) . getSharedTextColors ( ) ; IOverviewRuler ruler = new OverviewRuler ( access , VERTICAL_RULER_WIDTH , sharedColors ) ; Iterator e = fAnnotationPreferences . getAnnotationPreferences ( ) . iterator ( ) ; while ( e . hasNext ( ) ) { AnnotationPreference preference = ( AnnotationPreference ) e . next ( ) ; if ( preference . contributesToHeader ( ) ) ruler . addHeaderAnnotationType ( preference . getAnnotationType ( ) ) ; } String type = BOOKMARK_ANNOTATION_TYPE ; ruler . addAnnotationType ( type ) ; ruler . addHeaderAnnotationType ( type ) ; ruler . setAnnotationTypeLayer ( type , 1 ) ; ruler . setAnnotationTypeColor ( type , bookmarkColor ) ; return ruler ; } private String getTabName ( String path ) { Path p = new Path ( path ) ; return p . lastSegment ( ) ; } private void makeTabBold ( boolean bold ) { Font currentFont = item . getFont ( ) ; int style = currentFont . getFontData ( ) [ 0 ] . getStyle ( ) ; if ( bold ) { if ( ( style & SWT . BOLD ) != 0 ) { return ; } item . setFont ( boldFont ) ; } else { if ( ( style & SWT . BOLD ) == 0 ) { return ; } item . setFont ( normalFont ) ; } } private void makeTabShaded ( boolean shaded ) { if ( shaded ) { Font currentFont = item . getFont ( ) ; if ( currentFont . equals ( italicFont ) ) { return ; } item . setFont ( italicFont ) ; } else { Font currentFont = item . getFont ( ) ; if ( ! currentFont . equals ( italicFont ) ) { return ; } item . setFont ( normalFont ) ; } } private void initializeColors ( ) { textForeground = LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . getTextColor ( ) ; } private void initializeFonts ( ) { Font normalFont = item . getFont ( ) ; this . normalFont = normalFont ; FontData data = normalFont . getFontData ( ) [ 0 ] ; int originalStyle = data . getStyle ( ) ; int boldStyle = originalStyle | SWT . BOLD ; data . setStyle ( boldStyle ) ; this . boldFont = new Font ( normalFont . getDevice ( ) , new FontData [ ] { data } ) ; int italicStyle = originalStyle | SWT . ITALIC ; data = normalFont . getFontData ( ) [ 0 ] ; data . setStyle ( italicStyle ) ; this . italicFont = new Font ( normalFont . getDevice ( ) , new FontData [ ] { data } ) ; } private void createActions ( ) { actionAddBookmark = new LoggingMarkerRulerAction ( Messages . getResourceBundle ( ) , BOOKMARK_PREFIX , resource , getDocument ( ) , model , ruler , true , IMarker . BOOKMARK , item . getControl ( ) . getShell ( ) ) ; actionAddBookmark . update ( ) ; } private void initAnnotationModel ( ) { model = new UniformResourceMarkerAnnotationModel ( resource ) ; model . connect ( getDocument ( ) ) ; ruler . setModel ( model ) ; overviewRuler . setModel ( model ) ; } private void registerResourceListener ( ) { watcher . registerListener ( new ILogResourceListener ( ) { public void resourceAvailable ( boolean available ) { resourceAvailable = available ; if ( ! unreadDataAvailable ) { if ( ! resourceAvailable ) { makeTabShaded ( true ) ; } else { makeTabShaded ( false ) ; } } } } ) ; } private void registerTailListener ( ) { tailListener = new ILogTailListener ( ) { public void dataAvailable ( String data , long globalOffset , long globalLength ) { updateData ( data , globalOffset , globalLength ) ; } public void errorHappened ( Throwable th ) { IdeLog . logError ( LoggingPlugin . getDefault ( ) , com . aptana . ide . logging . view . Messages . LogTab_ERR_FetchTail , th ) ; } } ; watcher . registerListener ( tailListener ) ; } private void updateContentDependentActions ( ) { Iterator it = contentDependentActions . iterator ( ) ; while ( it . hasNext ( ) ) { Action action = ( Action ) it . next ( ) ; if ( action instanceof IUpdate ) { ( ( IUpdate ) action ) . update ( ) ; } } } void markAsContentDependent ( Action action ) { contentDependentActions . add ( action ) ; } private void linkColorer ( ) { if ( _lineStyleListener == null ) { _lineStyleListener = new LineStyleListener ( ) { public void lineGetStyle ( LineStyleEvent e ) { int orgOffset = e . lineOffset ; int offset = orgOffset ; int extra = 0 ; int lineLength = e . lineText . length ( ) ; if ( viewer instanceof ITextViewerExtension5 ) { ITextViewerExtension5 v5 = ( ITextViewerExtension5 ) viewer ; offset = v5 . widgetOffset2ModelOffset ( e . lineOffset ) ; extra = offset - e . lineOffset ; } int maxLineLength = lineLength > _maxColorizingColumns ? _maxColorizingColumns : lineLength ; Lexeme [ ] lexemes = null ; int lineNumber ; try { lineNumber = document . getLineOfOffset ( e . lineOffset ) ; lexemes = lexememanager . getLexemes ( lineNumber ) ; } catch ( BadLocationException e1 ) { IdeLog . logError ( LoggingPlugin . getDefault ( ) , com . aptana . ide . logging . view . Messages . LogTab_ERR_Exception , e1 ) ; } if ( lexemes != null && lexemes . length > 0 ) { Vector styles = new Vector ( ) ; _colorizer . createStyles ( styles , lexemes , false ) ; StyleRange [ ] styleResults = ( StyleRange [ ] ) styles . toArray ( new StyleRange [ ] { } ) ; if ( extra > 0 ) { for ( int i = 0 ; i < styleResults . length ; i ++ ) { StyleRange range = styleResults [ i ] ; range . start -= extra ; } } e . styles = styleResults ; } else { StyleRange [ ] styles = new StyleRange [ 1 ] ; styles [ 0 ] = new StyleRange ( e . lineOffset , e . lineText . length ( ) , textForeground , null ) ; e . styles = styles ; } } } ; } if ( _textChangeListener == null ) { _textChangeListener = new TextChangeListener ( ) { public void textChanging ( TextChangingEvent event ) { } public void textChanged ( TextChangedEvent event ) { StyledText text = getViewer ( ) . getTextWidget ( ) ; redrawFrom ( text , text . getLineAtOffset ( text . getCaretOffset ( ) ) ) ; } public void textSet ( TextChangedEvent event ) { StyledText text = getViewer ( ) . getTextWidget ( ) ; redrawFrom ( text , 0 ) ; } private void redrawFrom ( StyledText text , int lno ) { if ( lno < 0 || lno >= text . getLineCount ( ) ) { return ; } int height = text . getClientArea ( ) . height ; int width = text . getClientArea ( ) . width + text . getHorizontalPixel ( ) ; try { text . redraw ( 0 , 0 , width , height , true ) ; } catch ( Exception e ) { } } } ; } getViewer ( ) . getTextWidget ( ) . addLineStyleListener ( _lineStyleListener ) ; getViewer ( ) . getTextWidget ( ) . getContent ( ) . addTextChangeListener ( _textChangeListener ) ; } private void addLexeme ( Lexeme lexeme , LexemeList lexemes ) { lexemes . add ( lexeme ) ; return ; } private ILexer getLexer ( ) { return TokenTypes . getLexerFactory ( ) . getLexer ( ) ; } private Lexeme findLastRecognizedRegexpLexeme ( LexemeList lexemes ) { if ( lexemes . size ( ) == 0 ) { return null ; } for ( int i = lexemes . size ( ) - 1 ; i >= 0 ; i -- ) { Lexeme currentLexeme = lexemes . get ( i ) ; if ( TokenTypes . isRegexpType ( currentLexeme . getType ( ) ) ) { return currentLexeme ; } } return null ; } private void disposeListeners ( ) { UnifiedEditorsPlugin . getDefault ( ) . getPreferenceStore ( ) . removePropertyChangeListener ( colorizationPreferencesListener ) ; LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . removePreferenceListener ( loggingPreferenceListener ) ; watcher . removeListener ( tailListener ) ; } private void createColorizer ( ) { _colorizer = new LoggingColorizer ( getLexer ( ) . getTokenList ( TokenTypes . LANGUAGE ) ) ; } private boolean getAutobolding ( ) { return LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . getAutoBolding ( ) ; } protected void createSourceViewerDecorationSupport ( ISourceViewer viewer ) { if ( decorationSupport == null ) { ISharedTextColors sharedColors = EditorsPlugin . getDefault ( ) . getSharedTextColors ( ) ; decorationSupport = new SourceViewerDecorationSupport ( viewer , null , null , sharedColors ) ; configureSourceViewerDecorationSupport ( decorationSupport ) ; decorationSupport . install ( LoggingPlugin . getDefault ( ) . getPreferenceStore ( ) ) ; ; } } protected void configureSourceViewerDecorationSupport ( SourceViewerDecorationSupport support ) { support . setCursorLinePainterPreferenceKeys ( LoggingPreferences . CURSORLINE_KEY , LoggingPreferences . CURSORLINE_COLOR_KEY ) ; support . setSymbolicFontName ( LoggingPreferences . MAIN_TEXT_FONT_KEY ) ; } private IDocument getDocument ( ) { return document ; } private void updateData ( String data , long globalOffset , long globalLength ) { if ( ! LogTab . this . equals ( logView . getActiveTab ( ) ) && getAutobolding ( ) ) { unreadDataAvailable = true ; makeTabBold ( true ) ; } Integer topIndex = null ; Integer bottomIndex = null ; int horizontalScrollPixel = - 1 ; ISelection selection = null ; if ( viewer != null ) { horizontalScrollPixel = viewer . getTextWidget ( ) . getHorizontalPixel ( ) ; viewer . setRedraw ( false ) ; selection = viewer . getSelection ( ) ; topIndex = viewer . getTopIndex ( ) ; bottomIndex = viewer . getBottomIndex ( ) ; } updateDocumentData ( data , globalOffset , globalLength ) ; if ( viewer != null ) { viewer . setRedraw ( true ) ; if ( followTail ) { ( ( LogSourceViewer ) viewer ) . revealLastLine ( ) ; } else { if ( topIndex != null ) { try { int startOffset = document . getLineOffset ( topIndex ) ; int endOffset = document . getLineOffset ( bottomIndex ) + document . getLineLength ( bottomIndex ) ; viewer . revealRange ( startOffset , endOffset - startOffset ) ; topIndex = viewer . getTopIndex ( ) ; bottomIndex = viewer . getBottomIndex ( ) ; } catch ( BadLocationException e ) { IdeLog . logError ( LoggingPlugin . getDefault ( ) , com . aptana . ide . logging . view . Messages . LogTab_ERR_Exception , e ) ; } } } final int horizontalScrollPixel1 = horizontalScrollPixel ; viewer . getTextWidget ( ) . setHorizontalPixel ( horizontalScrollPixel1 ) ; } } private void updateDocumentData ( String data , long globalOffset , long globalLength ) { try { IDocument document = getDocument ( ) ; long lastGlobalPosition = currentGlobalOffset + document . getLength ( ) ; if ( globalOffset == 0 && globalLength >= Integer . MAX_VALUE ) { document . replace ( 0 , document . getLength ( ) , data ) ; currentGlobalOffset = globalOffset ; } else if ( globalOffset < currentGlobalOffset ) { int diff = ( int ) ( currentGlobalOffset - globalOffset ) ; if ( diff <= data . length ( ) ) { int lengthToReplace = ( int ) ( globalLength - diff ) ; String beginData = data . substring ( 0 , diff ) ; String innrerData = data . substring ( diff , data . length ( ) ) ; replaceInnerData ( innrerData , currentGlobalOffset , lengthToReplace , document ) ; document . replace ( 0 , diff , beginData ) ; currentGlobalOffset = globalOffset ; } else { document . replace ( 0 , document . getLength ( ) , data ) ; currentGlobalOffset = globalOffset ; } } else if ( globalOffset >= currentGlobalOffset && globalOffset < lastGlobalPosition ) { replaceInnerData ( data , globalOffset , globalLength , document ) ; } else if ( globalOffset == lastGlobalPosition ) { document . replace ( document . getLength ( ) , 0 , data ) ; } else { document . replace ( 0 , document . getLength ( ) , data ) ; currentGlobalOffset = globalOffset ; } int numberOfLines = document . getNumberOfLines ( ) ; int allowedNumberOfLines = LoggingPlugin . getDefault ( ) . getLoggingPreferences ( ) . getBacklogLines ( ) ; List < String > topLines = new ArrayList < String > ( ) ; if ( numberOfLines > allowedNumberOfLines ) { int toRemoveLinesNum = numberOfLines - allowedNumberOfLines ; int offset = document . getLineOffset ( toRemoveLinesNum ) ; for ( int i = 0 ; i < toRemoveLinesNum ; i ++ ) { int lineOffset = document . getLineOffset ( i ) ; int lineLength = document . getLineLength ( i ) ; topLines . add ( document . get ( lineOffset , lineLength ) ) ; } currentGlobalOffset += offset ; document . replace ( 0 , offset , "" ) ; } if ( lexememanager != null ) { lexememanager . dataAvailable ( topLines ) ; } } catch ( Throwable e ) { IdeLog . logError ( LoggingPlugin . getDefault ( ) , com . aptana . ide . logging . view . Messages . LogTab_8 , e ) ; } } private void replaceInnerData ( String data , long globalOffset , long globalLength , IDocument document ) throws BadLocationException { int diff = ( int ) ( globalOffset - currentGlobalOffset ) ; int lengthToReplace = ( int ) globalLength ; if ( lengthToReplace == Integer . MAX_VALUE || diff + lengthToReplace > document . getLength ( ) ) { lengthToReplace = document . getLength ( ) - diff ; } document . replace ( diff , lengthToReplace , data ) ; } } 
=======
public class AndroidHttpTransport extends HttpTransportSE { public AndroidHttpTransport ( String url ) { super ( url ) ; } @ Override protected ServiceConnection getServiceConnection ( ) throws IOException { return new AndroidServiceConnection ( super . url ) ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
