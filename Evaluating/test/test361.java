class SchemaImpl extends AbstractSchema { private final Templates templates ; private final Class < ? extends SAXTransformerFactory > factoryClass ; SchemaImpl ( Templates templates , Class < ? extends SAXTransformerFactory > factoryClass , PropertyMap properties , PropertyId < ? > [ ] supportedPropertyIds ) { super ( properties , supportedPropertyIds ) ; this . templates = templates ; this . factoryClass = factoryClass ; } public Validator createValidator ( PropertyMap properties ) { try { return new ValidatorImpl ( templates , factoryClass . newInstance ( ) , properties ) ; } catch ( InstantiationException e ) { throw new RuntimeException ( "unexpected InstantiationException creating SAXTransformerFactory" ) ; } catch ( IllegalAccessException e ) { throw new RuntimeException ( "unexpected IllegalAccessException creating SAXTransformerFactory" ) ; } } } 