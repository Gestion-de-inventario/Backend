package com.comedor.backend.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Donation {
    Integer id;
    LocalDate donationDate;
    private List<DonationDetail> details = new ArrayList<>();
}
