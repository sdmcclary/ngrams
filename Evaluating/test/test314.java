public class ArrayValueImpl extends ArrayValue implements Serializable { private static final long serialVersionUID = 1L ; private static final int DEFAULT_SIZE = 16 ; private static final int MIN_HASH = 4 ; protected Entry [ ] _entries ; protected int _hashMask ; protected int _size ; protected long _nextAvailableIndex ; protected boolean _isDirty ; protected Entry _head ; protected Entry _tail ; private ConstArrayValue _constSource ; public ArrayValueImpl ( ) { } public ArrayValueImpl ( int size ) { } public ArrayValueImpl ( ArrayValue source ) { for ( Entry ptr = source . getHead ( ) ; ptr != null ; ptr = ptr . _next ) { Entry entry = createEntry ( ptr . _key ) ; if ( ptr . _var != null ) entry . _var = ptr . _var ; else entry . _value = ptr . _value . copyArrayItem ( ) ; } } public ArrayValueImpl ( ArrayValueImpl source ) { if ( ! source . _isDirty ) source . _isDirty = true ; _isDirty = true ; _size = source . _size ; _entries = source . _entries ; _hashMask = source . _hashMask ; _head = source . _head ; _current = source . _current ; _tail = source . _tail ; _nextAvailableIndex = source . _nextAvailableIndex ; } public ArrayValueImpl ( ConstArrayValue source ) { _constSource = source ; _isDirty = true ; _size = source . _size ; _entries = source . _entries ; _hashMask = source . _hashMask ; _head = source . _head ; _current = source . _current ; _tail = source . _tail ; _nextAvailableIndex = source . _nextAvailableIndex ; } public ArrayValueImpl ( Env env , IdentityHashMap < Value , Value > map , ArrayValue copy ) { this ( ) ; map . put ( copy , this ) ; for ( Entry ptr = copy . getHead ( ) ; ptr != null ; ptr = ptr . _next ) { Value value = ptr . _var != null ? ptr . _var . toValue ( ) : ptr . _value ; append ( ptr . _key , value . copy ( env , map ) ) ; } } protected ArrayValueImpl ( Env env , ArrayValue copy , CopyRoot root ) { this ( ) ; root . putCopy ( copy , this ) ; for ( Entry ptr = copy . getHead ( ) ; ptr != null ; ptr = ptr . _next ) { Value value = ptr . _var != null ? ptr . _var . toValue ( ) : ptr . _value ; append ( ptr . _key , value . copyTree ( env , root ) ) ; } } public ArrayValueImpl ( Value [ ] keys , Value [ ] values ) { this ( ) ; for ( int i = 0 ; i < keys . length ; i ++ ) { if ( keys [ i ] != null ) append ( keys [ i ] , values [ i ] ) ; else put ( values [ i ] ) ; } } public ArrayValueImpl ( Value [ ] values ) { this ( ) ; for ( int i = 0 ; i < values . length ; i ++ ) { put ( values [ i ] ) ; } } public ArrayValueImpl ( Env env , ArrayValueComponent [ ] components ) { for ( int i = 0 ; i < components . length ; i ++ ) { components [ i ] . init ( env ) ; components [ i ] . addTo ( this ) ; } } public ArrayValueImpl ( ArrayValueComponent [ ] components ) { for ( int i = 0 ; i < components . length ; i ++ ) { components [ i ] . init ( ) ; components [ i ] . addTo ( this ) ; } } private void copyOnWrite ( ) { if ( ! _isDirty ) return ; _constSource = null ; _isDirty = false ; Entry [ ] entries = _entries ; if ( entries != null ) entries = new Entry [ entries . length ] ; else entries = null ; Entry prev = null ; for ( Entry ptr = _head ; ptr != null ; ptr = ptr . _next ) { Entry ptrCopy = new Entry ( ptr ) ; if ( entries != null ) { int hash = ptr . _key . hashCode ( ) & _hashMask ; Entry head = entries [ hash ] ; if ( head != null ) { ptrCopy . _nextHash = head ; } entries [ hash ] = ptrCopy ; } if ( prev == null ) _head = _current = ptrCopy ; else { prev . _next = ptrCopy ; ptrCopy . _prev = prev ; } prev = ptrCopy ; } _tail = prev ; _entries = entries ; } public String getType ( ) { return "array" ; } public boolean toBoolean ( ) { return _size != 0 ; } public StringValue toReprString ( Env env ) { return env . createStringOld ( "Array" ) ; } public Object toObject ( ) { return null ; } public Value copy ( ) { return new ArrayValueImpl ( this ) ; } public Value copyReturn ( ) { return new ArrayValueImpl ( this ) ; } public Value copy ( Env env , IdentityHashMap < Value , Value > map ) { Value oldValue = map . get ( this ) ; if ( oldValue != null ) return oldValue ; return new ArrayValueImpl ( env , map , this ) ; } @ Override public Value copyTree ( Env env , CopyRoot root ) { Value copy = root . getCopy ( this ) ; if ( copy != null ) return copy ; else return new ArrayCopyValueImpl ( env , this , root ) ; } @ Override public Value toArgValue ( ) { return copy ( ) ; } @ Override public Value toRefValue ( ) { return this ; } public int size ( ) { return _size ; } public int getSize ( ) { return size ( ) ; } public void clear ( ) { if ( _isDirty ) { _isDirty = false ; } _entries = null ; _size = 0 ; _head = _tail = _current = null ; _nextAvailableIndex = 0 ; } public boolean isArray ( ) { return true ; } public ArrayValue append ( Value key , Value value ) { if ( _isDirty ) { copyOnWrite ( ) ; } if ( key instanceof UnsetValue ) key = createTailKey ( ) ; Entry entry = createEntry ( key ) ; Var oldVar = entry . _var ; if ( value instanceof Var ) { Var var = ( Var ) value ; var . setReference ( ) ; entry . _var = var ; } else if ( oldVar != null ) { oldVar . set ( value ) ; } else { entry . _value = value ; } return this ; } public ArrayValue unshift ( Value value ) { if ( _isDirty ) copyOnWrite ( ) ; _size ++ ; Entry [ ] entries = _entries ; if ( ( entries == null && _size >= MIN_HASH ) || ( entries != null && entries . length <= 2 * _size ) ) { expand ( ) ; } Value key = createTailKey ( ) ; Entry entry = new Entry ( key , value . toArgValue ( ) ) ; addEntry ( entry ) ; if ( _head != null ) { _head . _prev = entry ; entry . _next = _head ; _head = entry ; } else { _head = _tail = entry ; } return this ; } public ArrayValue splice ( int start , int end , ArrayValue replace ) { if ( _isDirty ) copyOnWrite ( ) ; int index = 0 ; ArrayValueImpl result = new ArrayValueImpl ( ) ; Entry ptr = _head ; Entry next = null ; for ( ; ptr != null ; ptr = next ) { next = ptr . _next ; if ( index < start ) { } else if ( index < end ) { _size -- ; if ( ptr . _prev != null ) ptr . _prev . _next = ptr . _next ; else _head = ptr . _next ; if ( ptr . _next != null ) ptr . _next . _prev = ptr . _prev ; else _tail = ptr . _prev ; if ( ptr . getKey ( ) . isString ( ) ) result . put ( ptr . getKey ( ) , ptr . getValue ( ) ) ; else result . put ( ptr . getValue ( ) ) ; } else if ( replace == null ) { return result ; } else { for ( Entry replaceEntry = replace . getHead ( ) ; replaceEntry != null ; replaceEntry = replaceEntry . _next ) { _size ++ ; Entry [ ] entries = _entries ; if ( ( entries == null && _size >= MIN_HASH ) || ( entries != null && entries . length <= 2 * _size ) ) { expand ( ) ; } Entry entry = new Entry ( createTailKey ( ) , replaceEntry . getValue ( ) ) ; addEntry ( entry ) ; entry . _next = ptr ; entry . _prev = ptr . _prev ; if ( ptr . _prev != null ) ptr . _prev . _next = entry ; else _head = entry ; ptr . _prev = entry ; } return result ; } index ++ ; } if ( replace != null ) { for ( Entry replaceEntry = replace . getHead ( ) ; replaceEntry != null ; replaceEntry = replaceEntry . _next ) { put ( replaceEntry . getValue ( ) ) ; } } return result ; } @ Override public ArrayValue slice ( Env env , int start , int end , boolean isPreserveKeys ) { ArrayValueImpl array = new ArrayValueImpl ( ) ; int i = 0 ; for ( Entry ptr = _head ; i < end && ptr != null ; ptr = ptr . _next ) { if ( start > i ++ ) continue ; Value key = ptr . getKey ( ) ; Value value = ptr . getValue ( ) ; if ( isPreserveKeys || key . isString ( ) ) array . put ( key , value ) ; else array . put ( value ) ; } return array ; } @ Override public Value getArg ( Value index , boolean isTop ) { if ( _isDirty ) copyOnWrite ( ) ; Entry entry = getEntry ( index ) ; if ( entry != null ) { Value value = entry . getValue ( ) ; if ( ! isTop && value . isset ( ) ) return value ; else return entry . toArg ( ) ; } else { return new ArgGetValue ( this , index ) ; } } @ Override public Value getObject ( Env env , Value fieldName ) { Value value = get ( fieldName ) ; if ( ! value . isset ( ) ) { value = env . createObject ( ) ; put ( fieldName , value ) ; } return value ; } public Value getArray ( Value index ) { if ( _isDirty ) { copyOnWrite ( ) ; } Entry entry = createEntry ( index ) ; Value value = entry . toValue ( ) ; Value array = value . toAutoArray ( ) ; if ( value != array ) { value = array ; entry . set ( value ) ; } return value ; } public Value getDirty ( Value index ) { if ( _isDirty ) copyOnWrite ( ) ; return get ( index ) ; } public Value put ( Value value ) { if ( _isDirty ) copyOnWrite ( ) ; Value key = createTailKey ( ) ; append ( key , value ) ; return value ; } public Var putRef ( ) { if ( _isDirty ) copyOnWrite ( ) ; Value tailKey = createTailKey ( ) ; return getRef ( tailKey ) ; } public Value createTailKey ( ) { if ( _nextAvailableIndex < 0 ) updateNextAvailableIndex ( ) ; return LongValue . create ( _nextAvailableIndex ) ; } public Value get ( Value key ) { key = key . toKey ( ) ; Entry [ ] entries = _entries ; if ( entries != null ) { int hash = key . hashCode ( ) & _hashMask ; for ( Entry entry = entries [ hash ] ; entry != null ; entry = entry . _nextHash ) { if ( key . equals ( entry . _key ) ) { Var var = entry . _var ; return var != null ? var . toValue ( ) : entry . _value ; } } } else { for ( Entry entry = _head ; entry != null ; entry = entry . _next ) { if ( key . equals ( entry . _key ) ) { Var var = entry . _var ; return var != null ? var . toValue ( ) : entry . _value ; } } } return UnsetValue . UNSET ; } @ Override public Value getRaw ( Value key ) { key = key . toKey ( ) ; Entry [ ] entries = _entries ; if ( entries != null ) { int hashMask = _hashMask ; int hash = key . hashCode ( ) & hashMask ; for ( Entry entry = entries [ hash ] ; entry != null ; entry = entry . _nextHash ) { if ( key . equals ( entry . _key ) ) { Var var = entry . _var ; return var != null ? var : entry . _value ; } } } else { for ( Entry entry = _head ; entry != null ; entry = entry . _next ) { if ( key . equals ( entry . _key ) ) { Var var = entry . _var ; return var != null ? var : entry . _value ; } } } return UnsetValue . UNSET ; } @ Override public Value contains ( Value value ) { for ( Entry entry = getHead ( ) ; entry != null ; entry = entry . _next ) { if ( entry . getValue ( ) . eq ( value ) ) return entry . getKey ( ) ; } return NullValue . NULL ; } @ Override public Value containsStrict ( Value value ) { for ( Entry entry = getHead ( ) ; entry != null ; entry = entry . _next ) { if ( entry . getValue ( ) . eql ( value ) ) return entry . getKey ( ) ; } return NullValue . NULL ; } @ Override public Value containsKey ( Value key ) { Entry entry = getEntry ( key ) ; if ( entry != null ) return entry . getValue ( ) ; else return null ; } private Entry getEntry ( Value key ) { key = key . toKey ( ) ; Entry [ ] entries = _entries ; if ( entries != null ) { int hash = key . hashCode ( ) & _hashMask ; for ( Entry entry = entries [ hash ] ; entry != null ; entry = entry . _nextHash ) { if ( key . equals ( entry . _key ) ) return entry ; } } else { for ( Entry entry = _head ; entry != null ; entry = entry . _next ) { if ( key . equals ( entry . _key ) ) return entry ; } } return null ; } @ Override public boolean isset ( Value key ) { key = key . toKey ( ) ; Entry [ ] entries = _entries ; if ( entries != null ) { int hash = key . hashCode ( ) & _hashMask ; for ( Entry entry = entries [ hash ] ; entry != null ; entry = entry . _nextHash ) { if ( key . equals ( entry . _key ) ) return true ; } } else { for ( Entry entry = _head ; entry != null ; entry = entry . _next ) { if ( key . equals ( entry . _key ) ) return true ; } } return false ; } @ Override public Value remove ( Value key ) { if ( _isDirty ) copyOnWrite ( ) ; key = key . toKey ( ) ; Entry [ ] entries = _entries ; if ( entries != null ) { int hash = key . hashCode ( ) & _hashMask ; Entry prevHash = null ; for ( Entry entry = entries [ hash ] ; entry != null ; entry = entry . _nextHash ) { if ( key . equals ( entry . _key ) ) { if ( prevHash != null ) prevHash . _nextHash = entry . _nextHash ; else entries [ hash ] = entry . _nextHash ; return removeEntry ( key , entry ) ; } prevHash = entry ; } } else { for ( Entry entry = _head ; entry != null ; entry = entry . _next ) { if ( key . equals ( entry . _key ) ) { return removeEntry ( key , entry ) ; } } } return UnsetValue . UNSET ; } private Value removeEntry ( Value key , Entry entry ) { Entry next = entry . _next ; Entry prev = entry . _prev ; if ( prev != null ) prev . _next = next ; else _head = next ; if ( next != null ) next . _prev = prev ; else _tail = prev ; entry . _prev = null ; entry . _next = null ; _current = _head ; _size -- ; Value value = entry . getValue ( ) ; if ( key . nextIndex ( - 1 ) == _nextAvailableIndex ) { _nextAvailableIndex = - 1 ; } return value ; } public Var getRef ( Value index ) { if ( _isDirty ) copyOnWrite ( ) ; Entry entry = createEntry ( index ) ; Var var = entry . _var ; if ( var != null ) return var ; var = new Var ( entry . _value ) ; entry . _var = var ; return var ; } private Entry createEntry ( Value key ) { if ( _isDirty ) copyOnWrite ( ) ; key = key . toKey ( ) ; int hashMask = _hashMask ; int hash = key . hashCode ( ) & hashMask ; Entry [ ] entries = _entries ; if ( entries != null ) { for ( Entry entry = entries [ hash ] ; entry != null ; entry = entry . _nextHash ) { if ( key . equals ( entry . _key ) ) return entry ; } } else { for ( Entry entry = _head ; entry != null ; entry = entry . _next ) { if ( key . equals ( entry . _key ) ) return entry ; } } _size ++ ; Entry newEntry = new Entry ( key ) ; if ( _nextAvailableIndex >= 0 ) _nextAvailableIndex = key . nextIndex ( _nextAvailableIndex ) ; if ( _entries == null && _size < MIN_HASH ) { } else { if ( _entries == null || _entries . length <= 2 * _size ) { expand ( ) ; hash = key . hashCode ( ) & _hashMask ; } Entry head = _entries [ hash ] ; newEntry . _nextHash = head ; _entries [ hash ] = newEntry ; } if ( _head == null ) { newEntry . _prev = null ; newEntry . _next = null ; _head = newEntry ; _tail = newEntry ; _current = newEntry ; } else { newEntry . _prev = _tail ; newEntry . _next = null ; _tail . _next = newEntry ; _tail = newEntry ; } return newEntry ; } private void expand ( ) { Entry [ ] entries = _entries ; if ( entries == null ) _entries = new Entry [ 8 ] ; else _entries = new Entry [ 2 * entries . length ] ; _hashMask = _entries . length - 1 ; for ( Entry entry = _head ; entry != null ; entry = entry . _next ) { addEntry ( entry ) ; } } private void addEntry ( Entry entry ) { Entry [ ] entries = _entries ; if ( entries != null ) { int hash = entry . _key . hashCode ( ) & _hashMask ; Entry head = entries [ hash ] ; entry . _nextHash = head ; entries [ hash ] = entry ; } if ( _nextAvailableIndex >= 0 ) _nextAvailableIndex = entry . _key . nextIndex ( _nextAvailableIndex ) ; } private void updateNextAvailableIndex ( ) { _nextAvailableIndex = 0 ; for ( Entry entry = _head ; entry != null ; entry = entry . _next ) { _nextAvailableIndex = entry . _key . nextIndex ( _nextAvailableIndex ) ; } } public Value pop ( ) { if ( _isDirty ) copyOnWrite ( ) ; if ( _tail != null ) return remove ( _tail . _key ) ; else return BooleanValue . FALSE ; } protected final Entry getHead ( ) { return _head ; } protected final Entry getTail ( ) { return _tail ; } public void shuffle ( ) { if ( _isDirty ) copyOnWrite ( ) ; Entry [ ] values = new Entry [ size ( ) ] ; int length = values . length ; if ( length == 0 ) return ; int i = 0 ; for ( Entry ptr = _head ; ptr != null ; ptr = ptr . _next ) values [ i ++ ] = ptr ; for ( i = 0 ; i < length ; i ++ ) { int rand = RandomUtil . nextInt ( length ) ; Entry temp = values [ rand ] ; values [ rand ] = values [ i ] ; values [ i ] = temp ; } _head = values [ 0 ] ; _head . _prev = null ; _tail = values [ values . length - 1 ] ; _tail . _next = null ; for ( i = 0 ; i < length ; i ++ ) { if ( i > 0 ) values [ i ] . _prev = values [ i - 1 ] ; if ( i < length - 1 ) values [ i ] . _next = values [ i + 1 ] ; } _current = _head ; } @ Override public Value getKeys ( ) { if ( _constSource != null ) return _constSource . getKeys ( ) ; else return super . getKeys ( ) ; } @ Override public Value getValues ( ) { if ( _constSource != null ) return _constSource . getValues ( ) ; else return super . getValues ( ) ; } private void writeObject ( ObjectOutputStream out ) throws IOException { out . writeInt ( _size ) ; for ( Map . Entry < Value , Value > entry : entrySet ( ) ) { out . writeObject ( entry . getKey ( ) ) ; out . writeObject ( entry . getValue ( ) ) ; } } private void readObject ( ObjectInputStream in ) throws ClassNotFoundException , IOException { int size = in . readInt ( ) ; int capacity = DEFAULT_SIZE ; while ( capacity < 4 * size ) { capacity *= 2 ; } _entries = new Entry [ capacity ] ; _hashMask = _entries . length - 1 ; for ( int i = 0 ; i < size ; i ++ ) { put ( ( Value ) in . readObject ( ) , ( Value ) in . readObject ( ) ) ; } } public void generate ( PrintWriter out ) throws IOException { out . print ( "new ConstArrayValue(" ) ; if ( getSize ( ) < ArrayValueComponent . MAX_SIZE ) { out . print ( "new Value[] {" ) ; for ( Entry entry = getHead ( ) ; entry != null ; entry = entry . _next ) { if ( entry != getHead ( ) ) out . print ( ", " ) ; if ( entry . getKey ( ) != null ) entry . getKey ( ) . generate ( out ) ; else out . print ( "null" ) ; } out . print ( "}, new Value[] {" ) ; for ( Entry entry = getHead ( ) ; entry != null ; entry = entry . _next ) { if ( entry != getHead ( ) ) out . print ( ", " ) ; entry . getValue ( ) . generate ( out ) ; } out . print ( "}" ) ; } else { ArrayValueComponent . generate ( out , this ) ; } out . print ( ")" ) ; } @ Override public int hashCode ( ) { if ( _size == 0 ) return 0 ; else return _head . getValue ( ) . hashCode ( ) ; } } 