package com.rivigo.riconet.core.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivigo.riconet.core.dto.ClientContactDTO;
import com.rivigo.riconet.core.service.ApiClientService;
import com.rivigo.riconet.core.service.impl.CMSServiceImpl;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
public class CMSServiceTest {

  @InjectMocks private CMSServiceImpl cmsService;

  @Mock private ApiClientService apiClientService;

  @Spy ObjectMapper objectMapper;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void clientDetailTest() {
    String clientCode = "UNIBC";

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put("code", Collections.singletonList(clientCode));

    String resp =
        "{\"response\":[{\"code\":\"UNIBC\",\"parentCode\":\"P-UNIBC\",\"name\":\"UNIBIC FOODS INDIA PVT LTD.\",\"legalEntityName\":\"UNIBIC FOODS INDIA PVT LTD.\",\"tan\":null,\"rivigoVendorCode\":null,\"remarkIds\":null,\"comment\":null,\"industryType\":\"INDUSTRIAL\",\"addresses\":[{\"id\":390,\"billingLegalEntityName\":null,\"addressLine1\":\"No.50/1,Heggadadevanpura Village,\",\"addressLine2\":\"Huskur Road, Alur Post, Dasanapura Hobli Bangalore\",\"landmark\":\"\",\"city\":\"BANGALORE RURAL\",\"state\":\"IN-KA\",\"stateDisplayName\":null,\"pincode\":\"562123\",\"email\":null,\"pan\":\"AAACU6928L\",\"addressType\":\"BILLING\",\"attention\":null,\"header\":null,\"footer\":null,\"contact\":null,\"gstApplicable\":\"APPLICABLE\",\"gstExemptionType\":null,\"gstPercentage\":18.0,\"gstin\":\"29AAACU6928L1ZQ\",\"primary\":false}],\"contacts\":[{\"id\":910,\"type\":\"CLIENT_FINANCE\",\"level\":1,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Raju P\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":true,\"email\":\"anmol.pradhan@rivigo.com\"},{\"id\":911,\"type\":\"CLIENT_OPERATIONS\",\"level\":1,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"MURUGAN\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":false,\"email\":\"anmol.pradhan@rivigo.com\"},{\"id\":5462,\"type\":\"CLIENT_OPERATIONS\",\"level\":2,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Vineet Singh\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":false,\"email\":\"jayant.aggarwal@rivigo.com\"},{\"id\":8139,\"type\":\"RIVIGO_FINANCE\",\"level\":1,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Rajesh Kumar Yadav\",\"phoneNumber\":\"9999789265\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":13930,\"type\":\"SERVICE_POC\",\"level\":2,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Sreekumar Hari Kumaran  .\",\"phoneNumber\":\"6238323978\",\"isSSoUserCreated\":false,\"email\":\"sreekumark.h@rivigo.com\"},{\"id\":14379,\"type\":\"SERVICE_POC\",\"level\":1,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Shubham Rai .\",\"phoneNumber\":\"7724070541\",\"isSSoUserCreated\":false,\"email\":\"gpp.Reddy@rivigo.com\"},{\"id\":21581,\"type\":\"SERVICE_POC\",\"level\":3,\"serviceType\":\"PRIME\",\"name\":\"Squad1 .\",\"phoneNumber\":\"8861000001\",\"isSSoUserCreated\":false,\"email\":\"squad1_prime@rivigo.com\"},{\"id\":22882,\"type\":\"CLIENT_OPERATIONS\",\"level\":3,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"shilpa\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":48086,\"type\":\"RIVIGO_BUSINESS\",\"level\":5,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Balakrishnan Janardhanan\",\"phoneNumber\":\"9790919293\",\"isSSoUserCreated\":false,\"email\":\"balakrishnan.janardhanan@rivigo.com\"},{\"id\":54227,\"type\":\"RIVIGO_BUSINESS\",\"level\":1,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"pramod .\",\"phoneNumber\":\"8892988530\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":54228,\"type\":\"RIVIGO_BUSINESS\",\"level\":3,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Srikanth Bhushan H .\",\"phoneNumber\":\"9900190339\",\"isSSoUserCreated\":false,\"email\":\"srikanth.bhushan@rivigo.com\"},{\"id\":58107,\"type\":\"RIVIGO_BUSINESS\",\"level\":1,\"serviceType\":\"PRIME\",\"name\":\"Shankar Suman .\",\"phoneNumber\":\"8095506903\",\"isSSoUserCreated\":false,\"email\":\"shankar.suman@rivigo.com\"},{\"id\":58579,\"type\":\"RIVIGO_BUSINESS\",\"level\":2,\"serviceType\":\"PRIME\",\"name\":\"Rishi Porwal .\",\"phoneNumber\":\"9742136665\",\"isSSoUserCreated\":false,\"email\":\"rishi.porwal@rivigo.com\"},{\"id\":59270,\"type\":\"RIVIGO_BUSINESS\",\"level\":3,\"serviceType\":\"PRIME\",\"name\":\"Balakrishnan Janardhanan .\",\"phoneNumber\":\"9790919293\",\"isSSoUserCreated\":false,\"email\":\"balakrishnan.janardhanan@rivigo.com\"},{\"id\":60575,\"type\":\"CLIENT_OPERATIONS\",\"level\":4,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Suresh Reddy\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":false,\"email\":\"anuj.hydrabadi@rivigo.com\"},{\"id\":74926,\"type\":\"RIVIGO_FINANCE\",\"level\":1,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Aashish Kapoor .\",\"phoneNumber\":\"9873938340\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":74927,\"type\":\"RIVIGO_FINANCE\",\"level\":3,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Shashi kant Mishra .\",\"phoneNumber\":\"9555903720\",\"isSSoUserCreated\":false,\"email\":\"Shashikant.Mishra@rivigo.com\"},{\"id\":93784,\"type\":\"RIVIGO_BUSINESS\",\"level\":6,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Srikanth Bhushan H .\",\"phoneNumber\":\"9900190339\",\"isSSoUserCreated\":false,\"email\":\"srikanth.bhushan@rivigo.com\"},{\"id\":97257,\"type\":\"CLIENT_OPERATIONS\",\"level\":5,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Moortuja\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":false,\"email\":\"siripalli.kumar@rivigo.com\"},{\"id\":99745,\"type\":\"RIVIGO_FINANCE\",\"level\":1,\"serviceType\":\"PRIME\",\"name\":\"Saurabh Gupta .\",\"phoneNumber\":\"7417564905\",\"isSSoUserCreated\":false,\"email\":\"saurabh.gupta2@rivigo.com\"},{\"id\":99746,\"type\":\"RIVIGO_FINANCE\",\"level\":2,\"serviceType\":\"PRIME\",\"name\":\"Bindurudra Routray .\",\"phoneNumber\":\"8080243322\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":99747,\"type\":\"RIVIGO_FINANCE\",\"level\":3,\"serviceType\":\"PRIME\",\"name\":\"Rahul Ranjan .\",\"phoneNumber\":\"7019161237\",\"isSSoUserCreated\":false,\"email\":\"rahul.ranjan2@rivigo.com\"},{\"id\":100066,\"type\":\"CLIENT_OPERATIONS\",\"level\":6,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Chetan\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":100648,\"type\":\"RIVIGO_BUSINESS\",\"level\":4,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Siddegowda Devaraju .\",\"phoneNumber\":\"7760377008\",\"isSSoUserCreated\":false,\"email\":\"s.devaraju@rivigo.com\"},{\"id\":103132,\"type\":\"CLIENT_OPERATIONS\",\"level\":7,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Kolkata Hub\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":106711,\"type\":\"CLIENT_FINANCE\",\"level\":2,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Netra\",\"phoneNumber\":\"9620178900\",\"isSSoUserCreated\":true,\"email\":\"anmol.pradhan@rivigo.com\"},{\"id\":106712,\"type\":\"RIVIGO_BUSINESS\",\"level\":2,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Thippeswamy He .\",\"phoneNumber\":\"7090574934\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":106986,\"type\":\"RIVIGO_FINANCE\",\"level\":2,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Bindurudra Routray .\",\"phoneNumber\":\"8080243322\",\"isSSoUserCreated\":false,\"email\":\"\"},{\"id\":107773,\"type\":\"RIVIGO_BUSINESS\",\"level\":10,\"serviceType\":\"ZOOM_CORPORATE\",\"name\":\"Thippeswamy He .\",\"phoneNumber\":\"7090574934\",\"isSSoUserCreated\":false,\"email\":\"\"}],\"clientStatus\":\"ACTIVE\",\"parentName\":null,\"parentLegalName\":null}],\"appErrorCode\":null,\"errorMessage\":null}";

    try {
      JsonNode respJson = objectMapper.readTree(resp);
      Mockito.when(
              apiClientService.getEntity(
                  null, HttpMethod.GET, "/clients/client-details", params, null))
          .thenReturn(respJson);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<ClientContactDTO> clientContacts = cmsService.getClientContacts(clientCode);
    assert clientContacts.size() == 29;
  }
}
