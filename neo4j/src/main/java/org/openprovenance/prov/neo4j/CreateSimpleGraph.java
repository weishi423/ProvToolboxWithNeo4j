package org.openprovenance.prov.neo4j;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CreateSimpleGraph
{
    private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data/";
    public static final Map<String, URI> nodeMap = new HashMap<String, URI>();

    public static void main( String[] args ) throws URISyntaxException
    {
        checkDatabaseIsRunning();

        // START SNIPPET: nodesAndProps
        URI firstNode = createNode();
        addNodeToIndex( "id", "test1", firstNode);
        addProperty( firstNode, "name", "Joe Strummer" );
        URI secondNode = createNode();
        addNodeToIndex( "id", "test2", secondNode);
        addProperty( secondNode, "band", "The Clash" );
        // END SNIPPET: nodesAndProps

        // START SNIPPET: addRel
        URI relationshipUri = addRelationship( firstNode, secondNode, "singer"
                );
        // END SNIPPET: addRel

        // START SNIPPET: addMetaToRel
       Map<String,String> property=new HashMap<String,String>();
		
		property.put("timestamp", "20120116");
		property.put("title", "test_title6");
		
		
		addMetadataToProperty(relationshipUri,property);
        // END SNIPPET: addMetaToRel

        // START SNIPPET: queryForSingers
     //   findSingersInBands( firstNode );
        // END SNIPPET: queryForSingers
    }

//    private static void findSingersInBands( URI startNode )
//            throws URISyntaxException
//    {
//        // START SNIPPET: traversalDesc
//        // TraversalDescription turns into JSON to send to the Server
//        TraversalDescription t = new TraversalDescription();
//        t.setOrder( TraversalDescription.DEPTH_FIRST );
//        t.setUniqueness( TraversalDescription.NODE );
//        t.setMaxDepth( 10 );
//        t.setReturnFilter( TraversalDescription.ALL );
//        t.setRelationships( new Relationship( "singer", Relationship.OUT ) );
//        // END SNIPPET: traversalDesc
//
//        // START SNIPPET: traverse
//        URI traverserUri = new URI( startNode.toString() + "/traverse/node" );
//        WebResource resource = Client.create()
//                .resource( traverserUri );
//        String jsonTraverserPayload = t.toJson();
//        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
//                .type( MediaType.APPLICATION_JSON )
//                .entity( jsonTraverserPayload )
//                .post( ClientResponse.class );
//
//        System.out.println( String.format(
//                "POST [%s] to [%s], status code [%d], returned data: "
//                        + System.getProperty( "line.separator" ) + "%s",
//                jsonTraverserPayload, traverserUri, response.getStatus(),
//                response.getEntity( String.class ) ) );
//        response.close();
//        // END SNIPPET: traverse
//    }

    // START SNIPPET: insideAddMetaToProp
	public static void addMetadataToProperty( URI relationshipUri,
            Map<String,String> property) throws URISyntaxException
    {
        URI propertyUri = new URI( relationshipUri.toString() + "/properties" );
        String entity = toJsonNameValuePairCollection( property );
        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( entity )
                .put( ClientResponse.class );

        System.out.println( String.format(
                "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
                response.getStatus() ) );
        response.close();
    }
	
	

	private static String toJsonNameValuePairCollection(Map<String,String> property){
		
		Set<String> keys=property.keySet();
		Iterator<String> iter=keys.iterator();
		String outPut="{ ";
		while(iter.hasNext()){
			String key=iter.next();
			String value=property.get(key);
			
			outPut+="\""+key+"\" : \""+value+"\"";
			if(iter.hasNext()){
				outPut+=", ";
			}
		}
		outPut+="}";
		
//		return "{ \"timestamp\" : \"20120113\", \"title\" : \"test_title3\"}";
		
		return outPut;

	}

    public static URI createNode()
    {
        // START SNIPPET: createNode
        final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
        // http://localhost:7474/db/data/node

        WebResource resource = Client.create()
                .resource( nodeEntryPointUri );
        // POST {} to the node entry point URI
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( "{}" )
                .post( ClientResponse.class );

        final URI location = response.getLocation();
        System.out.println( String.format(
                "POST to [%s], status code [%d], location header [%s]",
                nodeEntryPointUri, response.getStatus(), location.toString() ) );
        response.close();

        return location;
        // END SNIPPET: createNode
    }
    
    public static void addNodeToIndex( String key, String value, URI nodeUri){
		final String indexNodeUri = SERVER_ROOT_URI + "index/node/test" ;
		WebResource resource = Client.create().resource(indexNodeUri);
		String indexNodeJson = "{\"key\" : \"" + key + "\", \"value\" : \"" + value + "\", \"uri\" : \"" + nodeUri + "\" }";

		ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).entity(indexNodeJson)
				.post(ClientResponse.class);
		
		 System.out.println( String.format(
		 "POST [%s] to [%s], status code [%d], returned data: "
		 + System.getProperty( "line.separator" ) + "%s",
		 indexNodeJson, indexNodeUri, response.getStatus(),
		 response.getEntity( String.class ) ));

		response.close();

		
	}

    // START SNIPPET: insideAddRel
    public static URI addRelationship( URI startNode, URI endNode,
            String relationshipType)
            throws URISyntaxException
    {
        URI fromUri = new URI( startNode.toString() + "/relationships" );
        String relationshipJson = generateJsonRelationship( endNode,
                relationshipType);

        WebResource resource = Client.create()
                .resource( fromUri );
        // POST JSON to the relationships URI
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( relationshipJson )
                .post( ClientResponse.class );

        final URI location = response.getLocation();
        System.out.println( String.format(
                "POST to [%s], status code [%d], location header [%s]",
                fromUri, response.getStatus(), location.toString() ) );

        response.close();
        return location;
    }
    // END SNIPPET: insideAddRel

    public static String generateJsonRelationship( URI endNode,
            String relationshipType, String... jsonAttributes )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "{ \"to\" : \"" );
        sb.append( endNode.toString() );
        sb.append( "\", " );

        sb.append( "\"type\" : \"" );
        sb.append( relationshipType );
        if ( jsonAttributes == null || jsonAttributes.length < 1 )
        {
            sb.append( "\"" );
        }
        else
        {
            sb.append( "\", \"data\" : " );
            for ( int i = 0; i < jsonAttributes.length; i++ )
            {
                sb.append( jsonAttributes[i] );
                if ( i < jsonAttributes.length - 1 )
                { // Miss off the final comma
                    sb.append( ", " );
                }
            }
        }

        sb.append( " }" );
        return sb.toString();
    }

    public static void addProperty( URI nodeUri, String propertyName,
            String propertyValue )
    {
        // START SNIPPET: addProp
        String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
        // http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

        WebResource resource = Client.create()
                .resource( propertyUri );
        ClientResponse response = resource.accept( MediaType.APPLICATION_JSON )
                .type( MediaType.APPLICATION_JSON )
                .entity( "\"" + propertyValue + "\"" )
                .put( ClientResponse.class );

        System.out.println( String.format( "PUT to [%s], status code [%d]",
                propertyUri, response.getStatus() ) );
        response.close();
        // END SNIPPET: addProp
    }

    public static void checkDatabaseIsRunning()
    {
        // START SNIPPET: checkServer
        WebResource resource = Client.create()
                .resource( SERVER_ROOT_URI );
        ClientResponse response = resource.get( ClientResponse.class );

        System.out.println( String.format( "GET on [%s], status code [%d]",
                SERVER_ROOT_URI, response.getStatus() ) );
        response.close();
        // END SNIPPET: checkServer
    }
}


