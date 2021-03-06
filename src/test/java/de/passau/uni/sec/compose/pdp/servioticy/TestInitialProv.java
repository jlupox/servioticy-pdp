package de.passau.uni.sec.compose.pdp.servioticy;

import static org.junit.Assert.*;
import org.junit.rules.ExpectedException.*;

import java.io.IOException;
import java.util.UUID;
import java.util.List;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.passau.uni.sec.compose.pdp.servioticy.provenance.ServioticyProvenance;
import de.passau.uni.sec.compose.pdp.servioticy.exception.PDPServioticyException;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;

public class TestInitialProv 
{
	 private PDP pdp; 
	
	 @Before
	 public void setUp()
	 {
		 pdp = new LocalPDP();
	 }
	
	 @Test
	 public  void initialProvenanceOK() throws PDPServioticyException
	 {
		  	PermissionCacheObject ret;
			try {
				// Generate input
				String token=UUID.randomUUID().toString();
				JsonNode so_data = buildJsonSoMetadata(token);
				// Get initial provenance
				ret = pdp.checkAuthorization(token, so_data, null, null, PDP.operationID.SendDataToServiceObjectProv);
				//ret = ServioticyProvenance.getInitialProvenance(so_data);
				// Check initial provenance (if it is a valide JSON doc and if it has the right provenance onbehalf entry)
				String onbehalfString = "";
				List<Object>  tempList = new LinkedList<Object>();
				tempList = JsonPath.read(ret.getCache().toString(), ".provenance.onbehalf");
				for (Object tempObject : tempList)
				{
					if (tempObject != null)
					{
						onbehalfString += tempObject.toString();
					}
				}	
				assertEquals("owner_identifier123123", onbehalfString);



				//ret = pdp.checkAuthorization(token, so_data, null, null, PDP.operationID.SendDataToServiceObject);
			} catch (PDPServioticyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail();
			} catch (IOException e) {
				fail();
			}
  
			
	 }
	 /**
	  * 
	  * @param token
	  * @return A subset of 
	  * @throws JsonProcessingException
	  * @throws IOException
	  */
	 private JsonNode buildJsonSoMetadata(String token) throws JsonProcessingException, IOException {
		     String string = "{\"id\":\"13412341234123412341324\", \"api_token\": \""+token+"\", \"owner_id\":\"owner_identifier123123\"}";
		    ObjectMapper mapper = new ObjectMapper();
		    JsonNode so_data;
			so_data = mapper.readTree(string);
			return so_data;
	}


	 @Test(expected = PDPServioticyException.class)
	 public  void initialProvenanceFailerNull() throws PDPServioticyException
	 {
			String token=UUID.randomUUID().toString();
			pdp.checkAuthorization(token, null, null, null, PDP.operationID.SendDataToServiceObjectProv);
			assertEquals(1, 2);
	 }

	 @Test(expected = PDPServioticyException.class)
	 public  void initialProvenanceFailerJSON() throws JsonProcessingException, IOException, PDPServioticyException 
	 {
			String token=UUID.randomUUID().toString();
		    	ObjectMapper mapper = new ObjectMapper();
			JsonNode so_data = mapper.readTree("{\"bla\" : \"blub\"}");
			PermissionCacheObject ret = pdp.checkAuthorization(token, so_data, null, null, PDP.operationID.SendDataToServiceObjectProv);
	
	 }

}
