package com.huerto.controller;

import com.huerto.dto.SaleDTO;
import com.huerto.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Ventas", description = "Gestión de ventas y reportes (Solo administradores)")
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    @Operation(summary = "Obtener todas las ventas", description = "Retorna todas las ventas registradas")
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Retorna los detalles de una venta específica")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener ventas por usuario", description = "Retorna las ventas de un usuario específico")
    public ResponseEntity<List<SaleDTO>> getSalesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(saleService.getSalesByUserId(userId));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Obtener ventas por rango de fechas", description = "Filtra ventas por rango de fechas")
    public ResponseEntity<List<SaleDTO>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(saleService.getSalesByDateRange(start, end));
    }

    @GetMapping("/report")
    @Operation(summary = "Generar reporte de ventas", description = "Genera un reporte de ventas con totales y estadísticas")
    public ResponseEntity<Map<String, Object>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(saleService.getSalesReport(start, end));
    }
}

