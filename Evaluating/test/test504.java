public final class PercentDecodingReaderInputStream extends InputStream { private final Reader delegate ; public PercentDecodingReaderInputStream ( final Reader delegate ) { this . delegate = delegate ; } @ Override public int read ( ) throws IOException { int c = delegate . read ( ) ; if ( c == - 1 ) { return - 1 ; } if ( c == '%' ) { return readHexByte ( ) ; } else if ( c < 0x80 ) { return c ; } else { throw new MalformedURLException ( "Unescaped non-ASCII character." ) ; } } private int readHexByte ( ) throws IOException { int c = delegate . read ( ) ; if ( isHexDigit ( c ) ) { int hi = Character . getNumericValue ( c ) << 4 ; c = delegate . read ( ) ; if ( isHexDigit ( c ) ) { return hi | Character . getNumericValue ( c ) ; } else { throw new MalformedURLException ( "Malformed percent escape." ) ; } } else { throw new MalformedURLException ( "Malformed percent escape." ) ; } } private boolean isHexDigit ( int c ) { return ( c >= '0' && c <= '9' ) || ( c >= 'a' && c <= 'f' ) || ( c >= 'A' && c <= 'F' ) ; } @ Override public void close ( ) throws IOException { delegate . close ( ) ; } } 