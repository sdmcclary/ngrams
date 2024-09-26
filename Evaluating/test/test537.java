<<<<<<< HEAD
public final class XMLMemento implements IMemento { private Document factory ; private Element element ; public static XMLMemento createReadRoot ( Reader reader ) throws CoreException { return createReadRoot ( reader , null ) ; } public static XMLMemento createReadRoot ( Reader reader , String baseDir ) throws CoreException { String errorMessage = null ; Exception exception = null ; try { DocumentBuilderFactory factory = DocumentBuilderFactory . newInstance ( ) ; DocumentBuilder parser = factory . newDocumentBuilder ( ) ; InputSource source = new InputSource ( reader ) ; if ( baseDir != null ) { source . setSystemId ( baseDir ) ; } Document document = parser . parse ( source ) ; NodeList list = document . getChildNodes ( ) ; for ( int i = 0 ; i < list . getLength ( ) ; i ++ ) { Node node = list . item ( i ) ; if ( node instanceof Element ) { return new XMLMemento ( document , ( Element ) node ) ; } } } catch ( ParserConfigurationException e ) { exception = e ; errorMessage = "Internal XML parser configuration error." ; } catch ( IOException e ) { exception = e ; errorMessage = "Could not read content of XML file." ; } catch ( SAXException e ) { exception = e ; errorMessage = "Could not parse content of XML file." ; } String problemText = null ; if ( exception != null ) { problemText = exception . getMessage ( ) ; } if ( problemText == null || problemText . length ( ) == 0 ) { problemText = errorMessage != null ? errorMessage : "Could not find root element node of XML file." ; } throw new CoreException ( new Status ( Status . ERROR , CoreEPLPlugin . PLUGIN_ID , 0 , problemText , exception ) ) ; } public static XMLMemento createWriteRoot ( String type ) { Document document ; try { document = DocumentBuilderFactory . newInstance ( ) . newDocumentBuilder ( ) . newDocument ( ) ; Element element = document . createElement ( type ) ; document . appendChild ( element ) ; return new XMLMemento ( document , element ) ; } catch ( ParserConfigurationException e ) { throw new Error ( e . getMessage ( ) ) ; } } public XMLMemento ( Document document , Element element ) { super ( ) ; this . factory = document ; this . element = element ; } public IMemento createChild ( String type ) { Element child = factory . createElement ( type ) ; element . appendChild ( child ) ; return new XMLMemento ( factory , child ) ; } public IMemento createChild ( String type , String id ) { Element child = factory . createElement ( type ) ; child . setAttribute ( TAG_ID , id == null ? "" : id ) ; element . appendChild ( child ) ; return new XMLMemento ( factory , child ) ; } public IMemento copyChild ( IMemento child ) { Element childElement = ( ( XMLMemento ) child ) . element ; Element newElement = ( Element ) factory . importNode ( childElement , true ) ; element . appendChild ( newElement ) ; return new XMLMemento ( factory , newElement ) ; } public IMemento getChild ( String type ) { NodeList nodes = element . getChildNodes ( ) ; int size = nodes . getLength ( ) ; if ( size == 0 ) { return null ; } for ( int nX = 0 ; nX < size ; nX ++ ) { Node node = nodes . item ( nX ) ; if ( node instanceof Element ) { Element element = ( Element ) node ; if ( element . getNodeName ( ) . equals ( type ) ) { return new XMLMemento ( factory , element ) ; } } } return null ; } public IMemento [ ] getChildren ( String type ) { NodeList nodes = element . getChildNodes ( ) ; int size = nodes . getLength ( ) ; if ( size == 0 ) { return new IMemento [ 0 ] ; } ArrayList < Element > list = new ArrayList < Element > ( size ) ; for ( int nX = 0 ; nX < size ; nX ++ ) { Node node = nodes . item ( nX ) ; if ( node instanceof Element ) { Element element = ( Element ) node ; if ( element . getNodeName ( ) . equals ( type ) ) { list . add ( element ) ; } } } size = list . size ( ) ; IMemento [ ] results = new IMemento [ size ] ; for ( int x = 0 ; x < size ; x ++ ) { results [ x ] = new XMLMemento ( factory , ( Element ) list . get ( x ) ) ; } return results ; } public Float getFloat ( String key ) { Attr attr = element . getAttributeNode ( key ) ; if ( attr == null ) { return null ; } String strValue = attr . getValue ( ) ; try { return new Float ( strValue ) ; } catch ( NumberFormatException e ) { CoreEPLPlugin . log ( "Memento problem - Invalid float for key: " + key + " value: " + strValue , e ) ; return null ; } } public String getType ( ) { return element . getNodeName ( ) ; } public String getID ( ) { return element . getAttribute ( TAG_ID ) ; } public Integer getInteger ( String key ) { Attr attr = element . getAttributeNode ( key ) ; if ( attr == null ) { return null ; } String strValue = attr . getValue ( ) ; try { return new Integer ( strValue ) ; } catch ( NumberFormatException e ) { CoreEPLPlugin . log ( "Memento problem - invalid integer for key: " + key + " value: " + strValue , e ) ; return null ; } } public String getString ( String key ) { Attr attr = element . getAttributeNode ( key ) ; if ( attr == null ) { return null ; } return attr . getValue ( ) ; } public Boolean getBoolean ( String key ) { Attr attr = element . getAttributeNode ( key ) ; if ( attr == null ) { return null ; } return Boolean . valueOf ( attr . getValue ( ) ) ; } public String getTextData ( ) { Text textNode = getTextNode ( ) ; if ( textNode != null ) { return textNode . getData ( ) ; } return null ; } public String [ ] getAttributeKeys ( ) { NamedNodeMap map = element . getAttributes ( ) ; int size = map . getLength ( ) ; String [ ] attributes = new String [ size ] ; for ( int i = 0 ; i < size ; i ++ ) { Node node = map . item ( i ) ; attributes [ i ] = node . getNodeName ( ) ; } return attributes ; } private Text getTextNode ( ) { NodeList nodes = element . getChildNodes ( ) ; int size = nodes . getLength ( ) ; if ( size == 0 ) { return null ; } for ( int nX = 0 ; nX < size ; nX ++ ) { Node node = nodes . item ( nX ) ; if ( node instanceof Text ) { return ( Text ) node ; } } return null ; } private void putElement ( Element element , boolean copyText ) { NamedNodeMap nodeMap = element . getAttributes ( ) ; int size = nodeMap . getLength ( ) ; for ( int i = 0 ; i < size ; i ++ ) { Attr attr = ( Attr ) nodeMap . item ( i ) ; putString ( attr . getName ( ) , attr . getValue ( ) ) ; } NodeList nodes = element . getChildNodes ( ) ; size = nodes . getLength ( ) ; boolean needToCopyText = copyText ; for ( int i = 0 ; i < size ; i ++ ) { Node node = nodes . item ( i ) ; if ( node instanceof Element ) { XMLMemento child = ( XMLMemento ) createChild ( node . getNodeName ( ) ) ; child . putElement ( ( Element ) node , true ) ; } else if ( node instanceof Text && needToCopyText ) { putTextData ( ( ( Text ) node ) . getData ( ) ) ; needToCopyText = false ; } } } public void putFloat ( String key , float f ) { element . setAttribute ( key , String . valueOf ( f ) ) ; } public void putInteger ( String key , int n ) { element . setAttribute ( key , String . valueOf ( n ) ) ; } public void putMemento ( IMemento memento ) { putElement ( ( ( XMLMemento ) memento ) . element , false ) ; } public void putString ( String key , String value ) { if ( value == null ) { return ; } element . setAttribute ( key , value ) ; } public void putBoolean ( String key , boolean value ) { element . setAttribute ( key , value ? "true" : "false" ) ; } public void putTextData ( String data ) { Text textNode = getTextNode ( ) ; if ( textNode == null ) { textNode = factory . createTextNode ( data ) ; element . insertBefore ( textNode , element . getFirstChild ( ) ) ; } else { textNode . setData ( data ) ; } } public void save ( Writer writer ) throws IOException { DOMWriter out = new DOMWriter ( writer ) ; try { out . print ( element ) ; } finally { out . close ( ) ; } } private static final class DOMWriter extends PrintWriter { private int tab ; private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ; public DOMWriter ( Writer output ) { super ( output ) ; tab = 0 ; println ( XML_VERSION ) ; } public void print ( Element element ) { boolean hasChildren = element . hasChildNodes ( ) ; startTag ( element , hasChildren ) ; if ( hasChildren ) { tab ++ ; boolean prevWasText = false ; NodeList children = element . getChildNodes ( ) ; for ( int i = 0 ; i < children . getLength ( ) ; i ++ ) { Node node = children . item ( i ) ; if ( node instanceof Element ) { if ( ! prevWasText ) { println ( ) ; printTabulation ( ) ; } print ( ( Element ) children . item ( i ) ) ; prevWasText = false ; } else if ( node instanceof Text ) { print ( getEscaped ( node . getNodeValue ( ) ) ) ; prevWasText = true ; } } tab -- ; if ( ! prevWasText ) { println ( ) ; printTabulation ( ) ; } endTag ( element ) ; } } private void printTabulation ( ) { } private void startTag ( Element element , boolean hasChildren ) { StringBuffer sb = new StringBuffer ( ) ; sb . append ( "<" ) ; sb . append ( element . getTagName ( ) ) ; NamedNodeMap attributes = element . getAttributes ( ) ; for ( int i = 0 ; i < attributes . getLength ( ) ; i ++ ) { Attr attribute = ( Attr ) attributes . item ( i ) ; sb . append ( " " ) ; sb . append ( attribute . getName ( ) ) ; sb . append ( "=\"" ) ; sb . append ( getEscaped ( String . valueOf ( attribute . getValue ( ) ) ) ) ; sb . append ( "\"" ) ; } sb . append ( hasChildren ? ">" : "/>" ) ; print ( sb . toString ( ) ) ; } private void endTag ( Element element ) { StringBuffer sb = new StringBuffer ( ) ; sb . append ( "</" ) ; sb . append ( element . getNodeName ( ) ) ; sb . append ( ">" ) ; print ( sb . toString ( ) ) ; } private static void appendEscapedChar ( StringBuffer buffer , char c ) { String replacement = getReplacement ( c ) ; if ( replacement != null ) { buffer . append ( '&' ) ; buffer . append ( replacement ) ; buffer . append ( ';' ) ; } else if ( c == 9 || c == 10 || c == 13 || c >= 32 ) { buffer . append ( c ) ; } } private static String getEscaped ( String s ) { StringBuffer result = new StringBuffer ( s . length ( ) + 10 ) ; for ( int i = 0 ; i < s . length ( ) ; ++ i ) { appendEscapedChar ( result , s . charAt ( i ) ) ; } return result . toString ( ) ; } private static String getReplacement ( char c ) { switch ( c ) { case '<' : return "lt" ; case '>' : return "gt" ; case '"' : return "quot" ; case '\'' : return "apos" ; case '&' : return "amp" ; case '\r' : return "#x0D" ; case '\n' : return "#x0A" ; case '	' : return "#x09" ; } return null ; } } } 
=======
public final class Idref extends Id { public static final Idref THE_INSTANCE = new Idref ( ) ; private Idref ( ) { super ( ) ; } @ Override public String getName ( ) { return "id reference" ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
