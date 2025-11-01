package com.granotec.inventory_api.dispatch.details_dispatch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailsDispatchRepository extends JpaRepository<DetailsDispatch, Integer> {
}

