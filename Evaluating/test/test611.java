public class TypedDescription { private static final Map < String , String > BUILTIN_ALIASES ; private static final String [ ] NO_STRINGS = new String [ 0 ] ; private static final TypedDescription [ ] NO_TYPED_DESCRIPTIONS = new TypedDescription [ 0 ] ; private List < String > _types ; private List < TypedDescription > _defaultValues ; private String _description = "" ; private String _name = "" ; static { BUILTIN_ALIASES = new HashMap < String , String > ( ) ; BUILTIN_ALIASES . put ( "array" , "Array" ) ; BUILTIN_ALIASES . put ( "boolean" , "Boolean" ) ; BUILTIN_ALIASES . put ( "Bool" , "Boolean" ) ; BUILTIN_ALIASES . put ( "bool" , "Boolean" ) ; BUILTIN_ALIASES . put ( "char" , "String" ) ; BUILTIN_ALIASES . put ( "Char" , "String" ) ; BUILTIN_ALIASES . put ( "date" , "Date" ) ; BUILTIN_ALIASES . put ( "double" , "Number" ) ; BUILTIN_ALIASES . put ( "Double" , "Number" ) ; BUILTIN_ALIASES . put ( "error" , "Error" ) ; BUILTIN_ALIASES . put ( "float" , "Number" ) ; BUILTIN_ALIASES . put ( "Float" , "Number" ) ; BUILTIN_ALIASES . put ( "function" , "Function" ) ; BUILTIN_ALIASES . put ( "int" , "Number" ) ; BUILTIN_ALIASES . put ( "Int" , "Number" ) ; BUILTIN_ALIASES . put ( "integer" , "Number" ) ; BUILTIN_ALIASES . put ( "Integer" , "Number" ) ; BUILTIN_ALIASES . put ( "number" , "Number" ) ; BUILTIN_ALIASES . put ( "object" , "Object" ) ; BUILTIN_ALIASES . put ( "regEx" , "RegExp" ) ; BUILTIN_ALIASES . put ( "RegEx" , "RegExp" ) ; BUILTIN_ALIASES . put ( "regex" , "RegExp" ) ; BUILTIN_ALIASES . put ( "regExp" , "RegExp" ) ; BUILTIN_ALIASES . put ( "regexp" , "RegExp" ) ; BUILTIN_ALIASES . put ( "string" , "String" ) ; } public TypedDescription ( ) { } public TypedDescription ( String description ) { this . setDescription ( description ) ; } public TypedDescription ( String description , String name ) { this . setDescription ( description ) ; this . setName ( name ) ; } public void addDefaultValue ( TypedDescription value ) { if ( value != null ) { if ( this . _defaultValues == null ) { this . _defaultValues = new ArrayList < TypedDescription > ( ) ; } this . _defaultValues . add ( value ) ; } } public void addType ( String value ) { value = ( value == null ) ? "" : value ; if ( this . _types == null ) { this . _types = new ArrayList < String > ( ) ; } this . _types . add ( checkForBuiltInAlias ( value ) ) ; } private String checkForBuiltInAlias ( String value ) { if ( BUILTIN_ALIASES . containsKey ( value ) ) { return BUILTIN_ALIASES . get ( value ) ; } else { return value ; } } public void clearDefaultValues ( ) { if ( this . _defaultValues != null ) { this . _defaultValues . clear ( ) ; } } public void clearTypes ( ) { if ( this . _types != null ) { this . _types . clear ( ) ; } } public TypedDescription [ ] getDefaultValues ( ) { TypedDescription [ ] result = NO_TYPED_DESCRIPTIONS ; if ( this . _defaultValues != null ) { result = this . _defaultValues . toArray ( new TypedDescription [ this . _defaultValues . size ( ) ] ) ; } return result ; } public String getDescription ( ) { return this . _description ; } public String getName ( ) { return this . _name ; } public String [ ] getTypes ( ) { String [ ] result = NO_STRINGS ; if ( this . _types != null ) { result = this . _types . toArray ( new String [ this . _types . size ( ) ] ) ; ; } return result ; } public void read ( DataInput input ) throws IOException { int size = input . readInt ( ) ; if ( size > 0 ) { this . _defaultValues = new ArrayList < TypedDescription > ( ) ; for ( int i = 0 ; i < size ; i ++ ) { TypedDescription param = new TypedDescription ( ) ; param . read ( input ) ; this . _defaultValues . add ( param ) ; } } size = input . readInt ( ) ; if ( size > 0 ) { this . _types = new ArrayList < String > ( ) ; for ( int i = 0 ; i < size ; i ++ ) { String type = input . readUTF ( ) ; this . _types . add ( type ) ; } } this . _description = input . readUTF ( ) ; this . _name = input . readUTF ( ) ; } public void setDescription ( String value ) { this . _description = ( value == null ) ? "" : value ; } public void setName ( String value ) { this . _name = ( value == null ) ? "" : value ; } public void write ( DataOutput output ) throws IOException { if ( this . _defaultValues != null ) { output . writeInt ( this . _defaultValues . size ( ) ) ; for ( int i = 0 ; i < this . _defaultValues . size ( ) ; i ++ ) { TypedDescription param = this . _defaultValues . get ( i ) ; param . write ( output ) ; } } else { output . writeInt ( 0 ) ; } if ( this . _types != null ) { output . writeInt ( this . _types . size ( ) ) ; for ( int i = 0 ; i < this . _types . size ( ) ; i ++ ) { output . writeUTF ( this . _types . get ( i ) ) ; } } else { output . writeInt ( 0 ) ; } output . writeUTF ( this . _description ) ; output . writeUTF ( this . _name ) ; } } 