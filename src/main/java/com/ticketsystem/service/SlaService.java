package com.ticketsystem.service;

import com.ticketsystem.entity.*;
import com.ticketsystem.enums.SlaStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SlaService {

    /**
     * Calculate working hours between two timestamps.
     * Excludes: weekends (Saturday, Sunday)
     * Only counts hours within shift time (e.g. 9AM - 6PM)
     * 
     * Example:
     * Ticket raised: Friday 5PM
     * Resolved: Monday 11AM
     * Shift: 9AM - 6PM
     * 
     * Friday: 5PM to 6PM = 1 hour
     * Saturday: excluded
     * Sunday: excluded
     * Monday: 9AM to 11AM = 2 hours
     * Total = 3 hours
     */
    public double calculateWorkingHours(
            LocalDateTime start,
            LocalDateTime end,
            Shift shift) {

        if (start == null || end == null) return 0.0;
        if (end.isBefore(start)) return 0.0;

        LocalTime shiftStart = shift != null ? shift.getStartTime() : LocalTime.of(9, 0);
        LocalTime shiftEnd = shift != null ? shift.getEndTime() : LocalTime.of(18, 0);

        double totalMinutes = 0;
        LocalDateTime current = start;

        while (current.isBefore(end)) {
            // Skip weekends
            DayOfWeek day = current.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                current = current.plusDays(1).toLocalDate().atTime(shiftStart);
                continue;
            }

            // Get working window for this day
            LocalDateTime dayShiftStart = current.toLocalDate().atTime(shiftStart);
            LocalDateTime dayShiftEnd = current.toLocalDate().atTime(shiftEnd);

            // Effective start for this day
            LocalDateTime effectiveStart = current.isBefore(dayShiftStart)
                ? dayShiftStart : current;

            // Effective end for this day
            LocalDateTime effectiveEnd = end.isBefore(dayShiftEnd)
                ? end : dayShiftEnd;

            if (effectiveStart.isBefore(effectiveEnd)) {
                totalMinutes += ChronoUnit.MINUTES.between(effectiveStart, effectiveEnd);
            }

            // Move to next day
            current = current.toLocalDate().plusDays(1).atTime(shiftStart);
        }

        return Math.round(totalMinutes / 60.0 * 100.0) / 100.0;
    }

    /**
     * Calculate SLA breach time based on project SLA hours
     * and working hours from ticket creation
     */
    public LocalDateTime calculateSlaBreachTime(
            LocalDateTime createdAt,
            int slaHours,
            Shift shift) {

        LocalTime shiftStart = shift != null ? shift.getStartTime() : LocalTime.of(9, 0);
        LocalTime shiftEnd = shift != null ? shift.getEndTime() : LocalTime.of(18, 0);
        int shiftMinutesPerDay = (int) ChronoUnit.MINUTES.between(shiftStart, shiftEnd);

        int remainingMinutes = slaHours * 60;
        LocalDateTime current = createdAt;

        while (remainingMinutes > 0) {
            DayOfWeek day = current.getDayOfWeek();

            // Skip weekends
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                current = current.toLocalDate().plusDays(1).atTime(shiftStart);
                continue;
            }

            LocalDateTime dayShiftEnd = current.toLocalDate().atTime(shiftEnd);

            if (current.isBefore(current.toLocalDate().atTime(shiftStart))) {
                current = current.toLocalDate().atTime(shiftStart);
            }

            int minutesLeftToday = (int) ChronoUnit.MINUTES.between(current, dayShiftEnd);
            if (minutesLeftToday <= 0) {
                current = current.toLocalDate().plusDays(1).atTime(shiftStart);
                continue;
            }

            if (remainingMinutes <= minutesLeftToday) {
                return current.plusMinutes(remainingMinutes);
            }

            remainingMinutes -= minutesLeftToday;
            current = current.toLocalDate().plusDays(1).atTime(shiftStart);
        }

        return current;
    }

    /**
     * Determine SLA status for a ticket
     */
    public SlaStatus determineSlaStatus(
            LocalDateTime slaBreachTime,
            TicketStatus status) {

        if (slaBreachTime == null) return SlaStatus.WITHIN_SLA;

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(slaBreachTime)) {
            return SlaStatus.SLA_BREACHED;
        }

        // Warning if less than 2 hours remaining
        if (ChronoUnit.HOURS.between(now, slaBreachTime) < 2) {
            return SlaStatus.SLA_WARNING;
        }

        return SlaStatus.WITHIN_SLA;
    }
}