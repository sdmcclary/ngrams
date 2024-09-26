public class TypeAwareDocumentProcessor implements DocumentProcessor { private DocumentProcessor defaultProcessor ; private Map < String , DocumentProcessor > typeProcessors ; public Document processDocument ( Document doc ) throws DocumentProcessorException { Document . Type dt = doc . getType ( ) ; if ( this . typeProcessors . containsKey ( dt . name ( ) ) ) { return this . typeProcessors . get ( dt . name ( ) ) . processDocument ( doc ) ; } if ( this . defaultProcessor != null ) { return this . defaultProcessor . processDocument ( doc ) ; } throw new DocumentProcessorException ( "please specify at least one processor" ) ; } public Map < String , DocumentProcessor > getTypeProcessors ( ) { return typeProcessors ; } public void setTypeProcessors ( Map < String , DocumentProcessor > typeProcessors ) { this . typeProcessors = typeProcessors ; } public DocumentProcessor getDefaultProcessor ( ) { return defaultProcessor ; } public void setDefaultProcessor ( DocumentProcessor defaultProcessor ) { this . defaultProcessor = defaultProcessor ; } } 