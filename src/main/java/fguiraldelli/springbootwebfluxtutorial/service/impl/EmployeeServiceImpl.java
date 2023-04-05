package fguiraldelli.springbootwebfluxtutorial.service.impl;

import fguiraldelli.springbootwebfluxtutorial.dto.EmployeeDto;
import fguiraldelli.springbootwebfluxtutorial.entity.Employee;
import fguiraldelli.springbootwebfluxtutorial.mapper.EmployeeMapper;
import fguiraldelli.springbootwebfluxtutorial.repository.EmployeeRepository;
import fguiraldelli.springbootwebfluxtutorial.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;
    @Override
    public Mono<EmployeeDto> saveEmployee(EmployeeDto employeeDto) {

        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        Mono<Employee> savedEmployee = employeeRepository.save(employee);



//        return savedEmployee.map((employeeEntity) -> EmployeeMapper.mapToEmployeeDto(employeeEntity));
        return savedEmployee.map(EmployeeMapper::mapToEmployeeDto);
    }

    @Override
    public Mono<EmployeeDto> getEmployee(String employeeId) {

        Mono<Employee> savedEmployee = employeeRepository.findById(employeeId);

        return savedEmployee.map(EmployeeMapper::mapToEmployeeDto);
    }

    @Override
    public Flux<EmployeeDto> getAllEmployees() {

        Flux<Employee> employeeFlux = employeeRepository.findAll();

        return employeeFlux
                .map(EmployeeMapper::mapToEmployeeDto)
                .switchIfEmpty(Flux.empty());
    }

    @Override
    public Mono<EmployeeDto> updateEmployee(EmployeeDto employeeDto, String employeeId) {

        Mono<Employee> existedEmployee = employeeRepository.findById(employeeId);

       Mono<Employee>updatedEmployee = existedEmployee.flatMap((employee) -> {
            employee.setFirstName(employeeDto.getFirstName());
            employee.setLastName(employeeDto.getLastName());
            employee.setEmail(employeeDto.getEmail());

            return employeeRepository.save(employee);
        });

        return updatedEmployee.map(EmployeeMapper::mapToEmployeeDto);
    }

    @Override
    public Mono<Void> deleteEmployee(String employeeId) {
        return employeeRepository.deleteById(employeeId);
    }
}
