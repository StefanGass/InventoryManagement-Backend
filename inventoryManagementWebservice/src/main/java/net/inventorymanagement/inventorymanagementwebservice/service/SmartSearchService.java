package net.inventorymanagement.inventorymanagementwebservice.service;

import java.util.*;
import javax.persistence.*;
import javax.transaction.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.inventorymanagement.inventorymanagementwebservice.model.*;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.*;
import org.hibernate.search.query.dsl.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SmartSearchService {

    private final EntityManager em;

    public void reindex() throws InterruptedException {
        log.info("Initiating indexing...");
        FullTextEntityManager fullTextEntityManager =
            Search.getFullTextEntityManager(em);
        fullTextEntityManager.createIndexer().startAndWait();
        log.info("All entities indexed");
    }

    public List<InventoryItem> getPostBasedOnWord(Integer departmentId, String search) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(
            InventoryItem.class).get();
        var q = qb.bool().must(
            qb.keyword().wildcard().onFields(
                    "serialNumber",
                    "itemInternalNumber",
                    "itemName",
                    "issuedTo",
                    "droppingReason",
                    "comments",
                    "status",
                    "oldItemNumber",
                    "type.typeName",
                    "type.category.categoryName",
                    "location.locationName",
                    "supplier.supplierName")
                .matching(search.toLowerCase())// Index is lower case
                .createQuery());

        if (departmentId != null) {
            q.must(
                qb.keyword().onField("department.departmentId").matching(departmentId).createQuery()
            );
        }

        Query luceneQuery = q.createQuery();

        javax.persistence.Query jpaQuery =
            fullTextEntityManager.createFullTextQuery(luceneQuery, InventoryItem.class);

        // execute search

        List<InventoryItem> items = null;
        try {
            items = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            ;// do nothing
            items = new ArrayList<>();
        }

        return items;
    }

}