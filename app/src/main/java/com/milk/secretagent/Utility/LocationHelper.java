package com.milk.secretagent.Utility;


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.milk.secretagent.Model.LocationTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Milk on 2015/8/7.
 */
public class LocationHelper {
    private static LocationHelper ourInstance = new LocationHelper();
    private ArrayList<ArrayList<Location>> m_LocationList = new ArrayList<ArrayList<Location>>();

    public static LocationHelper getInstance() {
        return ourInstance;
    }

    private LocationHelper() {
    }

    public void clearLocationList() {
        for (ArrayList<Location> locationList : this.m_LocationList) {
            locationList.clear();
        }

        this.m_LocationList.clear();
    }

    public void addLocationList(ArrayList<Location> locationList) {
        m_LocationList.add(locationList);
    }

    public ArrayList<Location> getLocationListByIndex(int index) {
        if (0 > index || this.m_LocationList.size() <= index) {
            return null;
        }

        return this.m_LocationList.get(index);
    }

    public ArrayList<LatLng> getLocationPointsByIndex(int index) {
        ArrayList<LatLng> locationPoints = new ArrayList<>();
        ArrayList<Location> locationList = this.m_LocationList.get(index);

        for (Location location : locationList) {
            locationPoints.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }

        return locationPoints;
    }

    public static void saveLocationInfo(LocationTask locationTask, ArrayList<Location> locationList) {

        File locationFile = new File(locationTask.getFilePath());

        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(locationFile));

            printWriter.println("Timestamp Latitude Longitude Altitude Speed Accuracy");
            for (Location location : locationList) {
                String line = String.format("%d %f %f %f %f %f",
                        location.getTime(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        location.getSpeed(),
                        location.getAccuracy());

                printWriter.println(line);
            }

            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getLocationInfoList(LocationTask locationTask, ArrayList<Location> locationList) {

        try {
            File file = new File(locationTask.getFilePath());

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine(); // First line is all field descriptions
            while ((line = bufferedReader.readLine()) != null) {
                Location location = new Location(line);

                String[] tokens = line.split(" ");
                location.setTime(Long.valueOf(tokens[0]));
                location.setLatitude(Double.valueOf(tokens[1]));
                location.setLongitude(Double.valueOf(tokens[2]));
                location.setAltitude(Double.valueOf(tokens[3]));
                location.setSpeed(Float.valueOf(tokens[4]));
                location.setAccuracy(Float.valueOf(tokens[5]));

                locationList.add(location);
            }

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float getTotalDistance(int index) {
        ArrayList<Location> locationList = this.m_LocationList.get(index);
        float totalDistance = 0;

        for (int i = 0; i < locationList.size() - 1; i++) {
            int next = i + 1;
            totalDistance += locationList.get(i).distanceTo(locationList.get(next));
        }

        return totalDistance;
    }

    public int getTotalMoveTime(int index) {
        ArrayList<Location> locationList = this.m_LocationList.get(index);

        long startTimestamp = 0;
        long finishTimestamp = 0;
        int timeDiffInMilliseconds = 0;

        for (int speedIndex = 0; speedIndex < locationList.size() - 1;) {
            Location locationCurrent = locationList.get(speedIndex);

            if (locationCurrent.getSpeed() > 0f) {
                startTimestamp = locationCurrent.getTime();
                speedIndex++;

                for (; speedIndex < locationList.size(); speedIndex++) {

                    if (locationList.get(speedIndex).getSpeed() > 0f) {
                        finishTimestamp = locationList.get(speedIndex).getTime();
                        timeDiffInMilliseconds += (finishTimestamp - startTimestamp);
                        startTimestamp = finishTimestamp;
                    }
                    else {
                        break;
                    }
                }
            }
            else {
                speedIndex++;
            }
        }

        return timeDiffInMilliseconds / 1000;
    }

    public float getAverageSpeed(int index) {
        // TODO: think better way to calculate average speed
        ArrayList<Location> locationList = this.m_LocationList.get(index);
        float totalSpeed = 0;
        int count = 0;

        // Rule: remove all locations which has no speed value
        for (Location location : locationList) {
            if (location.hasSpeed()) {
                totalSpeed += location.getSpeed();
                count++;
            }
        }

        return totalSpeed / count;
    }

    public float getAverageAltitude(int index) {
        ArrayList<Location> locationList = this.m_LocationList.get(index);
        float totalAltitude = 0;
        int count = 0;

        // Rule: remove all locations which has no speed value
        for (Location location : locationList) {
            if (location.hasAltitude()) {
                totalAltitude += location.getAltitude();
                count++;
            }
        }

        return totalAltitude / count;
    }

    public float getAverageAccuracy(int index) {
        ArrayList<Location> locationList = this.m_LocationList.get(index);
        float totalAccuracy = 0;
        int count = 0;

        // Rule: remove all locations which has no speed value
        for (Location location : locationList) {
            if (location.hasAccuracy()) {
                totalAccuracy += location.getAccuracy();
                count++;
            }
        }

        return totalAccuracy / count;
    }

    private double getAltitude(Double longitude, Double latitude) {
        double result = Double.NaN;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        String url = "http://gisdata.usgs.gov/"
                + "xmlwebservices2/elevation_service.asmx/"
                + "getElevation?X_Value=" + String.valueOf(longitude)
                + "&Y_Value=" + String.valueOf(latitude)
                + "&Elevation_Units=METERS&Source_Layer=-1&Elevation_Only=true";
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                int r = -1;
                StringBuffer respStr = new StringBuffer();
                while ((r = instream.read()) != -1)
                    respStr.append((char) r);
                String tagOpen = "<double>";
                String tagClose = "</double>";
                if (respStr.indexOf(tagOpen) != -1) {
                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
                    int end = respStr.indexOf(tagClose);
                    String value = respStr.substring(start, end);
                    result = Double.parseDouble(value);
                }
                instream.close();
            }
        } catch (ClientProtocolException e) {}
        catch (IOException e) {}
        return result;
    }
}
