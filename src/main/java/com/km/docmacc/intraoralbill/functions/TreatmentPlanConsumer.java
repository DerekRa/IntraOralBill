package com.km.docmacc.intraoralbill.functions;

import com.km.docmacc.intraoralbill.model.dto.CommunicationBillSwitchStatus;
import com.km.docmacc.intraoralbill.service.BillService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.logging.Logger;

@Configuration
public class TreatmentPlanConsumer {
    private static final Logger logger = Logger.getLogger(TreatmentPlanConsumer.class.getName());
    @Bean
    public Consumer<CommunicationBillSwitchStatus> intraOralTreatmentPlanConsumerFunction(BillService billService) {
        return message -> {
            // Process the incoming message
            logger.info("Received Intraoral Examination Data: " + message);
            // Here you can add logic to handle the intraoral examination data
            billService.updateIntraoralTreatmentPlanConsumer(message);
            logger.info("Processed Intraoral Examination Data successfully");
        };
    }
}
