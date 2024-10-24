package com.example.controller;

import com.example.dto.InvestmentResponse;
import com.example.dto.InvestmentSummaryResponse;
import com.example.service.InvestmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class InvestmentControllerTest {

    @Mock
    private InvestmentService investmentService;

    @InjectMocks
    private InvestmentController investmentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Tests for getInvestmentsByUserId

    @Test
    public void getInvestmentsByUserId_UserDoesNotExist_ReturnsNotFound() {
        Long userId = 2L;

        ResponseEntity<List<InvestmentResponse>> response = investmentController.getInvestmentsByUserId(userId);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(investmentService, times(0)).getUserInvestments(userId);
    }

    @Test
    public void getInvestmentsByUserId_UserExistsNoInvestments_ReturnsNoContent() {
        Long userId = 1L;

        when(investmentService.getUserInvestments(userId)).thenReturn(Collections.emptyList());
        ResponseEntity<List<InvestmentResponse>> response = investmentController.getInvestmentsByUserId(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(investmentService, times(1)).getUserInvestments(userId);
    }

    @Test
    public void getInvestmentsByUserId_UserExistsWithInvestments_ReturnsInvestments() {
        Long userId = 1L;
        List<InvestmentResponse> investments = Arrays.asList(new InvestmentResponse(), new InvestmentResponse());

        when(investmentService.getUserInvestments(userId)).thenReturn(investments);
        ResponseEntity<List<InvestmentResponse>> response = investmentController.getInvestmentsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(investments, response.getBody());
        verify(investmentService, times(1)).getUserInvestments(userId);
    }

    // Tests for getUserInvestmentSummary

    @Test
    public void getUserInvestmentSummary_UserDoesNotExist_ReturnsNotFound() {
        Long userId = 2L;

        ResponseEntity<InvestmentSummaryResponse> response = investmentController.getUserInvestmentSummary(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(investmentService, times(0)).getUserInvestmentSummary(userId);
    }

    @Test
    public void getUserInvestmentSummary_UserExistsNoSummary_ReturnsNoContent() {
        Long userId = 1L;

        when(investmentService.getUserInvestmentSummary(userId)).thenReturn(null);
        ResponseEntity<InvestmentSummaryResponse> response = investmentController.getUserInvestmentSummary(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(investmentService, times(1)).getUserInvestmentSummary(userId);
    }

    @Test
    public void getUserInvestmentSummary_UserExistsWithSummary_ReturnsSummary() {
        Long userId = 1L;
        InvestmentSummaryResponse summary = new InvestmentSummaryResponse();

        when(investmentService.getUserInvestmentSummary(userId)).thenReturn(summary);
        ResponseEntity<InvestmentSummaryResponse> response = investmentController.getUserInvestmentSummary(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(summary, response.getBody());
        verify(investmentService, times(1)).getUserInvestmentSummary(userId);
    }
}
