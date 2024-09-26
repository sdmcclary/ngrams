public class SpellCheckIterator implements ISpellCheckIterator { protected final String fContent ; private final String fDelimiter ; protected String fLastToken = null ; protected int fNext = 1 ; protected final int fOffset ; private int fPredecessor ; protected int fPrevious = 0 ; private final LinkedList fSentenceBreaks = new LinkedList ( ) ; private boolean fStartsSentence = false ; protected int fSuccessor ; private final BreakIterator fWordIterator ; private boolean fIsIgnoringSingleLetters ; public SpellCheckIterator ( IDocument document , IRegion region , Locale locale ) { this ( document , region , locale , BreakIterator . getWordInstance ( locale ) ) ; } public SpellCheckIterator ( IDocument document , IRegion region , Locale locale , BreakIterator breakIterator ) { this . fOffset = region . getOffset ( ) ; this . fWordIterator = breakIterator ; this . fDelimiter = TextUtilities . getDefaultLineDelimiter ( document ) ; String content ; try { content = document . get ( region . getOffset ( ) , region . getLength ( ) ) ; } catch ( final Exception exception ) { content = "" ; } this . fContent = content ; this . fWordIterator . setText ( content ) ; this . fPredecessor = this . fWordIterator . first ( ) ; this . fSuccessor = this . fWordIterator . next ( ) ; final BreakIterator iterator = BreakIterator . getSentenceInstance ( locale ) ; iterator . setText ( content ) ; int offset = iterator . current ( ) ; while ( offset != BreakIterator . DONE ) { this . fSentenceBreaks . add ( new Integer ( offset ) ) ; offset = iterator . next ( ) ; } } public void setIgnoreSingleLetters ( boolean state ) { this . fIsIgnoringSingleLetters = state ; } public final int getBegin ( ) { return this . fPrevious + this . fOffset ; } public final int getEnd ( ) { return this . fNext + this . fOffset - 1 ; } public final boolean hasNext ( ) { return this . fSuccessor != BreakIterator . DONE ; } protected final boolean isAlphaNumeric ( final int begin , final int end ) { char character = 0 ; boolean letter = false ; for ( int index = begin ; index < end ; index ++ ) { character = this . fContent . charAt ( index ) ; if ( Character . isLetter ( character ) ) { letter = true ; } if ( ! Character . isLetterOrDigit ( character ) ) { return false ; } } return letter ; } protected final boolean isToken ( final String [ ] tags ) { return this . isToken ( this . fLastToken , tags ) ; } protected final boolean isToken ( final String token , final String [ ] tags ) { if ( token != null ) { for ( int index = 0 ; index < tags . length ; index ++ ) { if ( token . equals ( tags [ index ] ) ) { return true ; } } } return false ; } protected final boolean isSingleLetter ( final int begin ) { if ( ! Character . isLetter ( this . fContent . charAt ( begin ) ) ) { return false ; } if ( ( begin > 0 ) && ! Character . isWhitespace ( this . fContent . charAt ( begin - 1 ) ) ) { return false ; } if ( ( begin < this . fContent . length ( ) - 1 ) && ! Character . isWhitespace ( this . fContent . charAt ( begin + 1 ) ) ) { return false ; } return true ; } protected final boolean isUrlToken ( final int begin ) { for ( int index = 0 ; index < DefaultSpellChecker . URL_PREFIXES . length ; index ++ ) { if ( this . fContent . startsWith ( DefaultSpellChecker . URL_PREFIXES [ index ] , begin ) ) { return true ; } } return false ; } protected final boolean isWhitespace ( final int begin , final int end ) { for ( int index = begin ; index < end ; index ++ ) { if ( ! Character . isWhitespace ( this . fContent . charAt ( index ) ) ) { return false ; } } return true ; } public Object next ( ) { String token = this . nextToken ( ) ; while ( ( token == null ) && ( this . fSuccessor != BreakIterator . DONE ) ) { token = this . nextToken ( ) ; } this . fLastToken = token ; return token ; } protected final void nextBreak ( ) { this . fNext = this . fSuccessor ; this . fPredecessor = this . fSuccessor ; this . fSuccessor = this . fWordIterator . next ( ) ; } protected final int nextSentence ( ) { return ( ( Integer ) this . fSentenceBreaks . getFirst ( ) ) . intValue ( ) ; } protected String nextToken ( ) { String token = null ; this . fPrevious = this . fPredecessor ; this . fStartsSentence = false ; this . nextBreak ( ) ; boolean update = false ; if ( this . fNext - this . fPrevious > 0 ) { if ( ( this . fSuccessor != BreakIterator . DONE ) && ( this . fContent . charAt ( this . fPrevious ) == IJavaDocTagConstants . JAVADOC_TAG_PREFIX ) ) { this . nextBreak ( ) ; if ( Character . isLetter ( this . fContent . charAt ( this . fPrevious + 1 ) ) ) { update = true ; token = this . fContent . substring ( this . fPrevious , this . fNext ) ; } else { this . fPredecessor = this . fNext ; } } else if ( ( this . fSuccessor != BreakIterator . DONE ) && ( this . fContent . charAt ( this . fPrevious ) == IHtmlTagConstants . HTML_TAG_PREFIX ) && ( Character . isLetter ( this . fContent . charAt ( this . fNext ) ) || ( this . fContent . charAt ( this . fNext ) == '/' ) ) ) { if ( this . fContent . startsWith ( IHtmlTagConstants . HTML_CLOSE_PREFIX , this . fPrevious ) ) { this . nextBreak ( ) ; } this . nextBreak ( ) ; if ( ( this . fSuccessor != BreakIterator . DONE ) && ( this . fContent . charAt ( this . fNext ) == IHtmlTagConstants . HTML_TAG_POSTFIX ) ) { this . nextBreak ( ) ; if ( this . fSuccessor != BreakIterator . DONE ) { update = true ; token = this . fContent . substring ( this . fPrevious , this . fNext ) ; } } } else if ( ( this . fSuccessor != BreakIterator . DONE ) && ( this . fContent . charAt ( this . fPrevious ) == IHtmlTagConstants . HTML_ENTITY_START ) && ( Character . isLetter ( this . fContent . charAt ( this . fNext ) ) ) ) { this . nextBreak ( ) ; if ( ( this . fSuccessor != BreakIterator . DONE ) && ( this . fContent . charAt ( this . fNext ) == IHtmlTagConstants . HTML_ENTITY_END ) ) { this . nextBreak ( ) ; if ( this . isToken ( this . fContent . substring ( this . fPrevious , this . fNext ) , IHtmlTagConstants . HTML_ENTITY_CODES ) ) { this . skipTokens ( this . fPrevious , IHtmlTagConstants . HTML_ENTITY_END ) ; update = true ; } else { token = this . fContent . substring ( this . fPrevious , this . fNext ) ; } } else { token = this . fContent . substring ( this . fPrevious , this . fNext ) ; } update = true ; } else if ( ! this . isWhitespace ( this . fPrevious , this . fNext ) && this . isAlphaNumeric ( this . fPrevious , this . fNext ) ) { if ( this . isUrlToken ( this . fPrevious ) ) { this . skipTokens ( this . fPrevious , ' ' ) ; } else if ( this . isToken ( IJavaDocTagConstants . JAVADOC_PARAM_TAGS ) ) { this . fLastToken = null ; } else if ( this . isToken ( IJavaDocTagConstants . JAVADOC_REFERENCE_TAGS ) ) { this . fLastToken = null ; this . skipTokens ( this . fPrevious , this . fDelimiter . charAt ( 0 ) ) ; } else if ( ( this . fNext - this . fPrevious > 1 ) || ( this . isSingleLetter ( this . fPrevious ) && ! this . fIsIgnoringSingleLetters ) ) { token = this . fContent . substring ( this . fPrevious , this . fNext ) ; } update = true ; } } if ( update && ( this . fSentenceBreaks . size ( ) > 0 ) ) { if ( this . fPrevious >= this . nextSentence ( ) ) { while ( ( this . fSentenceBreaks . size ( ) > 0 ) && ( this . fPrevious >= this . nextSentence ( ) ) ) { this . fSentenceBreaks . removeFirst ( ) ; } this . fStartsSentence = ( this . fLastToken == null ) || ( token != null ) ; } } return token ; } public final void remove ( ) { throw new UnsupportedOperationException ( ) ; } protected final void skipTokens ( final int begin , final char stop ) { int end = begin ; while ( ( end < this . fContent . length ( ) ) && ( this . fContent . charAt ( end ) != stop ) ) { end ++ ; } if ( end < this . fContent . length ( ) ) { this . fNext = end ; this . fPredecessor = this . fNext ; this . fSuccessor = this . fWordIterator . following ( this . fNext ) ; } else { this . fSuccessor = BreakIterator . DONE ; } } public final boolean startsSentence ( ) { return this . fStartsSentence ; } } 