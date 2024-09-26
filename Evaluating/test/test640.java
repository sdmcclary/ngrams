public class UnifiedViewer extends ProjectionViewer implements IUnifiedViewer { private boolean hotkeyActivated = false ; private TextPresentation presentation ; protected Map < String , ITextTripleClickStrategy > fTripleClickStrategies ; protected TripleClickConnector fTripleClickStrategyConnector ; public UnifiedViewer ( Composite parent , IVerticalRuler ruler , IOverviewRuler overviewRuler , boolean showsAnnotationOverview , int styles ) { super ( parent , ruler , overviewRuler , showsAnnotationOverview , styles ) ; } protected Layout createLayout ( ) { return new RulerLayout ( 0 ) ; } protected void shift ( boolean useDefaultPrefixes , boolean right ) { shift ( useDefaultPrefixes , right , false ) ; } protected StyledText createTextWidget ( Composite parent , int styles ) { return new StyledText ( parent , styles ) { boolean swap = false ; public void setLineBackground ( int startLine , int lineCount , Color background ) { swap = true ; super . setLineBackground ( startLine , lineCount , background ) ; swap = false ; } public boolean isListening ( int eventType ) { if ( swap && eventType == 3001 ) { return false ; } return super . isListening ( eventType ) ; } public void invokeAction ( int action ) { if ( getWordWrap ( ) && ST . LINE_DOWN == action ) { int previous = getCaretOffset ( ) ; super . invokeAction ( action ) ; if ( previous == getCaretOffset ( ) ) { int line = getLineAtOffset ( previous ) ; if ( line + 1 < getLineCount ( ) ) { setCaretOffset ( getOffsetAtLine ( line + 1 ) ) ; } } } else { super . invokeAction ( action ) ; } } } ; } protected void shift ( boolean useDefaultPrefixes , boolean right , boolean ignoreWhitespace ) { if ( fUndoManager != null ) { fUndoManager . beginCompoundChange ( ) ; } setRedraw ( false ) ; startSequentialRewriteMode ( true ) ; IDocument d = getDocument ( ) ; Map partitioners = null ; try { Point selection = getSelectedRange ( ) ; IRegion block = getTextBlockFromSelection ( selection ) ; ITypedRegion [ ] regions = TextUtilities . computePartitioning ( d , getDocumentPartitioning ( ) , block . getOffset ( ) , block . getLength ( ) , false ) ; int lineCount = 0 ; int [ ] lines = new int [ regions . length * 2 ] ; for ( int i = 0 , j = 0 ; i < regions . length ; i ++ , j += 2 ) { lines [ j ] = getFirstCompleteLineOfRegion ( regions [ i ] ) ; int length = regions [ i ] . getLength ( ) ; int offset = regions [ i ] . getOffset ( ) + length ; if ( length > 0 ) { offset -- ; } lines [ j + 1 ] = ( lines [ j ] == - 1 ? - 1 : d . getLineOfOffset ( offset ) ) ; lineCount += lines [ j + 1 ] - lines [ j ] + 1 ; } if ( lineCount >= 20 ) { partitioners = TextUtilities . removeDocumentPartitioners ( d ) ; } IPositionUpdater positionUpdater = new ShiftPositionUpdater ( SHIFTING ) ; Position rememberedSelection = new Position ( selection . x , selection . y ) ; d . addPositionCategory ( SHIFTING ) ; d . addPositionUpdater ( positionUpdater ) ; try { d . addPosition ( SHIFTING , rememberedSelection ) ; } catch ( BadPositionCategoryException ex ) { } Map map = ( useDefaultPrefixes ? fDefaultPrefixChars : fIndentChars ) ; for ( int i = 0 , j = 0 ; i < regions . length ; i ++ , j += 2 ) { String [ ] prefixes = ( String [ ] ) selectContentTypePlugin ( regions [ i ] . getType ( ) , map ) ; if ( prefixes != null && prefixes . length > 0 && lines [ j ] >= 0 && lines [ j + 1 ] >= 0 ) { if ( right ) { shiftRight ( lines [ j ] , lines [ j + 1 ] , prefixes [ 0 ] , d ) ; } else { shiftLeft ( lines [ j ] , lines [ j + 1 ] , prefixes , ignoreWhitespace , d ) ; } } } setSelectedRange ( rememberedSelection . getOffset ( ) , rememberedSelection . getLength ( ) ) ; try { d . removePositionUpdater ( positionUpdater ) ; d . removePositionCategory ( SHIFTING ) ; } catch ( BadPositionCategoryException ex ) { } } catch ( BadLocationException x ) { } finally { if ( partitioners != null ) { TextUtilities . addDocumentPartitioners ( d , partitioners ) ; } stopSequentialRewriteMode ( ) ; setRedraw ( true ) ; if ( fUndoManager != null ) { fUndoManager . endCompoundChange ( ) ; } } } public static void shiftRight ( int startLine , int endLine , String prefix , IDocument document ) { try { while ( startLine <= endLine ) { document . replace ( document . getLineOffset ( startLine ++ ) , 0 , prefix ) ; } } catch ( BadLocationException x ) { if ( TRACE_ERRORS ) { IdeLog . logError ( UnifiedEditorsPlugin . getDefault ( ) , "TextViewer.shiftRight: BadLocationException" , x ) ; } } } public static void shiftLeft ( int startLine , int endLine , String [ ] prefixes , boolean ignoreWhitespace , IDocument document ) { try { IRegion [ ] occurrences = new IRegion [ endLine - startLine + 1 ] ; for ( int i = 0 ; i < occurrences . length ; i ++ ) { IRegion line = document . getLineInformation ( startLine + i ) ; String text = document . get ( line . getOffset ( ) , line . getLength ( ) ) ; int index = - 1 ; int [ ] found = TextUtilities . indexOf ( prefixes , text , 0 ) ; if ( found [ 0 ] != - 1 ) { if ( ignoreWhitespace ) { String s = document . get ( line . getOffset ( ) , found [ 0 ] ) ; s = s . trim ( ) ; if ( s . length ( ) == 0 ) { index = line . getOffset ( ) + found [ 0 ] ; } } else if ( found [ 0 ] == 0 ) { index = line . getOffset ( ) ; } } if ( index > - 1 ) { int length = prefixes [ found [ 1 ] ] . length ( ) ; if ( length == 0 && ! ignoreWhitespace && line . getLength ( ) > 0 ) { occurrences [ i ] = new Region ( index , 0 ) ; } else { occurrences [ i ] = new Region ( index , length ) ; } } else { occurrences [ i ] = new Region ( index , 0 ) ; } } int decrement = 0 ; for ( int i = 0 ; i < occurrences . length ; i ++ ) { IRegion r = occurrences [ i ] ; if ( r . getLength ( ) == 0 ) { continue ; } document . replace ( r . getOffset ( ) - decrement , r . getLength ( ) , StringUtils . EMPTY ) ; decrement += r . getLength ( ) ; } } catch ( BadLocationException x ) { if ( TRACE_ERRORS ) { Trace . info ( "TextViewer.shiftLeft: BadLocationException" ) ; } } } private IRegion getTextBlockFromSelection ( Point selection ) { try { IDocument document = getDocument ( ) ; IRegion line = document . getLineInformationOfOffset ( selection . x ) ; int length = selection . y == 0 ? line . getLength ( ) : selection . y + ( selection . x - line . getOffset ( ) ) ; return new Region ( line . getOffset ( ) , length ) ; } catch ( BadLocationException x ) { } return null ; } private int getFirstCompleteLineOfRegion ( IRegion region ) { try { IDocument d = getDocument ( ) ; int startLine = d . getLineOfOffset ( region . getOffset ( ) ) ; int offset = d . getLineOffset ( startLine ) ; if ( offset >= region . getOffset ( ) ) { return startLine ; } offset = d . getLineOffset ( startLine + 1 ) ; return ( offset > region . getOffset ( ) + region . getLength ( ) ? - 1 : startLine + 1 ) ; } catch ( BadLocationException x ) { } return - 1 ; } private Object selectContentTypePlugin ( String type , Map plugins ) { if ( plugins == null ) { return null ; } return plugins . get ( type ) ; } static class ShiftPositionUpdater extends DefaultPositionUpdater { protected ShiftPositionUpdater ( String category ) { super ( category ) ; } protected void adaptToInsert ( ) { int myStart = fPosition . offset ; int myEnd = fPosition . offset + fPosition . length - 1 ; myEnd = Math . max ( myStart , myEnd ) ; int yoursStart = fOffset ; int yoursEnd = fOffset + fReplaceLength - 1 ; yoursEnd = Math . max ( yoursStart , yoursEnd ) ; if ( myEnd < yoursStart ) { return ; } if ( myStart <= yoursStart ) { fPosition . length += fReplaceLength ; return ; } if ( myStart > yoursStart ) { fPosition . offset += fReplaceLength ; } } } public boolean isHotkeyActivated ( ) { return hotkeyActivated ; } public void setHotkeyActivated ( boolean value ) { hotkeyActivated = value ; } public void closeContentAssist ( ) { if ( fContentAssistant != null && fContentAssistant instanceof IUnifiedContentAssistant ) { ( ( IUnifiedContentAssistant ) fContentAssistant ) . hide ( ) ; } } public void activatePlugins ( ) { super . activatePlugins ( ) ; } public void setTextTripleClickStrategy ( ITextTripleClickStrategy strategy , String contentType ) { if ( strategy != null ) { if ( fTripleClickStrategies == null ) { fTripleClickStrategies = new HashMap < String , ITextTripleClickStrategy > ( ) ; } fTripleClickStrategies . put ( contentType , strategy ) ; } else if ( fTripleClickStrategies != null ) { fTripleClickStrategies . remove ( contentType ) ; } } public void configure ( SourceViewerConfiguration configuration ) { super . configure ( configuration ) ; if ( configuration instanceof UnifiedConfiguration ) { UnifiedConfiguration conf = ( UnifiedConfiguration ) configuration ; String [ ] types = configuration . getConfiguredContentTypes ( this ) ; for ( int i = 0 ; i < types . length ; i ++ ) { String t = types [ i ] ; setTextTripleClickStrategy ( conf . getTripleClickStrategy ( this , t ) , t ) ; } } activateTripleClickStrategies ( ) ; } private void activateTripleClickStrategies ( ) { if ( fTripleClickStrategies != null && ! fTripleClickStrategies . isEmpty ( ) && fTripleClickStrategyConnector == null ) { fTripleClickStrategyConnector = new TripleClickConnector ( ) { @ Override public void mouseTripleClick ( MouseEvent e ) { ITextTripleClickStrategy s = ( ITextTripleClickStrategy ) selectContentTypePlugin ( getSelectedRange ( ) . x , fTripleClickStrategies ) ; s . tripleClicked ( UnifiedViewer . this ) ; } } ; getTextWidget ( ) . addMouseListener ( fTripleClickStrategyConnector ) ; } } public void changeTextPresentation ( TextPresentation presentation , boolean controlRedraw ) { super . changeTextPresentation ( presentation , controlRedraw ) ; this . presentation = presentation ; } public TextPresentation getTextPresentation ( ) { return presentation ; } } 