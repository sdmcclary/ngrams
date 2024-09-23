public class HttpTransport extends Transport { ServiceConnection connection ; OutputStream os ; InputStream is ; private boolean connected = false ; public HttpTransport ( String url ) { super ( url ) ; } public void call ( String soapAction , SoapEnvelope envelope ) throws IOException , XmlPullParserException { if ( soapAction == null ) soapAction = "\"\"" ; byte [ ] requestData = createRequestData ( envelope ) ; requestDump = debug ? new String ( requestData ) : null ; responseDump = null ; try { connected = true ; connection = getServiceConnection ( ) ; connection . setRequestProperty ( "SOAPAction" , soapAction ) ; connection . setRequestProperty ( "Content-Type" , "text/xml" ) ; connection . setRequestProperty ( "Content-Length" , "" + requestData . length ) ; connection . setRequestProperty ( "User-Agent" , "kSOAP/2.0" ) ; connection . setRequestMethod ( HttpConnection . POST ) ; os = connection . openOutputStream ( ) ; os . write ( requestData , 0 , requestData . length ) ; os . close ( ) ; requestData = null ; is = connection . openInputStream ( ) ; if ( debug ) { ByteArrayOutputStream bos = new ByteArrayOutputStream ( ) ; byte [ ] buf = new byte [ 256 ] ; while ( true ) { int rd = is . read ( buf , 0 , 256 ) ; if ( rd == - 1 ) break ; bos . write ( buf , 0 , rd ) ; } bos . flush ( ) ; buf = bos . toByteArray ( ) ; responseDump = new String ( buf ) ; is . close ( ) ; is = new ByteArrayInputStream ( buf ) ; } parseResponse ( envelope , is ) ; } finally { if ( ! connected ) throw new InterruptedIOException ( ) ; reset ( ) ; } if ( envelope . bodyIn instanceof SoapFault ) throw ( ( SoapFault ) envelope . bodyIn ) ; } public void reset ( ) { connected = false ; if ( is != null ) { try { is . close ( ) ; } catch ( Throwable e ) { } is = null ; } if ( connection != null ) { try { connection . disconnect ( ) ; } catch ( Throwable e ) { } connection = null ; } } protected ServiceConnection getServiceConnection ( ) throws IOException { return new ServiceConnectionMidp ( url ) ; } } 