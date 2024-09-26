public class ErrorResponseWriter extends HttpResponseWriterBase { @ SuppressWarnings ( { "ThrowableResultOfMethodCallIgnored" } ) @ Override public void write ( final ChannelHandlerContext context , final Request request , final Response response ) { FullHttpResponse httpResponse = createHttpResponse ( response ) ; httpResponse . headers ( ) . set ( HttpHeaderNames . CONTENT_TYPE , "text/plain; charset=UTF-8" ) ; StringWriter builder = new StringWriter ( ) ; response . getException ( ) . printStackTrace ( new PrintWriter ( builder ) ) ; httpResponse . replace ( Unpooled . copiedBuffer ( builder . toString ( ) , StandardCharsets . UTF_8 ) ) ; if ( request . isKeepAlive ( ) ) { writeContentLength ( httpResponse ) ; writeToChannel ( context , httpResponse , ChannelFutureListener . CLOSE_ON_FAILURE ) ; } else { httpResponse . headers ( ) . set ( HttpHeaderNames . CONNECTION , "close" ) ; writeToChannel ( context , httpResponse , ChannelFutureListener . CLOSE ) ; } } private void writeContentLength ( final FullHttpResponse httpResponse ) { httpResponse . headers ( ) . set ( HttpHeaderNames . CONTENT_LENGTH , String . valueOf ( httpResponse . content ( ) . readableBytes ( ) ) ) ; } private void writeToChannel ( final ChannelHandlerContext context , final HttpResponse response , ChannelFutureListener future ) { context . channel ( ) . write ( response ) . addListener ( future ) ; } } 