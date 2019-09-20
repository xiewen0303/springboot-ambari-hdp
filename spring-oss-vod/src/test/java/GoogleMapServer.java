import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.junit.Test;

import java.io.IOException;

public class GoogleMapServer {

    @Test
    public void testGoogleMap() throws Exception {
        GeoApiContext context = new GeoApiContext.Builder()
                . apiKey("AIza...")
                . build();
        GeocodingResult[] results = GeocodingApi.geocode(context,"1600 Amphitheatre Parkway Mountain View, CA 94043").await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(results[0].addressComponents));
    }

    @Test
    public void reverseGeocode() throws Exception {

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAr1Inx9JJPg7e6XW5Ym5HJDpmD5JdHsqA")
                .build();
        double lat = 36.254d;
        double lng = 111.36d;
        GeocodingResult[] results = new GeocodingResult[0];
        try {
            results = GeocodingApi.reverseGeocode(context,new LatLng(lat,lng)).await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(results[0].addressComponents));
    }
}
