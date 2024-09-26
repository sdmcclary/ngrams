<<<<<<< HEAD
public final class FormFields { private HashMap map = new HashMap ( ) ; private ArrayList list = new ArrayList ( ) ; private FormFields ( ) { } static FormFields construct ( Segment segment ) { FormFields formFields = new FormFields ( ) ; formFields . loadInputControls ( segment ) ; formFields . loadTextAreaControls ( segment ) ; formFields . loadButtonControls ( segment ) ; formFields . loadSelectControls ( segment ) ; Collections . sort ( formFields . list , FormField . COMPARATOR ) ; return formFields ; } public int getCount ( ) { return list . size ( ) ; } public int size ( ) { return getCount ( ) ; } public FormField get ( String name ) { return ( FormField ) map . get ( name . toLowerCase ( ) ) ; } public Iterator iterator ( ) { return list . iterator ( ) ; } public void merge ( FormFields formFields ) { for ( Iterator i = formFields . iterator ( ) ; i . hasNext ( ) ; ) { FormField formField = ( FormField ) i . next ( ) ; String name = formField . getName ( ) ; FormField existingFormField = get ( name ) ; if ( existingFormField == null ) add ( formField ) ; else existingFormField . merge ( formField ) ; } Collections . sort ( list , FormField . COMPARATOR ) ; } public String toString ( ) { StringBuffer sb = new StringBuffer ( ) ; for ( Iterator i = iterator ( ) ; i . hasNext ( ) ; ) { sb . append ( i . next ( ) ) ; } return sb . toString ( ) ; } private void add ( FormField formField ) { map . put ( formField . getName ( ) . toLowerCase ( ) , formField ) ; list . add ( formField ) ; } private void loadInputControls ( Segment segment ) { for ( Iterator i = segment . findAllStartTags ( Tag . INPUT ) . iterator ( ) ; i . hasNext ( ) ; ) { StartTag startTag = ( StartTag ) i . next ( ) ; Attributes attributes = startTag . getAttributes ( ) ; Attribute nameAttribute = attributes . get ( "name" ) ; if ( nameAttribute == null ) continue ; FormControlType formControlType = startTag . getFormControlType ( ) ; if ( formControlType == null ) continue ; String predefinedValue = null ; if ( formControlType . isPredefinedValue ( ) ) { Attribute valueAttribute = attributes . get ( "value" ) ; if ( valueAttribute != null ) predefinedValue = valueAttribute . getValue ( ) ; } String name = nameAttribute . getValue ( ) ; registerField ( name , startTag . begin , predefinedValue , formControlType ) ; String [ ] additionalSubmitNames = formControlType . getAdditionalSubmitNames ( name ) ; if ( additionalSubmitNames != null ) { for ( int j = 0 ; j < additionalSubmitNames . length ; j ++ ) { registerUserValueField ( additionalSubmitNames [ j ] , startTag . begin ) ; } } } } private void loadTextAreaControls ( Segment segment ) { for ( Iterator i = segment . findAllStartTags ( Tag . TEXTAREA ) . iterator ( ) ; i . hasNext ( ) ; ) { StartTag startTag = ( StartTag ) i . next ( ) ; Attributes attributes = startTag . getAttributes ( ) ; Attribute nameAttribute = attributes . get ( "name" ) ; if ( nameAttribute == null ) continue ; String name = nameAttribute . getValue ( ) ; registerUserValueField ( name , startTag . begin ) ; } } private void loadButtonControls ( Segment segment ) { for ( Iterator i = segment . findAllStartTags ( Tag . BUTTON ) . iterator ( ) ; i . hasNext ( ) ; ) { StartTag startTag = ( StartTag ) i . next ( ) ; FormControlType formControlType = startTag . getFormControlType ( ) ; if ( formControlType == null ) continue ; Attributes attributes = startTag . getAttributes ( ) ; Attribute nameAttribute = attributes . get ( "name" ) ; if ( nameAttribute == null ) continue ; String name = nameAttribute . getValue ( ) ; String predefinedValue = null ; Attribute valueAttribute = attributes . get ( "value" ) ; if ( valueAttribute != null ) predefinedValue = valueAttribute . getValue ( ) ; registerField ( name , startTag . begin , predefinedValue , formControlType ) ; } } private void loadSelectControls ( Segment segment ) { List selectElements = segment . findAllElements ( Tag . SELECT ) ; if ( selectElements . isEmpty ( ) ) return ; List optionTags = segment . findAllStartTags ( Tag . OPTION ) ; if ( optionTags . isEmpty ( ) ) return ; Iterator selectIterator = selectElements . iterator ( ) ; Element selectElement = ( Element ) selectIterator . next ( ) ; Element lastSelectElement = null ; String name = null ; FormControlType formControlType = null ; for ( Iterator optionIterator = optionTags . iterator ( ) ; optionIterator . hasNext ( ) ; ) { StartTag optionTag = ( StartTag ) optionIterator . next ( ) ; while ( optionTag . begin > selectElement . end ) { if ( ! selectIterator . hasNext ( ) ) return ; selectElement = ( Element ) selectIterator . next ( ) ; } if ( selectElement != lastSelectElement ) { if ( optionTag . begin < selectElement . begin ) continue ; StartTag selectTag = selectElement . getStartTag ( ) ; formControlType = selectTag . getFormControlType ( ) ; if ( formControlType == null ) throw new RuntimeException ( "Internal Error: FormControlType not recognised for select tag " + selectTag ) ; Attribute nameAttribute = selectTag . getAttributes ( ) . get ( "name" ) ; if ( nameAttribute == null ) { if ( ! selectIterator . hasNext ( ) ) return ; selectElement = ( Element ) selectIterator . next ( ) ; lastSelectElement = null ; continue ; } name = nameAttribute . getValue ( ) ; if ( name == null ) continue ; } lastSelectElement = selectElement ; String value ; Attribute valueAttribute = optionTag . getAttributes ( ) . get ( "value" ) ; if ( valueAttribute != null ) { value = valueAttribute . getValue ( ) ; } else { Segment valueSegment = optionTag . getFollowingTextSegment ( ) ; value = valueSegment . getSourceTextNoWhitespace ( ) ; if ( value . length ( ) == 0 ) continue ; } registerPredefinedValueField ( name , selectElement . begin , value , formControlType ) ; } } private void registerField ( String name , int position , String predefinedValue , FormControlType formControlType ) { if ( predefinedValue == null ) registerUserValueField ( name , position ) ; else registerPredefinedValueField ( name , position , predefinedValue , formControlType ) ; } private void registerUserValueField ( String name , int position ) { FormField formField = get ( name ) ; if ( formField == null ) { add ( formField = new FormField ( name , position , null ) ) ; } else { formField . setMultipleValues ( ) ; formField . setLowerPosition ( position ) ; } formField . incrementUserValueCount ( ) ; } private void registerPredefinedValueField ( String name , int position , String predefinedValue , FormControlType formControlType ) { FormField formField = get ( name ) ; if ( formField == null ) { add ( formField = new FormField ( name , position , formControlType ) ) ; } else { formField . setMultipleValues ( formControlType ) ; formField . setLowerPosition ( position ) ; } formField . addPredefinedValue ( predefinedValue ) ; } } 
=======
public class SchemaReaderSchemaReceiver implements SchemaReceiver { private final SchemaReader schemaLanguage ; private final PropertyMap properties ; public SchemaReaderSchemaReceiver ( SchemaReader schemaLanguage , PropertyMap properties ) { this . schemaLanguage = schemaLanguage ; this . properties = properties ; } public SchemaFuture installHandlers ( XMLReader xr ) throws SAXException { throw new ReparseException ( ) { public Schema reparse ( SAXSource source ) throws IncorrectSchemaException , SAXException , IOException { return schemaLanguage . createSchema ( source , properties ) ; } } ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
