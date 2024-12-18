package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Voucher;
import com.example.Mini_Project1.repository.VoucherRepository;
import com.example.Mini_Project1.request.voucher.CreateVoucherRequest;
import com.example.Mini_Project1.request.voucher.UpdateVoucherRequest;
import com.example.Mini_Project1.response.voucher.VoucherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VoucherServiceTest {
    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private VoucherService voucherService;

    private Voucher voucher;
    private CreateVoucherRequest createVoucherRequest;
    private UpdateVoucherRequest updateVoucherRequest;
    private VoucherResponse voucherResponse;

    @BeforeEach
    void setUp(){
        voucher = new Voucher();
        voucher.setId("f9d7c8f9-b7c2-4759-b3e7-0d1f6c72837a");
        voucher.setName("Giam gia 10%");
        voucher.setCode("GG10");

        voucherResponse = new VoucherResponse();
        voucherResponse.setId("f9d7c8f9-b7c2-4759-b3e7-0d1f6c72837a");
        voucherResponse.setName("Giam gia 10%");
        voucherResponse.setCode("GG10");

        createVoucherRequest = new CreateVoucherRequest(20, "Giam gia 10%", "GG10");

        updateVoucherRequest = new UpdateVoucherRequest();
        updateVoucherRequest.setVoucherId(UUID.fromString("f9d7c8f9-b7c2-4759-b3e7-0d1f6c72837a"));
        updateVoucherRequest.setName("Giam gia 10");
        updateVoucherRequest.setCode("GG10");

    }

    @Test
    void testCreateVoucherService_success(){
        Configuration mockConfig = mock(Configuration.class);
        when(modelMapper.getConfiguration()).thenReturn(mockConfig);
        when(mockConfig.setSkipNullEnabled(true)).thenReturn(mockConfig);

        when(voucherRepository.existsByCode(createVoucherRequest.getCode())).thenReturn(false);
        when(modelMapper.map(createVoucherRequest, Voucher.class)).thenReturn(voucher);
        when(voucherRepository.save(voucher)).thenReturn(voucher);
        when(modelMapper.map(voucher, VoucherResponse.class)).thenReturn(voucherResponse);

        VoucherResponse response = voucherService.createVoucherService(createVoucherRequest);

        assertEquals("Giam gia 10%", response.getName());
        verify(voucherRepository, times(1)).save(voucher);
    }

    @Test
    void testCreateVoucherService_existCodeAlready(){
        when(voucherRepository.existsByCode(createVoucherRequest.getCode())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            voucherService.createVoucherService(createVoucherRequest);
        });

        assertEquals("This voucher has already exist", exception.getMessage());
    }

    @Test
    void testGetAllVouchersService(){
        List<Voucher> mockVouchers = new ArrayList<>();
        mockVouchers.add(voucher);

        when(voucherRepository.findAll()).thenReturn(mockVouchers);

        List<VoucherResponse> mockResponses = new ArrayList<>();
        mockResponses.add(voucherResponse);

        when(modelMapper.map(mockVouchers, new TypeToken<List<VoucherResponse>>(){}
                .getType())).thenReturn(mockResponses);

        List<VoucherResponse> responses = voucherService.getAllVouchersService();

        assertNotNull(responses);
        assertEquals("Giam gia 10%", responses.get(0).getName());
    }

    @Test
    void testGetVoucherByNameService_nameProvided(){
        String name = "Giam gia 10%";

        List<Voucher> mockVouchers = new ArrayList<>();
        mockVouchers.add(voucher);

        when(voucherRepository.findByName(name)).thenReturn(mockVouchers);

        List<VoucherResponse> mockResponses = new ArrayList<>();
        mockResponses.add(voucherResponse);

        when(modelMapper.map(mockVouchers, new TypeToken<List<VoucherResponse>>(){}
                .getType())).thenReturn(mockResponses);

        List<VoucherResponse> responses = voucherService.getVoucherByNameService(name);

        assertNotNull(responses);
        assertEquals("Giam gia 10%", responses.get(0).getName());
    }

    @Test
    void testGetVoucherByNameService_nameNotProvided(){
        Voucher voucher1 = new Voucher();
        voucher1.setName("voucher1");
        voucher1.setDiscountPercent(20);

        List<Voucher> mockVouchers = new ArrayList<>();
        mockVouchers.add(voucher1);
        mockVouchers.add(voucher);

        when(voucherRepository.findAll()).thenReturn(mockVouchers);

        VoucherResponse mockResponse = new VoucherResponse();
        mockResponse.setName("voucher1");

        List<VoucherResponse> mockResponses = new ArrayList<>();
        mockResponses.add(mockResponse);
        mockResponses.add(voucherResponse);

        when(modelMapper.map(mockVouchers, new TypeToken<List<VoucherResponse>>(){}
                .getType())).thenReturn(mockResponses);

        List<VoucherResponse> responses = voucherService.getVoucherByNameService(null);

        assertNotNull(responses);
        assertEquals("voucher1", responses.get(0).getName());
        assertEquals("Giam gia 10%", responses.get(1).getName());
    }

    @Test
    void testGetVoucherByCodeService_codeProvided(){
        String code = "GG10";
        when(voucherRepository.findByCode(code)).thenReturn(List.of(voucher));
        when(modelMapper.map(any(Voucher.class), eq(VoucherResponse.class))).thenReturn(voucherResponse);

        VoucherResponse responses = voucherService.getVoucherByCodeService(code);

        assertNotNull(responses);
        assertEquals("GG10", responses.getCode());
        assertEquals("Giam gia 10%", responses.getName());
    }

    @Test
    void testGetVoucherByCodeService_codeNotFound(){
        when(voucherRepository.findByCode(voucher.getCode())).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(RuntimeException.class, () -> {
           voucherService.getVoucherByCodeService(voucher.getCode());
        });

        assertEquals("Voucher not found with code " + voucher.getCode(), exception.getMessage());
    }

    @Test
    void testUpdateVoucherService_success(){
        when(voucherRepository.findById(voucher.getId())).thenReturn(Optional.of(voucher));
        when(voucherRepository.existsByCode(updateVoucherRequest.getCode())).thenReturn(false);
        when(voucherRepository.save(voucher)).thenReturn(voucher);

        voucher.setName(updateVoucherRequest.getName());
        voucherResponse.setName(updateVoucherRequest.getName());

        Configuration mockConfig = mock(Configuration.class);
        when(modelMapper.getConfiguration()).thenReturn(mockConfig);
        when(mockConfig.setSkipNullEnabled(true)).thenReturn(mockConfig);

        doNothing().when(modelMapper).map(updateVoucherRequest, voucher);
        when(voucherRepository.save(voucher)).thenReturn(voucher);
        when(modelMapper.map(voucher, VoucherResponse.class)).thenReturn(voucherResponse);

        VoucherResponse response = voucherService.updateVoucherService(updateVoucherRequest);

        assertNotNull(response);
        assertEquals("Giam gia 10", response.getName());
    }

    @Test
    void testUpdateVoucherService_notFound(){
        when(voucherRepository.findById(voucher.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            voucherService.updateVoucherService(updateVoucherRequest);
        });

        assertEquals("Voucher not found with id " + voucher.getId(), exception.getMessage());
    }

    @Test
    void testDeleteVoucherService_success(){
        when(voucherRepository.findById(voucher.getId())).thenReturn(Optional.of(voucher));
        when(modelMapper.map(voucher, VoucherResponse.class)).thenReturn(voucherResponse);

        VoucherResponse response = voucherService.deleteVoucherService(UUID.fromString(voucher.getId()));

        verify(voucherRepository, times(1)).delete(voucher);
    }

    @Test
    void testDeleteVoucherService_notFound(){
        when(voucherRepository.findById(voucher.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            voucherService.deleteVoucherService(UUID.fromString(voucher.getId()));
        });
        assertEquals("Voucher not found with id " + voucher.getId(), exception.getMessage());
    }
}
