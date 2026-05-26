package com.ticketsystem.enums;

public enum SlaStatus {
    WITHIN_SLA,
    SLA_BREACHED,
    SLA_WARNING   // less than 2 hours remaining
}