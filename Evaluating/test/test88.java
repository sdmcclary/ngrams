<<<<<<< HEAD
public class CmdLineParser { public static abstract class OptionException extends Exception { OptionException ( String msg ) { super ( msg ) ; } } public static class UnknownOptionException extends OptionException { UnknownOptionException ( String optionName ) { this ( optionName , "Unknown option '" + optionName + "'" ) ; } UnknownOptionException ( String optionName , String msg ) { super ( msg ) ; this . optionName = optionName ; } public String getOptionName ( ) { return this . optionName ; } private final String optionName ; } public static class UnknownSuboptionException extends UnknownOptionException { private char suboption ; UnknownSuboptionException ( String option , char suboption ) { super ( option , "Illegal option: '" + suboption + "' in '" + option + "'" ) ; this . suboption = suboption ; } public char getSuboption ( ) { return suboption ; } } public static class NotFlagException extends UnknownOptionException { private char notflag ; NotFlagException ( String option , char unflaggish ) { super ( option , "Illegal option: '" + option + "', '" + unflaggish + "' requires a value" ) ; notflag = unflaggish ; } public char getOptionChar ( ) { return notflag ; } } public static class IllegalOptionValueException extends OptionException { public < T > IllegalOptionValueException ( Option < T > opt , String value ) { super ( "Illegal value '" + value + "' for option " + ( opt . shortForm ( ) != null ? "-" + opt . shortForm ( ) + "/" : "" ) + "--" + opt . longForm ( ) ) ; this . option = opt ; this . value = value ; } public Option < ? > getOption ( ) { return this . option ; } public String getValue ( ) { return this . value ; } private final Option < ? > option ; private final String value ; } public static abstract class Option < T > { protected Option ( String longForm , boolean wantsValue ) { this ( null , longForm , wantsValue ) ; } protected Option ( char shortForm , String longForm , boolean wantsValue ) { this ( new String ( new char [ ] { shortForm } ) , longForm , wantsValue ) ; } private Option ( String shortForm , String longForm , boolean wantsValue ) { if ( longForm == null ) { throw new IllegalArgumentException ( "Null longForm not allowed" ) ; } this . shortForm = shortForm ; this . longForm = longForm ; this . wantsValue = wantsValue ; } public String shortForm ( ) { return this . shortForm ; } public String longForm ( ) { return this . longForm ; } public boolean wantsValue ( ) { return this . wantsValue ; } public final T getValue ( String arg , Locale locale ) throws IllegalOptionValueException { if ( this . wantsValue ) { if ( arg == null ) { throw new IllegalOptionValueException ( this , "" ) ; } return this . parseValue ( arg , locale ) ; } else { return this . getDefaultValue ( ) ; } } protected T parseValue ( String arg , Locale locale ) throws IllegalOptionValueException { return null ; } protected T getDefaultValue ( ) { return null ; } private final String shortForm ; private final String longForm ; private final boolean wantsValue ; public static class BooleanOption extends Option < Boolean > { public BooleanOption ( char shortForm , String longForm ) { super ( shortForm , longForm , false ) ; } public BooleanOption ( String longForm ) { super ( longForm , false ) ; } @ Override public Boolean parseValue ( String arg , Locale lcoale ) { return Boolean . TRUE ; } @ Override public Boolean getDefaultValue ( ) { return Boolean . TRUE ; } } public static class IntegerOption extends Option < Integer > { public IntegerOption ( char shortForm , String longForm ) { super ( shortForm , longForm , true ) ; } public IntegerOption ( String longForm ) { super ( longForm , true ) ; } @ Override protected Integer parseValue ( String arg , Locale locale ) throws IllegalOptionValueException { try { return new Integer ( arg ) ; } catch ( NumberFormatException e ) { throw new IllegalOptionValueException ( this , arg ) ; } } } public static class LongOption extends Option < Long > { public LongOption ( char shortForm , String longForm ) { super ( shortForm , longForm , true ) ; } public LongOption ( String longForm ) { super ( longForm , true ) ; } @ Override protected Long parseValue ( String arg , Locale locale ) throws IllegalOptionValueException { try { return new Long ( arg ) ; } catch ( NumberFormatException e ) { throw new IllegalOptionValueException ( this , arg ) ; } } } public static class DoubleOption extends Option < Double > { public DoubleOption ( char shortForm , String longForm ) { super ( shortForm , longForm , true ) ; } public DoubleOption ( String longForm ) { super ( longForm , true ) ; } @ Override protected Double parseValue ( String arg , Locale locale ) throws IllegalOptionValueException { try { NumberFormat format = NumberFormat . getNumberInstance ( locale ) ; Number num = ( Number ) format . parse ( arg ) ; return new Double ( num . doubleValue ( ) ) ; } catch ( ParseException e ) { throw new IllegalOptionValueException ( this , arg ) ; } } } public static class StringOption extends Option < String > { public StringOption ( char shortForm , String longForm ) { super ( shortForm , longForm , true ) ; } public StringOption ( String longForm ) { super ( longForm , true ) ; } @ Override protected String parseValue ( String arg , Locale locale ) { return arg ; } } } public final < T > Option < T > addOption ( Option < T > opt ) { if ( opt . shortForm ( ) != null ) { this . options . put ( "-" + opt . shortForm ( ) , opt ) ; } this . options . put ( "--" + opt . longForm ( ) , opt ) ; return opt ; } public final Option < String > addStringOption ( char shortForm , String longForm ) { return addOption ( new Option . StringOption ( shortForm , longForm ) ) ; } public final Option < String > addStringOption ( String longForm ) { return addOption ( new Option . StringOption ( longForm ) ) ; } public final Option < Integer > addIntegerOption ( char shortForm , String longForm ) { return addOption ( new Option . IntegerOption ( shortForm , longForm ) ) ; } public final Option < Integer > addIntegerOption ( String longForm ) { return addOption ( new Option . IntegerOption ( longForm ) ) ; } public final Option < Long > addLongOption ( char shortForm , String longForm ) { return addOption ( new Option . LongOption ( shortForm , longForm ) ) ; } public final Option < Long > addLongOption ( String longForm ) { return addOption ( new Option . LongOption ( longForm ) ) ; } public final Option < Double > addDoubleOption ( char shortForm , String longForm ) { return addOption ( new Option . DoubleOption ( shortForm , longForm ) ) ; } public final Option < Double > addDoubleOption ( String longForm ) { return addOption ( new Option . DoubleOption ( longForm ) ) ; } public final Option < Boolean > addBooleanOption ( char shortForm , String longForm ) { return addOption ( new Option . BooleanOption ( shortForm , longForm ) ) ; } public final Option < Boolean > addBooleanOption ( String longForm ) { return addOption ( new Option . BooleanOption ( longForm ) ) ; } public final < T > T getOptionValue ( Option < T > o ) { return getOptionValue ( o , null ) ; } public final < T > T getOptionValue ( Option < T > o , T def ) { List < ? > v = values . get ( o . longForm ( ) ) ; if ( v == null ) { return def ; } else if ( v . isEmpty ( ) ) { return null ; } else { @ SuppressWarnings ( "unchecked" ) T result = ( T ) v . remove ( 0 ) ; return result ; } } public final < T > Collection < T > getOptionValues ( Option < T > option ) { Collection < T > result = new ArrayList < T > ( ) ; while ( true ) { T o = getOptionValue ( option , null ) ; if ( o == null ) { return result ; } else { result . add ( o ) ; } } } public final String [ ] getRemainingArgs ( ) { return this . remainingArgs ; } public final void parse ( String [ ] argv ) throws OptionException { parse ( argv , Locale . getDefault ( ) ) ; } public final void parse ( String [ ] argv , Locale locale ) throws OptionException { ArrayList < Object > otherArgs = new ArrayList < Object > ( ) ; int position = 0 ; this . values = new HashMap < String , List < ? > > ( 10 ) ; while ( position < argv . length ) { String curArg = argv [ position ] ; if ( curArg . startsWith ( "-" ) ) { if ( curArg . equals ( "--" ) ) { position += 1 ; break ; } String valueArg = null ; if ( curArg . startsWith ( "--" ) ) { int equalsPos = curArg . indexOf ( "=" ) ; if ( equalsPos != - 1 ) { valueArg = curArg . substring ( equalsPos + 1 ) ; curArg = curArg . substring ( 0 , equalsPos ) ; } } else if ( curArg . length ( ) > 2 ) { for ( int i = 1 ; i < curArg . length ( ) ; i ++ ) { Option < ? > opt = this . options . get ( "-" + curArg . charAt ( i ) ) ; if ( opt == null ) { throw new UnknownSuboptionException ( curArg , curArg . charAt ( i ) ) ; } if ( opt . wantsValue ( ) ) { throw new NotFlagException ( curArg , curArg . charAt ( i ) ) ; } addValue ( opt , null , locale ) ; } position ++ ; continue ; } Option < ? > opt = this . options . get ( curArg ) ; if ( opt == null ) { throw new UnknownOptionException ( curArg ) ; } if ( opt . wantsValue ( ) ) { if ( valueArg == null ) { position += 1 ; if ( position < argv . length ) { valueArg = argv [ position ] ; } } addValue ( opt , valueArg , locale ) ; } else { addValue ( opt , null , locale ) ; } position += 1 ; } else { otherArgs . add ( curArg ) ; position += 1 ; } } for ( ; position < argv . length ; ++ position ) { otherArgs . add ( argv [ position ] ) ; } this . remainingArgs = new String [ otherArgs . size ( ) ] ; remainingArgs = otherArgs . toArray ( remainingArgs ) ; } private < T > void addValue ( Option < T > opt , String valueArg , Locale locale ) throws IllegalOptionValueException { T value = opt . getValue ( valueArg , locale ) ; String lf = opt . longForm ( ) ; @ SuppressWarnings ( "unchecked" ) List < T > v = ( List < T > ) values . get ( lf ) ; if ( v == null ) { v = new ArrayList < T > ( ) ; values . put ( lf , v ) ; } v . add ( value ) ; } private String [ ] remainingArgs = null ; private Map < String , Option < ? > > options = new HashMap < String , Option < ? > > ( 10 ) ; private Map < String , List < ? > > values = new HashMap < String , List < ? > > ( 10 ) ; } 
=======
class AttributeNameClassChecker implements NameClassVisitor { private String errorMessageId = null ; public void visitChoice ( NameClass nc1 , NameClass nc2 ) { nc1 . accept ( this ) ; nc2 . accept ( this ) ; } public void visitNsName ( String ns ) { if ( ns . equals ( WellKnownNamespaces . XMLNS ) ) errorMessageId = "xmlns_uri_attribute" ; } public void visitNsNameExcept ( String ns , NameClass nc ) { visitNsName ( ns ) ; nc . accept ( this ) ; } public void visitAnyName ( ) { } public void visitAnyNameExcept ( NameClass nc ) { nc . accept ( this ) ; } public void visitName ( Name name ) { visitNsName ( name . getNamespaceUri ( ) ) ; if ( name . equals ( new Name ( "" , "xmlns" ) ) ) errorMessageId = "xmlns_attribute" ; } public void visitNull ( ) { } public void visitError ( ) { } String checkNameClass ( NameClass nc ) { errorMessageId = null ; nc . accept ( this ) ; return errorMessageId ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
