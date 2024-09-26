public class TableSorter extends TableMap { private int indexes [ ] ; private Vector sortingColumns ; private boolean ascending ; private int compares ; private int currentColumn = - 1 ; public TableSorter ( ) { this ( null ) ; } public TableSorter ( TableModel model ) { indexes = new int [ 0 ] ; sortingColumns = new Vector ( ) ; ascending = true ; setModel ( model ) ; } public void setModel ( TableModel aModel ) { super . setModel ( aModel ) ; reallocateIndexes ( ) ; } private int compareRowsByColumn ( int row1 , int row2 , int column ) { Class type = model . getColumnClass ( column ) ; TableModel data = model ; Object o1 = data . getValueAt ( row1 , column ) ; Object o2 = data . getValueAt ( row2 , column ) ; if ( o1 == null && o2 == null ) { return 0 ; } else if ( o1 == null ) { return - 1 ; } else if ( o2 == null ) { return 1 ; } if ( type . getSuperclass ( ) == java . lang . Number . class ) { Number n1 = ( Number ) data . getValueAt ( row1 , column ) ; double d1 = n1 . doubleValue ( ) ; Number n2 = ( Number ) data . getValueAt ( row2 , column ) ; double d2 = n2 . doubleValue ( ) ; if ( d1 < d2 ) { return - 1 ; } else if ( d1 > d2 ) { return 1 ; } else { return 0 ; } } else if ( type == java . util . Date . class ) { Date d1 = ( Date ) data . getValueAt ( row1 , column ) ; long n1 = d1 . getTime ( ) ; Date d2 = ( Date ) data . getValueAt ( row2 , column ) ; long n2 = d2 . getTime ( ) ; if ( n1 < n2 ) { return - 1 ; } else if ( n1 > n2 ) { return 1 ; } else { return 0 ; } } else if ( type == String . class ) { String s1 = ( String ) data . getValueAt ( row1 , column ) ; String s2 = ( String ) data . getValueAt ( row2 , column ) ; int result = s1 . compareTo ( s2 ) ; if ( result < 0 ) { return - 1 ; } else if ( result > 0 ) { return 1 ; } else { return 0 ; } } else if ( type == Boolean . class ) { Boolean bool1 = ( Boolean ) data . getValueAt ( row1 , column ) ; boolean b1 = bool1 . booleanValue ( ) ; Boolean bool2 = ( Boolean ) data . getValueAt ( row2 , column ) ; boolean b2 = bool2 . booleanValue ( ) ; if ( b1 == b2 ) { return 0 ; } else if ( b1 ) { return 1 ; } else { return - 1 ; } } else { Object v1 = data . getValueAt ( row1 , column ) ; String s1 = v1 . toString ( ) ; Object v2 = data . getValueAt ( row2 , column ) ; String s2 = v2 . toString ( ) ; int result = s1 . compareTo ( s2 ) ; if ( result < 0 ) { return - 1 ; } else if ( result > 0 ) { return 1 ; } else { return 0 ; } } } private int compare ( int row1 , int row2 ) { compares ++ ; for ( int level = 0 ; level < sortingColumns . size ( ) ; level ++ ) { Integer column = ( Integer ) sortingColumns . elementAt ( level ) ; int result = compareRowsByColumn ( row1 , row2 , column . intValue ( ) ) ; if ( result != 0 ) { return ascending ? result : - result ; } } return 0 ; } private void reallocateIndexes ( ) { int rowCount = model . getRowCount ( ) ; indexes = new int [ rowCount ] ; for ( int row = 0 ; row < rowCount ; row ++ ) { indexes [ row ] = row ; } } public void tableChanged ( TableModelEvent e ) { reallocateIndexes ( ) ; super . tableChanged ( e ) ; } private void checkModel ( ) { if ( indexes . length != model . getRowCount ( ) ) { System . err . println ( Strings . error ( "sorter.not.informed.of.change.in.model" ) ) ; } } private void sort ( ) { checkModel ( ) ; compares = 0 ; shuttlesort ( ( int [ ] ) indexes . clone ( ) , indexes , 0 , indexes . length ) ; } private void shuttlesort ( int from [ ] , int to [ ] , int low , int high ) { if ( high - low < 2 ) { return ; } int middle = ( low + high ) / 2 ; shuttlesort ( to , from , low , middle ) ; shuttlesort ( to , from , middle , high ) ; int p = low ; int q = middle ; if ( high - low >= 4 && compare ( from [ middle - 1 ] , from [ middle ] ) <= 0 ) { for ( int i = low ; i < high ; i ++ ) { to [ i ] = from [ i ] ; } return ; } for ( int i = low ; i < high ; i ++ ) { if ( q >= high || ( p < middle && compare ( from [ p ] , from [ q ] ) <= 0 ) ) { to [ i ] = from [ p ++ ] ; } else { to [ i ] = from [ q ++ ] ; } } } public int convertToDataRow ( int viewRow ) { return indexes [ viewRow ] ; } public Object getValueAt ( int rowIndex , int columnIndex ) { checkModel ( ) ; return model . getValueAt ( indexes [ rowIndex ] , columnIndex ) ; } public void setValueAt ( Object aValue , int rowIndex , int columnIndex ) { checkModel ( ) ; model . setValueAt ( aValue , indexes [ rowIndex ] , columnIndex ) ; } public void sortByColumn ( int column ) { sortByColumn ( column , true ) ; } public void sortByColumn ( int column , boolean asc ) { sortByColumns ( new int [ ] { column } , asc ) ; } public void sortByColumns ( int [ ] columns , boolean asc ) { if ( columns != null && columns . length > 0 ) { ascending = asc ; currentColumn = columns [ 0 ] ; sortingColumns . clear ( ) ; for ( int i = 0 ; i < columns . length ; i ++ ) { sortingColumns . add ( new Integer ( columns [ i ] ) ) ; } sort ( ) ; super . tableChanged ( new TableModelEvent ( this ) ) ; } } public void addMouseListenerToHeaderInTable ( JTable table ) { final TableSorter sorter = this ; final JTable tableView = table ; tableView . setColumnSelectionAllowed ( false ) ; MouseAdapter listMouseListener = new MouseAdapter ( ) { public void mouseClicked ( MouseEvent e ) { TableColumnModel columnModel = tableView . getColumnModel ( ) ; int viewColumn = columnModel . getColumnIndexAtX ( e . getX ( ) ) ; int column = tableView . convertColumnIndexToModel ( viewColumn ) ; if ( e . getClickCount ( ) == 1 && column != - 1 ) { boolean asc = true ; if ( currentColumn == column ) asc = ! ascending ; sorter . sortByColumn ( column , asc ) ; } } } ; JTableHeader th = tableView . getTableHeader ( ) ; th . addMouseListener ( listMouseListener ) ; } } 