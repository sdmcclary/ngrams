public class Zero extends AbstractDatatype { public static final Zero THE_INSTANCE = new Zero ( ) ; private Zero ( ) { } @ Override public String getName ( ) { return "zero" ; } @ Override public void checkValid ( CharSequence literal ) throws DatatypeException { if ( literal . length ( ) != 1 || literal . charAt ( 0 ) != '0' ) { throw newDatatypeException ( 0 , "Only “0” is a permitted zero literal." ) ; } } } 