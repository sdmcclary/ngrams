public class DocumentTotalizer implements DocumentProcessor { public Document processDocument ( Document doc ) throws DocumentProcessorException { long total = 0 ; for ( Item item : doc . getItems ( ) ) { if ( ! doc . getCur ( ) . equals ( item . getCur ( ) ) ) { throw new DocumentProcessorException ( "currency does not match" ) ; } total += item . getCent ( ) ; } doc . setTotalCent ( total ) ; return doc ; } } 