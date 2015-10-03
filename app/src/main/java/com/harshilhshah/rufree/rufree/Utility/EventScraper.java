package com.harshilhshah.rufree.rufree.Utility;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import com.facebook.GraphRequest;
import com.facebook.AccessToken;
import com.facebook.HttpMethod;
import com.facebook.GraphResponse;
import com.harshilhshah.rufree.rufree.Model.Event;
import com.harshilhshah.rufree.rufree.Model.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants;

/**
 * Created by harshilhshah on 9/27/15.
 *
 * CREDIT: Sam Agnew & shreyashirday
 */
public class EventScraper {

    private final static int NUM_DAYS = 10;

    private final static String ru_events_url = "http://ruevents.rutgers.edu/events/getEventsRss.xml";
    private final static String student_life_url = "https://rutgers.collegiatelink.net/EventRss/EventsRss";
    private final static  String[] fb_urls = {
                                            "/RUPAPresents/events",
                                            "/RUOCSA/events",
                                            "/ruaacc/events",
                                            "/youth.eagleton/events",
                                            "/Cookiesncrepesnb/events",
                                            "/rutgersmad/events"
                                            };



    /** Creates a list of event objects from the results of the XML downloads.
     *
     * @return events
     *
     * @throws java.io.IOException
     */
    public static ArrayList<Event> getEvents(boolean getFBEvents) throws IOException {

        ArrayList<Event> events = new ArrayList<>();

        String ru_events_xml = getXML(ru_events_url );
        String student_life_xml = getXML(student_life_url);

        try {

            events = getRutgersEventsViaJSON(ru_events_xml);
            events.addAll(getStudentLifeEventsViaJSON(student_life_xml));
            if (getFBEvents) events.addAll(getFacebookEvents());

        } catch (ParseException e) {
            Log.e("EventScraper: PE", e.getMessage());

        } catch (ParserConfigurationException e) {
            Log.e("EventScraper: PCE", e.getMessage());

        } catch (SAXException e) {
            Log.e("EventScraper: SAXExcep", e.getMessage());

        } catch (IOException e) {
            Log.e("EventScraper: IOExcept", e.getMessage());
            throw e;
        } catch (XPathExpressionException | JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(events,new DateComparator());

        return events;
    }


    public static ArrayList<Event> getRutgersEventsViaJSON(String xml) throws ParseException, ParserConfigurationException, SAXException, IOException, JSONException {

        String title, description, start_time, end_time;
        GregorianCalendar startGC, endGC;
        startGC = endGC = (GregorianCalendar) GregorianCalendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEE");

        JSONArray array = XML.toJSONObject(xml).getJSONObject("rss").getJSONObject("channel").getJSONArray("item");

        ArrayList<Event> events = new ArrayList<>(50);

                for(int i = 0; i < array.length(); i++){

                        JSONObject event = array.getJSONObject(i);

                        title = event.getString("title");
                        description = event.getString("description");

                        if (hasFreeFood(description)) {

                            start_time = event.getString("event:beginDateTime");
                            end_time = event.getString("event:endDateTime");

                            startGC.setTime(formatter.parse(start_time));
                            endGC.setTime(formatter.parse(end_time));

                            Location loc = new Location(event);

                            events.add(new Event(title,description,startGC,endGC,loc));
                        }
                }


        return events;
    }


    public static ArrayList<Event> getStudentLifeEventsViaJSON(String xml) throws ParseException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        ArrayList<Event> events = new ArrayList<>(50);

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        Document doc = db.parse(is);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr;
        NodeList nl;
            expr = xpath.compile("/rss/channel/item");
            nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for(int i = 0; i < nl.getLength(); i++) {
                Element elem = (Element) nl.item(i);
                String title = elem.getElementsByTagName("title").item(0).getFirstChild().getTextContent();
                String description = elem.getElementsByTagName("description").item(0).getFirstChild().getTextContent();
                description = "<pre>" + description + "</pre>";
                GregorianCalendar startGC, endGC;
                Date startParsed, endParsed;
                DateFormat formatter;
                if(hasFreeFood(description)) {
                        is.setCharacterStream(new StringReader(description));
                        doc = db.parse(is);
                        String time = doc.getElementsByTagName("pre").item(0).getFirstChild().getTextContent();
                        if (time.trim().contains(") -")) {
                            formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy (hh:mm a)");
                            startParsed = formatter.parse(time.split("-")[0].trim());
                            endParsed = formatter.parse(time.split("-")[1].trim());
                        } else {
                            formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy (hh:mm a");
                            startParsed = formatter.parse(time.split("-")[0].trim());
                            formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy hh:mm a)");
                            endParsed = formatter.parse(time.split("\\(")[0] + time.split("-")[1].trim());
                        }

                        startGC = endGC = (GregorianCalendar) GregorianCalendar.getInstance();
                        startGC.setTime(startParsed);
                        endGC.setTime(endParsed);

                        description = description.replaceAll("<b>.*</b>","");
                        description = description.replaceAll("<[\\w|\\s|/]*>","");
                        description = description.replaceAll("<[span|div].*>","");
                        description = description.replace("Location:","");
                        description = description.replace("&nbsp;", " ");

                        if(elem.getElementsByTagName("enclosure").getLength() == 1){
                            String url = elem.getElementsByTagName("enclosure").item(0).getAttributes().getNamedItem("url").getNodeValue();
                            events.add(new Event(title,description,startGC, endGC, null,url));
                        }else {
                            events.add(new Event(title, description, startGC, endGC, null));
                        }
                }
            }

        return events;
    }


