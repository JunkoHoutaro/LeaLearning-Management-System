package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.voucher.CreateVoucherRequest;
import com.example.Mini_Project1.request.voucher.UpdateVoucherRequest;
import com.example.Mini_Project1.response.voucher.VoucherResponse;
import com.example.Mini_Project1.service.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;

public class VoucherControllerTest {
    @Mock
    private VoucherService voucherService;

    @InjectMocks
    private VoucherController voucherController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(voucherController).build();
    }

    @Test
    void testCreateVoucherController(){

        CreateVoucherRequest createVoucherRequest = new CreateVoucherRequest(
                20,
                "Test Create Voucher",
                "GG20");

        VoucherResponse voucherResponse = new VoucherResponse();
        voucherResponse.setId("v1");
        voucherResponse.setName("Test Create Voucher");
        voucherResponse.setCode("GG20");

        when(voucherService.createVoucherService(any(CreateVoucherRequest.class)))
                .thenReturn(voucherResponse);

        ResponseEntity<VoucherResponse> response = voucherController.createVoucher(createVoucherRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Create Voucher", response.getBody().getName());
        assertEquals("GG20", response.getBody().getCode());
    }

    @Test
    void testGetAllVoucherController(){
        VoucherResponse voucher1 = new VoucherResponse();
        voucher1.setId("v1");
        voucher1.setName("voucher1");
        voucher1.setDiscountPercent(20);

        VoucherResponse voucher2  = new VoucherResponse();
        voucher2.setId("v2");
        voucher2.setName("voucher2");
        voucher2.setDiscountPercent(10);

        List<VoucherResponse> vouchers = Arrays.asList(voucher1, voucher2);

        when(voucherService.getAllVouchersService())
                .thenReturn(vouchers);

        ResponseEntity<List<VoucherResponse>> response = voucherController.getAllVouchers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetVoucherByNameController(){
        VoucherResponse voucher1 = new VoucherResponse();
        voucher1.setId("v1");
        voucher1.setName("voucher");
        voucher1.setDiscountPercent(20);

        VoucherResponse voucher2  = new VoucherResponse();
        voucher2.setId("v2");
        voucher2.setName("voucher") ;
        voucher2.setDiscountPercent(10);

        VoucherResponse voucher3  = new VoucherResponse();
        voucher3.setId("v3");
        voucher3.setName("voucher3");
        voucher3.setDiscountPercent(30);

        when(voucherService.getVoucherByNameService("voucher"))
                .thenReturn(Arrays.asList(voucher1, voucher2));

        ResponseEntity<List<VoucherResponse>> response = voucherController.getVoucherByName("voucher");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("voucher", response.getBody().get(0).getName());
        assertEquals("voucher", response.getBody().get(1).getName());
    }

    @Test
    void testGetVoucherByCodeController(){
        VoucherResponse voucher1 = new VoucherResponse();
        voucher1.setId("v1");
        voucher1.setName("voucher1");
        voucher1.setDiscountPercent(20);
        voucher1.setCode("gg20");

        VoucherResponse voucher2  = new VoucherResponse();
        voucher2.setId("v2");
        voucher2.setName("voucher2") ;
        voucher2.setDiscountPercent(10);
        voucher2.setCode("gg10");

        when(voucherService.getVoucherByCodeService(anyString()))
                .thenReturn(voucher1);

        ResponseEntity<VoucherResponse> response = voucherController.getVoucherByCode("gg20");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("gg20", response.getBody().getCode());
    }

    @Test
    void testUpdateVoucherController(){
        UpdateVoucherRequest updateVoucherRequest = new UpdateVoucherRequest();
        updateVoucherRequest.setName("Voucher1 Updated");

        VoucherResponse voucherResponse = new VoucherResponse();
        voucherResponse.setId("v1");
        voucherResponse.setName("Voucher1 Updated");

        when(voucherService.updateVoucherService(any(UpdateVoucherRequest.class)))
                .thenReturn(voucherResponse);

        ResponseEntity<VoucherResponse> response = voucherController.updateVoucher(updateVoucherRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Voucher1 Updated", response.getBody().getName());
    }

    @Test
    void testDeleteVoucherController(){
        VoucherResponse voucherResponse = new VoucherResponse();
        UUID voucherId = UUID.randomUUID();
        voucherResponse.setId(voucherId.toString());
        voucherResponse.setName("voucher1");
        voucherResponse.setDiscountPercent(20);
        voucherResponse.setCode("gg20");

        when(voucherService.deleteVoucherService(voucherId))
                .thenReturn(voucherResponse);

        ResponseEntity<VoucherResponse> response = voucherController.deleteVoucher(voucherId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

