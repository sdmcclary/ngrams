public class Axis2InOutMessageReceiver extends AbstractInOutMessageReceiver { private static Logger LOG = Logger . getLogger ( Axis2InOutMessageReceiver . class ) ; private Class serviceClass ; private Axis2MessageReceiver msgReceiver ; public Axis2InOutMessageReceiver ( Axis2MessageReceiver msgReceiver , Class serviceClass ) throws AxisFault { this . serviceClass = serviceClass ; this . msgReceiver = msgReceiver ; } public void invokeBusinessLogic ( MessageContext msgContext , MessageContext newMsgContext ) throws AxisFault { String methodName = this . findOperation ( msgContext ) ; Class serviceMethodArgType = this . findArgumentClass ( methodName ) ; SOAPFactory factory = this . getSOAPFactory ( msgContext ) ; OMElement msgBodyOm = msgContext . getEnvelope ( ) . getBody ( ) . getFirstElement ( ) ; String bindingName = this . findBindingName ( msgBodyOm ) ; EucalyptusMessage wrappedParam = this . bindMessage ( methodName , serviceMethodArgType , msgBodyOm , bindingName ) ; HttpRequest httprequest = ( HttpRequest ) msgContext . getProperty ( GenericHttpDispatcher . HTTP_REQUEST ) ; if ( httprequest == null ) { this . verifyUser ( msgContext , wrappedParam ) ; } else { bindingName = httprequest . getBindingName ( ) ; Policy p = new Policy ( ) ; newMsgContext . setProperty ( RampartMessageData . KEY_RAMPART_POLICY , p ) ; if ( httprequest . isPureClient ( ) ) { if ( wrappedParam instanceof ModifyImageAttributeType ) { ModifyImageAttributeType pure = ( ( ModifyImageAttributeType ) wrappedParam ) ; pure . setImageId ( purifyImageIn ( pure . getImageId ( ) ) ) ; } else if ( wrappedParam instanceof DescribeImageAttributeType ) { DescribeImageAttributeType pure = ( ( DescribeImageAttributeType ) wrappedParam ) ; pure . setImageId ( purifyImageIn ( pure . getImageId ( ) ) ) ; } else if ( wrappedParam instanceof ResetImageAttributeType ) { ResetImageAttributeType pure = ( ( ResetImageAttributeType ) wrappedParam ) ; pure . setImageId ( purifyImageIn ( pure . getImageId ( ) ) ) ; } else if ( wrappedParam instanceof DescribeImagesType ) { ArrayList < String > strs = Lists . newArrayList ( ) ; for ( String imgId : ( ( DescribeImagesType ) wrappedParam ) . getImagesSet ( ) ) { strs . add ( purifyImageIn ( imgId ) ) ; } ( ( DescribeImagesType ) wrappedParam ) . setImagesSet ( strs ) ; } else if ( wrappedParam instanceof DeregisterImageType ) { DeregisterImageType pure = ( ( DeregisterImageType ) wrappedParam ) ; pure . setImageId ( purifyImageIn ( pure . getImageId ( ) ) ) ; } else if ( wrappedParam instanceof RunInstancesType ) { RunInstancesType pure = ( ( RunInstancesType ) wrappedParam ) ; pure . setImageId ( purifyImageIn ( pure . getImageId ( ) ) ) ; pure . setKernelId ( purifyImageIn ( pure . getKernelId ( ) ) ) ; pure . setRamdiskId ( purifyImageIn ( pure . getRamdiskId ( ) ) ) ; } } } MuleMessage message = this . invokeService ( methodName , wrappedParam ) ; if ( message == null ) throw new AxisFault ( "Received a NULL response. This is a bug -- it should NEVER happen." ) ; this . checkException ( message ) ; if ( httprequest != null ) { if ( httprequest . isPureClient ( ) ) { if ( message . getPayload ( ) instanceof DescribeImagesResponseType ) { DescribeImagesResponseType purify = ( DescribeImagesResponseType ) message . getPayload ( ) ; for ( ImageDetails img : purify . getImagesSet ( ) ) { img . setImageId ( img . getImageId ( ) . replaceFirst ( "^e" , "a" ) . toLowerCase ( ) ) ; if ( img . getKernelId ( ) != null ) img . setKernelId ( img . getKernelId ( ) . replaceFirst ( "^e" , "a" ) . toLowerCase ( ) ) ; if ( img . getRamdiskId ( ) != null ) img . setRamdiskId ( img . getRamdiskId ( ) . replaceFirst ( "^e" , "a" ) . toLowerCase ( ) ) ; } } else if ( message . getPayload ( ) instanceof DescribeInstancesResponseType ) { DescribeInstancesResponseType purify = ( DescribeInstancesResponseType ) message . getPayload ( ) ; for ( ReservationInfoType rsvInfo : purify . getReservationSet ( ) ) { for ( RunningInstancesItemType r : rsvInfo . getInstancesSet ( ) ) { r . setImageId ( r . getImageId ( ) . replaceFirst ( "^e" , "a" ) . toLowerCase ( ) ) ; if ( r . getKernel ( ) != null ) r . setKernel ( r . getKernel ( ) . replaceFirst ( "^e" , "a" ) . toLowerCase ( ) ) ; if ( r . getRamdisk ( ) != null ) r . setRamdisk ( r . getRamdisk ( ) . replaceFirst ( "^e" , "a" ) . toLowerCase ( ) ) ; } } } } } if ( newMsgContext != null ) { SOAPEnvelope envelope = generateMessage ( methodName , factory , bindingName , message . getPayload ( ) , httprequest == null ? null : httprequest . getOriginalNamespace ( ) ) ; newMsgContext . setEnvelope ( envelope ) ; } newMsgContext . setProperty ( Axis2HttpWorker . REAL_HTTP_REQUEST , msgContext . getProperty ( Axis2HttpWorker . REAL_HTTP_REQUEST ) ) ; newMsgContext . setProperty ( Axis2HttpWorker . REAL_HTTP_RESPONSE , msgContext . getProperty ( Axis2HttpWorker . REAL_HTTP_RESPONSE ) ) ; LOG . info ( "Returning reply: " + message . getPayload ( ) ) ; if ( message . getPayload ( ) instanceof WalrusErrorMessageType ) { WalrusErrorMessageType errorMessage = ( WalrusErrorMessageType ) message . getPayload ( ) ; msgContext . setProperty ( Axis2HttpWorker . HTTP_STATUS , errorMessage . getHttpCode ( ) ) ; newMsgContext . setProperty ( Axis2HttpWorker . HTTP_STATUS , errorMessage . getHttpCode ( ) ) ; newMsgContext . setProperty ( "messageType" , "application/walrus" ) ; return ; } Boolean putType = ( Boolean ) msgContext . getProperty ( WalrusProperties . STREAMING_HTTP_PUT ) ; Boolean getType = ( Boolean ) msgContext . getProperty ( WalrusProperties . STREAMING_HTTP_GET ) ; if ( getType != null || putType != null ) { WalrusDataResponseType reply = ( WalrusDataResponseType ) message . getPayload ( ) ; AxisHttpResponse response = ( AxisHttpResponse ) msgContext . getProperty ( Axis2HttpWorker . REAL_HTTP_RESPONSE ) ; response . addHeader ( new BasicHeader ( "Last-Modified" , reply . getLastModified ( ) ) ) ; response . addHeader ( new BasicHeader ( "ETag" , '\"' + reply . getEtag ( ) + '\"' ) ) ; if ( getType != null ) { newMsgContext . setProperty ( WalrusProperties . STREAMING_HTTP_GET , getType ) ; WalrusDataRequestType request = ( WalrusDataRequestType ) wrappedParam ; Boolean isCompressed = request . getIsCompressed ( ) ; if ( isCompressed == null ) isCompressed = false ; if ( isCompressed ) { newMsgContext . setProperty ( "GET_COMPRESSED" , isCompressed ) ; } else { Long contentLength = reply . getSize ( ) ; response . addHeader ( new BasicHeader ( HTTP . CONTENT_LEN , String . valueOf ( contentLength ) ) ) ; } List < MetaDataEntry > metaData = reply . getMetaData ( ) ; for ( MetaDataEntry metaDataEntry : metaData ) { response . addHeader ( new BasicHeader ( WalrusProperties . AMZ_META_HEADER_PREFIX + metaDataEntry . getName ( ) , metaDataEntry . getValue ( ) ) ) ; } if ( getType . equals ( Boolean . TRUE ) ) { newMsgContext . setProperty ( "GET_KEY" , request . getBucket ( ) + "." + request . getKey ( ) ) ; newMsgContext . setProperty ( "GET_RANDOM_KEY" , request . getRandomKey ( ) ) ; } newMsgContext . setProperty ( "messageType" , "application/walrus" ) ; } else if ( putType != null ) { if ( reply instanceof PostObjectResponseType ) { PostObjectResponseType postReply = ( PostObjectResponseType ) reply ; String redirectUrl = postReply . getRedirectUrl ( ) ; if ( redirectUrl != null ) { response . addHeader ( new BasicHeader ( "Location" , redirectUrl ) ) ; msgContext . setProperty ( Axis2HttpWorker . HTTP_STATUS , HttpStatus . SC_SEE_OTHER ) ; newMsgContext . setProperty ( Axis2HttpWorker . HTTP_STATUS , HttpStatus . SC_SEE_OTHER ) ; newMsgContext . setProperty ( "messageType" , "application/walrus" ) ; } else { Integer successCode = postReply . getSuccessCode ( ) ; if ( successCode != null ) { newMsgContext . setProperty ( Axis2HttpWorker . HTTP_STATUS , successCode ) ; if ( successCode == 201 ) { return ; } else { newMsgContext . setProperty ( "messageType" , "application/walrus" ) ; return ; } } } } response . addHeader ( new BasicHeader ( HTTP . CONTENT_LEN , String . valueOf ( 0 ) ) ) ; } } } private String purifyImageIn ( String id ) { id = "e" + id . substring ( 1 , 4 ) + id . substring ( 4 ) . toUpperCase ( ) ; return id ; } private void checkException ( final MuleMessage message ) throws AxisFault { if ( message . getPayload ( ) instanceof EucalyptusErrorMessageType ) throw new AxisFault ( message . getPayload ( ) . toString ( ) ) ; else if ( message . getExceptionPayload ( ) != null ) { MuleException umoException = ExceptionHelper . getRootMuleException ( message . getExceptionPayload ( ) . getException ( ) ) ; if ( umoException . getCause ( ) != null ) throw AxisFault . makeFault ( umoException . getCause ( ) ) ; else throw AxisFault . makeFault ( umoException ) ; } } private SOAPEnvelope generateMessage ( final String methodName , final SOAPFactory factory , String bindingName , final Object response , final String altNs ) { SOAPEnvelope envelope = null ; LOG . info ( "[" + serviceClass . getSimpleName ( ) + ":" + methodName + "] Got return type " + response . getClass ( ) . getSimpleName ( ) ) ; if ( response instanceof AddClusterResponseType ) bindingName = "msgs_eucalyptus_ucsb_edu" ; try { envelope = factory . getDefaultEnvelope ( ) ; Binding binding = BindingManager . getBinding ( bindingName , this . serviceClass ) ; OMElement msgElement = binding . toOM ( response , altNs ) ; envelope . getBody ( ) . addChild ( msgElement ) ; } catch ( JiBXException e ) { LOG . error ( e , e ) ; } LOG . info ( "[" + serviceClass . getSimpleName ( ) + ":" + methodName + "] Returning message of type " + response . getClass ( ) . getSimpleName ( ) ) ; return envelope ; } private MuleMessage invokeService ( final String methodName , final EucalyptusMessage wrappedParam ) throws AxisFault { LOG . info ( "[" + serviceClass . getSimpleName ( ) + ":" + methodName + "] Invoking method " + methodName ) ; MuleMessage message = null ; try { message = this . msgReceiver . routeMessage ( new DefaultMuleMessage ( this . msgReceiver . getConnector ( ) . getMessageAdapter ( wrappedParam ) ) , true ) ; } catch ( MuleException wsException ) { MuleException umoException = ExceptionHelper . getRootMuleException ( wsException ) ; if ( umoException . getCause ( ) != null ) throw AxisFault . makeFault ( umoException . getCause ( ) ) ; else throw AxisFault . makeFault ( umoException ) ; } return message ; } private EucalyptusMessage bindMessage ( final String methodName , final Class serviceMethodArgType , final OMElement msgBodyOm , final String bindingName ) throws AxisFault { EucalyptusMessage wrappedParam = null ; try { Binding msgBinding = BindingManager . getBinding ( bindingName , this . serviceClass ) ; wrappedParam = ( EucalyptusMessage ) msgBinding . fromOM ( msgBodyOm , serviceMethodArgType ) ; LOG . info ( "[" + serviceClass . getSimpleName ( ) + ":" + methodName + "] Unmarshalled parameter type " + wrappedParam . getClass ( ) ) ; } catch ( JiBXException e ) { LOG . error ( e , e ) ; throw AxisFault . makeFault ( e ) ; } return wrappedParam ; } private String findBindingName ( final OMElement msgBodyOm ) { String bindingName = null ; String nsUri = msgBodyOm . getNamespace ( ) . getNamespaceURI ( ) ; bindingName = BindingUtil . sanitizeNamespace ( nsUri ) ; return bindingName ; } private Class findArgumentClass ( final String methodName ) throws AxisFault { Class serviceMethodArgType = null ; try { serviceMethodArgType = Class . forName ( "edu.ucsb.eucalyptus.msgs." + methodName + "Type" ) ; } catch ( ClassNotFoundException e ) { throw new AxisFault ( "Argument type not found: edu.ucsb.eucalyptus.msgs." + methodName + "Type" ) ; } return serviceMethodArgType ; } private String findOperation ( final MessageContext msgContext ) throws AxisFault { AxisOperation op = msgContext . getOperationContext ( ) . getAxisOperation ( ) ; String methodName = null ; if ( op == null || ( op . getName ( ) == null ) || ( ( methodName = JavaUtils . xmlNameToJava ( op . getName ( ) . getLocalPart ( ) ) ) == null ) ) throw new AxisFault ( "Operation not found: " + op . getName ( ) ) ; return methodName ; } private void verifyUser ( MessageContext msgContext , EucalyptusMessage msg ) throws EucalyptusCloudException { Vector < WSHandlerResult > wsResults = ( Vector < WSHandlerResult > ) msgContext . getProperty ( WSHandlerConstants . RECV_RESULTS ) ; for ( WSHandlerResult wsResult : wsResults ) if ( wsResult . getResults ( ) != null ) for ( WSSecurityEngineResult engResult : ( Vector < WSSecurityEngineResult > ) wsResult . getResults ( ) ) if ( engResult . containsKey ( WSSecurityEngineResult . TAG_X509_CERTIFICATE ) ) { X509Certificate cert = ( X509Certificate ) engResult . get ( WSSecurityEngineResult . TAG_X509_CERTIFICATE ) ; msg = this . msgReceiver . getProperties ( ) . getAuthenticator ( ) . authenticate ( cert , msg ) ; } } } 