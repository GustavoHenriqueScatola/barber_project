package com.cesul.barber.controllers;

import com.cesul.barber.dtos.UpdateUserAvatarDTO;
import com.cesul.barber.dtos.UserCreationDTO;
import com.cesul.barber.services.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Gerenciamento de usuários da barbearia")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    @Operation(summary = "Listar usuários")
    public ResponseEntity<List<UserCreationDTO>> getAll() {
        return ResponseEntity.ok(usersService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuário por ID")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    public ResponseEntity<UserCreationDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.of(usersService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar usuário")
    @ApiResponse(responseCode = "201", description = "Usuário cadastrado", useReturnTypeSchema = true)
    public ResponseEntity<UserCreationDTO> create(@RequestBody UserCreationDTO user) {
        UserCreationDTO created = usersService.create(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Substituir dados do usuário", description = "Substitui nome, e-mail e avatar.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    public ResponseEntity<UserCreationDTO> update(@PathVariable UUID id, @RequestBody UserCreationDTO user) {
        return ResponseEntity.of(usersService.update(id, user));
    }

    @PatchMapping("/{id}/avatar")
    @Operation(summary = "Atualizar avatar", description = "Altera somente o avatar. Envie null para removê-lo.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    public ResponseEntity<UserCreationDTO> patchUser(@PathVariable UUID id, @RequestBody UpdateUserAvatarDTO avatar) {
        if (!avatar.hasAvatarUrl()) {
            return ResponseEntity.of(usersService.findById(id));
        }
        return ResponseEntity.of(usersService.updateAvatar(id, avatar.getAvatarUrl()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário")
    @ApiResponse(responseCode = "204", description = "Usuário excluído")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (usersService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Excluir todos os usuários")
    @ApiResponse(responseCode = "204", description = "Usuários excluídos")
    public ResponseEntity<Void> clearUsers() {
        usersService.clear();
        return ResponseEntity.noContent().build();
    }
}
