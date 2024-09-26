public class GZInputStream extends InputStream { private PushbackInputStream _in ; private Inflater _inflater ; private CRC32 _crc ; private boolean _eof ; private boolean _isGzip ; private byte [ ] _readBuffer ; private byte [ ] _tbuffer ; private int _readBufferSize ; private int _inputSize ; private long _totalInputSize ; public GZInputStream ( InputStream in ) throws IOException { this ( in , 512 ) ; } public GZInputStream ( InputStream in , int size ) throws IOException { _in = new PushbackInputStream ( in , size ) ; _inflater = new Inflater ( true ) ; _crc = new CRC32 ( ) ; _eof = false ; _readBuffer = new byte [ size ] ; _tbuffer = new byte [ 128 ] ; _totalInputSize = 0 ; init ( ) ; } public int available ( ) throws IOException { if ( ! _isGzip ) return _in . available ( ) ; if ( _eof == true ) return 0 ; return 1 ; } public void close ( ) throws IOException { _inflater . end ( ) ; } public boolean markSupported ( ) { return false ; } public int read ( ) throws IOException { byte [ ] b = new byte [ 1 ] ; int n = read ( b ) ; if ( n < 0 ) return - 1 ; return b [ 0 ] ; } public int read ( byte [ ] b ) throws IOException { return read ( b , 0 , b . length ) ; } public int read ( byte [ ] b , int off , int len ) throws IOException { if ( len <= 0 || off < 0 || off + len > b . length ) return 0 ; if ( _eof ) return - 1 ; if ( ! _isGzip ) return _in . read ( b , off , len ) ; try { int sublen ; int length = 0 ; while ( length < len ) { if ( _inflater . needsInput ( ) ) { _readBufferSize = _in . read ( _readBuffer , 0 , _readBuffer . length ) ; if ( _readBufferSize < 0 ) break ; _inflater . setInput ( _readBuffer , 0 , _readBufferSize ) ; } sublen = _inflater . inflate ( b , off + length , len - length ) ; _crc . update ( b , off + length , sublen ) ; _inputSize += sublen ; _totalInputSize += sublen ; length += sublen ; if ( _inflater . finished ( ) ) { int remaining = _inflater . getRemaining ( ) ; _in . unread ( _readBuffer , _readBufferSize - remaining , remaining ) ; readTrailer ( ) ; int secondPart = read ( b , off + length , len - length ) ; return secondPart > 0 ? length + secondPart : length ; } } return length ; } catch ( DataFormatException e ) { throw new IOException ( e . getMessage ( ) ) ; } } public long skip ( long n ) throws IOException { if ( _eof || n <= 0 ) return 0 ; long remaining = n ; while ( remaining > 0 ) { int length = ( int ) Math . min ( _tbuffer . length , remaining ) ; int sublen = read ( _tbuffer , 0 , length ) ; if ( sublen < 0 ) break ; remaining -= sublen ; } return ( n - remaining ) ; } private void init ( ) throws IOException { _inflater . reset ( ) ; _crc . reset ( ) ; _inputSize = 0 ; _readBufferSize = 0 ; byte flg ; int length = _in . read ( _tbuffer , 0 , 10 ) ; if ( length < 0 ) { _isGzip = false ; return ; } else if ( length != 10 ) { _isGzip = false ; _in . unread ( _tbuffer , 0 , length ) ; return ; } if ( _tbuffer [ 0 ] != ( byte ) 0x1f || _tbuffer [ 1 ] != ( byte ) 0x8b ) { _isGzip = false ; _in . unread ( _tbuffer , 0 , length ) ; return ; } flg = _tbuffer [ 3 ] ; if ( ( flg & ( byte ) 0x04 ) > 0 ) { length = _in . read ( _tbuffer , 0 , 2 ) ; if ( length != 2 ) throw new IOException ( "Bad GZIP (FEXTRA) header." ) ; length = ( ( ( int ) _tbuffer [ 1 ] ) << 4 ) | _tbuffer [ 0 ] ; _in . skip ( length ) ; } int c ; if ( ( flg & ( byte ) 0x08 ) > 0 ) { c = _in . read ( ) ; while ( c != 0 ) { if ( c < 0 ) throw new IOException ( "Bad GZIP (FNAME) header." ) ; c = _in . read ( ) ; } } if ( ( flg & 0x10 ) > 0 ) { c = _in . read ( ) ; while ( c != 0 ) { if ( c < 0 ) throw new IOException ( "Bad GZIP (FCOMMENT) header." ) ; c = _in . read ( ) ; } } if ( ( flg & 0x02 ) > 0 ) { length = _in . read ( _tbuffer , 0 , 2 ) ; if ( length != 2 ) throw new IOException ( "Bad GZIP (FHCRC) header." ) ; } _isGzip = true ; } private void readTrailer ( ) throws IOException { int length = _in . read ( _tbuffer , 0 , 8 ) ; if ( length != 8 ) throw new IOException ( "Bad GZIP trailer." ) ; int refValue = _tbuffer [ 3 ] & 0xff ; refValue <<= 8 ; refValue |= _tbuffer [ 2 ] & 0xff ; refValue <<= 8 ; refValue |= _tbuffer [ 1 ] & 0xff ; refValue <<= 8 ; refValue |= _tbuffer [ 0 ] & 0xff ; int value = ( int ) _crc . getValue ( ) ; if ( refValue != value ) throw new IOException ( "Bad GZIP trailer (CRC32)." ) ; refValue = _tbuffer [ 7 ] & 0xff ; refValue <<= 8 ; refValue |= _tbuffer [ 6 ] & 0xff ; refValue <<= 8 ; refValue |= _tbuffer [ 5 ] & 0xff ; refValue <<= 8 ; refValue |= _tbuffer [ 4 ] & 0xff ; if ( refValue != _inputSize ) throw new IOException ( "Bad GZIP trailer (LENGTH)." ) ; int c = _in . read ( ) ; if ( c < 0 ) _eof = true ; else { _in . unread ( c ) ; init ( ) ; if ( ! _isGzip ) _eof = true ; } } public boolean isGzip ( ) { return _isGzip ; } } 