public class NFAGraph { static Stack < Integer > recycle = new Stack < Integer > ( ) ; static NFANode [ ] nodes = new NFANode [ 0 ] ; int _start ; int _end ; public int getAcceptState ( int nodeIndex ) { return this . getItem ( nodeIndex ) . getAcceptState ( ) ; } public NFANode getItem ( int index ) { return nodes [ index ] ; } public int getEnd ( ) { return _end ; } public CharacterSet getInputSet ( ) { CharacterSet result = new CharacterSet ( ) ; for ( int i = 0 ; i < nodes . length ; i ++ ) { Input input = this . getItem ( i ) . getInput ( ) ; if ( input != null ) { result . addMembers ( input . getCharacters ( ) ) ; } } return result ; } public int getStart ( ) { return _start ; } public NFAGraph ( int acceptState ) { this . _start = this . createNewState ( ) ; this . _end = this . _start ; this . getItem ( this . _end ) . setAcceptState ( acceptState ) ; } public void add ( Input input ) { NFANode previousEnd = this . getItem ( this . _end ) ; this . _end = this . createNewState ( ) ; this . getItem ( this . _end ) . setAcceptState ( previousEnd . getAcceptState ( ) ) ; previousEnd . setInput ( input ) ; previousEnd . setNext ( this . _end ) ; previousEnd . setAcceptState ( - 1 ) ; } public void andMachines ( NFAGraph rhs ) { this . getItem ( this . _end ) . copy ( rhs . getItem ( rhs . _start ) ) ; rhs . recycleNode ( rhs . _start ) ; rhs . _start = this . _end ; this . _end = rhs . _end ; } private void applyClosure ( int type ) { int front = this . createNewState ( ) ; int back = this . createNewState ( ) ; this . getItem ( front ) . addEpsilon ( this . _start ) ; if ( type == Closure . KLEENE || type == Closure . OPTION ) { this . getItem ( front ) . addEpsilon ( back ) ; } if ( type == Closure . KLEENE || type == Closure . POSITIVE ) { this . getItem ( this . _end ) . addEpsilon ( this . _start ) ; } this . getItem ( back ) . setAcceptState ( this . getItem ( this . _end ) . getAcceptState ( ) ) ; this . getItem ( this . _end ) . addEpsilon ( back ) ; this . getItem ( this . _end ) . setAcceptState ( - 1 ) ; this . _start = front ; this . _end = back ; } public int createNewState ( ) { int result ; if ( recycle . size ( ) > 0 ) { result = recycle . pop ( ) . intValue ( ) ; } else { result = nodes . length ; NFANode [ ] newNodes = new NFANode [ result + 1 ] ; System . arraycopy ( nodes , 0 , newNodes , 0 , result ) ; newNodes [ result ] = new NFANode ( ) ; nodes = newNodes ; } return result ; } public void kleeneClosure ( ) { this . applyClosure ( Closure . KLEENE ) ; } public void option ( ) { this . applyClosure ( Closure . OPTION ) ; } public void orMachines ( NFAGraph rhs ) { int front = this . createNewState ( ) ; int back = this . createNewState ( ) ; this . getItem ( front ) . addEpsilon ( this . _start ) ; this . getItem ( front ) . addEpsilon ( rhs . _start ) ; this . getItem ( back ) . setAcceptState ( this . getItem ( this . _end ) . getAcceptState ( ) ) ; this . getItem ( this . _end ) . addEpsilon ( back ) ; rhs . getItem ( rhs . _end ) . addEpsilon ( back ) ; this . getItem ( this . _end ) . setAcceptState ( - 1 ) ; rhs . getItem ( rhs . _end ) . setAcceptState ( - 1 ) ; this . _start = front ; this . _end = back ; } public void positiveClosure ( ) { this . applyClosure ( Closure . POSITIVE ) ; } public void recycleNode ( int index ) { this . getItem ( index ) . reset ( ) ; recycle . push ( new Integer ( index ) ) ; } public static void reset ( ) { nodes = new NFANode [ 0 ] ; recycle . clear ( ) ; } public String toString ( ) { StringBuffer sb = new StringBuffer ( ) ; sb . append ( "NFA\n===\n" ) ; for ( int i = 0 ; i < nodes . length ; i ++ ) { if ( i == this . _start && i == this . _end ) { sb . append ( "<->" ) . append ( i ) . append ( " : " ) ; } else if ( i == this . _start ) { sb . append ( " ->" ) . append ( i ) . append ( " : " ) ; } else if ( i == this . _end ) { sb . append ( "<- " ) . append ( i ) . append ( " : " ) ; } else { sb . append ( "   " ) . append ( i ) . append ( " : " ) ; } sb . append ( this . getItem ( i ) ) ; sb . append ( "\n" ) ; } sb . append ( "\nInputs\n======\n" ) . append ( this . getInputSet ( ) ) . append ( "\n" ) ; return sb . toString ( ) ; } } 