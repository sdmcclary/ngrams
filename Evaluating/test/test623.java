<<<<<<< HEAD
class ContextInformationPopup implements IContentAssistListener , KeyListener { private static final int OUTER_BORDER = 1 ; private static final int INNER_BORDER = 3 ; static class ContextFrame { final int fBeginOffset ; final int fOffset ; final int fVisibleOffset ; final IContextInformation fInformation ; final IContextInformationValidator fValidator ; final IContextInformationPresenter fPresenter ; public ContextFrame ( IContextInformation information , int beginOffset , int offset , int visibleOffset , IContextInformationValidator validator , IContextInformationPresenter presenter ) { fInformation = information ; fBeginOffset = beginOffset ; fOffset = offset ; fVisibleOffset = visibleOffset ; fValidator = validator ; fPresenter = presenter ; } public boolean equals ( Object obj ) { if ( obj instanceof ContextFrame ) { ContextFrame frame = ( ContextFrame ) obj ; return fInformation . equals ( frame . fInformation ) && fBeginOffset == frame . fBeginOffset ; } return super . equals ( obj ) ; } public int hashCode ( ) { return ( fInformation . hashCode ( ) << 16 ) | fBeginOffset ; } } private ITextViewer fViewer ; private ContentAssistant fContentAssistant ; private PopupCloser fPopupCloser = new PopupCloser ( ) ; private Shell fContextSelectorShell ; private Table fContextSelectorTable ; private IContextInformation [ ] fContextSelectorInput ; private String fLineDelimiter = null ; private Shell fContextInfoPopup ; private StyledText fContextInfoText ; private TextPresentation fTextPresentation ; private Stack fContextFrameStack = new Stack ( ) ; private IContentAssistSubjectControl fContentAssistSubjectControl ; private ContentAssistSubjectControlAdapter fContentAssistSubjectControlAdapter ; private SelectionListener fTextWidgetSelectionListener ; private ContextFrame fLastContext = null ; private char fActivationKey ; public ContextInformationPopup ( ContentAssistant contentAssistant , ITextViewer viewer ) { fContentAssistant = contentAssistant ; fViewer = viewer ; fContentAssistSubjectControlAdapter = new ContentAssistSubjectControlAdapter ( fViewer ) ; } public ContextInformationPopup ( ContentAssistant contentAssistant , IContentAssistSubjectControl contentAssistSubjectControl ) { fContentAssistant = contentAssistant ; fContentAssistSubjectControl = contentAssistSubjectControl ; fContentAssistSubjectControlAdapter = new ContentAssistSubjectControlAdapter ( fContentAssistSubjectControl ) ; } public String showContextProposals ( final boolean autoActivated ) { final Control control = fContentAssistSubjectControlAdapter . getControl ( ) ; BusyIndicator . showWhile ( control . getDisplay ( ) , new Runnable ( ) { public void run ( ) { int offset = fContentAssistSubjectControlAdapter . getSelectedRange ( ) . x ; IContextInformation [ ] contexts = computeContextInformation ( offset ) ; int count = ( contexts == null ? 0 : contexts . length ) ; if ( count == 1 ) { ContextFrame frame = createContextFrame ( contexts [ 0 ] , offset ) ; if ( isDuplicate ( frame ) ) { validateContextInformation ( ) ; } else { internalShowContextInfo ( frame ) ; } } else if ( count > 0 ) { for ( int i = 0 ; i < contexts . length ; i ++ ) { IContextInformation info = contexts [ i ] ; ContextFrame frame = createContextFrame ( info , offset ) ; if ( isDuplicate ( frame ) ) { validateContextInformation ( ) ; return ; } if ( isLastFrame ( frame ) ) { internalShowContextInfo ( frame ) ; return ; } for ( Iterator it = fContextFrameStack . iterator ( ) ; it . hasNext ( ) ; ) { ContextFrame stackFrame = ( ContextFrame ) it . next ( ) ; if ( stackFrame . equals ( frame ) ) { validateContextInformation ( ) ; return ; } } } if ( fLineDelimiter == null ) { fLineDelimiter = fContentAssistSubjectControlAdapter . getLineDelimiter ( ) ; } createContextSelector ( ) ; setContexts ( contexts ) ; displayContextSelector ( ) ; hideContextInfoPopup ( ) ; } } } ) ; return getErrorMessage ( ) ; } public void showContextInformation ( final IContextInformation info , final int offset ) { Control control = fContentAssistSubjectControlAdapter . getControl ( ) ; BusyIndicator . showWhile ( control . getDisplay ( ) , new Runnable ( ) { public void run ( ) { if ( info == null ) { validateContextInformation ( ) ; } else { ContextFrame frame = createContextFrame ( info , offset ) ; if ( isDuplicate ( frame ) ) { validateContextInformation ( ) ; } else { internalShowContextInfo ( frame ) ; } hideContextSelector ( ) ; } } } ) ; } private void internalShowContextInfo ( ContextFrame frame ) { if ( frame != null ) { fContextFrameStack . push ( frame ) ; if ( fContextFrameStack . size ( ) == 1 ) { fLastContext = null ; } internalShowContextFrame ( frame , fContextFrameStack . size ( ) == 1 ) ; validateContextInformation ( ) ; } } private ContextFrame createContextFrame ( IContextInformation information , int offset ) { IContextInformationValidator validator = fContentAssistSubjectControlAdapter . getContextInformationValidator ( fContentAssistant , offset ) ; if ( validator != null ) { int beginOffset = ( information instanceof IContextInformationExtension ) ? ( ( IContextInformationExtension ) information ) . getContextInformationPosition ( ) : offset ; if ( beginOffset == - 1 ) { beginOffset = offset ; } int visibleOffset = fContentAssistSubjectControlAdapter . getWidgetSelectionRange ( ) . x - ( offset - beginOffset ) ; IContextInformationPresenter presenter = fContentAssistSubjectControlAdapter . getContextInformationPresenter ( fContentAssistant , offset ) ; return new ContextFrame ( information , beginOffset , offset , visibleOffset , validator , presenter ) ; } return null ; } private boolean isDuplicate ( ContextFrame frame ) { if ( frame == null ) { return false ; } if ( fContextFrameStack . isEmpty ( ) ) { return false ; } ContextFrame top = ( ContextFrame ) fContextFrameStack . peek ( ) ; return frame . equals ( top ) ; } private boolean isLastFrame ( ContextFrame frame ) { return frame != null && frame . equals ( fLastContext ) ; } private void internalShowContextFrame ( ContextFrame frame , boolean initial ) { fContentAssistSubjectControlAdapter . installValidator ( frame ) ; if ( frame . fPresenter != null ) { if ( fTextPresentation == null ) { fTextPresentation = new TextPresentation ( ) ; } fContentAssistSubjectControlAdapter . installContextInformationPresenter ( frame ) ; frame . fPresenter . updatePresentation ( frame . fOffset , fTextPresentation ) ; } createContextInfoPopup ( ) ; fContextInfoText . setText ( frame . fInformation . getInformationDisplayString ( ) ) ; if ( fTextPresentation != null ) { TextPresentation . applyTextPresentation ( fTextPresentation , fContextInfoText ) ; } resize ( ) ; if ( initial ) { if ( fContentAssistant . addContentAssistListener ( this , ContentAssistant . CONTEXT_INFO_POPUP ) ) { if ( fContentAssistSubjectControlAdapter . getControl ( ) != null ) { fTextWidgetSelectionListener = new SelectionAdapter ( ) { public void widgetSelected ( SelectionEvent e ) { validateContextInformation ( ) ; } } ; fContentAssistSubjectControlAdapter . addSelectionListener ( fTextWidgetSelectionListener ) ; } fContentAssistant . addToLayout ( this , fContextInfoPopup , ContentAssistant . LayoutManager . LAYOUT_CONTEXT_INFO_POPUP , frame . fVisibleOffset ) ; fContextInfoPopup . setVisible ( true ) ; fContentAssistSubjectControlAdapter . addKeyListener ( this ) ; } } else { fContentAssistant . layout ( ContentAssistant . LayoutManager . LAYOUT_CONTEXT_INFO_POPUP , frame . fVisibleOffset ) ; } } private IContextInformation [ ] computeContextInformation ( int offset ) { return fContentAssistSubjectControlAdapter . computeContextInformation ( fContentAssistant , offset ) ; } private String getErrorMessage ( ) { return fContentAssistant . getErrorMessage ( ) ; } private void createContextInfoPopup ( ) { if ( Helper . okToUse ( fContextInfoPopup ) ) { return ; } int shellStyle = SWT . TOOL ; int style = SWT . NONE ; GridLayout layout ; GridData gd ; Control control = fContentAssistSubjectControlAdapter . getControl ( ) ; Display display = control . getDisplay ( ) ; fContextInfoPopup = new Shell ( control . getShell ( ) , SWT . NO_FOCUS | SWT . ON_TOP | shellStyle ) ; fContextInfoPopup . setBackground ( display . getSystemColor ( SWT . COLOR_INFO_BACKGROUND ) ) ; Composite composite = fContextInfoPopup ; layout = new GridLayout ( 1 , false ) ; int border = ( ( shellStyle & SWT . NO_TRIM ) == 0 ) ? 0 : OUTER_BORDER ; layout . marginHeight = border ; layout . marginWidth = border ; composite . setLayout ( layout ) ; gd = new GridData ( GridData . FILL_BOTH ) ; composite . setLayoutData ( gd ) ; composite = new Composite ( composite , SWT . NONE ) ; layout = new GridLayout ( 1 , false ) ; layout . marginHeight = 1 ; layout . marginWidth = 1 ; layout . verticalSpacing = 3 ; composite . setLayout ( layout ) ; gd = new GridData ( GridData . FILL_BOTH ) ; composite . setLayoutData ( gd ) ; composite . setForeground ( display . getSystemColor ( SWT . COLOR_INFO_FOREGROUND ) ) ; composite . setBackground ( display . getSystemColor ( SWT . COLOR_INFO_BACKGROUND ) ) ; fContextInfoText = new StyledText ( composite , SWT . MULTI | SWT . READ_ONLY | style ) ; gd = new GridData ( GridData . BEGINNING | GridData . FILL_BOTH ) ; gd . horizontalIndent = INNER_BORDER ; gd . verticalIndent = INNER_BORDER ; fContextInfoText . setLayoutData ( gd ) ; fContextInfoText . setForeground ( display . getSystemColor ( SWT . COLOR_INFO_FOREGROUND ) ) ; fContextInfoText . setBackground ( display . getSystemColor ( SWT . COLOR_INFO_BACKGROUND ) ) ; fContextInfoText . setLocation ( 1 , 1 ) ; } private void resize ( ) { Point size = fContextInfoText . computeSize ( SWT . DEFAULT , SWT . DEFAULT , true ) ; size . x += 2 ; fContextInfoText . setSize ( size ) ; size . x += 8 ; size . y += 10 ; fContextInfoPopup . setSize ( size ) ; } private void hideContextInfoPopup ( ) { if ( Helper . okToUse ( fContextInfoPopup ) ) { int size = fContextFrameStack . size ( ) ; if ( size > 0 ) { fLastContext = ( ContextFrame ) fContextFrameStack . pop ( ) ; -- size ; } if ( size > 0 ) { ContextFrame current = ( ContextFrame ) fContextFrameStack . peek ( ) ; internalShowContextFrame ( current , false ) ; } else { fContentAssistant . removeContentAssistListener ( this , ContentAssistant . CONTEXT_INFO_POPUP ) ; if ( fContentAssistSubjectControlAdapter . getControl ( ) != null ) { fContentAssistSubjectControlAdapter . removeSelectionListener ( fTextWidgetSelectionListener ) ; fContentAssistSubjectControlAdapter . removeKeyListener ( this ) ; } fTextWidgetSelectionListener = null ; fContextInfoPopup . setVisible ( false ) ; fContextInfoPopup . dispose ( ) ; fContextInfoPopup = null ; if ( fTextPresentation != null ) { fTextPresentation . clear ( ) ; fTextPresentation = null ; } } } if ( fContextInfoPopup == null ) { fContentAssistant . contextInformationClosed ( ) ; } } private void createContextSelector ( ) { if ( Helper . okToUse ( fContextSelectorShell ) ) { return ; } Control control = fContentAssistSubjectControlAdapter . getControl ( ) ; fContextSelectorShell = new Shell ( control . getShell ( ) , SWT . ON_TOP | SWT . RESIZE ) ; GridLayout layout = new GridLayout ( ) ; layout . marginWidth = 0 ; layout . marginHeight = 0 ; fContextSelectorShell . setLayout ( layout ) ; fContextSelectorShell . setBackground ( control . getDisplay ( ) . getSystemColor ( SWT . COLOR_BLACK ) ) ; fContextSelectorTable = new Table ( fContextSelectorShell , SWT . H_SCROLL | SWT . V_SCROLL ) ; fContextSelectorTable . setLocation ( 1 , 1 ) ; GridData gd = new GridData ( GridData . FILL_BOTH ) ; gd . heightHint = fContextSelectorTable . getItemHeight ( ) * 10 ; gd . widthHint = 300 ; fContextSelectorTable . setLayoutData ( gd ) ; fContextSelectorShell . pack ( true ) ; Color c = fContentAssistant . getContextSelectorBackground ( ) ; if ( c == null ) { c = control . getDisplay ( ) . getSystemColor ( SWT . COLOR_INFO_BACKGROUND ) ; } fContextSelectorTable . setBackground ( c ) ; c = fContentAssistant . getContextSelectorForeground ( ) ; if ( c == null ) { c = control . getDisplay ( ) . getSystemColor ( SWT . COLOR_INFO_FOREGROUND ) ; } fContextSelectorTable . setForeground ( c ) ; fContextSelectorTable . addSelectionListener ( new SelectionListener ( ) { public void widgetSelected ( SelectionEvent e ) { } public void widgetDefaultSelected ( SelectionEvent e ) { insertSelectedContext ( ) ; hideContextSelector ( ) ; } } ) ; fPopupCloser . install ( fContentAssistant , fContextSelectorTable ) ; fContextSelectorTable . setHeaderVisible ( false ) ; fContentAssistant . addToLayout ( this , fContextSelectorShell , ContentAssistant . LayoutManager . LAYOUT_CONTEXT_SELECTOR , fContentAssistant . getSelectionOffset ( ) ) ; } private void insertSelectedContext ( ) { int i = fContextSelectorTable . getSelectionIndex ( ) ; if ( i < 0 || i >= fContextSelectorInput . length ) { return ; } int offset = fContentAssistSubjectControlAdapter . getSelectedRange ( ) . x ; internalShowContextInfo ( createContextFrame ( fContextSelectorInput [ i ] , offset ) ) ; } private void setContexts ( IContextInformation [ ] contexts ) { if ( Helper . okToUse ( fContextSelectorTable ) ) { fContextSelectorInput = contexts ; fContextSelectorTable . setRedraw ( false ) ; fContextSelectorTable . removeAll ( ) ; TableItem item ; IContextInformation t ; for ( int i = 0 ; i < contexts . length ; i ++ ) { t = contexts [ i ] ; item = new TableItem ( fContextSelectorTable , SWT . NULL ) ; if ( t . getImage ( ) != null ) { item . setImage ( t . getImage ( ) ) ; } item . setText ( t . getContextDisplayString ( ) ) ; } fContextSelectorTable . select ( 0 ) ; fContextSelectorTable . setRedraw ( true ) ; } } private void displayContextSelector ( ) { if ( fContentAssistant . addContentAssistListener ( this , ContentAssistant . CONTEXT_SELECTOR ) ) { fContextSelectorShell . setVisible ( true ) ; } } private void hideContextSelector ( ) { if ( Helper . okToUse ( fContextSelectorShell ) ) { fContentAssistant . removeContentAssistListener ( this , ContentAssistant . CONTEXT_SELECTOR ) ; fPopupCloser . uninstall ( ) ; fContextSelectorShell . setVisible ( false ) ; fContextSelectorShell . dispose ( ) ; fContextSelectorShell = null ; } if ( ! Helper . okToUse ( fContextInfoPopup ) ) { fContentAssistant . contextInformationClosed ( ) ; } } public boolean hasFocus ( ) { if ( Helper . okToUse ( fContextSelectorShell ) ) { return ( fContextSelectorShell . isFocusControl ( ) || fContextSelectorTable . isFocusControl ( ) ) ; } return false ; } public void hide ( ) { fContentAssistSubjectControlAdapter . removeKeyListener ( this ) ; hideContextSelector ( ) ; hideContextInfoPopup ( ) ; } public boolean isActive ( ) { return ( Helper . okToUse ( fContextInfoPopup ) || Helper . okToUse ( fContextSelectorShell ) ) ; } public boolean verifyKey ( VerifyEvent e ) { if ( Helper . okToUse ( fContextSelectorShell ) ) { return contextSelectorKeyPressed ( e ) ; } if ( Helper . okToUse ( fContextInfoPopup ) ) { return contextInfoPopupKeyPressed ( e ) ; } return true ; } private boolean contextSelectorKeyPressed ( VerifyEvent e ) { char key = e . character ; if ( key == 0 ) { int change ; int visibleRows = ( fContextSelectorTable . getSize ( ) . y / fContextSelectorTable . getItemHeight ( ) ) - 1 ; int selection = fContextSelectorTable . getSelectionIndex ( ) ; switch ( e . keyCode ) { case SWT . ARROW_UP : change = ( fContextSelectorTable . getSelectionIndex ( ) > 0 ? - 1 : 0 ) ; break ; case SWT . ARROW_DOWN : change = ( fContextSelectorTable . getSelectionIndex ( ) < fContextSelectorTable . getItemCount ( ) - 1 ? 1 : 0 ) ; break ; case SWT . PAGE_DOWN : change = visibleRows ; if ( selection + change >= fContextSelectorTable . getItemCount ( ) ) { change = fContextSelectorTable . getItemCount ( ) - selection ; } break ; case SWT . PAGE_UP : change = - visibleRows ; if ( selection + change < 0 ) { change = - selection ; } break ; case SWT . HOME : change = - selection ; break ; case SWT . END : change = fContextSelectorTable . getItemCount ( ) - selection ; break ; default : if ( e . keyCode != SWT . CAPS_LOCK && e . keyCode != SWT . MOD1 && e . keyCode != SWT . MOD2 && e . keyCode != SWT . MOD3 && e . keyCode != SWT . MOD4 ) { hideContextSelector ( ) ; } return true ; } fContextSelectorTable . setSelection ( selection + change ) ; fContextSelectorTable . showSelection ( ) ; e . doit = false ; return false ; } else if ( '\t' == key ) { e . doit = false ; fContextSelectorShell . setFocus ( ) ; return false ; } else if ( key == 0x1B ) { hideContextSelector ( ) ; } return true ; } private boolean contextInfoPopupKeyPressed ( KeyEvent e ) { char key = e . character ; if ( key == 0 ) { switch ( e . keyCode ) { case SWT . ARROW_LEFT : case SWT . ARROW_RIGHT : validateContextInformation ( ) ; break ; default : if ( e . keyCode != SWT . CAPS_LOCK && e . keyCode != SWT . MOD1 && e . keyCode != SWT . MOD2 && e . keyCode != SWT . MOD3 && e . keyCode != SWT . MOD4 ) { hideContextInfoPopup ( ) ; } break ; } } else if ( key == 0x1B ) { hideContextInfoPopup ( ) ; } else { validateContextInformation ( ) ; } return true ; } public void processEvent ( VerifyEvent event ) { if ( Helper . okToUse ( fContextSelectorShell ) ) { contextSelectorProcessEvent ( event ) ; } if ( Helper . okToUse ( fContextInfoPopup ) ) { contextInfoPopupProcessEvent ( event ) ; } } private void contextSelectorProcessEvent ( VerifyEvent e ) { if ( e . start == e . end && e . text != null && e . text . equals ( fLineDelimiter ) ) { e . doit = false ; insertSelectedContext ( ) ; } hideContextSelector ( ) ; } private void contextInfoPopupProcessEvent ( VerifyEvent e ) { if ( e . start != e . end && ( e . text == null || e . text . length ( ) == 0 ) ) { validateContextInformation ( ) ; } } private void validateContextInformation ( ) { if ( ! Helper . okToUse ( fContextInfoPopup ) ) { return ; } fContextInfoPopup . getDisplay ( ) . asyncExec ( new Runnable ( ) { private ContextFrame fFrame = ( ContextFrame ) fContextFrameStack . peek ( ) ; public void run ( ) { if ( ! fContextFrameStack . isEmpty ( ) && fFrame == fContextFrameStack . peek ( ) ) { int offset = fContentAssistSubjectControlAdapter . getSelectedRange ( ) . x ; while ( Helper . okToUse ( fContextInfoPopup ) && ! fContextFrameStack . isEmpty ( ) ) { ContextFrame top = ( ContextFrame ) fContextFrameStack . peek ( ) ; if ( top . fValidator == null || ! top . fValidator . isContextInformationValid ( offset ) ) { hideContextInfoPopup ( ) ; } else if ( top . fPresenter != null && top . fPresenter . updatePresentation ( offset , fTextPresentation ) ) { TextPresentation . applyTextPresentation ( fTextPresentation , fContextInfoText ) ; resize ( ) ; break ; } else { break ; } } } } } ) ; } public char getActivationKey ( ) { return fActivationKey ; } public void setActivationKey ( char activationKey ) { fActivationKey = activationKey ; } public void keyPressed ( KeyEvent e ) { } public void keyReleased ( KeyEvent e ) { int key = e . character ; if ( key == 13 ) { int offset = fContentAssistSubjectControlAdapter . getSelectedRange ( ) . x ; this . fContentAssistant . layout ( LayoutManager . LAYOUT_CONTEXT_INFO_POPUP , offset ) ; } } } 
=======
public class MimeTypeList extends AbstractDatatype { public static final MimeTypeList THE_INSTANCE = new MimeTypeList ( ) ; private enum State { WS_BEFORE_TYPE , IN_TYPE , ASTERISK_TYPE_SEEN , ASTERISK_AND_SLASH_SEEN , WS_BEFORE_COMMA , SLASH_SEEN , IN_SUBTYPE } private MimeTypeList ( ) { super ( ) ; } @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { State state = State . WS_BEFORE_TYPE ; for ( int i = 0 ; i < literal . length ( ) ; i ++ ) { char c = literal . charAt ( i ) ; switch ( state ) { case WS_BEFORE_TYPE : if ( isWhitespace ( c ) ) { continue ; } else if ( c == '*' ) { state = State . ASTERISK_TYPE_SEEN ; } else if ( isTokenChar ( c ) ) { state = State . IN_TYPE ; continue ; } else { throw newDatatypeException ( i , "Expected whitespace, a token character or “*” but saw " , c , " instead." ) ; } case ASTERISK_TYPE_SEEN : if ( c == '/' ) { state = State . ASTERISK_AND_SLASH_SEEN ; continue ; } else { throw newDatatypeException ( i , "Expected “/” but saw " , c , " instead." ) ; } case ASTERISK_AND_SLASH_SEEN : if ( c == '*' ) { state = State . WS_BEFORE_COMMA ; continue ; } else { throw newDatatypeException ( i , "Expected “*” but saw " , c , " instead." ) ; } case IN_TYPE : if ( c == '/' ) { state = State . SLASH_SEEN ; continue ; } else if ( isTokenChar ( c ) ) { continue ; } else { throw newDatatypeException ( i , "Expected a token character or “/” but saw " , c , " instead." ) ; } case SLASH_SEEN : if ( c == '*' ) { state = State . WS_BEFORE_COMMA ; continue ; } else if ( isTokenChar ( c ) ) { state = State . IN_SUBTYPE ; continue ; } else { throw newDatatypeException ( i , "Expected a token character or “*” but saw " , c , " instead." ) ; } case IN_SUBTYPE : if ( isWhitespace ( c ) ) { state = State . WS_BEFORE_COMMA ; continue ; } else if ( c == ',' ) { state = State . WS_BEFORE_TYPE ; continue ; } else if ( isTokenChar ( c ) ) { continue ; } else { throw newDatatypeException ( i , "Expected a token character, whitespace or a comma but saw " , c , " instead." ) ; } case WS_BEFORE_COMMA : if ( c == ',' ) { state = State . WS_BEFORE_TYPE ; continue ; } else if ( isWhitespace ( c ) ) { continue ; } else { throw newDatatypeException ( i , "Expected whitespace or a comma but saw " , c , " instead." ) ; } } } switch ( state ) { case IN_SUBTYPE : case WS_BEFORE_COMMA : return ; case ASTERISK_AND_SLASH_SEEN : throw newDatatypeException ( "Expected “*” but the literal ended." ) ; case ASTERISK_TYPE_SEEN : throw newDatatypeException ( "Expected “/” but the literal ended." ) ; case IN_TYPE : throw newDatatypeException ( "Expected “/” but the literal ended." ) ; case SLASH_SEEN : throw newDatatypeException ( "Expected subtype but the literal ended." ) ; case WS_BEFORE_TYPE : throw newDatatypeException ( "Expected a MIME type but the literal ended." ) ; } } private boolean isTokenChar ( char c ) { return ( c >= 33 && c <= 126 ) && ! ( c == '(' || c == ')' || c == '<' || c == '>' || c == '@' || c == ',' || c == ';' || c == ':' || c == '\\' || c == '\"' || c == '/' || c == '[' || c == ']' || c == '?' || c == '=' || c == '{' || c == '}' ) ; } @ Override public String getName ( ) { return "MIME type list" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
