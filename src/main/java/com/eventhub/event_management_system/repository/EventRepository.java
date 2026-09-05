package com.eventhub.event_management_system.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventhub.event_management_system.entity.Event;
import com.eventhub.event_management_system.entity.EventStatus;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);

    List<Event> findByCityIgnoreCase(String city);

    List<Event> findByCategoryIgnoreCase(String category);

    List<Event> findByEventDate(LocalDate eventDate);

    List<Event> findByTitleContainingIgnoreCase(String title);

    List<Event> findByOrganizerId(Long organizerId);
    
    long countByOrganizerId(Long organizerId);

    @Query(
        value = """
            SELECT e FROM Event e
            WHERE (:category IS NULL OR :category = '' OR LOWER(e.category) = LOWER(:category))
            AND (:city IS NULL OR :city = '' OR LOWER(e.city) = LOWER(:city))
            AND (:search IS NULL OR :search = '' OR
                 LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(e.venue) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(e.city) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(e.category) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
        countQuery = """
            SELECT COUNT(e) FROM Event e
            WHERE (:category IS NULL OR :category = '' OR LOWER(e.category) = LOWER(:category))
            AND (:city IS NULL OR :city = '' OR LOWER(e.city) = LOWER(:city))
            AND (:search IS NULL OR :search = '' OR
                 LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(e.venue) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(e.city) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(e.category) LIKE LOWER(CONCAT('%', :search, '%')))
            """
    )
    Page<Event> searchAndFilterEvents(
            @Param("search") String search,
            @Param("category") String category,
            @Param("city") String city,
            Pageable pageable
    );

    @Query("SELECT DISTINCT e.category FROM Event e WHERE e.category IS NOT NULL AND TRIM(e.category) != '' ORDER BY e.category ASC")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT e.city FROM Event e WHERE e.city IS NOT NULL AND TRIM(e.city) != '' ORDER BY e.city ASC")
    List<String> findDistinctCities();
}

