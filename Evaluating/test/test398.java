public class XmlPullSource implements ParserSource { private XmlPullParser parser ; private InputStream in ; private boolean inProgress ; private boolean atStart ; private Stack < StartTag > sts = new Stack < StartTag > ( ) ; public XmlPullSource ( XmlPullParser parser , InputStream in , Reader reader ) { this . parser = parser ; try { parser . setFeature ( XmlPullParser . FEATURE_PROCESS_NAMESPACES , true ) ; parser . setInput ( reader ) ; } catch ( XmlPullParserException ex ) { ex . printStackTrace ( ) ; throw new Error ( "Parser " + parser + " not namespace-conformant" ) ; } this . in = in ; inProgress = true ; atStart = true ; } public XmlPullSource ( XmlPullParser parser , InputStream in ) throws IOException { this . parser = parser ; try { parser . setFeature ( XmlPullParser . FEATURE_PROCESS_NAMESPACES , true ) ; } catch ( XmlPullParserException ex ) { ex . printStackTrace ( ) ; throw new Error ( "Parser " + parser + " not namespace-conformant" ) ; } try { parser . setInput ( in , null ) ; } catch ( XmlPullParserException ex ) { throw ( IOException ) new IOException ( ex . getMessage ( ) ) . initCause ( ex ) ; } this . in = in ; inProgress = true ; atStart = true ; } public Item next ( ) throws IOException { Item result = null ; if ( inProgress ) { while ( inProgress && result == null ) { try { if ( ! atStart ) { parser . nextToken ( ) ; } else { atStart = false ; } int type = parser . getEventType ( ) ; switch ( type ) { case XmlPullParser . START_DOCUMENT : sts . push ( null ) ; result = StartDocument . instance ( ) ; break ; case XmlPullParser . END_DOCUMENT : sts . pop ( ) ; result = EndDocument . instance ( ) ; inProgress = false ; break ; case XmlPullParser . START_TAG : { StartTag st = new StartTag ( new Qname ( parser . getNamespace ( ) , parser . getName ( ) ) , sts . peek ( ) ) ; int start = parser . getNamespaceCount ( parser . getDepth ( ) - 1 ) ; int end = parser . getNamespaceCount ( parser . getDepth ( ) ) ; for ( int i = start ; i < end ; i ++ ) { String prefix = parser . getNamespacePrefix ( i ) ; if ( prefix == null ) { prefix = "" ; } String namespace = parser . getNamespaceUri ( i ) ; st . addPrefix ( namespace , prefix ) ; } int atts = parser . getAttributeCount ( ) ; for ( int i = 0 ; i < atts ; i ++ ) { st . addAttribute ( new Qname ( parser . getAttributeNamespace ( i ) , parser . getAttributeName ( i ) ) , parser . getAttributeValue ( i ) ) ; } sts . push ( st ) ; result = st ; break ; } case XmlPullParser . END_TAG : result = new EndTag ( new Qname ( parser . getNamespace ( ) , parser . getName ( ) ) ) ; sts . pop ( ) ; break ; case XmlPullParser . TEXT : result = new Text ( parser . getText ( ) ) ; break ; case XmlPullParser . ENTITY_REF : { String name = parser . getName ( ) ; if ( name . equals ( "amp" ) ) { result = new Text ( "&" ) ; } else { result = new EntityRef ( parser . getName ( ) ) ; } break ; } case XmlPullParser . COMMENT : result = new Comment ( parser . getText ( ) ) ; break ; case XmlPullParser . PROCESSING_INSTRUCTION : { String text = parser . getText ( ) ; String [ ] comps = text . split ( "\\s+" , 2 ) ; result = new Pi ( comps [ 0 ] , comps [ 1 ] ) ; break ; } case XmlPullParser . IGNORABLE_WHITESPACE : break ; case XmlPullParser . DOCDECL : { String text = parser . getText ( ) ; int index = text . indexOf ( "PUBLIC" ) ; if ( index >= 0 ) { String name = text . substring ( 0 , index ) . trim ( ) ; int end = index + 6 ; char term = text . charAt ( end ) ; while ( term != '"' && term != '\'' ) { end += 1 ; term = text . charAt ( end ) ; } end += 1 ; int start = end ; while ( text . charAt ( end ) != term ) { end += 1 ; } String publicId = text . substring ( start , end ) ; String systemId = text . substring ( end + 2 ) . trim ( ) ; result = new Doctype ( name , publicId , systemId ) ; } else { index = text . indexOf ( "SYSTEM" ) ; if ( index >= 0 ) { String name = text . substring ( 0 , index ) . trim ( ) ; String systemId = text . substring ( index + 6 ) . trim ( ) ; result = new Doctype ( name , systemId ) ; } } break ; } default : throw new IllegalStateException ( "Got unrecognized type " + type ) ; } } catch ( XmlPullParserException ex ) { inProgress = false ; throw ( IOException ) new IOException ( ex . getMessage ( ) ) . initCause ( ex ) ; } catch ( IOException ex ) { inProgress = false ; throw ex ; } } } return result ; } public InputStream getInputStream ( ) { return in ; } public String getEncoding ( ) { String result = parser . getInputEncoding ( ) ; if ( result == null ) { result = "UTF-8" ; } return result ; } public StartTag getContext ( ) { if ( ! sts . isEmpty ( ) ) { return sts . peek ( ) ; } else { return null ; } } public void setContext ( StartTag context ) { inProgress = true ; atStart = false ; sts . clear ( ) ; sts . push ( context ) ; } public void reset ( ) { inProgress = true ; atStart = true ; sts . clear ( ) ; } } 