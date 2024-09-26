<<<<<<< HEAD
public class JSParseNodeTypes { protected JSParseNodeTypes ( ) { } public static final int ERROR = - 1 ; public static final int UNKNOWN = 0 ; public static final int ASSIGN = 1 ; public static final int ADD_AND_ASSIGN = 2 ; public static final int ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN = 3 ; public static final int BITWISE_AND_AND_ASSIGN = 4 ; public static final int BITWISE_OR_AND_ASSIGN = 5 ; public static final int BITWISE_XOR_AND_ASSIGN = 6 ; public static final int DIVIDE_AND_ASSIGN = 7 ; public static final int MOD_AND_ASSIGN = 8 ; public static final int MULTIPLY_AND_ASSIGN = 9 ; public static final int SHIFT_LEFT_AND_ASSIGN = 10 ; public static final int SHIFT_RIGHT_AND_ASSIGN = 11 ; public static final int SUBTRACT_AND_ASSIGN = 12 ; public static final int GET_ELEMENT = 13 ; public static final int GET_PROPERTY = 14 ; public static final int EQUAL = 15 ; public static final int GREATER_THAN = 16 ; public static final int GREATER_THAN_OR_EQUAL = 17 ; public static final int IDENTITY = 18 ; public static final int IN = 19 ; public static final int INSTANCE_OF = 20 ; public static final int LESS_THAN = 21 ; public static final int LESS_THAN_OR_EQUAL = 22 ; public static final int LOGICAL_AND = 23 ; public static final int LOGICAL_OR = 24 ; public static final int NOT_EQUAL = 25 ; public static final int NOT_IDENTITY = 26 ; public static final int ADD = 27 ; public static final int ARITHMETIC_SHIFT_RIGHT = 28 ; public static final int BITWISE_AND = 29 ; public static final int BITWISE_OR = 30 ; public static final int BITWISE_XOR = 31 ; public static final int DIVIDE = 32 ; public static final int MOD = 33 ; public static final int MULTIPLY = 34 ; public static final int SHIFT_LEFT = 35 ; public static final int SHIFT_RIGHT = 36 ; public static final int SUBTRACT = 37 ; public static final int CATCH = 38 ; public static final int CONDITIONAL = 39 ; public static final int CONSTRUCT = 40 ; public static final int DECLARATION = 41 ; public static final int DO = 42 ; public static final int EMPTY = 43 ; public static final int FINALLY = 44 ; public static final int FOR_IN = 45 ; public static final int FOR = 46 ; public static final int FUNCTION = 47 ; public static final int IF = 48 ; public static final int INVOKE = 49 ; public static final int LABELLED = 50 ; public static final int BREAK = 51 ; public static final int CONTINUE = 52 ; public static final int ARGUMENTS = 53 ; public static final int ARRAY_LITERAL = 54 ; public static final int COMMA = 55 ; public static final int DEFAULT = 56 ; public static final int CASE = 57 ; public static final int SWITCH = 58 ; public static final int OBJECT_LITERAL = 59 ; public static final int PARAMETERS = 60 ; public static final int STATEMENTS = 61 ; public static final int VAR = 62 ; public static final int FALSE = 63 ; public static final int IDENTIFIER = 64 ; public static final int NULL = 65 ; public static final int NUMBER = 66 ; public static final int REGULAR_EXPRESSION = 67 ; public static final int STRING = 68 ; public static final int TRUE = 69 ; public static final int NAME_VALUE_PAIR = 70 ; public static final int THIS = 71 ; public static final int TRY = 72 ; public static final int DELETE = 73 ; public static final int GROUP = 74 ; public static final int LOGICAL_NOT = 75 ; public static final int BITWISE_NOT = 76 ; public static final int NEGATE = 77 ; public static final int POSITIVE = 78 ; public static final int POST_DECREMENT = 79 ; public static final int POST_INCREMENT = 80 ; public static final int PRE_DECREMENT = 81 ; public static final int PRE_INCREMENT = 82 ; public static final int RETURN = 83 ; public static final int THROW = 84 ; public static final int TYPEOF = 85 ; public static final int VOID = 86 ; public static final int WHILE = 87 ; public static final int WITH = 88 ; public static final int MAX_VALUE = 88 ; public static String [ ] getNames ( ) { String [ ] result = new String [ MAX_VALUE + 1 ] ; for ( int i = 0 ; i <= MAX_VALUE ; i ++ ) { result [ i ] = getName ( i ) ; } return result ; } public static String getName ( int type ) { switch ( type ) { case ERROR : return "ERROR" ; case UNKNOWN : return "UNKNOWN" ; case ASSIGN : return "ASSIGN" ; case ADD_AND_ASSIGN : return "ADD_AND_ASSIGN" ; case ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN : return "ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN" ; case BITWISE_AND_AND_ASSIGN : return "BITWISE_AND_AND_ASSIGN" ; case BITWISE_OR_AND_ASSIGN : return "BITWISE_OR_AND_ASSIGN" ; case BITWISE_XOR_AND_ASSIGN : return "BITWISE_XOR_AND_ASSIGN" ; case DIVIDE_AND_ASSIGN : return "DIVIDE_AND_ASSIGN" ; case MOD_AND_ASSIGN : return "MOD_AND_ASSIGN" ; case MULTIPLY_AND_ASSIGN : return "MULTIPLY_AND_ASSIGN" ; case SHIFT_LEFT_AND_ASSIGN : return "SHIFT_LEFT_AND_ASSIGN" ; case SHIFT_RIGHT_AND_ASSIGN : return "SHIFT_RIGHT_AND_ASSIGN" ; case SUBTRACT_AND_ASSIGN : return "SUBTRACT_AND_ASSIGN" ; case GET_ELEMENT : return "GET_ELEMENT" ; case GET_PROPERTY : return "GET_PROPERTY" ; case EQUAL : return "EQUAL" ; case GREATER_THAN : return "GREATER_THAN" ; case GREATER_THAN_OR_EQUAL : return "GREATER_THAN_OR_EQUAL" ; case IDENTITY : return "IDENTITY" ; case IN : return "IN" ; case INSTANCE_OF : return "INSTANCE_OF" ; case LESS_THAN : return "LESS_THAN" ; case LESS_THAN_OR_EQUAL : return "LESS_THAN_OR_EQUAL" ; case LOGICAL_AND : return "LOGICAL_AND" ; case LOGICAL_OR : return "LOGICAL_OR" ; case NOT_EQUAL : return "NOT_EQUAL" ; case NOT_IDENTITY : return "NOT_IDENTITY" ; case ADD : return "ADD" ; case ARITHMETIC_SHIFT_RIGHT : return "ARITHMETIC_SHIFT_RIGHT" ; case BITWISE_AND : return "BITWISE_AND" ; case BITWISE_OR : return "BITWISE_OR" ; case BITWISE_XOR : return "BITWISE_XOR" ; case DIVIDE : return "DIVIDE" ; case MOD : return "MOD" ; case MULTIPLY : return "MULTIPLY" ; case SHIFT_LEFT : return "SHIFT_LEFT" ; case SHIFT_RIGHT : return "SHIFT_RIGHT" ; case SUBTRACT : return "SUBTRACT" ; case CATCH : return "CATCH" ; case CONDITIONAL : return "CONDITIONAL" ; case CONSTRUCT : return "CONSTRUCT" ; case DECLARATION : return "DECLARATION" ; case DO : return "DO" ; case EMPTY : return "EMPTY" ; case FINALLY : return "FINALLY" ; case FOR_IN : return "FOR_IN" ; case FOR : return "FOR" ; case FUNCTION : return "FUNCTION" ; case IF : return "IF" ; case INVOKE : return "INVOKE" ; case LABELLED : return "LABELLED" ; case BREAK : return "BREAK" ; case CONTINUE : return "CONTINUE" ; case ARGUMENTS : return "ARGUMENTS" ; case ARRAY_LITERAL : return "ARRAY_LITERAL" ; case COMMA : return "COMMA" ; case DEFAULT : return "DEFAULT" ; case CASE : return "CASE" ; case SWITCH : return "SWITCH" ; case OBJECT_LITERAL : return "OBJECT_LITERAL" ; case PARAMETERS : return "PARAMETERS" ; case STATEMENTS : return "STATEMENTS" ; case VAR : return "VAR" ; case FALSE : return "FALSE" ; case IDENTIFIER : return "IDENTIFIER" ; case NULL : return "NULL" ; case NUMBER : return "NUMBER" ; case REGULAR_EXPRESSION : return "REGULAR_EXPRESSION" ; case STRING : return "STRING" ; case TRUE : return "TRUE" ; case NAME_VALUE_PAIR : return "NAME_VALUE_PAIR" ; case THIS : return "THIS" ; case TRY : return "TRY" ; case DELETE : return "DELETE" ; case GROUP : return "GROUP" ; case LOGICAL_NOT : return "LOGICAL_NOT" ; case BITWISE_NOT : return "BITWISE_NOT" ; case NEGATE : return "NEGATE" ; case POSITIVE : return "POSITIVE" ; case POST_DECREMENT : return "POST_DECREMENT" ; case POST_INCREMENT : return "POST_INCREMENT" ; case PRE_DECREMENT : return "PRE_DECREMENT" ; case PRE_INCREMENT : return "PRE_INCREMENT" ; case RETURN : return "RETURN" ; case THROW : return "THROW" ; case TYPEOF : return "TYPEOF" ; case VOID : return "VOID" ; case WHILE : return "WHILE" ; case WITH : return "WITH" ; default : return "<unknown>" ; } } public static int getIntValue ( String name ) { Class < ? > c = JSParseNodeTypes . class ; int result = - 1 ; try { Field f = c . getField ( name ) ; result = f . getInt ( c ) ; } catch ( SecurityException e ) { } catch ( NoSuchFieldException e ) { } catch ( IllegalArgumentException e ) { } catch ( IllegalAccessException e ) { } return result ; } } 
=======
public class Html5DatatypeException extends DatatypeException { private Class datatypeClass ; private String [ ] segments ; final boolean warning ; public Html5DatatypeException ( int index , Class datatypeClass , String datatypeName , String message ) { super ( index , "Bad " + datatypeName + ": " + message ) ; this . datatypeClass = datatypeClass ; this . segments = new String [ 1 ] ; this . segments [ 0 ] = message ; this . warning = false ; } public Html5DatatypeException ( int index , Class datatypeClass , String datatypeName , String head , String literal , String tail ) { super ( index , "Bad " + datatypeName + ": " + head + '“' + literal + '”' + tail ) ; this . datatypeClass = datatypeClass ; this . segments = new String [ 3 ] ; this . segments [ 0 ] = head ; this . segments [ 1 ] = literal ; this . segments [ 2 ] = tail ; this . warning = false ; } public Html5DatatypeException ( Class datatypeClass , String datatypeName , String message ) { this ( - 1 , datatypeClass , datatypeName , message ) ; } public Html5DatatypeException ( Class datatypeClass , String datatypeName , String head , String literal , String tail ) { this ( - 1 , datatypeClass , datatypeName , head , literal , tail ) ; } public Html5DatatypeException ( int index , Class datatypeClass , String datatypeName , String message , boolean warning ) { super ( index , "Bad " + datatypeName + ": " + message ) ; this . datatypeClass = datatypeClass ; this . segments = new String [ 1 ] ; this . segments [ 0 ] = message ; this . warning = warning ; } public Html5DatatypeException ( int index , Class datatypeClass , String datatypeName , String head , String literal , String tail , boolean warning ) { super ( index , "Bad " + datatypeName + ": " + head + '“' + literal + '”' + tail ) ; this . datatypeClass = datatypeClass ; this . segments = new String [ 3 ] ; this . segments [ 0 ] = head ; this . segments [ 1 ] = literal ; this . segments [ 2 ] = tail ; this . warning = warning ; } public Html5DatatypeException ( Class datatypeClass , String datatypeName , String message , boolean warning ) { this ( - 1 , datatypeClass , datatypeName , message , warning ) ; } public Html5DatatypeException ( Class datatypeClass , String datatypeName , String head , String literal , String tail , boolean warning ) { this ( - 1 , datatypeClass , datatypeName , head , literal , tail , warning ) ; } public Class getDatatypeClass ( ) { return datatypeClass ; } public String [ ] getSegments ( ) { return segments ; } public boolean isWarning ( ) { return warning ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
