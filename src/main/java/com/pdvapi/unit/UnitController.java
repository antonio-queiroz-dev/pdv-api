package com.pdvapi.unit;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnitResponse create(@Valid @RequestBody UnitRequest request) {
        return unitService.create(request);
    }

    @GetMapping
    public List<UnitSummary> list() {
        return unitService.listActive();
    }

    @PutMapping("/{id}")
    public UnitResponse update(@PathVariable UUID id, @Valid @RequestBody UnitRequest request) {
        return unitService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public UnitResponse deactivate(@PathVariable UUID id) {
        return unitService.deactivate(id);
    }

    @PatchMapping("/{id}/activate")
    public UnitResponse activate(@PathVariable UUID id) {
        return unitService.activate(id);
    }
}
