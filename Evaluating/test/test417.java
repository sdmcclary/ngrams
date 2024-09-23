public class RdfaLiteChecker extends Checker { private static final String GUIDANCE = " Consider checking against the HTML5 + RDFa 1.1 schema instead." ; private void warnNonRDFaLite ( String localName , String att ) throws SAXException { warn ( "RDFa Core attribute “" + att + "” is not allowed on the “" + localName + "” element in HTML5 + RDFa 1.1 Lite documents." + GUIDANCE ) ; } @ Override public void startElement ( String uri , String localName , String qName , Attributes atts ) throws SAXException { if ( "http://www.w3.org/1999/xhtml" != uri ) { return ; } int len = atts . getLength ( ) ; for ( int i = 0 ; i < len ; i ++ ) { String att = atts . getLocalName ( i ) ; if ( "datatype" == att || "about" == att || "inlist" == att || "rev" == att ) { warn ( "RDFa Core attribute “" + att + "” is not allowed in HTML5 + RDFa 1.1 Lite documents." + GUIDANCE ) ; } else if ( "content" == att && "meta" != localName ) { warnNonRDFaLite ( localName , att ) ; } else if ( ( "rel" == att ) && "a" != localName && "area" != localName && "link" != localName ) { warnNonRDFaLite ( localName , att ) ; } } } } 