public interface ValidationContext { String resolveNamespacePrefix ( String prefix ) ; String getBaseUri ( ) ; boolean isUnparsedEntity ( String entityName ) ; boolean isNotation ( String notationName ) ; } 