public abstract class AbstractSchemaReader implements SchemaReader { public Schema createSchema ( InputSource in , PropertyMap properties ) throws IOException , SAXException , IncorrectSchemaException { return createSchema ( new SAXSource ( in ) , properties ) ; } } 