package com.km.docmacc.intraoralbill.clients;

//import com.km.docmacc.treatmentplan.clients.dto.IntraoralExaminationResponse;
//import com.km.docmacc.treatmentplan.clients.dto.ToothNumbersDentalChartDesign;
//import com.km.docmacc.treatmentplan.model.HttpResponse;
import com.km.docmacc.intraoralbill.clients.dto.IntraoralExaminationResponse;
import com.km.docmacc.intraoralbill.model.dto.HttpResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@CircuitBreaker(name = "externalIntraoralExamination", fallbackMethod = "fallback")
@FeignClient(url="${item.request.url.intraoralExamination}", name="intraoralExamination")
public interface IntraOralExaminationClient {

    @GetMapping(value="${item.request.api.intraoralExaminationGetList}")
    List<IntraoralExaminationResponse> getIntraoralExaminationList(@PathVariable Long profileId);

    default HttpResponse fallback(Integer id, Exception e){
        /*throw new DentalChartDesignNotFoundException(e.getMessage());*/
        /*throw new CustomException(404, HttpStatus.NOT_FOUND, "Not Fund Dental Chart", "Intraoral Examination is not available.");*/
        return new HttpResponse(404, HttpStatus.NOT_FOUND, "Not Fund Dental Chart", "Intraoral Examination is not available.");
    }

    default List<IntraoralExaminationResponse> fallback(Long id, Exception e){
        return new ArrayList<IntraoralExaminationResponse>();
    }
}
