package fguiraldelli.springbootwebfluxtutorial.IT;

import fguiraldelli.springbootwebfluxtutorial.dto.EmployeeDto;
import fguiraldelli.springbootwebfluxtutorial.entity.Employee;
import fguiraldelli.springbootwebfluxtutorial.mapper.EmployeeMapper;
import fguiraldelli.springbootwebfluxtutorial.repository.EmployeeRepository;
import fguiraldelli.springbootwebfluxtutorial.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIT {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private EmployeeRepository employeeRepository;
    private List<EmployeeDto> employeeDtoList;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {

        employeeRepository.deleteAll().subscribe();

        Employee employee = Employee.builder()
                .id("642d325170b4ca2ee82bf897")
                .firstName("Kirk")
                .lastName("Douglas")
                .email("kid@gmail.com")
                .build();

        Employee employee2 = Employee.builder()
                .id("643d325170b4ca2ee82bf590")
                .firstName("Frank")
                .lastName("Ghira")
                .email("fg@gmail.com")
                .build();

        employeeDtoList = Arrays.asList(
                EmployeeMapper.mapToEmployeeDto(employee),
                EmployeeMapper.mapToEmployeeDto(employee2)
        );

        employeeDto = EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Test
    public void testSaveEmployee() {

        WebTestClient.ResponseSpec response = webTestClient.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(employeeDto), EmployeeDto.class)
                .exchange();

        //then

        response.expectStatus().isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());

    }

    @Test
    public void testGetSingleEmployee() {

       EmployeeDto savedEmployeeDto = employeeService.saveEmployee(employeeDto).block();

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/employees/{id}", Collections.singletonMap("id", employeeDto.getId()))
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isEqualTo(savedEmployeeDto.getId())
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    @Test
    public void testGetAllEmployees() {

        WebTestClient.ResponseSpec response = webTestClient.get().uri("/api/employees")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .consumeWith(System.out::println);

    }

    @Test
    public void testUpdateEmployee() {

        EmployeeDto savedEmployeeDto = employeeService.saveEmployee(employeeDto).block();

        EmployeeDto modifiedEmployeeDto = EmployeeDto.builder()
                .firstName("Kirkiest")
                .lastName("Douglasu")
                .email("kido@gmail.com")
                .build();



        WebTestClient.ResponseSpec response = webTestClient.put().uri("/api/employees/{id}",
                        Collections.singletonMap("id", savedEmployeeDto.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(modifiedEmployeeDto), EmployeeDto.class)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(modifiedEmployeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(modifiedEmployeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(modifiedEmployeeDto.getEmail());


    }

    @Test
    public void testDeleteEmployee() {

        EmployeeDto savedEmployeeDto = employeeService.saveEmployee(employeeDto).block();

        WebTestClient.ResponseSpec response = webTestClient
                .delete()
                .uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployeeDto.getId()))
                .exchange();


        response.expectStatus().isNoContent()
                .expectBody()
                .consumeWith(System.out::println);

    }


}
