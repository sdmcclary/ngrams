<<<<<<< HEAD
public class SourceWriter { private StringBuffer _buffer ; private String _indentText ; private String _currentIndent ; private static final String DEFAULT_NEWLINE = System . getProperty ( "line.separator" ) ; private String newLine = DEFAULT_NEWLINE ; public SourceWriter ( ) { this . _buffer = new StringBuffer ( ) ; this . _indentText = "    " ; this . _currentIndent = "" ; } public StringBuffer getBuffer ( ) { return this . _buffer ; } public String getIndentText ( ) { return this . _currentIndent ; } public SourceWriter ( int initialIndent , String indent , int tabSize ) { this . _buffer = new StringBuffer ( ) ; this . _indentText = indent ; StringBuffer bf = new StringBuffer ( ) ; if ( indent . length ( ) == 1 && indent . charAt ( 0 ) == '\t' ) { if ( tabSize == 0 ) { tabSize = 1 ; } int tabCount = initialIndent / tabSize ; for ( int a = 0 ; a < tabCount ; a ++ ) { bf . append ( '\t' ) ; } for ( int a = 0 ; a < initialIndent % tabSize ; a ++ ) { bf . append ( ' ' ) ; } } else { for ( int a = 0 ; a < initialIndent ; a ++ ) { bf . append ( ' ' ) ; } } this . _currentIndent = bf . toString ( ) ; } public SourceWriter decreaseIndent ( ) { if ( this . _currentIndent . length ( ) > 0 ) { int currentLength = this . _currentIndent . length ( ) ; int indentTextLength = this . _indentText . length ( ) ; this . _currentIndent = this . _currentIndent . substring ( 0 , currentLength - indentTextLength ) ; } return this ; } public SourceWriter increaseIndent ( ) { this . _currentIndent += this . _indentText ; return this ; } public void setCurrentIndentLevel ( int level ) { this . _currentIndent = "" ; for ( int i = 0 ; i < level ; i ++ ) { increaseIndent ( ) ; } } public int getIndentLevel ( ) { if ( this . _indentText . length ( ) == 0 ) { return 0 ; } else { return this . _currentIndent . length ( ) / this . _indentText . length ( ) ; } } public static String join ( String delimiter , String [ ] items ) { int length = items . length ; String result = "" ; if ( length > 0 ) { StringBuffer sb = new StringBuffer ( ) ; for ( int i = 0 ; i < length - 1 ; i ++ ) { sb . append ( items [ i ] ) . append ( delimiter ) ; } sb . append ( items [ length - 1 ] ) ; result = sb . toString ( ) ; } return result ; } public SourceWriter print ( char c ) { this . _buffer . append ( c ) ; return this ; } public SourceWriter print ( String text ) { this . _buffer . append ( text ) ; return this ; } public int getCurrentIndentationLevel ( ) { int pos = 0 ; for ( int a = this . _buffer . length ( ) - 1 ; a >= 0 ; a -- ) { char charAt = this . _buffer . charAt ( a ) ; if ( charAt == '\n' || charAt == '\r' ) { break ; } pos ++ ; if ( ! Character . isWhitespace ( charAt ) ) { pos = 0 ; } } return pos ; } public String getCurrentIndentationString ( ) { int pos = this . _buffer . length ( ) ; int startLine = 0 ; for ( int a = this . _buffer . length ( ) - 1 ; a >= 0 ; a -- ) { char charAt = this . _buffer . charAt ( a ) ; if ( charAt == '\n' || charAt == '\r' ) { startLine = a + 1 ; break ; } if ( ! Character . isWhitespace ( charAt ) ) { pos = a ; } } if ( _buffer . length ( ) == 0 ) { return "" ; } return _buffer . substring ( startLine , pos ) ; } public int getCurrentIndentLevel ( ) { int pos = 0 ; for ( int a = this . _buffer . length ( ) - 1 ; a >= 0 ; a -- ) { char charAt = this . _buffer . charAt ( a ) ; if ( charAt == '\n' || charAt == '\r' ) { break ; } pos ++ ; } return pos ; } public SourceWriter printWithIndent ( String text ) { this . _buffer . append ( this . _currentIndent ) . append ( text ) ; return this ; } public SourceWriter printIndent ( ) { this . _buffer . append ( this . _currentIndent ) ; return this ; } public SourceWriter println ( ) { this . println ( "" ) ; return this ; } public SourceWriter println ( String text ) { this . _buffer . append ( text ) . append ( newLine ) ; return this ; } public SourceWriter printlnWithIndent ( String text ) { this . _buffer . append ( this . _currentIndent ) . append ( text ) . append ( newLine ) ; return this ; } public static char [ ] splice ( char [ ] source , char [ ] insertText , int insertOffset , int removeLength ) { if ( insertOffset < 0 ) { throw new InvalidParameterException ( Messages . SourceWriter_Offset_Below_Zero + insertOffset ) ; } if ( removeLength < 0 ) { throw new InvalidParameterException ( Messages . SourceWriter_Remove_Length_Below_Zero + removeLength ) ; } int sourceLength = ( source != null ) ? source . length : 0 ; int postRemoveIndex = insertOffset + removeLength ; if ( postRemoveIndex > sourceLength ) { throw new InvalidParameterException ( Messages . SourceWriter_Remove_Beyond_Length ) ; } int insertLength = ( insertText != null ) ? insertText . length : 0 ; char [ ] result = new char [ sourceLength - removeLength + insertLength ] ; if ( insertOffset > 0 ) { System . arraycopy ( source , 0 , result , 0 , insertOffset ) ; } if ( insertLength > 0 ) { System . arraycopy ( insertText , 0 , result , insertOffset , insertLength ) ; } if ( insertOffset + removeLength < sourceLength ) { System . arraycopy ( source , postRemoveIndex , result , insertOffset + insertLength , sourceLength - postRemoveIndex ) ; } return result ; } public String toString ( ) { return this . _buffer . toString ( ) ; } public void setLineDelimeter ( String separator ) { this . newLine = separator ; } public String getLineDelimeter ( ) { return this . newLine ; } public String getIndentString ( ) { return this . _currentIndent ; } public int getLength ( ) { return _buffer . length ( ) ; } } 
=======
public class SoapEnvelope { public static final int VER10 = 100 ; public static final int VER11 = 110 ; public static final int VER12 = 120 ; public static final String ENV2001 = "http://www.w3.org/2001/12/soap-envelope" ; public static final String ENC2001 = "http://www.w3.org/2001/12/soap-encoding" ; public static final String ENV = "http://schemas.xmlsoap.org/soap/envelope/" ; public static final String ENC = "http://schemas.xmlsoap.org/soap/encoding/" ; public static final String XSD = "http://www.w3.org/2001/XMLSchema" ; public static final String XSI = "http://www.w3.org/2001/XMLSchema-instance" ; public static final String XSD1999 = "http://www.w3.org/1999/XMLSchema" ; public static final String XSI1999 = "http://www.w3.org/1999/XMLSchema-instance" ; public static boolean stringToBoolean ( String booleanAsString ) { if ( booleanAsString == null ) return false ; booleanAsString = booleanAsString . trim ( ) . toLowerCase ( ) ; return ( booleanAsString . equals ( "1" ) || booleanAsString . equals ( "true" ) ) ; } public Object bodyIn ; public Object bodyOut ; public Element [ ] headerIn ; public Element [ ] headerOut ; public String encodingStyle ; public int version ; public String env ; public String enc ; public String xsi ; public String xsd ; public SoapEnvelope ( int version ) { this . version = version ; if ( version == SoapEnvelope . VER10 ) { xsi = SoapEnvelope . XSI1999 ; xsd = SoapEnvelope . XSD1999 ; } else { xsi = SoapEnvelope . XSI ; xsd = SoapEnvelope . XSD ; } if ( version < SoapEnvelope . VER12 ) { enc = SoapEnvelope . ENC ; env = SoapEnvelope . ENV ; } else { enc = SoapEnvelope . ENC2001 ; env = SoapEnvelope . ENV2001 ; } } public void parse ( XmlPullParser parser ) throws IOException , XmlPullParserException { parser . nextTag ( ) ; parser . require ( XmlPullParser . START_TAG , env , "Envelope" ) ; encodingStyle = parser . getAttributeValue ( env , "encodingStyle" ) ; parser . nextTag ( ) ; if ( parser . getEventType ( ) == XmlPullParser . START_TAG && parser . getNamespace ( ) . equals ( env ) && parser . getName ( ) . equals ( "Header" ) ) { parseHeader ( parser ) ; parser . require ( XmlPullParser . END_TAG , env , "Header" ) ; parser . nextTag ( ) ; } parser . require ( XmlPullParser . START_TAG , env , "Body" ) ; encodingStyle = parser . getAttributeValue ( env , "encodingStyle" ) ; parseBody ( parser ) ; parser . require ( XmlPullParser . END_TAG , env , "Body" ) ; parser . nextTag ( ) ; parser . require ( XmlPullParser . END_TAG , env , "Envelope" ) ; } public void parseHeader ( XmlPullParser parser ) throws IOException , XmlPullParserException { parser . nextTag ( ) ; Node headers = new Node ( ) ; headers . parse ( parser ) ; int count = 0 ; for ( int i = 0 ; i < headers . getChildCount ( ) ; i ++ ) { Element child = headers . getElement ( i ) ; if ( child != null ) count ++ ; } headerIn = new Element [ count ] ; count = 0 ; for ( int i = 0 ; i < headers . getChildCount ( ) ; i ++ ) { Element child = headers . getElement ( i ) ; if ( child != null ) headerIn [ count ++ ] = child ; } } public void parseBody ( XmlPullParser parser ) throws IOException , XmlPullParserException { parser . nextTag ( ) ; if ( parser . getEventType ( ) == XmlPullParser . START_TAG && parser . getNamespace ( ) . equals ( env ) && parser . getName ( ) . equals ( "Fault" ) ) { SoapFault fault = new SoapFault ( ) ; fault . parse ( parser ) ; bodyIn = fault ; } else { Node node = ( bodyIn instanceof Node ) ? ( Node ) bodyIn : new Node ( ) ; node . parse ( parser ) ; bodyIn = node ; } } public void write ( XmlSerializer writer ) throws IOException { writer . setPrefix ( "i" , xsi ) ; writer . setPrefix ( "d" , xsd ) ; writer . setPrefix ( "c" , enc ) ; writer . setPrefix ( "v" , env ) ; writer . startTag ( env , "Envelope" ) ; writer . startTag ( env , "Header" ) ; writeHeader ( writer ) ; writer . endTag ( env , "Header" ) ; writer . startTag ( env , "Body" ) ; writeBody ( writer ) ; writer . endTag ( env , "Body" ) ; writer . endTag ( env , "Envelope" ) ; } public void writeHeader ( XmlSerializer writer ) throws IOException { if ( headerOut != null ) { for ( int i = 0 ; i < headerOut . length ; i ++ ) { headerOut [ i ] . write ( writer ) ; } } } public void writeBody ( XmlSerializer writer ) throws IOException { if ( encodingStyle != null ) writer . attribute ( env , "encodingStyle" , encodingStyle ) ; ( ( Node ) bodyOut ) . write ( writer ) ; } public void setOutputSoapObject ( Object soapObject ) { bodyOut = soapObject ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
