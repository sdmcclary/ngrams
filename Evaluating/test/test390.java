<<<<<<< HEAD
public class XmlWriter extends PrintWriter { public final static Strategy XML = new Xml ( ) ; public final static Strategy XHTML = new Xhtml ( ) ; public final static Strategy HTML = new Html ( ) ; private boolean _isIndenting = false ; private int _indent = 0 ; private boolean _isElementOpen ; private boolean _isElementOpenNeedsNewline ; private String _openElementName ; private Strategy _strategy = XML ; private String _contentType = "text/xml" ; private String _characterEncoding ; private boolean _isNewLine = true ; public XmlWriter ( Writer out ) { super ( out ) ; } public String getContentType ( ) { return _contentType ; } public void setContentType ( String contentType ) { _contentType = contentType ; if ( _contentType . equals ( "text/xml" ) ) _strategy = XML ; if ( _contentType . equals ( "application/xml" ) ) _strategy = XML ; else if ( _contentType . equals ( "text/xhtml" ) ) _strategy = XHTML ; else if ( _contentType . equals ( "application/xhtml+xml" ) ) _strategy = XHTML ; else if ( _contentType . equals ( "text/html" ) ) _strategy = HTML ; else _strategy = XML ; } public void setStrategy ( Strategy strategy ) { _strategy = strategy ; } public void setIndenting ( boolean isIndenting ) { _isIndenting = isIndenting ; } public boolean isIndenting ( ) { return _isIndenting ; } public void setCharacterEncoding ( String characterEncoding ) { _characterEncoding = characterEncoding ; } public String getCharacterEncoding ( ) { return _characterEncoding ; } private boolean closeElementIfNeeded ( boolean isEnd ) { if ( _isElementOpen ) { _isElementOpen = false ; _strategy . closeElement ( this , _openElementName , isEnd ) ; if ( _isElementOpenNeedsNewline ) { _isElementOpenNeedsNewline = false ; softPrintln ( ) ; } return true ; } return false ; } private void startElement ( String name , boolean isLineBefore , boolean isLineAfter ) { closeElementIfNeeded ( false ) ; if ( isLineBefore ) softPrintln ( ) ; _openElementName = name ; _strategy . openElement ( this , name ) ; _isElementOpen = true ; _isElementOpenNeedsNewline = isLineAfter ; if ( _isIndenting ) _indent ++ ; } private void endElement ( String name , boolean isLineBefore , boolean isLineAfter ) { if ( _isIndenting ) _indent -- ; if ( ! closeElementIfNeeded ( true ) ) { if ( isLineBefore ) softPrintln ( ) ; _strategy . endElement ( this , name ) ; } if ( isLineAfter ) softPrintln ( ) ; } public void startElement ( String name ) { startElement ( name , false , false ) ; } public void endElement ( String name ) { endElement ( name , false , false ) ; } public void startBlockElement ( String name ) { startElement ( name , true , true ) ; } public void endBlockElement ( String name ) { endElement ( name , true , true ) ; } public void startLineElement ( String name ) { startElement ( name , true , false ) ; } public void endLineElement ( String name ) { endElement ( name , false , true ) ; } public void writeElement ( String name ) { startElement ( name ) ; endElement ( name ) ; } public void writeLineElement ( String name ) { startLineElement ( name ) ; endLineElement ( name ) ; } public void writeBlockElement ( String name ) { startBlockElement ( name ) ; endBlockElement ( name ) ; } public void writeElement ( String name , Object text ) { startElement ( name ) ; writeText ( text ) ; endElement ( name ) ; } public void writeLineElement ( String name , Object text ) { startLineElement ( name ) ; writeText ( text ) ; endLineElement ( name ) ; } public void writeBlockElement ( String name , Object text ) { startBlockElement ( name ) ; writeText ( text ) ; endBlockElement ( name ) ; } public void writeAttribute ( String name , Object value ) { if ( ! _isElementOpen ) throw new IllegalStateException ( "no open element" ) ; if ( value == null ) return ; _isElementOpen = false ; try { _strategy . writeAttribute ( this , name , value ) ; } finally { _isElementOpen = true ; } } public void writeAttribute ( String name , Object ... values ) { if ( ! _isElementOpen ) throw new IllegalStateException ( "no open element" ) ; _isElementOpen = false ; try { _strategy . writeAttribute ( this , name , values ) ; } finally { _isElementOpen = true ; } } public void writeText ( char ch ) { closeElementIfNeeded ( false ) ; writeIndentIfNewLine ( ) ; _strategy . writeText ( this , ch ) ; } public void writeText ( char [ ] buf ) { closeElementIfNeeded ( false ) ; writeIndentIfNewLine ( ) ; _strategy . writeText ( this , buf ) ; } public void writeText ( char [ ] buf , int offset , int length ) { closeElementIfNeeded ( false ) ; writeIndentIfNewLine ( ) ; _strategy . writeText ( this , buf , offset , length ) ; } public void writeText ( Object obj ) { closeElementIfNeeded ( false ) ; writeIndentIfNewLine ( ) ; _strategy . writeTextObject ( this , obj ) ; } public void writeComment ( String comment ) { closeElementIfNeeded ( false ) ; writeIndentIfNewLine ( ) ; _strategy . writeComment ( this , comment ) ; } public void flush ( ) { closeElementIfNeeded ( true ) ; super . flush ( ) ; } public void println ( ) { closeElementIfNeeded ( false ) ; super . println ( ) ; _isNewLine = true ; } public boolean isNewLine ( ) { return _isNewLine ; } public boolean softPrintln ( ) { if ( ! isNewLine ( ) ) { println ( ) ; return true ; } else return false ; } public void write ( int ch ) { closeElementIfNeeded ( false ) ; _isNewLine = false ; super . write ( ch ) ; } public void write ( char buf [ ] , int off , int len ) { closeElementIfNeeded ( false ) ; _isNewLine = false ; super . write ( buf , off , len ) ; } public void write ( char buf [ ] ) { closeElementIfNeeded ( false ) ; _isNewLine = false ; super . write ( buf ) ; } public void write ( String s , int off , int len ) { closeElementIfNeeded ( false ) ; _isNewLine = false ; super . write ( s , off , len ) ; } public void write ( String s ) { closeElementIfNeeded ( false ) ; _isNewLine = false ; super . write ( s ) ; } static public abstract class Strategy { abstract void openElement ( XmlWriter writer , String name ) ; abstract void closeElement ( XmlWriter writer , String name , boolean isEnd ) ; abstract void endElement ( XmlWriter writer , String name ) ; abstract void writeAttribute ( XmlWriter writer , String name , Object value ) ; abstract void writeAttribute ( XmlWriter writer , String name , Object ... values ) ; abstract void writeText ( XmlWriter writer , char ch ) ; abstract void writeText ( XmlWriter writer , char [ ] buf ) ; abstract void writeText ( XmlWriter writer , char [ ] buf , int offset , int length ) ; abstract void writeTextObject ( XmlWriter writer , Object obj ) ; abstract void writeComment ( XmlWriter writer , String comment ) ; } static public class Xml extends Strategy { void openElement ( XmlWriter writer , String name ) { writer . writeIndentIfNewLine ( ) ; writer . write ( '<' ) ; writer . write ( name ) ; } void closeElement ( XmlWriter writer , String name , boolean isEnd ) { if ( isEnd ) writer . write ( '/' ) ; writer . write ( '>' ) ; } void endElement ( XmlWriter writer , String name ) { writer . writeIndentIfNewLine ( ) ; writer . write ( "</" ) ; writer . write ( name ) ; writer . write ( '>' ) ; } void writeAttribute ( XmlWriter writer , String name , Object value ) { writer . write ( " " ) ; writer . write ( name ) ; writer . write ( '=' ) ; writer . write ( "'" ) ; writeAttributeValue ( writer , name , value ) ; writer . write ( "'" ) ; } void writeAttribute ( XmlWriter writer , String name , Object ... values ) { writer . write ( " " ) ; writer . write ( name ) ; writer . write ( '=' ) ; writer . write ( "'" ) ; int len = values . length ; for ( int i = 0 ; i < len ; i ++ ) { Object value = values [ i ] ; if ( value == null ) continue ; if ( i > 0 ) writer . write ( ' ' ) ; writeAttributeValue ( writer , name , value ) ; } writer . write ( "'" ) ; } protected void writeAttributeValue ( XmlWriter writer , String name , Object value ) { writeXmlEscaped ( writer , value ) ; } public void writeText ( XmlWriter writer , char ch ) { writeXmlEscapedChar ( writer , ch ) ; } public void writeText ( XmlWriter writer , char [ ] buf ) { int endIndex = buf . length ; for ( int i = 0 ; i < endIndex ; i ++ ) { writeXmlEscapedChar ( writer , buf [ i ] ) ; } } public void writeText ( XmlWriter writer , char [ ] buf , int offset , int length ) { int endIndex = offset + length ; for ( int i = offset ; i < endIndex ; i ++ ) { writeXmlEscapedChar ( writer , buf [ i ] ) ; } } public void writeTextObject ( XmlWriter writer , Object obj ) { String string = String . valueOf ( obj ) ; int len = string . length ( ) ; for ( int i = 0 ; i < len ; i ++ ) { writeXmlEscapedChar ( writer , string . charAt ( i ) ) ; } } public void writeComment ( XmlWriter writer , String comment ) { writer . write ( "<!-- " ) ; writeXmlEscaped ( writer , comment ) ; writer . write ( " -->" ) ; } private void writeXmlEscapedChar ( XmlWriter writer , char ch ) { switch ( ch ) { case '<' : writer . write ( "&lt;" ) ; break ; case '>' : writer . write ( "&gt;" ) ; break ; case '&' : writer . write ( "&amp;" ) ; break ; case '\"' : writer . write ( "&quot;" ) ; break ; case '\'' : writer . write ( "&apos;" ) ; break ; default : writer . write ( ch ) ; } } private void writeXmlEscaped ( XmlWriter writer , Object object ) { String string = object . toString ( ) ; int len = string . length ( ) ; for ( int i = 0 ; i < len ; i ++ ) { writeXmlEscapedChar ( writer , string . charAt ( i ) ) ; } } } private void writeIndentIfNewLine ( ) { if ( isNewLine ( ) ) { for ( int i = _indent * 2 ; i > 0 ; i -- ) { write ( ' ' ) ; } } } static public class Xhtml extends Xml { private int EMPTY = 1 ; private int BREAK_BEFORE = 2 ; private int BREAK_AFTER = 4 ; private int BREAK_AFTER_CONTENT = 8 ; private int BOOLEAN_ATTRIBUTE = 1024 ; private HashMap < String , Integer > _flags = new HashMap < String , Integer > ( ) ; public Xhtml ( ) { addFlags ( "html" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "head" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "body" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "style" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "meta" , BREAK_BEFORE | BREAK_AFTER | EMPTY ) ; addFlags ( "link" , BREAK_BEFORE | BREAK_AFTER | EMPTY ) ; addFlags ( "title" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "base" , BREAK_BEFORE | BREAK_AFTER | EMPTY ) ; addFlags ( "h1" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "h2" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "h3" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "h4" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "h5" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "h6" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "p" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "div" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "ul" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "ol" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "li" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "dl" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "dt" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "dd" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "hr" , BREAK_BEFORE | BREAK_AFTER | EMPTY ) ; addFlags ( "br" , BREAK_AFTER | EMPTY ) ; addFlags ( "option" , EMPTY ) ; addFlags ( "img" , EMPTY ) ; addFlags ( "area" , EMPTY ) ; addFlags ( "pre" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "blockquote" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "address" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "fieldset" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "form" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "ins" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "del" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "script" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "noscript" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "input" , EMPTY ) ; addFlags ( "table" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "thead" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "tfoot" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "tr" , BREAK_BEFORE | BREAK_AFTER_CONTENT ) ; addFlags ( "col" , EMPTY ) ; addFlags ( "object" , BREAK_BEFORE | BREAK_AFTER ) ; addFlags ( "param" , BREAK_BEFORE | BREAK_AFTER | EMPTY ) ; addFlags ( "compact" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "nowrap" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "ismap" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "declare" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "noshade" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "checked" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "disabled" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "readonly" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "multiple" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "selected" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "noresize" , BOOLEAN_ATTRIBUTE ) ; addFlags ( "defer" , BOOLEAN_ATTRIBUTE ) ; } protected void addFlags ( String name , int flag ) { int intValue = getFlags ( name ) ; intValue |= flag ; _flags . put ( name , intValue ) ; } protected int getFlags ( String name ) { int intValue ; Integer integer = _flags . get ( name ) ; if ( integer == null ) intValue = 0 ; else intValue = integer ; return intValue ; } void openElement ( XmlWriter writer , String name ) { int flags = getFlags ( name ) ; if ( ( flags & BREAK_BEFORE ) > 0 ) writer . softPrintln ( ) ; writer . writeIndentIfNewLine ( ) ; writer . write ( '<' ) ; writer . write ( name ) ; } protected void writeAttributeValue ( XmlWriter writer , String name , Object value ) { int flags = getFlags ( name ) ; if ( ( flags & BOOLEAN_ATTRIBUTE ) > 0 ) value = name . toUpperCase ( ) ; super . writeAttributeValue ( writer , name , value ) ; } void closeElement ( XmlWriter writer , String name , boolean isEnd ) { int flags = getFlags ( name ) ; boolean isEmpty = ( flags & EMPTY ) > 0 ; if ( isEnd && isEmpty ) writer . write ( " />" ) ; else writer . write ( '>' ) ; if ( ( flags & BREAK_AFTER ) > 0 ) writer . softPrintln ( ) ; if ( isEnd && ! isEmpty ) endElement ( writer , name ) ; } void endElement ( XmlWriter writer , String name ) { int flags = getFlags ( name ) ; boolean isFullBreak = ( flags & ( BREAK_BEFORE | BREAK_AFTER ) ) == ( BREAK_BEFORE | BREAK_AFTER ) ; if ( isFullBreak ) writer . softPrintln ( ) ; writer . writeIndentIfNewLine ( ) ; if ( ( flags & EMPTY ) == 0 ) { writer . write ( "</" ) ; writer . write ( name ) ; writer . write ( '>' ) ; } if ( isFullBreak || ( ( flags & BREAK_AFTER_CONTENT ) > 0 ) ) writer . softPrintln ( ) ; } protected void writeDoctype ( XmlWriter writer ) { writer . println ( "<!DOCTYPE html  PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" ) ; } protected void writeXmlDeclaration ( XmlWriter writer ) { String encoding = writer . getCharacterEncoding ( ) ; writer . println ( "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" ) ; } } static public class Html extends Xhtml { public Html ( ) { } } } 
=======
public class Jaxp11XMLReaderCreator implements XMLReaderCreator { private final SAXParserFactory factory ; public Jaxp11XMLReaderCreator ( ) { factory = SAXParserFactory . newInstance ( ) ; factory . setNamespaceAware ( true ) ; factory . setValidating ( false ) ; } public XMLReader createXMLReader ( ) throws SAXException { try { return factory . newSAXParser ( ) . getXMLReader ( ) ; } catch ( ParserConfigurationException e ) { throw new SAXException ( e ) ; } } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
