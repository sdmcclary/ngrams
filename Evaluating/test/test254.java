public class Segment { int begin ; int end ; Source source ; private static final String WHITESPACE = " \n\r\t" ; public Segment ( Source source , int begin , int end ) { this ( begin , end ) ; if ( source == null ) throw new IllegalArgumentException ( "source argument must not be null" ) ; this . source = source ; } Segment ( int begin , int end ) { if ( begin == - 1 || end == - 1 || begin > end ) throw new IllegalArgumentException ( ) ; this . begin = begin ; this . end = end ; } Segment ( ) { } public final int getBegin ( ) { return begin ; } public final int getEnd ( ) { return end ; } public final boolean equals ( Object object ) { if ( object == null || ! ( object instanceof Segment ) ) return false ; Segment segment = ( Segment ) object ; return segment . begin == begin && segment . end == end && segment . source == source ; } public int hashCode ( ) { return begin + end ; } public final int length ( ) { return end - begin ; } public final boolean encloses ( Segment segment ) { return begin <= segment . begin && end >= segment . end ; } public final boolean encloses ( int pos ) { return begin <= pos && pos < end ; } public boolean isComment ( ) { return false ; } public String getSourceText ( ) { return source . getSourceText ( ) . substring ( begin , end ) ; } public final String getSourceTextNoWhitespace ( ) { StringBuffer sb = new StringBuffer ( ) ; int i = begin ; boolean lastWasWhitespace = true ; boolean isWhitespace = false ; while ( i < end ) { char c = source . getSourceText ( ) . charAt ( i ++ ) ; if ( isWhitespace = isWhiteSpace ( c ) ) { if ( ! lastWasWhitespace ) sb . append ( ' ' ) ; } else { sb . append ( c ) ; } lastWasWhitespace = isWhitespace ; } if ( isWhitespace ) sb . setLength ( Math . max ( 0 , sb . length ( ) - 1 ) ) ; return sb . toString ( ) ; } public final List findWords ( ) { ArrayList words = new ArrayList ( ) ; int wordBegin = - 1 ; for ( int i = begin ; i < end ; i ++ ) { if ( isWhiteSpace ( source . getSourceText ( ) . charAt ( i ) ) ) { if ( wordBegin == - 1 ) continue ; words . add ( new Segment ( source , wordBegin , i ) ) ; wordBegin = - 1 ; } else { if ( wordBegin == - 1 ) wordBegin = i ; } } if ( wordBegin != - 1 ) words . add ( new Segment ( source , wordBegin , end ) ) ; return words ; } public List findAllStartTags ( ) { return findAllStartTags ( null ) ; } public List findAllStartTags ( String name ) { if ( name != null ) name = name . toLowerCase ( ) ; StartTag startTag = findNextStartTag ( begin , name ) ; if ( startTag == null ) return Collections . EMPTY_LIST ; ArrayList list = new ArrayList ( ) ; do { list . add ( startTag ) ; startTag = findNextStartTag ( startTag . end , name ) ; } while ( startTag != null ) ; return list ; } public List findAllComments ( ) { return findAllStartTags ( SpecialTag . COMMENT . getName ( ) ) ; } public List findAllElements ( ) { return findAllElements ( null ) ; } public List findAllElements ( String name ) { if ( name != null ) name = name . toLowerCase ( ) ; List startTags = findAllStartTags ( name ) ; if ( startTags . isEmpty ( ) ) return Collections . EMPTY_LIST ; ArrayList elements = new ArrayList ( startTags . size ( ) ) ; for ( Iterator i = startTags . iterator ( ) ; i . hasNext ( ) ; ) { StartTag startTag = ( StartTag ) i . next ( ) ; Element element = startTag . getElement ( ) ; if ( element . end > end ) break ; elements . add ( element ) ; } return elements ; } public List findAllCharacterReferences ( ) { CharacterReference characterReference = findNextCharacterReference ( begin ) ; if ( characterReference == null ) return Collections . EMPTY_LIST ; ArrayList list = new ArrayList ( ) ; do { list . add ( characterReference ) ; characterReference = findNextCharacterReference ( characterReference . end ) ; } while ( characterReference != null ) ; return list ; } public FormFields findFormFields ( ) { return FormFields . construct ( this ) ; } public Attributes parseAttributes ( ) { return source . parseAttributes ( begin , end ) ; } public void ignoreWhenParsing ( ) { source . ignoreWhenParsing ( begin , end ) ; } public static final boolean isWhiteSpace ( char c ) { return WHITESPACE . indexOf ( c ) != - 1 ; } public String toString ( ) { return "(" + begin + ',' + end + ')' ; } static boolean isIdentifierStart ( char c ) { return Character . isLetter ( c ) || c == '_' || c == ':' ; } static boolean isIdentifierPart ( char c ) { return Character . isLetterOrDigit ( c ) || c == '.' || c == '-' || c == '_' || c == ':' ; } private StartTag findNextStartTag ( int pos , String name ) { StartTag startTag = source . findNextStartTag ( pos , name ) ; if ( startTag == null || startTag . end > end ) return null ; return startTag ; } private CharacterReference findNextCharacterReference ( int pos ) { CharacterReference characterReference = source . findNextCharacterReference ( pos ) ; if ( characterReference == null || characterReference . end > end ) return null ; return characterReference ; } } 