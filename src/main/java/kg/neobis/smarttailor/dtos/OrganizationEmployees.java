package kg.neobis.smarttailor.dtos;

import java.io.Serializable;
import java.util.List;

public record OrganizationEmployees(
        List<EmployeeCard> five,
        List<EmployeeCard> four,
        List<EmployeeCard> three,
        List<EmployeeCard> two,
        List<EmployeeCard> one
) implements Serializable {}