package rest_resource;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
//@RequestMapping("/")
public class ResourceIndex {

    public ResourceIndex() {

    }

    @GetMapping("")
    public ResponseEntity<byte[]> indexs(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String fullUrl = request.getRequestURI();
        String fullUri = request.getRequestURI().substring(1);
        String baseUrl = "";
        String hostPost = String.valueOf(request.getServerPort());
        String redirectUrl = "";
        if (!fullUri.equals("")) {
            baseUrl = request.getRequestURL().toString().replace(fullUrl, "");
        } else {
            fullUrl = request.getRequestURL().toString();
            baseUrl = fullUrl.substring(0, fullUrl.length() - 1);
        }
        //System.out.println("fullUrl : " + fullUrl);
        //System.out.println("fullUri : " + fullUri);
        //System.out.println("baseUrl : " + baseUrl);
        try {
            //for redirect to https
//            if (baseUrl.toLowerCase().contains("http://")) {
//                //this line should code for check url contain port or not
//                baseUrl = baseUrl.contains(":8090") ? baseUrl : baseUrl + ":8090";
//                hostPost = "8090";
//                redirectUrl = baseUrl.replace(hostPost, "20001");
//                redirectUrl = redirectUrl.replace("http://", "https://");
//                URI landingPage = new URI(redirectUrl + "/SpreadSheet/ControllerSpreadSheetDashboard/getDashboard/index?level_id=1");
//                httpHeaders.setLocation(landingPage);
//            } else if (baseUrl.toLowerCase().contains("https://")) {
//                //this line should code for check url contain port or not
//                redirectUrl = baseUrl.replace(hostPost, "20001");
//                URI landingPage = new URI(redirectUrl + "/SpreadSheet/ControllerSpreadSheetDashboard/getDashboard/index?level_id=1");
//                httpHeaders.setLocation(landingPage);
//            } else {
//                URI landingPage = new URI(baseUrl + "/SpreadSheet/ControllerSpreadSheetDashboard/getDashboard/index?level_id=1");
//                httpHeaders.setLocation(landingPage);
//            }
            URI landingPage = new URI(baseUrl + "/makrotik/ControllerSpreadSheetDashboard/getDashboard/index?level_id=1");
            httpHeaders.setLocation(landingPage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }
}
