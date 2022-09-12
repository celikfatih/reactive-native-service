package com.fati.nativeservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Slf4j
@SpringBootApplication
public class NativeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NativeServiceApplication.class, args);
    }

    @Bean
    ApplicationListener<AvailabilityChangeEvent<?>> availabilityChangeEventApplicationListener() {
        return event -> log.info("{} : {}", event.getResolvableType(), event.getState());
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(CustomerRepository customerRepository) {
        return event -> Flux.just("Alex", "Cristian", "Maria", "Kate")
                .map(name -> new Customer(null, name))
                .flatMap(customerRepository::save)
                .subscribe(customer -> log.info(customer.toString()));
    }
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class CustomerHttpController {
    private final CustomerRepository customerRepository;

    @GetMapping(path = {"/customers"})
    Flux<Customer> getAll() {
        return this.customerRepository.findAll();
    }
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class AvailabilityHttpController {
    private final ApplicationContext context;

    @GetMapping(path = {"/down"})
    void down() {
        AvailabilityChangeEvent.publish(this.context, LivenessState.BROKEN);
    }

    @GetMapping(path = {"/slow"})
    void slow() throws InterruptedException {
        Thread.sleep(10_000);
    }
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {}

record Customer (@Id Integer id, String name) {}
