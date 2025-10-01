package com.km.docmacc.intraoralbill.repository;

import com.km.docmacc.intraoralbill.model.entity.IntraoralTreatmentPlanConsumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntraoralTreatmentPlanConsumerRepository extends JpaRepository<IntraoralTreatmentPlanConsumer, Long> {
}
