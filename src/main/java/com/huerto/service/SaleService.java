package com.huerto.service;

import com.huerto.dto.SaleDTO;
import com.huerto.model.Order;
import com.huerto.model.Sale;
import com.huerto.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<SaleDTO> getAllSales() {
        return saleRepository.findAll().stream()
                .map(sale -> modelMapper.map(sale, SaleDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SaleDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + id));
        return modelMapper.map(sale, SaleDTO.class);
    }

    @Transactional(readOnly = true)
    public List<SaleDTO> getSalesByUserId(Long userId) {
        return saleRepository.findByUserId(userId).stream()
                .map(sale -> modelMapper.map(sale, SaleDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SaleDTO> getSalesByDateRange(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByCreatedAtBetween(start, end).stream()
                .map(sale -> modelMapper.map(sale, SaleDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public Sale createSaleFromOrder(Order order) {
        Sale sale = new Sale();
        sale.setOrderId(order.getId());
        sale.setUserId(order.getUser().getId());
        sale.setItems(order.getItems());
        sale.setTotal(order.getTotal());
        sale.setSubtotal(order.getSubtotal());
        sale.setDiscount(order.getDiscount());
        sale.setCustomer(order.getCustomer());
        sale.setPaymentMethod(order.getPaymentMethod());
        sale.setDeliveredAt(LocalDateTime.now());
        sale.setStatus("completed");

        return saleRepository.save(sale);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSalesReport(LocalDateTime start, LocalDateTime end) {
        List<Sale> sales = saleRepository.findByCreatedAtBetween(start, end);
        
        BigDecimal totalRevenue = saleRepository.sumTotalsBetween(start, end);
        Long totalSales = saleRepository.countSalesBetween(start, end);
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalSales", totalSales != null ? totalSales : 0L);
        report.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        report.put("sales", sales.stream()
                .map(sale -> modelMapper.map(sale, SaleDTO.class))
                .collect(Collectors.toList()));
        
        return report;
    }
}

