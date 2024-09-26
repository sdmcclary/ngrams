public class GridPattern extends TextGrid { private ArrayList regExps = new ArrayList ( ) ; private boolean regExpsAreValid = false ; private static final boolean DEBUG = false ; private boolean usesStandardSyntax = false ; public GridPattern ( ) { super ( 3 , 3 ) ; } public GridPattern ( String row1 , String row2 , String row3 ) { super ( Math . max ( Math . max ( row1 . length ( ) , row2 . length ( ) ) , row3 . length ( ) ) , 3 ) ; setTo ( row1 , row2 , row3 ) ; regExpsAreValid = false ; } public boolean usesStandardSyntax ( ) { return usesStandardSyntax ; } public void setUsesStandardSyntax ( boolean b ) { usesStandardSyntax = b ; regExpsAreValid = false ; } public boolean isMatchedBy ( TextGrid grid ) { if ( ! regExpsAreValid ) prepareRegExps ( ) ; for ( int i = 0 ; i < grid . getHeight ( ) ; i ++ ) { String row = ( String ) grid . getRow ( i ) ; String regexp = ( String ) regExps . get ( i ) ; if ( ! row . matches ( regexp ) ) { if ( DEBUG ) System . out . println ( row + " does not match " + regexp ) ; return false ; } } return true ; } private void prepareRegExps ( ) { regExpsAreValid = true ; regExps . clear ( ) ; if ( DEBUG ) System . out . println ( "Trying to match:" ) ; if ( ! usesStandardSyntax ) { Iterator it = getRows ( ) . iterator ( ) ; while ( it . hasNext ( ) ) { String row = ( String ) it . next ( ) ; regExps . add ( makeRegExp ( row ) ) ; if ( DEBUG ) System . out . println ( row + " becomes " + makeRegExp ( row ) ) ; } } else { Iterator it = getRows ( ) . iterator ( ) ; while ( it . hasNext ( ) ) { String row = ( String ) it . next ( ) ; regExps . add ( row ) ; } } } private String makeRegExp ( String pattern ) { String result = "" ; int tokensHandled = 0 ; for ( int i = 0 ; i < pattern . length ( ) && tokensHandled < 3 ; i ++ ) { char c = pattern . charAt ( i ) ; if ( c == '[' ) { result += "[^|:]" ; } else if ( c == '|' ) { result += "[|:]" ; } else if ( c == '-' ) { result += "-" ; } else if ( c == '!' ) { result += "[^-=\\/\\\\+|:]" ; } else if ( c == 'b' ) { result += "[-=\\/\\\\+|:]" ; } else if ( c == '^' ) { result += "[\\/\\\\+|:]" ; } else if ( c == '(' ) { result += "[-=\\/\\\\+]" ; } else if ( c == '~' ) { result += "." ; } else if ( c == '+' ) { result += "\\+" ; } else if ( c == '\\' ) { result += "\\\\" ; } else if ( c == 's' ) { result += "[-=+|:]" ; } else if ( c == 'S' ) { result += "[\\/\\\\]" ; } else if ( c == '*' ) { result += "\\*" ; } else if ( c == '1' ) { result += "[\\\\]" ; } else if ( c == '2' ) { result += "[|:+\\/\\\\]" ; } else if ( c == '3' ) { result += "[\\/]" ; } else if ( c == '4' ) { result += "[-=+\\/\\\\]" ; } else if ( c == '5' ) { result += "[\\\\]" ; } else if ( c == '6' ) { result += "[|:+\\/\\\\]" ; } else if ( c == '7' ) { result += "[\\/]" ; } else if ( c == '8' ) { result += "[-=+\\/\\\\]" ; } else if ( c == '%' ) { if ( i + 1 > pattern . length ( ) ) { throw new RuntimeException ( "Invalid pattern, found % at the end" ) ; } c = pattern . charAt ( ++ i ) ; if ( c == '1' ) { result += "[^\\\\]" ; } else if ( c == '2' ) { result += "[^|:+\\/\\\\]" ; } else if ( c == '3' ) { result += "[^\\/]" ; } else if ( c == '4' ) { result += "[^-=+\\/\\\\]" ; } else if ( c == '5' ) { result += "[^\\\\]" ; } else if ( c == '6' ) { result += "[^|:+\\/\\\\]" ; } else if ( c == '7' ) { result += "[^\\/]" ; } else if ( c == '8' ) { result += "[^-=+\\/\\\\]" ; } } else result += String . valueOf ( c ) ; tokensHandled ++ ; } return result ; } public void setTo ( String row1 , String row2 , String row3 ) { if ( getHeight ( ) != 3 ) throw new RuntimeException ( "This method can only be called for GridPatternS with height 3" ) ; regExpsAreValid = false ; writeStringTo ( 0 , 0 , row1 ) ; writeStringTo ( 0 , 1 , row2 ) ; writeStringTo ( 0 , 2 , row3 ) ; } public static void main ( String [ ] args ) { TextGrid grid = new TextGrid ( 3 , 3 ) ; grid . setRow ( 0 , "---" ) ; grid . setRow ( 1 , " / " ) ; grid . setRow ( 2 , "---" ) ; grid . printDebug ( ) ; if ( GridPatternGroup . loneDiagonalCriteria . isAnyMatchedBy ( grid ) ) { System . out . println ( "Grid is lone diagonal" ) ; } else { System . out . println ( "Grid is not lone diagonal" ) ; } grid . setRow ( 0 , "--/" ) ; grid . setRow ( 1 , " / " ) ; grid . setRow ( 2 , "---" ) ; grid . printDebug ( ) ; if ( GridPatternGroup . loneDiagonalCriteria . isAnyMatchedBy ( grid ) ) { System . out . println ( "Grid is lone diagonal" ) ; } else { System . out . println ( "Grid is not lone diagonal" ) ; } grid . setRow ( 0 , "-- " ) ; grid . setRow ( 1 , " \\ " ) ; grid . setRow ( 2 , "---" ) ; grid . printDebug ( ) ; if ( GridPatternGroup . loneDiagonalCriteria . isAnyMatchedBy ( grid ) ) { System . out . println ( "Grid is lone diagonal" ) ; } else { System . out . println ( "Grid is not lone diagonal" ) ; } grid . setRow ( 0 , "-- " ) ; grid . setRow ( 1 , " \\ " ) ; grid . setRow ( 2 , "--\\" ) ; grid . printDebug ( ) ; if ( GridPatternGroup . loneDiagonalCriteria . isAnyMatchedBy ( grid ) ) { System . out . println ( "Grid is lone diagonal" ) ; } else { System . out . println ( "Grid is not lone diagonal" ) ; } grid . setRow ( 0 , "   " ) ; grid . setRow ( 1 , "-\\/" ) ; grid . setRow ( 2 , " ||" ) ; grid . printDebug ( ) ; if ( GridPatternGroup . loneDiagonalCriteria . isAnyMatchedBy ( grid ) ) { System . out . println ( "Grid is lone diagonal" ) ; } else { System . out . println ( "Grid is not lone diagonal" ) ; } } } 