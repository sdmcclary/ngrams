class SchemaImpl extends Schema2 { private final SchemaFactoryImpl factory ; private final SchemaPatternBuilder spb ; private final Pattern start ; SchemaImpl ( SchemaFactoryImpl factory , SchemaPatternBuilder spb , Pattern start ) { this . factory = factory ; this . spb = spb ; this . start = start ; } public ValidatorHandler2 newValidatorHandler ( ) { return new ValidatorHandlerImpl ( factory , start , new ValidatorPatternBuilder ( spb ) ) ; } } 