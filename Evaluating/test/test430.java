final class HorizontalCellComparator implements Comparator < Cell > { public static final HorizontalCellComparator THE_INSTANCE = new HorizontalCellComparator ( ) ; private HorizontalCellComparator ( ) { super ( ) ; } public final int compare ( Cell cell0 , Cell cell1 ) { if ( cell0 . getLeft ( ) < cell1 . getLeft ( ) ) { return - 1 ; } else if ( cell0 . getLeft ( ) > cell1 . getLeft ( ) ) { return 1 ; } else { throw new IllegalStateException ( "Two cells in effect cannot start on the same column, so this should never happen!" ) ; } } } 