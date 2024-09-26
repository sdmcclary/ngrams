<<<<<<< HEAD
public class RPath extends RGeomElem { public int type = RGeomElem . SUBSHAPE ; public RCommand [ ] commands ; public RPoint lastPoint ; boolean closed = false ; public RPath ( ) { this . lastPoint = new RPoint ( ) ; } public RPath ( RPoint [ ] points ) { if ( points == null ) return ; this . lastPoint = points [ 0 ] ; for ( int i = 1 ; i < points . length ; i ++ ) { this . addLineTo ( points [ i ] ) ; } } public RPath ( float x , float y ) { this . lastPoint = new RPoint ( x , y ) ; } public RPath ( RPoint p ) { this . lastPoint = p ; } public RPath ( RPath s ) { int numCommands = s . countCommands ( ) ; if ( numCommands != 0 ) { lastPoint = new RPoint ( s . commands [ 0 ] . startPoint ) ; for ( int i = 0 ; i < numCommands ; i ++ ) { this . append ( new RCommand ( s . commands [ i ] , lastPoint ) ) ; lastPoint = commands [ i ] . endPoint ; } } closed = s . closed ; setStyle ( s ) ; } public RPath ( RCommand c ) { this ( ) ; this . addCommand ( c ) ; } public int countCommands ( ) { if ( this . commands == null ) { return 0 ; } return this . commands . length ; } public RPoint [ ] getHandles ( ) { int numCommands = countCommands ( ) ; RPoint [ ] result = null ; RPoint [ ] newresult = null ; for ( int i = 0 ; i < numCommands ; i ++ ) { RPoint [ ] newPoints = commands [ i ] . getHandles ( ) ; if ( newPoints != null ) { if ( result == null ) { result = new RPoint [ newPoints . length ] ; System . arraycopy ( newPoints , 0 , result , 0 , newPoints . length ) ; } else { int overlap = 0 ; if ( newPoints [ 0 ] == result [ result . length - 1 ] ) { overlap = 1 ; } newresult = new RPoint [ result . length + newPoints . length - overlap ] ; System . arraycopy ( result , 0 , newresult , 0 , result . length ) ; System . arraycopy ( newPoints , overlap , newresult , result . length , newPoints . length - overlap ) ; result = newresult ; } } } return result ; } public RPoint [ ] getPoints ( ) { int numCommands = countCommands ( ) ; if ( numCommands == 0 ) { return null ; } RCommand . segmentAccOffset = RCommand . segmentOffset ; RPoint [ ] result = null ; RPoint [ ] newresult = null ; for ( int i = 0 ; i < numCommands ; i ++ ) { RPoint [ ] newPoints = commands [ i ] . getPoints ( false ) ; if ( newPoints != null ) { if ( result == null ) { result = new RPoint [ newPoints . length ] ; System . arraycopy ( newPoints , 0 , result , 0 , newPoints . length ) ; } else { RPoint lastp = result [ result . length - 1 ] ; RPoint firstp = newPoints [ 0 ] ; int overlap = 0 ; if ( ( lastp . x == firstp . x ) && ( lastp . y == firstp . y ) ) { overlap = 1 ; } newresult = new RPoint [ result . length + newPoints . length - overlap ] ; System . arraycopy ( result , 0 , newresult , 0 , result . length ) ; System . arraycopy ( newPoints , overlap , newresult , result . length , newPoints . length - overlap ) ; result = newresult ; } } } newresult = new RPoint [ result . length + 1 ] ; System . arraycopy ( result , 0 , newresult , 0 , result . length ) ; newresult [ newresult . length - 1 ] = new RPoint ( commands [ numCommands - 1 ] . endPoint ) ; return newresult ; } public RPoint [ ] [ ] getPointsInPaths ( ) { RPoint [ ] [ ] result = { this . getPoints ( ) } ; return result ; } public RPoint [ ] [ ] getHandlesInPaths ( ) { RPoint [ ] [ ] result = { this . getHandles ( ) } ; return result ; } public RPoint [ ] [ ] getTangentsInPaths ( ) { RPoint [ ] [ ] result = { this . getTangents ( ) } ; return result ; } protected void calculateCurveLengths ( ) { lenCurves = new float [ countCommands ( ) ] ; lenCurve = 0F ; for ( int i = 0 ; i < countCommands ( ) ; i ++ ) { lenCurves [ i ] = commands [ i ] . getCurveLength ( ) ; lenCurve += lenCurves [ i ] ; } } public RPoint [ ] getTangents ( ) { int numCommands = countCommands ( ) ; if ( numCommands == 0 ) { return null ; } RPoint [ ] result = null ; RPoint [ ] newresult = null ; for ( int i = 0 ; i < numCommands ; i ++ ) { RPoint [ ] newTangents = commands [ i ] . getTangents ( ) ; if ( newTangents != null ) { if ( newTangents . length != 1 ) { int overlap = 1 ; if ( result == null ) { result = new RPoint [ newTangents . length ] ; System . arraycopy ( newTangents , 0 , result , 0 , newTangents . length ) ; } else { newresult = new RPoint [ result . length + newTangents . length - overlap ] ; System . arraycopy ( result , 0 , newresult , 0 , result . length ) ; System . arraycopy ( newTangents , overlap , newresult , result . length , newTangents . length - overlap ) ; result = newresult ; } } } } return result ; } public RPoint [ ] intersectionPoints ( RCommand other ) { int numCommands = countCommands ( ) ; if ( numCommands == 0 ) { return null ; } RPoint [ ] result = null ; RPoint [ ] newresult = null ; for ( int i = 0 ; i < numCommands ; i ++ ) { RPoint [ ] newPoints = commands [ i ] . intersectionPoints ( other ) ; if ( newPoints != null ) { if ( result == null ) { result = new RPoint [ newPoints . length ] ; System . arraycopy ( newPoints , 0 , result , 0 , newPoints . length ) ; } else { newresult = new RPoint [ result . length + newPoints . length ] ; System . arraycopy ( result , 0 , newresult , 0 , result . length ) ; System . arraycopy ( newPoints , 0 , newresult , result . length , newPoints . length ) ; result = newresult ; } } } return result ; } public RPoint [ ] intersectionPoints ( RPath other ) { int numCommands = countCommands ( ) ; int numOtherCommands = other . countCommands ( ) ; if ( numCommands == 0 ) { return null ; } RPoint [ ] result = null ; RPoint [ ] newresult = null ; for ( int j = 0 ; j < numOtherCommands ; j ++ ) { for ( int i = 0 ; i < numCommands ; i ++ ) { RPoint [ ] newPoints = commands [ i ] . intersectionPoints ( other . commands [ j ] ) ; if ( newPoints != null ) { if ( result == null ) { result = new RPoint [ newPoints . length ] ; System . arraycopy ( newPoints , 0 , result , 0 , newPoints . length ) ; } else { newresult = new RPoint [ result . length + newPoints . length ] ; System . arraycopy ( result , 0 , newresult , 0 , result . length ) ; System . arraycopy ( newPoints , 0 , newresult , result . length , newPoints . length ) ; result = newresult ; } } } } return result ; } public RClosest closestPoints ( RCommand other ) { int numCommands = countCommands ( ) ; if ( numCommands == 0 ) { return null ; } float minDist = 100000 ; RClosest result = new RClosest ( ) ; for ( int i = 0 ; i < numCommands ; i ++ ) { RClosest currResult = commands [ i ] . closestPoints ( other ) ; result . update ( currResult ) ; } return result ; } public RClosest closestPoints ( RPath other ) { int numCommands = countCommands ( ) ; int numOtherCommands = other . countCommands ( ) ; if ( numCommands == 0 ) { return null ; } float minDist = 100000 ; RClosest result = new RClosest ( ) ; for ( int j = 0 ; j < numOtherCommands ; j ++ ) { for ( int i = 0 ; i < numCommands ; i ++ ) { RClosest currResult = commands [ i ] . closestPoints ( other . commands [ j ] ) ; result . update ( currResult ) ; } } return result ; } public RPoint getPoint ( float t ) { int numCommands = countCommands ( ) ; if ( numCommands == 0 ) { return new RPoint ( ) ; } if ( t == 0.0F ) { return commands [ 0 ] . getPoint ( 0F ) ; } if ( t == 1.0F ) { return commands [ numCommands - 1 ] . getPoint ( 1F ) ; } float [ ] indAndAdv = indAndAdvAt ( t ) ; int indOfElement = ( int ) ( indAndAdv [ 0 ] ) ; float advOfElement = indAndAdv [ 1 ] ; return commands [ indOfElement ] . getPoint ( advOfElement ) ; } public RPoint getTangent ( float t ) { int numCommands = countCommands ( ) ; if ( numCommands == 0 ) { return new RPoint ( ) ; } if ( t == 0.0F ) { return commands [ 0 ] . getTangent ( 0F ) ; } if ( t == 1.0F ) { return commands [ numCommands - 1 ] . getTangent ( 1F ) ; } float [ ] indAndAdv = indAndAdvAt ( t ) ; int indOfElement = ( int ) ( indAndAdv [ 0 ] ) ; float advOfElement = indAndAdv [ 1 ] ; return commands [ indOfElement ] . getTangent ( advOfElement ) ; } public boolean contains ( RPoint p ) { float testx = p . x ; float testy = p . y ; RRectangle bbox = getBounds ( ) ; float xmin = bbox . getMinX ( ) ; float xmax = bbox . getMaxX ( ) ; float ymin = bbox . getMinY ( ) ; float ymax = bbox . getMaxY ( ) ; if ( ( testx < xmin ) || ( testx > xmax ) || ( testy < ymin ) || ( testy > ymax ) ) { return false ; } RPoint [ ] verts = getPoints ( ) ; if ( verts == null ) { return false ; } int nvert = verts . length ; int i , j = 0 ; boolean c = false ; for ( i = 0 , j = nvert - 1 ; i < nvert ; j = i ++ ) { if ( ( ( verts [ i ] . y > testy ) != ( verts [ j ] . y > testy ) ) && ( testx < ( verts [ j ] . x - verts [ i ] . x ) * ( testy - verts [ i ] . y ) / ( verts [ j ] . y - verts [ i ] . y ) + verts [ i ] . x ) ) c = ! c ; } return c ; } public void insertHandle ( float t ) { if ( ( t == 0F ) || ( t == 1F ) ) { return ; } float [ ] indAndAdv = indAndAdvAt ( t ) ; int indOfElement = ( int ) ( indAndAdv [ 0 ] ) ; float advOfElement = indAndAdv [ 1 ] ; RCommand [ ] splittedCommands = commands [ indOfElement ] . split ( advOfElement ) ; if ( splittedCommands [ 0 ] == null || splittedCommands [ 1 ] == null ) { return ; } extract ( indOfElement ) ; insert ( splittedCommands [ 1 ] , indOfElement ) ; insert ( splittedCommands [ 0 ] , indOfElement ) ; lenCurves = null ; lenCurve = - 1F ; return ; } public void insertHandleInPaths ( float t ) { if ( ( t == 0F ) || ( t == 1F ) ) { return ; } int numCommands = countCommands ( ) ; for ( int i = 0 ; i < numCommands * 2 ; i += 2 ) { RCommand [ ] splittedCommands = commands [ i ] . split ( t ) ; if ( splittedCommands [ 0 ] == null || splittedCommands [ 1 ] == null ) { return ; } extract ( i ) ; insert ( splittedCommands [ 1 ] , i ) ; insert ( splittedCommands [ 0 ] , i ) ; } lenCurves = null ; lenCurve = - 1F ; return ; } public RPath [ ] split ( float t ) { RPath [ ] result = new RPath [ 2 ] ; int numCommands = countCommands ( ) ; if ( numCommands == 0 ) { return null ; } if ( t == 0.0F ) { result [ 0 ] = new RPath ( ) ; result [ 1 ] = new RPath ( this ) ; result [ 0 ] . setStyle ( this ) ; result [ 1 ] . setStyle ( this ) ; return result ; } if ( t == 1.0F ) { result [ 0 ] = new RPath ( this ) ; result [ 1 ] = new RPath ( ) ; result [ 0 ] . setStyle ( this ) ; result [ 1 ] . setStyle ( this ) ; return result ; } float [ ] indAndAdv = indAndAdvAt ( t ) ; int indOfElement = ( int ) ( indAndAdv [ 0 ] ) ; float advOfElement = indAndAdv [ 1 ] ; RCommand [ ] splittedCommands = commands [ indOfElement ] . split ( advOfElement ) ; result [ 0 ] = new RPath ( ) ; for ( int i = 0 ; i < indOfElement ; i ++ ) { result [ 0 ] . addCommand ( new RCommand ( commands [ i ] ) ) ; } result [ 0 ] . addCommand ( new RCommand ( splittedCommands [ 0 ] ) ) ; result [ 0 ] . setStyle ( this ) ; result [ 1 ] = new RPath ( ) ; for ( int i = indOfElement + 1 ; i < countCommands ( ) ; i ++ ) { result [ 1 ] . addCommand ( new RCommand ( commands [ i ] ) ) ; } result [ 1 ] . addCommand ( new RCommand ( splittedCommands [ 1 ] ) ) ; result [ 1 ] . setStyle ( this ) ; return result ; } public void polygonize ( ) { RPoint [ ] points = getPoints ( ) ; if ( points == null ) { this . commands = null ; } else { RPath result = new RPath ( points [ 0 ] ) ; for ( int i = 1 ; i < points . length ; i ++ ) { result . addLineTo ( points [ i ] ) ; } this . commands = result . commands ; } } public void draw ( PGraphics g ) { countCommands ( ) ; int lastSegmentator = RCommand . segmentType ; RCommand . setSegmentator ( RCommand . ADAPTATIVE ) ; RPoint [ ] points = getPoints ( ) ; if ( points == null ) { return ; } g . beginShape ( ) ; for ( int i = 0 ; i < points . length ; i ++ ) { g . vertex ( points [ i ] . x , points [ i ] . y ) ; } g . endShape ( closed ? PConstants . CLOSE : PConstants . OPEN ) ; RCommand . setSegmentator ( lastSegmentator ) ; } public void draw ( PApplet g ) { countCommands ( ) ; int lastSegmentator = RCommand . segmentType ; RCommand . setSegmentator ( RCommand . ADAPTATIVE ) ; RPoint [ ] points = getPoints ( ) ; RCommand . setSegmentator ( lastSegmentator ) ; if ( points == null ) { return ; } g . beginShape ( ) ; for ( int i = 0 ; i < points . length ; i ++ ) { g . vertex ( points [ i ] . x , points [ i ] . y ) ; } g . endShape ( closed ? PConstants . CLOSE : PConstants . OPEN ) ; RCommand . setSegmentator ( lastSegmentator ) ; } public void addCommand ( RCommand p ) { this . append ( p ) ; lastPoint = commands [ commands . length - 1 ] . endPoint ; } public void addBezierTo ( RPoint cp1 , RPoint cp2 , RPoint end ) { this . addCommand ( RCommand . createBezier4 ( lastPoint , cp1 , cp2 , end ) ) ; } public void addBezierTo ( float cp1x , float cp1y , float cp2x , float cp2y , float endx , float endy ) { RPoint cp1 = new RPoint ( cp1x , cp1y ) ; RPoint cp2 = new RPoint ( cp2x , cp2y ) ; RPoint end = new RPoint ( endx , endy ) ; addBezierTo ( cp1 , cp2 , end ) ; } public void addQuadTo ( RPoint cp1 , RPoint end ) { this . addCommand ( RCommand . createBezier3 ( lastPoint , cp1 , end ) ) ; } public void addQuadTo ( float cp1x , float cp1y , float endx , float endy ) { RPoint cp1 = new RPoint ( cp1x , cp1y ) ; RPoint end = new RPoint ( endx , endy ) ; addQuadTo ( cp1 , end ) ; } public void addLineTo ( RPoint end ) { this . addCommand ( RCommand . createLine ( lastPoint , end ) ) ; } public void addLineTo ( float endx , float endy ) { RPoint end = new RPoint ( endx , endy ) ; addLineTo ( end ) ; } public void addClose ( ) { if ( commands == null ) { return ; } if ( ( commands [ commands . length - 1 ] . endPoint . x == commands [ 0 ] . startPoint . x ) && ( commands [ commands . length - 1 ] . endPoint . y == commands [ 0 ] . startPoint . y ) ) { commands [ commands . length - 1 ] . endPoint = new RPoint ( commands [ 0 ] . startPoint . x , commands [ 0 ] . startPoint . y ) ; lastPoint = commands [ commands . length - 1 ] . endPoint ; } else { addLineTo ( new RPoint ( commands [ 0 ] . startPoint . x , commands [ 0 ] . startPoint . y ) ) ; } closed = true ; } public RPolygon toPolygon ( ) { return this . toShape ( ) . toPolygon ( ) ; } public RShape toShape ( ) { return new RShape ( this ) ; } public RMesh toMesh ( ) { return this . toPolygon ( ) . toMesh ( ) ; } public int getType ( ) { return type ; } public void print ( ) { for ( int i = 0 ; i < countCommands ( ) ; i ++ ) { String commandType = "" ; switch ( commands [ i ] . commandType ) { case RCommand . LINETO : commandType = "LINETO" ; break ; case RCommand . CUBICBEZIERTO : commandType = "BEZIERTO" ; break ; case RCommand . QUADBEZIERTO : commandType = "QUADBEZIERTO" ; break ; } System . out . println ( "cmd type: " + commandType ) ; System . out . print ( "start point: " ) ; commands [ i ] . startPoint . print ( ) ; System . out . print ( "\n" ) ; System . out . print ( "end point: " ) ; commands [ i ] . endPoint . print ( ) ; System . out . print ( "\n" ) ; if ( commands [ i ] . controlPoints != null ) { System . out . println ( "control points: " ) ; for ( int j = 0 ; j < commands [ i ] . controlPoints . length ; j ++ ) { commands [ i ] . controlPoints [ j ] . print ( ) ; System . out . print ( " " ) ; System . out . print ( "\n" ) ; } } System . out . print ( "\n" ) ; } } private float [ ] indAndAdvAt ( float t ) { int indOfElement = 0 ; float [ ] lengthsCurves = getCurveLengths ( ) ; float lengthCurve = getCurveLength ( ) ; float accumulatedAdvancement = lengthsCurves [ indOfElement ] / lengthCurve ; float prevAccumulatedAdvancement = 0F ; while ( t > accumulatedAdvancement ) { indOfElement ++ ; prevAccumulatedAdvancement = accumulatedAdvancement ; accumulatedAdvancement += ( lengthsCurves [ indOfElement ] / lengthCurve ) ; } float advOfElement = ( t - prevAccumulatedAdvancement ) / ( lengthsCurves [ indOfElement ] / lengthCurve ) ; float [ ] indAndAdv = new float [ 2 ] ; indAndAdv [ 0 ] = indOfElement ; indAndAdv [ 1 ] = advOfElement ; return indAndAdv ; } private void append ( RCommand nextcommand ) { RCommand [ ] newcommands ; if ( commands == null ) { newcommands = new RCommand [ 1 ] ; newcommands [ 0 ] = nextcommand ; } else { newcommands = new RCommand [ this . commands . length + 1 ] ; System . arraycopy ( this . commands , 0 , newcommands , 0 , this . commands . length ) ; newcommands [ this . commands . length ] = nextcommand ; } this . commands = newcommands ; } private void insert ( RCommand newcommand , int i ) throws RuntimeException { if ( i < 0 ) { throw new RuntimeException ( "Negative values for indexes are not valid." ) ; } RCommand [ ] newcommands ; if ( commands == null ) { newcommands = new RCommand [ 1 ] ; newcommands [ 0 ] = newcommand ; } else { if ( i > commands . length ) { throw new RuntimeException ( "Index out of the bounds.  You are trying to insert an element with an index higher than the number of commands in the group." ) ; } newcommands = new RCommand [ this . commands . length + 1 ] ; System . arraycopy ( this . commands , 0 , newcommands , 0 , i ) ; newcommands [ i ] = newcommand ; System . arraycopy ( this . commands , i , newcommands , i + 1 , this . commands . length - i ) ; } this . commands = newcommands ; } private void extract ( int i ) throws RuntimeException { RCommand [ ] newcommands ; if ( commands == null ) { throw new RuntimeException ( "The group is empty. No commands to remove." ) ; } else { if ( i < 0 ) { throw new RuntimeException ( "Negative values for indexes are not valid." ) ; } if ( i > commands . length - 1 ) { throw new RuntimeException ( "Index out of the bounds of the group.  You are trying to erase an element with an index higher than the number of commands in the group." ) ; } if ( commands . length == 1 ) { newcommands = null ; } else if ( i == 0 ) { newcommands = new RCommand [ this . commands . length - 1 ] ; System . arraycopy ( this . commands , 1 , newcommands , 0 , this . commands . length - 1 ) ; } else if ( i == commands . length - 1 ) { newcommands = new RCommand [ this . commands . length - 1 ] ; System . arraycopy ( this . commands , 0 , newcommands , 0 , this . commands . length - 1 ) ; } else { newcommands = new RCommand [ this . commands . length - 1 ] ; System . arraycopy ( this . commands , 0 , newcommands , 0 , i ) ; System . arraycopy ( this . commands , i + 1 , newcommands , i , this . commands . length - i - 1 ) ; } } this . commands = newcommands ; } } 
=======
public class IncorrectSchemaException extends Exception { } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
