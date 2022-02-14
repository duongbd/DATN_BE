package vn.nuce.datn_be.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nuce.datn_be.enity.App;

import java.util.List;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {
    List<App> findAllByAppNameIn(List<String> appNames);
}
