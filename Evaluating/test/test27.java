public class RegexSyntaxException extends Exception { private final int position ; static public final int UNKNOWN_POSITION = - 1 ; public RegexSyntaxException ( String detail ) { this ( detail , UNKNOWN_POSITION ) ; } public RegexSyntaxException ( String detail , int position ) { super ( detail ) ; this . position = position ; } public int getPosition ( ) { return position ; } } 