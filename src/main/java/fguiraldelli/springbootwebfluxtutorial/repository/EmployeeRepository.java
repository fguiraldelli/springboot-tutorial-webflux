package fguiraldelli.springbootwebfluxtutorial.repository;

import fguiraldelli.springbootwebfluxtutorial.entity.Employee;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {

}
