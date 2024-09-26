<<<<<<< HEAD
public class FieldTable extends LinkedHashMap { private long _encodedSize = 0 ; public FieldTable ( ) { super ( ) ; } public FieldTable ( ByteBuffer buffer , long length ) throws AMQFrameDecodingException { super ( ) ; assert length > 0 ; _encodedSize = length ; int sizeRead = 0 ; while ( sizeRead < _encodedSize ) { int sizeRemaining = buffer . remaining ( ) ; final String key = EncodingUtils . readShortString ( buffer ) ; byte iType = buffer . get ( ) ; final char type = ( char ) iType ; Object value ; switch ( type ) { case 'S' : value = EncodingUtils . readLongString ( buffer ) ; break ; case 'I' : value = new Long ( buffer . getUnsignedInt ( ) ) ; break ; case 'V' : value = null ; break ; default : String msg = "Unsupported field table type: " + type ; msg += " (" + iType + "), length=" + length + ", sizeRead=" + sizeRead + ", sizeRemaining=" + sizeRemaining ; throw new AMQFrameDecodingException ( msg ) ; } sizeRead += ( sizeRemaining - buffer . remaining ( ) ) ; super . put ( key , value ) ; } } public void writeToBuffer ( ByteBuffer buffer ) { EncodingUtils . writeUnsignedInteger ( buffer , _encodedSize ) ; final Iterator it = this . entrySet ( ) . iterator ( ) ; while ( it . hasNext ( ) ) { Map . Entry me = ( Map . Entry ) it . next ( ) ; String key = ( String ) me . getKey ( ) ; EncodingUtils . writeShortStringBytes ( buffer , key ) ; Object value = me . getValue ( ) ; if ( value == null ) { buffer . put ( ( byte ) 'V' ) ; } else if ( value instanceof byte [ ] ) { buffer . put ( ( byte ) 'S' ) ; EncodingUtils . writeLongstr ( buffer , ( byte [ ] ) value ) ; } else if ( value instanceof String ) { buffer . put ( ( byte ) 'S' ) ; EncodingUtils . writeLongStringBytes ( buffer , ( String ) value ) ; } else if ( value instanceof Long ) { buffer . put ( ( byte ) 'I' ) ; EncodingUtils . writeUnsignedInteger ( buffer , ( ( Long ) value ) . longValue ( ) ) ; } else { throw new IllegalArgumentException ( "Unsupported type in field table: " + value . getClass ( ) ) ; } } } public byte [ ] getDataAsBytes ( ) { final ByteBuffer buffer = ByteBuffer . allocate ( ( int ) _encodedSize ) ; final Iterator it = this . entrySet ( ) . iterator ( ) ; while ( it . hasNext ( ) ) { Map . Entry me = ( Map . Entry ) it . next ( ) ; String key = ( String ) me . getKey ( ) ; EncodingUtils . writeShortStringBytes ( buffer , key ) ; Object value = me . getValue ( ) ; if ( value == null ) { buffer . put ( ( byte ) 'V' ) ; } else if ( value instanceof byte [ ] ) { buffer . put ( ( byte ) 'S' ) ; EncodingUtils . writeLongstr ( buffer , ( byte [ ] ) value ) ; } else if ( value instanceof String ) { buffer . put ( ( byte ) 'S' ) ; EncodingUtils . writeLongStringBytes ( buffer , ( String ) value ) ; } else if ( value instanceof char [ ] ) { buffer . put ( ( byte ) 'S' ) ; EncodingUtils . writeLongStringBytes ( buffer , ( char [ ] ) value ) ; } else if ( value instanceof Long || value instanceof Integer ) { buffer . put ( ( byte ) 'I' ) ; EncodingUtils . writeUnsignedInteger ( buffer , ( ( Long ) value ) . longValue ( ) ) ; } else { assert false ; } } final byte [ ] result = new byte [ ( int ) _encodedSize ] ; buffer . flip ( ) ; buffer . get ( result ) ; buffer . release ( ) ; return result ; } public Object put ( Object key , Object value ) { if ( key == null ) { throw new IllegalArgumentException ( "All keys must be Strings - was passed: null" ) ; } else if ( ! ( key instanceof String ) ) { throw new IllegalArgumentException ( "All keys must be Strings - was passed: " + key . getClass ( ) ) ; } _encodedSize += EncodingUtils . encodedShortStringLength ( ( String ) key ) ; if ( value == null ) { _encodedSize += 1 ; } else if ( value instanceof String ) { _encodedSize += 1 + EncodingUtils . encodedLongStringLength ( ( String ) value ) ; } else if ( value instanceof char [ ] ) { _encodedSize += 1 + EncodingUtils . encodedLongStringLength ( ( char [ ] ) value ) ; } else if ( value instanceof Integer ) { _encodedSize += 1 + 4 ; } else if ( value instanceof Long ) { _encodedSize += 1 + 4 ; } else { throw new IllegalArgumentException ( "Unsupported type in field table: " + value . getClass ( ) ) ; } return super . put ( key , value ) ; } public Object remove ( Object key ) { if ( super . containsKey ( key ) ) { final Object value = super . remove ( key ) ; _encodedSize -= EncodingUtils . encodedShortStringLength ( ( String ) key ) ; if ( value != null ) { if ( value == null ) { _encodedSize -= 1 ; } else if ( value instanceof String ) { _encodedSize -= 1 + EncodingUtils . encodedLongStringLength ( ( String ) value ) ; } else if ( value instanceof char [ ] ) { _encodedSize -= 1 + EncodingUtils . encodedLongStringLength ( ( char [ ] ) value ) ; } else if ( value instanceof Integer ) { _encodedSize -= 5 ; } else if ( value instanceof Long ) { _encodedSize -= 5 ; } else { throw new IllegalArgumentException ( "Internal error: unsupported type in field table: " + value . getClass ( ) ) ; } } return value ; } else { return null ; } } public long getEncodedSize ( ) { return _encodedSize ; } } 
=======
public class Transform { private Transform ( ) { } public static URIResolver createSAXURIResolver ( Resolver resolver ) { final SAXResolver saxResolver = new SAXResolver ( resolver ) ; return new URIResolver ( ) { public Source resolve ( String href , String base ) throws TransformerException { try { return saxResolver . resolve ( href , base ) ; } catch ( SAXException e ) { throw toTransformerException ( e ) ; } catch ( IOException e ) { throw new TransformerException ( e ) ; } } } ; } public static Resolver createResolver ( final URIResolver uriResolver ) { return new AbstractResolver ( ) { public void resolve ( Identifier id , Input input ) throws IOException , ResolverException { if ( input . isResolved ( ) ) return ; Source source ; try { source = uriResolver . resolve ( id . getUriReference ( ) , id . getBase ( ) ) ; } catch ( TransformerException e ) { throw toResolverException ( e ) ; } if ( source == null ) return ; if ( source instanceof SAXSource ) { setInput ( input , ( SAXSource ) source ) ; return ; } InputSource in = SAXSource . sourceToInputSource ( source ) ; if ( in != null ) { SAX . setInput ( input , in ) ; return ; } throw new ResolverException ( "URIResolver returned unsupported subclass of Source" ) ; } } ; } private static void setInput ( Input input , SAXSource source ) { XMLReader reader = source . getXMLReader ( ) ; if ( reader != null ) { if ( input instanceof SAXInput ) ( ( SAXInput ) input ) . setXMLReader ( reader ) ; } InputSource in = source . getInputSource ( ) ; if ( in != null ) SAX . setInput ( input , in ) ; } private static TransformerException toTransformerException ( SAXException e ) { Exception wrapped = SAX . getWrappedException ( e ) ; if ( wrapped != null ) { if ( wrapped instanceof TransformerException ) return ( TransformerException ) wrapped ; return new TransformerException ( wrapped ) ; } return new TransformerException ( e ) ; } private static ResolverException toResolverException ( TransformerException e ) { Throwable wrapped = getWrappedException ( e ) ; if ( wrapped != null ) { if ( wrapped instanceof ResolverException ) return ( ResolverException ) wrapped ; return new ResolverException ( wrapped ) ; } return new ResolverException ( e ) ; } private static Throwable getWrappedException ( TransformerException e ) { Throwable wrapped = e . getException ( ) ; if ( wrapped == null ) return null ; String message = e . getMessage ( ) ; if ( message != null && ! message . equals ( wrapped . getMessage ( ) ) ) return null ; return wrapped ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
