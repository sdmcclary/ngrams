public class NewSaxonSchemaReaderFactory extends SchematronSchemaReaderFactory { public SAXTransformerFactory newTransformerFactory ( ) { return new TransformerFactoryImpl ( ) ; } public void initTransformerFactory ( TransformerFactory factory ) { factory . setAttribute ( FeatureKeys . LINE_NUMBERING , Boolean . TRUE ) ; factory . setAttribute ( FeatureKeys . VERSION_WARNING , Boolean . FALSE ) ; } } 