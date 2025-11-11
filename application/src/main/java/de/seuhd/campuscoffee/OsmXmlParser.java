package com.campuscoffee.osm;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;

public class OsmXmlParser {
    public static OsmParsedNode parse(String xml) {
        try {
            var db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            var doc = db.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
            Element node = (Element) doc.getElementsByTagName("node").item(0);

            long id = Long.parseLong(node.getAttribute("id"));
            double lat = Double.parseDouble(node.getAttribute("lat"));
            double lon = Double.parseDouble(node.getAttribute("lon"));

            String name = null, category = null, opening = null, phone = null, website = null;
            String street=null, house=null, city=null, postcode=null;

            var tags = node.getElementsByTagName("tag");
            for (int i=0; i<tags.getLength(); i++) {
                var e = (Element) tags.item(i);
                String k = e.getAttribute("k");
                String v = e.getAttribute("v");
                switch (k) {
                    case "name" -> name = v;
                    case "amenity" -> category = v;
                    case "opening_hours" -> opening = v;
                    case "contact:phone", "phone" -> { if (phone==null) phone = v; }
                    case "contact:website", "website" -> { if (website==null) website = v; }
                    case "addr:street" -> street = v;
                    case "addr:housenumber" -> house = v;
                    case "addr:city" -> city = v;
                    case "addr:postcode" -> postcode = v;
                }
            }
            return new OsmParsedNode(id, lat, lon, name, category, opening, phone, website, street, house, postcode, city);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OSM XML", e);
        }
    }
}
