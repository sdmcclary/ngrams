final class VerticalCellComparator implements Comparator < Cell > { public static final VerticalCellComparator THE_INSTANCE = new VerticalCellComparator ( ) ; private VerticalCellComparator ( ) { super ( ) ; } public final int compare ( Cell cell0 , Cell cell1 ) { if ( cell0 . getBottom ( ) < cell1 . getBottom ( ) ) { return - 1 ; } else if ( cell0 . getBottom ( ) > cell1 . getBottom ( ) ) { return 1 ; } else if ( cell0 == cell1 ) { return 0 ; } else { if ( cell0 . getLeft ( ) < cell1 . getLeft ( ) ) { return - 1 ; } else if ( cell0 . getLeft ( ) > cell1 . getLeft ( ) ) { return 1 ; } else { throw new IllegalStateException ( "Two cells in effect cannot start on the same column, so this should never happen!!\n" + "cell0 from line " + cell0 . getLineNumber ( ) + ", bottom=" + cell0 . getBottom ( ) + ", left=" + cell0 . getLeft ( ) + "\n" + "cell1 from line " + cell1 . getLineNumber ( ) + ", bottom=" + cell1 . getBottom ( ) + ", left=" + cell1 . getLeft ( ) ) ; } } } } 