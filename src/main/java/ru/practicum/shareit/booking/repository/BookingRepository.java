package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
/**
 * @author Min Danil 12.10.2023
 */
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByItemIdOrderByIdDesc(Integer itemId);

    List<Booking> findByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(Integer bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerId(Integer bookerId, Sort sort);

    List<Booking> findBookingByItemOwnerAndStatus(Integer bookerId, BookingStatus status, Sort sort);

    List<Booking> findBookingByItemOwnerAndEndIsBefore(Integer bookerId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemOwnerAndStartIsAfter(Integer bookerId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemOwner(Integer bookerId, Sort sort);

    List<Booking> findBookingByItemIdAndStartBefore(Integer itemId, LocalDateTime end, Sort sort);

    List<Booking> findBookingByItemIdAndStartAfter(Integer itemId, LocalDateTime start, Sort sort);

    @Query("select b from bookings b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc ")
    List<Booking> findByBookerIdCurrent(Integer userId, LocalDateTime now);

    @Query("select b from bookings b " +
            "where b.item.owner = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start asc")
    List<Booking> findBookingsByItemOwnerCurrent(Integer userId, LocalDateTime now);

    @Query("select b from bookings b " +
            " where b.item.id = ?1 " +
            " and b.booker.id = ?2" +
            " and b.end < ?3")
    List<Booking> findBookingsForAddComments(Integer itemId, Integer userId, LocalDateTime now);

}