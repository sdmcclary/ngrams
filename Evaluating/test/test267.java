class SchemaReceiverImpl implements SchemaReceiver { private static final String MNS_SCHEMA = "mns.rng" ; private static final String RNC_MEDIA_TYPE = "application/x-rnc" ; private final PropertyMap properties ; private final PropertyMap attributeSchemaProperties ; private final boolean attributesSchema ; private final SchemaReader autoSchemaLanguage ; private Schema mnsSchema = null ; public SchemaReceiverImpl ( PropertyMap properties ) { Name attributeOwner = properties . get ( WrapProperty . ATTRIBUTE_OWNER ) ; attributesSchema = ( attributeOwner != null ) ; PropertyMapBuilder builder = new PropertyMapBuilder ( properties ) ; if ( ValidatorImpl . OWNER_NAME . equals ( attributeOwner ) ) { attributeSchemaProperties = properties ; builder . put ( WrapProperty . ATTRIBUTE_OWNER , null ) ; this . properties = builder . toPropertyMap ( ) ; } else { if ( attributeOwner == null ) this . properties = properties ; else { builder . put ( WrapProperty . ATTRIBUTE_OWNER , null ) ; this . properties = builder . toPropertyMap ( ) ; } builder . put ( WrapProperty . ATTRIBUTE_OWNER , ValidatorImpl . OWNER_NAME ) ; attributeSchemaProperties = builder . toPropertyMap ( ) ; } this . autoSchemaLanguage = new AutoSchemaReader ( properties . get ( SchemaReceiverFactory . PROPERTY ) ) ; } public SchemaFuture installHandlers ( XMLReader xr ) { return new SchemaImpl ( attributesSchema ) . installHandlers ( xr , this ) ; } Schema getMnsSchema ( ) throws IOException , IncorrectSchemaException , SAXException { if ( mnsSchema == null ) { String className = SchemaReceiverImpl . class . getName ( ) ; String resourceName = className . substring ( 0 , className . lastIndexOf ( '.' ) ) . replace ( '.' , '/' ) + "/resources/" + MNS_SCHEMA ; URL mnsSchemaUrl = getResource ( resourceName ) ; mnsSchema = SAXSchemaReader . getInstance ( ) . createSchema ( new InputSource ( mnsSchemaUrl . toString ( ) ) , properties ) ; } return mnsSchema ; } private static URL getResource ( String resourceName ) { ClassLoader cl = SchemaReceiverImpl . class . getClassLoader ( ) ; if ( cl == null ) return ClassLoader . getSystemResource ( resourceName ) ; else return cl . getResource ( resourceName ) ; } PropertyMap getProperties ( ) { return properties ; } Schema createChildSchema ( InputSource inputSource , String schemaType , boolean isAttributesSchema ) throws IOException , IncorrectSchemaException , SAXException { SchemaReader lang = isRnc ( schemaType ) ? CompactSchemaReader . getInstance ( ) : autoSchemaLanguage ; return lang . createSchema ( inputSource , isAttributesSchema ? attributeSchemaProperties : properties ) ; } private static boolean isRnc ( String schemaType ) { if ( schemaType == null ) return false ; schemaType = schemaType . trim ( ) ; return schemaType . equals ( RNC_MEDIA_TYPE ) ; } } 