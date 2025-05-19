package vn.edu.iuh.sophiahotelanalyticsservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.iuh.sophiahotelanalyticsservice.dtos.responses.ReportResponse;
import vn.edu.iuh.sophiahotelanalyticsservice.models.Report;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    @Mapping(source = "reportType", target = "reportType")
    ReportResponse toDto(Report report);
}
