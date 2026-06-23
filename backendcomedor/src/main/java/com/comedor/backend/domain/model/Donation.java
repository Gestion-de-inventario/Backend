package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.EstadoOrden;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Donation {
    Integer id;

    LocalDate donationDate;

    EstadoOrden status;

    private List<DonationDetail> details = new ArrayList<>();

    public Donation() {
    }

    public Integer getId() {
        return id;
    }

    public EstadoOrden getStatus() {
        return status;
    }

    public void setStatus(EstadoOrden status) {
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public List<DonationDetail> getDetails() {
        return details;
    }

    public void setDetails(List<DonationDetail> details) {
        this.details = details;
    }
}
