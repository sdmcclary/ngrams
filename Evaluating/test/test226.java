<<<<<<< HEAD
public final class NormalizationChecker extends Checker { @ SuppressWarnings ( "deprecation" ) private static final UnicodeSet COMPOSING_CHARACTERS = ( UnicodeSet ) new UnicodeSet ( "[[:nfc_qc=maybe:][:^ccc=0:]]" ) . freeze ( ) ; private char [ ] buf = new char [ 128 ] ; private char [ ] bufHolder = null ; private int pos ; private boolean atStartOfRun ; private boolean alreadyComplainedAboutThisRun ; private final boolean sourceTextMode ; private static boolean isComposingCharOrSurrogate ( char c ) { if ( UCharacter . isHighSurrogate ( c ) || UCharacter . isLowSurrogate ( c ) ) { return true ; } return isComposingChar ( c ) ; } private static boolean isComposingChar ( int c ) { return COMPOSING_CHARACTERS . contains ( c ) ; } public static boolean startsWithComposingChar ( String str ) throws SAXException { if ( str . length ( ) == 0 ) { return false ; } int first32 ; char first = str . charAt ( 0 ) ; if ( UCharacter . isHighSurrogate ( first ) ) { try { char second = str . charAt ( 1 ) ; first32 = UCharacter . getCodePoint ( first , second ) ; } catch ( StringIndexOutOfBoundsException e ) { throw new SAXException ( "Malformed UTF-16!" ) ; } catch ( IllegalArgumentException e ) { throw new SAXException ( e . getMessage ( ) ) ; } } else { first32 = first ; } return isComposingChar ( first32 ) ; } public NormalizationChecker ( ) { this ( false ) ; } public NormalizationChecker ( boolean sourceTextMode ) { super ( ) ; this . sourceTextMode = sourceTextMode ; reset ( ) ; } public void reset ( ) { atStartOfRun = true ; alreadyComplainedAboutThisRun = false ; pos = 0 ; if ( bufHolder != null ) { buf = bufHolder ; bufHolder = null ; } } public void characters ( char [ ] ch , int start , int length ) throws SAXException { if ( alreadyComplainedAboutThisRun ) { return ; } if ( atStartOfRun ) { char c = ch [ start ] ; if ( pos == 1 ) { if ( isComposingChar ( UCharacter . getCodePoint ( buf [ 0 ] , c ) ) ) { warn ( "Text run starts with a composing character." ) ; } atStartOfRun = false ; } else { if ( length == 1 && UCharacter . isHighSurrogate ( c ) ) { buf [ 0 ] = c ; pos = 1 ; return ; } else { if ( UCharacter . isHighSurrogate ( c ) ) { if ( isComposingChar ( UCharacter . getCodePoint ( c , ch [ start + 1 ] ) ) ) { warn ( "Text run starts with a composing character." ) ; } } else { if ( isComposingCharOrSurrogate ( c ) ) { warn ( "Text run starts with a composing character." ) ; } } atStartOfRun = false ; } } } int i = start ; int stop = start + length ; if ( pos > 0 ) { while ( i < stop && isComposingCharOrSurrogate ( ch [ i ] ) ) { i ++ ; } appendToBuf ( ch , start , i ) ; if ( i == stop ) { return ; } else { if ( ! Normalizer . isNormalized ( buf , 0 , pos , Normalizer . NFC , 0 ) ) { errAboutTextRun ( ) ; } pos = 0 ; } } if ( i < stop ) { start = i ; i = stop - 1 ; while ( i > start && isComposingCharOrSurrogate ( ch [ i ] ) ) { i -- ; } if ( i > start ) { if ( ! Normalizer . isNormalized ( ch , start , i , Normalizer . NFC , 0 ) ) { errAboutTextRun ( ) ; } } appendToBuf ( ch , i , stop ) ; } } private void errAboutTextRun ( ) throws SAXException { if ( sourceTextMode ) { warn ( "Source text is not in Unicode Normalization Form C." ) ; } else { warn ( "Text run is not in Unicode Normalization Form C." ) ; } alreadyComplainedAboutThisRun = true ; } private void appendToBuf ( char [ ] ch , int start , int end ) { if ( start == end ) { return ; } int neededBufLen = pos + ( end - start ) ; if ( neededBufLen > buf . length ) { char [ ] newBuf = new char [ neededBufLen ] ; System . arraycopy ( buf , 0 , newBuf , 0 , pos ) ; if ( bufHolder == null ) { bufHolder = buf ; } buf = newBuf ; } System . arraycopy ( ch , start , buf , pos , end - start ) ; pos += ( end - start ) ; } public void endElement ( String uri , String localName , String qName ) throws SAXException { flush ( ) ; } public void processingInstruction ( String target , String data ) throws SAXException { flush ( ) ; if ( ! "" . equals ( target ) ) { if ( startsWithComposingChar ( target ) ) { warn ( "Processing instruction target starts with a composing character." ) ; } } if ( ! "" . equals ( data ) ) { if ( startsWithComposingChar ( data ) ) { warn ( "Processing instruction data starts with a composing character." ) ; } else if ( ! Normalizer . isNormalized ( data , Normalizer . NFC , 0 ) ) { warn ( "Processing instruction data in not in Unicode Normalization Form C." ) ; } } } public void startElement ( String uri , String localName , String qName , Attributes atts ) throws SAXException { flush ( ) ; if ( startsWithComposingChar ( localName ) ) { warn ( "Element name “ " + localName + "” starts with a composing character." ) ; } int len = atts . getLength ( ) ; for ( int i = 0 ; i < len ; i ++ ) { String name = atts . getLocalName ( i ) ; if ( startsWithComposingChar ( name ) ) { warn ( "Attribute name “ " + localName + "” starts with a composing character." ) ; } String value = atts . getValue ( i ) ; if ( ! "" . equals ( value ) ) { if ( startsWithComposingChar ( value ) ) { warn ( "The value of attribute “" + atts . getLocalName ( i ) + "”" + ( "" . equals ( atts . getURI ( i ) ) ? "" : " in namespace “" + atts . getURI ( i ) + "”" ) + " on element “" + localName + "” from namespace “" + uri + "” starts with a composing character." ) ; } else if ( ! Normalizer . isNormalized ( value , Normalizer . NFC , 0 ) ) { warn ( "The value of attribute “" + atts . getLocalName ( i ) + "”" + ( "" . equals ( atts . getURI ( i ) ) ? "" : " in namespace “" + atts . getURI ( i ) + "”" ) + " on element “" + localName + "” from namespace “" + uri + "” is not in Unicode Normalization Form C." ) ; } } } } public void startPrefixMapping ( String prefix , String uri ) throws SAXException { if ( startsWithComposingChar ( prefix ) ) { warn ( "Namespace prefix “ " + prefix + "” starts with a composing character." ) ; } if ( startsWithComposingChar ( uri ) ) { warn ( "Namespace URI “ " + uri + "” starts with a composing character." ) ; } } public void flush ( ) throws SAXException { if ( ! alreadyComplainedAboutThisRun && ! Normalizer . isNormalized ( buf , 0 , pos , Normalizer . NFC , 0 ) ) { errAboutTextRun ( ) ; } reset ( ) ; } } 
=======
public class Flag { private Flag ( ) { } public static final Flag PRESENT = new Flag ( ) ; } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
