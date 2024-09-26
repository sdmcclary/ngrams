public class FileInputOutput extends AbstractBinaryOutput implements BinaryInput , BinaryOutput , LockableStream , EnvCleanup { private static final Logger log = Logger . getLogger ( FileInputOutput . class . getName ( ) ) ; private Env _env ; private Path _path ; private LineReader _lineReader ; private RandomAccessStream _stream ; private int _buffer ; private boolean _doUnread = false ; @ SuppressWarnings ( "unused" ) private Reader _readEncoding ; private String _readEncodingName ; private boolean _temporary ; public FileInputOutput ( Env env , Path path ) throws IOException { this ( env , path , false , false , false ) ; } public FileInputOutput ( Env env , Path path , boolean append , boolean truncate ) throws IOException { this ( env , path , append , truncate , false ) ; } public FileInputOutput ( Env env , Path path , boolean append , boolean truncate , boolean temporary ) throws IOException { _env = env ; env . addCleanup ( this ) ; _path = path ; _lineReader = new LineReader ( env ) ; if ( truncate ) path . truncate ( 0L ) ; _stream = path . openRandomAccess ( ) ; if ( append && _stream . getLength ( ) > 0 ) _stream . seek ( _stream . getLength ( ) ) ; _temporary = temporary ; } public OutputStream getOutputStream ( ) { try { return _stream . getOutputStream ( ) ; } catch ( IOException e ) { log . log ( Level . FINE , e . toString ( ) , e ) ; return null ; } } public InputStream getInputStream ( ) { try { return _stream . getInputStream ( ) ; } catch ( IOException e ) { log . log ( Level . FINE , e . toString ( ) , e ) ; return null ; } } public Path getPath ( ) { return _path ; } public void setEncoding ( String encoding ) throws UnsupportedEncodingException { String mimeName = Encoding . getMimeName ( encoding ) ; if ( mimeName != null && mimeName . equals ( _readEncodingName ) ) return ; _readEncoding = Encoding . getReadEncoding ( getInputStream ( ) , encoding ) ; _readEncodingName = mimeName ; } public void unread ( ) throws IOException { _doUnread = true ; } public int read ( ) throws IOException { if ( _doUnread ) { _doUnread = false ; return _buffer ; } else { _buffer = _stream . read ( ) ; return _buffer ; } } public int read ( byte [ ] buffer , int offset , int length ) throws IOException { _doUnread = false ; return _stream . read ( buffer , offset , length ) ; } public int read ( char [ ] buffer , int offset , int length ) throws IOException { _doUnread = false ; return _stream . read ( buffer , offset , length ) ; } public StringValue appendTo ( StringValue builder ) throws IOException { if ( _stream != null ) return builder . append ( _stream ) ; else return builder ; } public StringValue read ( int length ) throws IOException { StringValue bb = _env . createBinaryBuilder ( ) ; TempBuffer temp = TempBuffer . allocate ( ) ; try { byte [ ] buffer = temp . getBuffer ( ) ; while ( length > 0 ) { int sublen = buffer . length ; if ( length < sublen ) sublen = length ; sublen = read ( buffer , 0 , sublen ) ; if ( sublen > 0 ) { bb . append ( buffer , 0 , sublen ) ; length -= sublen ; } else break ; } } finally { TempBuffer . free ( temp ) ; } return bb ; } public boolean readOptionalLinefeed ( ) throws IOException { int ch = read ( ) ; if ( ch == '\n' ) { return true ; } else { unread ( ) ; return false ; } } public StringValue readLine ( long length ) throws IOException { return _lineReader . readLine ( _env , this , length ) ; } public boolean isEOF ( ) { try { return _stream . getLength ( ) <= _stream . getFilePointer ( ) ; } catch ( IOException e ) { return true ; } } public void print ( char v ) throws IOException { _stream . write ( ( byte ) v ) ; } public void print ( String v ) throws IOException { for ( int i = 0 ; i < v . length ( ) ; i ++ ) write ( v . charAt ( i ) ) ; } public void write ( byte [ ] buffer , int offset , int length ) throws IOException { _stream . write ( buffer , offset , length ) ; } public void write ( int ch ) throws IOException { _stream . write ( ch ) ; } public void flush ( ) throws IOException { } public void closeWrite ( ) { close ( ) ; } public void closeRead ( ) { close ( ) ; } public void close ( ) { _env . removeCleanup ( this ) ; cleanup ( ) ; } public void cleanup ( ) { try { RandomAccessStream ras = _stream ; _stream = null ; if ( ras != null ) { ras . close ( ) ; if ( _temporary ) _path . remove ( ) ; } } catch ( IOException e ) { log . log ( Level . FINE , e . toString ( ) , e ) ; } } public long getPosition ( ) { try { return _stream . getFilePointer ( ) ; } catch ( IOException e ) { log . log ( Level . FINE , e . toString ( ) , e ) ; return - 1 ; } } public boolean setPosition ( long offset ) { return _stream . seek ( offset ) ; } public long seek ( long offset , int whence ) { long position ; switch ( whence ) { case BinaryStream . SEEK_CUR : position = getPosition ( ) + offset ; break ; case BinaryStream . SEEK_END : try { position = _stream . getLength ( ) + offset ; } catch ( IOException e ) { log . log ( Level . FINE , e . toString ( ) , e ) ; return getPosition ( ) ; } break ; case BinaryStream . SEEK_SET : default : position = offset ; break ; } if ( ! setPosition ( position ) ) return - 1L ; else return position ; } public BinaryInput openCopy ( ) throws IOException { return new FileInputOutput ( _env , _path ) ; } public boolean lock ( boolean shared , boolean block ) { return _stream . lock ( shared , block ) ; } public boolean unlock ( ) { return _stream . unlock ( ) ; } public Value stat ( ) { return FileModule . statImpl ( _env , getPath ( ) ) ; } public String toString ( ) { return "FileInputOutput[" + getPath ( ) + "]" ; } } 