package com.ecommerce.api.user;

import org.springframework.hateoas.server.core.*;
import org.springframework.hateoas.server.mvc.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final UserRepository repository;
    private final UserResourceAssembler assembler;

    UserController(UserRepository repository, UserResourceAssembler assembler){
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/users")
    CollectionModel<EntityModel<User>> all(){

        List<EntityModel<User>> users = repository.findAll() .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(users,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).all()).withSelfRel());
    }

    @PostMapping("/users")
    User newUser(@RequestBody User user) {
        return repository.save(user);
    }

    @GetMapping("/users/{id}")
    EntityModel<User> one(@PathVariable Long id){

        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return assembler.toModel(user);
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id){
        return repository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id){
        repository.deleteById(id);
    }
}
