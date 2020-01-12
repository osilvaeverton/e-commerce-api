package com.ecommerce.api.user;

import org.springframework.hateoas.server.mvc.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.*;

import java.net.URI;
import java.net.URISyntaxException;
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
    ResponseEntity<?> newUser(@RequestBody User newUser) throws URISyntaxException {

        EntityModel<User> userResource = assembler.toModel(repository.save(newUser));

        return ResponseEntity
                .created(new URI(userResource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
                .body(userResource);
    }

    @GetMapping("/users/{id}")
    EntityModel<User> one(@PathVariable Long id){

        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return assembler.toModel(user);
    }


    @PutMapping("/users/{id}")
    ResponseEntity<?> replaceUser(@RequestBody User newUser, @PathVariable Long id) throws URISyntaxException {

        User updatedUser = repository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });

        EntityModel<User> userResource = assembler.toModel(updatedUser);

        return ResponseEntity
                .created(new URI(userResource.getRequiredLink(IanaLinkRelations.SELF).getHref()))
                .body(userResource);
    }

    @DeleteMapping("/users/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id){
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
