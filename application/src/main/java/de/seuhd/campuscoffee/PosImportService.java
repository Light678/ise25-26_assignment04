package com.campuscoffee.pos;

import com.campuscoffee.osm.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PosImportService {
    private final OsmClient osmClient;
    private final PointOfSaleRepository repo;

    public PosImportService(OsmClient osmClient, PointOfSaleRepository repo) {
        this.osmClient = osmClient; this.repo = repo;
    }

    @Transactional
    public PointOfSaleDto importFromOsm(long nodeId) {
        String xml;
        try { xml = osmClient.fetchNodeXml(nodeId); }
        catch (OsmNotFoundException e) { throw new PosNotFoundException("OSM node 404: " + nodeId); }
        catch (Exception e) { throw new PosUpstreamException("OSM upstream error", e); }

        var p = OsmXmlParser.parse(xml);
        if (p.name() == null || p.name().isBlank())
            throw new PosUnprocessableException("OSM node lacks 'name'");

        var pos = repo.findByOsmNodeId(p.id()).orElseGet(PointOfSale::new);
        pos.setOsmNodeId(p.id());
        pos.setName(p.name());
        pos.setCategory(p.category() != null ? p.category() : "unknown");
        pos.setLatitude(p.lat());
        pos.setLongitude(p.lon());
        pos.setStreet(p.street());
        pos.setHouseNumber(p.houseNumber());
        pos.setPostcode(p.postcode());
        pos.setCity(p.city());
        pos.setOpeningHours(p.openingHours());
        pos.setPhone(p.phone());
        pos.setWebsite(p.website());

        repo.save(pos);
        return new PointOfSaleDto(
            pos.getOsmNodeId(), pos.getName(), pos.getCategory(),
            pos.getLatitude(), pos.getLongitude(),
            pos.getStreet(), pos.getHouseNumber(), pos.getPostcode(), pos.getCity(),
            pos.getOpeningHours(), pos.getPhone(), pos.getWebsite());
    }
}
