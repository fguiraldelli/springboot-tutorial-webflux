package fguiraldelli.springbootwebfluxtutorial.controller;

import fguiraldelli.springbootwebfluxtutorial.dto.EmployeeDto;
import fguiraldelli.springbootwebfluxtutorial.entity.Employee;
import fguiraldelli.springbootwebfluxtutorial.mapper.EmployeeMapper;
import fguiraldelli.springbootwebfluxtutorial.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = EmployeeController.class)
public class EmployeeControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeDto employeeDto;

    private List<EmployeeDto> employeeDtoList;

    @BeforeEach
    void setUp() {

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
    public void givenEmployeeObject_whenSaveEmployee_thenReturnSavedEmployee() {
        //given
        given(employeeService.saveEmployee(any(EmployeeDto.class)))
                .willReturn(Mono.just(employeeDto));

        //when
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
    public void givenEmployeeId_whenGetEmployee_thenReturnEmployeeObject() {

        given(employeeService.getEmployee(employeeDto.getId()))
                .willReturn(Mono.just(employeeDto));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/employees/{id}", Collections.singletonMap("id", employeeDto.getId()))
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    @Test
    public void givenListOfEmployees_whenGetAllEmployees_returnListOfEmployees() {

        Flux<EmployeeDto> employeeDtoFlux = Flux.fromIterable(employeeDtoList);
        given(employeeService.getAllEmployees())
                .willReturn(employeeDtoFlux);

        WebTestClient.ResponseSpec response = webTestClient.get().uri("/api/employees")
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .consumeWith(System.out::println);

    }

    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnUpdatedEmployeeObject() {

        EmployeeDto modifiedEmployeeDto = EmployeeDto.builder()
                .firstName("Kirkiest")
                .lastName("Douglasu")
                .email("kido@gmail.com")
                .build();

        given(employeeService.updateEmployee(any(EmployeeDto.class), any(String.class)))
                .willReturn(Mono.just(modifiedEmployeeDto));

        WebTestClient.ResponseSpec response = webTestClient.put().uri("/api/employees/{id}",
                Collections.singletonMap("id", employeeDto.getId()))
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
    public void givenEmployeeId_whenDeleteEmployee_thenReturnNothing() {

        Mono<Void> voidMono = Mono.empty();

        given(employeeService.deleteEmployee(employeeDto.getId()))
                .willReturn(voidMono);

        WebTestClient.ResponseSpec response = webTestClient
                .delete()
                .uri("/api/employees/{id}", Collections.singletonMap("id", employeeDto.getId()))
                .exchange();


        response.expectStatus().isNoContent()
                .expectBody()
                .consumeWith(System.out::println);

    }

}
