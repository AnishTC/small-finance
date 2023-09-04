package com.tc.training.smallFinance.repository;

import com.tc.training.smallFinance.model.RoleAndPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleAndPermissionRepo extends JpaRepository<RoleAndPermission, UUID> {
    List<RoleAndPermission> findByMethodAndUri(RequestMethod method, String uri);

}
