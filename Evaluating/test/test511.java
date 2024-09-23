abstract class AbstractRel extends AbstractDatatype { @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { Set < String > tokensSeen = new HashSet < String > ( ) ; StringBuilder builder = new StringBuilder ( ) ; int len = literal . length ( ) ; for ( int i = 0 ; i < len ; i ++ ) { char c = literal . charAt ( i ) ; if ( isWhitespace ( c ) && builder . length ( ) > 0 ) { checkToken ( builder , i , tokensSeen ) ; builder . setLength ( 0 ) ; } else { builder . append ( toAsciiLowerCase ( c ) ) ; } } if ( builder . length ( ) > 0 ) { checkToken ( builder , len , tokensSeen ) ; } } private void checkToken ( StringBuilder builder , int i , Set < String > tokensSeen ) throws DatatypeException { String token = builder . toString ( ) ; if ( tokensSeen . contains ( token ) ) { throw newDatatypeException ( i - 1 , "Duplicate keyword " , token , "." ) ; } tokensSeen . add ( token ) ; if ( ! isRegistered ( token ) ) { try { Html5DatatypeLibrary dl = new Html5DatatypeLibrary ( ) ; Iri iri = ( Iri ) dl . createDatatype ( "iri" ) ; iri . checkValid ( token ) ; } catch ( DatatypeException e ) { throw newDatatypeException ( i - 1 , "The string " , token , " is not a registered keyword or absolute URL." ) ; } } } protected abstract boolean isRegistered ( String token ) ; } 