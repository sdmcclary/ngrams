<<<<<<< HEAD
abstract public class ObjectValue extends Value { transient protected QuercusClass _quercusClass ; protected String _className ; protected String _incompleteObjectName ; protected ObjectValue ( ) { } protected ObjectValue ( QuercusClass quercusClass ) { _quercusClass = quercusClass ; _className = quercusClass . getName ( ) ; } protected void setQuercusClass ( QuercusClass cl ) { _quercusClass = cl ; _className = cl . getName ( ) ; } public QuercusClass getQuercusClass ( ) { return _quercusClass ; } public boolean isIncompleteObject ( ) { return _incompleteObjectName != null ; } public String getIncompleteObjectName ( ) { return _incompleteObjectName ; } public void setIncompleteObjectName ( String name ) { _incompleteObjectName = name ; } public void initObject ( Env env , QuercusClass cls ) { setQuercusClass ( cls ) ; _incompleteObjectName = null ; } public String getClassName ( ) { return _className ; } abstract public Set < ? extends Map . Entry < Value , Value > > entrySet ( ) ; public String getName ( ) { return _className ; } public String getParentClassName ( ) { return _quercusClass . getParentName ( ) ; } @ Override public boolean isObject ( ) { return true ; } @ Override public String getType ( ) { return "object" ; } @ Override public boolean toBoolean ( ) { return true ; } @ Override public boolean isA ( String name ) { return _quercusClass . isA ( name ) ; } @ Override public long toLong ( ) { return 1 ; } @ Override public double toDouble ( ) { return toLong ( ) ; } @ Override public Value get ( Value key ) { ArrayDelegate delegate = _quercusClass . getArrayDelegate ( ) ; if ( delegate != null ) return delegate . get ( this , key ) ; else return super . get ( key ) ; } @ Override public Value put ( Value key , Value value ) { ArrayDelegate delegate = _quercusClass . getArrayDelegate ( ) ; if ( delegate != null ) return delegate . put ( this , key , value ) ; else return super . put ( key , value ) ; } @ Override public Value put ( Value value ) { ArrayDelegate delegate = _quercusClass . getArrayDelegate ( ) ; if ( delegate != null ) return delegate . put ( this , value ) ; else return super . put ( value ) ; } public Value append ( Value index , Value value ) { put ( index , value ) ; return this ; } @ Override public boolean isset ( Value key ) { ArrayDelegate delegate = _quercusClass . getArrayDelegate ( ) ; if ( delegate != null ) return delegate . isset ( this , key ) ; else return super . isset ( key ) ; } @ Override public Value remove ( Value key ) { ArrayDelegate delegate = _quercusClass . getArrayDelegate ( ) ; if ( delegate != null ) return delegate . unset ( this , key ) ; else return super . remove ( key ) ; } @ Override public Iterator < Map . Entry < Value , Value > > getIterator ( Env env ) { TraversableDelegate delegate = _quercusClass . getTraversableDelegate ( ) ; if ( delegate != null ) return delegate . getIterator ( env , this ) ; else return super . getIterator ( env ) ; } @ Override public Iterator < Value > getKeyIterator ( Env env ) { TraversableDelegate delegate = _quercusClass . getTraversableDelegate ( ) ; if ( delegate != null ) return delegate . getKeyIterator ( env , this ) ; else return super . getKeyIterator ( env ) ; } @ Override public Iterator < Value > getValueIterator ( Env env ) { TraversableDelegate delegate = _quercusClass . getTraversableDelegate ( ) ; if ( delegate != null ) return delegate . getValueIterator ( env , this ) ; else return super . getValueIterator ( env ) ; } @ Override public int getCount ( Env env ) { CountDelegate delegate = _quercusClass . getCountDelegate ( ) ; if ( delegate != null ) return delegate . count ( this ) ; else return super . getSize ( ) ; } public Value putField ( String key , String value ) { Env env = Env . getInstance ( ) ; return putThisField ( env , env . createStringOld ( key ) , env . createStringOld ( value ) ) ; } public Value putField ( Env env , String key , String value ) { return putThisField ( env , env . createStringOld ( key ) , env . createStringOld ( value ) ) ; } public Value putField ( String key , long value ) { Env env = Env . getInstance ( ) ; return putThisField ( env , env . createStringOld ( key ) , LongValue . create ( value ) ) ; } public Value putField ( Env env , String key , long value ) { return putThisField ( env , env . createStringOld ( key ) , LongValue . create ( value ) ) ; } public Value putField ( Env env , String key , Value value ) { return putThisField ( env , env . createStringOld ( key ) , value ) ; } @ Override public void initField ( StringValue key , Value value , FieldVisibility visibility ) { putThisField ( Env . getInstance ( ) , key , value ) ; } public Value putField ( String key , double value ) { Env env = Env . getInstance ( ) ; return putThisField ( env , env . createStringOld ( key ) , DoubleValue . create ( value ) ) ; } @ Override public boolean eq ( Value rValue ) { rValue = rValue . toValue ( ) ; if ( rValue instanceof ObjectValue ) return cmpObject ( ( ObjectValue ) rValue ) == 0 ; else return super . eq ( rValue ) ; } public int cmpObject ( ObjectValue rValue ) { if ( rValue == this ) return 0 ; int result = getName ( ) . compareTo ( rValue . getName ( ) ) ; if ( result != 0 ) return result ; Set < ? extends Map . Entry < Value , Value > > aSet = entrySet ( ) ; Set < ? extends Map . Entry < Value , Value > > bSet = rValue . entrySet ( ) ; if ( aSet . equals ( bSet ) ) return 0 ; else if ( aSet . size ( ) > bSet . size ( ) ) return 1 ; else if ( aSet . size ( ) < bSet . size ( ) ) return - 1 ; else { TreeSet < Map . Entry < Value , Value > > aTree = new TreeSet < Map . Entry < Value , Value > > ( aSet ) ; TreeSet < Map . Entry < Value , Value > > bTree = new TreeSet < Map . Entry < Value , Value > > ( bSet ) ; Iterator < Map . Entry < Value , Value > > iterA = aTree . iterator ( ) ; Iterator < Map . Entry < Value , Value > > iterB = bTree . iterator ( ) ; while ( iterA . hasNext ( ) ) { Map . Entry < Value , Value > a = iterA . next ( ) ; Map . Entry < Value , Value > b = iterB . next ( ) ; result = a . getKey ( ) . cmp ( b . getKey ( ) ) ; if ( result != 0 ) return result ; result = a . getValue ( ) . cmp ( b . getValue ( ) ) ; if ( result != 0 ) return result ; } return 0 ; } } public void varDumpObject ( Env env , WriteStream out , int depth , IdentityHashMap < Value , String > valueSet ) throws IOException { int size = getSize ( ) ; if ( isIncompleteObject ( ) ) size ++ ; out . println ( "object(" + getName ( ) + ") (" + size + ") {" ) ; if ( isIncompleteObject ( ) ) { printDepth ( out , 2 * ( depth + 1 ) ) ; out . println ( "[\"__Quercus_Incomplete_Class_name\"]=>" ) ; printDepth ( out , 2 * ( depth + 1 ) ) ; Value value = env . createStringOld ( getIncompleteObjectName ( ) ) ; value . varDump ( env , out , depth + 1 , valueSet ) ; out . println ( ) ; } ArrayValue sortedEntries = new ArrayValueImpl ( ) ; Iterator < Map . Entry < Value , Value > > iter = getIterator ( env ) ; while ( iter . hasNext ( ) ) { Map . Entry < Value , Value > entry = iter . next ( ) ; sortedEntries . put ( entry . getKey ( ) , entry . getValue ( ) ) ; } ArrayModule . ksort ( env , sortedEntries , ArrayModule . SORT_STRING ) ; iter = sortedEntries . getIterator ( env ) ; while ( iter . hasNext ( ) ) { Map . Entry < Value , Value > entry = iter . next ( ) ; Value key = entry . getKey ( ) ; Value value = entry . getValue ( ) ; printDepth ( out , 2 * depth ) ; out . println ( "[\"" + key + "\"]=>" ) ; depth ++ ; printDepth ( out , 2 * depth ) ; value . varDump ( env , out , depth , valueSet ) ; out . println ( ) ; depth -- ; } printDepth ( out , 2 * depth ) ; out . print ( "}" ) ; } } 
=======
class InvalidPatternException extends Exception { } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
