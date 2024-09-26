<<<<<<< HEAD
public class Diagram { private static final boolean DEBUG = false ; private static final boolean VERBOSE_DEBUG = false ; private ArrayList shapes = new ArrayList ( ) ; private ArrayList compositeShapes = new ArrayList ( ) ; private ArrayList textObjects = new ArrayList ( ) ; private int width , height ; private int cellWidth , cellHeight ; public Diagram ( TextGrid grid , ConversionOptions options ) { this . cellWidth = options . renderingOptions . getCellWidth ( ) ; this . cellHeight = options . renderingOptions . getCellHeight ( ) ; width = grid . getWidth ( ) * cellWidth ; height = grid . getHeight ( ) * cellHeight ; TextGrid workGrid = new TextGrid ( grid ) ; workGrid . replaceTypeOnLine ( ) ; workGrid . replacePointMarkersOnLine ( ) ; if ( DEBUG ) workGrid . printDebug ( ) ; int width = grid . getWidth ( ) ; int height = grid . getHeight ( ) ; AbstractionGrid temp = new AbstractionGrid ( workGrid , workGrid . getAllBoundaries ( ) ) ; ArrayList boundarySetsStep1 = temp . getDistinctShapes ( ) ; if ( DEBUG ) { System . out . println ( "******* Distinct shapes found using AbstractionGrid *******" ) ; Iterator dit = boundarySetsStep1 . iterator ( ) ; while ( dit . hasNext ( ) ) { CellSet set = ( CellSet ) dit . next ( ) ; set . printAsGrid ( ) ; } System . out . println ( "******* Same set of shapes after processing them by filling *******" ) ; } ArrayList boundarySetsStep2 = new ArrayList ( ) ; Iterator boundarySetIt = boundarySetsStep1 . iterator ( ) ; while ( boundarySetIt . hasNext ( ) ) { CellSet set = ( CellSet ) boundarySetIt . next ( ) ; TextGrid fillBuffer = new TextGrid ( width * 3 , height * 3 ) ; for ( int yi = 0 ; yi < height * 3 ; yi ++ ) { for ( int xi = 0 ; xi < width * 3 ; xi ++ ) { if ( fillBuffer . isBlank ( xi , yi ) ) { TextGrid copyGrid = new AbstractionGrid ( workGrid , set ) . getCopyOfInternalBuffer ( ) ; CellSet boundaries = copyGrid . findBoundariesExpandingFrom ( copyGrid . new Cell ( xi , yi ) ) ; if ( boundaries . size ( ) == 0 ) continue ; boundarySetsStep2 . add ( boundaries . makeScaledOneThirdEquivalent ( ) ) ; copyGrid = new AbstractionGrid ( workGrid , set ) . getCopyOfInternalBuffer ( ) ; CellSet filled = copyGrid . fillContinuousArea ( copyGrid . new Cell ( xi , yi ) , '*' ) ; fillBuffer . fillCellsWith ( filled , '*' ) ; fillBuffer . fillCellsWith ( boundaries , '-' ) ; if ( DEBUG ) { boundaries . makeScaledOneThirdEquivalent ( ) . printAsGrid ( ) ; System . out . println ( "-----------------------------------" ) ; } } } } } if ( DEBUG ) System . out . println ( "******* Removed duplicates *******" ) ; boundarySetsStep2 = CellSet . removeDuplicateSets ( boundarySetsStep2 ) ; if ( DEBUG ) { Iterator dit = boundarySetsStep2 . iterator ( ) ; while ( dit . hasNext ( ) ) { CellSet set = ( CellSet ) dit . next ( ) ; set . printAsGrid ( ) ; } } int originalSize = boundarySetsStep2 . size ( ) ; boundarySetsStep2 = CellSet . removeDuplicateSets ( boundarySetsStep2 ) ; if ( DEBUG ) { System . out . println ( "******* Removed duplicates: there were " + originalSize + " shapes and now there are " + boundarySetsStep2 . size ( ) ) ; } boolean removedAnyObsolete = removeObsoleteShapes ( workGrid , boundarySetsStep2 ) ; if ( DEBUG ) System . out . println ( "******* First evaluation of openess *******" ) ; ArrayList open = new ArrayList ( ) ; ArrayList closed = new ArrayList ( ) ; ArrayList mixed = new ArrayList ( ) ; Iterator sets = boundarySetsStep2 . iterator ( ) ; while ( sets . hasNext ( ) ) { CellSet set = ( CellSet ) sets . next ( ) ; int type = set . getType ( workGrid ) ; if ( type == CellSet . TYPE_CLOSED ) closed . add ( set ) ; else if ( type == CellSet . TYPE_OPEN ) open . add ( set ) ; else if ( type == CellSet . TYPE_MIXED ) mixed . add ( set ) ; if ( DEBUG ) { if ( type == CellSet . TYPE_CLOSED ) System . out . println ( "Closed boundaries:" ) ; else if ( type == CellSet . TYPE_OPEN ) System . out . println ( "Open boundaries:" ) ; else if ( type == CellSet . TYPE_MIXED ) System . out . println ( "Mixed boundaries:" ) ; set . printAsGrid ( ) ; } } boolean hadToEliminateMixed = false ; if ( mixed . size ( ) > 0 && closed . size ( ) > 0 ) { if ( DEBUG ) System . out . println ( "******* Eliminating mixed shapes (basic algorithm) *******" ) ; hadToEliminateMixed = true ; sets = mixed . iterator ( ) ; while ( sets . hasNext ( ) ) { CellSet set = ( CellSet ) sets . next ( ) ; Iterator closedSets = closed . iterator ( ) ; while ( closedSets . hasNext ( ) ) { CellSet closedSet = ( CellSet ) closedSets . next ( ) ; set . subtractSet ( closedSet ) ; } if ( set . getType ( workGrid ) == CellSet . TYPE_OPEN ) { boundarySetsStep2 . remove ( set ) ; boundarySetsStep2 . addAll ( set . breakIntoDistinctBoundaries ( workGrid ) ) ; } } } else if ( mixed . size ( ) > 0 && closed . size ( ) == 0 ) { hadToEliminateMixed = true ; if ( DEBUG ) System . out . println ( "******* Eliminating mixed shapes (advanced algorithm for truly mixed shapes) *******" ) ; sets = mixed . iterator ( ) ; while ( sets . hasNext ( ) ) { CellSet set = ( CellSet ) sets . next ( ) ; boundarySetsStep2 . remove ( set ) ; boundarySetsStep2 . addAll ( set . breakTrulyMixedBoundaries ( workGrid ) ) ; } } else { if ( DEBUG ) System . out . println ( "No mixed shapes found. Skipped mixed shape elimination step" ) ; } if ( hadToEliminateMixed ) { if ( DEBUG ) System . out . println ( "******* Second evaluation of openess *******" ) ; open = new ArrayList ( ) ; closed = new ArrayList ( ) ; mixed = new ArrayList ( ) ; sets = boundarySetsStep2 . iterator ( ) ; while ( sets . hasNext ( ) ) { CellSet set = ( CellSet ) sets . next ( ) ; int type = set . getType ( workGrid ) ; if ( type == CellSet . TYPE_CLOSED ) closed . add ( set ) ; else if ( type == CellSet . TYPE_OPEN ) open . add ( set ) ; else if ( type == CellSet . TYPE_MIXED ) mixed . add ( set ) ; if ( DEBUG ) { if ( type == CellSet . TYPE_CLOSED ) System . out . println ( "Closed boundaries:" ) ; else if ( type == CellSet . TYPE_OPEN ) System . out . println ( "Open boundaries:" ) ; else if ( type == CellSet . TYPE_MIXED ) System . out . println ( "Mixed boundaries:" ) ; set . printAsGrid ( ) ; } } } boolean allCornersRound = false ; if ( options . processingOptions . areAllCornersRound ( ) ) allCornersRound = true ; ArrayList closedShapes = new ArrayList ( ) ; sets = closed . iterator ( ) ; while ( sets . hasNext ( ) ) { CellSet set = ( CellSet ) sets . next ( ) ; DiagramComponent shape = DiagramComponent . createClosedFromBoundaryCells ( workGrid , set , cellWidth , cellHeight , allCornersRound ) ; if ( shape != null ) { if ( shape instanceof DiagramShape ) { addToShapes ( ( DiagramShape ) shape ) ; closedShapes . add ( shape ) ; } else if ( shape instanceof CompositeDiagramShape ) addToCompositeShapes ( ( CompositeDiagramShape ) shape ) ; } } if ( options . processingOptions . performSeparationOfCommonEdges ( ) ) separateCommonEdges ( closedShapes ) ; sets = open . iterator ( ) ; while ( sets . hasNext ( ) ) { CellSet set = ( CellSet ) sets . next ( ) ; if ( set . size ( ) == 1 ) { TextGrid . Cell cell = ( TextGrid . Cell ) set . get ( 0 ) ; if ( ! grid . cellContainsDashedLineChar ( cell ) ) { DiagramShape shape = DiagramShape . createSmallLine ( workGrid , cell , cellWidth , cellHeight ) ; if ( shape != null ) { addToShapes ( shape ) ; shape . connectEndsToAnchors ( workGrid , this ) ; } } } else { DiagramComponent shape = CompositeDiagramShape . createOpenFromBoundaryCells ( workGrid , set , cellWidth , cellHeight , allCornersRound ) ; if ( shape != null ) { if ( shape instanceof CompositeDiagramShape ) { addToCompositeShapes ( ( CompositeDiagramShape ) shape ) ; ( ( CompositeDiagramShape ) shape ) . connectEndsToAnchors ( workGrid , this ) ; } else if ( shape instanceof DiagramShape ) { addToShapes ( ( DiagramShape ) shape ) ; ( ( DiagramShape ) shape ) . connectEndsToAnchors ( workGrid , this ) ; ( ( DiagramShape ) shape ) . moveEndsToCellEdges ( grid , this ) ; } } } } Iterator cellColorPairs = grid . findColorCodes ( ) . iterator ( ) ; while ( cellColorPairs . hasNext ( ) ) { TextGrid . CellColorPair pair = ( TextGrid . CellColorPair ) cellColorPairs . next ( ) ; ShapePoint point = new ShapePoint ( getCellMidX ( pair . cell ) , getCellMidY ( pair . cell ) ) ; Iterator shapes = getShapes ( ) . iterator ( ) ; while ( shapes . hasNext ( ) ) { DiagramShape shape = ( DiagramShape ) shapes . next ( ) ; if ( shape . contains ( point ) ) shape . setFillColor ( pair . color ) ; } } Iterator cellTagPairs = grid . findMarkupTags ( ) . iterator ( ) ; while ( cellTagPairs . hasNext ( ) ) { TextGrid . CellTagPair pair = ( TextGrid . CellTagPair ) cellTagPairs . next ( ) ; ShapePoint point = new ShapePoint ( getCellMidX ( pair . cell ) , getCellMidY ( pair . cell ) ) ; Iterator shapes = getShapes ( ) . iterator ( ) ; while ( shapes . hasNext ( ) ) { DiagramShape shape = ( DiagramShape ) shapes . next ( ) ; if ( shape . contains ( point ) ) { if ( pair . tag == TextGrid . TAG_DOCUMENT ) { shape . setType ( DiagramShape . TYPE_DOCUMENT ) ; } else if ( pair . tag == TextGrid . TAG_STORAGE ) { shape . setType ( DiagramShape . TYPE_STORAGE ) ; } else if ( pair . tag == TextGrid . TAG_IO ) { shape . setType ( DiagramShape . TYPE_IO ) ; } } } } Iterator arrowheadCells = workGrid . findArrowheads ( ) . iterator ( ) ; while ( arrowheadCells . hasNext ( ) ) { TextGrid . Cell cell = ( TextGrid . Cell ) arrowheadCells . next ( ) ; DiagramShape arrowhead = DiagramShape . createArrowhead ( workGrid , cell , cellWidth , cellHeight ) ; if ( arrowhead != null ) addToShapes ( arrowhead ) ; else System . err . println ( "Could not create arrowhead shape. Unexpected error." ) ; } Iterator markersIt = grid . getPointMarkersOnLine ( ) . iterator ( ) ; while ( markersIt . hasNext ( ) ) { TextGrid . Cell cell = ( TextGrid . Cell ) markersIt . next ( ) ; DiagramShape mark = new DiagramShape ( ) ; mark . addToPoints ( new ShapePoint ( getCellMidX ( cell ) , getCellMidY ( cell ) ) ) ; mark . setType ( DiagramShape . TYPE_POINT_MARKER ) ; mark . setFillColor ( Color . white ) ; shapes . add ( mark ) ; } removeDuplicateShapes ( ) ; if ( DEBUG ) System . out . println ( "Shape count: " + shapes . size ( ) ) ; if ( DEBUG ) System . out . println ( "Composite shape count: " + compositeShapes . size ( ) ) ; workGrid = new TextGrid ( grid ) ; workGrid . removeNonText ( ) ; TextGrid textGroupGrid = new TextGrid ( workGrid ) ; CellSet gaps = textGroupGrid . getAllBlanksBetweenCharacters ( ) ; textGroupGrid . fillCellsWith ( gaps , '|' ) ; CellSet nonBlank = textGroupGrid . getAllNonBlank ( ) ; ArrayList textGroups = nonBlank . breakIntoDistinctBoundaries ( ) ; if ( DEBUG ) System . out . println ( textGroups . size ( ) + " text groups found" ) ; Font font = FontMeasurer . instance ( ) . getFontFor ( cellHeight ) ; Iterator textGroupIt = textGroups . iterator ( ) ; while ( textGroupIt . hasNext ( ) ) { CellSet textGroupCellSet = ( CellSet ) textGroupIt . next ( ) ; TextGrid isolationGrid = new TextGrid ( width , height ) ; workGrid . copyCellsTo ( textGroupCellSet , isolationGrid ) ; ArrayList strings = isolationGrid . findStrings ( ) ; Iterator it = strings . iterator ( ) ; while ( it . hasNext ( ) ) { TextGrid . CellStringPair pair = ( TextGrid . CellStringPair ) it . next ( ) ; TextGrid . Cell cell = pair . cell ; String string = pair . string ; if ( DEBUG ) System . out . println ( "Found string " + string ) ; TextGrid . Cell lastCell = isolationGrid . new Cell ( cell . x + string . length ( ) - 1 , cell . y ) ; int minX = getCellMinX ( cell ) ; int y = getCellMaxY ( cell ) ; int maxX = getCellMaxX ( lastCell ) ; DiagramText textObject ; if ( FontMeasurer . instance ( ) . getWidthFor ( string , font ) > maxX - minX ) { Font lessWideFont = FontMeasurer . instance ( ) . getFontFor ( maxX - minX , string ) ; textObject = new DiagramText ( minX , y , string , lessWideFont ) ; } else textObject = new DiagramText ( minX , y , string , font ) ; textObject . centerVerticallyBetween ( getCellMinY ( cell ) , getCellMaxY ( cell ) ) ; int otherStart = isolationGrid . otherStringsStartInTheSameColumn ( cell ) ; int otherEnd = isolationGrid . otherStringsEndInTheSameColumn ( lastCell ) ; if ( 0 == otherStart && 0 == otherEnd ) { textObject . centerHorizontallyBetween ( minX , maxX ) ; } else if ( otherEnd > 0 && otherStart == 0 ) { textObject . alignRightEdgeTo ( maxX ) ; } else if ( otherEnd > 0 && otherStart > 0 ) { if ( otherEnd > otherStart ) { textObject . alignRightEdgeTo ( maxX ) ; } else if ( otherEnd == otherStart ) { textObject . centerHorizontallyBetween ( minX , maxX ) ; } } addToTextObjects ( textObject ) ; } } if ( DEBUG ) System . out . println ( "Positioned text" ) ; Iterator shapes = this . getAllDiagramShapes ( ) . iterator ( ) ; while ( shapes . hasNext ( ) ) { DiagramShape shape = ( DiagramShape ) shapes . next ( ) ; Color fillColor = shape . getFillColor ( ) ; if ( shape . isClosed ( ) && shape . getType ( ) != DiagramShape . TYPE_ARROWHEAD && fillColor != null && BitmapRenderer . isColorDark ( fillColor ) ) { Iterator textObjects = getTextObjects ( ) . iterator ( ) ; while ( textObjects . hasNext ( ) ) { DiagramText textObject = ( DiagramText ) textObjects . next ( ) ; if ( shape . intersects ( textObject . getBounds ( ) ) ) { textObject . setColor ( Color . white ) ; } } } } if ( DEBUG ) System . out . println ( "Corrected color of text according to underlying color" ) ; } public ArrayList getAllDiagramShapes ( ) { ArrayList shapes = new ArrayList ( ) ; shapes . addAll ( this . getShapes ( ) ) ; Iterator shapesIt = this . getCompositeShapes ( ) . iterator ( ) ; while ( shapesIt . hasNext ( ) ) { CompositeDiagramShape compShape = ( CompositeDiagramShape ) shapesIt . next ( ) ; shapes . addAll ( compShape . getShapes ( ) ) ; } return shapes ; } private boolean removeObsoleteShapes ( TextGrid grid , ArrayList sets ) { if ( DEBUG ) System . out . println ( "******* Removing obsolete shapes *******" ) ; boolean removedAny = false ; ArrayList filledSets = new ArrayList ( ) ; Iterator it ; if ( VERBOSE_DEBUG ) { System . out . println ( "******* Sets before *******" ) ; it = sets . iterator ( ) ; while ( it . hasNext ( ) ) { CellSet set = ( CellSet ) it . next ( ) ; set . printAsGrid ( ) ; } } it = sets . iterator ( ) ; while ( it . hasNext ( ) ) { CellSet set = ( CellSet ) it . next ( ) ; set = set . getFilledEquivalent ( grid ) ; if ( set == null ) { return false ; } else filledSets . add ( set ) ; } ArrayList toBeRemovedIndices = new ArrayList ( ) ; it = filledSets . iterator ( ) ; while ( it . hasNext ( ) ) { CellSet set = ( CellSet ) it . next ( ) ; if ( VERBOSE_DEBUG ) { System . out . println ( "Looking at set:" ) ; set . printAsGrid ( ) ; } ArrayList common = new ArrayList ( ) ; common . add ( set ) ; Iterator it2 = filledSets . iterator ( ) ; while ( it2 . hasNext ( ) ) { CellSet set2 = ( CellSet ) it2 . next ( ) ; if ( set != set2 && set . hasCommonCells ( set2 ) ) { common . add ( set2 ) ; } } if ( common . size ( ) == 2 ) continue ; CellSet largest = set ; it2 = common . iterator ( ) ; while ( it2 . hasNext ( ) ) { CellSet set2 = ( CellSet ) it2 . next ( ) ; if ( set2 . size ( ) > largest . size ( ) ) { largest = set2 ; } } if ( VERBOSE_DEBUG ) { System . out . println ( "Largest:" ) ; largest . printAsGrid ( ) ; } common . remove ( largest ) ; TextGrid gridOfSmalls = new TextGrid ( largest . getMaxX ( ) + 2 , largest . getMaxY ( ) + 2 ) ; CellSet sumOfSmall = new CellSet ( ) ; it2 = common . iterator ( ) ; while ( it2 . hasNext ( ) ) { CellSet set2 = ( CellSet ) it2 . next ( ) ; if ( VERBOSE_DEBUG ) { System . out . println ( "One of smalls:" ) ; set2 . printAsGrid ( ) ; } gridOfSmalls . fillCellsWith ( set2 , '*' ) ; } if ( VERBOSE_DEBUG ) { System . out . println ( "Grid of smalls:" ) ; gridOfSmalls . printDebug ( ) ; } TextGrid gridLargest = new TextGrid ( largest . getMaxX ( ) + 2 , largest . getMaxY ( ) + 2 ) ; gridLargest . fillCellsWith ( largest , '*' ) ; int index = filledSets . indexOf ( largest ) ; if ( gridLargest . equals ( gridOfSmalls ) && ! toBeRemovedIndices . contains ( new Integer ( index ) ) ) { toBeRemovedIndices . add ( new Integer ( index ) ) ; if ( DEBUG ) { System . out . println ( "Decided to remove set:" ) ; largest . printAsGrid ( ) ; } } } ArrayList setsToBeRemoved = new ArrayList ( ) ; it = toBeRemovedIndices . iterator ( ) ; while ( it . hasNext ( ) ) { int i = ( ( Integer ) it . next ( ) ) . intValue ( ) ; setsToBeRemoved . add ( sets . get ( i ) ) ; } it = setsToBeRemoved . iterator ( ) ; while ( it . hasNext ( ) ) { CellSet set = ( CellSet ) it . next ( ) ; removedAny = true ; sets . remove ( set ) ; } if ( VERBOSE_DEBUG ) { System . out . println ( "******* Sets after *******" ) ; it = sets . iterator ( ) ; while ( it . hasNext ( ) ) { CellSet set = ( CellSet ) it . next ( ) ; set . printAsGrid ( ) ; } } return removedAny ; } public float getMinimumOfCellDimension ( ) { return Math . min ( getCellWidth ( ) , getCellHeight ( ) ) ; } private void separateCommonEdges ( ArrayList shapes ) { float offset = getMinimumOfCellDimension ( ) / 5 ; ArrayList edges = new ArrayList ( ) ; Iterator it = shapes . iterator ( ) ; while ( it . hasNext ( ) ) { DiagramShape shape = ( DiagramShape ) it . next ( ) ; edges . addAll ( shape . getEdges ( ) ) ; } ArrayList listOfGroups = new ArrayList ( ) ; it = edges . iterator ( ) ; while ( it . hasNext ( ) ) { ShapeEdge edge = ( ShapeEdge ) it . next ( ) ; boolean putEdgeIntoExistingGroup = false ; Iterator it2 = listOfGroups . iterator ( ) ; while ( it2 . hasNext ( ) ) { ArrayList group = ( ArrayList ) it2 . next ( ) ; ShapeEdge firstEdge = ( ShapeEdge ) group . get ( 0 ) ; if ( edge . equals ( firstEdge ) ) { group . add ( edge ) ; putEdgeIntoExistingGroup = true ; } } if ( ! putEdgeIntoExistingGroup ) { ArrayList group = new ArrayList ( ) ; group . add ( edge ) ; listOfGroups . add ( group ) ; } } it = listOfGroups . iterator ( ) ; while ( it . hasNext ( ) ) { ArrayList group = ( ArrayList ) it . next ( ) ; if ( group . size ( ) == 1 ) continue ; Iterator it2 = group . iterator ( ) ; while ( it2 . hasNext ( ) ) { ShapeEdge edge = ( ShapeEdge ) it2 . next ( ) ; edge . moveInwardsBy ( offset ) ; } } } private void removeDuplicateShapes ( ) { ArrayList originalShapes = new ArrayList ( ) ; Iterator shapesIt = getShapesIterator ( ) ; while ( shapesIt . hasNext ( ) ) { DiagramShape shape = ( DiagramShape ) shapesIt . next ( ) ; boolean isOriginal = true ; Iterator originals = originalShapes . iterator ( ) ; while ( originals . hasNext ( ) ) { DiagramShape originalShape = ( DiagramShape ) originals . next ( ) ; if ( shape . equals ( originalShape ) ) { isOriginal = false ; } } if ( isOriginal ) originalShapes . add ( shape ) ; } shapes . clear ( ) ; shapes . addAll ( originalShapes ) ; } private void addToTextObjects ( DiagramText shape ) { textObjects . add ( shape ) ; } private void addToCompositeShapes ( CompositeDiagramShape shape ) { compositeShapes . add ( shape ) ; } private void addToShapes ( DiagramShape shape ) { shapes . add ( shape ) ; } public Iterator getShapesIterator ( ) { return shapes . iterator ( ) ; } public int getHeight ( ) { return height ; } public int getWidth ( ) { return width ; } public int getCellWidth ( ) { return cellWidth ; } public int getCellHeight ( ) { return cellHeight ; } public ArrayList getCompositeShapes ( ) { return compositeShapes ; } public ArrayList getShapes ( ) { return shapes ; } public int getCellMinX ( TextGrid . Cell cell ) { return getCellMinX ( cell , cellWidth ) ; } public static int getCellMinX ( TextGrid . Cell cell , int cellXSize ) { return cell . x * cellXSize ; } public int getCellMidX ( TextGrid . Cell cell ) { return getCellMidX ( cell , cellWidth ) ; } public static int getCellMidX ( TextGrid . Cell cell , int cellXSize ) { return cell . x * cellXSize + cellXSize / 2 ; } public int getCellMaxX ( TextGrid . Cell cell ) { return getCellMaxX ( cell , cellWidth ) ; } public static int getCellMaxX ( TextGrid . Cell cell , int cellXSize ) { return cell . x * cellXSize + cellXSize ; } public int getCellMinY ( TextGrid . Cell cell ) { return getCellMinY ( cell , cellHeight ) ; } public static int getCellMinY ( TextGrid . Cell cell , int cellYSize ) { return cell . y * cellYSize ; } public int getCellMidY ( TextGrid . Cell cell ) { return getCellMidY ( cell , cellHeight ) ; } public static int getCellMidY ( TextGrid . Cell cell , int cellYSize ) { return cell . y * cellYSize + cellYSize / 2 ; } public int getCellMaxY ( TextGrid . Cell cell ) { return getCellMaxY ( cell , cellHeight ) ; } public static int getCellMaxY ( TextGrid . Cell cell , int cellYSize ) { return cell . y * cellYSize + cellYSize ; } public TextGrid . Cell getCellFor ( ShapePoint point ) { if ( point == null ) throw new IllegalArgumentException ( "ShapePoint cannot be null" ) ; TextGrid g = new TextGrid ( ) ; return g . new Cell ( ( int ) point . x / cellWidth , ( int ) point . y / cellHeight ) ; } public ArrayList getTextObjects ( ) { return textObjects ; } } 
=======
public class JarvSchemaReaderFactory implements SchemaReaderFactory { public SchemaReader createSchemaReader ( String namespaceUri ) { try { VerifierFactory vf = VerifierFactory . newInstance ( namespaceUri ) ; if ( vf != null ) return new VerifierFactorySchemaReader ( vf ) ; } catch ( VerifierConfigurationException e ) { } return null ; } public Option getOption ( String uri ) { return null ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