    public static ArrayList<Event> getFacebookEvents(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String today = dateFormat.format(cal.getTime());

        final ArrayList<Event> events = new ArrayList<>();

        final Bundle param = new Bundle();
        param.putString("since", today);

        final AccessToken token = AccessToken.getCurrentAccessToken();

        final GraphRequest.Callback graphCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {

                String title, description;
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");

                try {

                    JSONArray event_data = response.getJSONObject().getJSONArray("data");

                    for(int i = 0; i < event_data.length(); i++) {

                        try {

                            JSONObject event_item = event_data.getJSONObject(i);

                            try {
                                title = event_item.getString("name");
                            } catch (JSONException je) {
                                title = "No Title";
                            }

                            description = event_item.getString("description");

                            if (hasFreeFood(description)) {

                                String start_time, end_time;
                                GregorianCalendar startGC, endGC;
                                startGC = endGC = (GregorianCalendar) GregorianCalendar.getInstance();

                                start_time = event_item.getString("start_time");

                                try {
                                    end_time = event_item.getString("end_time");
                                } catch (JSONException ignored) {
                                    end_time = start_time;
                                }

                                try {
                                    startGC.setTime(formatter.parse(start_time));
                                    endGC.setTime(formatter.parse(end_time));
                                } catch (ParseException ignored) {
                                }

                                JSONObject place = event_item.getJSONObject("place");

                                Location loc = new Location();
                                loc.setCity(place.getJSONObject("location").getString("city"));
                                loc.setState(place.getJSONObject("location").getString("state"));
                                loc.setBuilding(place.getString("name"));
                                loc.createAddress();
                                loc.createCampus();

                                events.add(new Event(title, description, startGC, endGC, loc));

                            }

                        } catch (JSONException ignored) {}

                    }
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        };

        for(String url: fb_urls)
            new GraphRequest(token,url,param,HttpMethod.GET,graphCallback).executeAndWait();

        return events;
    }


    /** Retrieves XML from specified URL to be parsed.
     *
     * @param urlstr: url string
     * @return String
     */
    public static String getXML(String urlstr){

        StringBuilder buff = new StringBuilder();

        URL url = null;
        try {
            url = new URL(urlstr);
        } catch (MalformedURLException e) {
            Log.e("XML Error", "Error using specified URL.");
        }
        BufferedReader br = null;
        try {
            assert url != null;
            br = new BufferedReader(
                    new InputStreamReader(url.openConnection().getInputStream()));
        } catch (IOException e) {
            Log.e("XML Error,", "Error Reading I/O in XML Parse");
        } catch (NullPointerException e){
            Log.e("Url doesn't exit","Error url not found");
        }

        int c;

        try {
            assert br != null;
            while((c=br.read())!=-1) {
                buff.append((char)c);
            }
            br.close();
        } catch(IOException e){
            e.printStackTrace();
        }

        return buff.toString();
    }


    /** This function takes the event description string and looks for
     *  the specified parameters.
     * @param description : string to be vetted
     * @return boolean with result of event vetting process
     */
    public static boolean hasFreeFood(String description){

        description = description.toLowerCase();

        String[]  keywords = {"food", "appetizer", "snack", "pizza", "lunch", "dinner", "breakfast", "meal",
                "candy", "drink", "punch", "pie", "cake", "soda", "chicken", "wing", "burger",
                "burrito", "shirt", "stuff", "bagel", "coffee", " ice ", "cream", "water", "donut", "beer",
                "sub", "hoagie", "sandwich", "turkey", "supper", "brunch", "takeout", "refresh",
                "beverage", "cookie", "brownie", "corn", "chips", "soup", "grill", "bbq", "barbecue"};

        if(description.toLowerCase().contains("free")){
            for(String word: keywords){
                if(description.toLowerCase().contains(word)){
                    return true;
                }
            }
        }
        return false;
    }


    private static class DateComparator implements Comparator<Event> {

        @Override
        public int compare(Event e1, Event e2) {
            if(e1 == null)
                return 1;
            if(e2 == null)
                return -1;

            if (e1.getStartDate().getTimeInMillis() > e2.getStartDate().getTimeInMillis())
                return 1;
            else
                return -1;
        }

    }

}
