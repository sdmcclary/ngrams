<<<<<<< HEAD
public class HashMapImpl < K , V > extends AbstractMap < K , V > { private K [ ] _keys ; private V [ ] _values ; private V _nullValue ; private int _size ; private int _mask ; public HashMapImpl ( ) { this ( 16 ) ; } @ SuppressWarnings ( "unchecked" ) public HashMapImpl ( int initialCapacity ) { int capacity ; for ( capacity = 16 ; capacity < 2 * initialCapacity ; capacity *= 2 ) { } _keys = ( K [ ] ) new Object [ capacity ] ; _values = ( V [ ] ) new Object [ capacity ] ; _mask = capacity - 1 ; } public int size ( ) { return _size ; } public void clear ( ) { if ( _size > 0 ) { for ( int i = 0 ; i < _values . length ; i ++ ) { _keys [ i ] = null ; _values [ i ] = null ; } _size = 0 ; } _nullValue = null ; } public V get ( Object key ) { if ( key == null ) return _nullValue ; int hash = key . hashCode ( ) & _mask ; int count = _size + 1 ; K [ ] keys = _keys ; for ( ; count > 0 ; count -- ) { K mapKey = keys [ hash ] ; if ( mapKey == null ) return null ; if ( key . equals ( _keys [ hash ] ) ) return _values [ hash ] ; hash = ( hash + 1 ) & _mask ; } return null ; } @ SuppressWarnings ( "unchecked" ) public V put ( K key , V value ) { if ( key == null ) { V item = _nullValue ; _nullValue = value ; return item ; } V item = putImpl ( key , value ) ; if ( 3 * _values . length <= 4 * _size ) { K [ ] oldKeys = _keys ; V [ ] oldValues = _values ; _keys = ( K [ ] ) new Object [ 2 * oldKeys . length ] ; _values = ( V [ ] ) new Object [ 2 * oldValues . length ] ; _mask = _values . length - 1 ; _size = 0 ; for ( int i = oldValues . length - 1 ; i >= 0 ; i -- ) { K oldKey = oldKeys [ i ] ; V oldValue = oldValues [ i ] ; if ( oldValue != null ) putImpl ( oldKey , oldValue ) ; } } return item ; } private V putImpl ( K key , V value ) { V item = null ; int hash = key . hashCode ( ) & _mask ; int count = _size + 1 ; for ( ; count > 0 ; count -- ) { item = _values [ hash ] ; if ( item == null ) { _keys [ hash ] = key ; _values [ hash ] = value ; _size ++ ; return null ; } if ( _keys [ hash ] . equals ( key ) ) { _values [ hash ] = value ; return item ; } hash = ( hash + 1 ) & _mask ; } throw new IllegalStateException ( ) ; } public V remove ( Object key ) { if ( key == null ) { V value = _nullValue ; _nullValue = null ; return value ; } int hash = key . hashCode ( ) & _mask ; int count = _size + 1 ; V item = null ; for ( ; count > 0 ; count -- ) { item = _values [ hash ] ; if ( item == null ) return null ; if ( _keys [ hash ] . equals ( key ) ) { _keys [ hash ] = null ; _values [ hash ] = null ; _size -- ; refillEntries ( hash ) ; break ; } hash = ( hash + 1 ) & _mask ; } if ( count < 0 ) throw new RuntimeException ( "internal cache error" ) ; return item ; } private void refillEntries ( int hash ) { for ( int count = _size ; count >= 0 ; count -- ) { hash = ( hash + 1 ) & _mask ; if ( _values [ hash ] == null ) return ; refillEntry ( hash ) ; } } private void refillEntry ( int baseHash ) { K key = _keys [ baseHash ] ; V value = _values [ baseHash ] ; _keys [ baseHash ] = null ; _values [ baseHash ] = null ; int hash = key . hashCode ( ) & _mask ; for ( int count = _size ; count >= 0 ; count -- ) { if ( _values [ hash ] == null ) { _keys [ hash ] = key ; _values [ hash ] = value ; return ; } hash = ( hash + 1 ) & _mask ; } } public Set < K > keySet ( ) { return new KeySet < K , V > ( this ) ; } static class KeySet < K1 , V1 > extends AbstractSet < K1 > { private HashMapImpl < K1 , V1 > _map ; KeySet ( HashMapImpl < K1 , V1 > map ) { _map = map ; } public int size ( ) { return _map . size ( ) ; } public boolean contains ( Object key ) { if ( key == null ) return _map . _nullValue != null ; K1 [ ] keys = _map . _keys ; for ( int i = keys . length - 1 ; i >= 0 ; i -- ) { K1 testKey = keys [ i ] ; if ( key . equals ( testKey ) ) return true ; } return false ; } public boolean removeAll ( Collection < ? > keys ) { if ( keys == null ) return false ; Iterator < ? > iter = keys . iterator ( ) ; while ( iter . hasNext ( ) ) { Object key = iter . next ( ) ; _map . remove ( key ) ; } return true ; } public Iterator < K1 > iterator ( ) { return new KeyIterator < K1 , V1 > ( _map ) ; } } static class KeyIterator < K1 , V1 > implements Iterator < K1 > { private HashMapImpl < K1 , V1 > _map ; private int _i ; KeyIterator ( HashMapImpl < K1 , V1 > map ) { init ( map ) ; } void init ( HashMapImpl < K1 , V1 > map ) { _map = map ; _i = 0 ; } public boolean hasNext ( ) { K1 [ ] keys = _map . _keys ; int len = keys . length ; for ( ; _i < len ; _i ++ ) { if ( keys [ _i ] != null ) return true ; } return false ; } public K1 next ( ) { K1 [ ] keys = _map . _keys ; int len = keys . length ; for ( ; _i < len ; _i ++ ) { K1 key = keys [ _i ] ; if ( key != null ) { _i ++ ; return key ; } } return null ; } public void remove ( ) { if ( _i > 0 ) _map . remove ( _map . _keys [ _i - 1 ] ) ; } } public Set < Map . Entry < K , V > > entrySet ( ) { return new EntrySet < K , V > ( this ) ; } static class EntrySet < K1 , V1 > extends AbstractSet < Map . Entry < K1 , V1 > > { private HashMapImpl < K1 , V1 > _map ; EntrySet ( HashMapImpl < K1 , V1 > map ) { _map = map ; } public int size ( ) { return _map . size ( ) ; } public Iterator < Map . Entry < K1 , V1 > > iterator ( ) { return new EntryIterator < K1 , V1 > ( _map ) ; } } static class EntryIterator < K1 , V1 > implements Iterator < Map . Entry < K1 , V1 > > { private final Entry < K1 , V1 > _entry = new Entry < K1 , V1 > ( ) ; private HashMapImpl < K1 , V1 > _map ; private int _i ; EntryIterator ( HashMapImpl < K1 , V1 > map ) { init ( map ) ; } void init ( HashMapImpl < K1 , V1 > map ) { _map = map ; _i = 0 ; } public boolean hasNext ( ) { K1 [ ] keys = _map . _keys ; int len = keys . length ; for ( ; _i < len ; _i ++ ) { if ( keys [ _i ] != null ) return true ; } return false ; } public Map . Entry < K1 , V1 > next ( ) { K1 [ ] keys = _map . _keys ; int len = keys . length ; for ( ; _i < len ; _i ++ ) { if ( keys [ _i ] != null ) { _entry . init ( _map , _i ++ ) ; return _entry ; } } return null ; } public void remove ( ) { if ( _i > 0 ) _map . remove ( _map . _keys [ _i - 1 ] ) ; } } static class Entry < K1 , V1 > implements Map . Entry < K1 , V1 > { private HashMapImpl < K1 , V1 > _map ; private int _i ; void init ( HashMapImpl < K1 , V1 > map , int i ) { _map = map ; _i = i ; } public K1 getKey ( ) { return _map . _keys [ _i ] ; } public V1 getValue ( ) { return _map . _values [ _i ] ; } public V1 setValue ( V1 value ) { V1 oldValue = _map . _values [ _i ] ; _map . _values [ _i ] = value ; return oldValue ; } } } 
=======
abstract public class Validator2 extends Validator { protected Validator2 ( ) { } public void validate ( File file ) throws SAXException , IOException { validate ( new StreamSource ( file ) ) ; } public void validate ( URL url ) throws SAXException , IOException { validate ( new StreamSource ( url . toExternalForm ( ) ) ) ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
